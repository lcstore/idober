package com.lezo.idober.action.movie;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.timer.OnlineTorrentMovieTimer;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TaskUtils;
import com.lezo.idober.vo.ActionReturnVo;

@Controller
@Log4j
@RequestMapping("movie/edit")
public class MovieEditDetailController extends BaseController {
    private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
    private static final String CORE_MOVIE = SolrUtils.CORE_SOURCE_MOVIE;
    @Autowired
    private OnlineTorrentMovieTimer onlineTorrentMovieTimer;

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public ModelAndView loadDetail(@PathVariable("itemCode") String itemCode, ModelMap model) throws Exception {
        itemCode = Jsoup.clean(itemCode, Whitelist.basic());
        Matcher matcher = NUM_REG.matcher(itemCode);
        SolrDocument doc = null;
        if (matcher.find()) {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(0);
            solrQuery.setRows(1);
            solrQuery.set("q", "(id:" + itemCode + " OR old_id_s:" + itemCode + ")");
            solrQuery.addFilterQuery("type:movie");
            QueryResponse resp = SolrUtils.getSolrServer(CORE_MOVIE).query(solrQuery);
            SolrDocumentList docList = resp.getResults();
            if (CollectionUtils.isNotEmpty(docList)) {
                doc = docList.get(0);
            }
        }
        if (doc == null) {
            throw new NotFoundException();
        }
        JSONObject dObject = convert2JSON(doc);
        model.addAttribute("oDoc", dObject);
        return new ModelAndView("MovieEditDetail");
    }

    @ResponseBody
    @RequestMapping(value = { "detail" }, method = RequestMethod.POST)
    public ActionReturnVo updateDetail(@RequestBody JSONObject paramObject) throws Exception {
        ActionReturnVo returnVo = new ActionReturnVo();
        JSONArray idArray = paramObject.getJSONArray("ids");
        if (CollectionUtils.isEmpty(idArray)) {
            returnVo.setMsg("empty id array");
            returnVo.setCode(ActionReturnVo.CODE_PARAM);
            return returnVo;
        }
        JSONArray taskArray = new JSONArray();
        List<String> typeList = Lists.newArrayList("douban-movie-detail");
        for (int index = 0, size = idArray.size(); index < size; index++) {
            String idString = idArray.getString(index);
            if (StringUtils.isBlank(idString)) {
                continue;
            }
            for (String type : typeList) {
                JSONObject taskObject = new JSONObject();
                taskObject.put("type", type);
                taskObject.put("url", "https://movie.douban.com/subject/" + idString + "/");
                taskObject.put("level", 1000);
                taskObject = TaskUtils.withParam(taskObject, "retry", "0");
                taskObject = TaskUtils.withParam(taskObject, "id", idString);
                taskArray.add(taskObject);
            }
        }
        TaskUtils.createTasks(taskArray);
        return returnVo;
    }

