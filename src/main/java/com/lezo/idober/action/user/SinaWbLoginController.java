package com.lezo.idober.action.user;

import java.net.URLDecoder;
import java.util.Date;
import java.util.Locale;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.cacher.ExpireCacher;
import com.lezo.idober.config.AppConfig;

@Controller
@RequestMapping("oauth2.0")
@Log4j
public class SinaWbLoginController {
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_UID = "uid";
	private static final String VAL_TOKEN_URL =
			"https://api.weibo.com/oauth2/access_token?client_id=[APP_KEY]&client_secret=[APP_SECRET]&grant_type=authorization_code&code=[CODE]&redirect_uri=[REDIRECT_URI]";

	@Autowired
	private AppConfig config;

	@RequestMapping(value = { "wbLogin" }, method = RequestMethod.GET)
	public ModelAndView doLogin(@RequestParam("code") String code,
			@RequestParam(name = "state", required = false) String state,
			@CookieValue(name = "retTo", defaultValue = "/") String retTo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("wbLogin ....path:" + request.getRequestURI() + ",code:" + code + ",state:" + state);
		JSONObject tObject = accessToken(request, code, state);
		if (tObject != null) {
			String token = tObject.getString(KEY_ACCESS_TOKEN);
			String userId = tObject.getString(KEY_UID);
			Cookie cookie = new Cookie("retTo", "");
			cookie.setPath("/");
			response.addCookie(cookie);
			if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(token)) {
				String sKey = "loginWB:" + token;
				long expireMills = System.currentTimeMillis() + 60000;
				ExpireCacher.getInstance().putValue(sKey, 1, expireMills);

				String expires = tObject.getString(KEY_EXPIRES_IN);
				int expireNum = NumberUtils.toInt(expires, 86400);
				Date destDate = DateUtils.addSeconds(new Date(), expireNum);
				String format = DateFormatUtils.format(destDate, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
				String sVal = userId + ":" + token;
				response.addHeader("Set-Cookie", "__wb__k=" + sVal + ";Path=/;Expires=" + format);
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
		ModelAndView modelAndView = new ModelAndView("LoginHome");
		return modelAndView;
	}

	private JSONObject accessToken(HttpServletRequest request, String code, String state) throws Exception {
		// https://api.weibo.com/oauth2/access_token?client_id=942886765&client_secret=daadef913df628ef1d4b488aee1ac3a6&grant_type=authorization_code&code=2d69a4b9f4b48d8da1a86432c42b833d&redirect_uri=http://www.lezomao.com/
		String sTokenUrl = VAL_TOKEN_URL;
		sTokenUrl = sTokenUrl.replace("[APP_KEY]", config.getWbAppKey());
		sTokenUrl = sTokenUrl.replace("[APP_SECRET]", config.getWbAppSecret());
		sTokenUrl = sTokenUrl.replace("[REDIRECT_URI]", config.getWbRedirectUrl());
		sTokenUrl = sTokenUrl.replace("[CODE]", code);

		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		}
		JSONObject tObject = null;
		try {
			Response resp = Jsoup.connect(sTokenUrl).ignoreContentType(true)
					.userAgent(userAgent).method(Method.POST).execute();
			tObject = JSON.parseObject(resp.body());
		} catch (Exception e) {
			log.warn("request wbToken,cause:", e);
		}
		return tObject;
	}

	@RequestMapping(value = { "wbLogout" }, method = RequestMethod.GET)
	public ModelAndView doLogout(@RequestParam("code") String code, @RequestParam("state") String state,
			@CookieValue(name = "retTo", required = false) String retTo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("wbLogout ....path:" + request.getRequestURI() + ",code:" + code + ",state:" + state);
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