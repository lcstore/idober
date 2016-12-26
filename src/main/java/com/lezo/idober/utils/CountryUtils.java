package com.lezo.idober.utils;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;

/**
 * 国家，统一转换
 * 
 * @author lezo
 * @since 2016年12月26日
 */
public class CountryUtils {

    public static String unifyCountry(Set<String> synonymCountrySet) throws Exception {
        if (CollectionUtils.isEmpty(synonymCountrySet)) {
            return null;
        }
        String sQuery = "(type:idober-synonym-country AND synonym_ss:(";
        StringBuilder sb = new StringBuilder();
        sb.append(sQuery);
        for (String region : synonymCountrySet) {
            if (sb.length() > sQuery.length()) {
                sb.append(" OR ");
            }
            region = ClientUtils.escapeQueryChars(region);
            sb.append(region);
        }
        sb.append("))");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(1);
        solrQuery.addField("title");
        solrQuery.set("q", sb.toString());
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
        SolrDocumentList counts = resp.getResults();
        if (CollectionUtils.isNotEmpty(counts)) {
            return counts.get(0).getFieldValue("title").toString();
        }
        return null;
    }

}
