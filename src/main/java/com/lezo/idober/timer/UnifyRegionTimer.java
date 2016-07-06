package com.lezo.idober.timer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.idober.utils.SolrUtils;

/**
 * 地区归一化
 * 
 * @author lezo
 * @since 2016年7月4日
 */
@Log4j
public class UnifyRegionTimer implements Runnable {
	private static AtomicBoolean running = new AtomicBoolean(false);

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			log.warn(this.getClass().getSimpleName() + " is runing...");
			return;
		}
		long startMills = System.currentTimeMillis();
		int total = 0;
		Exception exception = null;
		try {
			SolrServer movieServer = SolrUtils.getSolrWithMovie();
			SolrServer metaServer = SolrUtils.getSolrWithMeta();
			int offset = 0;
			int limit = 100;
			while (true) {
				SolrDocumentList selectDocs = null;
				try {
					selectDocs = getMovieWithLimit(movieServer, offset, limit);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs == null) {
					break;
				}
				try {
					unifyRegions(metaServer, movieServer, selectDocs);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs.size() < limit) {
					break;
				}
				offset += limit;
				total += limit;
			}
		} catch (Exception e) {
			exception = e;
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			String msg = "UnifyRegion,total:" + total + ",cost:" + costMills;
			if (exception != null) {
				log.warn(msg, exception);
			} else {
				log.info(msg);
			}
			running.set(false);
		}

	}

	@SuppressWarnings("unchecked")
	private void unifyRegions(SolrServer metaServer, SolrServer movieServer, SolrDocumentList selectDocs)
			throws Exception {
		if (selectDocs == null) {
			return;
		}
		JSONArray tArray = new JSONArray();
		for (SolrDocument doc : selectDocs) {
			String id = doc.getFieldValue("id").toString();
			List<String> regionList = (List<String>) doc.getFieldValue("regions");
			JSONObject dObject = new JSONObject();
			dObject.put("id", id);
			if (CollectionUtils.isEmpty(regionList)) {
				JSONObject regionObject = new JSONObject();
				regionObject.put("set", Lists.newArrayList());
				dObject.put("_ex_region_txts", regionObject);
				tArray.add(dObject);
			} else {
				try {
					Set<String> regionSet = convertRegions(regionList);
					SolrDocumentList synonymDocs = querySynonymDocs(regionSet, metaServer);
					List<String> uRegions = Lists.newArrayList();
					if (synonymDocs != null) {
						for (SolrDocument sDoc : synonymDocs) {
							String sRegion = sDoc.getFieldValue("title").toString();
							uRegions.add(sRegion);
						}
					} else {
						System.err.println("emptyRegions:" + ArrayUtils.toString(regionList));
					}
					JSONObject regionObject = new JSONObject();
					regionObject.put("set", uRegions);
					dObject.put("_ex_region_txts", regionObject);
					tArray.add(dObject);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		if (tArray.isEmpty()) {
			return;
		}
		String sContent = tArray.toJSONString();
		String contentType = "application/json";
		Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
		ContentStreamUpdateRequest request =
				new ContentStreamUpdateRequest("/update/json");
		for (ContentStream cs : strems) {
			request.addContentStream(cs);
		}
		request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		NamedList<Object> resp = movieServer.request(request);
		log.info("resp:" + resp.size() + ",update:" + tArray.size());
	}

	private SolrDocumentList querySynonymDocs(Set<String> regionSet, SolrServer metaServer) throws Exception {
		if (CollectionUtils.isEmpty(regionSet)) {
			return null;
		}
		String sQuery = "(type:idober-synonym-country AND (";
		StringBuilder sb = new StringBuilder();
		sb.append(sQuery);
		for (String region : regionSet) {
			if (sb.length() > sQuery.length()) {
				sb.append(" OR ");
			}
			sb.append("synonym_ss:");
			sb.append(region);
		}
		sb.append("))");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.addField("title");
		solrQuery.set("q", sb.toString());
		QueryResponse resp = metaServer.query(solrQuery);
		return resp.getResults();
	}

	private Set<String> convertRegions(Collection<String> regions) {
		Pattern cnReg = Pattern.compile("([\u4E00-\u9FA5]+)");
		Set<String> regionSet = Sets.newHashSet();
		for (String region : regions) {
			region = region.replaceAll("\\.", "");
			Matcher matcher = cnReg.matcher(region);
			int index = 0;
			if (matcher.find()) {
				String cnName = matcher.group(1);
				index = matcher.end(1);
				cnName = cnName.trim();
				if (cnName.contains("西德")) {
					cnName = "德国";
				}
				cnName = ChineseHelper.convertToSimplifiedChinese(cnName);
				regionSet.add(cnName);

			}
			if (index < region.length()) {
				region = region.substring(index).toLowerCase();
				String[] txtArr = region.split("[/|]");
				for (String txt : txtArr) {
					if (StringUtils.isBlank(txt)) {
						continue;
					}
					txt = txt.trim();
					txt = ChineseHelper.convertToSimplifiedChinese(txt);
					regionSet.add(txt);
				}
			}
		}
		return regionSet;

	}

	private SolrDocumentList getMovieWithLimit(SolrServer movieServer, int offset, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.addField("id,regions");
		solrQuery.set("q", "-_ex_region_txts:*");
		QueryResponse resp = movieServer.query(solrQuery);
		return resp.getResults();
	}
}
