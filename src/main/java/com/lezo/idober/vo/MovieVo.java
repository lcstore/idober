package com.lezo.idober.vo;

import java.util.Date;

import lombok.Data;

@Data
public class MovieVo {
    public static final int CLASSIFY_NORMAL = 0;
    public static final int CLASSIFY_GROUP = 1;
    private String id;
    private String name;
    private String enname;
    private String imgUrl;
    private String region;
    private Date date;
    private Integer year;
    private String directors;
    private String actors;
    private String genres;
    private Float score;
    private String shares;
    private String torrents;
    private int tcount;
    // private int classify;
    // private int upCount;
    // private int downCount;
}
