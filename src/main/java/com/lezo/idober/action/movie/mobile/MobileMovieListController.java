package com.lezo.idober.action.movie.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.MovieListController;

@Controller
@RequestMapping("m")
public class MobileMovieListController extends MovieListController {

	@RequestMapping(value = { "movie.html" }, method = RequestMethod.GET)
	public ModelAndView listFirstMovie(ModelMap model, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = super.listFirstMovie(model, request);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

	@RequestMapping(value = { "movie/{pageNum}.html" }, method = RequestMethod.GET)
	public ModelAndView listMovie(@PathVariable("pageNum") Integer curPage, ModelMap model, HttpServletRequest request)
			throws Exception {
		ModelAndView modelAndView = super.listMovie(curPage, model, request);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

}
