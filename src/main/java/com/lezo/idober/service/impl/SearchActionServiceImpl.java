package com.lezo.idober.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lezo.idober.service.SearchActionService;
import com.lezo.idober.vo.ActionReturnVo;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.service.SearchHisService;
import com.lezo.iscript.utils.JSONUtils;

@Component
public class SearchActionServiceImpl implements SearchActionService {
	@Autowired
	private SearchHisService searchHisService;
	@Autowired
	private ProductStatService productStatService;

	@Override
	public Long buildSearch(String keyWord, Integer curPage, Integer pageSize) throws Exception {
		SearchHisDto searchHisDto = new SearchHisDto();
		searchHisDto.setQueryWord(keyWord);
		searchHisDto.setQuerySolr(getSolrQueryParams(keyWord, curPage, pageSize));
		searchHisDto.setStatus(SearchHisDto.STATUS_NEW);
		searchHisDto.setCreateTime(new Date());
		searchHisDto.setUpdateTime(searchHisDto.getCreateTime());
		return searchHisService.saveSearchHisDtoAndGetId(searchHisDto).getId();
	}

	// q=copyText%3A%E7%89%9B%E5%A5%B6&start=1&rows=8&fl=siteId%2CproductCode%2CproductName%2CproductBrand&wt=xml&indent=true
	private String getSolrQueryParams(String keyWord, Integer curPage, Integer pageSize) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("q=");
		sb.append(getQueryWord(keyWord));
		sb.append("&start=");
		sb.append(getStart(curPage, pageSize));
		sb.append("&rows=");
		sb.append(pageSize);
		sb.append("&fl=");
		sb.append(getFields(keyWord));
		// sb.append("&wt=json&indent=true");
		return sb.toString();
	}

	private String getFields(String keyWord) {
		return "siteId%2cproductCode%2cproductName%2cproductUrl%2cmarketPrice%2cimgUrl%2cunionUrl%2ccategoryNav";
	}

	private String getQueryWord(String keyWord) throws UnsupportedEncodingException {
		String qWord = null;
		if (keyWord.startsWith("http:") || keyWord.startsWith("https:")) {
			qWord = "productUrl:" + keyWord + "*";
		} else if (keyWord.matches("[0-9a-zA-Z]{4,}")) {
			qWord = "productCode:" + keyWord + " OR copyText:" + keyWord;
		} else {
			qWord = "copyText:" + keyWord;
		}
		return URLEncoder.encode(qWord, "UTF-8");
	}

	private Object getStart(Integer curPage, Integer pageSize) {
		return (curPage - 1) * pageSize;
	}

	@Override
	public ActionReturnVo getSearchResult(Long searchId) {
		ActionReturnVo returnVo = new ActionReturnVo();
		List<Long> idList = new ArrayList<Long>(1);
		idList.add(searchId);
		List<SearchHisDto> dataList = searchHisService.getSearchHisDtoByIds(idList);
		if (dataList.isEmpty()) {
			returnVo.setCode(SearchHisDto.STATUS_DONE);
			returnVo.setMsg("not found searchId:" + searchId);
		} else {
			SearchHisDto hasDto = dataList.get(0);
			returnVo.setCode(hasDto.getStatus());
			if (hasDto.getStatus() == SearchHisDto.STATUS_DONE) {
				returnVo.setMsg("success");
				JSONObject rObject = handleResult(hasDto.getQueryResult());
				returnVo.setData(rObject.toString());
			}
		}
		return returnVo;
	}

	private JSONObject handleResult(String queryResult) {
		JSONObject rObject = JSONUtils.getJSONObject(queryResult);
		Integer numFound = JSONUtils.getInteger(rObject, "numFound");
		if (numFound < 1) {
			return rObject;
		}
		JSONArray docArray = JSONUtils.get(rObject, "docs");
		Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
		Map<String, JSONObject> keyObjectMap = new HashMap<String, JSONObject>();
		for (int i = 0; i < docArray.length(); i++) {
			JSONObject dObject = null;
			try {
				dObject = docArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			Integer siteId = JSONUtils.getInteger(dObject, "siteId");
			String productCode = JSONUtils.getString(dObject, "productCode");
			Set<String> codeSet = siteCodeMap.get(siteId);
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(siteId, codeSet);
			}
			codeSet.add(productCode);
			String key = siteId + ":" + productCode;
			keyObjectMap.put(key, dObject);
		}
		for (Entry<Integer, Set<String>> entry : siteCodeMap.entrySet()) {
			List<ProductStatDto> statList = productStatService.getProductStatDtos(new ArrayList<String>(entry.getValue()), entry.getKey(), 1);
			for (ProductStatDto stat : statList) {
				String key = stat.getSiteId() + ":" + stat.getProductCode();
				JSONObject dObject = keyObjectMap.get(key);
				JSONUtils.put(dObject, "productPrice", stat.getProductPrice());
			}
		}
		return rObject;
	}

}
