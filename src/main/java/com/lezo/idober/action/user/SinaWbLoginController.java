package com.lezo.idober.action.user;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.config.AppConfig;
import com.lezo.idober.service.UserKind;
import com.lezo.idober.service.UserService;

@Controller
@RequestMapping("oauth2.0")
@Log4j
public class SinaWbLoginController {
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_UID = "uid";
	private static final String VAL_TOKEN_URL =
			"https://api.weibo.com/oauth2/access_token?client_id=[APP_KEY]&client_secret=[APP_SECRET]&grant_type=authorization_code&code=[CODE]&redirect_uri=[REDIRECT_URI]";

	private static final int LOGIN_MAX_AGE = 24 * 60 * 60;
	@Autowired
	private AppConfig config;
	@Autowired
	private UserService userService;

	@RequestMapping(value = { "wbLogin" }, method = RequestMethod.GET)
	public ModelAndView doLogin(@RequestParam("code") String code,
			@RequestParam(name = "state", required = false) String state,
			@CookieValue(name = "retTo", defaultValue = "/") String retTo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			JSONObject tObject = accessToken(request, code, state);
			if (tObject == null) {
				throw new RuntimeException("code to token error");
			}
			String token = tObject.getString(KEY_ACCESS_TOKEN);
			String userId = tObject.getString(KEY_UID);
			if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
				throw new RuntimeException("token error");
			}
			addOrUpdateUser(userId, token, request, response);
			addCookies(tObject, response);

			if (StringUtils.isBlank(retTo)) {
				retTo = "/";
			} else {
				retTo = URLDecoder.decode(retTo, "UTF-8");
			}
			RedirectView red = new RedirectView(retTo, true);
			red.setStatusCode(HttpStatus.FOUND);
			return new ModelAndView(red);
		} catch (Exception ex) {
			log.warn("wbLogin,cause:", ex);
			RedirectView red = new RedirectView("/login", true);
			red.setStatusCode(HttpStatus.FOUND);
			return new ModelAndView(red);
		}
	}

	private void addCookies(JSONObject tObject, HttpServletResponse response) {
		String expires = tObject.getString(KEY_EXPIRES_IN);
		String token = tObject.getString(KEY_ACCESS_TOKEN);
		String userId = tObject.getString(KEY_UID);
		int expireNum = NumberUtils.toInt(expires, LOGIN_MAX_AGE);
		Date destDate = DateUtils.addSeconds(new Date(), expireNum);
		String format = DateFormatUtils.format(destDate, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		String sVal = userId + ":" + token;
		response.addHeader("Set-Cookie", "__wb__k=" + sVal + ";Path=/;Expires=" + format);

		Cookie cookie = new Cookie("retTo", "");
		cookie.setPath("/");
		response.addCookie(cookie);

	}

	private void addOrUpdateUser(String openId, String token, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SolrDocument userDoc = null;
		UserKind kind = UserKind.WBUSER;
		synchronized (openId) {
			userDoc = userService.loginUser(openId, token, kind);
			if (userDoc == null) {
				JSONObject dObject = getWBUserInfo(token, openId, config.getWbAppKey(), request);
				if (dObject.getString("id") == null) {
					log.warn("getUserInfo,cause:" + dObject);
					throw new RuntimeException("openId to UserInfo error");
				}
				String nick = dObject.getString("screen_name");
				nick = nick == null ? dObject.getString("name") : nick;
				SolrInputDocument doc = new SolrInputDocument();
				Map<String, Object> cmdMap = Maps.newHashMap();
				cmdMap.put("add", new Date());
				doc.setField("creation", cmdMap);
				doc.setField("wb_open_id", kind.value() + openId);
				doc.setField("wb_token", token);
				doc.setField("nick", nick);
				String sGender = dObject.getString("gender");
				int sex = 0;
				if ("m".equals(sGender)) {
					sex = 1;
				} else if ("f".equals(sGender)) {
					sex = 2;
				}
				doc.setField("sex", sex);
				List<SolrInputDocument> docs = Lists.newArrayList();
				docs.add(doc);
				userService.createUsers(docs);
				userDoc = userService.loginUser(openId, token, kind);
			}
			if (userDoc == null) {
				throw new RuntimeException("login error,WBOpenid:" + openId);
			}
		}
		String id = userDoc.getFieldValue("id").toString();
		String nick = userDoc.getFieldValue("nick").toString();
		String sVal = id + ":" + URLEncoder.encode(nick, "UTF-8");
		Cookie cookie = new Cookie("user_nick", sVal);
		cookie.setMaxAge(LOGIN_MAX_AGE);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	private JSONObject getWBUserInfo(String token, String openId, String wbAppKey, HttpServletRequest request)
			throws Exception {
		String sUserInfo = "https://api.weibo.com/2/users/show.json?uid=" + openId + "&source=" + wbAppKey
				+ "&_cache_time=0&method=get&access_token=" + token + "&__rnd=" + System.currentTimeMillis();
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		}
		Response resp = Jsoup.connect(sUserInfo).ignoreContentType(true).userAgent(userAgent)
				.method(Method.GET).execute();
		String body = resp.body();
		return JSONObject.parseObject(body);
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