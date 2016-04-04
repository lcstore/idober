package com.lezo.idober.solr.pojo;

import java.util.Date;
import java.util.List;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

import com.lezo.idober.utils.AESCodecUtils;
import com.lezo.idober.utils.SolrUtils;

@Data
public class MovieSolr {
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
    @Field
    private Date timestamp;
    @Field
    private List<Date> creation;

    public static String getSolrFields() {
        return SolrUtils.getSolrFields(MovieSolr.class);
    }

    public String getEncryptId() {
        if (id != null) {
            try {
                return AESCodecUtils.encrypt(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public String getId() {
        return id;
    }
}
