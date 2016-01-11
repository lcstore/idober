package com.lezo.idober.vo;

import java.util.List;

import lombok.Data;

@Data
public class SolrDocListVo {
    private String type;
    private List<SolrDocVo> docs;
}
