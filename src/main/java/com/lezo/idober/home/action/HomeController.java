package com.lezo.idober.home.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.PromotionMapDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.service.PromotionMapService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Controller
public class HomeController {
	private ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);
	private ProductService productService = SpringBeanUtils.getBean(ProductService.class);

	/**
	 * Saves the static list of ProductVos in model and renders it via
	 * freemarker template.
	 * 
	 * @param model
	 * @return The index view (FTL)
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@ModelAttribute("model") ModelMap model) {
		PromotionMapService promotionMapService = SpringBeanUtils.getBean(PromotionMapService.class);
		Integer siteId = 1001;
		Integer promoteType = null;
		Integer promoteStatus = PromotionMapDto.PROMOTE_STATUS_START;
		Integer isDelete = PromotionMapDto.DELETE_FALSE;
		List<String> pCodeList = promotionMapService.getProductCodeSetBySiteIdAndType(siteId, promoteType, promoteStatus, isDelete);
		List<ProductStatDto> statList = productStatService.getProductStatDtos(pCodeList, siteId);
		Map<String, ProductStatDto> statMap = getKeyMap(statList);
		List<Entry<String, ProductStatDto>> statEntryList = new ArrayList<Entry<String, ProductStatDto>>(statMap.entrySet());
		doCommentDesc(statEntryList);
		int hotCount = 4;
		addHotList(statEntryList, hotCount, statMap, model);
		addHomeList(statEntryList, hotCount, model);
		return "index";
	}

	private Map<Integer, Set<String>> getSiteCodeMap(List<ProductStatDto> statList) {
		if (CollectionUtils.isEmpty(statList)) {
			return Collections.emptyMap();
		}
		Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
		for (ProductStatDto statDto : statList) {
			Set<String> codeSet = siteCodeMap.get(statDto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(statDto.getShopId(), codeSet);
			}
			codeSet.add(statDto.getProductCode());
		}
		return siteCodeMap;
	}

	private void addHomeList(List<Entry<String, ProductStatDto>> statEntryList, int hotCount, ModelMap model) {
		if (CollectionUtils.isEmpty(statEntryList)) {
			return;
		}

	}

	private void addHotList(List<Entry<String, ProductStatDto>> statEntryList, int hotCount, Map<String, ProductStatDto> statMap, ModelMap model) {
		if (CollectionUtils.isEmpty(statEntryList)) {
			model.addAttribute("indexHotList", Collections.emptyList());
			return;
		}
		int len = statEntryList.size();
		len = len < hotCount ? len : hotCount;
		List<ProductStatDto> hotList = new ArrayList<ProductStatDto>(len);
		for (int i = 0; i < len; i++) {
			hotList.add(statEntryList.get(i).getValue());
		}
		Map<Integer, Set<String>> siteCodeMap = getSiteCodeMap(hotList);
		List<ProductVo> voList = new ArrayList<ProductVo>();
		for (Entry<Integer, Set<String>> entry : siteCodeMap.entrySet()) {
			List<ProductDto> productList = productService.getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			for (ProductDto pDto : productList) {
				String key = pDto.getSiteId() + "-" + pDto.getProductCode();
				ProductVo pVo = new ProductVo();
				ProductStatDto statDto = statMap.get(key);
				pVo.setImgUrl(pDto.getImgUrl());
				if (statDto != null) {
					pVo.setMarketPrice(statDto.getMarketPrice());
					pVo.setProductUrl(statDto.getProductUrl());
					pVo.setProductCode(statDto.getProductCode());
					pVo.setProductName(statDto.getProductName());
					pVo.setShopId(statDto.getShopId());
					pVo.setProductPrice(statDto.getProductPrice());
				}
				voList.add(pVo);
			}
		}
		model.addAttribute("indexHotList", voList);
	}

	private Map<String, ProductStatDto> getKeyMap(List<ProductStatDto> hotList) {
		if (CollectionUtils.isEmpty(hotList)) {
			return Collections.emptyMap();
		}
		Map<String, ProductStatDto> keyMap = new HashMap<String, ProductStatDto>(hotList.size());
		for (ProductStatDto dto : hotList) {
			String key = getDtoKey(dto);
			keyMap.put(key, dto);
		}
		return keyMap;
	}

	private String getDtoKey(ProductStatDto dto) {
		return dto.getSiteId() + "-" + dto.getProductCode();
	}

	private void doPriceAsc(List<Entry<String, ProductStatDto>> statEntryList) {
		Collections.sort(statEntryList, new Comparator<Entry<String, ProductStatDto>>() {
			@Override
			public int compare(Entry<String, ProductStatDto> o1, Entry<String, ProductStatDto> o2) {
				ProductStatDto statLeft = o1.getValue();
				ProductStatDto statRight = o2.getValue();
				return statLeft.getProductPrice().compareTo(statRight.getProductPrice());
			}
		});
	}

	private void doCommentDesc(List<Entry<String, ProductStatDto>> statEntryList) {
		Collections.sort(statEntryList, new Comparator<Entry<String, ProductStatDto>>() {
			@Override
			public int compare(Entry<String, ProductStatDto> o1, Entry<String, ProductStatDto> o2) {
				ProductStatDto statLeft = o1.getValue();
				ProductStatDto statRight = o2.getValue();
				if (statLeft.getCommentNum() == null) {
					return -1;
				}
				if (statRight.getCommentNum() == null) {
					return 1;
				}
				return statRight.getCommentNum().compareTo(statRight.getCommentNum());
			}
		});
	}
}