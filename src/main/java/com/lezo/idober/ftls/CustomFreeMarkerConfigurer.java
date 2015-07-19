package com.lezo.idober.ftls;

import java.io.IOException;
import java.util.List;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class CustomFreeMarkerConfigurer extends FreeMarkerConfigurer {
	public static final String KEY_VERSION = "version";
	private String version = "" + System.currentTimeMillis();

	public CustomFreeMarkerConfigurer() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		super.afterPropertiesSet();
		getConfiguration().setSharedVariable(KEY_VERSION, version);
	}

	@Override
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		templateLoaders.add(new ClassTemplateLoader(CustomFreeMarkerConfigurer.class, ""));
	}

	@Override
	protected void postProcessConfiguration(Configuration config) throws IOException, TemplateException {
		// TODO Auto-generated method stub
		super.postProcessConfiguration(config);
	}

}
