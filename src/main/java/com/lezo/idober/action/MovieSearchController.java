package com.lezo.idober.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrConstant;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.movie.MovieVo;

@Controller
@RequestMapping("search")
public class MovieSearchController extends BaseController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(MovieSearchController.class);

    @RequestMapping("movie")
    public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
            @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
            throws Exception {
        long start = System.currentTimeMillis();
        pageSize = pageSize < 12 ? 12 : pageSize;
        int offset = (curPage - 1) * pageSize;
        int limit = pageSize;
        offset = offset < 0 ? 0 : offset;
        keyWord = keyWord.trim();
        List<MovieVo> movieVos = queryDocByWord(keyWord, offset, limit);
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", movieVos);
        long cost = System.currentTimeMillis() - start;
        logger.info("search:{},page:{},count:{},cost:{}", keyWord, curPage, movieVos.size(), cost);

        return new ModelAndView("MovieSearch");
    }

    private List<MovieVo> queryDocByWord(String keyWord, int offset, int limit) throws Exception {
        if (StringUtils.isBlank(keyWord)) {
            return Collections.emptyList();
        }
        keyWord = ClientUtils.escapeQueryChars(keyWord);
        SolrQuery solrQuery = new SolrQuery(SolrConstant.SORL_QUERY_DEFAULT_FRANGE);
        String queryString = "names:" + keyWord;
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("qq", queryString);
        solrQuery.addField(MovieSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
        List<MovieSolr> solrList = resp.getBeans(MovieSolr.class);
        List<MovieVo> destList = Lists.newArrayList();
        for (MovieSolr solr : solrList) {
            MovieVo movieVo = new MovieVo();
            BeanUtils.copyProperties(solr, movieVo);
            JSONObject ctObject = JSONObject.parseObject(solr.getContent());
            if (ctObject != null) {
                movieVo.setStory(ctObject.getString("story"));
                if (StringUtils.isBlank(movieVo.getImgUrl())) {
                    movieVo.setImgUrl(ctObject.getString("img_url"));
                }
            }
            if (StringUtils.isNotBlank(solr.getTorrents())) {
                JSONArray tArray = JSONArray.parseArray(solr.getTorrents());
                JSONArray sArray = new JSONArray();
                JSONArray newArray = new JSONArray();
                for (int i = 0; i < tArray.size(); i++) {
                    JSONObject tObject = tArray.getJSONObject(i);
                    String type = tObject.getString("type");
                    String source = tObject.getString("source");
                    if (StringUtils.isBlank(type) && source.equals("bttiantang-torrent")) {
                        tObject.remove("source");
                        tObject.put("type", source);
                        JSONObject pObject = tObject.getJSONObject("param");
                        StringBuilder sb = new StringBuilder();
                        for (String key : pObject.keySet()) {
                            if (sb.length() > 0) {
                                sb.append("&");
                            }
                            sb.append(key);
                            sb.append("=");
                            sb.append(pObject.getString(key));
                        }
                        tObject.put("param", sb.toString());
                        tObject.put("url", "");
                        if (newArray.isEmpty()) {
                            newArray.add(tObject);
                        }
                    } else if (type != null && type.endsWith("-share")) {
                        sArray.add(tObject);
                    } else if (newArray.isEmpty()) {
                        newArray.add(tObject);
                    }
                }
                movieVo.setTorrents(newArray.toJSONString());
                movieVo.setShares(sArray.toJSONString());
            }
            destList.add(movieVo);
        }
        Comparator<MovieVo> c = new Comparator<MovieVo>() {
            @Override
            public int compare(MovieVo o1, MovieVo o2) {
                if (o2.getTcount() > 0 && o1.getTcount() > 0) {
                    return o2.getYear().compareTo(o1.getYear());
                }
                return o2.getTcount() - o1.getTcount();
            }
        };
        Collections.sort(destList, c);
        return destList;
    }
}
