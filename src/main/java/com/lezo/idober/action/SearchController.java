package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.service.SearchActionService;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Controller
@RequestMapping("search")
public class SearchController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(SearchController.class);
	private ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);
	private ProductService productService = SpringBeanUtils.getBean(ProductService.class);
	private SearchActionService searchActionService = SpringBeanUtils.getBean(SearchActionService.class);

	@RequestMapping("build/{keyWord}")
	public String buildSearch(@ModelAttribute("model") ModelMap model, @PathVariable String keyWord, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "10") Integer pageSize) {
		long start = System.currentTimeMillis();
		Long searchId = 0L;
		try {
			searchId = searchActionService.buildSearch(keyWord, curPage, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("search:%s,page:%s,cause:", keyWord, curPage);
			logger.warn(msg, e);
		}
		model.addAttribute("sid", searchId);
		long cost = System.currentTimeMillis() - start;
		logger.info("search:{},page:{},searchId:{},cost:{}", keyWord, curPage, searchId, cost);
		return "searchList";
	}

	@RequestMapping("query/{searchId}")
	@ResponseBody
	public String getSearchResult(@ModelAttribute("model") ModelMap model, @PathVariable Long searchId) {
		long start = System.currentTimeMillis();
		long cost = System.currentTimeMillis() - start;
		return searchActionService.getSearchResult(searchId);
	}
}