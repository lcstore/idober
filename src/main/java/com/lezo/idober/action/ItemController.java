package com.lezo.idober.action;

import java.util.ArrayList;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.idober.vo.ListItemVo;
import com.lezo.idober.vo.TagRectVo;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;

@RequestMapping("item")
@Controller
public class ItemController {
    @Autowired
    private MatchService matchService;

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public String getItem(@PathVariable String itemCode, @ModelAttribute("model") ModelMap model) {
        List<String> mCodes = Lists.newArrayList(itemCode);
        int offset = 0;
        int limit = 12;
        List<MatchDto> matchDtos = matchService.getDtoByMatchCodesWithLimit(mCodes, offset, limit);
        Map<String, List<MatchDto>> cateMap = Maps.newHashMap();
        for (MatchDto mDto : matchDtos) {
            List<MatchDto> dtoList = cateMap.get(mDto.getTokenCategory());
            if (dtoList == null) {
                dtoList = Lists.newArrayList();
                cateMap.put(mDto.getTokenCategory(), dtoList);
            }
            dtoList.add(mDto);
        }
        List<TagRectVo<ListItemVo>> tagRectVos = new ArrayList<TagRectVo<ListItemVo>>();
        for (Entry<String, List<MatchDto>> entry : cateMap.entrySet()) {
            List<ListItemVo> voList = new ArrayList<ListItemVo>();
            for (MatchDto dto : entry.getValue()) {
                ListItemVo itemVo = new ListItemVo();
                BeanUtils.copyProperties(dto, itemVo);
                itemVo.setId(dto.getSkuCode());
                voList.add(itemVo);
            }
            TagRectVo<ListItemVo> tagRectVo = new TagRectVo<ListItemVo>();
            tagRectVo.setTagName(entry.getKey());
            tagRectVo.setDataList(voList);
            tagRectVos.add(tagRectVo);
        }
        model.addAttribute("tagRectList", tagRectVos);
        return "items";
    }
}
