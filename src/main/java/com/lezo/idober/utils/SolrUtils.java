package com.lezo.idober.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.core.CoreContainer;

import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class SolrUtils {
    private static final String ENV_NAME_DEV = "dev";
    private static CoreContainer coreContainer;
    private static final ConcurrentHashMap<String, SolrServer> SERVER_MAP = new ConcurrentHashMap<String, SolrServer>();
    private static final String CORE_DATA = "core0";
    private static final String CORE_SKU = "core1";
    private static final String CORE_MOVIE = "core2";

    public static SolrServer getDataServer() {
        return getSolrServer(CORE_DATA);
    }

    public static SolrServer getSkuServer() {
        return getSolrServer(CORE_SKU);
    }

    public static SolrServer getMovieServer() {
        return getSolrServer(CORE_MOVIE);
    }

    public static SolrServer getEmbeddedSolrServer() {
        return getSolrServer(CORE_SKU);
    }

    public static void setCoreContainer(CoreContainer coreContainer) {
        SolrUtils.coreContainer = coreContainer;
    }

    public static SolrServer getSolrServer(String coreName) throws SolrException {
        coreName = coreName == null ? StringUtils.EMPTY : coreName;
        SolrServer hasServer = SERVER_MAP.get(coreName);
        if (hasServer == null) {
            synchronized (SERVER_MAP) {
                hasServer = SERVER_MAP.get(coreName);
                if (hasServer == null) {
                    AppConfig appConfig = SpringBeanUtils.getBean(AppConfig.class);
                    if (appConfig.getEnvName() != null && ENV_NAME_DEV.equals(appConfig.getEnvName().toLowerCase())) {
                        hasServer = new HttpSolrServer(appConfig.getSorlServerUrl() + coreName);
                    } else {
                        if (!SolrUtils.coreContainer.getAllCoreNames().contains(coreName)) {
                            throw new SolrException(ErrorCode.SERVER_ERROR, "SolrCore '" + coreName +
                                    "' is not available due to init failure: ");
                        }
                        hasServer = new EmbeddedSolrServer(SolrUtils.coreContainer, coreName);
                    }
                    SERVER_MAP.put(coreName, hasServer);
                }
            }
        }
        return hasServer;
    }

    public static <T> String getSolrFields(Class<T> solrClass) {
        StringBuilder sb = new StringBuilder();
        for (java.lang.reflect.Field fld : solrClass.getDeclaredFields()) {
            Field annField = fld.getAnnotation(Field.class);
            if (annField == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(fld.getName());
        }
        return sb.toString();
    }
}
