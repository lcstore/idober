package com.lezo.idober.action.movie.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.MovieGenreController;

@Controller
@RequestMapping("m/movie")
public class MobileMovieGenreController extends MovieGenreController {

	@RequestMapping(value = { "genre/{name}.html" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, ModelMap model, HttpServletRequest request)
			throws Exception {
		ModelAndView modelAndView = super.listGenres(sGenre, model, request);
		return modelAndView;
	}

	@RequestMapping(value = { "genre/{name}/{curPage}.html" }, method = RequestMethod.GET)
	public ModelAndView listGenres(@PathVariable("name") String sGenre, @PathVariable("curPage") Integer curPage,
			ModelMap model, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = super.listGenres(sGenre, curPage, model, request);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

}
