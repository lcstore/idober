package com.lezo.idober.timer;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class OnlineTorrentMovieTimer implements Runnable {
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static final Set<String> REMOVE_FIELDS = Sets.newHashSet();
	private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
	private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");
	private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");
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
		int sumMoveCount = 0;
		int upcomingCount = 0;
		try {
			SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
			SolrServer destServer = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
			int offset = 0;
			int limit = 100;
			int foundCount = 1;
			while (total < foundCount) {
				SolrDocumentList foundDocList = getSourceMovieWithTorrents(sourceServer, offset, limit);
				if (foundCount <= 1) {
					foundCount = (int) foundDocList.getNumFound();
				}
				int moveCount = moveTorrents(foundDocList, sourceServer,
						destServer);
				sumMoveCount += moveCount;
				total += foundDocList.size();
				if (foundDocList.size() < limit) {
					break;
				}
			}
			foundCount = 1;
			while (upcomingCount < foundCount) {
				SolrDocumentList selectDocs = getUpcomingSourceMovie(sourceServer, offset, limit);
				if (foundCount <= 1) {
					foundCount = (int) selectDocs.getNumFound();
				}
				upcomingCount += moveTorrents(selectDocs, sourceServer, destServer);
				total += selectDocs.size();
				if (selectDocs.size() < limit) {
					break;
				}
			}

		} catch (Exception e) {
			exception = e;
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			String msg = "total:" + total + ",moveCount:" + sumMoveCount + ",upcoming:" + upcomingCount + ",cost:"
					+ costMills;
			if (exception != null) {
				log.warn(msg, exception);
			} else {
				log.info(msg);
			}
			running.set(false);
		}

	}

	@SuppressWarnings("unchecked")
	private int moveTorrents(SolrDocumentList selectDocs, SolrServer sourceServer, SolrServer destServer) {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return 0;
		}
		List<String> markList = Lists.newArrayList();
		for (SolrDocument doc : selectDocs) {
			String id = doc.getFieldValue("id").toString();
			Object tObject = doc.getFieldValue("torrents");
			if (tObject == null) {
				continue;
			}
			// 最新电影，无下载地址，也要移到online-movie
			List<String> torrents = (List<String>) tObject;
			if (CollectionUtils.isNotEmpty(torrents)) {
				markList.add(id);
			}
		}
		int addCount = 0;
		try {
			Map<String, Object> fieldModifier = Maps.newHashMap();
			fieldModifier.put("add", new Date());
			for (SolrDocument doc : selectDocs) {
				createIfHasShares(doc);
				unifyRegion(doc);
				addOldCode(doc);
				for (String field : REMOVE_FIELDS) {
					doc.remove(field);
				}
				doc.addField("creation", fieldModifier);
				SolrInputDocument inDoc = ClientUtils.toSolrInputDocument(doc);
				addCount++;
				destServer.add(inDoc);
			}
			if (addCount > 0) {
				destServer.commit();
			}
			hadMoveSourceDocuments(sourceServer, markList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addCount;

	}

	private void addOldCode(SolrDocument doc) {
		try {
			if (doc.getFieldValue("old_id_s") != null) {
				return;
			}
			SolrServer oldServer = SolrUtils.getSolrServer(SolrUtils.CORE_OLD_MOVIE);
			SolrQuery solrQuery = createMetaQuery(doc, false);
			QueryResponse sResp = oldServer.query(solrQuery);
			SolrDocumentList hadDocs = sResp.getResults();
			addOldCode(doc, hadDocs);
		} catch (Exception e) {
			log.warn("addOldCode:" + doc.getFieldValue("id") + ",name:" + doc.getFieldValue("name") + ",cause:");
		}
	}

	private void addOldCode(SolrDocument doc, List<SolrDocument> metaDocs) {
		if (CollectionUtils.isEmpty(metaDocs)) {
			return;
		}
		for (SolrDocument mDoc : metaDocs) {
			// 1.不匹配数据过滤
			if (!isSameDoc(doc, mDoc)) {
				continue;
			}
			Map<String, Object> fieldMap = Maps.newHashMap();
			fieldMap.put("set", mDoc.getFieldValue("id"));
			doc.addField("old_id_s", fieldMap);
			break;

		}
	}

	private boolean isSameDoc(SolrDocument doc, SolrDocument mDoc) {
		Boolean bSame = isSameImdb(doc, mDoc);
		if (bSame != null) {
			return bSame;
		}
		bSame = hasSameDirector(doc, mDoc);
		if (bSame != null) {
			return bSame;
		}
		bSame = hasSameActor(doc, mDoc);
		bSame = bSame == null ? false : bSame;
		return bSame;
	}

	private Boolean hasSameActor(SolrDocument doc, SolrDocument mDoc) {
		Object mContentObj = mDoc.getFieldValue("content");
		String sContent = mContentObj.toString();
		JSONObject ctObject = JSONObject.parseObject(sContent);
		String actors = ctObject.getString("actors");
		Boolean bSame = null;
		if (StringUtils.isBlank(actors)) {
			return bSame;
		}
		Collection<Object> actorList = mDoc.getFieldValues("actors");
		if (CollectionUtils.isEmpty(actorList)) {
			return bSame;
		}
		bSame = false;
		for (Object aObj : actorList) {
			if (actors.contains(aObj.toString())) {
				bSame = true;
				break;
			}
		}
		return bSame;
	}

	private Boolean isSameImdb(SolrDocument doc, SolrDocument mDoc) {
		String srcContent = doc.getFieldValue("content").toString();
		JSONObject srcObject = JSONObject.parseObject(srcContent);
		String sImdb = srcObject.getString("imdb");
		if (StringUtils.isBlank(sImdb)) {
			return null;
		}
		Object imdbObj = mDoc.getFieldValue("imdb_txt");
		if (imdbObj == null) {
			return null;
		}
		Boolean bSame = null;
		if (imdbObj != null && StringUtils.isNotBlank(sImdb)) {
			String sImdbTxt = imdbObj.toString();
			if (StringUtils.isNotBlank(sImdbTxt) &&
					!sImdbTxt.equals("imdb")) {
				bSame = sImdb.contains(sImdbTxt);
			}
		}
		return bSame;
	}

	@SuppressWarnings("unchecked")
	private Boolean hasSameDirector(SolrDocument doc, SolrDocument mDoc) {
		Object srcObject = mDoc.getFieldValue("directors");
		if (srcObject == null) {
			return null;
		}
		String srcDirector = srcObject.toString();
		if (srcDirector.equals("-")) {
			return null;
		}
		Object referObject = doc.getFieldValue("directors");
		if (referObject == null) {
			return null;
		}
		boolean hasSame = false;
		List<String> directors = (List<String>) referObject;
		for (String director : directors) {
			if (srcDirector.contains(director)) {
				hasSame = true;
				break;
			}
		}
		return hasSame;
	}

	private SolrQuery createMetaQuery(SolrDocument doc, boolean useDirector) {
		int offset = 0;
		int limit = 100;

		List<String> missFields = Lists.newArrayList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		StringBuilder sb = new StringBuilder();
		String sName = doc.getFieldValue("name").toString();
		sName = SYMBOL_REG.matcher(sName).replaceAll(" ");
		sName = IGNORE_REG.matcher(sName).replaceAll(" ");
		Matcher matcher = CN_NAME_REG.matcher(sName);
		if (matcher.find()) {
			sName = matcher.group(1).trim();
		}
		sName = sName.trim();
		sb.append(sName);
		String sQuery = ClientUtils.escapeQueryChars(sb.toString());
		solrQuery.set("q", sQuery);
		Object yearObj = doc.getFieldValue("year");
		if (yearObj != null) {
			solrQuery.addFilterQuery("year:" + yearObj);
		} else {
			missFields.add("year");
		}
		if (!missFields.isEmpty()) {
			String idString = doc.getFieldValue("id").toString();
			log.warn("cmovie(" + idString + "),checks:" + ArrayUtils.toString(missFields));
		}
		return solrQuery;
	}

	@SuppressWarnings("unchecked")
	private void unifyRegion(SolrDocument doc) throws Exception {
		String fieldName = "regions";
		Object rObject = doc.getFieldValue(fieldName);
		if (rObject == null) {
			return;
		}
		Collection<String> regionList = (Collection<String>) rObject;
		Set<String> regionSet = convertRegions(regionList);
		SolrServer metaServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		SolrDocumentList synonymDocs = querySynonymDocs(regionSet, metaServer);
		Set<String> uRegions = Sets.newLinkedHashSet();
		if (synonymDocs != null) {
			for (SolrDocument sDoc : synonymDocs) {
				String sRegion = sDoc.getFieldValue("title").toString();
				String sGroup = queryRegionGroup(sRegion, metaServer);
				if (StringUtils.isNotBlank(sGroup)) {
					uRegions.add(sGroup.trim());
				}
				uRegions.add(sRegion);
			}
		}
		doc.put(fieldName, uRegions);

	}

	private String queryRegionGroup(String sRegion, SolrServer metaServer) {
		sRegion = ClientUtils.escapeQueryChars(sRegion);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.addField("title");
		solrQuery.set("q", "(type:idober-group-region AND group_ss:" + sRegion + ")");
		try {
			QueryResponse resp = metaServer.query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			if (CollectionUtils.isNotEmpty(docList)) {
				return docList.get(0).getFieldValue("title").toString();
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
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
			region = region.replaceAll("[()（）]", "");
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
			if (StringUtils.isBlank(type) && "bttiantang-torrent".equals(source)) {
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

	/**
	 * 即将上映
	 * 
	 * @param sourceServer
	 * @param offset
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	private SolrDocumentList getUpcomingSourceMovie(SolrServer sourceServer, int offset, int limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		// 从7天前开始，即将上映的电影
		solrQuery.set("q", "(!had_move_s:* AND release:[NOW-7DAY/DAY TO *])");
		solrQuery.addSort("release", ORDER.desc);
		QueryResponse resp = sourceServer.query(solrQuery);
		return resp.getResults();
	}

	private SolrDocumentList getSourceMovieWithTorrents(SolrServer sourceServer, int offset, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(!had_move_s:* AND torrents_size:[1 TO *])");
		QueryResponse resp = sourceServer.query(solrQuery);
		return resp.getResults();
	}

	private void hadMoveSourceDocuments(SolrServer sourceServer, List<String> idList) throws Exception {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}
		Map<String, Object> fieldModifier = Maps.newHashMap();
		fieldModifier.put("set", "1");
		for (String id : idList) {
			SolrInputDocument inDoc = new SolrInputDocument();
			inDoc.addField("id", id);
			inDoc.addField("had_move_s", fieldModifier);
			sourceServer.add(inDoc);
		}
		sourceServer.commit();
	}
}
