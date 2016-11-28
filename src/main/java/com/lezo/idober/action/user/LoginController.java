package com.lezo.idober.action.user;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lezo.idober.cacher.ExpireCacher;
import com.lezo.idober.config.AppConfig;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ActionReturnVo;

@Controller
@RequestMapping("login")
public class LoginController {
    private ExpireCacher expireCacher = ExpireCacher.getInstance();
    @Autowired
    private AppConfig config;

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public String toLogin(@RequestParam(name = "retTo", defaultValue = "") String returnTo,
            ModelMap model) throws Exception {
        return "LoginHome";
    }

    @ResponseBody
    @RequestMapping(value = { "loginWB" }, method = RequestMethod.POST)
    public ActionReturnVo loginWB(@RequestBody com.alibaba.fastjson.JSONObject tokenObj, HttpServletRequest request) {
        String openId = tokenObj.getString("openId");
        String wbOpenId = convertWBId(openId);
        String accessToken = tokenObj.getString("token");
        ActionReturnVo returnVo = new ActionReturnVo();
        if (StringUtils.isBlank(openId)) {
            returnVo.setMsg("error openId");
            returnVo.setCode(ActionReturnVo.CODE_PARAM);
            return returnVo;
        }
        try {

            SolrDocumentList solrList = updateAndGetWBUser(openId, accessToken, request);
            if (CollectionUtils.isEmpty(solrList)) {
                returnVo.setMsg("error token");
                returnVo.setCode(ActionReturnVo.CODE_PARAM);
                return returnVo;
            }
            SolrDocument userDoc = solrList.get(0);
            String id = userDoc.getFieldValue("id").toString();
            // Map<String, Object> uObject = Maps.newHashMap();
            JSONObject uObject = new JSONObject();
            uObject.put("user_id", id);
            Object loginDateObj = userDoc.getFieldValue("login_date");
            Date loginDate = new Date();
            if (loginDateObj != null) {
                loginDate = DateUtil.parseDate(loginDateObj.toString());
            }
            uObject.put("last_login", loginDate.getTime());
            Object nickObj = userDoc.getFieldValue("nick");
            if (nickObj == null) {
                int maxLen = 6;
                int len = wbOpenId.length() < maxLen ? wbOpenId.length() : maxLen;
                nickObj = wbOpenId.substring(wbOpenId.length() - len);
            }
            uObject.put("nick", nickObj.toString());
            returnVo.setData(uObject);
        } catch (Exception ex) {
            returnVo.setMsg(ex.getClass().getSimpleName());
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
        }
        return returnVo;
    }

    private SolrDocumentList updateAndGetWBUser(String openId, String accessToken, HttpServletRequest request)
            throws Exception {
        synchronized (openId) {
            String exkey = "loginWB:" + accessToken;
            String wbOpenId = convertWBId(openId);
            SolrDocumentList solrList = queryUsers(wbOpenId);
            if (expireCacher.getValue(exkey) != null) {
                if (CollectionUtils.isEmpty(solrList)) {
                    addWBUser(openId, accessToken, request);
                    expireCacher.removeValue(exkey);
                    solrList = queryUsers(wbOpenId);
                }
                if (CollectionUtils.isNotEmpty(solrList)) {
                    updateLogin(solrList.get(0));
                }
            }
            return solrList;
        }
    }

    private SolrDocumentList updateAndGetQQUser(String openId, String accessToken) throws Exception {
        synchronized (openId) {
            String exkey = "loginQQ:" + accessToken;
            SolrDocumentList solrList = queryUsers(openId);
            if (expireCacher.getValue(exkey) != null) {
                if (CollectionUtils.isEmpty(solrList)) {
                    addQQUser(openId);
                    expireCacher.removeValue(exkey);
                    solrList = queryUsers(openId);
                }
                if (CollectionUtils.isNotEmpty(solrList)) {
                    updateLogin(solrList.get(0));
                }
            }
            return solrList;
        }
    }

