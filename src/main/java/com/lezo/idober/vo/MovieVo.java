package com.lezo.idober.vo;

import lombok.Data;

@Data
public class MovieVo {
    public static final int CLASSIFY_NORMAL = 0;
    public static final int CLASSIFY_GROUP = 1;
    private String title;
    private String url;
    private String imgUrl;
    private String type;
    private int classify;
    private int upCount;
    private int downCount;
}
