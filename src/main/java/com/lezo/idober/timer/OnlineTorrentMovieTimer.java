package com.lezo.idober.timer;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class OnlineTorrentMovieTimer implements Runnable {
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static final Set<String> REMOVE_FIELDS = Sets.newHashSet();
	static {
		REMOVE_FIELDS.add("_version_");
		REMOVE_FIELDS.add("creation");
		REMOVE_FIELDS.add("timestamp");
		REMOVE_FIELDS.add("tcount");
		REMOVE_FIELDS.add("torrents_size");
		REMOVE_FIELDS.add("scount");
		REMOVE_FIELDS.add("editor");
	}

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			log.warn(this.getClass().getSimpleName() + " is runing...");
			return;
		}
		long startMills = System.currentTimeMillis();
		Exception exception = null;
		int total = 0;
		try {
			SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
			SolrServer destServer = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
			int offset = 0;
			int limit = 100;
			while (true) {
				SolrDocumentList selectDocs = null;
				try {
					selectDocs = getSourceMovieWithTorrents(sourceServer, offset, limit);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs == null) {
					break;
				}
				try {
					movieTorrents(selectDocs, sourceServer, destServer);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs.size() < limit) {
					break;
				}
				offset += limit - selectDocs.size();
				total += limit;
			}
		} catch (Exception e) {
			exception = e;
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			String msg = this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total;
			if (exception != null) {
				log.warn(msg, exception);
			} else {
				log.info(msg);
			}
			running.set(false);
		}

	}

	private void movieTorrents(SolrDocumentList selectDocs, SolrServer sourceServer, SolrServer destServer) {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return;
		}
		List<String> idList = Lists.newArrayList();
		for (SolrDocument doc : selectDocs) {
			String id = doc.getFieldValue("id").toString();
			idList.add(id);
		}
		try {
			SolrDocumentList hasDocs = hasDocuments(destServer, idList);
			Set<String> hasIdSet = Sets.newHashSet();
			if (hasDocs != null) {
				for (SolrDocument hDoc : hasDocs) {
					String hasId = hDoc.getFieldValue("id").toString();
					hasIdSet.add(hasId);
				}
			}
			int addCount = 0;
			Map<String, Object> fieldModifier = Maps.newHashMap();
			fieldModifier.put("add", new Date());
			for (SolrDocument doc : selectDocs) {
				String id = doc.getFieldValue("id").toString();
				if (hasIdSet.contains(id)) {
					continue;
				}
				for (String field : REMOVE_FIELDS) {
					doc.remove(field);
				}
				createIfHasShares(doc);
				doc.addField("creation", fieldModifier);
				SolrInputDocument inDoc = ClientUtils.toSolrInputDocument(doc);
				addCount++;
				destServer.add(inDoc);
			}
			if (addCount > 0) {
				destServer.commit();
			}
			removeDocuments(sourceServer, idList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private NamedList<Object> doCommit(String sContent, SolrServer server) throws Exception {
		String contentType = "application/json";
		Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
		ContentStreamUpdateRequest request =
				new ContentStreamUpdateRequest("/update/json");
		for (ContentStream cs : strems) {
			request.addContentStream(cs);
		}
		request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		return server.request(request);
	}

	private void createIfHasShares(SolrDocument doc) {
		String torrentField = "torrents";
		String shareField = "shares";
		Collection<Object> torrents = doc.getFieldValues(torrentField);
		if (CollectionUtils.isEmpty(torrents)) {
			return;
		}
		List<String> shareList = Lists.newArrayList();
		List<String> torrentList = Lists.newArrayList();
		for (Object torrent : torrents) {
			JSONObject tObject = JSONObject.parseObject(torrent.toString());
			String type = tObject.getString("type");
			String source = tObject.getString("source");
			if (StringUtils.isBlank(source)) {
				String sUrl = tObject.getString("url");
				if (sUrl != null && sUrl.contains(".bttiantang.com")) {
					source = "bttiantang-torrent";
				}
			}
			if (StringUtils.isBlank(type) && source.equals("bttiantang-torrent")) {
				tObject.remove("source");
				tObject.put("type", source);
				JSONObject pObject = tObject.getJSONObject("param");
				StringBuilder sb = new StringBuilder();
				for (String key : pObject.keySet()) {
					if (sb.length() > 0) {
						sb.append("&");
					}
					sb.append(key);
					sb.append("=");
					sb.append(pObject.getString(key));
				}
				tObject.put("param", sb.toString());
				tObject.put("url", "");
				torrentList.add(tObject.toJSONString());
			} else if (type != null && type.endsWith("-share")) {
				shareList.add(tObject.toJSONString());
			} else if (type != null) {
				torrentList.add(tObject.toJSONString());
			}
		}
		doc.put(torrentField, torrentList);

		if (shareList.isEmpty()) {
			doc.remove(shareField);
		} else {
			doc.put(shareField, shareList);
		}
	}

	private SolrDocumentList getSourceMovieWithTorrents(SolrServer sourceServer, int offset, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "tcount:[1 TO *]");
		QueryResponse resp = sourceServer.query(solrQuery);
		return resp.getResults();
	}

	private void removeDocuments(SolrServer sourceServer, List<String> idList) throws Exception {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}
		sourceServer.deleteById(idList);
	}

	private SolrDocumentList hasDocuments(SolrServer destServer, List<String> idList) throws Exception {
		if (CollectionUtils.isEmpty(idList)) {
			return null;
		}
		String sHead = "(";
		StringBuilder sb = new StringBuilder(sHead);
		for (String id : idList) {
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sb.append("id:");
			sb.append(id);
		}
		sb.append(")");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(idList.size());
		solrQuery.set("q", sb.toString());
		solrQuery.addField("id");
		QueryResponse resp = destServer.query(solrQuery);
		return resp.getResults();
	}
}
