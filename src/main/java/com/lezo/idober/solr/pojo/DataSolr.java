package com.lezo.idober.solr.pojo;

import java.util.Date;
import java.util.List;

import lombok.Data;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.lezo.idober.utils.SolrUtils;

@Data
public class DataSolr {
    @Field
    private String id;
    @Field
    private String type;
    @Field
    private String title;
    @Field
    private String group;
    @Field
    private Integer ranking;
    @Field
    private String content;
    @Field
    private Date timestamp;
    @Field
    private List<Date> creation;

    public static String getSolrFields() {
        return SolrUtils.getSolrFields(DataSolr.class);
    }

    public Date getCreation() {
        if (CollectionUtils.isNotEmpty(creation)) {
            return creation.get(0);
        }
        return null;
    }
}
