package com.lezo.idober.action.movie;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.timer.OnlineTorrentMovieTimer;
import com.lezo.idober.utils.CountryUtils;
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.RegionUtils;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TaskUtils;
import com.lezo.idober.utils.TypeUtils;
import com.lezo.idober.vo.ActionReturnVo;

@Controller
@Log4j
@RequestMapping("movie/edit")
public class MovieEditDetailController extends BaseController {
	private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
	private static final String CORE_MOVIE = SolrUtils.CORE_SOURCE_MOVIE;
	private static final Pattern CN_BLANK_REG = Pattern.compile("([\u4e00-\u9fa50-9]+\\s+)");
	private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");
	@Autowired
	private OnlineTorrentMovieTimer onlineTorrentMovieTimer;

	@RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
	public ModelAndView loadDetail(@PathVariable("itemCode") String itemCode, ModelMap model) throws Exception {
		itemCode = Jsoup.clean(itemCode, Whitelist.basic());
		Matcher matcher = NUM_REG.matcher(itemCode);
		SolrDocument doc = null;
		if (matcher.find()) {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setStart(0);
			solrQuery.setRows(1);
			solrQuery.set("q", "(id:" + itemCode + " OR old_id_s:" + itemCode + ")");
			solrQuery.addFilterQuery("type:movie");
			QueryResponse resp = SolrUtils.getSolrServer(CORE_MOVIE).query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			if (CollectionUtils.isNotEmpty(docList)) {
				doc = docList.get(0);
			}
		}
		if (doc == null) {
			throw new NotFoundException();
		}
		JSONObject dObject = convert2JSON(doc);
		model.addAttribute("qAction", "/search/movie/edit");
		model.addAttribute("oDoc", dObject);
		return new ModelAndView("MovieEditDetail");
	}

	@ResponseBody
	@RequestMapping(value = { "detail" }, method = RequestMethod.POST)
	public ActionReturnVo updateDetail(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		JSONArray idArray = paramObject.getJSONArray("ids");
		if (CollectionUtils.isEmpty(idArray)) {
			returnVo.setMsg("empty id array");
			returnVo.setCode(ActionReturnVo.CODE_PARAM);
			return returnVo;
		}
		JSONArray taskArray = new JSONArray();
		List<String> typeList = Lists.newArrayList("douban-movie-detail");
		for (int index = 0, size = idArray.size(); index < size; index++) {
			String idString = idArray.getString(index);
			if (StringUtils.isBlank(idString)) {
				continue;
			}
			for (String type : typeList) {
				JSONObject taskObject = new JSONObject();
				taskObject.put("type", type);
				taskObject.put("url", "https://movie.douban.com/subject/" + idString + "/");
				taskObject.put("level", 1000);
				taskObject = TaskUtils.withParam(taskObject, "retry", "0");
				taskObject = TaskUtils.withParam(taskObject, "id", idString);
				taskArray.add(taskObject);
			}
		}
		TaskUtils.createTasks(taskArray);
		return returnVo;
	}

