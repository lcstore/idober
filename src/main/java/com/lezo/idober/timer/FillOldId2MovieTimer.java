package com.lezo.idober.timer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.utils.SolrUtils;

@Log4j
public class FillOldId2MovieTimer implements Runnable {
    private static final String CORE_NAME_MOVIE = "cmovie";
    private static final String CORE_NAME_OLD = "core2";
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
    private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");
    private static final Pattern IGNORE_REG = Pattern.compile("(第[一二三四五六七八九0-9][部季集])");

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
            coreName = CORE_NAME_OLD;
            SolrServer metaServer = SolrUtils.getSolrServer(coreName);
            int offset = 0;
            int limit = 100;
            while (true) {
                SolrDocumentList selectDocs = null;
                try {
                    selectDocs = getMovieWithLimit(movieServer, offset, limit);
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
        JSONArray docArray = new JSONArray();
        for (SolrDocument doc : selectDocs) {
            SolrQuery solrQuery = createMetaQuery(doc, false);
            List<SolrDocument> metaDocs = getMetaDocuments(metaServer, solrQuery);
            JSONObject tObject = createTorrentDoc(doc, metaDocs);
            if (tObject == null) {
                log.warn("no oldMovie[" + doc.getFirstValue("id") + "],name:" + doc.getFirstValue("name"));
            } else {
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
    }

    private JSONObject createTorrentDoc(SolrDocument doc, List<SolrDocument> metaDocs) {
        if (CollectionUtils.isEmpty(metaDocs)) {
            return null;
        }
        JSONObject docObject = new JSONObject();
        String idString = doc.getFieldValue("id").toString();
        docObject.put("id", idString);

        Map<String, Object> fieldMap = Maps.newHashMap();
        docObject.put("old_id_s", fieldMap);

        for (SolrDocument mDoc : metaDocs) {
            // 1.不匹配数据过滤
            if (!isSameDoc(doc, mDoc)) {
                continue;
            }
            fieldMap.put("set", mDoc.getFieldValue("id"));
            break;

        }
        if (fieldMap.isEmpty()) {
            return null;
        }
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
        Collection<Object> actorList = mDoc.getFieldValues("actors");
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

    @SuppressWarnings("unchecked")
    private Boolean hasSameDirector(SolrDocument doc, SolrDocument mDoc) {
        Object srcObject = mDoc.getFieldValue("directors");
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
        String sQuery = ClientUtils.escapeQueryChars(sb.toString());
        solrQuery.set("q", sQuery);
        Object yearObj = doc.getFieldValue("year");
        if (yearObj != null) {
            solrQuery.addFilterQuery("year:" + yearObj);
        } else {
            missFields.add("year");
        }
        if (!missFields.isEmpty()) {
            String idString = doc.getFieldValue("id").toString();
            log.warn("cmovie(" + idString + "),checks:" + ArrayUtils.toString(missFields));
        }
        return solrQuery;
    }

    private SolrDocumentList getMovieWithLimit(SolrServer movieServer, int offset, int limit)
            throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "-old_id_s:[\"\" TO *]");
        QueryResponse resp = movieServer.query(solrQuery);
        return resp.getResults();
    }

}
