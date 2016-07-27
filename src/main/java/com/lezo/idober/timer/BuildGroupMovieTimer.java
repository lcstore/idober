package com.lezo.idober.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

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

import com.google.common.collect.Sets;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class BuildGroupMovieTimer implements Runnable {
	private static final String CORE_NAME_MOVIE = "cmovie";
	private static final String CORE_NAME_OLD = "core2";
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
	private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");
	private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");

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
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			log.warn(this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total);
			running.set(false);
		}

	}

	private void buildUpcoming() throws Exception {
		String type = "idober-id-upcoming";
		String title = "即将上映";
		Integer limit = 100;
		SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
		// 每周周五，增加一条记录
		int destWeek = 6;
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int days = destWeek - week;
		days = days <= 0 ? days : days - 7;
		Date current = new Date();
		Date lastFridDate = DateUtils.addDays(current, days);
		String sDate = DateFormatUtils.format(lastFridDate, "yyyyMMdd");
		SolrInputDocument inDoc = new SolrInputDocument();
		String sIdVal = type + SolrUtils.VALUE_SPLITOR + sDate;
		inDoc.addField("id", sIdVal);
		inDoc.addField("title", title + " " + sDate);
		inDoc.addField("type", type);
		inDoc.addField("date_s", sDate);

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "category_s:upcoming");
		solrQuery.addFilterQuery("type:douban-movie-playing");
		solrQuery.addSort("date_s", ORDER.desc);
		QueryResponse resp = sourceServer.query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (docList.isEmpty()) {
			return;
		}
		String sDateGroup = docList.get(0).getFieldValue("date_s").toString();
		Set<String> codeSet = Sets.newLinkedHashSet();
		for (SolrDocument doc : docList) {
			String sCode = (String) doc.getFieldValue("code_s");
			if (StringUtils.isBlank(sCode)) {
				continue;
			}
			String sCurDate = (String) doc.getFieldValue("date_s");
			if (!sDateGroup.equals(sCurDate) && codeSet.size() < 10) {
				sDateGroup = sCurDate;
			} else {
				break;
			}
			codeSet.add(sCode);
		}
		SolrDocumentList movieList = getMovieByCodes(codeSet);

	}

	private SolrDocumentList getMovieByCodes(Set<String> codeSet) throws Exception {
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
		solrQuery.addField("id,code_s,torrents_size,shares_size");
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		QueryResponse resp = server.query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		return docList;
	}

}