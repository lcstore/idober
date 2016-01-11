package com.lezo.idober.vo;

import lombok.Data;

@Data
public class SolrFieldVo {
    private String key;
    private String value;

    public SolrFieldVo() {
    }

    public SolrFieldVo(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }
}
