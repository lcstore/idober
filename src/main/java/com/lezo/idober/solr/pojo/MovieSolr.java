package com.lezo.idober.solr.pojo;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class MovieSolr {
    private static String SEARCH_FIELDS;
    @Field
    private String id;
    @Field
    private String type;
    @Field
    private String title;
    @Field
    private String code;
    @Field
    private String content;

    public static String getSolrFields() {
        if (SEARCH_FIELDS == null) {
            synchronized (MovieSolr.class) {
                if (SEARCH_FIELDS == null) {
                    StringBuilder sb = new StringBuilder();
                    for (java.lang.reflect.Field fld : MovieSolr.class.getDeclaredFields()) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(fld.getName());
                    }
                    SEARCH_FIELDS = sb.toString();
                }
            }
        }
        return SEARCH_FIELDS;
    }
}
