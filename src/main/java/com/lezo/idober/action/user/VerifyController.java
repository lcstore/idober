package com.lezo.idober.action.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("verify")
@Controller
public class VerifyController {

	@RequestMapping(value = { "email" }, method = RequestMethod.GET)
	public String emailVerify(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
			ModelMap model) throws Exception {
		return "LoginHome";
	}

	@RequestMapping(value = { "phone" }, method = RequestMethod.GET)
	public String phoneVerify(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
			ModelMap model) throws Exception {
		return "LoginHome";
	}

}