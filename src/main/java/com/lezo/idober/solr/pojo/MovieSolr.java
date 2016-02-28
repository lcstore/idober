package com.lezo.idober.solr.pojo;

import java.util.Date;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class MovieSolr {
    public static final String TEXT_SPLITOR = ";";
    private static String SEARCH_FIELDS;
    @Field
    private String id;
    @Field
    private String name;
    @Field
    private String enname;
    @Field
    private String imgUrl;
    @Field
    private String region;
    /**
     * 上映时间
     */
    @Field
    private Date date;
    /**
     * 上映年份
     */
    @Field
    private Integer year;
    @Field
    private String directors;
    /**
     * 主演
     */
    @Field
    private String actors;
    /**
     * 电影类型
     */
    @Field
    private String genres;
    /**
     * 电影名称别名,只用于查询
     */
    // @Field
    // private String names;
    @Field
    private Float score;
    /**
     * 种子信息
     */
    @Field
    private String torrents;
    @Field
    private String content;
    @Field
    private int tcount;

    public static String getSolrFields() {
        if (SEARCH_FIELDS == null) {
            synchronized (MovieSolr.class) {
                if (SEARCH_FIELDS == null) {
                    StringBuilder sb = new StringBuilder();
                    for (java.lang.reflect.Field fld : MovieSolr.class.getDeclaredFields()) {
                        if (fld.getName().equals("content")) {
                            continue;
                        }
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
