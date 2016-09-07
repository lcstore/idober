package com.lezo.idober.action.movie.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.MovieRegionController;

@Controller
@RequestMapping("m/movie")
public class MobileMovieRegionController extends MovieRegionController {

	@RequestMapping(value = { "region/{name}.html" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion, ModelMap model, HttpServletRequest request)
			throws Exception {
		ModelAndView modelAndView = super.listRegions(sRegion, model, request);
		return modelAndView;
	}

	@RequestMapping(value = { "region/{name}/{curPage}.html" }, method = RequestMethod.GET)
	public ModelAndView listRegions(@PathVariable("name") String sRegion,
			@PathVariable("curPage") Integer curPage, ModelMap model, HttpServletRequest request)
			throws Exception {
		ModelAndView modelAndView = super.listRegions(sRegion, curPage, model, request);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

}
