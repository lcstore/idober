package com.lezo.idober.ftls;

import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sitemesh.DecoratorSelector;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.webapp.SiteMeshFilter;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;
import org.sitemesh.webapp.contentfilter.Selector;

public class CustomSiteMeshFilter extends SiteMeshFilter {
	private ContentProcessor contentProcessor;
	private boolean includeErrorPages;

	public CustomSiteMeshFilter(Selector selector, ContentProcessor contentProcessor,
			DecoratorSelector<WebAppContext> decoratorSelector, boolean includeErrorPages) {
		super(selector, contentProcessor, decoratorSelector, includeErrorPages);
		this.contentProcessor = contentProcessor;
		this.includeErrorPages = includeErrorPages;
	}

	@Override
	protected WebAppContext createContext(String contentType, HttpServletRequest request, HttpServletResponse response,
			ResponseMetaData metaData) {
		return new FreeMarkerWebAppContext(contentType, request, response,
				getFilterConfig().getServletContext(), contentProcessor, metaData, includeErrorPages);
	}

	@Override
	protected boolean postProcess(String contentType, CharBuffer buffer, HttpServletRequest request,
			HttpServletResponse response, ResponseMetaData metaData) throws IOException, ServletException {
		// can decorate respone
		return super.postProcess(contentType, buffer, request, response, metaData);
	}
}
