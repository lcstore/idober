package com.lezo.idober.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 电影地区（北美、欧洲、东南亚、中国、中东）
 * 
 * @author lezo
 * @since 2016年12月26日
 */
@Log4j
public class RegionUtils {
	private static final Map<String, String> REGION_MAP = Maps.newHashMap();
	public static final String DEFAULT_REGION = "zhongguo";
	public static final String DEFAULT_REGION_CN = "中国";

	public static String toCNRegion(String pingyinRegion, String defaultVal) {
		String sGroup = toCNRegion(pingyinRegion);
		return sGroup == null ? defaultVal : sGroup;
	}

	public static String toCNRegion(String pingyinRegion) {
		if (REGION_MAP.isEmpty()) {
			loadRegionGroup();
		}
		String sGroup = REGION_MAP.get(pingyinRegion);
		return sGroup;
	}

	private synchronized static void loadRegionGroup() {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.set("q", "type:idober-group-region");
		solrQuery.addField("title,short_s");
		try {
			QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			for (SolrDocument doc : docList) {
				String key = doc.getFieldValue("short_s").toString().trim();
				String value = doc.getFieldValue("title").toString().trim();
				REGION_MAP.put(key, value);
			}
		} catch (Exception e) {
			log.warn("loadRegionGroup.cause:", e);
		}

	}

	public static String country2Region(String sCountry) throws Exception {
		if (StringUtils.isBlank(sCountry)) {
			return null;
		}
		sCountry = ClientUtils.escapeQueryChars(sCountry);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(1);
		solrQuery.addField("title");
		solrQuery.set("q", "(type:idober-group-region AND group_ss:" + sCountry + ")");
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		if (CollectionUtils.isNotEmpty(docList)) {
			return docList.get(0).getFieldValue("title").toString();
		}
		return null;
	}

	public static Set<String> unifyRegions(Collection<String> regions) {
		Pattern cnReg = Pattern.compile("([\u4E00-\u9FA5]+)");
		Set<String> regionSet = Sets.newHashSet();
		for (String region : regions) {
			region = region.replaceAll("\\.", "");
			region = region.replaceAll("[()（）]", "");
			Matcher matcher = cnReg.matcher(region);
			int index = 0;
			if (matcher.find()) {
				String cnName = matcher.group(1);
				index = matcher.end(1);
				cnName = cnName.trim();
				if (cnName.contains("西德")) {
					cnName = "德国";
				}
				cnName = ChineseHelper.convertToSimplifiedChinese(cnName);
				regionSet.add(cnName);

			}
			if (index < region.length()) {
				region = region.substring(index).toLowerCase();
				String[] txtArr = region.split("[/|]");
				for (String txt : txtArr) {
					if (StringUtils.isBlank(txt)) {
						continue;
					}
					txt = txt.trim();
					txt = ChineseHelper.convertToSimplifiedChinese(txt);
					regionSet.add(txt);
				}
			}
		}
		return regionSet;
	}
}
