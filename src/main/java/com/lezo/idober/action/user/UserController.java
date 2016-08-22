package com.lezo.idober.action.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("user")
@Controller
public class UserController {

	@RequestMapping(value = { "bind" }, method = RequestMethod.GET)
	public String bindUser(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
			ModelMap model) throws Exception {
		return "LoginHome";
	}

}