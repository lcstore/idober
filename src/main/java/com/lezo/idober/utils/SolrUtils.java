package com.lezo.idober.utils;

import org.apache.solr.client.solrj.SolrServer;

public class SolrUtils {
    // private static SolrServer server = new HttpSolrServer("http://www.lezomao.com/");
    private static SolrServer embeddedSolrServer;

    public static SolrServer getSolrServer() {
        return getEmbeddedSolrServer();
        // return server;
    }

    public static SolrServer getEmbeddedSolrServer() {
        return SolrUtils.embeddedSolrServer;
    }

    public static void setEmbeddedSolrServer(SolrServer embeddedSolrServer) {
        SolrUtils.embeddedSolrServer = embeddedSolrServer;
    }
}
