package com.lezo.idober.action.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.idober.config.AppConfig;

@Controller
public class LogoutController {
    @Autowired
    private AppConfig config;

    @RequestMapping(value = { "logout" }, method = RequestMethod.GET)
    public String toLogin(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
            ModelMap model) throws Exception {
        return "LoginHome";
    }
}