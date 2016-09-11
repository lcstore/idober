package com.lezo.idober.action.movie;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.SolrConstant;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("search")
public class MovieEditSearchController extends BaseController {

	@RequestMapping("movie/edit")
	public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
			@RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
			throws Exception {
		pageSize = pageSize < 12 ? 12 : pageSize;
		int offset = (curPage - 1) * pageSize;
		int limit = pageSize;
		offset = offset < 0 ? 0 : offset;
		keyWord = keyWord.trim();
		SolrDocumentList documentList = queryDocByWord(keyWord, offset, limit);
		model.addAttribute("qAction", "/search/movie/edit");
		model.addAttribute("qWord", keyWord);
		model.addAttribute("qResponse", documentList);
		return new ModelAndView("MovieEditSearch");
	}

	private SolrDocumentList queryDocByWord(String keyWord, int offset, int limit) throws Exception {
		if (StringUtils.isBlank(keyWord)) {
			return new SolrDocumentList();
		}
		keyWord = ClientUtils.escapeQueryChars(keyWord);
		SolrQuery solrQuery = new SolrQuery(SolrConstant.SORL_QUERY_DEFAULT_FRANGE);
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("qq", keyWord);
		solrQuery.addFilterQuery("type:movie");
		solrQuery.addSort("release", ORDER.desc);
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
		return resp.getResults();
	}
}
