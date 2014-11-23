package com.lezo.idober.service;

public interface SearchActionService {

	Long buildSearch(String keyWord, Integer curPage, Integer pageSize) throws Exception;

	String getSearchResult(Long searchId);
}
