package com.lezo.idober.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class ParamUtils {
	public static final int PAGE_SIZE = 20;
	public static final int MIN_PAGE_NUM = 1;
	public static final int MAX_PAGE_NUM = 100;

	public static Integer inRange(Integer curPage) {
		return inRange(curPage, MIN_PAGE_NUM, MAX_PAGE_NUM);
	}

	public static Integer inRange(Integer curPage, int fromNum, int toNum) {
		if (curPage == null || curPage < fromNum) {
			curPage = fromNum;
		} else {
			curPage = curPage > toNum ? toNum : curPage;
		}
		return curPage;
	}

	public static String xssClean(String source) {
		if (source != null) {
			source = Jsoup.clean(source, Whitelist.basic());
		}
		return source;
	}
}
