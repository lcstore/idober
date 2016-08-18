package com.lezo.idober.solr;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

public class RegExpTest {
    private static final Pattern CN_NAME_REG = Pattern.compile("([\u4e00-\u9fa5\\s0-9]+)");
    private static final Pattern SYMBOL_REG = Pattern.compile("([:-_()（）：/]+)");

    @Test
    public void test() {
        String sName = "冰河世纪：巨蛋恶作剧 Ice Age: The Great Egg-Scapade";
        System.err.println("origin:" + sName);
        sName = SYMBOL_REG.matcher(sName).replaceAll(" ");
        Matcher matcher = CN_NAME_REG.matcher(sName);
        if (matcher.find()) {
            sName = matcher.group(1).trim();
        }
        System.err.println("new:" + sName);
    }

    @Test
    public void testNum() {
        String itemCode = "12323abd";
        Pattern NUM_REG = Pattern.compile("^[0-9]+$");
        Matcher matcher = NUM_REG.matcher(itemCode);
        if (matcher.find()) {
            System.err.println(itemCode + " is number");
        } else {
            System.err.println(itemCode + " is not a number");
        }
        Date date = new Date();
        String format = DateFormatUtils.format(date, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        System.err.println(date.toGMTString());
        System.err.println(format);
    }
}
