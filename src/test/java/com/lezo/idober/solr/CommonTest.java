package com.lezo.idober.solr;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

public class CommonTest {

    @Test
    public void test() {
        int destWeek = 6;
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int days = destWeek - week;
        days = days <= 0 ? days : days - 7;
        Date date = DateUtils.addDays(new Date(), days);
        System.err.println("week:" + week + ",date:" + date + ",now:" + new Date());
    }
}
