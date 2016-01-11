package com.lezo.idober.solr.pojo;

import java.util.Date;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class ItemSolr {
    private static String ITEM_SEARCH_FIELDS;

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

    public static String getSolrFields() {
        if (ITEM_SEARCH_FIELDS == null) {
            synchronized (ItemSolr.class) {
                if (ITEM_SEARCH_FIELDS == null) {
                    StringBuilder sb = new StringBuilder();
                    for (java.lang.reflect.Field fld : ItemSolr.class.getDeclaredFields()) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(fld.getName());
                    }
                    ITEM_SEARCH_FIELDS = sb.toString();
                }
            }
        }
        return ITEM_SEARCH_FIELDS;
    }
}
