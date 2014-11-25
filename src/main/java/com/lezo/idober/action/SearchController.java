package com.lezo.idober.action;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.service.SearchActionService;
import com.lezo.idober.vo.ActionReturnVo;
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

	@RequestMapping("build")
	public String buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize) {
		long start = System.currentTimeMillis();
		Long searchId = 0L;
		try {
			searchId = searchActionService.buildSearch(keyWord, curPage, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("search:%s,page:%s,cause:", keyWord, curPage);
			logger.warn(msg, e);
		}
		model.addAttribute("qWord", keyWord);
		model.addAttribute("sid", searchId);
		long cost = System.currentTimeMillis() - start;
		logger.info("search:{},page:{},searchId:{},cost:{}", keyWord, curPage, searchId, cost);
		return "searchList";
	}

	@RequestMapping("query")
	@ResponseBody
	public ActionReturnVo getSearchResult(@ModelAttribute("model") ModelMap model, @RequestParam(value = "sid") Long searchId) {
		long start = System.currentTimeMillis();
		long cost = System.currentTimeMillis() - start;
		ActionReturnVo vo = searchActionService.getSearchResult(searchId);
		// ObjectMapper mapper = new ObjectMapper();
		// StringWriter writer = new StringWriter();
		// mapper.writeValue(writer, vo);
		return vo;
	}
}