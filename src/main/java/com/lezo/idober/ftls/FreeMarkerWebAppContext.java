package com.lezo.idober.ftls;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.scheme.Scheme;
import org.sitemesh.content.Content;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewRendererServlet;

import com.lezo.idober.error.CustomExceptionHandlerExceptionResolver;
import com.lezo.iscript.spring.context.SpringBeanUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerWebAppContext extends WebAppContext {
	private static final String KEY_MODEL = "model";
	private HttpServletRequest request;
	private boolean includeErrorPages;

	public FreeMarkerWebAppContext(String contentType, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext, ContentProcessor contentProcessor, ResponseMetaData metaData,
			boolean includeErrorPages) {
		super(contentType, request, response, servletContext, contentProcessor, metaData, includeErrorPages);
		this.request = request;
		this.includeErrorPages = includeErrorPages;
	}

	@Override
	protected void decorate(String decoratorPath, Content content, Writer out) throws IOException {
		if (decoratorPath.toLowerCase().endsWith(".ftl")) {
			CustomFreeMarkerConfigurer config = SpringBeanUtils.getBean(CustomFreeMarkerConfigurer.class);
			// String version =
			// config.getConfiguration().getSharedVariable(CustomFreeMarkerConfigurer.KEY_VERSION)
			// .toString();
			Configuration configuration = config.getConfiguration();
			Template template = configuration.getTemplate(decoratorPath);
			ModelMap dataModel = getModelMap();
			StringWriter srcOut = new StringWriter();
			try {
				template.process(dataModel, srcOut);
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			// super.decorate(decoratorPath, content, srcOut);
			// String ftlContent = srcOut.toString();
			// ftlContent = ftlContent.replaceAll("\\$\\{version\\}", version);
			out.append(srcOut.toString());
		} else {
			super.decorate(decoratorPath, content, out);
		}
	}

	private ModelMap getModelMap() {
		ModelMap dataModel = new ModelMap();
		HttpServletRequest request = getRequest();
		Enumeration<?> attrName = request.getAttributeNames();
		while (attrName.hasMoreElements()) {
			Object attKey = attrName.nextElement();
			if (attKey instanceof String) {
				String sAttKey = attKey.toString();
				Object attVal = request.getAttribute(sAttKey);
				if (attVal != null) {
					dataModel.put(sAttKey, attVal);
					if ("model".equals(sAttKey) && attVal instanceof ModelMap) {
						ModelMap modelMap = (ModelMap) attVal;
						dataModel.putAll(modelMap);
					}
				}
			}
		}
		return dataModel;
	}

	@Override
	public String getPath() {
		if (includeErrorPages) {
			Object errHandler = this.request.getAttribute(CustomExceptionHandlerExceptionResolver.KEY_ERROR_HANDLER);
			if (errHandler != null) {
				return errHandler.toString();
			}
		}
		return super.getPath();
	}
}
