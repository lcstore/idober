package com.lezo.idober.action;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;
import org.codehaus.jackson.map.annotate.JsonView;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.view.ReturnView;
import com.lezo.idober.vo.ActionReturnVo;
import com.lezo.idober.vo.SolrDocListVo;
import com.lezo.idober.vo.SolrDocVo;
import com.lezo.idober.vo.SolrFieldVo;
import com.lezo.iscript.utils.BatchIterator;

@Log4j
@RequestMapping("doc")
@Controller
public class DocumentController {
    public static final String MOVIE_SPLITOR = "-";

    @RequestMapping(value = "{core}", method = RequestMethod.POST)
    @ResponseBody
    public ActionReturnVo addDocs(@PathVariable("core") String core, @RequestBody SolrDocListVo docs) {
        ActionReturnVo returnVo = new ActionReturnVo();
        try {
            // core = "core0";
            if (docs == null) {
                returnVo.setCode(ActionReturnVo.CODE_PARAM);
                returnVo.setMsg("非法参数");
                return returnVo;
            }
            // log.info("docs:" + JSON.toJSONString(docs));
            SolrServer server = SolrUtils.getSolrServer(core);
            if ("1".equals(docs.getType())) {
                int count = 0;
                for (SolrDocVo docVo : docs.getDocs()) {
                    SolrInputDocument doc = new SolrInputDocument();
                    for (SolrFieldVo fld : docVo.getFields()) {
                        Object valObject = fld.getValue();
                        if (fld.getKey().equals("date") && NumberUtils.isNumber(fld.getValue())) {
                            valObject = new Date(Long.valueOf(fld.getValue()));
                        }
                        doc.addField(fld.getKey(), valObject);
                    }
                    if (core.equals("core0") || core.equals("core2")) {
                        Map<String, Object> fieldModifier = Maps.newHashMap();
                        fieldModifier.put("add", new Date());
                        doc.addField("creation", fieldModifier);
                    }
                    try {
                        server.add(doc);
                        count++;
                    } catch (Exception e) {
                        log.warn("add doc to core:" + core, e);
                    }
                }
                if (count > 0) {
                    server.commit();
                }
                log.info("core:" + core + ",create doc:" + count);
            }
        } catch (Exception e) {
            log.warn("core:" + core, e);
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
            returnVo.setMsg(e.getMessage());
        }
        return returnVo;
    }

    @RequestMapping(value = "movie", method = RequestMethod.GET)
    @JsonView(ReturnView.StatusCode.class)
    @ResponseBody
    public ActionReturnVo buildMovieHome() {
        ActionReturnVo returnVo = new ActionReturnVo();
        Integer offset = 0;
        Integer limit = 100;
        Integer rankCount = 20;
        Integer dailyCount = 36;
        Integer weeklyCount = 12;
        Integer newlyCount = 12;
        Integer classicCount = 18;
        try {
            SolrDocListVo docListVo = new SolrDocListVo();
            docListVo.setType("1");
            List<SolrDocVo> docsList = Lists.newArrayList();
            docListVo.setDocs(docsList);

            JSONObject ctObject = createRankContentByGroup("北美", rankCount, offset, limit);
            String content = ctObject.toJSONString();
            String sGroup = "票房榜" + MOVIE_SPLITOR + "北美";
            addWeekMovieHomeByGroup(sGroup, content, docsList);
            ctObject = createRankContentByGroup("中国内地", rankCount, offset, limit);
            content = ctObject.toJSONString();
            sGroup = "票房榜" + MOVIE_SPLITOR + "全国";
            addWeekMovieHomeByGroup(sGroup, content, docsList);
            ctObject = createRankContentByGroup("*", rankCount, offset, limit);
            content = ctObject.toJSONString();
            sGroup = "票房榜" + MOVIE_SPLITOR + "综合";
            addWeekMovieHomeByGroup(sGroup, content, docsList);
            // 今日更新
            ctObject = createDailyContent(dailyCount, offset, limit);
            content = ctObject.toJSONString();
            sGroup = "今日更新";
            addDailyMovieHomeByGroup(sGroup, content, docsList);
            // 本周热门
            ctObject = createWeeklyContent(weeklyCount, offset, limit);
            content = ctObject.toJSONString();
            sGroup = "本周热门";
            addWeekMovieHomeByGroup(sGroup, content, docsList);
            // 即将上映
            ctObject = createNewlyContent(newlyCount, offset, limit);
            content = ctObject.toJSONString();
            sGroup = "即将上映";
            addDailyMovieHomeByGroup(sGroup, content, docsList);
            // 经典电影
            ctObject = createClassicContent(classicCount, offset, 10000);
            content = ctObject.toJSONString();
            sGroup = "经典电影";
            addWeekMovieHomeByGroup(sGroup, content, docsList);
            addTasks(docListVo);
            addDocs("core0", docListVo);
            JSONObject rsObject = getBuildResult(docsList);
            returnVo.setData(rsObject.toJSONString());
        } catch (Exception e) {
            log.warn("buildMovieHome cause:", e);
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
            returnVo.setMsg(e.getMessage());
        }
        return returnVo;
    }

