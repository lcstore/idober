package com.lezo.idober.vo;

import java.util.List;

import lombok.Data;

import org.apache.solr.common.SolrInputDocument;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

@Data
public class SolrDocVo {
    private List<SolrFieldVo> fields;

    public void addField(SolrFieldVo fieldVo) {
        if (fieldVo == null) {
            return;
        }
        if (fields == null) {
            fields = Lists.newArrayList();
        }
        fields.add(fieldVo);
    }

    private SolrInputDocument createDocument(JSONObject record) {
        String splitor = ";";
        String type = "bdp-share";
        SolrInputDocument doc = new SolrInputDocument();
        String idChars = type + splitor + record.getString("source_id");
        doc.addField("id", idChars);
        doc.addField("type", type);
        doc.addField("title", record.getString("title"));
        StringBuilder sb = new StringBuilder();
        sb.append(record.getString("title"));
        sb.append(splitor);
        sb.append(record.getString("uk"));
        sb.append(splitor);
        sb.append(record.getString("username"));
        sb.append(splitor);
        sb.append(record.getString("desc"));
        doc.addField("search", sb.toString());
        doc.addField("code", record.getString("uk"));
        record.remove("filelist");
        doc.addField("content", record.toJSONString());
        return doc;
    }
}
