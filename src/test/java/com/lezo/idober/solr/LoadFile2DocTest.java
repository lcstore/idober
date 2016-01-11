package com.lezo.idober.solr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.vo.SolrDocListVo;
import com.lezo.idober.vo.SolrDocVo;
import com.lezo.idober.vo.SolrFieldVo;

public class LoadFile2DocTest {

    @Test
    public void testCreateDoc() throws Exception {
        // /apps/data/snapper/20151226/bdp-share
        System.setProperty("solr.solr.home", "/apps/src/istore/solr_home");
        HttpSolrServer server = new HttpSolrServer("http://www.lezomao.com/core0");

        String path = "src/test/resources/test.txt";
        File dirFile = new File(path);
        ArrayList<File> dataFiles = Lists.newArrayList(dirFile);
        int total = 0;
        int count = 0;
        for (File df : dataFiles) {
            List<String> lines = FileUtils.readLines(df);
            for (String line : lines) {
                JSONObject gObj = null;
                JSONArray records = null;
                try {
                    gObj = JSON.parseObject(line);
                    String dChars = gObj.getString("data");
                    JSONObject dObject = JSON.parseObject(dChars);
                    dObject = dObject.getJSONObject("data");
                    String shareChars = dObject.getString("share");
                    JSONObject shareObj = JSON.parseObject(shareChars);
                    records = shareObj.getJSONArray("records");
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if (records == null) {
                    continue;
                }
                for (int i = 0; i < records.size(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    SolrInputDocument doc = createDocument(record);
                    server.add(doc);
                    count++;
                    total++;
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
        System.err.println("total:" + total);
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
        return doc;
    }
}
