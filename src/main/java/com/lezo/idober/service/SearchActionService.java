package com.lezo.idober.service;

import com.lezo.idober.vo.ActionReturnVo;

public interface SearchActionService {

	Long buildSearch(String keyWord, Integer curPage, Integer pageSize) throws Exception;

	ActionReturnVo getSearchResult(Long searchId);
}
