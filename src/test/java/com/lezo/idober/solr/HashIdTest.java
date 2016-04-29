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
    }
}
