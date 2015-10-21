package com.lezo.idober.vo;

import java.util.Date;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class SkuVo {
    @Field
    private Integer siteId;
    @Field
    private Long shopId;
    @Field
    private String skuCode;
    @Field
    private String productName;
    @Field
    private String productUrl;
    @Field
    private String imgUrl;
    @Field
    private String categoryNav;
    @Field
    private String tokenCategory;
    @Field
    private String tokenBrand;
    @Field
    private String tokenModel;
    @Field
    private String tokenUnit;
    @Field
    private Float marketPrice;
    @Field
    private Float productPrice;
    @Field
    private Float minPrice;
    @Field
    private Float maxPrice;
    @Field
    private Long commentNum;
    @Field
    private Long goodComment;
    @Field
    private Long stockNum;
    @Field
    private Long soldNum;
    @Field
    private Date updateTime;
}
