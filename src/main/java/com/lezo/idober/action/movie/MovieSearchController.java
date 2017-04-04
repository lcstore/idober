package com.lezo.idober.action.movie;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.DocUtils;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("search")
public class MovieSearchController extends BaseController {

    @RequestMapping("movie")
    public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
            @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
            throws Exception {
        pageSize = pageSize < 12 ? 12 : pageSize;
        int offset = (curPage - 1) * pageSize;
        int limit = pageSize;
        offset = offset < 0 ? 0 : offset;
        keyWord = keyWord.trim();
        JSONObject documentList = queryDocByWord(keyWord, offset, limit);
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", documentList);
        return new ModelAndView("MovieSearch");
    }

    private JSONObject queryDocByWord(String keyWord, int offset, int limit) throws Exception {
        if (StringUtils.isBlank(keyWord)) {
            return new JSONObject();
        }
        keyWord = ClientUtils.escapeQueryChars(keyWord);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", keyWord);
        solrQuery.set("spellcheck", "true");
		solrQuery.set("spellcheck.count", "10");
        solrQuery.addFilterQuery("type:movie");
        // solrQuery.addFilterQuery("(torrents_size:[1 TO *] OR shares_size:[1 TO *])");
        // 发布时间在2个月之后，且无下载地址的电影不显示
        solrQuery.addFilterQuery("!(release:[NOW+60DAY/DAY TO *] AND torrents_size:0 AND shares_size:0)");
        solrQuery.addSort("release", ORDER.desc);
        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
        String sAlterToken = null;
        if (CollectionUtils.isEmpty(resp.getResults())) {
            SpellCheckResponse sResp = resp.getSpellCheckResponse();
            if (sResp != null && CollectionUtils.isNotEmpty(sResp.getSuggestions())) {
                List<Suggestion> suggestions = sResp.getSuggestions();
                Comparator<Suggestion> c = new Comparator<SpellCheckResponse.Suggestion>() {
                    @Override
                    public int compare(Suggestion o1, Suggestion o2) {
                        return o2.getToken().length() - o1.getToken().length();
                    }
                };
                Collections.sort(suggestions, c);
                Suggestion maxSuggestion = suggestions.get(0);
                List<String> alters = maxSuggestion.getAlternatives();
                Comparator<String> cc = new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o2.length() - o1.length();
                    }
                };
                Collections.sort(alters, cc);
                sAlterToken = alters.get(0);
            }
        }
        DocUtils.changeImage(resp.getResults());
        JSONObject rsObj = new JSONObject();
        rsObj.put("dataList", resp.getResults());
        if (sAlterToken != null) {
            JSONObject sugObj = queryDocByWord(sAlterToken, offset, limit);
            sugObj.put("suggestion", sAlterToken);
            return sugObj;
        } else {
            return rsObj;
        }
    }
}
