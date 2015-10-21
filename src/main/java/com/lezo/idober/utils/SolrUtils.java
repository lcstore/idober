package com.lezo.idober.utils;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import com.lezo.idober.solr.EmbeddedSolrServerHolder;

public class SolrUtils {
    // private static SolrServer server = new HttpSolrServer("http://www.lezomao.com/");

    public static SolrServer getSolrServer() {
        EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
        return server;
    }
}
