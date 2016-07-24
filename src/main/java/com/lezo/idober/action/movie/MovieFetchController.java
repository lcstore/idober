package com.lezo.idober.action.movie;

import java.io.IOException;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("movie")
public class MovieFetchController {

    @RequestMapping("fetch")
    @ResponseBody
    public String fetchMovie(@RequestParam(value = "title") String title)
            throws IOException {
        String url = "http://www.lezomao.com:8090/moviemgr/fetch?title=" + title;
        String referrer = "http://www.lezomao.com";
        Response resp =
                Jsoup.connect(url).referrer(referrer).method(Method.GET)
                        .ignoreContentType(true).execute();
        return resp.body();
    }
}
