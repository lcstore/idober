package com.lezo.idober.ftls;

import javax.servlet.Filter;

import org.sitemesh.builder.SiteMeshFilterBuilder;

public class CustomSiteMeshFilterBuilder extends SiteMeshFilterBuilder {

	@Override
	public Filter create() {
		return new CustomSiteMeshFilter(
				getSelector(),
				getContentProcessor(),
				getDecoratorSelector(),
				isIncludeErrorPages());
	}

}
