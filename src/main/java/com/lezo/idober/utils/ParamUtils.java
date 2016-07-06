package com.lezo.idober.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class ParamUtils {
	public static final int PAGE_SIZE = 36;
	public static final int MAX_PAGE = 36;

	public static Integer clampPageSize(Integer curPage) {
		return PAGE_SIZE;
	}
	public static Integer clampPageNum(Integer curPage) {
		if (curPage == null || curPage < 1) {
			curPage = 1;
		} else {
			curPage = curPage > MAX_PAGE ? MAX_PAGE : curPage;
		}
		return curPage;
	}

	public static String xssClean(String source) {
		if(source !=null){
			source = Jsoup.clean(source, Whitelist.basic());
		}
		return source;
	}
}
