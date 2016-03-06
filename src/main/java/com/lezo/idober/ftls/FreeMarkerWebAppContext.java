package com.lezo.idober.ftls;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sitemesh.content.Content;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewRendererServlet;

import com.lezo.iscript.spring.context.SpringBeanUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerWebAppContext extends WebAppContext {
    private static final String KEY_MODEL = "model";

    public FreeMarkerWebAppContext(String contentType, HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext, ContentProcessor contentProcessor, ResponseMetaData metaData,
            boolean includeErrorPages) {
        super(contentType, request, response, servletContext, contentProcessor, metaData, includeErrorPages);
    }

    @Override
    protected void decorate(String decoratorPath, Content content, Writer out) throws IOException {
        if (decoratorPath.toLowerCase().endsWith(".ftl")) {
            CustomFreeMarkerConfigurer config = SpringBeanUtils.getBean(CustomFreeMarkerConfigurer.class);
            // String version = config.getConfiguration().getSharedVariable(CustomFreeMarkerConfigurer.KEY_VERSION)
            // .toString();
            Configuration configuration = config.getConfiguration();
            File file = new File(decoratorPath);
            String decoratorName = file.getParentFile().getName() + "/" + file.getName();
            Template template = configuration.getTemplate(decoratorName);
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

    @SuppressWarnings("unchecked")
    private ModelMap getModelMap() {
        ModelMap dataModel = new ModelMap();
        HttpServletRequest request = getRequest();
        Object modelObj = request.getAttribute(KEY_MODEL);
        if (modelObj != null && modelObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) modelObj;
            dataModel.putAll(map);
        }
        modelObj = request.getAttribute(ViewRendererServlet.MODEL_ATTRIBUTE);
        if (modelObj != null && modelObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) modelObj;
            dataModel.putAll(map);
        }
        modelObj = request.getAttribute(View.PATH_VARIABLES);
        if (modelObj != null && modelObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) modelObj;
            dataModel.putAll(map);
        }
        modelObj = request.getParameterMap();
        if (modelObj != null && modelObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) modelObj;
            dataModel.putAll(map);
        }
        return dataModel;
    }
}