	@ResponseBody
	@RequestMapping(value = { "copyright" }, method = RequestMethod.POST)
	public ActionReturnVo updateCopyright(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		JSONArray idArray = paramObject.getJSONArray("ids");
		if (CollectionUtils.isEmpty(idArray)) {
			returnVo.setMsg("empty id array");
			returnVo.setCode(ActionReturnVo.CODE_PARAM);
			return returnVo;
		}
		int copyright = paramObject.getIntValue("copyright");
		JSONArray docArray = new JSONArray();
		for (int index = 0, size = idArray.size(); index < size; index++) {
			String idString = idArray.getString(index);
			if (StringUtils.isBlank(idString)) {
				continue;
			}
			JSONObject docObj = new JSONObject();
			docObj.put("id", idString);
			Map<String, Object> setValMap = new JSONObject();
			setValMap.put("set", copyright);
			docObj.put("copyright_s", setValMap);
			docArray.add(docObj);
		}
		if (docArray.isEmpty()) {
			return returnVo;
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
		NamedList<Object> resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).request(request);
		resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).request(request);
		log.info("updateCopyright.resp:" + resp.size() + ",update:" + docArray.size());
		return returnVo;
	}

	@ResponseBody
	@RequestMapping(value = { "deploy" }, method = RequestMethod.POST)
	public ActionReturnVo deployMovie(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		JSONArray idArray = paramObject.getJSONArray("ids");
		if (CollectionUtils.isEmpty(idArray)) {
			returnVo.setMsg("empty id array");
			returnVo.setCode(ActionReturnVo.CODE_PARAM);
			return returnVo;
		}
		StringBuilder sb = new StringBuilder();
		for (int index = 0, size = idArray.size(); index < size; index++) {
			String idString = idArray.getString(index);
			if (StringUtils.isBlank(idString)) {
				continue;
			}
			if (sb.length() > 1) {
				sb.append(" OR ");
			}
			sb.append(idString);
		}
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(idArray.size());
		solrQuery.set("q", "id:(" + sb.toString() + ")");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
		SolrDocumentList srcList = resp.getResults();

		Map<String, Object> moveModifier = Maps.newHashMap();
		moveModifier.put("set", "1");
		Map<String, Object> addModifier = Maps.newHashMap();
		addModifier.put("add", new Date());
		List<String> rmFields = Lists.newArrayList();
		rmFields.add("_version_");
		rmFields.add("creation");
		rmFields.add("timestamp");
		rmFields.add("tcount");
		rmFields.add("torrents_size");
		rmFields.add("shares_size");
		rmFields.add("scount");
		rmFields.add("editor");
		SolrServer srcServer = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE);
		SolrServer destServer = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		for (SolrDocument srcDoc : srcList) {
			SolrInputDocument newDoc = ClientUtils.toSolrInputDocument(srcDoc);
			for (String field : rmFields) {
				newDoc.remove(field);
			}
			createIfHasShares(srcDoc, newDoc);
			unifyRegion(srcDoc, newDoc);
			newDoc.setField("creation", addModifier);
			destServer.add(newDoc);

			SolrInputDocument srcInDoc = new SolrInputDocument();
			srcInDoc.setField("id", srcDoc.getFieldValue("id"));
			srcInDoc.setField("had_move_s", moveModifier);
			srcServer.add(srcInDoc);
		}
		destServer.commit();
		srcServer.commit();
		return returnVo;
	}

	@ResponseBody
	@RequestMapping(value = { "fillTorrents" }, method = RequestMethod.POST)
	public ActionReturnVo fillTorrents(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		String idString = paramObject.getString("id");

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.set("q", "id:" + idString);
		solrQuery.addField("id,name,names,imdb_s,year,actors,release,content,directors");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
		SolrDocumentList srcList = resp.getResults();

		Map<String, Object> fieldMap = Maps.newHashMap();
		fieldMap.put("set", 0);
		JSONArray docArray = new JSONArray();
		for (SolrDocument destDoc : srcList) {
			JSONObject tObject = new JSONObject();
			tObject.put("id", destDoc.getFieldValue("id"));
			tObject.put("had_move_s", fieldMap);
			solrQuery = createMetaQuery(destDoc);
			JSONArray tArray = new JSONArray();
			JSONArray sArray = new JSONArray();
			resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
			SolrDocumentList torrentList = resp.getResults();
			if (CollectionUtils.isEmpty(torrentList)) {
				continue;
			}
			for (SolrDocument tDoc : torrentList) {
				// 1.不匹配数据过滤
				if (!DocUtils.isSameMovieDoc(destDoc, tDoc)) {
					continue;
				}
				Object mContentObj = tDoc.getFieldValue("content");
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
						if (url.contains("bbs.rarbt.com")) {
							continue;
						}
						if (type.contains("share")) {
							if (url.contains(".baidu.com")) {
								sArray.add(torObj.toJSONString());
							}
						} else {
							tArray.add(torObj.toJSONString());
						}
					}
				}
			}
			tObject.put("torrents", tArray);
			tObject.put("shares", sArray);
			docArray.add(tObject);
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
		NamedList<Object> respList = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).request(request);
		log.info("resp:" + respList.size() + ",update:" + docArray.size());
		return returnVo;
	}

	private SolrQuery createMetaQuery(SolrDocument doc) {
		int offset = 0;
		int limit = 100;
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
			String sName = nameObj.toString().trim();
			List<String> partList = Lists.newArrayList();
			while (true) {
				Matcher matcher = CN_BLANK_REG.matcher(sName);
				if (matcher.find()) {
					String sPart = matcher.group().trim();
					partList.add(sPart);
					sName = matcher.replaceFirst("");
				} else {
					if (StringUtils.isNotBlank(sName)) {
						partList.add(sName);
					}
					break;
				}

			}

			if (partList.isEmpty()) {
				continue;
			}
			for (String sPart : partList) {
				sPart = IGNORE_REG.matcher(sPart).replaceAll(" ");
				sPart = sPart.trim();
				if (StringUtils.isBlank(sPart)) {
					continue;
				}
				if (sb.length() > sHead.length()) {
					sb.append(" OR ");
				}
				sPart = ClientUtils.escapeQueryChars(sPart);
				sb.append(sPart);
			}
		}
		sb.append(")");
		String sQuery = sb.toString();
		if (sb.length() < 3) {
			sQuery = ClientUtils.escapeQueryChars(doc.getFieldValue("name").toString());
		}

		StringBuilder typeBuilder = new StringBuilder();
		typeBuilder.append("type:(");
		List<String> typeList = TypeUtils.getTypeList();
		for (int i = 0, len = typeList.size(); i < len; i++) {
			String type = typeList.get(i);
			if (i > 0) {
				typeBuilder.append(" OR ");
			}
			typeBuilder.append(type);
		}
		typeBuilder.append(")");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", sQuery);
		solrQuery.addFilterQuery(typeBuilder.toString());
		return solrQuery;
	}

	@SuppressWarnings("unchecked")
	private void unifyRegion(SolrDocument srcDoc, SolrInputDocument newDoc) throws Exception {
		String fieldName = "regions";
		Object rObject = srcDoc.getFieldValue(fieldName);
		if (rObject == null) {
			return;
		}
		Collection<String> regionList = (Collection<String>) rObject;
		Set<String> regionSet = RegionUtils.formatRegions(regionList);
		String sUnifyCountry = CountryUtils.unifyCountry(regionSet);
		String sUnifyRegion = RegionUtils.country2Region(regionSet);
		Set<String> uRegions = Sets.newLinkedHashSet();
		if (StringUtils.isNotBlank(sUnifyCountry)) {
			uRegions.add(sUnifyCountry);
		}
		if (StringUtils.isNotBlank(sUnifyRegion)) {
			uRegions.add(sUnifyRegion);
		}
		newDoc.setField(fieldName, uRegions);

	}

	private void createIfHasShares(SolrDocument doc, SolrInputDocument inDoc) throws Exception {
		String torrentField = "torrents";
		String shareField = "shares";
		Collection<Object> torrents = doc.getFieldValues(torrentField);
		Collection<Object> shares = doc.getFieldValues(shareField);
		List<Object> srcTorrents = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(torrents)) {
			srcTorrents.addAll(torrents);
		}
		Map<String, String> shareMap = Maps.newHashMap();
		Map<String, String> torrentMap = Maps.newHashMap();
		boolean hasShare = false;
		if (CollectionUtils.isNotEmpty(shares)) {
			for (Object share : shares) {
				JSONObject tObject = JSONObject.parseObject(share.toString());
				String sUrl = tObject.getString("url");
				sUrl = sUrl == null ? tObject.getString("name") : sUrl;
				sUrl = sUrl == null ? "" : sUrl;
				if (sUrl.contains(".baidu.com")) {
					shareMap.put(sUrl, tObject.toJSONString());
					hasShare = true;
				}
			}
		}
		for (Object torrent : srcTorrents) {
			JSONObject tObject = JSONObject.parseObject(torrent.toString());
			tObject.remove("data");
			String type = tObject.getString("type");
			String source = tObject.getString("source");
			String sUrl = tObject.getString("url");
			sUrl = sUrl == null ? type : sUrl;
			if (StringUtils.isBlank(source)) {
				if (sUrl != null && sUrl.contains(".bttiantang.com")) {
					source = "bttiantang-torrent";
				}
			}
			if (StringUtils.isBlank(type) && "bttiantang-torrent".equals(source)) {
				continue;
			} else if (type != null && type.endsWith("-share")) {
				sUrl = sUrl == null ? tObject.getString("name") : sUrl;
				sUrl = sUrl == null ? "" : sUrl;
				if (sUrl.contains(".baidu.com") && hasShare) {
					shareMap.put(sUrl, tObject.toJSONString());
				}
			} else if (type != null) {
				if (sUrl.contains("rarbt.com") && sUrl.contains("bbs")) {
					continue;
				} else {
					torrentMap.put(sUrl, tObject.toJSONString());
				}
			}
		}

		inDoc.setField(torrentField, torrentMap.values());
		inDoc.setField(shareField, shareMap.values());
	}

	@ResponseBody
	@RequestMapping(value = { "addtorrent" }, method = RequestMethod.POST)
	public ActionReturnVo addTorrent(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		String itemCode = paramObject.getString("id");
		Boolean bCover = paramObject.getBoolean("cover");
		String level = paramObject.getString("level");
		String name = paramObject.getString("name");
		Map<String, String> shareMap = Maps.newHashMap();
		JSONObject sObject = new JSONObject();
		String sUrl = paramObject.getString("url");
		String secret = paramObject.getString("secret");
		Long size = paramObject.getLong("size");
		name = StringUtils.isEmpty(name) ? "百度云分享" : name;
		sObject.put("url", sUrl);
		sObject.put("secret", secret);
		sObject.put("type", "baidu-share");
		sObject.put("name", name);
		sObject.put("level", level);
		sObject.put("size", size);
		shareMap.put(sUrl, sObject.toJSONString());
		if (bCover == null || !bCover) {
			SolrDocument movieDoc = queryMovieShareById(itemCode);
			if (movieDoc == null) {
				return returnVo;
			}
			Collection<Object> shares = movieDoc.getFieldValues("shares");
			if (CollectionUtils.isNotEmpty(shares)) {
				for (Object oContent : shares) {
					String sContent = oContent.toString();
					JSONObject srcObject = JSONObject.parseObject(sContent);
					String sLink = srcObject.getString("url");
					if (shareMap.containsKey(sLink)) {
						continue;
					}
					shareMap.put(sLink, sContent);
				}
			}
		}
		JSONArray docArray = new JSONArray();
		JSONObject docObj = new JSONObject();
		docObj.put("id", itemCode);
		Map<String, Object> setValMap = new JSONObject();
		setValMap.put("set", shareMap.values());
		docObj.put("shares", setValMap);
		docArray.add(docObj);
		String sContent = docArray.toJSONString();
		String contentType = "application/json";
		Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
		ContentStreamUpdateRequest request =
				new ContentStreamUpdateRequest("/update/json");
		for (ContentStream cs : strems) {
			request.addContentStream(cs);
		}
		request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		NamedList<Object> resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).request(request);
		log.info("resp:" + resp.size() + ",update:" + docArray.size());
		return returnVo;
	}

	@ResponseBody
	@RequestMapping(value = { "deltorrent" }, method = RequestMethod.POST)
	public ActionReturnVo deleteTorrent(@RequestBody JSONObject paramObject) throws Exception {
		ActionReturnVo returnVo = new ActionReturnVo();
		JSONArray idArray = paramObject.getJSONArray("ids");
		if (CollectionUtils.isEmpty(idArray)) {
			returnVo.setMsg("empty id array");
			returnVo.setCode(ActionReturnVo.CODE_PARAM);
			return returnVo;
		}
		JSONArray docArray = new JSONArray();
		for (int index = 0, size = idArray.size(); index < size; index++) {
			String idString = idArray.getString(index);
			if (StringUtils.isBlank(idString)) {
				continue;
			}
			JSONObject docObj = new JSONObject();
			docObj.put("id", idString);
			Map<String, Object> setValMap = new JSONObject();
			setValMap.put("set", 1);
			docObj.put("delete_ti", setValMap);
			docArray.add(docObj);
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
		NamedList<Object> resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).request(request);
		log.info("resp:" + resp.size() + ",update:" + docArray.size());
		return returnVo;
	}

	private SolrDocument queryMovieShareById(String itemCode) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.set("q", "(id:" + itemCode + ")");
		solrQuery.addField("shares");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (CollectionUtils.isNotEmpty(docList)) {
			return docList.get(0);
		}
		return null;
	}

	private JSONObject convert2JSON(SolrDocument doc) {
		JSONObject srcObject = new JSONObject(doc);
		JSONArray crumbArr = createCrumbs(srcObject);
		srcObject.put("crumbs", crumbArr);
		// assortTorrents(srcObject);
		fillFeeds(srcObject);
		return srcObject;
	}

	private void fillFeeds(JSONObject srcObject) {
		String name = srcObject.getString("name");
		if (StringUtils.isEmpty(name)) {
			return;
		}
		name = ClientUtils.escapeQueryChars(name);
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "title:" + name);
		solrQuery.setRows(10);
		solrQuery.addFilterQuery("source_group_s:torrent");
		solrQuery.addFilterQuery("!delete_ti:1");
		try {
			QueryResponse resp = server.query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			srcObject.put("src_count", docList.getNumFound());
			if (docList.isEmpty()) {
				// 显示一条空数据
				docList.add(new SolrDocument());
			}
			srcObject.put("feeds", docList);
		} catch (Exception e) {
			log.warn("fillFeedInfo,name:" + name + ",cause:", e);
		}
	}

	private JSONArray createCrumbs(JSONObject srcObject) {
		JSONArray crumbArr = new JSONArray();
		String type = srcObject.getString("type");
		type = type == null ? "movie" : type;
		Map<String, String> typeNameMap = Maps.newHashMap();
		typeNameMap.put("movie", "电影");
		JSONArray cArray = new JSONArray();
		JSONObject cObj = new JSONObject();
		cObj.put("name", typeNameMap.get(type));
		cObj.put("link", "/" + type + ".html");
		cArray.add(cObj);
		crumbArr.add(cArray);
		addCrumbByRegion(srcObject, crumbArr, type);
		addCrumbByGenres(srcObject, crumbArr, type);
		return crumbArr;
	}

	private void addCrumbByRegion(JSONObject srcObject, JSONArray crumbArr, String type) {
		JSONArray regionArr = srcObject.getJSONArray("regions");
		if (regionArr != null) {
			JSONArray groupArray = new JSONArray();
			groupArray.add(regionArr.get(0));
			JSONArray rArray = new JSONArray();
			Map<String, String> regionMap = queryKeyValMapByRegions(groupArray);
			String regionLink = "/" + type + "/region/";
			for (int i = groupArray.size() - 1; i >= 0; i--) {
				String sRegion = groupArray.getString(i);
				if (StringUtils.isBlank(sRegion)) {
					continue;
				}
				sRegion = sRegion.trim();
				String sPyRegion = regionMap.get(sRegion);
				JSONObject gObj = new JSONObject();
				gObj.put("name", sRegion);
				gObj.put("pyVal", sPyRegion);
				gObj.put("link", regionLink + sPyRegion + ".html");
				rArray.add(gObj);
			}
			crumbArr.add(rArray);

			if (regionArr.size() > 1) {
				regionArr.remove(0);
				srcObject.put("regions", regionArr);
			}
		}

	}

	private void addCrumbByGenres(JSONObject srcObject, JSONArray crumbArr, String type) {
		JSONArray regionArr = crumbArr.getJSONArray(crumbArr.size() - 1);
		String sRegionPy = null;
		if (!regionArr.isEmpty()) {
			JSONObject oRegion = regionArr.getJSONObject(0);
			sRegionPy = oRegion.getString("pyVal");
		}
		JSONArray genreArr = srcObject.getJSONArray("genres");
		if (genreArr != null) {
			JSONArray gArray = new JSONArray();
			Map<String, String> kvMap = queryKeyValMapByGeners(genreArr);
			String genreLink = "/" + type + "/genre/";
			for (int i = 0, size = genreArr.size(); i < size; i++) {
				String sGenre = genreArr.getString(i);
				if (StringUtils.isBlank(sGenre)) {
					continue;
				}
				sGenre = sGenre.trim();
				String sPyGenre = kvMap.get(sGenre);
				if (sPyGenre == null) {
					continue;
				}
				if (StringUtils.isNotBlank(sRegionPy)) {
					sPyGenre += "-" + sRegionPy;
				}
				JSONObject gObj = new JSONObject();
				gObj.put("name", sGenre);
				gObj.put("link", genreLink + sPyGenre + ".html");
				gArray.add(gObj);
			}
			crumbArr.add(gArray);
		}

	}

	private Map<String, String> queryKeyValMapByRegions(JSONArray regionArr) {
		String sHead = "(type:idober-group-region AND (";
		StringBuilder sb = new StringBuilder();
		sb.append(sHead);
		for (int i = 0, size = regionArr.size(); i < size; i++) {
			String sKeyWord = regionArr.getString(i);
			if (StringUtils.isBlank(sKeyWord)) {
				continue;
			}
			sKeyWord = sKeyWord.trim();
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sb.append("title:");
			sb.append(sKeyWord);
		}
		sb.append("))");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.set("q", sb.toString());
		solrQuery.addField("title,short_s");
		Map<String, String> kvMap = Maps.newHashMap();
		try {
			QueryResponse resp = SolrUtils.getSolrWithMeta().query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			for (SolrDocument doc : docList) {
				String cnVal = doc.getFieldValue("title").toString();
				String pyVal = doc.getFieldValue("short_s").toString();
				kvMap.put(cnVal, pyVal);
			}
		} catch (Exception e) {
			log.warn("query region:" + regionArr.toJSONString() + ",cause:", e);
		}
		return kvMap;
	}

	private Map<String, String> queryKeyValMapByGeners(JSONArray genreArr) {
		return queryKeyValMap("idober-kv-genres", genreArr);
	}

	private Map<String, String> queryKeyValMap(String type, JSONArray cnArray) {
		String sHead = "(type:" + type + " AND (";
		StringBuilder sb = new StringBuilder();
		sb.append(sHead);
		for (int i = 0, size = cnArray.size(); i < size; i++) {
			String sGenre = cnArray.getString(i);
			if (StringUtils.isBlank(sGenre)) {
				continue;
			}
			sGenre = sGenre.trim();
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sb.append("cn_s:");
			sb.append(sGenre);
		}
		sb.append("))");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.set("q", sb.toString());
		solrQuery.addField("cn_s,py_s");
		Map<String, String> kvMap = Maps.newHashMap();
		try {
			QueryResponse resp = SolrUtils.getSolrWithMeta().query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			for (SolrDocument doc : docList) {
				String cnVal = doc.getFieldValue("cn_s").toString();
				String pyVal = doc.getFieldValue("py_s").toString();
				kvMap.put(cnVal, pyVal);
			}
		} catch (Exception e) {
			log.warn("query type:" + type + ",cause:", e);
		}
		return kvMap;
	}

}
