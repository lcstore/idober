package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.idober.vo.ClusterItemVo;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.ProductStatService;

@RequestMapping("cluster")
public class ClusterController {
	@Autowired
	private MatchService matchService;
	@Autowired
	private ProductStatService productStatService;

	@RequestMapping(value = "{mCode}", method = RequestMethod.GET)
	public String getCluster(@ModelAttribute("model") ModelMap model, @PathVariable @RequestParam("mCode") Long mCode) {
		List<Long> mCodeList = new ArrayList<Long>();
		mCodeList.add(mCode);
        List<MatchDto> dtoList = null;
        // List<MatchDto> dtoList = matchService.getMatchDtoByMatchCodes(mCodeList);
		Map<String, MatchDto> key2MatchMap = new HashMap<String, MatchDto>();
		Map<Integer, Set<String>> site2CodesMap = new HashMap<Integer, Set<String>>();
		for (MatchDto dto : dtoList) {
			Set<String> codeSet = site2CodesMap.get(dto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				site2CodesMap.put(dto.getSiteId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
			String key = dto.getSiteId() + ":" + dto.getProductCode();
			key2MatchMap.put(key, dto);
		}
		Integer minStock = null;
		List<ClusterItemVo> itemVos = new ArrayList<ClusterItemVo>(dtoList.size());
		for (Entry<Integer, Set<String>> entry : site2CodesMap.entrySet()) {
			Integer siteId = entry.getKey();
			List<String> codeList = new ArrayList<String>(entry.getValue());
			List<ProductStatDto> statList = productStatService.getProductStatDtos(codeList, siteId, minStock);
			for (String code : codeList) {
				String key = siteId + ":" + code;
				MatchDto matchDto = key2MatchMap.get(key);
			}
		}

		for (MatchDto dto : dtoList) {
			ClusterItemVo itemVo = new ClusterItemVo();
			BeanUtils.copyProperties(dto, itemVo);
			itemVos.add(itemVo);
		}
		model.addAttribute("clusterList", itemVos);
		return "clusters";
	}
}
