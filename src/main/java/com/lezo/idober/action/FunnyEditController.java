package com.lezo.idober.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.solr.pojo.DataSolr;
import com.lezo.idober.utils.SolrUtils;

@Controller
@RequestMapping("editmgr")
public class FunnyEditController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(FunnyEditController.class);

    @RequestMapping("funny")
    public ModelAndView listFunnys(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q",
            defaultValue = "") String keyWord,
            @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
            throws Exception {
        long start = System.currentTimeMillis();
        pageSize = pageSize < 12 ? 12 : pageSize;
        int offset = (curPage - 1) * pageSize;
        int limit = pageSize;
        offset = offset < 0 ? 0 : offset;
        keyWord = keyWord.trim();
        SolrDocumentList movieVos = queryDocByWord(keyWord, offset, limit);
        model.addAttribute("qAction", "/editmgr/funny");
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", movieVos);
        long cost = System.currentTimeMillis() - start;
        logger.info("edit:{},page:{},count:{},cost:{}", keyWord, curPage, movieVos.size(), cost);
        return new ModelAndView("FunnyEditor");
    }

    private SolrDocumentList queryDocByWord(String keyWord, int offset, int limit) throws Exception {
        String query = "(type:wx-mpdetail)";
        if (StringUtils.isNotBlank(keyWord)) {
            keyWord = ClientUtils.escapeQueryChars(keyWord);
            query = "(type:wx-mpdetail AND title:" + keyWord + ")";
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.set("q", query);
        solrQuery.addSort("timestamp", ORDER.desc);
        solrQuery.addSort("ranking", ORDER.desc);
        solrQuery.addField(DataSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getDataServer().query(solrQuery);

        return resp.getResults();
    }
}
