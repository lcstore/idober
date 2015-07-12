package com.lezo.idober.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TagRectVo<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String tagName;
    private List<T> dataList;

}
