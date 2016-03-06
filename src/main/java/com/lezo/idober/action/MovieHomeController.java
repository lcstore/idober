package com.lezo.idober.action;

import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.movie.MovieRankVo;
import com.lezo.iscript.utils.BatchIterator;

@RequestMapping("movie")
@Controller
@Log4j
public class MovieHomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHotMovie(@ModelAttribute("model") ModelMap model) throws Exception {
        try {
            String group = "北美";
            Integer offset = 0;
            Integer limit = 100;
            List<DataSolr> enRanks = queryDataSolrMovieRanks(group, offset, limit);
            List<MovieSolr> enRankMovies = queryMovieSolrByNames(enRanks, offset, 20);
            List<MovieRankVo> enRankVos = toMovieRankVo(enRankMovies);
            model.addAttribute("enRankVos", enRankVos);
            List<DataSolr> cnRanks = queryDataSolrMovieRanks("中国内地", offset, limit);
            List<MovieSolr> cnRankMovies = queryMovieSolrByNames(cnRanks, offset, 20);
            List<MovieRankVo> cnRankVos = toMovieRankVo(cnRankMovies);
            model.addAttribute("cnRankVos", cnRankVos);
            List<DataSolr> allRanks = queryDataSolrMovieRanks("*", offset, limit);
            List<MovieSolr> allRankMovies = queryMovieSolrByNames(allRanks, offset, 20);
            List<MovieRankVo> allRankVos = toMovieRankVo(allRankMovies);
            model.addAttribute("allRankVos", allRankVos);
            return "MovieHome";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "MovieHome";
    }

    private List<MovieRankVo> toMovieRankVo(List<MovieSolr> enRankMovies) {
        List<MovieRankVo> enRankVos = Lists.newArrayList();
        for (MovieSolr rankMovie : enRankMovies) {
            MovieRankVo rankVo = new MovieRankVo();
            rankVo.setTitle(rankMovie.getName());
            rankVo.setCode(rankMovie.getId());
            rankVo.setUpdateTime(rankMovie.getTimestamp());
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
        Pattern oDateReg = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (DataSolr rs : rankSolrs) {
            String title = rs.getTitle();
            title = ClientUtils.escapeQueryChars(title);
            JSONObject mObject = JSON.parseObject(rs.getContent());
            String sDate = mObject.getString("date");
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
            Date toDate = DateUtils.addMonths(date, 13);
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
}
