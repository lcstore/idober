package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
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
        List<String> itemCodes = queryItemCodes(keyWord);
        itemCodes = toPageItemCodes(itemCodes, curPage, pageSize);
        String qResponse = "";
        if (CollectionUtils.isNotEmpty(itemCodes)) {
            SolrDocumentList docs = queryDocByItemCodes(itemCodes);
            // 不序列化null
            mapper.setSerializationInclusion(Inclusion.NON_NULL);
            qResponse = mapper.writeValueAsString(docs);
            logger.info("msg:" + qResponse);
        }
        model.addAttribute("qWord", keyWord);
        model.addAttribute("qResponse", qResponse);
        long cost = System.currentTimeMillis() - start;
        // logger.info("search:{},page:{},cost:{}", keyWord, curPage, cost);

        return new ModelAndView("searchList");
    }

    private SolrDocumentList queryDocByItemCodes(List<String> itemCodes) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        StringBuilder sb = new StringBuilder();
        for (String itemCode : itemCodes) {
            if (sb.length() > 0) {
                sb.append("OR");
            }
            sb.append("(skuCode:");
            sb.append(itemCode);
            sb.append(")");
        }
        solrQuery.add("q", sb.toString());
        solrQuery.addField("matchCode");
        solrQuery.addField("skuCode");
        solrQuery.addField("productName");
        solrQuery.addField("marketPrice");
        solrQuery.addField("productPrice");
        solrQuery.addField("imgUrl");
        // solrQuery.addFilterQuery("stockNum:[1 TO * ]");
        EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
        QueryResponse resp = server.query(solrQuery);
        return resp.getResults();
    }

    private List<String> toPageItemCodes(List<String> itemCodes, Integer curPage, Integer pageSize) {
        if (CollectionUtils.isEmpty(itemCodes)) {
            return itemCodes;
        }
        int fromIndex = (curPage - 1) * pageSize;
        fromIndex = fromIndex < 0 ? 0 : fromIndex;
        if (itemCodes.size() < fromIndex) {
            return Collections.emptyList();
        }
        int toIndex = fromIndex + pageSize;
        toIndex = toIndex > itemCodes.size() ? itemCodes.size() : toIndex;
        return itemCodes.subList(fromIndex, toIndex);
    }

    private List<String> queryItemCodes(String keyWord) throws Exception {
        String facetName = "itemCode";
        SolrQuery solrQuery = new SolrQuery("{!frange l=0.4}query($qq)");
        StringBuilder sb = new StringBuilder("copyText:");
        sb.append(keyWord);
        solrQuery.setParam("qq", sb.toString());
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(1);
        solrQuery.addFacetField(facetName);
        solrQuery.setRows(0);
        EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
        QueryResponse respone = server.query(solrQuery);
        FacetField faField = respone.getFacetField(facetName);
        if (faField == null || faField.getValueCount() < 1) {
            return Collections.emptyList();
        }
        List<String> itemList = new ArrayList<String>(faField.getValueCount());
        for (Count valCount : faField.getValues()) {
            itemList.add(valCount.getName());
        }
        return itemList;
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