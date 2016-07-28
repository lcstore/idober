package com.lezo.idober.action.movie;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.GenreUtils;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.RegionUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie")
public class MovieGenreController extends BaseController {

	@RequestMapping(value = { "genre/{name}.html" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, ModelMap model, HttpServletRequest request)
			throws Exception {
		return listGenres(sGenre, 1, model, request);
	}

	@RequestMapping(value = { "genre/{name}/{curPage}.html" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, @PathVariable("curPage") Integer curPage,
			ModelMap model, HttpServletRequest request) throws Exception {
		sGenre = ParamUtils.xssClean(sGenre);
		curPage = ParamUtils.inRange(curPage);
		String[] genreArr = sGenre.split("-");
		sGenre = genreArr[0].trim();
		String sCNGenre = GenreUtils.toCNGenre(sGenre);
		if (sCNGenre == null) {
			sCNGenre = GenreUtils.DEFAULT_GENRE_CN;
			sGenre = GenreUtils.DEFAULT_GENRE;
		}

		String sRegion = "";
		if (genreArr.length > 1) {
			sRegion = genreArr[1].trim();
		}
		String sCNRegion = RegionUtils.toCNRegion(sRegion);
		if (sCNRegion == null) {
			sCNRegion = RegionUtils.DEFAULT_REGION_CN;
			sRegion = RegionUtils.DEFAULT_REGION;
		}

		int start = (curPage - 1) * ParamUtils.PAGE_SIZE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(start);
		solrQuery.setRows(ParamUtils.PAGE_SIZE);
		solrQuery.addField("image,id,name,rate");
		solrQuery.set("q", "genres:" + sCNGenre);
		solrQuery.addFilterQuery("regions:" + sCNRegion);
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");
		solrQuery.addSort("release", ORDER.desc);

		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		long total = docList.getNumFound();
		long totalPage = total / ParamUtils.PAGE_SIZE;
		totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);

		String sPath = request.getPathInfo();
		sPath = sPath.replaceAll("[0-9/]*\\.html$", "");

		model.addAttribute("oDocList", docList);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("curPath", sPath);

		addCrumbs(model, sGenre, sRegion, sCNGenre, sCNRegion);
		addHighStarDoc(model, sCNGenre, sCNRegion, 10);

		return new ModelAndView("MovieGenre");
	}

	private void addCrumbs(ModelMap model, String sGenre, String sRegion, String sCNGenre, String sCNRegion) {
		List<JSONObject> crumbList = Lists.newArrayList();
		JSONObject oCrumbObj = new JSONObject();
		oCrumbObj.put("title", "电影");
		oCrumbObj.put("link", "/movie/page/");
		crumbList.add(oCrumbObj);
		oCrumbObj = new JSONObject();
		oCrumbObj.put("title", sCNRegion);
		oCrumbObj.put("link", "/movie/region/" + sRegion);
		crumbList.add(oCrumbObj);
		oCrumbObj = new JSONObject();
		oCrumbObj.put("title", sCNGenre);
		oCrumbObj.put("link", "/movie/genre/" + sGenre + "-" + sRegion);
		crumbList.add(oCrumbObj);
		model.addAttribute("oCrumbList", crumbList);
	}

	private void addHighStarDoc(ModelMap model, String sGenre, String sRegion, int limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.addField("cover,id,name,rate");
		solrQuery.set("q", "genres:" + sGenre);
		solrQuery.addFilterQuery("regions:" + sRegion);
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addSort("year", ORDER.desc);
		solrQuery.addSort("star", ORDER.desc);
		solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");

		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();

		model.addAttribute("oStarList", docList);
	}

}
