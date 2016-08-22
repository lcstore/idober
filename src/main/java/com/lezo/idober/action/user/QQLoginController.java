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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.config.AppConfig;
import com.lezo.idober.service.UserKind;
import com.lezo.idober.service.UserService;

@Controller
@RequestMapping("oauth2.0")
@Log4j
public class QQLoginController {
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String VAL_TOKEN_URL =
			"https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=[YOUR_APP_ID]&client_secret=[YOUR_APP_Key]&code=[The_AUTHORIZATION_CODE]&state=[The_CLIENT_STATE]&redirect_uri=[YOUR_REDIRECT_URI]";
	private static final int LOGIN_MAX_AGE = 24 * 60 * 60;
	@Autowired
	private AppConfig config;
	@Autowired
	private UserService userService;

	@RequestMapping(value = { "qqLogin" }, method = RequestMethod.GET)
	public ModelAndView doLogin(@RequestParam("code") String code, @RequestParam("state") String state,
			@CookieValue(name = "retTo", required = false) String retTo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			Map<String, String> paramMap = getTokenParams(code, state, request);
			String token = paramMap.get(KEY_ACCESS_TOKEN);
			if (StringUtils.isBlank(token)) {
				throw new RuntimeException("error token");
			}
			JSONObject idObject = getOpenIdParams(token, request);
			String openid = idObject.getString("openid");
			if (StringUtils.isBlank(openid)) {
				throw new RuntimeException("error openid");
			}
			addOrUpdateUser(openid, token, request, response);
			addCookies(paramMap, response);
			if (StringUtils.isBlank(retTo)) {
				retTo = "/";
			} else {
				retTo = URLDecoder.decode(retTo, "UTF-8");
			}
			RedirectView red = new RedirectView(retTo, true);
			red.setStatusCode(HttpStatus.FOUND);
			return new ModelAndView(red);
		} catch (Exception ex) {
			log.warn("qqLogin,cause:", ex);
			RedirectView red = new RedirectView("/login", true);
			red.setStatusCode(HttpStatus.FOUND);
			return new ModelAndView(red);
		}

	}

	private void addOrUpdateUser(String openId, String token, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SolrDocument userDoc = null;
		synchronized (openId) {
			userDoc = userService.loginUser(openId, token, UserKind.QQUSER);
			if (userDoc == null) {
				JSONObject uObject = getQQUserInfo(token, openId, config.getQqConnectAppid(), request);
				JSONObject dObject = uObject.getJSONObject("data");
				if (dObject == null) {
					log.warn("getUserInfo,cause:" + uObject);
					throw new RuntimeException("openId to UserInfo error");
				}
				String nick = dObject.getString("nick");
				nick = nick == null ? dObject.getString("name") : nick;
				SolrInputDocument doc = new SolrInputDocument();
				Map<String, Object> cmdMap = Maps.newHashMap();
				cmdMap.put("add", new Date());
				doc.setField("creation", cmdMap);
				doc.setField("qq_open_id", UserKind.QQUSER.value() + openId);
				doc.setField("qq_token", token);
				doc.setField("nick", nick);
				doc.setField("sex", dObject.getIntValue("sex"));
				doc.setField("birth_year", dObject.getIntValue("birth_year"));
				doc.setField("birth_month", dObject.getIntValue("birth_month"));
				doc.setField("birth_day", dObject.getIntValue("birth_day"));
				List<SolrInputDocument> docs = Lists.newArrayList();
				docs.add(doc);
				userService.createUsers(docs);
				userDoc = userService.loginUser(openId, token, UserKind.QQUSER);
			}
			if (userDoc == null) {
				throw new RuntimeException("login error,QQOpenid:" + openId);
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

	private void addCookies(Map<String, String> paramMap, HttpServletResponse response) {
		String token = paramMap.get(KEY_ACCESS_TOKEN);
		String expires = paramMap.get(KEY_EXPIRES_IN);
		String sVal = "TC_MK=" + token;
		int expireNum = NumberUtils.toInt(expires, 86400);
		Date destDate = DateUtils.addSeconds(new Date(), expireNum);
		String format = DateFormatUtils.format(destDate, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		Cookie cookie = new Cookie("retTo", "");
		cookie.setPath("/");
		response.addCookie(cookie);
		// response.addCookie(cookie);//=作为特殊字符,会添加双引号__qc__k="TC_MK=03DC873FB5948E1373C392B9FFB8C928";
		response.addHeader("Set-Cookie", "__qc__k=" + sVal + ";Path=/;Expires=" + format);

	}

	private Map<String, String> getTokenParams(String code, String state, HttpServletRequest request) throws Exception {
		String sTokenUrl = VAL_TOKEN_URL;
		sTokenUrl = sTokenUrl.replace("[YOUR_APP_ID]", config.getQqConnectAppid());
		sTokenUrl = sTokenUrl.replace("[YOUR_APP_Key]", config.getQqConnectAppkey());
		sTokenUrl = sTokenUrl.replace("[YOUR_REDIRECT_URI]", config.getQqRedirectUrl());
		sTokenUrl = sTokenUrl.replace("[The_AUTHORIZATION_CODE]", code);
		sTokenUrl = sTokenUrl.replace("[The_CLIENT_STATE]", state);
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		}
		Response resp =
				Jsoup.connect(sTokenUrl).ignoreContentType(true).userAgent(userAgent).method(Method.GET).execute();
		String sPath = resp.body();
		if (!sPath.contains(KEY_ACCESS_TOKEN)) {
			log.warn("code to token,cause:" + sPath);
			throw new RuntimeException("code to access token error");
		}
		String[] paramArr = sPath.split("&");
		Map<String, String> paramMap = Maps.newHashMap();
		for (String param : paramArr) {
			String[] kvArr = param.split("=");
			if (kvArr.length == 2) {
				String key = kvArr[0].trim();
				String value = kvArr[1].trim();
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}

	private JSONObject getOpenIdParams(String token, HttpServletRequest request) throws Exception {
		String sTokenUrl = "https://graph.qq.com/oauth2.0/me?access_token=" + token;
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		}
		Response resp = Jsoup.connect(sTokenUrl).ignoreContentType(true).userAgent(userAgent)
				.method(Method.GET).execute();
		String body = resp.body();
		int beginIndex = body.indexOf("{");
		int endIndex = body.lastIndexOf("}");
		if (beginIndex < 0 || endIndex < 0) {
			return new JSONObject();
		}
		body = body.substring(beginIndex, endIndex + 1);
		return JSONObject.parseObject(body);
	}

	private JSONObject getQQUserInfo(String token, String openId, String appId, HttpServletRequest request)
			throws Exception {
		String sUserInfo = "https://graph.qq.com/user/get_info?access_token=" + token
				+ "&oauth_consumer_key=" + appId + "&openid=" + openId + "&format=";
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		}
		Response resp = Jsoup.connect(sUserInfo).ignoreContentType(true).userAgent(userAgent)
				.method(Method.GET).execute();
		String body = resp.body();
		return JSONObject.parseObject(body);
	}
}