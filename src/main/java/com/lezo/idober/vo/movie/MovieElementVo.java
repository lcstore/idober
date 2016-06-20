package com.lezo.idober.vo.movie;

import java.util.Date;

import lombok.Data;

import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Data
public class MovieElementVo {
    private String title;
    private String code;
    private String imgUrl;
    private Date updateTime;
    private int tcount;
    private int isNew;

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

    public void setImgUrl(String imgUrl) {
        String host = SpringBeanUtils.getBean(AppConfig.class).getStaticHost();
        this.imgUrl = imgUrl.replaceFirst("http:.{3,}?/", host + "/img/");
    }
}
