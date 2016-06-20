package com.lezo.idober.ftls;

import java.io.IOException;
import java.util.List;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.spring.context.SpringBeanUtils;

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
    public void afterPropertiesSet() throws IOException,
            TemplateException {
        super.afterPropertiesSet();
        getConfiguration().setSharedVariable(KEY_VERSION, version);
        AppConfig config = SpringBeanUtils.getBean(AppConfig.class);
        getConfiguration().setSharedVariable("static_host", config.getStaticHost());
    }

    @Override
    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        templateLoaders.add(new ClassTemplateLoader(CustomFreeMarkerConfigurer.class, ""));
    }

    @Override
    protected void postProcessConfiguration(Configuration config) throws IOException,
            TemplateException {
        super.postProcessConfiguration(config);
    }

}
