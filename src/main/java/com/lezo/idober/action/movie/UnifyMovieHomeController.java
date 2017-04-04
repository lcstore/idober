package com.lezo.idober.action.movie;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.SolrUtils;

//@RequestMapping("movie")
@Controller
@Log4j
public class UnifyMovieHomeController {
	private static final Comparator<SolrDocument> DOC_RATE_DESC = new Comparator<SolrDocument>() {
		@Override
		public int compare(SolrDocument o1, SolrDocument o2) {
			Object lRateObj = o1.getFieldValue("rate");
			Object rRateObj = o2.getFieldValue("rate");
			if (lRateObj == null) {
				return -1;
			}
			if (rRateObj == null) {
				return 1;
			}
			Float lRate = NumberUtils.toFloat(lRateObj.toString());
			Float rRate = NumberUtils.toFloat(rRateObj.toString());
			return rRate.compareTo(lRate);
		}
	};

	@RequestMapping(value = { "/", "movie/" }, method = RequestMethod.GET)
	public ModelAndView loadHome(@ModelAttribute("model") ModelMap model) throws Exception {
		try {
			Integer offset = 0;
			Integer limit = 100;
			String group = null;
			JSONObject dataObject = null;
			SolrDocumentList docList = null;
			String fields = null;
			Map<String, String> regionMap = Maps.newLinkedHashMap();
			regionMap.put("hot-huayu", "华语");
			regionMap.put("hot-oumei", "欧美");
			regionMap.put("hot-hanguo", "韩国");
			regionMap.put("hot-riben", "日本");
			// regionMap.clear();
			fields = "id,name,rate";
			limit = 30;
			JSONArray rankArray = new JSONArray();
			for (Entry<String, String> entry : regionMap.entrySet()) {
				group = entry.getKey();
				docList = queryMovieIdByGroup(fields, group, offset, limit);
				Collections.sort(docList, DOC_RATE_DESC);
				JSONObject rankObject = new JSONObject();
				rankObject.put("name", entry.getValue());
				rankObject.put("dataList", docList);
				rankArray.add(rankObject);
			}
			model.addAttribute("rankList", rankArray);

			group = "newly";
			limit = 36;
			fields = "id,name,timestamp";
			List<DataSolr> enRanks = queryIdMetaByGroup(group, offset, 1);
			List<String> idList = toIdListByMovieHome(enRanks);
			List<String> fieldList = Lists.newArrayList("timestamp");
			docList = queryMovieSolrByIds(fields, idList, offset, limit, fieldList);
			// docList = queryMovieIdByGroup(fields, group, offset, limit);
			dataObject = new JSONObject();
			dataObject.put("name", "最新电影");
			dataObject.put("dataList", docList);
			model.addAttribute("newlyObj", dataObject);

			group = "playing";
			limit = 12;
			fields = "id,name,release,image,torrents_size,shares_size";
			fieldList = Lists.newArrayList("torrents_size", "shares_size");
			enRanks = queryIdMetaByGroup(group, offset, 1);
			idList = toIdListByMovieHome(enRanks);
			docList = queryMovieSolrByIds(fields, idList, offset, limit, fieldList);
			dataObject = new JSONObject();
			dataObject.put("name", "正在热播");
			dataObject.put("dataList", docList);
			model.addAttribute("playingObj", dataObject);

			group = "upcoming";
			limit = 6;
			fields = "id,name,release,image,torrents_size,shares_size";
			enRanks = queryIdMetaByGroup(group, offset, 1);
			idList = toIdListByMovieHome(enRanks);
			docList = queryMovieSolrByIds(fields, idList, offset, limit, fieldList);
			dataObject = new JSONObject();
			dataObject.put("name", "即将上映");
			dataObject.put("dataList", docList);
			model.addAttribute("upcomingObj", dataObject);

			List<String> groupList = Lists.newArrayList();
			groupList.add("hotly");// 热门
			groupList.add("latestly");// 最新
			groupList.add("classic");// 经典
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int groupIndex = hour % groupList.size();
			group = groupList.get(groupIndex);
			limit = 24;
			fields = "id,name,release,image";
			docList = queryMovieIdByGroup(fields, group, offset, limit);
			dataObject = new JSONObject();
			dataObject.put("name", "推荐电影");
			dataObject.put("dataList", docList);
			model.addAttribute("classicObj", dataObject);

			// search,搜索框默认搜索词
			JSONObject newlyObj = (JSONObject) model.get("newlyObj");
			JSONArray dArray = newlyObj.getJSONArray("dataList");
			if (dArray != null && dArray.size() > 0) {
				Random random = new Random();
				int index = random.nextInt(dArray.size());
				JSONObject docObject = dArray.getJSONObject(index);
				model.addAttribute("qWord", docObject.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelAndView modelAndView = new ModelAndView("MovieHome", model);
		return modelAndView;
	}

	private SolrDocumentList queryMovieIdByGroup(String fields, String group,
			Integer offset, Integer limit) throws Exception {
		List<DataSolr> enRanks = queryIdMetaByGroup(group, offset, 1);
		List<String> idList = toIdListByMovieHome(enRanks);
		return queryMovieSolrByIds(fields, idList, offset, limit);
	}

	private List<String> toIdListByMovieHome(List<DataSolr> homeSolrs) {
		if (homeSolrs == null) {
			return Collections.emptyList();
		}
		List<String> idList = Lists.newArrayList();
		for (DataSolr hs : homeSolrs) {
			JSONArray dataArr = JSON.parseArray(hs.getContent());
			if (dataArr == null) {
				continue;
			}
			for (int i = 0; i < dataArr.size(); i++) {
				String idString = dataArr.getString(i);
				idList.add(idString);
			}
		}
		return idList;
	}

	private List<DataSolr> queryIdMetaByGroup(String group, Integer offset, Integer limit) throws Exception {
		if (StringUtils.isBlank(group)) {
			return Collections.emptyList();
		}
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(group_s:" + group + ")");
		solrQuery.setSort("date_s", ORDER.desc);
		solrQuery.addFilterQuery("type:idober-movie-ids");
		solrQuery.addField(DataSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
		return resp.getBeans(DataSolr.class);
	}

	private SolrDocumentList queryMovieSolrByIds(String fields, List<String> idList,
			Integer offset, Integer limit) throws Exception {
		return queryMovieSolrByIds(fields, idList, offset, limit, null);
	}

	private SolrDocumentList queryMovieSolrByIds(String fields, List<String> idList,
			Integer offset, Integer limit, List<String> descFeilds) throws Exception {
		if (CollectionUtils.isEmpty(idList)) {
			return new SolrDocumentList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String idString : idList) {
			if (sb.length() > 1) {
				sb.append(" OR ");
			}
			sb.append("id:" + ClientUtils.escapeQueryChars(idString));
		}
		sb.append(")");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", sb.toString());
		if (StringUtils.isNotBlank(fields)) {
			solrQuery.addField(fields);
		}
		if (CollectionUtils.isNotEmpty(descFeilds)) {
			for (String field : descFeilds) {
				solrQuery.addSort(field, ORDER.desc);
			}
		}
		QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
		SolrDocumentList docList = resp.getResults();
		DocUtils.changeImage(docList);
		return docList;
	}
}