    @ResponseBody
    @RequestMapping(value = { "deploy" }, method = RequestMethod.POST)
    public ActionReturnVo deployMovie(@RequestBody JSONObject paramObject) throws Exception {
        ActionReturnVo returnVo = new ActionReturnVo();
        JSONArray idArray = paramObject.getJSONArray("ids");
        if (CollectionUtils.isEmpty(idArray)) {
            returnVo.setMsg("empty id array");
            returnVo.setCode(ActionReturnVo.CODE_PARAM);
            return returnVo;
        }
        JSONArray docArray = new JSONArray();
        for (int index = 0, size = idArray.size(); index < size; index++) {
            String idString = idArray.getString(index);
            if (StringUtils.isBlank(idString)) {
                continue;
            }
            JSONObject docObj = new JSONObject();
            docObj.put("id", idString);
            docObj.put("had_move_s", 0);
            Map<String, Object> setValMap = new JSONObject();
            setValMap.put("set", 1);
            docObj.put("editor", setValMap);
            docArray.add(docObj);
        }
        String sContent = docArray.toJSONString();
        String contentType = "application/json";
        Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
        ContentStreamUpdateRequest request =
                new ContentStreamUpdateRequest("/update/json");
        for (ContentStream cs : strems) {
            request.addContentStream(cs);
        }
        request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        NamedList<Object> resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).request(request);
        log.info("resp:" + resp.size() + ",update:" + docArray.size());
        onlineTorrentMovieTimer.run();
        return returnVo;
    }

    @ResponseBody
    @RequestMapping(value = { "torrent" }, method = RequestMethod.POST)
    public ActionReturnVo addTorrent(@RequestBody JSONObject paramObject) throws Exception {
        ActionReturnVo returnVo = new ActionReturnVo();
        String itemCode = paramObject.getString("id");
        Boolean bCover = paramObject.getBoolean("cover");
        String level = paramObject.getString("level");
        String name = paramObject.getString("name");
        Map<String, String> shareMap = Maps.newHashMap();
        JSONObject sObject = new JSONObject();
        String sUrl = paramObject.getString("url");
        String secret = paramObject.getString("secret");
        Long size = paramObject.getLong("size");
        name = StringUtils.isEmpty(name) ? "百度云分享" : name;
        sObject.put("url", sUrl);
        sObject.put("secret", secret);
        sObject.put("type", "baidu-share");
        sObject.put("name", name);
        sObject.put("level", level);
        sObject.put("size", size);
        shareMap.put(sUrl, sObject.toJSONString());
        if (bCover == null || !bCover) {
            SolrDocument movieDoc = queryMovieShareById(itemCode);
            if (movieDoc == null) {
                return returnVo;
            }
            Collection<Object> shares = movieDoc.getFieldValues("shares");
            if (CollectionUtils.isNotEmpty(shares)) {
                for (Object oContent : shares) {
                    String sContent = oContent.toString();
                    JSONObject srcObject = JSONObject.parseObject(sContent);
                    String sLink = srcObject.getString("url");
                    if (shareMap.containsKey(sLink)) {
                        continue;
                    }
                    shareMap.put(sLink, sContent);
                }
            }
        }
        JSONArray docArray = new JSONArray();
        JSONObject docObj = new JSONObject();
        docObj.put("id", itemCode);
        Map<String, Object> setValMap = new JSONObject();
        setValMap.put("set", shareMap.values());
        docObj.put("shares", setValMap);
        docArray.add(docObj);
        String sContent = docArray.toJSONString();
        String contentType = "application/json";
        Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
        ContentStreamUpdateRequest request =
                new ContentStreamUpdateRequest("/update/json");
        for (ContentStream cs : strems) {
            request.addContentStream(cs);
        }
        request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        NamedList<Object> resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).request(request);
        log.info("resp:" + resp.size() + ",update:" + docArray.size());
        return returnVo;
    }

    private SolrDocument queryMovieShareById(String itemCode) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(1);
        solrQuery.set("q", "(id:" + itemCode + ")");
        solrQuery.addField("shares");
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
        SolrDocumentList docList = resp.getResults();
        if (CollectionUtils.isNotEmpty(docList)) {
            return docList.get(0);
        }
        return null;
    }

    private SolrDocument queryShareByCode(String code) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(1);
        solrQuery.set("q", "(code_s:" + code + ")");
        solrQuery.addFilterQuery("source_group_s:torrent");
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
        SolrDocumentList docList = resp.getResults();
        if (CollectionUtils.isNotEmpty(docList)) {
            return docList.get(0);
        }
        return null;
    }

    private JSONObject convert2JSON(SolrDocument doc) {
        JSONObject srcObject = new JSONObject(doc);
        JSONArray crumbArr = createCrumbs(srcObject);
        srcObject.put("crumbs", crumbArr);
        // assortTorrents(srcObject);
        fillFeeds(srcObject);
        return srcObject;
    }

    private void fillFeeds(JSONObject srcObject) {
        String name = srcObject.getString("name");
        if (StringUtils.isEmpty(name)) {
            return;
        }
        name = ClientUtils.escapeQueryChars(name);
        SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "title:" + name);
        solrQuery.setRows(10);
        solrQuery.addFilterQuery("source_group_s:torrent");
        try {
            QueryResponse resp = server.query(solrQuery);
            SolrDocumentList docList = resp.getResults();
            srcObject.put("src_count", docList.getNumFound());
            srcObject.put("feeds", docList);
        } catch (Exception e) {
            log.warn("fillFeedInfo,name:" + name + ",cause:", e);
        }
    }

    private JSONArray createCrumbs(JSONObject srcObject) {
        JSONArray crumbArr = new JSONArray();
        String type = srcObject.getString("type");
        type = type == null ? "movie" : type;
        Map<String, String> typeNameMap = Maps.newHashMap();
        typeNameMap.put("movie", "电影");
        JSONArray cArray = new JSONArray();
        JSONObject cObj = new JSONObject();
        cObj.put("name", typeNameMap.get(type));
        cObj.put("link", "/" + type + ".html");
        cArray.add(cObj);
        crumbArr.add(cArray);
        addCrumbByRegion(srcObject, crumbArr, type);
        addCrumbByGenres(srcObject, crumbArr, type);
        return crumbArr;
    }

    private void addCrumbByRegion(JSONObject srcObject, JSONArray crumbArr, String type) {
        JSONArray regionArr = srcObject.getJSONArray("regions");
        if (regionArr != null) {
            JSONArray groupArray = new JSONArray();
            groupArray.add(regionArr.get(0));
            JSONArray rArray = new JSONArray();
            Map<String, String> regionMap = queryKeyValMapByRegions(groupArray);
            String regionLink = "/" + type + "/region/";
            for (int i = groupArray.size() - 1; i >= 0; i--) {
                String sRegion = groupArray.getString(i);
                if (StringUtils.isBlank(sRegion)) {
                    continue;
                }
                sRegion = sRegion.trim();
                String sPyRegion = regionMap.get(sRegion);
                JSONObject gObj = new JSONObject();
                gObj.put("name", sRegion);
                gObj.put("pyVal", sPyRegion);
                gObj.put("link", regionLink + sPyRegion + ".html");
                rArray.add(gObj);
            }
            crumbArr.add(rArray);

            if (regionArr.size() > 1) {
                regionArr.remove(0);
                srcObject.put("regions", regionArr);
            }
        }

    }

    private void addCrumbByGenres(JSONObject srcObject, JSONArray crumbArr, String type) {
        JSONArray regionArr = crumbArr.getJSONArray(crumbArr.size() - 1);
        String sRegionPy = null;
        if (!regionArr.isEmpty()) {
            JSONObject oRegion = regionArr.getJSONObject(0);
            sRegionPy = oRegion.getString("pyVal");
        }
        JSONArray genreArr = srcObject.getJSONArray("genres");
        if (genreArr != null) {
            JSONArray gArray = new JSONArray();
            Map<String, String> kvMap = queryKeyValMapByGeners(genreArr);
            String genreLink = "/" + type + "/genre/";
            for (int i = 0, size = genreArr.size(); i < size; i++) {
                String sGenre = genreArr.getString(i);
                if (StringUtils.isBlank(sGenre)) {
                    continue;
                }
                sGenre = sGenre.trim();
                String sPyGenre = kvMap.get(sGenre);
                if (sPyGenre == null) {
                    continue;
                }
                if (StringUtils.isNotBlank(sRegionPy)) {
                    sPyGenre += "-" + sRegionPy;
                }
                JSONObject gObj = new JSONObject();
                gObj.put("name", sGenre);
                gObj.put("link", genreLink + sPyGenre + ".html");
                gArray.add(gObj);
            }
            crumbArr.add(gArray);
        }

    }

    private Map<String, String> queryKeyValMapByRegions(JSONArray regionArr) {
        String sHead = "(type:idober-group-region AND (";
        StringBuilder sb = new StringBuilder();
        sb.append(sHead);
        for (int i = 0, size = regionArr.size(); i < size; i++) {
            String sKeyWord = regionArr.getString(i);
            if (StringUtils.isBlank(sKeyWord)) {
                continue;
            }
            sKeyWord = sKeyWord.trim();
            if (sb.length() > sHead.length()) {
                sb.append(" OR ");
            }
            sb.append("title:");
            sb.append(sKeyWord);
        }
        sb.append("))");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(100);
        solrQuery.set("q", sb.toString());
        solrQuery.addField("title,short_s");
        Map<String, String> kvMap = Maps.newHashMap();
        try {
            QueryResponse resp = SolrUtils.getSolrWithMeta().query(solrQuery);
            SolrDocumentList docList = resp.getResults();
            for (SolrDocument doc : docList) {
                String cnVal = doc.getFieldValue("title").toString();
                String pyVal = doc.getFieldValue("short_s").toString();
                kvMap.put(cnVal, pyVal);
            }
        } catch (Exception e) {
            log.warn("query region:" + regionArr.toJSONString() + ",cause:", e);
        }
        return kvMap;
    }

    private Map<String, String> queryKeyValMapByGeners(JSONArray genreArr) {
        return queryKeyValMap("idober-kv-genres", genreArr);
    }

    private Map<String, String> queryKeyValMap(String type, JSONArray cnArray) {
        String sHead = "(type:" + type + " AND (";
        StringBuilder sb = new StringBuilder();
        sb.append(sHead);
        for (int i = 0, size = cnArray.size(); i < size; i++) {
            String sGenre = cnArray.getString(i);
            if (StringUtils.isBlank(sGenre)) {
                continue;
            }
            sGenre = sGenre.trim();
            if (sb.length() > sHead.length()) {
                sb.append(" OR ");
            }
            sb.append("cn_s:");
            sb.append(sGenre);
        }
        sb.append("))");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(100);
        solrQuery.set("q", sb.toString());
        solrQuery.addField("cn_s,py_s");
        Map<String, String> kvMap = Maps.newHashMap();
        try {
            QueryResponse resp = SolrUtils.getSolrWithMeta().query(solrQuery);
            SolrDocumentList docList = resp.getResults();
            for (SolrDocument doc : docList) {
                String cnVal = doc.getFieldValue("cn_s").toString();
                String pyVal = doc.getFieldValue("py_s").toString();
                kvMap.put(cnVal, pyVal);
            }
        } catch (Exception e) {
            log.warn("query type:" + type + ",cause:", e);
        }
        return kvMap;
    }

}
