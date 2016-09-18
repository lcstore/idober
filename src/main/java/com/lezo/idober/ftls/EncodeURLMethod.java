package com.lezo.idober.ftls;

import java.net.URLEncoder;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Log4j
public class EncodeURLMethod implements TemplateMethodModelEx {

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (CollectionUtils.isEmpty(arguments)) {
			return null;
		}
		int index = -1;
		Object srcObject = arguments.get(++index);
		String source = srcObject == null ? null : srcObject.toString();
		if (source == null) {
			return null;
		}
		String charset = "UTF-8";
		if (arguments.size() > 1) {
			srcObject = arguments.get(++index);
			charset = srcObject == null ? charset : srcObject.toString();
		}
		try {
			return URLEncoder.encode(source, charset);
		} catch (Exception e) {
			log.warn("source:" + source, e);
			throw new TemplateModelException(e.getClass().getName() + ",msg:" + e.getMessage());
		}
	}

}
