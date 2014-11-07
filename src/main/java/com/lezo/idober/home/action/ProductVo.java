package com.lezo.idober.home.action;

import org.apache.commons.lang3.StringUtils;

public class ProductVo {
	private Integer siteId;
	private String productCode;
	private String productName;
	private Float marketPrice;
	private Float productPrice;
	private String productUrl;
	private String imgUrl;
	private String unionUrl;

	private String siteName;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Float getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Float marketPrice) {
		this.marketPrice = marketPrice;
	}

	public Float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Float productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductUrl() {
		if (!StringUtils.isEmpty(getUnionUrl())) {
			return getUnionUrl();
		}
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		if (imgUrl != null) {
			imgUrl = imgUrl.replace("_60x60.jpg", "_200x200.jpg");
		}
		this.imgUrl = imgUrl;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getUnionUrl() {
		return unionUrl;
	}

	public void setUnionUrl(String unionUrl) {
		this.unionUrl = unionUrl;
	}
}
