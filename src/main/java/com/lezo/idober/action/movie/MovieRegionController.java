package com.lezo.idober.action.movie;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.RegionUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie")
public class MovieRegionController extends BaseController {

	@RequestMapping(value = { "region" }, method = RequestMethod.GET)
	public ModelAndView listRegions(ModelMap model, HttpServletRequest request) throws Exception {
		return listRegions("zhongguo", 1, model, request);
	}

	@RequestMapping(value = { "region/{name}" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion, ModelMap model, HttpServletRequest request)
			throws Exception {
		return listRegions(sRegion, 1, model, request);
	}

	@RequestMapping(value = { "region/{name}/{curPage}" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion,
			@PathVariable("curPage") Integer curPage, ModelMap model, HttpServletRequest request)
			throws Exception {
		sRegion = ParamUtils.xssClean(sRegion);
		curPage = ParamUtils.inRange(curPage);
		String sGroup = RegionUtils.toCNRegionGroup(sRegion);
		int start = (curPage - 1) * ParamUtils.PAGE_SIZE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(start);
		solrQuery.setRows(ParamUtils.PAGE_SIZE);
		solrQuery.addField("image,id,name,rate");
		solrQuery.set("q", "regions:" + sGroup);

		String sPath = request.getPathInfo();
		sPath = sPath.replaceAll("/[0-9/]*$", "");
		// solrQuery.addFilterQuery("");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		long total = docList.getNumFound();
		long totalPage = total / ParamUtils.PAGE_SIZE;
		totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);
		model.addAttribute("oDocList", docList);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("curPath", sPath);
		return new ModelAndView("MovieRegion");
	}

}
