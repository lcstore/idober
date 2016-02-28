package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("movie")
@Controller
public class MovieHomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHotMovie() throws Exception {
        return "MovieHot";
    }
}
