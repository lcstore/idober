package com.lezo.idober.vo;

import java.util.Date;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class ItemVo {
    @Field
    private String itemCode;
    @Field
    private String matchCode;
    @Field
    private String productName;
    @Field
    private String wareCode;
    @Field
    private Float minPrice;
    @Field
    private Float maxPrice;
    @Field
    private String imgUrl;
    @Field
    private String tokenCategory;
    @Field
    private String tokenBrand;
    @Field
    private String tokenModel;
    @Field
    private String tokenUnit;
    @Field
    private String categoryNav;
    @Field
    private Date createTime;
    @Field
    private Date updateTime;
}
