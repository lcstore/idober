package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @RequestMapping(value = { "login" }, method = RequestMethod.GET)
    public String toLogin(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
            ModelMap model) throws Exception {
        return "LoginHome";
    }

    @RequestMapping(value = { "login/loginQQ" }, method = RequestMethod.POST)
    public String loginQQ(String openId, String accessToken, ModelMap model) throws Exception {
        return null;
    }
}