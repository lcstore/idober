package com.lezo.idober.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.SkuVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.service.MatchService;

@RequestMapping("item")
@Controller
public class ItemController {
    @Autowired
    private MatchService matchService;
    private static String SKU_SEARCH_FIELDS;

    static {
        StringBuilder sb = new StringBuilder();
        for (Field fld : SkuVo.class.getDeclaredFields()) {
            org.apache.solr.client.solrj.beans.Field annot =
                    fld.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
            if (annot == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",");
            }
            if (annot.value().equals(org.apache.solr.client.solrj.beans.Field.DEFAULT)) {
                sb.append(fld.getName());
            } else {
                sb.append(annot.value());
            }
        }
        SKU_SEARCH_FIELDS = sb.toString();
    }

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public String getItem(@PathVariable String itemCode, @ModelAttribute("model") ModelMap model) throws Exception {
        int offset = 0;
        int limit = 12;
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "matchCode:" + itemCode);
        solrQuery.addField(SKU_SEARCH_FIELDS);
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.addSort("commentNum", ORDER.desc);
        solrQuery.addSort("score", ORDER.desc);
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        QueryResponse respone = SolrUtils.getSolrServer().query(solrQuery);
        Map<String, List<SkuVo>> cateMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(respone.getResults())) {
            List<SkuVo> skuVos = respone.getBeans(SkuVo.class);
            for (SkuVo mDto : skuVos) {
                if (StringUtils.isBlank(mDto.getTokenCategory())) {
                    mDto.setTokenCategory(mDto.getCategoryNav().split(";")[0]);
                }
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
}
