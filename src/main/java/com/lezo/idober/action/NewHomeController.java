package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.idober.vo.ClusterItemVo;
import com.lezo.idober.vo.ListItemVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.SkuRankService;

@Controller
// @RequestMapping("new")
public class NewHomeController {
     @Autowired
     private SkuRankService skuRankService;
     @Autowired
     private MatchService matchService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
     public String getCategoryPage(@ModelAttribute("model") ModelMap model) {
         Map<String, String> keyCategoryMap = new HashMap<String, String>();
         keyCategoryMap.put("手机数码", "手机");
         int limit = 12;
         int offset = 0;
         List<TagRectVo<ListItemVo>> tagRectVos = new ArrayList<TagRectVo<ListItemVo>>();
         for (Entry<String, String> entry : keyCategoryMap.entrySet()) {
            // List<SkuRankDto> dtoList = skuRankService.getDtoByCategoryOrBarnd(entry.getValue(), null, offset, limit);
            List<SkuRankDto> dtoList = getDefaultSkuRank();
             List<ListItemVo> voList = new ArrayList<ListItemVo>();
             for (SkuRankDto dto : dtoList) {
                 ListItemVo itemVo = new ListItemVo();
                 BeanUtils.copyProperties(dto, itemVo);
                 voList.add(itemVo);
             }
             TagRectVo<ListItemVo> tagRectVo = new TagRectVo<ListItemVo>();
             tagRectVo.setTagName(entry.getKey());
             tagRectVo.setDataList(voList);
             tagRectVos.add(tagRectVo);
         }
         model.addAttribute("tagRectList", tagRectVos);
         // return "hello";
         return "items";
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
         List<MatchDto> dtoList = matchService.getMatchDtoByMatchCodes(mCodeList);
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
