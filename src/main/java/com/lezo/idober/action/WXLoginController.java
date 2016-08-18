package com.lezo.idober.action;

import java.net.URLDecoder;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.collect.Maps;
import com.lezo.idober.config.AppConfig;

@Controller
@RequestMapping("oauth2.0")
@Log4j
public class WXLoginController {
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String VAL_REDIRECT_URI = "http%3A%2F%2Fwww.lezomao.com%2Foauth2.0%2FqqLogin2";
    private static final String VAL_TOKEN_URL =
            "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=[YOUR_APP_ID]&client_secret=[YOUR_APP_Key]&code=[The_AUTHORIZATION_CODE]&state=[The_CLIENT_STATE]&redirect_uri="
                    + VAL_REDIRECT_URI;
    @Autowired
    private AppConfig config;

    @RequestMapping(value = { "wxLogin" }, method = RequestMethod.GET)
    public ModelAndView doLogin(@RequestParam("code") String code, @RequestParam("state") String state,
            @CookieValue(name = "retTo", required = false) String retTo,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String sTokenUrl = VAL_TOKEN_URL;
        sTokenUrl = sTokenUrl.replace("[YOUR_APP_ID]", config.getQqConnectAppid());
        sTokenUrl = sTokenUrl.replace("[YOUR_APP_Key]", config.getQqConnectAppkey());
        sTokenUrl = sTokenUrl.replace("[The_AUTHORIZATION_CODE]", code);
        sTokenUrl = sTokenUrl.replace("[The_CLIENT_STATE]", state);
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
        }
        Response resp =
                Jsoup.connect(sTokenUrl).ignoreContentType(true).userAgent(userAgent).method(Method.GET).execute();
        String sPath = resp.body();
        int index = sPath.lastIndexOf(KEY_ACCESS_TOKEN);

        if (index >= 0) {
            String sParam = sPath.substring(index);
            String[] paramArr = sParam.split("&");
            Map<String, String> paramMap = Maps.newHashMap();
            for (String param : paramArr) {
                String[] kvArr = param.split("=");
                if (kvArr.length == 2) {
                    String key = kvArr[0].trim();
                    String value = kvArr[1].trim();
                    paramMap.put(key, value);
                }
            }
            String token = paramMap.get(KEY_ACCESS_TOKEN);
            String expires = paramMap.get(KEY_EXPIRES_IN);
            String sVal = "TC_MK=" + token;
            int expireNum = NumberUtils.toInt(expires, 86400);
            log.info("code:" + code + ",source:" + sPath + ",userAgent:" + userAgent);
            Date destDate = DateUtils.addSeconds(new Date(), expireNum);
            String format = DateFormatUtils.format(destDate, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
            Cookie cookie = new Cookie("retTo", "");
            cookie.setPath("/");
            response.addCookie(cookie);
            // response.addCookie(cookie);//=作为特殊字符,会添加双引号__qc__k="TC_MK=03DC873FB5948E1373C392B9FFB8C928";
            response.addHeader("Set-Cookie", "__qc__k=" + sVal + ";Path=/;Expires=" + format);
        } else {
            log.warn("code:" + code + ",source:" + sPath + ",userAgent:" + userAgent);
        }
        if (StringUtils.isBlank(retTo)) {
            retTo = "/";
        } else {
            retTo = URLDecoder.decode(retTo, "UTF-8");
        }
        RedirectView red = new RedirectView(retTo, true);
        red.setStatusCode(HttpStatus.FOUND);
        return new ModelAndView(red);
    }
}