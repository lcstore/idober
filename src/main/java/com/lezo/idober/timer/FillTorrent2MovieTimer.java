package com.lezo.idober.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
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
import com.google.common.collect.Sets;
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TypeUtils;

@Log4j
public class FillTorrent2MovieTimer implements Runnable {
    private static final String CORE_NAME_MOVIE = "cmovie";
    private static final String CORE_NAME_META = "cmeta";
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static final Pattern CN_BLANK_REG = Pattern.compile("([\u4e00-\u9fa50-9]+\\s+)");
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
            coreName = CORE_NAME_META;
            SolrServer metaServer = SolrUtils.getSolrServer(coreName);
            int limit = 100;
            String fromId = "0";
            SolrDocumentList selectDocs = null;
            while (true) {
                try {
                    selectDocs = getEmptyTorrentMovieByIdWithLimit(movieServer, fromId,
                            limit);
                    // selectDocs = getMovieByIdWithLimit(movieServer, fromId,
                    // limit);
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
                } else {
                    for (SolrDocument doc : selectDocs) {
                        String curId = doc.getFieldValue("id").toString();
                        if (fromId.compareTo(curId) < 0) {
                            fromId = curId;
                        }
                    }
                }
                total += selectDocs.size();
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
        if (CollectionUtils.isEmpty(docArray)) {
            return;
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
        NamedList<Object> resp = movieServer.request(request);
        log.info("resp:" + resp.size() + ",update:" + docArray.size());
    }

    private JSONObject createTorrentDoc(SolrDocument doc, List<SolrDocument> metaDocs) {
        JSONObject docObject = new JSONObject();
        String idString = doc.getFieldValue("id").toString();
        docObject.put("id", idString);
        Map<String, Object> fieldMap = Maps.newHashMap();
        fieldMap.put("set", doc.getFieldValue("code_s"));
        docObject.put("code_s", fieldMap);
        docObject.put("had_move_s", 0);
        JSONArray tArray = new JSONArray();
        if (CollectionUtils.isNotEmpty(metaDocs)) {
            for (SolrDocument mDoc : metaDocs) {
                // 1.不匹配数据过滤
                if (!DocUtils.isSameMovieDoc(doc, mDoc)) {
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
                        String srcTor = srcObj.toString();
                        JSONObject torObj = JSONObject.parseObject(srcTor);
                        torObj.remove("data");
                        String type = torObj.getString("type");
                        String url = torObj.getString("url");
                        type = type == null ? "" : type;
                        url = url == null ? "" : url;
                        if (url.contains("bbs.rarbt.com")) {
                            continue;
                        }
                        if (!type.contains("share") || url.contains(".baidu.com")) {
                            tArray.add(torObj.toJSONString());
                        }
                    }
                }
            }
        }
        if (tArray.isEmpty()) {
            // return null;
        }
        docObject.put("torrents", tArray);
        return docObject;
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

    private SolrQuery createMetaQuery(SolrDocument doc, boolean useDirector) {
        int offset = 0;
        int limit = 100;
        Collection<Object> nameList = doc.getFieldValues("names");
        Set<Object> nameSet = Sets.newHashSet();
        nameSet.add(doc.getFieldValue("name"));
        if (nameList != null) {
            nameSet.addAll(nameList);
        }
        String sHead = "(";
        StringBuilder sb = new StringBuilder(sHead);
        for (Object nameObj : nameSet) {
            if (nameObj == null) {
                continue;
            }
            String sName = nameObj.toString().trim();
            List<String> partList = Lists.newArrayList();
            while (true) {
                Matcher matcher = CN_BLANK_REG.matcher(sName);
                if (matcher.find()) {
                    String sPart = matcher.group().trim();
                    partList.add(sPart);
                    sName = matcher.replaceFirst("");
                } else {
                    if (StringUtils.isNotBlank(sName)) {
                        partList.add(sName);
                    }
                    break;
                }

            }

            if (partList.isEmpty()) {
                continue;
            }
            for (String sPart : partList) {
                sPart = IGNORE_REG.matcher(sPart).replaceAll(" ");
                sPart = sPart.trim();
                if (StringUtils.isBlank(sPart)) {
                    continue;
                }
                if (sb.length() > sHead.length()) {
                    sb.append(" OR ");
                }
                sPart = ClientUtils.escapeQueryChars(sPart);
                sb.append(sPart);
            }
        }
        sb.append(")");
        String sQuery = sb.toString();
        if (sb.length() < 3) {
            sQuery = ClientUtils.escapeQueryChars(doc.getFieldValue("name").toString());
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", sQuery);
        
        StringBuilder typeBuilder = new StringBuilder();
		typeBuilder.append("type:(");
		List<String> typeList = TypeUtils.getTypeList();
		for (int i = 0, len = typeList.size(); i < len; i++) {
			String type = typeList.get(i);
			if (i > 0) {
				typeBuilder.append(" OR ");
			}
			typeBuilder.append(type);
		}
		typeBuilder.append(")");
		
        solrQuery.addFilterQuery(typeBuilder.toString());
        return solrQuery;
    }

    private SolrDocumentList getEmptyTorrentMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
            throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(limit);
        solrQuery.set("q", "id:[" + fromId + " TO *]");
        solrQuery.addSort("id", ORDER.asc);
        solrQuery.addFilterQuery("torrents_size:0");
        // solrQuery.set("q", "id:194531117");
        QueryResponse resp = movieServer.query(solrQuery);
        return resp.getResults();
    }

    private SolrDocumentList getMovieByIdWithLimit(SolrServer movieServer, String fromId, int limit)
            throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(limit);
        solrQuery.set("q", "id:[" + fromId + " TO *]");
        solrQuery.addSort("id", ORDER.asc);
        // solrQuery.addFilterQuery("timestamp:[* TO 2016-11-01T00:00:19.545Z]");
        // solrQuery.addFilterQuery("torrents_size:[1 TO *]");
        // solrQuery.set("q", "id:0406483785");
        QueryResponse resp = movieServer.query(solrQuery);
        return resp.getResults();
    }

}
