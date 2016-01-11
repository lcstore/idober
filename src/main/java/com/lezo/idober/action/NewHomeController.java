package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.idober.solr.pojo.ItemSolr;
import com.lezo.idober.utils.SolrConstant;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ClusterItemVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;

@Controller
// @RequestMapping("new")
public class NewHomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getCategoryPage(@ModelAttribute("model") ModelMap model) throws Exception {
        Map<String, String> keyCategoryMap = new HashMap<String, String>();
        keyCategoryMap.put("手机数码", "手机");
        keyCategoryMap.put("特色食品", "食品");
        int limit = 12;
        int offset = 0;
        List<TagRectVo<ItemSolr>> tagRectVos = new ArrayList<TagRectVo<ItemSolr>>();
        for (Entry<String, String> entry : keyCategoryMap.entrySet()) {
            // List<SkuRankDto> dtoList = skuRankService.getDtoByCategoryOrBarnd(entry.getValue(), null, offset, limit);
            // List<ItemDto> itemList = itemService.getDtoByCategory(entry.getValue(), offset, limit);
            // List<ListItemVo> voList = new ArrayList<ListItemVo>();
            // for (ItemDto dto : itemList) {
            // ListItemVo itemVo = new ListItemVo();
            // BeanUtils.copyProperties(dto, itemVo);
            // itemVo.setId(dto.getMatchCode());
            // voList.add(itemVo);
            // }
            List<ItemSolr> itemList = queryDocByCategory(entry.getValue(), offset, limit);
            TagRectVo<ItemSolr> tagRectVo = new TagRectVo<ItemSolr>();
            tagRectVo.setTagName(entry.getKey());
            tagRectVo.setDataList(itemList);
            tagRectVos.add(tagRectVo);
        }
        model.addAttribute("tagRectList", tagRectVos);
        // return "hello";
        return "home";
    }

    private List<ItemSolr> queryDocByCategory(String keyWord, int offset, int limit) throws Exception {
        if (StringUtils.isBlank(keyWord)) {
            return Collections.emptyList();
        }
        SolrQuery solrQuery = new SolrQuery(SolrConstant.SORL_QUERY_DEFAULT_FRANGE);
        String queryString =
                "group=true&group.field=itemCode&group.query=stockNum:[1%20TO%20*]&group.main=true";
        SolrParams params = SolrRequestParsers.parseQueryString(queryString);
        solrQuery.set("qq", "categoryNav:" + keyWord);
        solrQuery.add(params);
        solrQuery.setStart(offset);
        solrQuery.setRows(limit);
        solrQuery.addField(ItemSolr.getSolrFields());
        QueryResponse resp = SolrUtils.getSkuServer().query(solrQuery);
        return resp.getBeans(ItemSolr.class);
    }

    private List<SkuRankDto> getDefaultSkuRank() {
        List<SkuRankDto> dtoList = new ArrayList<SkuRankDto>();
        SkuRankDto dto = new SkuRankDto();
        dto.setProductCode("1217499");
        dto.setProductName("Apple iPhone 6 (A1586) 16GB 金色 移动");
        dto.setImgUrl("http://img14.360buyimg.com/n1/jfs/t277/193/1005339798/768456/29136988/542d0798N19d42ce3.jpg");
        dto.setProductUrl("http://item.jd.com/1217499.html");
        dto.setMatchCode(System.currentTimeMillis());
        dtoList.add(dto);
        dto = new SkuRankDto();
        dto.setProductCode("1514794");
        dto.setProductName("小米 4 2GB内存版 白色 移动4G手机");
        dto.setImgUrl("http://img14.360buyimg.com/n1/jfs/t1180/236/925849879/82803/784f564d/555aebfbN2625109b.jpg");
        dto.setProductUrl("http://item.jd.com/1514794.html");
        dto.setMatchCode(System.currentTimeMillis());
        dtoList.add(dto);
        return dtoList;
    }

    @RequestMapping(value = "cluster/{matchCode}", method = RequestMethod.GET)
    public String getCluster(@ModelAttribute("model") ModelMap model, @PathVariable Long matchCode) {
        List<Long> mCodeList = new ArrayList<Long>();
        mCodeList.add(matchCode);
        List<MatchDto> dtoList = null;
        // List<MatchDto> dtoList = matchService.getMatchDtoByMatchCodes(mCodeList);
        List<ClusterItemVo> itemVos = new ArrayList<ClusterItemVo>(dtoList.size());
        for (MatchDto dto : dtoList) {
            ClusterItemVo itemVo = new ClusterItemVo();
            BeanUtils.copyProperties(dto, itemVo);
            itemVos.add(itemVo);
        }
        model.addAttribute("clusterList", itemVos);
        return "clusters";
    }
}
