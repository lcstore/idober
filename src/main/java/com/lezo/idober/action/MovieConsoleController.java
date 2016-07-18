package com.lezo.idober.action;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie/console")
public class MovieConsoleController extends BaseController {

	@RequestMapping(value = { "source/{pageNum}", "/" },
			method = RequestMethod.GET)
	public ModelAndView listTypes(@PathVariable("pageNum") Integer curPage, ModelMap model) throws Exception {
		curPage = (curPage == null || curPage < 1) ? 1 : curPage;
		curPage = ParamUtils.inRange(curPage);
		int start = (curPage - 1) * ParamUtils.PAGE_SIZE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(start);
		solrQuery.setRows(ParamUtils.PAGE_SIZE);
		solrQuery.addField("image,id,name,rate");
		solrQuery.set("q", "*:*");
		// solrQuery.addFilterQuery("");
		QueryResponse resp = SolrUtils.getSolrWithMovie().query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		long total = docList.getNumFound();
		long totalPage = total / ParamUtils.PAGE_SIZE;
		totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);
		model.addAttribute("oDocList", docList);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPage", totalPage);
		return new ModelAndView("MovieConsole");
	}

}
