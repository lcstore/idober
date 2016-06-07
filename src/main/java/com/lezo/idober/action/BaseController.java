package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.lezo.idober.security.XssParamEditor;

@Controller
public class BaseController {

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, new XssParamEditor());
    }
}