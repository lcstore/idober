package com.lezo.idober.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class TypeUtils {
	private static List<String> typeList = Lists.newArrayList();
	static {
		// typeList.add("bttiantang-movie-torrent");
		// typeList.add("xiamp4-movie-torrent");
		typeList.add("dy2018-movie-torrent");
		typeList.add("rarbt-movie-torrent");
		typeList.add("bluraydisc-movie-torrent");
		typeList.add("xybl8-movie-torrent");
	}
	
	public static List<String> getTypeList() {
		return typeList;
	}
	
}
