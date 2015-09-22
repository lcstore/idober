package com.lezo.idober.ftls;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.config.ObjectFactory;
import org.sitemesh.config.properties.PropertiesFilterConfigurator;
import org.sitemesh.config.xml.XmlFilterConfigurator;

public class FreeMarkerSiteMeshFilter extends ConfigurableSiteMeshFilter {

    private FilterConfig filterConfig;
    private Map<String, String> configProperties;

    @Override
    protected Filter setup() throws ServletException {
        ObjectFactory objectFactory = getObjectFactory();
        CustomSiteMeshFilterBuilder builder = new CustomSiteMeshFilterBuilder();

        new PropertiesFilterConfigurator(objectFactory, configProperties)
                .configureFilter(builder);

        new XmlFilterConfigurator(getObjectFactory(),
                loadConfigXml(filterConfig, getConfigFileName()))
                .configureFilter(builder);

        applyCustomConfiguration(builder);

        return builder.create();

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.configProperties = getConfigProperties(filterConfig);
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException,
            ServletException {
        super.doFilter(servletRequest, servletResponse, filterChain);
    }

}
