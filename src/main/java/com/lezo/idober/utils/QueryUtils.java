package com.lezo.idober.utils;

import org.apache.solr.client.solrj.SolrQuery;

public class QueryUtils {
	
	public static SolrQuery withCommonMovieFilter(SolrQuery solrQuery) {
		solrQuery.addFilterQuery("type:movie");
		return solrQuery;
	}
}
