package com.lezo.idober.service;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public interface UserService {

	void createUsers(List<SolrInputDocument> docs) throws Exception;

	SolrDocument loginUser(String openId, String accessToken, UserKind kind) throws Exception;

	SolrDocumentList queryUserByOpenId(String openId) throws Exception;

}
