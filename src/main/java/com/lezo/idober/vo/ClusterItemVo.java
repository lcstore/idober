package com.lezo.idober.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClusterItemVo implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private Long matchCode;
	private Long wareCode;
	private Long similarCode;
	private Integer siteId;
	private Integer shopId;
	private String shopName;
	private String productCode;
	private String productName;
	private String productUrl;
	private Float marketPrice;
	private Float currentPrice;
	private String imgUrl;
	private String tokenBrand;
	private String tokenCategory;
	private String tokenVary;

}
