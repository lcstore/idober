package com.lezo.idober.solr;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.solr.pojo.ItemSolr;
import com.lezo.idober.vo.SolrDocListVo;
import com.lezo.idober.vo.SolrDocVo;
import com.lezo.idober.vo.SolrFieldVo;

public class SolrQueryTest {
    HttpSolrServer server;

    @Before
    public void setup() throws Exception {
        server = new HttpSolrServer("http://www.lezomao.com/core0");
        System.setProperty("solr.solr.home", "/apps/src/istore/solr_home");
        // CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        // CoreContainer coreContainer = initializer.initialize();
        // // server = new EmbeddedSolrServer(coreContainer, "collection1");
        // server = new EmbeddedSolrServer(coreContainer, "core0");
    }

    @Test
    public void testQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery("牛奶");
        String queryString =
                "group=true&group.field=itemCode&group.query=stockNum:[1%20TO%20*]&group.main=true&group.sort=commentNum%20desc&group.sort=score%20desc";
        SolrParams params = SolrRequestParsers.parseQueryString(queryString);
        solrQuery.add(params);
        solrQuery.add("group.offset", "0");
        solrQuery.add("group.limit", Integer.MAX_VALUE + "");
        StringBuilder sb = new StringBuilder();
        for (Field fld : ItemSolr.class.getDeclaredFields()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(fld.getName());
        }
        solrQuery.addField(sb.toString());
        // QueryRequest request = new QueryRequest(solrQuery);
        // QueryResponse resp = request.process(server);
        QueryResponse response = server.query(solrQuery);
        System.err.println(JSON.toJSONString(response.getResponse()));
        // List<ItemSolr> itemList = response.getBeans(ItemSolr.class);
        // System.err.println(JSON.toJSONString(itemList));
    }

    @Test
    public void testQueryId() throws Exception {
        SolrQuery solrQuery = new SolrQuery("q=(type:mtime-movie)");
        solrQuery.setFields("id");
        StringBuilder sb = new StringBuilder();
        solrQuery.setStart(0);
        solrQuery.setRows(1000);
        while (true) {
            // QueryRequest request = new QueryRequest(solrQuery);
            // QueryResponse resp = request.process(server);
            QueryResponse response = server.query(solrQuery);
            for (SolrDocument rs : response.getResults()) {
                String idString = rs.getFieldValue("id").toString();
                idString = idString.split(";")[1];
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(idString);
            }
            sb.append("\n");
            if (response.getResults().size() < solrQuery.getRows()) {
                break;
            }
            solrQuery.setStart(solrQuery.getStart() + solrQuery.getRows());
        }

        System.err.println(sb);
        File file = new File("./test.txt");
        FileUtils.writeStringToFile(file, sb.toString());
        // System.err.println(JSON.toJSONString(response.getResults()));
        // List<ItemSolr> itemList = response.getBeans(ItemSolr.class);
        // System.err.println(JSON.toJSONString(itemList));
    }

    @Test
    public void testSolrAddBdpShareByDir() throws Exception {
        String path = "/apps/data/snapper/bdp-share";
        File dirFile = new File(path);
        File[] dataFiles = dirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".data");
            }
        });
        if (dataFiles == null) {
            return;
        }
        int count = 0;
        for (File df : dataFiles) {
            List<String> lines = FileUtils.readLines(df);
            for (String line : lines) {
                JSONObject gObj = JSON.parseObject(line);
                String dChars = gObj.getString("data");
                JSONObject dObject = JSON.parseObject(dChars);
                dObject = dObject.getJSONObject("data");
                JSONObject shareObj = dObject.getJSONObject("share");
                JSONArray records = shareObj.getJSONArray("records");
                if (records == null) {
                    return;
                }
                for (int i = 0; i < records.size(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    SolrInputDocument doc = createDocument(record);
                    return;
                    // server.add(doc);
                    // count++;
                }
            }
            if (count >= 500) {
                server.commit();
                count = 0;
            }
        }
        if (count > 0) {
            server.commit();
        }
    }

    private SolrInputDocument createDocument(JSONObject record) {
        String splitor = ";";
        String type = "bdp-share";
        SolrDocListVo docListVo = new SolrDocListVo();
        docListVo.setType("1");
        List<SolrDocVo> docs = Lists.newArrayList();
        docListVo.setDocs(docs);
        SolrDocVo docVo = new SolrDocVo();
        docs.add(docVo);

        String idChars = type + splitor + record.getString("source_id");
        docVo.addField(new SolrFieldVo("id", idChars));
        docVo.addField(new SolrFieldVo("type", type));
        docVo.addField(new SolrFieldVo("title", record.getString("title")));
        StringBuilder sb = new StringBuilder();
        sb.append(record.getString("title"));
        sb.append(splitor);
        sb.append(record.getString("uk"));
        sb.append(splitor);
        sb.append(record.getString("username"));
        sb.append(splitor);
        sb.append(record.getString("desc"));
        docVo.addField(new SolrFieldVo("search", sb.toString()));
        docVo.addField(new SolrFieldVo("code", record.getString("uk")));
        record.remove("filelist");
        docVo.addField(new SolrFieldVo("content", record.toJSONString()));

        SolrInputDocument doc = new SolrInputDocument();
        for (SolrFieldVo fld : docVo.getFields()) {
            doc.addField(fld.getKey(), fld.getValue());
        }
        System.err.println(JSON.toJSONString(docListVo));
        return doc;
    }

    @Test
    public void testSolrDelete() throws Exception {
        String queryStr = "*:*";
        server.deleteByQuery(queryStr);
        server.commit();
        server.optimize();
    }
}
