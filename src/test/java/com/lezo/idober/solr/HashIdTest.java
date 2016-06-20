package com.lezo.idober.solr;

import org.junit.Test;

public class HashIdTest {

    @Test
    public void testHastId() {
        String source = "2010;金田敬;纯情";
        String date = "2010-9-4";
        String sCode = date + source.hashCode();
        sCode = "" + sCode.hashCode();
        sCode = sCode.replace("-", "0");
        System.err.println(sCode);
        String imgUrl = "http://img21.mtime.cn/mt/2011/07/29/114748.51099207_270X405X4.jpg";
        imgUrl = imgUrl.replaceFirst("http:.{3,}?/", "/img/");
        System.err.println(imgUrl);
    }
}
