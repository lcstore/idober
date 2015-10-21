package com.lezo.idober.action;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ItemVo;

@Controller
@RequestMapping("search")
public class SearchController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(SearchController.class);
    private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
    private static String ITEM_SEARCH_FIELDS;

    static {
        StringBuilder sb = new StringBuilder();
        for (Field fld : ItemVo.class.getDeclaredFields()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(fld.getName());
        }
        ITEM_SEARCH_FIELDS = sb.toString();
    }

    @RequestMapping("build")
    public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
            @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
            throws Exception {
        long start = System.currentTimeMillis();
        keyWord = keyWord.trim();
        if (keyWord.startsWith("http")) {
            model.put("url", keyWord);
            model.addAttribute("qWord", keyWord);
            return new ModelAndView("redirect:/union/", model);
        }
        Matcher matcher = NUM_REG.matcher(keyWord);
        if (matcher.find()) {
            model.put("code", keyWord);
            model.addAttribute("qWord", keyWord);
            return new ModelAndView("redirect:/union/jd", model);
        }
        List<ItemVo> itemList = queryDocByWord(keyWord);
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", itemList);
        long cost = System.currentTimeMillis() - start;
        logger.info("search:{},page:{},count:{},cost:{}", keyWord, curPage, itemList.size(), cost);

        return new ModelAndView("searchList");
    }

    private List<ItemVo> queryDocByWord(String keyWord) throws Exception {
        if (StringUtils.isBlank(keyWord)) {
            return Collections.emptyList();
        }
        SolrQuery solrQuery = new SolrQuery(keyWord);
        String queryString =
                "group=true&group.field=itemCode&group.query=stockNum:[1%20TO%20*]&group.main=true&group.sort=commentNum%20desc&group.sort=score%20desc";
        SolrParams params = SolrRequestParsers.parseQueryString(queryString);
        solrQuery.add(params);
        solrQuery.add("group.offset", "0");
        solrQuery.add("group.limit", "10");
        solrQuery.addField(ITEM_SEARCH_FIELDS);
        QueryResponse resp = SolrUtils.getSolrServer().query(solrQuery);
        return resp.getBeans(ItemVo.class);
    }

}