    private void addTasks(SolrDocListVo docListVo) {
        List<SolrDocVo> docs = docListVo.getDocs();
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }
        Set<String> idSets = Sets.newHashSet();
        for (int i = 0; i < docs.size(); i++) {
            SolrDocVo oDoc = docs.get(i);
            for (SolrFieldVo fld : oDoc.getFields()) {
                if ("content".equals(fld.getKey())) {
                    String sVal = fld.getValue();
                    JSONObject dObj = JSON.parseObject(sVal);
                    JSONArray dArray = dObj.getJSONArray("dataList");
                    for (int ij = 0; ij < dArray.size(); ij++) {
                        String sIdString = dArray.getString(ij);
                        idSets.add(sIdString);
                    }
                }
            }
        }
        //
        try {
            createTasks(Lists.newArrayList(idSets));
            log.info("finish to offer task:" + idSets.size());
        } catch (Exception e) {
            log.info("fail to offer task:" + idSets.size() + ",cause:", e);
        }

    }

    private void createTasks(List<String> idList) throws Exception {
        BatchIterator<String> it = new BatchIterator<String>(idList, 100);
        while (it.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (String id : it.next()) {
                if (sb.length() > 1) {
                    sb.append(" OR ");
                }
                sb.append("id:" + id);
            }
            sb.append(")");
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.set("q", sb.toString());
            solrQuery.setStart(0);
            solrQuery.setRows(1000);
            solrQuery.addField("content");
            QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
            List<MovieSolr> newSolrs = resp.getBeans(MovieSolr.class);
            if (CollectionUtils.isNotEmpty(newSolrs)) {
                JSONArray taskArray = new JSONArray();
                for (MovieSolr solr : newSolrs) {
                    JSONObject cObject = JSONObject.parseObject(solr.getContent());
                    String sSourceUrl = cObject.getString("source_url");
                    if (StringUtils.isBlank(sSourceUrl)) {
                        continue;
                    }
                    JSONObject tObject = new JSONObject();
                    tObject.put("type", "mtime-movie-torrent");
                    tObject.put("level", 1000);
                    tObject.put("url", sSourceUrl);
                    taskArray.add(tObject);
                }
                if (taskArray.size() > 0) {
                    createTasks(taskArray);
                }
            }
        }
    }

    private void createTasks(JSONArray taskArray) throws Exception {
        if (taskArray == null || taskArray.size() < 1) {
            return;
        }
        String url = "http://www.lezomao.com:8090/taskmgr/createtasks";
        String referrer = "http://www.lezomao.com/idober";
        Response resp =
                Jsoup.connect(url).referrer(referrer).method(Method.POST).data("tasks", taskArray.toJSONString())
                        .ignoreContentType(true).execute();
        log.info("resp:" + resp.body());
    }

    private JSONObject createClassicContent(Integer destCount, Integer offset, Integer limit) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "(type:douban-moviepraise)");
        solrQuery.setSort("group", ORDER.desc);
        solrQuery.setSort("ranking", ORDER.asc);
        // solrQuery.addField("id,title,group,ranking");
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        List<DataSolr> praiseSolrs = resp.getBeans(DataSolr.class);

        List<JSONObject> jsonList = toParamByRank(praiseSolrs);
        List<MovieSolr> enRankMovies = queryMovieSolrByNameDateBatch(jsonList, 10, offset, limit);

        JSONObject enObject = new JSONObject();
        JSONArray dataList = new JSONArray();
        enObject.put("dataList", dataList);
        enObject.put("total", resp.getResults().getNumFound());
        for (MovieSolr ms : enRankMovies) {
            if (StringUtils.isBlank(ms.getDirectors())) {
                continue;
            }
            dataList.add(ms.getId());
            if (dataList.size() >= destCount) {
                break;
            }
        }
        return enObject;
    }

    private JSONObject createNewlyContent(Integer destCount, Integer offset, Integer limit) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "(type:mtime-hoting)");
        solrQuery.setSort("group", ORDER.desc);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        List<DataSolr> newSolrs = resp.getBeans(DataSolr.class);
        List<JSONObject> jsonList = toParamByHoting(newSolrs);

        List<MovieSolr> enRankMovies = queryMovieSolrByNameDateBatch(jsonList, 10, offset, limit);
        JSONObject enObject = new JSONObject();
        JSONArray dataList = new JSONArray();
        enObject.put("dataList", dataList);
        enObject.put("total", resp.getResults().getNumFound());
        for (MovieSolr ms : enRankMovies) {
            if (StringUtils.isBlank(ms.getDirectors())) {
                continue;
            }
            dataList.add(ms.getId());
            if (dataList.size() >= destCount) {
                break;
            }
        }
        return enObject;
    }

    private List<JSONObject> toParamByHoting(List<DataSolr> newSolrs) {
        List<JSONObject> paramList = Lists.newArrayList();
        for (DataSolr solr : newSolrs) {
            JSONObject pObj = new JSONObject();
            pObj.put("name", solr.getTitle());
            try {
                Date date = DateUtils.parseDate(solr.getGroup(), "yyyyMMdd");
                pObj.put("date", date);
                paramList.add(pObj);
            } catch (Exception e) {
                log.warn("error group:" + solr.getGroup() + ",id:" + solr.getId());
                e.printStackTrace();
            }
        }
        return paramList;
    }

    private JSONObject createDailyContent(Integer destCount, Integer offset, Integer limit) throws Exception {
        // Date fromTime = DateUtils.addDays(new Date(), -10);
        // String sFromTime = DateUtil.getThreadLocalDateFormat().format(fromTime);
        Date fromTime = DateUtils.addDays(new Date(), -14);
        String sFromTime = DateUtil.getThreadLocalDateFormat().format(fromTime);
        StringBuilder sb = new StringBuilder();
        sb.append("(timestamp:[");
        sb.append(sFromTime);
        sb.append(" TO *] AND tcount:[1 TO *])");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", sb.toString());

        solrQuery.addSort("date", ORDER.desc);
        solrQuery.addField("id,name,timestamp,directors");
        QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
        List<MovieSolr> mSolrs = resp.getBeans(MovieSolr.class);

        JSONObject enObject = new JSONObject();
        JSONArray dataList = new JSONArray();
        enObject.put("dataList", dataList);
        enObject.put("total", resp.getResults().getNumFound());
        for (MovieSolr ms : mSolrs) {
            // if (StringUtils.isBlank(ms.getDirectors())) {
            // continue;
            // }
            dataList.add(ms.getId());
            if (dataList.size() >= destCount) {
                break;
            }
        }
        return enObject;
    }

    private JSONObject createWeeklyContent(Integer destCount, Integer offset, Integer limit) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "(type:douban-movieheat AND group:热门)");
        solrQuery.setSort("ranking", ORDER.asc);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        List<DataSolr> heatList = resp.getBeans(DataSolr.class);
        int batchSize = 10;
        List<JSONObject> jsonList = toParamByHeat(heatList);
        List<MovieSolr> newMovies = queryMovieSolrByNameDateBatch(jsonList, batchSize, offset, limit);

        JSONObject enObject = new JSONObject();
        JSONArray dataList = new JSONArray();
        enObject.put("dataList", dataList);
        enObject.put("total", resp.getResults().getNumFound());
        for (MovieSolr ms : newMovies) {
            if (StringUtils.isBlank(ms.getDirectors())) {
                continue;
            }
            dataList.add(ms.getId());
            if (dataList.size() >= destCount) {
                break;
            }
        }
        return enObject;
    }

    /**
     * douban-movieheat
     * 
     * @param newSolrs
     * @return
     */
    private List<JSONObject> toParamByHeat(List<DataSolr> newSolrs) {
        List<JSONObject> paramList = Lists.newArrayList();
        for (DataSolr solr : newSolrs) {
            JSONObject pObj = new JSONObject();
            pObj.put("name", solr.getTitle());
            try {
                JSONObject ctObject = JSON.parseObject(solr.getContent());
                String sYear = ctObject.getString("release_year");
                if (StringUtils.isBlank(sYear)) {
                    continue;
                }
                Date date = DateUtils.parseDate(sYear, "yyyy");
                pObj.put("date", date);
                paramList.add(pObj);
            } catch (Exception e) {
                log.warn("error group:" + solr.getGroup() + ",id:" + solr.getId());
                e.printStackTrace();
            }
        }
        return paramList;
    }

    private JSONObject createRankContentByGroup(String group, Integer destCount, Integer offset, Integer limit)
            throws Exception {
        List<DataSolr> enRanks = queryDataSolrMovieRanks(group, offset, limit);
        List<JSONObject> jsonList = toParamByRank(enRanks);
        List<MovieSolr> enRankMovies = queryMovieSolrByNameDateBatch(jsonList, 10, offset, limit);
        JSONObject enObject = new JSONObject();
        JSONArray dataList = new JSONArray();
        JSONArray nameList = new JSONArray();
        enObject.put("dataList", dataList);
        for (MovieSolr ms : enRankMovies) {
            if (StringUtils.isBlank(ms.getDirectors())) {
                continue;
            }
            nameList.add(ms.getName());
            dataList.add(ms.getId());
            if (dataList.size() >= destCount) {
                break;
            }
        }
        return enObject;
    }

    private void addDailyMovieHomeByGroup(String sGroup, String content, List<SolrDocVo> docsList) {
        Date current = new Date();
        String sDate = DateFormatUtils.format(current, "yyyyMMdd");
        String idString = sGroup + MOVIE_SPLITOR + sDate;
        addMovieHomeByGroup(idString, sGroup, content, docsList);
    }

    private void addWeekMovieHomeByGroup(String sGroup, String content, List<SolrDocVo> docsList) {
        // 每周周五，增加一条记录
        int destWeek = 6;
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int days = destWeek - week;
        days = days <= 0 ? days : days - 7;
        Date current = new Date();
        Date lastFridDate = DateUtils.addDays(current, days);
        String sDate = DateFormatUtils.format(lastFridDate, "yyyyMMdd");
        String idString = sGroup + MOVIE_SPLITOR + sDate;
        addMovieHomeByGroup(idString, sGroup, content, docsList);
    }

    private void addMovieHomeByGroup(String idString, String sGroup, String content, List<SolrDocVo> docsList) {
        SolrDocVo enRankDoc = new SolrDocVo();
        enRankDoc.addField(new SolrFieldVo("type", "idober-movie-home"));
        enRankDoc.addField(new SolrFieldVo("id", idString));
        enRankDoc.addField(new SolrFieldVo("group", sGroup));
        enRankDoc.addField(new SolrFieldVo("content", content));
        docsList.add(enRankDoc);
    }

    private JSONObject getBuildResult(List<SolrDocVo> docsList) {
        JSONObject rsObject = new JSONObject();
        for (SolrDocVo doc : docsList) {
            String key = "";
            String value = "0";
            for (SolrFieldVo fl : doc.getFields()) {
                if ("id".equals(fl.getKey())) {
                    key = fl.getValue();
                } else if ("content".equals(fl.getKey())) {
                    JSONObject dObject = JSON.parseObject(fl.getValue());
                    value = "" + dObject.getJSONArray("dataList").size();
                }
            }
            rsObject.put(key, value);
        }
        return rsObject;
    }

    private List<JSONObject> toParamByRank(List<DataSolr> newSolrs) {
        List<JSONObject> paramList = Lists.newArrayList();
        Pattern oDateReg = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
        for (DataSolr rs : newSolrs) {
            JSONObject pObj = new JSONObject();
            pObj.put("name", rs.getTitle());
            try {
                JSONObject mObject = JSON.parseObject(rs.getContent());
                String sDate = mObject.getString("date");
                sDate = sDate != null ? sDate : mObject.getString("time");
                if (StringUtils.isEmpty(sDate)) {
                    String sYear = mObject.getString("year");
                    if (StringUtils.isEmpty(sYear)) {
                        log.warn("no date or year.id:" + rs.getId() + ",title:" + rs.getTitle());
                        continue;
                    }
                    sDate = sYear + "-01-01";
                }
                sDate = sDate.replace("年", "-");
                sDate = sDate.replace("月", "-");
                Matcher matcher = oDateReg.matcher(sDate);
                if (matcher.find()) {
                    sDate = matcher.group();
                } else {
                    log.warn("error date:" + sDate + ",id:" + rs.getId() + ",title:" + rs.getTitle());
                    continue;
                }
                Date date = DateUtils.parseDate(sDate, "yyyy-MM-dd");
                pObj.put("date", date);
                paramList.add(pObj);
            } catch (Exception e) {
                log.warn("error group:" + rs.getGroup() + ",id:" + rs.getId());
                e.printStackTrace();
            }
        }
        return paramList;
    }

    private List<DataSolr> queryDataSolrMovieRanks(String group, Integer offset, Integer limit) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "(type:douban-movierank AND group:" + group + ")");
        solrQuery.setSort("ranking", ORDER.asc);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        return resp.getBeans(DataSolr.class);
    }

    private List<MovieSolr> queryMovieSolrByNameDateBatch(List<JSONObject> jsonList, Integer batchSize, Integer offset,
            Integer limit)
            throws Exception {
        BatchIterator<JSONObject> it = new BatchIterator<JSONObject>(jsonList, batchSize);
        List<MovieSolr> movieSolrs = Lists.newArrayList();
        while (it.hasNext()) {
            List<JSONObject> querySolrs = it.next();
            List<MovieSolr> hasList = queryMovieSolrByNameDates(querySolrs, offset, limit);
            if (CollectionUtils.isNotEmpty(hasList)) {
                movieSolrs.addAll(hasList);
            }
        }
        return movieSolrs;
    }

    private List<MovieSolr> queryMovieSolrByNameDates(List<JSONObject> jsonList,
            Integer offset, Integer limit) throws Exception {
        if (CollectionUtils.isEmpty(jsonList)) {
            return Collections.emptyList();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (JSONObject rs : jsonList) {
            String title = rs.getString("name");
            title = ClientUtils.escapeQueryChars(title);
            Date date = rs.getDate("date");
            Date fromDate = DateUtils.addMonths(date, -3);
            Date toDate = DateUtils.addMonths(date, 3);
            // String sFromDate = TrieDateField.formatExternal(fromDate);
            String sFromDate = DateUtil.getThreadLocalDateFormat().format(fromDate);
            String sToDate = DateUtil.getThreadLocalDateFormat().format(toDate);
            if (sb.length() > 1) {
                sb.append(" OR ");
            }
            sb.append("(names:");
            sb.append(title);
            sb.append(" AND date:[");
            sb.append(sFromDate);
            sb.append(" TO ");
            sb.append(sToDate);
            sb.append("])");
        }
        sb.append(")");
        if (sb.length() <= 2) {
            return Collections.emptyList();
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", sb.toString());
        solrQuery.addField(MovieSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
        return resp.getBeans(MovieSolr.class);
    }

}
