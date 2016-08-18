package com.lezo.idober.ftls;

import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * CustomFreeMarkerViewResolver,Descript.
 *
 * @author lilinchong
 * @since 2016年8月18日
 */
public class CustomFreeMarkerViewResolver extends FreeMarkerViewResolver {

    @Override
    protected Class<?> requiredViewClass() {
        return CustomFreeMarkerView.class;
    }

}
