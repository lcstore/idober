package com.lezo.idober.action;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("movie")
public class MovieDownController extends BaseController {

    @RequestMapping("download")
    public ResponseEntity<byte[]> download(@RequestParam(value = "u", required = false) String url,
            @RequestParam("p") String params,
            @RequestParam(value = "n", required = false) String sName,
            @RequestParam(value = "m", defaultValue = "GET") String sMethod)
            throws IOException {
        if (StringUtils.isBlank(url)) {
            url = "http://www.bttiantang.com/download3.php";
        }
        Connection conn = Jsoup.connect(url);
        Connection.Method method = Method.GET;
        if ("POST".equals(sMethod.toUpperCase())) {
            method = Method.POST;
            if (StringUtils.isNotEmpty(params)) {
                String[] paramArr = params.split("&");
                for (String param : paramArr) {
                    String[] kvArr = param.split("=");
                    int index = -1;
                    String key = kvArr[++index];
                    String value = kvArr[++index];
                    conn.data(key, value);
                }
            }
        } else {
            url += url.contains("?") ? params : "?" + params;
        }
        Response resp = conn.method(method).ignoreContentType(true).execute();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String disKey = "Content-Disposition";
        String attach = resp.header(disKey);
        if (StringUtils.isNotBlank(sName)) {
            sName = sName.endsWith(".torrent") ? sName : sName + ".torrent";
            headers.setContentDispositionFormData("attachment", sName);
        } else if (StringUtils.isNotBlank(attach)) {
            headers.set(disKey, attach);
        } else {
            headers.setContentDispositionFormData("attachment", System.currentTimeMillis() + ".torrent");
        }
        byte[] dataBytes = resp.bodyAsBytes();
        return new ResponseEntity<byte[]>(dataBytes, headers, HttpStatus.OK);
    }
}
