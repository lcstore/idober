package com.lezo.idober.action.movie;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
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
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("movie")
@Controller
@Log4j
public class UnifyMovieHomeController {

    @RequestMapping(value = { "/", "movie/" }, method = RequestMethod.GET)
    public String getHotMovie(@ModelAttribute("model") ModelMap model) throws Exception {
        try {
            Integer offset = 0;
            Integer limit = 100;
            String type = null;
            JSONObject dataObject = null;
            SolrDocumentList docList = null;
            String fields = null;
            Map<String, String> regionMap = Maps.newHashMap();
            regionMap.put("hot-huayu", "华语");
            regionMap.put("hot-oumei", "欧美");
            regionMap.put("hot-hanguo", "韩国");
            regionMap.put("hot-riben", "日本");
            regionMap.clear();
            fields = "id,name,release";
            JSONArray rankArray = new JSONArray();
            for (Entry<String, String> entry : regionMap.entrySet()) {
                type = "idober-movie-id-" + entry.getKey();
                docList = queryMovieByIdType(fields, type, offset, limit);
                JSONObject rankObject = new JSONObject();
                rankObject.put("name", entry.getValue());
                rankObject.put("dataList", docList);
                rankArray.add(rankObject);
            }
            model.addAttribute("rankArray", rankArray);

            type = "idober-movie-id-newly";
            limit = 36;
            fields = "id,name,release";
            docList = queryMovieByIdType(fields, type, offset, limit);
            dataObject = new JSONObject();
            dataObject.put("name", "最新电影");
            dataObject.put("dataList", docList);
            model.addAttribute("newlyObj", dataObject);

            type = "idober-movie-id-playing";
            limit = 12;
            fields = "id,name,release,image,torrents_size,shares_size";
            docList = queryMovieByIdType(fields, type, offset, limit);
            dataObject = new JSONObject();
            dataObject.put("name", "正在热播");
            dataObject.put("dataList", docList);
            model.addAttribute("playingObj", dataObject);

            type = "idober-movie-id-upcoming";
            limit = 12;
            fields = "id,name,release,image,torrents_size,shares_size";
            docList = queryMovieByIdType(fields, type, offset, limit);
            dataObject = new JSONObject();
            dataObject.put("name", "即将上映");
            dataObject.put("dataList", docList);
            model.addAttribute("upcomingObj", dataObject);

            type = "idober-movie-id-classic";
            limit = 12;
            fields = "id,name,release,image";
            docList = queryMovieByIdType(fields, type, offset, limit);
            dataObject = new JSONObject();
            dataObject.put("name", "经典电影");
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

            return "MovieHome";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "MovieHome";
    }

    private SolrDocumentList queryMovieByIdType(String fields, String type,
            Integer offset, Integer limit) throws Exception {
        List<DataSolr> enRanks = queryIdMetaByType(type, offset, 1);
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

    private List<DataSolr> queryIdMetaByType(String type, Integer offset, Integer limit) throws Exception {
        if (StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", "(type:" + type + ")");
        solrQuery.setSort("date_s", ORDER.desc);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);
        return resp.getBeans(DataSolr.class);
    }

    private SolrDocumentList
            queryMovieSolrByIds(String fields, List<String> idList, Integer offset, Integer limit) throws Exception {
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
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
        return resp.getResults();
    }
}