    private void addWBUser(String openId, String accessToken, HttpServletRequest request) throws Exception {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
        }
        String sWBUserUrl = "https://api.weibo.com/2/users/show.json?uid=" + openId + "&source="
                + config.getWbAppKey()
                + "&_cache_time=0&method=get&access_token=" + accessToken + "&__rnd=" + System.currentTimeMillis();
        Response resp = Jsoup.connect(sWBUserUrl).ignoreContentType(true).userAgent(userAgent).method(Method.GET)
                .execute();
        String sContent = resp.body();
        JSONObject wbObject = JSONObject.parseObject(sContent);
        String nick = wbObject.getString("name");
        SolrInputDocument doc = createUserDocument();
        doc.setField("wb_open_id", convertWBId(openId));
        doc.setField("nick", nick);
        doCommit(doc, SolrUtils.CORE_USER);
    }

    private void doCommit(SolrInputDocument doc, String coreName) throws Exception {
        SolrServer server = SolrUtils.getSolrServer(coreName);
        server.add(doc);
        server.commit();
    }

    @ResponseBody
    @RequestMapping(value = { "loginQQ" }, method = RequestMethod.POST)
    public ActionReturnVo loginQQ(@RequestBody com.alibaba.fastjson.JSONObject tokenObj) {
        String openId = convertQQId(tokenObj.getString("openId"));
        String accessToken = tokenObj.getString("token");
        ActionReturnVo returnVo = new ActionReturnVo();
        if (StringUtils.isBlank(openId)) {
            returnVo.setMsg("error openId");
            returnVo.setCode(ActionReturnVo.CODE_PARAM);
            return returnVo;
        }
        try {
            SolrDocumentList solrList = updateAndGetQQUser(openId, accessToken);
            if (CollectionUtils.isEmpty(solrList)) {
                returnVo.setMsg("error token");
                returnVo.setCode(ActionReturnVo.CODE_PARAM);
                return returnVo;
            }
            SolrDocument userDoc = solrList.get(0);
            String id = userDoc.getFieldValue("id").toString();
            // Map<String, Object> uObject = Maps.newHashMap();
            JSONObject uObject = new JSONObject();
            uObject.put("user_id", id);
            Object loginDateObj = userDoc.getFieldValue("login_date");
            Date loginDate = new Date();
            if (loginDateObj != null) {
                loginDate = DateUtil.parseDate(loginDateObj.toString());
            }
            uObject.put("last_login", loginDate.getTime());
            Object nickObj = userDoc.getFieldValue("nick");
            if (nickObj == null) {
                int maxLen = 6;
                int len = openId.length() < maxLen ? openId.length() : maxLen;
                nickObj = "QQ" + openId.substring(openId.length() - len);
            }
            uObject.put("nick", nickObj.toString());
            returnVo.setData(uObject);
        } catch (Exception ex) {
            returnVo.setMsg(ex.getClass().getSimpleName());
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
        }
        return returnVo;
    }

    private String convertWBId(String openId) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        return "WB" + openId;
    }

    private String convertQQId(String openId) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        return "QQ" + openId;
    }

    private void updateLogin(SolrDocument userDoc) throws Exception {
        SolrInputDocument doc = createUserDocument();
        doc.setField("id", userDoc.getFieldValue("id"));
        Map<String, Object> cmdMap = Maps.newHashMap();
        cmdMap.put("inc", 1);
        doc.setField("login_count", cmdMap);
        // String sLoginDate = DateUtil.getThreadLocalDateFormat().format(new Date());
        doc.setField("login_date", new Date());
        doCommit(doc, SolrUtils.CORE_USER);
    }

    private void addQQUser(String openId) throws Exception {
        SolrInputDocument doc = createUserDocument();
        doc.setField("qq_open_id", openId);
        doCommit(doc, SolrUtils.CORE_USER);
    }

    private SolrInputDocument createUserDocument() {
        SolrInputDocument doc = new SolrInputDocument();
        Map<String, Object> cmdMap = Maps.newHashMap();
        cmdMap.put("add", new Date());
        doc.setField("creation", cmdMap);
        return doc;
    }

    private SolrDocumentList queryUsers(String userKey) throws Exception {
        if (StringUtils.isBlank(userKey)) {
            return null;
        }
        userKey = ClientUtils.escapeQueryChars(userKey);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(100);
        solrQuery.set("q", userKey);
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_USER).query(solrQuery);
        return resp.getResults();
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("login");
//		http://www.mkyong.com/spring-security/spring-security-form-login-example/
		return model;

	}
}