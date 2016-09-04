package com.lezo.idober.action.movie.mobile;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.UnifyMovieHomeController;

@Log4j
@Controller
public class MobileMovieHomeController extends UnifyMovieHomeController {

	@Override
	@RequestMapping(value = { "m/", "m/movie/" }, method = RequestMethod.GET)
	public ModelAndView loadHome(ModelMap model) throws Exception {
		ModelAndView modelAndView = super.loadHome(model);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

}
