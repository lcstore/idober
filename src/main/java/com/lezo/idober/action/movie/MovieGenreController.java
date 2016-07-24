package com.lezo.idober.action.movie;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.ParamUtils;

@Controller
@RequestMapping("movie")
public class MovieGenreController extends BaseController {
	@RequestMapping(value = { "genre" }, method = RequestMethod.GET)
	public ModelAndView listGenres(ModelMap model, HttpServletRequest request) throws Exception {
		return listGenres("zhongguo", 1, model, request);
	}

	@RequestMapping(value = { "genre/{name}" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, ModelMap model, HttpServletRequest request)
			throws Exception {
		return listGenres(sGenre, 1, model, request);
	}

	@RequestMapping(value = { "genre/{name}/{curPage}" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, @PathVariable("curPage") Integer curPage,
			ModelMap model, HttpServletRequest request) throws Exception {
		sGenre = ParamUtils.xssClean(sGenre);
		curPage = ParamUtils.inRange(curPage);
		return new ModelAndView("MovieSearch");
	}

}
