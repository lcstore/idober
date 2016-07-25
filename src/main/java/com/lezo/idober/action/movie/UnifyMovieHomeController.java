package com.lezo.idober.action.movie;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.movie.MovieElementVo;
import com.lezo.iscript.utils.BatchIterator;

@RequestMapping("movie")
@Controller
@Log4j
public class UnifyMovieHomeController {

	@RequestMapping(value = { "/", "movie/" }, method = RequestMethod.GET)
	public String getHotMovie(@ModelAttribute("model") ModelMap model) throws Exception {
		try {
			Integer offset = 0;
			Integer limit = 1000;

			List<MovieElementVo> enRankVos = queryMovieElementVo("票房榜-北美", offset, limit);
			model.addAttribute("enRankVos", enRankVos);
			List<MovieElementVo> cnRankVos = queryMovieElementVo("票房榜-全国", offset, limit);
			model.addAttribute("cnRankVos", cnRankVos);
			List<MovieElementVo> allRankVos = queryMovieElementVo("票房榜-综合", offset, limit);
			model.addAttribute("allRankVos", allRankVos);

			JSONObject dailyObject = getMovieDaily();
			model.addAttribute("dailyObject", dailyObject);
			JSONObject weeklyObject = getMovieWeeky();
			model.addAttribute("weeklyObject", weeklyObject);

			List<MovieElementVo> newMovieVos = queryMovieElementVo("即将上映", offset, limit);
			model.addAttribute("newMovieVos", newMovieVos);

			List<MovieElementVo> classicVos = queryMovieElementVo("经典电影", offset, limit);
			model.addAttribute("classicVos", classicVos);
			// search,搜索框默认搜索词
			if (CollectionUtils.isNotEmpty(allRankVos)) {
				Random random = new Random();
				int index = random.nextInt(allRankVos.size());
				MovieElementVo movieVo = allRankVos.get(index);
				model.addAttribute("qWord", movieVo.getTitle());
			}

			return "MovieHome";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "MovieHome";
	}

	private List<MovieElementVo> queryMovieElementVo(String sGroup, Integer offset, Integer limit) throws Exception {
		List<DataSolr> enRanks = queryDataMovieIdByGroup(sGroup, offset, 1);
		List<String> idList = toIdListByMovieHome(enRanks);
		List<MovieSolr> enRankMovies = queryMovieSolrByIds(idList, offset, limit);
		List<MovieElementVo> enRankVos = toMovieRankVo(enRankMovies);
		return enRankVos;
	}

	private List<String> toIdListByMovieHome(List<DataSolr> homeSolrs) {
		if (homeSolrs == null) {
			return Collections.emptyList();
		}
		List<String> idList = Lists.newArrayList();
		for (DataSolr hs : homeSolrs) {
			JSONObject dObject = JSON.parseObject(hs.getContent());
			JSONArray dataArr = dObject.getJSONArray("dataList");
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

	/**
	 * douban-movieheat
	 * 
	 * @param newSolrs
	 * @return
	 */
	private List<JSONObject> toParamByHeat(List<DataSolr> newSolrs) {
		List<JSONObject> paramList = Lists.newArrayList();
		for (DataSolr solr : newSolrs) {
			JSONObject pObj = new JSONObject();
			pObj.put("name", solr.getTitle());
			try {
				JSONObject ctObject = JSON.parseObject(solr.getContent());
				String sYear = ctObject.getString("release_year");
				if (StringUtils.isBlank(sYear)) {
					continue;
				}
				Date date = DateUtils.parseDate(sYear, "yyyy");
				pObj.put("date", date);
				paramList.add(pObj);
			} catch (Exception e) {
				log.warn("error group:" + solr.getGroup() + ",id:" + solr.getId());
				e.printStackTrace();
			}
		}
		return paramList;
	}

	private List<JSONObject> toParamByHoting(List<DataSolr> newSolrs) {
		List<JSONObject> paramList = Lists.newArrayList();
		for (DataSolr solr : newSolrs) {
			JSONObject pObj = new JSONObject();
			pObj.put("name", solr.getTitle());
			try {
				Date date = DateUtils.parseDate(solr.getGroup(), "yyyyMMdd");
				pObj.put("date", date);
				paramList.add(pObj);
			} catch (Exception e) {
				log.warn("error group:" + solr.getGroup() + ",id:" + solr.getId());
				e.printStackTrace();
			}
		}
		return paramList;
	}

	private List<DataSolr> queryDataSolrMovieNews(String group, Integer offset, int limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(type:mtime-hoting)");
		solrQuery.setSort("group", ORDER.desc);
		// solrQuery.setSort("ranking", ORDER.desc);
		// solrQuery.addField("id,title,group,ranking");
		solrQuery.addField(DataSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
		return resp.getBeans(DataSolr.class);
	}

	private JSONObject getMovieWeeky() throws Exception {
		Integer offset = 0;
		Integer limit = 12;
		List<MovieElementVo> enRankVos = queryMovieElementVo("本周热门", offset, limit);

		JSONObject dObject = new JSONObject();
		dObject.put("dataList", enRankVos);
		dObject.put("total", 0);
		return dObject;
	}

	private JSONObject getMovieDaily() throws Exception {
		Integer offset = 0;
		Integer limit = 36;
		int total = 0;
		List<MovieElementVo> dailyRankVos = Collections.emptyList();
		List<DataSolr> solrs = queryDataMovieIdByGroup("今日更新", offset, 1);
		if (CollectionUtils.isNotEmpty(solrs)) {
			List<String> idList = toIdListByMovieHome(solrs);
			List<MovieSolr> mSolrs = queryMovieSolrByIds(idList, offset, limit);
			dailyRankVos = toMovieRankVo(mSolrs);
			JSONObject rsObj = JSON.parseObject(solrs.get(0).getContent());
			total = rsObj.getIntValue("total");
		}
		JSONObject dObject = new JSONObject();
		dObject.put("dailyRankVos", dailyRankVos);
		dObject.put("total", total);
		return dObject;
	}

	private List<MovieElementVo> toMovieRankVo(List<MovieSolr> enRankMovies) {
		List<MovieElementVo> enRankVos = Lists.newArrayList();
		long hasMills = System.currentTimeMillis() - DateUtils.addDays(new Date(), -15).getTime();
		for (MovieSolr rankMovie : enRankMovies) {
			MovieElementVo rankVo = new MovieElementVo();
			rankVo.setTitle(rankMovie.getName());
			rankVo.setCode(rankMovie.getId());
			rankVo.setUpdateTime(rankMovie.getTimestamp());
			rankVo.setImgUrl(rankMovie.getImgUrl());
			if (StringUtils.isBlank(rankVo.getImgUrl())) {
				JSONObject cObject = JSON.parseObject(rankMovie.getContent());
				if (cObject != null) {
					rankVo.setImgUrl(cObject.getString("img_url"));
				}
			}
			if (rankMovie.getDate() != null
					&& System.currentTimeMillis() - rankMovie.getDate().getTime() < hasMills) {
				rankVo.setIsNew(1);
			}
			rankVo.setTcount(rankMovie.getTcount());
			enRankVos.add(rankVo);
		}
		return enRankVos;
	}

	private List<MovieSolr> queryMovieSolrByNames(List<DataSolr> rankSolrs, Integer offset, Integer limit)
			throws Exception {
		BatchIterator<DataSolr> it = new BatchIterator<DataSolr>(rankSolrs, 10);
		List<MovieSolr> movieSolrs = Lists.newArrayList();
		while (it.hasNext()) {
			List<DataSolr> querySolrs = it.next();
			List<MovieSolr> hasList = queryMovieSolrByLimitNames(querySolrs, offset, limit);
			if (CollectionUtils.isNotEmpty(hasList)) {
				movieSolrs.addAll(hasList);
			}
		}
		return movieSolrs;
	}

	private List<MovieSolr> queryMovieSolrByLimitNames(List<DataSolr> rankSolrs,
			Integer offset, Integer limit) throws Exception {
		if (CollectionUtils.isEmpty(rankSolrs)) {
			return Collections.emptyList();
		}
		Pattern oDateReg = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (DataSolr rs : rankSolrs) {
			String title = rs.getTitle();
			title = ClientUtils.escapeQueryChars(title);
			JSONObject mObject = JSON.parseObject(rs.getContent());
			String sDate = mObject.getString("date");
			sDate = sDate != null ? sDate : mObject.getString("time");
			if (StringUtils.isEmpty(sDate)) {
				String sYear = mObject.getString("year");
				if (StringUtils.isEmpty(sYear)) {
					log.warn("no date or year.id:" + rs.getId() + ",title:" + rs.getTitle());
					continue;
				}
				sDate = sYear + "-01-01";
			}
			sDate = sDate.replace("年", "-");
			sDate = sDate.replace("月", "-");
			Matcher matcher = oDateReg.matcher(sDate);
			if (matcher.find()) {
				sDate = matcher.group();
			} else {
				log.warn("error date:" + sDate + ",id:" + rs.getId() + ",title:" + rs.getTitle());
				continue;
			}
			Date date = DateUtils.parseDate(sDate, "yyyy-MM-dd");
			Date fromDate = DateUtils.addMonths(date, -3);
			Date toDate = DateUtils.addMonths(date, 3);
			// String sFromDate = TrieDateField.formatExternal(fromDate);
			String sFromDate = DateUtil.getThreadLocalDateFormat().format(fromDate);
			String sToDate = DateUtil.getThreadLocalDateFormat().format(toDate);
			if (sb.length() > 1) {
				sb.append(" OR ");
			}
			sb.append("(names:");
			sb.append(title);
			sb.append(" AND date:[");
			sb.append(sFromDate);
			sb.append(" TO ");
			sb.append(sToDate);
			sb.append("])");
		}
		sb.append(")");
		if (sb.length() <= 2) {
			return Collections.emptyList();
		}
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", sb.toString());
		solrQuery.addField(MovieSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
		return resp.getBeans(MovieSolr.class);
	}

	private List<MovieSolr> queryMovieSolrByNameDates(List<JSONObject> jsonList,
			Integer offset, Integer limit) throws Exception {
		if (CollectionUtils.isEmpty(jsonList)) {
			return Collections.emptyList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (JSONObject rs : jsonList) {
			String title = rs.getString("name");
			title = ClientUtils.escapeQueryChars(title);
			Date date = rs.getDate("date");
			Date fromDate = DateUtils.addMonths(date, -3);
			Date toDate = DateUtils.addMonths(date, 3);
			// String sFromDate = TrieDateField.formatExternal(fromDate);
			String sFromDate = DateUtil.getThreadLocalDateFormat().format(fromDate);
			String sToDate = DateUtil.getThreadLocalDateFormat().format(toDate);
			if (sb.length() > 1) {
				sb.append(" OR ");
			}
			sb.append("(names:");
			sb.append(title);
			sb.append(" AND date:[");
			sb.append(sFromDate);
			sb.append(" TO ");
			sb.append(sToDate);
			sb.append("])");
		}
		sb.append(")");
		if (sb.length() <= 2) {
			return Collections.emptyList();
		}
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", sb.toString());
		solrQuery.addField(MovieSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
		return resp.getBeans(MovieSolr.class);
	}

	private List<DataSolr> queryDataMovieIdByGroup(String sGroup,
			Integer offset, Integer limit) throws Exception {
		if (StringUtils.isBlank(sGroup)) {
			return Collections.emptyList();
		}
		sGroup = ClientUtils.escapeQueryChars(sGroup);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(type:idober-movie-home AND group:" + sGroup + ")");
		solrQuery.setSort("timestamp", ORDER.desc);
		solrQuery.addField(DataSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
		return resp.getBeans(DataSolr.class);
	}

	private List<MovieSolr> queryMovieSolrByIds(List<String> idList,
			Integer offset, Integer limit) throws Exception {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
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
		solrQuery.addField(MovieSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
		List<MovieSolr> mSolrs = resp.getBeans(MovieSolr.class);

		// keep origin order
		Map<String, MovieSolr> idSolrMap = Maps.newHashMap();
		for (MovieSolr ms : mSolrs) {
			idSolrMap.put(ms.getId(), ms);
		}
		List<MovieSolr> sortSolrs = Lists.newArrayList();
		for (String idString : idList) {
			MovieSolr mSolr = idSolrMap.get(idString);
			if (mSolr != null) {
				sortSolrs.add(mSolr);
			}
		}
		return sortSolrs;
	}

	private List<DataSolr> queryDataSolrMovieRanks(String group, Integer offset, Integer limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(type:douban-movierank AND group:" + group + ")");
		solrQuery.setSort("ranking", ORDER.asc);
		solrQuery.addField(DataSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
		return resp.getBeans(DataSolr.class);
	}

	private List<DataSolr> queryDataSolrMoviePraises(String group, Integer offset, Integer limit) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.set("q", "(type:douban-moviepraise)");
		solrQuery.setSort("group", ORDER.desc);
		solrQuery.setSort("ranking", ORDER.desc);
		// solrQuery.addField("id,title,group,ranking");
		solrQuery.addField(DataSolr.getSolrFields());
		QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
		return resp.getBeans(DataSolr.class);
	}
}
