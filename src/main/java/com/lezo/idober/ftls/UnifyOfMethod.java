package com.lezo.idober.ftls;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class UnifyOfMethod implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (CollectionUtils.isEmpty(arguments)) {
            return null;
        }
        int index = -1;
        String source = arguments.get(++index).toString();
        if (source == null) {
            return null;
        }
        Integer maxLen = 20;
        if (arguments.size() > 1) {
            maxLen = Integer.valueOf(arguments.get(++index).toString());
        }
        String suffix = "...";
        if (arguments.size() > 2) {
            suffix = arguments.get(++index).toString();
        }
        if (maxLen < source.length()) {
            source = source.substring(0, maxLen);
            source += suffix;
        }
        return source;
    }

}
