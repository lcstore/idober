package com.lezo.idober.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.utils.SolrUtils;

/**
 * 
 * 组合电影ID,构建cms列表
 *
 * @author lilinchong
 * @since 2016年7月28日
 */
@Log4j
public class AssembleIdMovieTimer implements Runnable {
	private static AtomicBoolean running = new AtomicBoolean(false);

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			log.warn(this.getClass().getSimpleName() + " is runing...");
			return;
		}
		long startMills = System.currentTimeMillis();
		int total = 0;
		try {
			buildUpcoming();
			buildPlaying();
			buildNewly();
			buildClassic();
			buildRegionRanks();
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			log.warn(this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total);
			running.set(false);
		}

	}

	private void buildRegionRanks() {
		Map<String, String> regionMap = Maps.newHashMap();
		regionMap.put("hot-huayu", "华语");
		regionMap.put("hot-oumei", "欧美");
		regionMap.put("hot-hanguo", "韩国");
		regionMap.put("hot-riben", "日本");
		Integer limit = 100;
		for (Entry<String, String> entry : regionMap.entrySet()) {
			String group = "" + entry.getKey();
			String title = entry.getValue() + "热度榜";
			String sSortName = entry.getValue();
			try {
				buildByDoubanSort(group, title, limit, sSortName);
			} catch (Exception e) {
				log.warn("build,type:" + group + ",cause:", e);
			}
		}

	}

	private void buildClassic() {
		String group = "classic";
		String title = "经典电影";
		String sSortName = "经典";
		Integer limit = 100;
		try {
			buildByDoubanSort(group, title, limit, sSortName);
		} catch (Exception e) {
			log.warn("build,type:" + group + ",cause:", e);
		}
	}

	private void buildByDoubanSort(String group, String title, Integer limit, String sSortName) throws Exception {
		SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		SolrInputDocument inDoc = newDocument(group, title);
		String sortField = sSortName + "_rank_ti";
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", sortField + ":*");
		solrQuery.addFilterQuery("type:douban-movie-sort");
		solrQuery.addSort("date_s", ORDER.desc);
		QueryResponse resp = sourceServer.query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (docList.isEmpty()) {
			return;
		}
		Object dateGroupObj = docList.get(0).getFieldValue("date_s");
		String sDateGroup = dateGroupObj == null ? null : dateGroupObj.toString();
		Map<String, Integer> codeMap = Maps.newHashMap();
		for (SolrDocument doc : docList) {
			String sCode = (String) doc.getFieldValue("code_s");
			if (StringUtils.isBlank(sCode)) {
				continue;
			}
			String sCurDate = (String) doc.getFieldValue("date_s");
			if (StringUtils.isNotBlank(sDateGroup) && !sDateGroup.equals(sCurDate)) {
				break;
			}
			codeMap.put(sCode, codeMap.size());
		}
		SolrDocumentList movieList = getMovieIdByCodes(codeMap.keySet());
		if (movieList.isEmpty()) {
			return;
		}
		JSONArray idArray = toIdJSONArray(movieList);
		inDoc.addField("content", idArray.toJSONString());
		sourceServer.add(inDoc);
		sourceServer.commit();
	}

	private void buildNewly() {
		String group = "newly";
		String title = "最新电影";
		Integer limit = 36;
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setStart(0);
			solrQuery.setRows(limit);
			solrQuery.set("q", "(torrents_size:[1 TO *] OR shares_size:[1 TO *])");
			solrQuery.addField("id");
			solrQuery.addSort("release", ORDER.desc);
			SolrDocumentList docList = getMovieIdByQuery(solrQuery);
			if (!docList.isEmpty()) {
				SolrInputDocument inDoc = newDocument(group, title);
				JSONArray idArray = toIdJSONArray(docList);
				inDoc.addField("content", idArray.toJSONString());
				SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
				sourceServer.add(inDoc);
				sourceServer.commit();
			}
		} catch (Exception e) {
			log.warn("build,type:" + group + ",cause:", e);
		}
	}

	private void buildUpcoming() {
		String group = "upcoming";
		String title = "即将上映";
		String sCategory = "upcoming";
		Integer limit = 100;
		try {
			buildByDoubanPlaying(group, title, limit, sCategory);
		} catch (Exception e) {
			log.warn("build,type:" + group + ",cause:", e);
		}
	}

	private void buildPlaying() throws Exception {
		String group = "playing";
		String title = "正在热播";
		String sCategory = "nowplaying";
		Integer limit = 100;
		try {
			buildByDoubanPlaying(group, title, limit, sCategory);
		} catch (Exception e) {
			log.warn("build,type:" + group + ",cause:", e);
		}
	}

	private void buildByDoubanPlaying(String group, String title, Integer limit, String sCategory) throws Exception {
		SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		SolrInputDocument inDoc = newDocument(group, title);

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "category_s:" + sCategory);
		solrQuery.addFilterQuery("type:douban-movie-playing");
		solrQuery.addSort("date_s", ORDER.desc);
		solrQuery.addSort("ticket_rate_tf", ORDER.desc);
		QueryResponse resp = sourceServer.query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (docList.isEmpty()) {
			return;
		}
		String sDateGroup = docList.get(0).getFieldValue("date_s").toString();
		Map<String, Integer> codeMap = Maps.newHashMap();
		for (SolrDocument doc : docList) {
			String sCode = (String) doc.getFieldValue("code_s");
			if (StringUtils.isBlank(sCode)) {
				continue;
			}
			String sCurDate = (String) doc.getFieldValue("date_s");
			if (!sDateGroup.equals(sCurDate)) {
				break;
			}
			codeMap.put(sCode, codeMap.size());
		}
		SolrDocumentList movieList = getMovieIdByCodes(codeMap.keySet());
		if (movieList.isEmpty()) {
			return;
		}
		JSONArray idArray = toIdJSONArray(movieList);
		inDoc.addField("content", idArray.toJSONString());
		sourceServer.add(inDoc);
		sourceServer.commit();
	}

	private JSONArray toIdJSONArray(SolrDocumentList movieList) {
		List<String> idList = Lists.newArrayList();
		for (SolrDocument mDoc : movieList) {
			String id = (String) mDoc.getFieldValue("id");
			if (StringUtils.isBlank(id)) {
				continue;
			}
			idList.add(id);
		}
		JSONArray idArray = (JSONArray) JSONArray.toJSON(idList);
		return idArray;
	}

	private SolrDocumentList getMovieIdByCodes(Set<String> codeSet) throws Exception {
		if (CollectionUtils.isEmpty(codeSet)) {
			return new SolrDocumentList();
		}
		String sHead = "(";
		StringBuilder sb = new StringBuilder(sHead);
		for (String code : codeSet) {
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sb.append("code_s:");
			sb.append(code);
		}
		sb.append(")");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(codeSet.size());
		solrQuery.set("q", sb.toString());
		solrQuery.addField("id");

		return getMovieIdByQuery(solrQuery);
	}

	private SolrDocumentList getMovieIdByQuery(SolrQuery solrQuery) throws Exception {
		solrQuery = withCommonQuery(solrQuery);
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		QueryResponse resp = server.query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		return docList;
	}

	/**
	 * 每周周五(6)，增加一条记录Calendar.FRIDAY
	 * 
	 * @param destWeek
	 * @return
	 */
	private Date getCycleDate(int destWeek) {
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int days = destWeek - week;
		days = days <= 0 ? days : days - 7;
		Date current = new Date();
		Date cycleDate = DateUtils.addDays(current, days);
		return cycleDate;
	}

	private SolrInputDocument newDocument(String group, String title) {
		String type = "idober-movie-ids";
		Date cycleDate = getCycleDate(Calendar.FRIDAY);
		String sDate = DateFormatUtils.format(cycleDate, "yyyyMMdd");
		SolrInputDocument inDoc = new SolrInputDocument();
		String sIdVal = type + SolrUtils.VALUE_SPLITOR + group + SolrUtils.VALUE_SPLITOR + sDate;
		inDoc.addField("id", sIdVal);
		inDoc.addField("title", group + " " + sDate);
		inDoc.addField("type", type);
		inDoc.addField("date_s", sDate);
		inDoc.addField("group_s", group);
		return inDoc;
	}

	private SolrQuery withCommonQuery(SolrQuery solrQuery) {
		solrQuery.addFilterQuery("type:movie");
		return solrQuery;
	}

}