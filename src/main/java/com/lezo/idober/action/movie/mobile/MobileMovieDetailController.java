package com.lezo.idober.action.movie.mobile;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.UnifyMovieDetailController;

@Log4j
@Controller
@RequestMapping("m/movie/detail")
public class MobileMovieDetailController extends UnifyMovieDetailController {

	@Override
	@RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
	public ModelAndView loadDetail(@PathVariable("itemCode") String itemCode, ModelMap model) throws Exception {
		ModelAndView modelAndView = super.loadDetail(itemCode, model);
		if ("UMovieDetail".equals(modelAndView.getViewName())) {
			modelAndView.setViewName("mobile/MovieDetail");
		}
		return modelAndView;
	}

}
