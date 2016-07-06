package com.lezo.idober.action;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.ParamUtils;

@Controller
@RequestMapping("movie")
public class MovieAssortController extends BaseController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(MovieAssortController.class);

	@RequestMapping(value = "type/{name}", method = RequestMethod.GET)
	public ModelAndView listTypes(@PathVariable("name") String sortName,
			@RequestParam(defaultValue = "1") Integer curPage) throws Exception {
		sortName = ParamUtils.xssClean(sortName);
		curPage = ParamUtils.clampPageNum(curPage);
		return new ModelAndView("MovieSearch");
	}

	@RequestMapping(value = "genre/{name}", method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sortName,
			@RequestParam(defaultValue = "1") Integer curPage) throws Exception {
		sortName = ParamUtils.xssClean(sortName);
		curPage = ParamUtils.clampPageNum(curPage);
		return new ModelAndView("MovieSearch");
	}

	@RequestMapping(value = "region/{name}", method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sortName,
			@RequestParam(defaultValue = "1") Integer curPage) throws Exception {
		sortName = ParamUtils.xssClean(sortName);
		curPage = ParamUtils.clampPageNum(curPage);
		return new ModelAndView("MovieSearch");
	}


}
