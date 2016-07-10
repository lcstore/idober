package com.lezo.idober.action;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("umovie/detail")
@Controller
@Log4j
public class UnifyMovieDetailController extends BaseController {
	private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
	private static final String CORE_MOVIE = "cmovie";

	@RequestMapping(value = "{itemCode}",
			method = RequestMethod.GET)
	public ModelAndView loadDetail(@PathVariable String itemCode, ModelMap model) throws Exception {
		// String idString = AESCodecUtils.decrypt(itemCode);
		itemCode = Jsoup.clean(itemCode, Whitelist.basic());
		Matcher matcher = NUM_REG.matcher(itemCode);
		if (!matcher.find()) {
			throw new NotFoundException();
		}
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.set("q", "(id:" + itemCode + " OR old_id_s:" + itemCode + ")");
		// String fields = SolrUtils.getSolrFields(UnifyMovieSolr.class);
		// solrQuery.addField(fields);
		QueryResponse resp = SolrUtils.getSolrServer(CORE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (CollectionUtils.isEmpty(docList)) {
			throw new NotFoundException();
		}
		SolrDocument doc = docList.get(0);
		String oldCode = ObjectUtils.toString(doc.getFieldValue("old_id_s"), null);
		String idString = ObjectUtils.toString(doc.getFieldValue("id"), StringUtils.EMPTY);
		if (!idString.equals(itemCode) && StringUtils.isNotBlank(oldCode)) {
			RedirectView red = new RedirectView("/movie/detail/" + idString, true);
			red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			return new ModelAndView(red);
		}
		JSONObject dObject = convert2JSON(doc);
		model.addAttribute("oDoc", dObject);
		return new ModelAndView("UMovieDetail");
	}

	private JSONObject convert2JSON(SolrDocument doc) {
		doc = SolrUtils.overwriteWithEditVal(doc);
		JSONObject srcObject = new JSONObject(doc);
		JSONArray crumbArr = createCrumbs(srcObject);
		srcObject.put("crumbs", crumbArr);
		assortTorrents(srcObject);
		return srcObject;
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
		cObj.put("link", "/" + type + "/list");
		cArray.add(cObj);
		crumbArr.add(cArray);
		addCrumbByGenres(srcObject, crumbArr, type);
		addCrumbByRegion(srcObject, crumbArr, type);
		return crumbArr;
	}

	private void addCrumbByRegion(JSONObject srcObject, JSONArray crumbArr, String type) {
		JSONArray regionArr = srcObject.getJSONArray("regions");
		if (regionArr != null) {
			JSONArray groupArray = new JSONArray();
			groupArray.add(regionArr.get(regionArr.size() - 1));
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
				gObj.put("link", regionLink + sPyRegion);
				rArray.add(gObj);
			}
			crumbArr.add(rArray);
		}

	}

	private void addCrumbByGenres(JSONObject srcObject, JSONArray crumbArr, String type) {
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
				JSONObject gObj = new JSONObject();
				gObj.put("name", sGenre);
				gObj.put("link", genreLink + sPyGenre);
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

	private void assortTorrents(JSONObject dObject) {
		String assortKey = "torrents";
		JSONArray tArray = dObject.getJSONArray(assortKey);
		if (tArray == null) {
			dObject.put(assortKey, new JSONArray());
			return;
		}
		JSONArray sArray = new JSONArray();
		JSONArray newArray = new JSONArray();
		for (int i = 0; i < tArray.size(); i++) {
			JSONObject tObject = JSONObject.parseObject(tArray.getString(i));
			String type = tObject.getString("type");
			String source = tObject.getString("source");
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
				newArray.add(tObject);
			} else if (type != null && type.endsWith("-share")) {
				sArray.add(tObject);
			} else if (type != null) {
				newArray.add(tObject);
			}
		}
		dObject.put(assortKey, newArray);
		dObject.put("shares", sArray);
	}
}
