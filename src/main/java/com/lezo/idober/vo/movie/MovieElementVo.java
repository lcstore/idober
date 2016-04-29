package com.lezo.idober.vo.movie;

import java.util.Date;

import lombok.Data;

@Data
public class MovieElementVo {
    private String title;
    private String code;
    private String imgUrl;
    private Date updateTime;

    public void setCode(String code) {
        // if (code != null) {
        // try {
        // code = AESCodecUtils.encrypt(code);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        this.code = code;
    }
}
