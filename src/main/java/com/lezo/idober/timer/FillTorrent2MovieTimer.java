package com.lezo.idober.timer;

import java.util.ArrayList;
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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class FillTorrent2MovieTimer implements Runnable {
	private static final String CORE_NAME_MOVIE = "cmovie";
	private static final String CORE_NAME_META = "cmeta";
	private static String torrentFilterQuery;
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
	private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");
	private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");

	static {
		ArrayList<String> typeList = Lists.newArrayList();
		// typeList.add("bttiantang-movie-torrent");
		typeList.add("xiamp4-movie-torrent");
		typeList.add("dy2018-movie-torrent");
		typeList.add("rarbt-movie-torrent");
		typeList.add("bluraydisc-movie-torrent");
		StringBuilder sb = new StringBuilder();
		sb.append("type:(");
		for (int i = 0, len = typeList.size(); i < len; i++) {
			String type = typeList.get(i);
			if (i > 0) {
				sb.append(" OR ");
			}
			sb.append(type);
		}
		sb.append(")");
		torrentFilterQuery = sb.toString();
	}

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			log.warn(this.getClass().getSimpleName() + " is runing...");
			return;
		}
		long startMills = System.currentTimeMillis();
		int total = 0;
		try {
			log.info(this.getClass().getSimpleName() + " is start...");
			String coreName = CORE_NAME_MOVIE;
			SolrServer movieServer = SolrUtils.getSolrServer(coreName);
			coreName = CORE_NAME_META;
			SolrServer metaServer = SolrUtils.getSolrServer(coreName);
			int limit = 100;
			String fromId = "0";
			while (true) {
				SolrDocumentList selectDocs = null;
				try {
					// selectDocs =
					// getEmptyTorrentMovieByIdWithLimit(movieServer, fromId,
					// limit);
					selectDocs = getMovieByIdWithLimit(movieServer, fromId,
							limit);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs == null) {
					break;
				}
				try {
					fillTorrents(metaServer, movieServer, selectDocs);
				} catch (Exception e) {
					log.warn("", e);
				}
				if (selectDocs.size() < limit) {
					break;
				} else {
					for (SolrDocument doc : selectDocs) {
						String curId = doc.getFieldValue("id").toString();
						if (fromId.compareTo(curId) < 0) {
							fromId = curId;
						}
					}
				}
				total += selectDocs.size();
			}
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			log.warn(this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total);
			running.set(false);
		}

	}

	private void fillTorrents(SolrServer metaServer, SolrServer movieServer, SolrDocumentList selectDocs)
			throws Exception {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return;
		}
		JSONArray taskList = new JSONArray();
		JSONArray docArray = new JSONArray();
		for (SolrDocument doc : selectDocs) {
			SolrQuery solrQuery = createMetaQuery(doc, false);
			List<SolrDocument> metaDocs = getMetaDocuments(metaServer, solrQuery);
			JSONObject tObject = createTorrentDoc(doc, metaDocs);
			if (tObject == null || tObject.getJSONArray("torrents").isEmpty()) {
				String sId = doc.getFieldValue("id").toString();
				String sName = doc.getFieldValue("name").toString();
				JSONObject taskObj = new JSONObject();
				taskObj.put("type", "query-movie");
				taskObj.put("url", "");
				taskObj.put("level", 1000);
				JSONObject argsObj = new JSONObject();
				argsObj.put("title", sName);
				argsObj.put("mid", sId);
				argsObj.put("retry", "0");
				taskObj.put("args", argsObj);
				taskList.add(taskObj);
				log.warn("torrent.empty[" + doc.getFirstValue("id") + "],name:" + doc.getFirstValue("name"));
			}
			if (tObject != null) {
				docArray.add(tObject);
			}
		}
		if (CollectionUtils.isEmpty(docArray)) {
			return;
		}
		String sContent = docArray.toJSONString();
		String contentType = "application/json";
		Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
		ContentStreamUpdateRequest request =
				new ContentStreamUpdateRequest("/update/json");
		for (ContentStream cs : strems) {
			request.addContentStream(cs);
		}
		request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		NamedList<Object> resp = movieServer.request(request);
		log.info("resp:" + resp.size() + ",update:" + docArray.size());
	}

	private JSONObject createTorrentDoc(SolrDocument doc, List<SolrDocument> metaDocs) {
		JSONObject docObject = new JSONObject();
		String idString = doc.getFieldValue("id").toString();
		docObject.put("id", idString);
		Map<String, Object> fieldMap = Maps.newHashMap();
		fieldMap.put("set", doc.getFieldValue("code_s"));
		docObject.put("code_s", fieldMap);
		docObject.put("had_move_s", 0);
		JSONArray tArray = new JSONArray();
		if (CollectionUtils.isNotEmpty(metaDocs)) {
			for (SolrDocument mDoc : metaDocs) {
				// 1.不匹配数据过滤
				if (!isSameDoc(doc, mDoc)) {
					continue;
				}
				Object mContentObj = mDoc.getFieldValue("content");
				String sContent = mContentObj.toString();
				JSONObject ctObject = JSONObject.parseObject(sContent);
				JSONArray srcArray = ctObject.getJSONArray("torrents");
				if (srcArray != null) {
					// TODO: 2.匹配数据合并
					for (int index = 0, len = srcArray.size(); index < len; index++) {
						Object srcObj = srcArray.get(index);
						String srcTor = srcObj.toString();
						JSONObject torObj = JSONObject.parseObject(srcTor);
						torObj.remove("data");
						String type = torObj.getString("type");
						String url = torObj.getString("url");
						type = type == null ? "" : type;
						url = url == null ? "" : url;
						if (!type.contains("share") || url.contains(".baidu.com")) {
							tArray.add(torObj.toJSONString());
						}
					}
				}
			}
		}
		if (tArray.isEmpty()) {
			// return null;
		}
		docObject.put("torrents", tArray);
		return docObject;
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
		if (bSame == null) {
			bSame = hasSameRelease(doc, mDoc, 1);
		} else if (bSame) {
			bSame = hasSameRelease(doc, mDoc, 12);
		}
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
		Collection<Object> actorList = doc.getFieldValues("actors");
		if (CollectionUtils.isEmpty(actorList)) {
			return bSame;
		}
		bSame = isContains(actors, actorList);
		if (!bSame && !isSameLang(actors, JSON.toJSONString(actorList))) {
			bSame = null;
		}
		return bSame;
	}

	private boolean isContains(String actors, Collection<Object> actorList) {
		boolean bSame = false;
		int sameCount = 0;
		actors = actors.toLowerCase();
		for (Object aObj : actorList) {
			String sActor = aObj.toString().toLowerCase().trim();
			// 李璟荣, 全慧珍 VS 李景荣;全惠珍
			int hitCharCount = 0;
			for (int index = 0, len = sActor.length(); index < len; index++) {
				if (actors.indexOf(sActor.charAt(index)) >= 0) {
					hitCharCount++;
				}
			}
			float hitPer = hitCharCount * 1F / sActor.length();
			if (hitPer > 0.65F) {
				sameCount++;
			}
		}
		int aCount = actors.split(";").length;
		int minCount = (aCount > 1 && actorList.size() > 0) ? Math.min(aCount, actorList.size()) : actorList.size();
		Float countPer = sameCount * 1F / minCount;
		if (countPer >= 0.7F) {
			bSame = true;
		}
		return bSame;
	}

	private Boolean isSameImdb(SolrDocument doc, SolrDocument mDoc) {
		Object imdbSrcObj = doc.getFieldValue("imdb_s");
		String sImdb = null;
		if (imdbSrcObj == null) {
			String srcContent = doc.getFieldValue("content").toString();
			JSONObject srcObject = JSONObject.parseObject(srcContent);
			sImdb = srcObject.getString("imdb");
		} else {
			sImdb = imdbSrcObj.toString();
		}
		if (StringUtils.isBlank(sImdb)) {
			return null;
		}
		Object imdbObj = mDoc.getFieldValue("imdb_txt");
		if (imdbObj == null) {
			return null;
		}
		Boolean bSame = null;
		String sImdbTxt = imdbObj.toString();
		sImdbTxt = sImdbTxt.replaceAll("[^0-9]+", "").trim();
		if (StringUtils.isNotBlank(sImdbTxt)) {
			bSame = sImdb.contains(sImdbTxt);
		}
		return bSame;
	}

	private boolean hasSameRelease(SolrDocument doc, SolrDocument mDoc, int monthRange) {
		Object referObject = doc.getFieldValue("release");
		Object srcObject = mDoc.getFieldValue("release_tdt");
		srcObject = srcObject == null ? mDoc.getFieldValue("year_ti") : srcObject;
		if (referObject == null || srcObject == null) {
			return true;
		}
		Date referDate = (Date) referObject;
		Date srcDate = null;
		if (srcObject instanceof Integer) {
			srcDate = DateUtils.setYears(new Date(), NumberUtils.toInt(srcObject.toString()));
		} else {
			srcDate = (Date) srcObject;
		}
		monthRange = Math.abs(monthRange);
		Date fromDate = DateUtils.addMonths(referDate, -monthRange);
		Date toDate = DateUtils.addMonths(referDate, monthRange);
		boolean hasSame = srcDate.after(fromDate) && srcDate.before(toDate);
		return hasSame;
	}

	@SuppressWarnings("unchecked")
	private Boolean hasSameDirector(SolrDocument doc, SolrDocument mDoc) {
		Object srcObject = mDoc.getFieldValue("director_qtxt");
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
		srcDirector = srcDirector.toLowerCase();
		Collection<Object> directors = (Collection<Object>) referObject;
		Boolean hasSame = isContains(srcDirector, directors);
		if (!hasSame && !isSameLang(srcDirector, JSON.toJSONString(directors))) {
			hasSame = null;
		}
		return hasSame;
	}

	private boolean isSameLang(String lChars, String rChars) {
		Pattern cnReg = Pattern.compile("([\u4e00-\u9fa5]+)");
		Matcher lMatcher = cnReg.matcher(lChars);
		Matcher rMatcher = cnReg.matcher(JSON.toJSONString(rChars));
		boolean lHasCn = lMatcher.find();
		boolean rHasCn = rMatcher.find();
		// 中文 vs 英文
		return lHasCn & rHasCn;
	}

	private List<SolrDocument> getMetaDocuments(SolrServer metaServer, SolrQuery solrQuery) {
		try {
			QueryResponse sResp = metaServer.query(solrQuery);
			return sResp.getResults();
		} catch (SolrServerException e) {
			log.warn("", e);
		}
		return java.util.Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private SolrQuery createMetaQuery(SolrDocument doc, boolean useDirector) {
		int offset = 0;
		int limit = 100;

		List<String> missFields = Lists.newArrayList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		Collection<Object> nameList = doc.getFieldValues("names");
		Set<Object> nameSet = Sets.newHashSet();
		nameSet.add(doc.getFieldValue("name"));
		if (nameList != null) {
			nameSet.addAll(nameList);
		}
		String sHead = "(";
		StringBuilder sb = new StringBuilder(sHead);
		for (Object nameObj : nameSet) {
			if (nameObj == null) {
				continue;
			}
			String sName = nameObj.toString();
			sName = SYMBOL_REG.matcher(sName).replaceAll(" ");
			sName = IGNORE_REG.matcher(sName).replaceAll(" ");
			Matcher matcher = CN_NAME_REG.matcher(sName);
			if (matcher.find()) {
				String sPart = matcher.group(1).trim();
				if (StringUtils.isNotBlank(sPart)) {
					sName = sPart;
				}
			}
			sName = sName.trim();
			if (StringUtils.isBlank(sName)) {
				continue;
			}
			// sName = sName.replaceAll("[\\s]+", "").trim();
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sName = ClientUtils.escapeQueryChars(sName);
			sb.append(sName);
		}
		sb.append(")");
		String sQuery = sb.toString();
		if (sb.length() < 3) {
			sQuery = ClientUtils.escapeQueryChars(doc.getFieldValue("name").toString());
		}
		solrQuery.set("q", sQuery);
		solrQuery.addFilterQuery(torrentFilterQuery);
		Object yearObj = doc.getFieldValue("year");
		if (yearObj != null && NumberUtils.isNumber(yearObj.toString())) {
			int year = NumberUtils.toInt(yearObj.toString());
			StringBuilder sbQuery = new StringBuilder("year_ti:(");
			sbQuery.append(year - 1);
			sbQuery.append(" OR ");
			sbQuery.append(year);
			sbQuery.append(" OR ");
			sbQuery.append(year + 1);
			sbQuery.append(")");
			String sYearQuery = sbQuery.toString();
			solrQuery.addFilterQuery(sYearQuery);
		} else {
			missFields.add("year");
		}
		if (!missFields.isEmpty()) {
			String idString = doc.getFieldValue("id").toString();
			log.warn("cmovie(" + idString + "),checks:" + ArrayUtils.toString(missFields));
		}
		return solrQuery;
	}

	private SolrDocumentList getEmptyTorrentMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "id:[" + fromId + " TO *]");
		solrQuery.addSort("id", ORDER.asc);
		// solrQuery.addFilterQuery("torrents_size:0");
		// solrQuery.set("q", "id:0637825677");
		QueryResponse resp = movieServer.query(solrQuery);
		return resp.getResults();
	}

	private SolrDocumentList getMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "id:[" + fromId + " TO *]");
		solrQuery.addSort("id", ORDER.asc);
		// solrQuery.addFilterQuery("timestamp:[* TO 2016-11-01T00:00:19.545Z]");
		// solrQuery.addFilterQuery("torrents_size:[1 TO *]");
		// solrQuery.set("q", "id:0636784043");
		QueryResponse resp = movieServer.query(solrQuery);
		return resp.getResults();
	}

}
