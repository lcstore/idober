package com.lezo.idober.action.movie.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.action.movie.MovieSearchController;

@Controller
@RequestMapping("m/search")
public class MobileMovieSearchController extends MovieSearchController {

	@RequestMapping("movie")
	public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
			@RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
			throws Exception {
		ModelAndView modelAndView = super.buildSearch(model, keyWord, curPage, pageSize);
		modelAndView.setViewName("mobile/" + modelAndView.getViewName());
		return modelAndView;
	}

}
