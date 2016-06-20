package com.lezo.idober.solr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
