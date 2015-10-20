package com.lezo.idober.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.solr.EmbeddedSolrServerHolder;
import com.lezo.idober.vo.SkuVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.service.MatchService;

@RequestMapping("item")
@Controller
public class ItemController {
    @Autowired
    private MatchService matchService;

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public String getItem(@PathVariable String itemCode, @ModelAttribute("model") ModelMap model) throws Exception {
        int offset = 0;
        int limit = 12;

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "matchCode:" + itemCode);
        // solrQuery.addFacetField("skuCode", "productName", "productUrl", "imgUrl", "tokenBrand", "tokenCategory");
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
        QueryResponse respone = server.query(solrQuery);
        Map<String, List<SkuVo>> cateMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(respone.getResults())) {
            for (SolrDocument doc : respone.getResults()) {
                SkuVo mDto = new SkuVo();
                copyTo(doc, mDto);
                List<SkuVo> dtoList = cateMap.get(mDto.getTokenCategory());
                if (dtoList == null) {
                    dtoList = Lists.newArrayList();
                    cateMap.put(mDto.getTokenCategory(), dtoList);
                }
                dtoList.add(mDto);
            }
        }
        List<TagRectVo<SkuVo>> tagRectVos = new ArrayList<TagRectVo<SkuVo>>();
        for (Entry<String, List<SkuVo>> entry : cateMap.entrySet()) {
            TagRectVo<SkuVo> tagRectVo = new TagRectVo<SkuVo>();
            tagRectVo.setTagName(entry.getKey());
            tagRectVo.setDataList(entry.getValue());
            tagRectVos.add(tagRectVo);
        }
        model.addAttribute("tagRectList", tagRectVos);
        return "items";
    }

    private void copyTo(SolrDocument doc, SkuVo mDto) throws Exception {
        for (Field fld : mDto.getClass().getDeclaredFields()) {
            if (!fld.isAccessible()) {
                fld.setAccessible(true);
            }
            Object value = doc.get(fld.getName());
            fld.set(mDto, value);
        }
    }
}
