package com.lezo.idober.action;

import java.util.Collection;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.timer.AssembleIdMovieTimer;
import com.lezo.idober.timer.FillTorrent2MovieTimer;
import com.lezo.idober.timer.OnlineTorrentMovieTimer;
import com.lezo.idober.timer.QueryTorrent4NewMovieTimer;
import com.lezo.idober.timer.UnifyRegionTimer;
import com.lezo.idober.utils.SolrUtils;

@Log4j
@Controller
@RequestMapping("time")
public class TimeController extends BaseController {
	@Autowired
	private FillTorrent2MovieTimer fillTorrent2MovieTimer;
	@Autowired
	private UnifyRegionTimer unifyRegionTimer;
	@Autowired
	private OnlineTorrentMovieTimer onlineTorrentMovieTimer;
	@Autowired
	private QueryTorrent4NewMovieTimer queryTorrent4NewMovieTimer;
	@Autowired
	private AssembleIdMovieTimer assembleIdMovieTimer;

	@ResponseBody
	@RequestMapping(value = { "fixtorrent" }, method = RequestMethod.GET)
	public String fixBt() throws Exception {
		SolrServer movieServer = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		int limit = 100;
		String fromId = "0";
		while (true) {
			SolrDocumentList selectDocs = null;
			try {
				selectDocs = getMovieByIdWithLimit(movieServer, fromId, limit);
			} catch (Exception e) {
				log.warn("", e);
			}
			if (selectDocs == null) {
				break;
			}
			SolrDocumentList newDocumentList = new SolrDocumentList();
			for (SolrDocument inDoc : selectDocs) {
				Collection<Object> torrents = inDoc.getFieldValues("torrents");
				if (CollectionUtils.isEmpty(torrents)) {
					continue;
				}
				boolean bError = false;
				for (Object tor : torrents) {
					if (tor.toString().contains("bttiantang")) {
						bError = true;
						break;
					}
				}
				if (bError || true) {
					newDocumentList.add(inDoc);
				}
			}
			newQueryTasks(newDocumentList);
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
		}
		return "OK";
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
		if (taskArray == null || taskArray.size() < 1) {
			return;
		}
		String url = "http://www.lezomao.com:8090/taskmgr/createtasks";
		String referrer = "http://www.lezomao.com/idober";
		Response resp =
				Jsoup.connect(url).referrer(referrer).method(Method.POST).data("tasks", taskArray.toJSONString())
						.ignoreContentType(true).execute();
		log.info("task:" + taskArray.size() + ",resp:" + resp.body());
	}

	private SolrDocumentList getMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "id:[" + fromId + " TO *]");
		solrQuery.addSort("id", ORDER.asc);
		solrQuery.addFilterQuery("timestamp:[* TO 2016-11-01T00:00:19.545Z]");
		QueryResponse resp = movieServer.query(solrQuery);
		return resp.getResults();
	}

	@ResponseBody
	@RequestMapping(value = { "torrent" },
			method = RequestMethod.GET)
	public String fillTorrent2MovieTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		fillTorrent2MovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,fillTorrent2MovieTimer,cost:" + costMills);
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "query" },
			method = RequestMethod.GET)
	public String unifyRegionTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		queryTorrent4NewMovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,queryTorrent4NewMovieTimer,cost:" + costMills);
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "move" },
			method = RequestMethod.GET)
	public String onlineTorrentMovieTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		onlineTorrentMovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,onlineTorrentMovieTimer,cost:" + costMills);
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "assemble" }, method = RequestMethod.GET)
	public String assembleIdMovieTimer() throws Exception {
		long startMills = System.currentTimeMillis();
		assembleIdMovieTimer.run();
		long costMills = System.currentTimeMillis() - startMills;
		log.info("done,assembleIdMovieTimer,cost:" + costMills);
		return "OK";
	}
}
