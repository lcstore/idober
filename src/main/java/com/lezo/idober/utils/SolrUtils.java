package com.lezo.idober.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.core.CoreContainer;

import com.google.common.collect.Sets;
import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class SolrUtils {
	private static final String ENV_NAME_DEV = "dev";
	private static CoreContainer coreContainer;
	private static final ConcurrentHashMap<String, SolrServer> SERVER_MAP = new ConcurrentHashMap<String, SolrServer>();
	private static final String CORE_DATA = "core0";
	private static final String CORE_SKU = "core1";
	public static final String CORE_OLD_MOVIE = "core2";
	public static final String CORE_SOURCE_MOVIE = "cmovie";
	public static final String CORE_SOURCE_META = "cmeta";
	public static final String CORE_ONLINE_MOVIE = "omovie";
	public static final String CORE_USER = "cuser";
	public static final String CORE_ARTICLE = "carticle";

	public static final String KEY_EDIT_PREFIX = "_ex_";
	public static final String VALUE_SPLITOR = ";";

	public static SolrServer getDataServer() {
		return getSolrServer(CORE_DATA);
	}

	@Deprecated
	public static SolrServer getSkuServer() {
		return getSolrServer(CORE_SKU);
	}

	@Deprecated
	public static SolrServer getMovieServer() {
		return getSolrServer(CORE_OLD_MOVIE);
	}

	public static SolrServer getEmbeddedSolrServer() {
		return getSolrServer(CORE_SKU);
	}

	public static SolrServer getSolrWithMovie() {
		return getSolrServer(CORE_SOURCE_MOVIE);
	}

	public static SolrServer getSolrWithMeta() {
		return getSolrServer(CORE_SOURCE_META);
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

	public static Set<String> getSolrFieldSet(Class<?> solrClass) {
		Set<String> fieldSet = Sets.newHashSet();
		for (java.lang.reflect.Field fld : solrClass.getDeclaredFields()) {
			Field annField = fld.getAnnotation(Field.class);
			if (annField == null) {
				continue;
			}
			fieldSet.add(fld.getName());
		}
		return fieldSet;
	}

	@Deprecated
	public static SolrDocument overwriteWithEditVal(SolrDocument doc) {
		Set<String> delSet = Sets.newHashSet();
		Set<String> fieldSet = Sets.newHashSet(doc.getFieldNames());
		for (String fieldName : fieldSet) {
			if (fieldName.startsWith(KEY_EDIT_PREFIX)) {
				String originField = fieldName.replace(KEY_EDIT_PREFIX, "");
				int index = originField.lastIndexOf("_");
				originField = originField.substring(0, index);
				boolean isOrigin = false;
				if (fieldSet.contains(originField)) {
					isOrigin = true;
				} else {
					originField += "s";
					isOrigin = fieldSet.contains(originField);
				}
				if (isOrigin) {
					Object valObj = doc.getFieldValue(fieldName);
					if (valObj != null && StringUtils.isNotBlank(valObj.toString())) {
						doc.put(originField, valObj);
						delSet.add(fieldName);
					}
				}
			}
		}
		for (String field : delSet) {
			doc.removeFields(field);
		}
		return doc;
	}
}
