package com.lezo.idober.utils;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class SolrUtils {
    private static final String ENV_NAME_DEV = "dev";
    private static SolrServer httpSolrServer;
    private static SolrServer embeddedSolrServer;

    public static SolrServer getSolrServer() {
        AppConfig appConfig = SpringBeanUtils.getBean(AppConfig.class);
        if (ENV_NAME_DEV.equals(appConfig.getEnvName())) {
            if (httpSolrServer == null) {
                synchronized (SolrUtils.class) {
                    if (httpSolrServer == null) {
                        httpSolrServer = new HttpSolrServer(appConfig.getSorlServerUrl());
                    }
                }
            }
            return httpSolrServer;
        }
        return getEmbeddedSolrServer();
    }

    public static SolrServer getEmbeddedSolrServer() {
        return SolrUtils.embeddedSolrServer;
    }

    public static void setEmbeddedSolrServer(SolrServer embeddedSolrServer) {
        SolrUtils.embeddedSolrServer = embeddedSolrServer;
    }
}
