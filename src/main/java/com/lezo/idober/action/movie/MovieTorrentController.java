package com.lezo.idober.action.movie;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("doc")
public class MovieTorrentController {

    @RequestMapping("torrent")
    @ResponseBody
    public String addTorrent() throws Exception {
        int offset = 0;
        int limit = 1000;
        Set<String> urlSet = Sets.newHashSet();
        while (true) {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(offset);
            solrQuery.setRows(limit);
            solrQuery.set("q", "tcount:[1 TO *]");
            solrQuery.addField("content");
            QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
            List<MovieSolr> mSolrs = resp.getBeans(MovieSolr.class);
            offset += mSolrs.size();
            for (MovieSolr ms : mSolrs) {
                String content = ms.getContent();
                JSONObject oContent = JSONObject.parseObject(content);
                String sUrl = oContent.getString("source_url");
                if (StringUtils.isNotBlank(sUrl)) {
                    urlSet.add(sUrl);
                }
            }

            if (mSolrs.size() < limit) {
                break;
            }
        }
        FileUtils.writeLines(new File("./addTorrents.txt"), urlSet);
        return "total=" + offset + ",url:" + urlSet.size();
    }
}
