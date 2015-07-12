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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.idober.vo.ListItemVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;
import com.lezo.iscript.service.crawler.service.SkuRankService;

@Controller
@RequestMapping("new")
public class NewHomeController {
    @Autowired
    private SkuRankService skuRankService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getCategoryPage(@ModelAttribute("model") ModelMap model) {
        System.err.println("hello ,,,,,,,");
        Map<String, String> keyCategoryMap = new HashMap<String, String>();
        keyCategoryMap.put("手机数码", "手机");
        int limit = 12;
        int offset = 0;
        List<TagRectVo<ListItemVo>> tagRectVos = new ArrayList<TagRectVo<ListItemVo>>();
        for (Entry<String, String> entry : keyCategoryMap.entrySet()) {
            List<SkuRankDto> dtoList = skuRankService.getDtoByCategoryOrBarnd(entry.getValue(), null, offset, limit);
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
            tagRectVo = new TagRectVo<ListItemVo>();
            tagRectVo.setTagName(entry.getKey() + "002");
            tagRectVo.setDataList(voList);
            tagRectVos.add(tagRectVo);
        }
        model.addAttribute("tagRectList", tagRectVos);
        // return "hello";
        return "items";
    }
}
