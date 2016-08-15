package com.lezo.idober.action.movie;

import java.util.ArrayList;
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
import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
public class MovieListController extends BaseController {

	private static final List<JSONObject> CRUMB_LIST = new ArrayList<JSONObject>(1);
	static {
		JSONObject oCrumbObj = new JSONObject();
		oCrumbObj.put("title", "电影");
		oCrumbObj.put("link", "/movie.html");
		CRUMB_LIST.add(oCrumbObj);
	}

	@RequestMapping(value = { "movie.html" }, method = RequestMethod.GET)
	public ModelAndView listFirstMovie(ModelMap model, HttpServletRequest request) throws Exception {
		return listMovie(1, model, request);
	}

	@RequestMapping(value = { "movie/{pageNum}.html" }, method = RequestMethod.GET)
	public ModelAndView listMovie(@PathVariable("pageNum") Integer curPage, ModelMap model, HttpServletRequest request)
			throws Exception {
		curPage = (curPage == null || curPage < 1) ? 1 : curPage;
		curPage = ParamUtils.inRange(curPage);
		int start = (curPage - 1) * ParamUtils.PAGE_SIZE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(start);
		solrQuery.setRows(ParamUtils.PAGE_SIZE);
		solrQuery.addField("image,id,name,rate");
		solrQuery.set("q", "*:*");
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
		model.addAttribute("oCrumbList", CRUMB_LIST);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("curPath", sPath);

		addHighStarDoc(model, 10);
		return new ModelAndView("MovieList");
	}

	private void addHighStarDoc(ModelMap model, int limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(limit);
		solrQuery.addField("cover,id,name,rate");
		solrQuery.set("q", "*:*");
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addSort("release", ORDER.desc);
		solrQuery.addSort("comment", ORDER.desc);
		solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");

		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();

		model.addAttribute("oStarList", docList);
	}

}
