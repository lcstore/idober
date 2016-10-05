package com.lezo.idober.action.movie;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("movie/album")
public class MovieAlbumController extends BaseController {

	@RequestMapping(value = { "{albumId}.html" }, method = RequestMethod.GET)
	public ModelAndView albumDetail(@PathVariable("albumId") String albumId, ModelMap model, HttpServletRequest request)
			throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.set("q", "(id:" + albumId + ")");
		solrQuery.addFilterQuery("type:album");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ARTICLE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		SolrDocument doc = null;
		if (CollectionUtils.isNotEmpty(docList)) {
			doc = docList.get(0);
		}
		if (doc == null) {
			throw new NotFoundException();
		}
		JSONObject srcObject = new JSONObject(doc);
		model.addAttribute("oDoc", srcObject);
		ModelAndView andView = new ModelAndView("MovieAlbum");
		return andView;
	}

}
