package com.lezo.idober.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListItemVo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String productCode;
    private String productName;
    private String productUrl;
    private String imgUrl;
    private String tokenBrand;
    private String tokenCategory;

}
