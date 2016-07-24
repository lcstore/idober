package com.lezo.idober.action.movie;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.action.BaseController;

@Log4j
@Controller
public class MovieImageController extends BaseController {
    private static final int IMG_PREFIX_LEN = "/img".length();

    @ResponseBody
    @RequestMapping(value = { "/img/**" }, method = RequestMethod.GET)
    public ResponseEntity<byte[]> downImage(HttpServletRequest request) throws Exception {
        String url = request.getRequestURI();
        // http://img31.mtime.cn/mg/2014/08/02/105113.40980738_270X405X4.jpg
        url = url.substring(IMG_PREFIX_LEN);
        if (url.startsWith("/mt/2011/")) {
            url = "http://img21.mtime.cn" + url;
        } else {
            url = "http://img31.mtime.cn" + url;
        }
        Connection conn = Jsoup.connect(url);
        Connection.Method method = Method.GET;
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:46.0) Gecko/20100101 Firefox/46.0";
        String referrer = "http://movie.mtime.com/" + (new Random().nextInt(60000) + 1) + "/";
        Response resp =
                conn.method(method).timeout(20000).userAgent(userAgent).referrer(referrer).ignoreContentType(true)
                        .execute();

        log.info(resp.statusCode() + ",url:" + url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/jpeg");
        byte[] dataBytes = resp.bodyAsBytes();
        return new ResponseEntity<byte[]>(dataBytes, headers, HttpStatus.OK);
    }
}
