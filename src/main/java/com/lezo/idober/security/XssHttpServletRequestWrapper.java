package com.lezo.idober.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = cleanXSS(values[i]);
        }
        return values;

    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);

    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);

    }

    private String cleanXSS(String unsafe) {

        // // You'll need to remove the spaces from the html entities below
        //
        // value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        //
        // value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        //
        // value = value.replaceAll("'", "& #39;");
        //
        // value = value.replaceAll("eval\\((.*)\\)", "");
        //
        // value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        //
        // value = value.replaceAll("script", "");

        // https://jsoup.org/cookbook/cleaning-html/whitelist-sanitizer
        String safe = Jsoup.clean(unsafe, Whitelist.basic());
        return safe;
    }

}