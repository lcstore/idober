package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.ParamUtils;

@Controller
@RequestMapping("movie/genre")
public class MovieGenreController extends BaseController {

	@RequestMapping(value = { "{name}", "{name}/{curPage}" },
			method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, @RequestParam(name = "curPage",
			defaultValue = "1") Integer curPage) throws Exception {
		sGenre = ParamUtils.xssClean(sGenre);
		curPage = ParamUtils.inRange(curPage);
		return new ModelAndView("MovieSearch");
	}

}
