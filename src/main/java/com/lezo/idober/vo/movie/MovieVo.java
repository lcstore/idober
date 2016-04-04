package com.lezo.idober.vo.movie;

import java.util.Date;

import lombok.Data;

import com.lezo.idober.utils.AESCodecUtils;

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
    private String story;

    // private int classify;
    // private int upCount;
    // private int downCount;
    public String getDecryptId() {
        if (id != null) {
            try {
                return AESCodecUtils.decrypt(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public void setId(String id) {
        if (id != null) {
            try {
                id = AESCodecUtils.encrypt(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.id = id;
    }
}
