package com.lezo.idober.action;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie")
public class MovieListController extends BaseController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(MovieListController.class);
	public static final int PAGE_SIZE = 20;
	public static final int MIN_PAGE_NUM = 1;
	public static final int MAX_PAGE_NUM = 100;

	@RequestMapping(value = { "list/{pageNum}", "list" },
			method = RequestMethod.GET)
	public ModelAndView listTypes(@PathVariable("pageNum") Integer pageNum, ModelMap model) throws Exception {
		pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;

		pageNum = Math.min(pageNum, MAX_PAGE_NUM);
		int start = (pageNum - 1) * PAGE_SIZE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(start);
		solrQuery.setRows(PAGE_SIZE);
		solrQuery.addField("image,id,name,rate");
		solrQuery.set("q", "*:*");
		// solrQuery.addFilterQuery("");
		QueryResponse resp = SolrUtils.getSolrWithMovie().query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		model.addAttribute("oDocList", docList);
		return new ModelAndView("MovieList");
	}

}
