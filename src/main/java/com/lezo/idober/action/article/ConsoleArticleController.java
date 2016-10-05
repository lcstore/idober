package com.lezo.idober.action.article;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.log4j.Log4j;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.SolrUtils;

@Log4j
@Controller
@RequestMapping("console/article")
public class ConsoleArticleController extends BaseController {

	@ResponseBody
	@RequestMapping(value = { "save.action" }, method = RequestMethod.POST)
	public JSONObject saveArticle(@RequestBody JSONObject paramObj, ModelMap model) throws Exception {
		log.info("paramObj:" + paramObj);
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_ARTICLE);
		SolrInputDocument inDoc = new SolrInputDocument();
		for (Entry<String, Object> entry : paramObj.entrySet()) {
			inDoc.setField(entry.getKey(), entry.getValue());
		}

		addArticleId(paramObj, inDoc);
		Map<String, Object> fieldModifier = Maps.newHashMap();
		fieldModifier.put("add", new Date());
		inDoc.setField("creation", fieldModifier);
		server.add(inDoc);
		UpdateResponse resp = server.commit();
		JSONObject rsObject = new JSONObject();
		rsObject.put("status", resp.getStatus());
		rsObject.put("qtime", resp.getQTime());
		return rsObject;
	}

	private void addArticleId(JSONObject paramObj, SolrInputDocument inDoc) {
		String title = paramObj.getString("title");
		String source = paramObj.getString("source");
		String author = paramObj.getString("author");
		String type = paramObj.getString("type");
		title = title == null ? "" : title.trim();
		source = source == null ? "" : source.trim();
		author = author == null ? "" : author.trim();
		type = type == null ? "" : type.trim();
		String sIdString = type + SolrUtils.VALUE_SPLITOR + title + SolrUtils.VALUE_SPLITOR + source
				+ SolrUtils.VALUE_SPLITOR + author;
		String sVal = sIdString.toLowerCase(Locale.ROOT);
		sVal = "" + sVal.hashCode();
		sVal = sVal.replace("-", "0");
		inDoc.setField("id", sVal);
	}

	@RequestMapping(value = { "new.html" }, method = RequestMethod.GET)
	public ModelAndView newArticle(ModelMap model)
			throws Exception {
		ModelAndView andView = new ModelAndView("article/ArticleNew");
		return andView;
	}

}
