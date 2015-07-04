package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("new")
public class NewHomeController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getCategoryPage(@ModelAttribute("model") ModelMap model) {
		System.err.println("hello ,,,,,,,");
		return "hello";
	}
}
