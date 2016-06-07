package com.lezo.idober.security;

import java.beans.PropertyEditorSupport;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * PathVariable can be cover
 * 
 * @author lezo
 * @since 2016年6月7日
 */
public class XssParamEditor extends PropertyEditorSupport {

    public XssParamEditor() {
        super();
    }

    public void setAsText(String unsafe) {
        if (unsafe == null) {
            setValue(null);
            return;
        }
        String value = unsafe;
        value = Jsoup.clean(value, Whitelist.basic());
        setValue(value);
    }
}