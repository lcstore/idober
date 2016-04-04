package com.lezo.idober.solr;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;

public class TaskmgrTest {

    @Test
    public void test() throws Exception {
        String title = "夺命枪火";
        String url = "http://www.lezomao.com:8090/moviemgr/fetch?title=" + title;
        String referrer = "http://www.lezomao.com";
        Response resp =
                Jsoup.connect(url).referrer(referrer).method(Method.GET)
                        .ignoreContentType(true).execute();
        String body = resp.body();
        System.err.println("body:" + body);
    }
}
