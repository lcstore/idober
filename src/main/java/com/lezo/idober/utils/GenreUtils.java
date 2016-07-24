package com.lezo.idober.utils;

import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.google.common.collect.Maps;

@Log4j
public class GenreUtils {
	private static final Map<String, String> REGION_MAP = Maps.newHashMap();
	private static final String DEFAULT_REGION = "科幻";

	public static String toCNRegionGroup(String pingyinRegion) {
		if (REGION_MAP.isEmpty()) {
			loadRegionGroup();
		}
		String sGroup = REGION_MAP.get(pingyinRegion);
		return sGroup == null ? DEFAULT_REGION : sGroup;
	}

	private synchronized static void loadRegionGroup() {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.set("q", "type:idober-kv-genres");
		solrQuery.addField("cn_s,py_s");
		try {
			QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			for (SolrDocument doc : docList) {
				String key = doc.getFieldValue("py_s").toString().trim();
				String value = doc.getFieldValue("cn_s").toString().trim();
				REGION_MAP.put(key, value);
			}
		} catch (Exception e) {
			log.warn("loadRegionGroup.cause:", e);
		}

	}
}
