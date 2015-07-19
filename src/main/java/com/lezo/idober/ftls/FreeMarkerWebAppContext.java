package com.lezo.idober.ftls;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sitemesh.content.Content;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;

import com.lezo.iscript.spring.context.SpringBeanUtils;

public class FreeMarkerWebAppContext extends WebAppContext {
	public FreeMarkerWebAppContext(String contentType, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext, ContentProcessor contentProcessor, ResponseMetaData metaData,
			boolean includeErrorPages) {
		super(contentType, request, response, servletContext, contentProcessor, metaData, includeErrorPages);
	}

	@Override
	protected void decorate(String decoratorPath, Content content, Writer out) throws IOException {
		if (decoratorPath.toLowerCase().endsWith(".ftl")) {
			CustomFreeMarkerConfigurer config = SpringBeanUtils.getBean(CustomFreeMarkerConfigurer.class);
			String version = config.getConfiguration().getSharedVariable(CustomFreeMarkerConfigurer.KEY_VERSION)
					.toString();
			StringWriter srcOut = new StringWriter();
			super.decorate(decoratorPath, content, srcOut);
			String ftlContent = srcOut.toString();
			ftlContent = ftlContent.replaceAll("\\$\\{version\\}", version);
			out.append(ftlContent);
		} else {
			super.decorate(decoratorPath, content, out);
		}
	}

}
