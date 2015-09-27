package com.lezo.idober.solr;

import java.io.UnsupportedEncodingException;

import lombok.extern.log4j.Log4j;

import org.apache.solr.client.solrj.SolrQuery;

@Log4j
public class SolrQueryFactory {
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String[] QUERY_FIELDS =
            "siteId,productCode,productName,productUrl,marketPrice,imgUrl,unionUrl,categoryNav"
                    .split(",");

    public static SolrQuery newSolrQuery(String keyWord, Integer curPage, Integer pageSize) {
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        String queryWord = null;
        try {
            queryWord = getQueryWord(keyWord);
        } catch (UnsupportedEncodingException e) {
            log.warn("queryWord:" + keyWord, e);
            return null;
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(queryWord);
        solrQuery.setStart(getStart(curPage, pageSize));
        solrQuery.setRows(pageSize);
        solrQuery.setFields(QUERY_FIELDS);
        return solrQuery;
    }

    private static String getQueryWord(String keyWord) throws UnsupportedEncodingException {
        String qWord = null;
        if (keyWord.startsWith("http:") || keyWord.startsWith("https:")) {
            qWord = "productUrl:" + keyWord + "*";
        } else if (keyWord.matches("[0-9a-zA-Z]{4,}")) {
            qWord = "productCode:" + keyWord + " OR copyText:" + keyWord;
        } else {
            qWord = "copyText:" + keyWord;
        }
        // return URLEncoder.encode(qWord, "UTF-8");
        return qWord;
    }

    private static Integer getStart(Integer curPage, Integer pageSize) {
        return (curPage - 1) * pageSize;
    }
}
