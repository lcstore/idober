package com.lezo.idober.timer;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TaskUtils;

@Log4j
public class QueryTorrent4NewMovieTimer implements Runnable {
	private static AtomicBoolean running = new AtomicBoolean(false);

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			log.warn(this.getClass().getSimpleName() + " is runing...");
			return;
		}
		long startMills = System.currentTimeMillis();
		int total = 0;
		int upcomingCount = 0;
		try {
			int foundCount = 1;
			int offset = 0;
			int limit = 100;
			SolrServer sourceServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
			while (upcomingCount < foundCount) {
				SolrDocumentList selectDocs = getUpcomingMovieWithTorrents(sourceServer, offset, limit);
				if (foundCount <= 1) {
					foundCount = (int) selectDocs.getNumFound();
				}
				newQueryTasks(selectDocs);
				total += selectDocs.size();
				offset += selectDocs.size();
				if (selectDocs.size() < limit) {
					break;
				}
			}
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			long costMills = System.currentTimeMillis() - startMills;
			log.warn(this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total);
			running.set(false);
		}

	}

	private void newQueryTasks(SolrDocumentList selectDocs) throws Exception {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return;
		}
		JSONArray taskList = new JSONArray();
		for (SolrDocument doc : selectDocs) {
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
		}
		createTasks(taskList);
	}

	private void createTasks(JSONArray taskArray) throws Exception {
		TaskUtils.createTasks(taskArray);
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
	private SolrDocumentList getUpcomingMovieWithTorrents(SolrServer sourceServer, int offset, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		// 从7天前开始，即将上映的电影
		solrQuery.set("q", "(torrents_size:0 AND release:[NOW-90DAY/DAY TO *])");
		solrQuery.addField("id,name");
		QueryResponse resp = sourceServer.query(solrQuery);
		return resp.getResults();
	}

}
