package com.lezo.idober.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.service.SearchActionService;
import com.lezo.idober.solr.EmbeddedSolrServerHolder;
import com.lezo.idober.solr.SolrQueryFactory;
import com.lezo.idober.vo.ActionReturnVo;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Controller
@RequestMapping("search")
public class SearchController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(SearchController.class);
    private ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);
    private ProductService productService = SpringBeanUtils.getBean(ProductService.class);
    private SearchActionService searchActionService = SpringBeanUtils.getBean(SearchActionService.class);
    private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("build")
    public ModelAndView buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(value = "q") String keyWord,
            @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize)
            throws Exception {
        long start = System.currentTimeMillis();
        keyWord = keyWord.trim();
        Matcher matcher = NUM_REG.matcher(keyWord);
        if (matcher.find()) {
            model.put("code", keyWord);
            model.addAttribute("qWord", keyWord);
            return new ModelAndView("redirect:/union/jd", model);
        }
        SolrQuery solrQuery = SolrQueryFactory.newSolrQuery(keyWord, curPage, pageSize);
        EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
        QueryResponse respone = server.query(solrQuery);
        SolrDocumentList docs = respone.getResults();
        mapper.setSerializationInclusion(Inclusion.NON_NULL);
        // 不序列化null
        String qResponse = mapper.writeValueAsString(docs);
        logger.info("msg:" + qResponse);
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", qResponse);
        long cost = System.currentTimeMillis() - start;
        logger.info("search:{},page:{},cost:{}", keyWord, curPage, cost);

        return new ModelAndView("searchList");
    }

    @RequestMapping("query")
    @ResponseBody
    public ActionReturnVo getSearchResult(@ModelAttribute("model") ModelMap model,
            @RequestParam(value = "sid") Long searchId) {
        long start = System.currentTimeMillis();
        long cost = System.currentTimeMillis() - start;
        ActionReturnVo vo = searchActionService.getSearchResult(searchId);
        // ObjectMapper mapper = new ObjectMapper();
        // StringWriter writer = new StringWriter();
        // mapper.writeValue(writer, vo);
        return vo;
    }
}