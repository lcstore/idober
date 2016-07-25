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
	private static final Map<String, String> GENRE_MAP = Maps.newHashMap();
	public static final String DEFAULT_GENRE = "kehuan";
	public static final String DEFAULT_GENRE_CN = "科幻";

	public static String toCNGenre(String pingyinVal) {
		if (GENRE_MAP.isEmpty()) {
			loadRegionGroup();
		}
		String sGroup = GENRE_MAP.get(pingyinVal);
		return sGroup == null ? DEFAULT_GENRE : sGroup;
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
				GENRE_MAP.put(key, value);
			}
		} catch (Exception e) {
			log.warn("loadRegionGroup.cause:", e);
		}

	}
}
