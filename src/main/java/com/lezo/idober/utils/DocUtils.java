package com.lezo.idober.utils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class DocUtils {

	public static boolean isSameMovieDoc(SolrDocument doc, SolrDocument mDoc) {
		Boolean bSame = isSameImdb(doc, mDoc);
		if (bSame != null) {
			return bSame;
		}
		bSame = hasSameDirector(doc, mDoc);
		if (bSame != null) {
			return bSame;
		}
		bSame = hasSameActor(doc, mDoc);
		if (bSame == null) {
			bSame = hasSameRelease(doc, mDoc, 0);
		} else if (bSame) {
			bSame = hasSameRelease(doc, mDoc, 12);
		}
		return bSame;
	}

	public static Boolean isSameImdb(SolrDocument doc, SolrDocument mDoc) {
		Object imdbSrcObj = doc.getFieldValue("imdb_s");
		String sImdb = null;
		if (imdbSrcObj == null) {
			Object ctObject = doc.getFieldValue("content");
			if (ctObject != null) {
				String srcContent = ctObject.toString();
				JSONObject srcObject = JSONObject.parseObject(srcContent);
				sImdb = srcObject.getString("imdb");
			}
		} else {
			sImdb = imdbSrcObj.toString();
		}
		if (StringUtils.isBlank(sImdb)) {
			return null;
		}
		Object imdbObj = mDoc.getFieldValue("imdb_txt");
		if (imdbObj == null) {
			return null;
		}
		Boolean bSame = null;
		String sImdbTxt = imdbObj.toString();
		sImdbTxt = sImdbTxt.replaceAll("[^0-9]+", "").trim();
		if (StringUtils.isNotBlank(sImdbTxt)) {
			bSame = sImdb.contains(sImdbTxt);
		}
		return bSame;
	}

	public static boolean hasSameRelease(SolrDocument doc, SolrDocument mDoc, int monthRange) {
		Object referObject = doc.getFieldValue("release");
		if (referObject == null) {
			return true;
		}
		Date referDate = (Date) referObject;

		Date releaseDate = null;
		Object releaseObject = mDoc.getFieldValue("release_tdt");
		if (releaseObject != null && releaseObject instanceof Date) {
			releaseDate = (Date) releaseObject;
		}
		Date yearDate = null;
		Object yearObject = mDoc.getFieldValue("year_ti");
		if (yearObject != null && yearObject instanceof Integer) {
			int year = NumberUtils.toInt(yearObject.toString().trim());
			if (year > 0) {
				yearDate = DateUtils.setYears(new Date(), year);
			}
		}
		Date srcDate = releaseDate;
		if (releaseDate != null && yearDate != null) {
			// xiamp4 release 不靠谱
			Calendar c = Calendar.getInstance();
			c.setTime(srcDate);
			int srcYear = c.get(Calendar.YEAR);
			c = Calendar.getInstance();
			c.setTime(referDate);
			int referYear = c.get(Calendar.YEAR);
			int year = NumberUtils.toInt(yearObject.toString());
			if (referYear == srcYear) {
				srcDate = releaseDate;
			} else if (referYear == year) {
				srcDate = yearDate;
			} else if (srcYear != year) {
				srcDate = yearDate;
			}
		}
		srcDate = srcDate == null ? yearDate : srcDate;
		if (srcDate == null) {
			return true;
		}

		monthRange = Math.abs(monthRange);
		Date fromDate = DateUtils.addMonths(referDate, -monthRange);
		Date toDate = DateUtils.addMonths(referDate, monthRange);
		boolean hasSame = srcDate.after(fromDate) && srcDate.before(toDate);
		hasSame = hasSame || DateUtils.isSameDay(srcDate, referDate);
		return hasSame;
	}

	public static Boolean hasSameActor(SolrDocument doc, SolrDocument mDoc) {
		Object mContentObj = mDoc.getFieldValue("content");
		String sContent = mContentObj.toString();
		JSONObject ctObject = JSONObject.parseObject(sContent);
		String actors = ctObject.getString("actors");
		Boolean bSame = null;
		if (StringUtils.isBlank(actors)) {
			return bSame;
		}
		Collection<Object> actorList = doc.getFieldValues("actors");
		if (CollectionUtils.isEmpty(actorList)) {
			return bSame;
		}
		bSame = isContains(actors, actorList);
		if (!bSame && !isSameLang(actors, JSON.toJSONString(actorList))) {
			bSame = null;
		}
		return bSame;
	}

	public static boolean isContains(String actors, Collection<Object> actorList) {
		boolean bSame = false;
		int sameCount = 0;
		actors = actors.toLowerCase();
		int aCount = actors.split(";").length;
		for (Object aObj : actorList) {
			String sActor = aObj.toString().toLowerCase().trim();
			// 李璟荣, 全慧珍 VS 李景荣;全惠珍
			int dist = StringUtils.getLevenshteinDistance(sActor, actors);
			int minLen = Math.min(sActor.length(), actors.length());
			int maxLen = Math.max(sActor.length(), actors.length());
			int sameLen = maxLen - dist;
			float hitPer = sameLen * 1F / minLen;
			if (hitPer > 0.65F || actors.contains(sActor)) {
				sameCount++;
			}
		}
		int minCount = (aCount > 0 && actorList.size() > 0) ? Math.min(aCount, actorList.size()) : actorList.size();
		Float countPer = sameCount * 1F / minCount;
		if (countPer >= 0.7F) {
			bSame = true;
		}
		return bSame;
	}

	@SuppressWarnings("unchecked")
	public static Boolean hasSameDirector(SolrDocument doc, SolrDocument mDoc) {
		Object srcObject = mDoc.getFieldValue("director_qtxt");
		if (srcObject == null) {
			return null;
		}
		String srcDirector = srcObject.toString();
		if (srcDirector.equals("-") || srcDirector.length() > 50) {
			return null;
		}
		Object referObject = doc.getFieldValue("directors");
		if (referObject == null) {
			return null;
		}
		srcDirector = srcDirector.toLowerCase();
		Collection<Object> directors = (Collection<Object>) referObject;
		Boolean hasSame = isContains(srcDirector, directors);
		if (!hasSame && !isSameLang(srcDirector, JSON.toJSONString(directors))) {
			hasSame = null;
		}
		return hasSame;
	}

	public static boolean isSameLang(String lChars, String rChars) {
		Pattern cnReg = Pattern.compile("([\u4e00-\u9fa5]+)");
		Matcher lMatcher = cnReg.matcher(lChars);
		Matcher rMatcher = cnReg.matcher(JSON.toJSONString(rChars));
		boolean lHasCn = lMatcher.find();
		boolean rHasCn = rMatcher.find();
		// 中文 vs 英文
		return lHasCn & rHasCn;
	}

	public static void changeImage(SolrDocumentList docList) {
		if (docList == null) {
			return;
		}
		for (SolrDocument doc : docList) {
			changeImage(doc);
		}
	}

	public static void changeImage(SolrDocument doc) {
		if (doc == null) {
			return;
		}
		String key = "image";
		Object imageObj = doc.getFieldValue(key);
		if (imageObj != null) {
			String image = imageObj.toString();
			image = image.replace(".webp", ".jpg");
			doc.setField(key, image);
		}
	}
}
