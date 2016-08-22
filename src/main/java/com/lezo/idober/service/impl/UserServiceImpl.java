package com.lezo.idober.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.lezo.idober.service.UserKind;
import com.lezo.idober.service.UserService;
import com.lezo.idober.utils.SolrUtils;

@Component
public class UserServiceImpl implements UserService {

	@Override
	public void createUsers(List<SolrInputDocument> docs) throws Exception {
		if (CollectionUtils.isEmpty(docs)) {
			return;
		}
		for (SolrInputDocument doc : docs) {
			Map<String, Object> cmdMap = Maps.newHashMap();
			cmdMap.put("inc", 1);
			doc.setField("login_count", cmdMap);
			doc.setField("login_date", new Date());
		}
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_USER);
		server.add(docs);
		server.commit();
	}

	@Override
	public SolrDocument loginUser(String openId, String accessToken, UserKind kind) throws Exception {
		if (StringUtils.isBlank(openId)) {
			return null;
		}
		SolrDocumentList solrList = queryUserByOpenId(kind.value() + openId);
		if (CollectionUtils.isEmpty(solrList)) {
			return null;
		}
		SolrDocument userDoc = solrList.get(0);
		if (kind == UserKind.USER) {
			if (accessToken == null || !accessToken.equals(userDoc.getFieldValue("secret"))) {
				return null;
			}
		}
		SolrInputDocument doc = createUserDocument();
		doc.setField("id", userDoc.getFieldValue("id"));
		Map<String, Object> cmdMap = Maps.newHashMap();
		cmdMap.put("inc", 1);
		doc.setField("login_count", cmdMap);
		doc.setField("login_date", new Date());
		doCommit(doc, SolrUtils.CORE_USER);
		return userDoc;
	}

	private SolrInputDocument createUserDocument() {
		SolrInputDocument doc = new SolrInputDocument();
		Map<String, Object> cmdMap = Maps.newHashMap();
		cmdMap.put("add", new Date());
		doc.setField("creation", cmdMap);
		return doc;
	}

	@Override
	public SolrDocumentList queryUserByOpenId(String openId) throws Exception {
		if (StringUtils.isBlank(openId)) {
			return null;
		}
		openId = ClientUtils.escapeQueryChars(openId);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setRows(100);
		solrQuery.set("q", openId);
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_USER).query(solrQuery);
		return resp.getResults();
	}

	private void doCommit(SolrInputDocument doc, String coreName) throws Exception {
		SolrServer server = SolrUtils.getSolrServer(coreName);
		server.add(doc);
		server.commit();
	}
}
