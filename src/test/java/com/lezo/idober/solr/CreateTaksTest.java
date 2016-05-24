package com.lezo.idober.solr;

import lombok.extern.log4j.Log4j;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Log4j
public class CreateTaksTest {

    @Test
    public void testCreate() throws Exception {
        String url = "http://www.lezomao.com:8090/taskmgr/createtasks";
        String referrer = "http://www.lezomao.com";
        JSONArray taskArray = new JSONArray();
        JSONObject tObject = new JSONObject();
        tObject.put("type", "mtime-movie-torrent21");
        tObject.put("level", 1000);
        tObject.put("url", "url");
        taskArray.add(tObject);
        Response resp =
                Jsoup.connect(url).referrer(referrer).method(Method.POST).data("tasks", taskArray.toJSONString())
                        .ignoreContentType(true).execute();
        log.info("resp:" + resp.body());
    }
}
