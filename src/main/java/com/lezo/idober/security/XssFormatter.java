package com.lezo.idober.security;

import java.text.ParseException;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.format.Formatter;

public class XssFormatter implements Formatter<String> {

    @Override
    public String print(String source, Locale locale) {
        if (source != null) {
            source = Jsoup.clean(source, Whitelist.basic());
        }
        return source;
    }

    @Override
    public String parse(String formatted, Locale arg1) throws ParseException {
        return formatted;
    }

}
