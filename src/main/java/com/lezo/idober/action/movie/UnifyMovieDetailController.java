package com.lezo.idober.action.movie;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
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
import com.lezo.idober.action.BaseController;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.utils.AESCodecUtils;
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("movie/detail")
@Controller
@Log4j
public class UnifyMovieDetailController extends BaseController {
	private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
	private static final String CORE_MOVIE = SolrUtils.CORE_ONLINE_MOVIE;

	@RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
	public ModelAndView loadDetail(@PathVariable("itemCode") String itemCode, ModelMap model) throws Exception {
		itemCode = Jsoup.clean(itemCode, Whitelist.basic());
		Matcher matcher = NUM_REG.matcher(itemCode);
		SolrDocument doc = null;
		if (!matcher.find()) {
			// doc = getDocumentByDecrypt(itemCode);
		} else {
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
		String oldCode = ObjectUtils.toString(doc.getFieldValue("old_id_s"), null);
		String idString = ObjectUtils.toString(doc.getFieldValue("id"), StringUtils.EMPTY);
		if (itemCode.equals(oldCode)) {
			RedirectView red = new RedirectView("/movie/detail/" + idString + ".html", true);
			red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			return new ModelAndView(red);
		}
		JSONObject dObject = convert2JSON(doc);
		model.addAttribute("oDoc", dObject);
		return new ModelAndView("MovieDetail");
	}

	private JSONObject convert2JSON(SolrDocument doc) {
		DocUtils.changeImage(doc);
		JSONObject srcObject = new JSONObject(doc);
		JSONArray crumbArr = createCrumbs(srcObject);
		srcObject.put("crumbs", crumbArr);
		// handleCopyright(srcObject);
		assortTorrents(srcObject);
		return srcObject;
	}

	private void assortTorrents(JSONObject srcObject) {
		JSONArray tArray = srcObject.getJSONArray("torrents");
		if (tArray == null) {
			return;
		}
		String sMark = "http://www.rarbt.commagnet:?xt";
		for (int i = 0; i < tArray.size(); i++) {
			String sTorrent = tArray.getString(i);
			String sNewTorrent = sTorrent.replace(sMark, "magnet:?xt");
			if (!sTorrent.equals(sNewTorrent)) {
				sNewTorrent = sNewTorrent.replace("rarbt-torrent", "magnet-link");
				tArray.set(i, sNewTorrent);
			}
		}
		srcObject.put("torrents", tArray);
	}

	private void handleCopyright(JSONObject srcObject) {
		Calendar calc = Calendar.getInstance();
		int hour = calc.get(Calendar.HOUR_OF_DAY);
		if (hour >= 21 || hour <= 6) {
			Random rand = new Random();
			if (rand.nextBoolean()) {
				String key = "copyright_s";
				Integer copyright = srcObject.getInteger(key);
				if (copyright != null && copyright.equals(0)) {
					srcObject.put(key, "1");
				}
			}
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

	public SolrDocument getDocumentByDecrypt(String itemCode) {
		SolrDocument document = null;
		try {
			String idString = AESCodecUtils.decrypt(itemCode);
			String[] unitArr = idString.split(";");
			if (unitArr.length >= 3) {
				int index = -1;
				String sYear = unitArr[++index];
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < unitArr.length - 1; i++) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(unitArr[i]);
				}
				String sDirector = sb.toString();
				String sName = unitArr[unitArr.length - 1];
				sDirector = ClientUtils.escapeQueryChars(sDirector);
				sName = ClientUtils.escapeQueryChars(sName);
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.setStart(0);
				solrQuery.setRows(1);
				solrQuery.set("q", "(year:" + sYear + " AND directors:" + sDirector + " AND names:" + sName + ")");
				QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
				SolrDocumentList docList = resp.getResults();
				if (CollectionUtils.isNotEmpty(docList)) {
					return docList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
}
