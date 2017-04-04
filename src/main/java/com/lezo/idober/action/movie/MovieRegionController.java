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
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.RegionUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie")
public class MovieRegionController extends BaseController {

	@RequestMapping(value = { "region/{name}.html" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion, ModelMap model, HttpServletRequest request)
			throws Exception {
		return listRegions(sRegion, 1, model, request);
	}

	@RequestMapping(value = { "region/{name}/{curPage}.html" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion,
			@PathVariable("curPage") Integer curPage, ModelMap model, HttpServletRequest request)
			throws Exception {
		sRegion = ParamUtils.xssClean(sRegion);
		curPage = ParamUtils.inRange(curPage);
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
		solrQuery.set("q", "regions:" + sCNRegion);
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");
		solrQuery.addSort("release", ORDER.desc);

		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		DocUtils.changeImage(resp.getResults());
		long total = docList.getNumFound();
		long totalPage = total / ParamUtils.PAGE_SIZE;
		totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);

		String sPath = request.getPathInfo();
		sPath = sPath.replaceAll("[0-9/]*\\.html$", "");

		model.addAttribute("oDocList", docList);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("curPath", sPath);
		model.addAttribute("cnRegion", sCNRegion);
		addHighStarDoc(model, sCNRegion, 10);
		addCrumbs(model, sRegion, sCNRegion);
		return new ModelAndView("MovieRegion");
	}

	private void addCrumbs(ModelMap model, String sRegion, String sCNRegion) {
		List<JSONObject> crumbList = Lists.newArrayList();
		JSONObject oCrumbObj = new JSONObject();
		oCrumbObj.put("title", "电影");
		oCrumbObj.put("link", "/movie.html");
		crumbList.add(oCrumbObj);
		oCrumbObj = new JSONObject();
		oCrumbObj.put("title", sCNRegion);
		oCrumbObj.put("link", "/movie/region/" + sRegion + ".html");
		crumbList.add(oCrumbObj);
		model.addAttribute("oCrumbList", crumbList);
	}

	private void addHighStarDoc(ModelMap model, String sRegion, int limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.addField("cover,id,name,rate");
		solrQuery.set("q", "regions:" + sRegion);
		solrQuery.addSort("year", ORDER.desc);
		solrQuery.addSort("star", ORDER.desc);
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");

		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();

		model.addAttribute("oStarList", docList);
	}

}
