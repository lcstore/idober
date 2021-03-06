package com.lezo.idober.vo;

import lombok.Data;

@Data
public class ProductVo {
	private Integer siteId;
	private String productCode;
	private String productName;
	private Float marketPrice;
	private Float productPrice;
	private String productUrl;
	private String imgUrl;
	private String siteName;
    private String unionUrl;
}
