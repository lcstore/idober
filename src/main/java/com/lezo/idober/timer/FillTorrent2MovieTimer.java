package com.lezo.idober.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class FillTorrent2MovieTimer implements Runnable {
    private static final String CORE_NAME_MOVIE = "cmovie";
    private static final String CORE_NAME_META = "cmeta";
    private static String torrentFilterQuery;
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
    private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");
    private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");

    static {
        ArrayList<String> typeList = Lists.newArrayList();
        typeList.add("bttiantang-movie-torrent");
        typeList.add("xiamp4-movie-torrent");
        typeList.add("dy2018-movie-torrent");
        StringBuilder sb = new StringBuilder();
        sb.append("type:(");
        for (int i = 0, len = typeList.size(); i < len; i++) {
            String type = typeList.get(i);
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append(type);
        }
        sb.append(")");
        torrentFilterQuery = sb.toString();
    }

    @Override
    public void run() {
        if (!running.compareAndSet(false, true)) {
            log.warn(this.getClass().getSimpleName() + " is runing...");
            return;
        }
        long startMills = System.currentTimeMillis();
        int total = 0;
        try {
            log.info(this.getClass().getSimpleName() + " is start...");
            String coreName = CORE_NAME_MOVIE;
            SolrServer movieServer = SolrUtils.getSolrServer(coreName);
            coreName = CORE_NAME_META;
            SolrServer metaServer = SolrUtils.getSolrServer(coreName);
            int offset = 0;
            int limit = 100;
            while (true) {
                SolrDocumentList selectDocs = null;
                try {
                    selectDocs = getMovieEmptyTorrentWithLimit(movieServer, offset, limit);
                } catch (Exception e) {
                    log.warn("", e);
                }
                if (selectDocs == null) {
                    break;
                }
                try {
                    fillTorrents(metaServer, movieServer, selectDocs);
                } catch (Exception e) {
                    log.warn("", e);
                }
                if (selectDocs.size() < limit) {
                    break;
                }
                offset += limit;
                total += limit;
            }
        } catch (Exception e) {
            log.warn("", e);
        } finally {
            long costMills = System.currentTimeMillis() - startMills;
            log.warn(this.getClass().getSimpleName() + ".done,cost:" + costMills + ",total:" + total);
            running.set(false);
        }

    }

    private void fillTorrents(SolrServer metaServer, SolrServer movieServer, SolrDocumentList selectDocs)
            throws Exception {
        if (CollectionUtils.isEmpty(selectDocs)) {
            return;
        }
        JSONArray taskList = new JSONArray();
        JSONArray docArray = new JSONArray();
        for (SolrDocument doc : selectDocs) {
            SolrQuery solrQuery = createMetaQuery(doc, false);
            List<SolrDocument> metaDocs = getMetaDocuments(metaServer, solrQuery);
            JSONObject tObject = createTorrentDoc(doc, metaDocs);
            if (tObject == null || tObject.getJSONArray("torrents").isEmpty()) {
                String sId = doc.getFieldValue("id").toString();
                String sName = doc.getFieldValue("name").toString();
                JSONObject taskObj = new JSONObject();
                taskObj.put("type", "query-movie");
                taskObj.put("url", "");
                taskObj.put("level", 1000);
                JSONObject argsObj = new JSONObject();
                argsObj.put("title", sName);
                argsObj.put("mid", sId);
                argsObj.put("retry", "0");
                taskObj.put("args", argsObj);
                taskList.add(taskObj);
                log.warn("torrent.empty[" + doc.getFirstValue("id") + "],name:" + doc.getFirstValue("name"));
            }
            if (tObject != null) {
                docArray.add(tObject);
            }
        }
        if (!docArray.isEmpty() || docArray.isEmpty()) {
            String sContent = docArray.toJSONString();
            String contentType = "application/json";
            Collection<ContentStream> strems = ClientUtils.toContentStreams(sContent, contentType);
            ContentStreamUpdateRequest request =
                    new ContentStreamUpdateRequest("/update/json");
            for (ContentStream cs : strems) {
                request.addContentStream(cs);
            }
            request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
            NamedList<Object> resp = movieServer.request(request);
            log.info("resp:" + resp.size() + ",update:" + docArray.size());
        }
        createTasks(taskList);
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
        log.info("task:" + taskArray.size() + ",resp:" + resp.body());
    }

    private JSONObject createTorrentDoc(SolrDocument doc, List<SolrDocument> metaDocs) {
        if (CollectionUtils.isEmpty(metaDocs)) {
            return null;
        }
        JSONObject docObject = new JSONObject();
        String idString = doc.getFieldValue("id").toString();
        docObject.put("id", idString);

        Map<String, Object> fieldMap = Maps.newHashMap();
        fieldMap.put("set", doc.getFieldValue("code_s"));
        docObject.put("code_s", fieldMap);
        JSONArray tArray = new JSONArray();

        // String srcContent = doc.getFieldValue("content").toString();
        // JSONObject srcObject = JSONObject.parseObject(srcContent);
        // String sImdb = srcObject.getString("imdb");
        for (SolrDocument mDoc : metaDocs) {
            // 1.不匹配数据过滤
            if (!isSameDoc(doc, mDoc)) {
                continue;
            }
            Object mContentObj = mDoc.getFieldValue("content");
            String sContent = mContentObj.toString();
            JSONObject ctObject = JSONObject.parseObject(sContent);
            JSONArray srcArray = ctObject.getJSONArray("torrents");
            if (srcArray != null) {
                // TODO: 2.匹配数据合并
                for (int index = 0, len = srcArray.size(); index < len; index++) {
                    Object srcObj = srcArray.get(index);
                    tArray.add(srcObj.toString());
                }
            }

        }
        if (tArray.isEmpty()) {
            // return null;
        }
        docObject.put("torrents", tArray);
        return docObject;
    }

    private boolean isSameDoc(SolrDocument doc, SolrDocument mDoc) {
        Boolean bSame = isSameImdb(doc, mDoc);
        if (bSame != null) {
            return bSame;
        }
        bSame = hasSameDirector(doc, mDoc);
        if (bSame != null) {
            return bSame;
        }
        bSame = hasSameActor(doc, mDoc);
        bSame = bSame == null ? false : bSame;
        return bSame;
    }

    private Boolean hasSameActor(SolrDocument doc, SolrDocument mDoc) {
        Object mContentObj = mDoc.getFieldValue("content");
        String sContent = mContentObj.toString();
        JSONObject ctObject = JSONObject.parseObject(sContent);
        String actors = ctObject.getString("actors");
        Boolean bSame = null;
        if (StringUtils.isBlank(actors)) {
            return bSame;
        }
        Collection<Object> actorList = doc.getFieldValues("actors");
        if (CollectionUtils.isEmpty(actorList)) {
            return bSame;
        }
        bSame = false;
        for (Object aObj : actorList) {
            if (actors.contains(aObj.toString())) {
                bSame = true;
                break;
            }
        }
        return bSame;
    }

    private Boolean isSameImdb(SolrDocument doc, SolrDocument mDoc) {
        String srcContent = doc.getFieldValue("content").toString();
        JSONObject srcObject = JSONObject.parseObject(srcContent);
        String sImdb = srcObject.getString("imdb");
        if (StringUtils.isBlank(sImdb)) {
            return null;
        }
        Object imdbObj = mDoc.getFieldValue("imdb_txt");
        if (imdbObj == null) {
            return null;
        }
        Boolean bSame = null;
        if (imdbObj != null && StringUtils.isNotBlank(sImdb)) {
            String sImdbTxt = imdbObj.toString();
            if (StringUtils.isNotBlank(sImdbTxt) &&
                    !sImdbTxt.equals("imdb")) {
                bSame = sImdb.contains(sImdbTxt);
            }
        }
        return bSame;
    }

    private boolean hasSameRelease(SolrDocument doc, SolrDocument mDoc) {
        Object referObject = doc.getFieldValue("release");
        Object srcObject = mDoc.getFieldValue("release_tdt");
        if (referObject == null || srcObject == null) {
            return true;
        }
        Date referDate = (Date) referObject;
        Date srcDate = (Date) srcObject;
        Date fromDate = DateUtils.addMonths(referDate, -12);
        Date toDate = DateUtils.addMonths(referDate, 12);
        boolean hasSame = srcDate.getTime() >= fromDate.getTime() && srcDate.getTime() <= toDate.getTime();
        return hasSame;
    }

    @SuppressWarnings("unchecked")
    private Boolean hasSameDirector(SolrDocument doc, SolrDocument mDoc) {
        Object srcObject = mDoc.getFieldValue("director_qtxt");
        if (srcObject == null) {
            return null;
        }
        String srcDirector = srcObject.toString();
        if (srcDirector.equals("-")) {
            return null;
        }
        Object referObject = doc.getFieldValue("directors");
        if (referObject == null) {
            return null;
        }
        boolean hasSame = false;
        List<String> directors = (List<String>) referObject;
        for (String director : directors) {
            if (srcDirector.contains(director)) {
                hasSame = true;
                break;
            }
        }
        return hasSame;
    }

    private List<SolrDocument> getMetaDocuments(SolrServer metaServer, SolrQuery solrQuery) {
        try {
            QueryResponse sResp = metaServer.query(solrQuery);
            return sResp.getResults();
        } catch (SolrServerException e) {
            log.warn("", e);
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private SolrQuery createMetaQuery(SolrDocument doc, boolean useDirector) {
        int offset = 0;
        int limit = 100;

        List<String> missFields = Lists.newArrayList();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        StringBuilder sb = new StringBuilder();
        String sName = doc.getFieldValue("name").toString();
        sName = SYMBOL_REG.matcher(sName).replaceAll(" ");
        sName = IGNORE_REG.matcher(sName).replaceAll(" ");
        Matcher matcher = CN_NAME_REG.matcher(sName);
        if (matcher.find()) {
            sName = matcher.group(1).trim();
        }
        sName = sName.trim();
        sb.append(sName);
        if (useDirector) {
            List<String> directors = (List<String>) doc.getFieldValue("directors");
            if (CollectionUtils.isNotEmpty(directors)) {
                sb.append(" ");
                sb.append(directors.get(0));
            } else {
                missFields.add("directors");
            }
        }
        String sQuery = ClientUtils.escapeQueryChars(sb.toString());
        solrQuery.set("q", sQuery);
        solrQuery.addFilterQuery(torrentFilterQuery);
        Object yearObj = doc.getFieldValue("year");
        if (yearObj != null) {
            solrQuery.addFilterQuery("year_ti:" + yearObj);
        } else {
            missFields.add("year");
        }
        if (!missFields.isEmpty()) {
            String idString = doc.getFieldValue("id").toString();
            log.warn("cmovie(" + idString + "),checks:" + ArrayUtils.toString(missFields));
        }
        return solrQuery;
    }

    private SolrDocumentList getMovieEmptyTorrentWithLimit(SolrServer movieServer, int offset, int limit)
            throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "tcount:0");
        // solrQuery.set("q", "id:1094876573");
        QueryResponse resp = movieServer.query(solrQuery);
        return resp.getResults();
    }

}
