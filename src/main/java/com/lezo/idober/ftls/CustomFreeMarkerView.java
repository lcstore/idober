package com.lezo.idober.ftls;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;

/**
 * CustomFreeMarkerView,Descript.
 *
 * @author lilinchong
 * @since 2016年8月18日
 */
public class CustomFreeMarkerView extends FreeMarkerView {

    @Override
    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) {
        SimpleHash simpleHash = super.buildTemplateModel(model, request, response);
        Configuration config = getConfiguration();
        for (Object name : config.getSharedVariableNames()) {
            String sName = name.toString();
            if (model.containsKey(sName)) {
                continue;
            }
            simpleHash.put(sName, config.getSharedVariable(sName));
        }
        return simpleHash;
    }

}
