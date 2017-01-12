package com.lezo.idober.action;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.action.movie.MovieEditListController;
import com.lezo.idober.timer.AssembleIdMovieTimer;
import com.lezo.idober.timer.FillTorrent2MovieTimer;
import com.lezo.idober.timer.OnlineTorrentMovieTimer;
import com.lezo.idober.timer.QueryTorrent4NewMovieTimer;
import com.lezo.idober.timer.UnifyRegionTimer;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TaskUtils;

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
	@Autowired
	private MovieEditListController movieEditListController;

	@ResponseBody
	@RequestMapping(value = { "collect" }, method = RequestMethod.GET)
	public String collectTorrent4Edit()
			throws Exception {
		Integer curPage = 1;
		Integer beforeDay = 300;
		int start = 0;
		int limit = 500;
		Set<String> idSet = Sets.newHashSet();
		while (true) {
			start = (curPage - 1) * limit;
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setStart(start);
			solrQuery.setRows(limit);
			solrQuery.set("q", "(release:[NOW-" + beforeDay +
					"DAY/DAY TO NOW/DAY+7DAY])");
//			solrQuery.set("q", "(release:[* TO NOW/DAY+7DAY])");
			solrQuery.addFilterQuery("type:movie");
			solrQuery.addFilterQuery("(torrents_size:0 AND shares_size:0)");
			solrQuery.addSort("release", ORDER.desc);
			solrQuery.addField("id,name");

			QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			List<String> idList = hasFeedMovieIds(docList);
			idSet.addAll(idList);
			if (docList.size() < limit) {
				break;
			}
			curPage++;
		}
		String sContent = ArrayUtils.toString(idSet);
		SolrInputDocument inDoc = new SolrInputDocument();
		String type = "idober-movie-ids";
		String sGroup = "wait4edit";
		inDoc.setField("type", type);
		inDoc.setField("group_s", sGroup);
		inDoc.setField("id", type + ";" + sGroup);
		inDoc.setField("content", sContent);
		SolrServer mServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		mServer.add(inDoc);
		mServer.commit();
		return sContent;
	}

	private List<String> hasFeedMovieIds(SolrDocumentList docList) {
		if (CollectionUtils.isEmpty(docList)) {
			return Collections.emptyList();
		}
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRows(0);
		solrQuery.addFilterQuery("source_group_s:torrent");
		solrQuery.addFilterQuery("!delete_ti:1");
		List<String> movieIdList = Lists.newArrayList();
		for (SolrDocument doc : docList) {
			String name = doc.getFieldValue("name").toString();
			if (StringUtils.isBlank(name)) {
				continue;
			}
			name = ClientUtils.escapeQueryChars(name);
			solrQuery.set("q", "title:" + name);
			try {
				QueryResponse resp = server.query(solrQuery);
				long srcCount = resp.getResults().getNumFound();
				if (srcCount > 0) {
					String sCode = doc.getFieldValue("id").toString();
					movieIdList.add(sCode);
				}
			} catch (Exception e) {
				log.warn("fillFeedInfo,name:" + name + ",cause:", e);
			}
		}
		return movieIdList;
	}

	@ResponseBody
	@RequestMapping(value = { "source" }, method = RequestMethod.GET)
	public JSONObject fillSources(@RequestParam(name = "limit", defaultValue = "1000") Integer maxCount)
			throws Exception {
		SolrServer srcServer = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		SolrServer referServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
		int srcCount = 0;
		int taskCount = 0;
		int limit = 100;
		String fromId = "0";
		while (true) {
			SolrDocumentList selectDocs = null;
			try {
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.setStart(0);
				solrQuery.setRows(limit);
				solrQuery.set("q", "id:[" + fromId + " TO *]");
				solrQuery.addSort("id", ORDER.asc);
				solrQuery.addField("id,code_s");
				QueryResponse resp = srcServer.query(solrQuery);
				selectDocs = resp.getResults();
			} catch (Exception e) {
				log.warn("", e);
			}
			srcCount += selectDocs.size();
			if (selectDocs.size() < limit) {
				break;
			}
			Map<String, SolrDocument> idMap = Maps.newHashMap();
			StringBuffer sb = new StringBuffer();
			for (SolrDocument doc : selectDocs) {
				String curId = doc.getFieldValue("id").toString();
				if (fromId.compareTo(curId) < 0) {
					fromId = curId;
				}
				if (sb.length() > 0) {
					sb.append(" OR ");
				}
				idMap.put(curId, doc);
				sb.append(curId);
			}
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setStart(0);
			solrQuery.setRows(selectDocs.size());
			solrQuery.set("q", "id:(" + sb.toString() + ")");
			solrQuery.addField("id");
			QueryResponse resp = referServer.query(solrQuery);
			SolrDocumentList hasDocs = resp.getResults();
			JSONArray taskList = new JSONArray();
			for (SolrDocument hasDoc : hasDocs) {
				String sHasId = hasDoc.getFieldValue("id").toString();
				idMap.remove(sHasId);
			}
			for (SolrDocument hasDoc : idMap.values()) {
				Object codeObj = hasDoc.getFieldValue("code_s");
				if (codeObj == null) {
					continue;
				}
				taskCount++;
				String sHasId = hasDoc.getFieldValue("id").toString();
				String sUrl = "https://movie.douban.com/subject/" + codeObj.toString() + "/";
				JSONObject taskObj = new JSONObject();
				taskObj.put("type", "douban-movie-detail");
				taskObj.put("url", sUrl);
				taskObj.put("level", 1000);
				JSONObject argsObj = new JSONObject();
				argsObj.put("mid", sHasId);
				argsObj.put("retry", "0");
				taskObj.put("args", argsObj);
				taskList.add(taskObj);
			}
			TaskUtils.createTasks(taskList);
			if (taskCount >= maxCount) {
				break;
			}

		}
		JSONObject retObject = new JSONObject();
		retObject.put("srcCount", srcCount);
		retObject.put("taskCount", taskCount);
		return retObject;
	}

	@ResponseBody
	@RequestMapping(value = { "search" }, method = RequestMethod.GET)
	public String modifyDetail(@RequestParam(name = "day", defaultValue = "60") Integer beforeDay) throws Exception {
		SolrServer movieServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
		int limit = 100;
		String fromId = "0";
		while (true) {
			SolrDocumentList selectDocs = null;
			try {
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.setStart(0);
				solrQuery.setRows(limit);
				solrQuery.set("q", "id:[" + fromId + " TO *]");
				solrQuery.addFilterQuery("(release:[NOW-" + beforeDay + "DAY/DAY TO NOW/DAY+7DAY])");
				solrQuery.addFilterQuery("type:movie");
				solrQuery.addFilterQuery("(torrents_size:0 AND shares_size:0)");
				solrQuery.addSort("id", ORDER.asc);
				QueryResponse resp = movieServer.query(solrQuery);
				selectDocs = resp.getResults();
			} catch (Exception e) {
				log.warn("", e);
			}
			if (selectDocs == null) {
				break;
			}
			if (selectDocs.size() < limit) {
				break;
			}
			JSONArray tArray = new JSONArray();
			for (SolrDocument doc : selectDocs) {
				String curId = doc.getFieldValue("id").toString();
				if (fromId.compareTo(curId) < 0) {
					fromId = curId;
				}
				Object nameObj = doc.getFieldValue("name");
				if (nameObj != null) {
					tArray.add(nameObj);
				}
			}
			if (!tArray.isEmpty()) {
				JSONObject paramObject = new JSONObject();
				paramObject.put("names", tArray);
				movieEditListController.searchTorrents(paramObject);
			}
		}
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = { "fixtorrent" }, method = RequestMethod.GET)
	public String fixBt(@RequestParam(value = "core", defaultValue = "cmovie") String core) throws Exception {
		SolrServer movieServer = SolrUtils.getSolrServer(core);
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
					// newDocumentList.add(inDoc);
					continue;
				}
				List<Object> keepList = Lists.newArrayList();
				boolean bError = false;
				for (Object tor : torrents) {
					String sTor = tor.toString();
					if (sTor.contains("bttiantang") || sTor.contains("xiamp4.com")) {
						bError = true;
					} else {
						keepList.add(tor);
					}
				}
				if (bError) {
					inDoc.setField("torrents", keepList);
					newDocumentList.add(inDoc);
				}
			}
			if (!newDocumentList.isEmpty()) {
				for (SolrDocument uDoc : newDocumentList) {
					SolrInputDocument inDoc = ClientUtils.toSolrInputDocument(uDoc);
					// Map<String, Object> setMap = Maps.newHashMap();
					// setMap.put("set", uDoc.getFieldValue("code_s"));
					// inDoc.setField("code_s", setMap);
					// setMap = Maps.newHashMap();
					// setMap.put("set", uDoc.getFieldValue("torrents"));
					// inDoc.setField("torrents", setMap);
					movieServer.add(inDoc);
					movieServer.commit();
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

	private void newDetailTasks(SolrDocumentList selectDocs) throws Exception {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return;
		}
		JSONArray taskList = new JSONArray();
		for (SolrDocument doc : selectDocs) {
			String sId = doc.getFieldValue("id").toString();
			String sCode = doc.getFieldValue("code_s").toString();
			String sName = doc.getFieldValue("name").toString();
			String sUrl = "https://movie.douban.com/subject/" + sCode + "/";
			JSONObject taskObj = new JSONObject();
			taskObj.put("type", "douban-movie-detail");
			taskObj.put("url", sUrl);
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

	private void newQueryTasks(SolrDocumentList selectDocs) throws Exception {
		if (CollectionUtils.isEmpty(selectDocs)) {
			return;
		}
		JSONArray taskList = new JSONArray();
		for (SolrDocument doc : selectDocs) {
			String sId = doc.getFieldValue("id").toString();
			if (doc.getFieldValue("name") == null) {
				log.warn("empty name,id:" + sId);
				continue;
			}
			Object codeObj = doc.getFieldValue("code_s");
			if (codeObj == null) {
				continue;
			}
			String sUrl = "https://movie.douban.com/subject/" + codeObj.toString() + "/";
			String sName = doc.getFieldValue("name").toString();
			JSONObject taskObj = new JSONObject();
			// taskObj.put("type", "query-movie");
			// taskObj.put("type", "sogou-article-search");
			taskObj.put("type", "douban-movie-detail");
			taskObj.put("url", sUrl);
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

	private SolrDocumentList getMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.set("q", "id:[" + fromId + " TO *]");
		solrQuery.addSort("id", ORDER.asc);
		// solrQuery.addFilterQuery("timestamp:[* TO 2016-11-01T00:00:19.545Z]");
		solrQuery.addFilterQuery("torrents_size:[1 TO *]");
		// solrQuery.addField("id,torrents,code_s,name");
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
