package com.lezo.idober.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
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
import com.google.common.collect.Sets;
import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrConstant;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ActionReturnVo;
import com.lezo.idober.vo.SolrDocListVo;
import com.lezo.idober.vo.SolrDocVo;
import com.lezo.idober.vo.SolrFieldVo;

@Log4j
@RequestMapping("doc")
@Controller
public class DocumentController {

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
            SolrServer server = SolrUtils.getSolrServer(core);
            if ("1".equals(docs.getType())) {
                int count = 0;
                for (SolrDocVo docVo : docs.getDocs()) {
                    SolrInputDocument doc = new SolrInputDocument();
                    for (SolrFieldVo fld : docVo.getFields()) {
                        doc.addField(fld.getKey(), fld.getValue());
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

    @RequestMapping(value = "movie", method = RequestMethod.POST)
    @ResponseBody
    public ActionReturnVo updateMovieDocs() {
        ActionReturnVo returnVo = new ActionReturnVo();
        try {
            long startMills = System.currentTimeMillis();
            int total = 0;
            int hasTorrentCount = 0;
            Integer offset = 0;
            Integer limit = 100;
            SolrServer movieServer = SolrUtils.getMovieServer();
            while (true) {
                List<SolrInputDocument> docList = Lists.newArrayList();
                List<DataSolr> metaSolrs = queryMovieMetaDataSolrs(offset, limit);
                offset += metaSolrs.size();
                for (DataSolr metaSolr : metaSolrs) {
                    JSONObject metaObject = JSON.parseObject(metaSolr.getContent());
                    SolrInputDocument doc = new SolrInputDocument();

                    doc.addField("name", metaObject.remove("name"));
                    doc.addField("enname", metaObject.remove("enname"));
                    doc.addField("region", metaObject.remove("region"));
                    Pattern oNumReg = Pattern.compile("[0-9]{4}");
                    String sPlayYear = metaObject.getString("year");
                    sPlayYear = sPlayYear == null ? "1999" : sPlayYear;
                    Matcher matcher = oNumReg.matcher(sPlayYear);
                    Integer year = 1999;
                    if (matcher.find()) {
                        year = Integer.parseInt(matcher.group());
                    }
                    metaObject.remove("year");
                    doc.addField("year", year);
                    String sDate = metaObject.getString("play_date");
                    if (StringUtils.isEmpty(sDate)) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.MILLISECOND, 0);
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, 0);
                        c.set(Calendar.DAY_OF_MONTH, 1);
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        doc.addField("date", c.getTime());
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                        doc.addField("date", sdf.parse(sDate));
                        metaObject.remove("play_date");
                    }
                    String idString = getIdString(doc);

                    doc.addField("id", idString);

                    String sValue = null;
                    sValue = mergeValues(metaObject, "directors", "name");
                    doc.addField("directors", sValue);

                    sValue = mergeValues(metaObject, "main_actors", "cnname");
                    doc.addField("actors", sValue);

                    sValue = mergeValues(metaObject, "genres", null);
                    doc.addField("genres", sValue);
                    metaObject.remove("genres");

                    Set<String> nameSet = Sets.newHashSet();
                    Object vObject = doc.getFieldValue("name");
                    if (vObject != null) {
                        String sVal = vObject.toString();
                        if (StringUtils.isNotBlank(sVal)) {
                            nameSet.add(sVal);
                        }
                    }
                    vObject = doc.getFieldValue("enname");
                    if (vObject != null) {
                        String sVal = vObject.toString();
                        if (StringUtils.isNotBlank(sVal)) {
                            nameSet.add(sVal);
                        }
                    }
                    JSONArray nameArr = metaObject.getJSONArray("alias_names");
                    if (nameArr != null) {
                        for (int i = 0; i < nameArr.size(); i++) {
                            String sVal = nameArr.getString(i);
                            if (StringUtils.isNotBlank(sVal)) {
                                nameSet.add(sValue);
                            }
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for (String sName : nameSet) {
                        if (sb.length() > 0) {
                            sb.append(MovieSolr.TEXT_SPLITOR);
                        }
                        sb.append(sName);
                    }
                    doc.addField("names", sb.toString());
                    metaObject.remove("alias_names");

                    doc.addField("score", metaObject.remove("score"));
                    doc.addField("imgUrl", metaObject.remove("img_url"));
                    doc.addField("content", metaObject.toJSONString());

                    JSONArray torrents = new JSONArray();
                    String sName = metaSolr.getTitle();
                    List<DataSolr> torrentSolrs = queryMovieTorrentDataSolrs(sName);
                    if (CollectionUtils.isNotEmpty(torrentSolrs)) {
                        hasTorrentCount++;
                        Set<String> codeSet = Sets.newHashSet();
                        for (DataSolr tSolr : torrentSolrs) {
                            JSONObject tObject = JSON.parseObject(tSolr.getContent());
                            String sYear = tObject.getString("year");
                            if (StringUtils.isBlank(sYear) || !sYear.equals(year.toString())) {
                                continue;
                            }
                            JSONArray tArray = tObject.getJSONArray("torrents");
                            if (tArray == null) {
                                continue;
                            }
                            for (int i = 0; i < tArray.size(); i++) {
                                JSONObject tdObj = tArray.getJSONObject(i);
                                String sUrl = tdObj.getString("url");
                                if ("bttiantang-torrent".equals(tSolr.getType())) {
                                    String uhash = tdObj.getJSONObject("param").getString("uhash");
                                    tdObj.put("type", "bt-link");
                                    sUrl += uhash;
                                }
                                if (!codeSet.contains(sUrl)) {
                                    torrents.add(tdObj);
                                    codeSet.add(sUrl);
                                }
                            }
                        }
                    }
                    doc.addField("tcount", torrents.size());
                    doc.addField("torrents", torrents.toJSONString());
                    docList.add(doc);
                }
                total += metaSolrs.size();
                if (metaSolrs.size() < limit) {
                    break;
                }
                if (!docList.isEmpty()) {
                    movieServer.add(docList);
                    movieServer.commit();
                }
                log.info("create movie doc,total:" + total + ",hasTorrentCount:" + hasTorrentCount);
                // break;
            }
            // movieServer.commit();
            long costMills = System.currentTimeMillis() - startMills;
            log.info("create movie core document,total:" + total + ",hasTorrentCount:" + hasTorrentCount + ",cost:"
                    + costMills);
        } catch (Exception e) {
            log.warn("update movie doc,cause:", e);
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
            returnVo.setMsg(e.getMessage());
        }
        return returnVo;
    }

    private String getIdString(SolrInputDocument doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getFieldValue("name"));
        sb.append(doc.getFieldValue("date"));
        String hCode = "" + sb.toString().hashCode();
        return hCode.replace("-", "H");
    }

    private String mergeValues(JSONObject metaObject, String arrayKey, String valKey) {
        JSONArray dataArray = metaObject.getJSONArray(arrayKey);
        String destVal = StringUtils.EMPTY;
        if (dataArray != null && !dataArray.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dataArray.size(); i++) {
                String sVal = null;
                if (StringUtils.isNotBlank(valKey)) {
                    JSONObject dObject = dataArray.getJSONObject(i);
                    sVal = dObject.getString(valKey);
                } else {
                    sVal = dataArray.getString(i);
                }
                if (StringUtils.isBlank(sVal)) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(MovieSolr.TEXT_SPLITOR);
                }
                sb.append(sVal);
            }
            destVal = sb.toString();
        }
        return destVal;
    }

    private Long getMovieMaxId() throws Exception {
        String queryString = "q=*:*&stats=true&stats.field=id&rows=0&indent=true";
        MultiMapSolrParams params = SolrRequestParsers.parseQueryString(queryString);
        QueryResponse resp = SolrUtils.getMovieServer().query(params);
        log.info("resp:" + JSON.toJSONString(resp));
        return 0L;
    }

    private List<DataSolr> queryMovieTorrentDataSolrs(String sName) throws Exception {
        if (StringUtils.isBlank(sName)) {
            return Collections.emptyList();
        }
        Integer limit = 100;
        sName = ClientUtils.escapeQueryChars(sName);
        String torrentSource = "type:xiamp4-torrent OR type:bttiantang-torrent";
        SolrQuery solrQuery = new SolrQuery(SolrConstant.SORL_QUERY_DEFAULT_FRANGE);
        String queryString = "((" + torrentSource + ") AND search:" + sName + ")";
        solrQuery.setStart(0);
        solrQuery.setRows(limit);
        solrQuery.set("qq", queryString);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        return resp.getBeans(DataSolr.class);
    }

    private List<DataSolr> queryMovieMetaDataSolrs(Integer offset, Integer limit) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "type:mtime-movie");
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        return resp.getBeans(DataSolr.class);
    }
}
