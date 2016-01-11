package com.lezo.idober.action;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrConstant;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.MovieVo;

@Controller
@RequestMapping("search")
public class MovieSearchController {
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

        return new ModelAndView("searchMovie");
    }

    private List<MovieVo> queryDocByWord(String keyWord, int offset, int limit) throws Exception {
        if (StringUtils.isBlank(keyWord)) {
            return Collections.emptyList();
        }
        SolrQuery solrQuery = new SolrQuery(SolrConstant.SORL_QUERY_DEFAULT_FRANGE);
        String queryString = "title:" + keyWord;
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("qq", queryString);
        solrQuery.addField(MovieSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
        List<MovieSolr> solrList = resp.getBeans(MovieSolr.class);
        List<MovieVo> destList = Lists.newArrayList();
        for (MovieSolr solr : solrList) {
            MovieVo movieVo = new MovieVo();
            movieVo.setTitle(solr.getTitle());
            movieVo.setType(solr.getType());
            com.alibaba.fastjson.JSONObject dObject = JSON.parseObject(solr.getContent());
            String uk = dObject.getString("uk");
            movieVo.setImgUrl(dObject.getString("avatar_url"));
            if (dObject.containsKey("shorturl")) {
                String sUrl = "http://yun.baidu.com/s/" + dObject.getString("shorturl");
                movieVo.setUrl(sUrl);
            } else if (dObject.containsKey("album_id")) {
                movieVo.setClassify(MovieVo.CLASSIFY_GROUP);
                String sUrl = "http://yun.baidu.com/pcloud/album/info?uk=" + uk + "&album_id="
                                + dObject.getString("album_id");
                movieVo.setUrl(sUrl);
            } else {
                String sUrl = "http://yun.baidu.com/share/link?uk=" + uk + "&shareid=" + dObject.getString("shareid");
                movieVo.setUrl(sUrl);
            }
            destList.add(movieVo);
        }
        return destList;
    }

}
