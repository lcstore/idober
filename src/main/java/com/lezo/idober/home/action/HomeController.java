package com.lezo.idober.home.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.service.PromotionMapService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
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
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(@ModelAttribute("model") ModelMap model) {
		PromotionMapService promotionMapService = SpringBeanUtils.getBean(PromotionMapService.class);
		List<Integer> siteList = new ArrayList<Integer>();
		siteList.add(1001);
//		siteList.add(1002);
		Integer promoteType = null;
		Integer promoteStatus = PromotionMapDto.PROMOTE_STATUS_START;
		Integer isDelete = PromotionMapDto.DELETE_FALSE;
		List<ProductStatDto> summaryStatList = new ArrayList<ProductStatDto>();
		for (Integer siteId : siteList) {
			List<String> pCodeList = promotionMapService.getProductCodeSetBySiteIdAndType(siteId, promoteType, promoteStatus, isDelete);
			List<ProductStatDto> statList = productStatService.getProductStatDtos(pCodeList, siteId, 1);
			if (!statList.isEmpty()) {
				summaryStatList.addAll(statList);
			}
		}
		doCommentDesc(summaryStatList);
		int hotCount = 3;
		addHotList(summaryStatList, hotCount, model);
		int maxCount = hotCount + 100;
		maxCount = maxCount > summaryStatList.size() ? summaryStatList.size() : maxCount;
		summaryStatList = summaryStatList.subList(hotCount, maxCount);
		addHomeList(summaryStatList, hotCount, model);
		return "index";
	}

	private Map<Integer, Set<String>> getSiteCodeMap(List<ProductStatDto> statList) {
		if (CollectionUtils.isEmpty(statList)) {
			return Collections.emptyMap();
		}
		Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
		for (ProductStatDto statDto : statList) {
			Set<String> codeSet = siteCodeMap.get(statDto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(statDto.getSiteId(), codeSet);
			}
			codeSet.add(statDto.getProductCode());
		}
		return siteCodeMap;
	}

	private void addHomeList(List<ProductStatDto> statList, int hotCount, ModelMap model) {
		if (CollectionUtils.isEmpty(statList) || statList.size() <= hotCount) {
			return;
		}
		statList = statList.subList(hotCount, statList.size());
//		doPriceAsc(statList);
		int pageSize = 36;
		int toIndex = statList.size();
		toIndex = toIndex < pageSize ? toIndex : pageSize;
		List<ProductStatDto> pageList = statList.subList(0, toIndex);
		List<ProductVo> voList = convert2ProductVos(pageList);
		model.addAttribute("pageList", voList);
	}

	private void addHotList(List<ProductStatDto> statList, int hotCount, ModelMap model) {
		if (CollectionUtils.isEmpty(statList)) {
			model.addAttribute("indexHotList", Collections.emptyList());
			return;
		}
		int toIndex = statList.size();
		toIndex = toIndex < hotCount ? toIndex : hotCount;
		List<ProductStatDto> hotList = statList.subList(0, toIndex);
		List<ProductVo> voList = convert2ProductVos(hotList);
		model.addAttribute("indexHotList", voList);
	}

	public List<ProductVo> convert2ProductVos(List<ProductStatDto> statList) {
		if (CollectionUtils.isEmpty(statList)) {
			return Collections.emptyList();
		}
		Map<Integer, Set<String>> siteCodeMap = getSiteCodeMap(statList);
		Map<String, ProductStatDto> statMap = getKeyMap(statList);
		List<ProductVo> voList = new ArrayList<ProductVo>(statList.size());
		for (Entry<Integer, Set<String>> entry : siteCodeMap.entrySet()) {
			List<ProductDto> productList = productService.getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			ShopDto siteDto = ShopCacher.getInstance().getShopDto(entry.getKey());
			String siteName = siteDto == null ? "" : siteDto.getShopName();
			for (ProductDto pDto : productList) {
				String key = pDto.getSiteId() + "-" + pDto.getProductCode();
				ProductVo pVo = new ProductVo();
				ProductStatDto statDto = statMap.get(key);
				pVo.setImgUrl(pDto.getImgUrl());
				pVo.setSiteName(siteName);
				if (statDto != null) {
					pVo.setMarketPrice(statDto.getMarketPrice());
					pVo.setProductUrl(statDto.getProductUrl());
					pVo.setProductCode(statDto.getProductCode());
					pVo.setProductName(statDto.getProductName());
					pVo.setSiteId(statDto.getSiteId());
					pVo.setProductPrice(statDto.getProductPrice());
				}
				voList.add(pVo);
			}
		}
		return voList;
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

	private void doPriceAsc(List<ProductStatDto> statList) {
		Collections.sort(statList, new Comparator<ProductStatDto>() {
			@Override
			public int compare(ProductStatDto statLeft, ProductStatDto statRight) {
				return statLeft.getProductPrice().compareTo(statRight.getProductPrice());
			}
		});
	}

	private void doCommentDesc(List<ProductStatDto> statList) {
		Collections.sort(statList, new Comparator<ProductStatDto>() {
			@Override
			public int compare(ProductStatDto statLeft, ProductStatDto statRight) {
				Integer leftNum = statLeft.getSoldNum() == null ? statLeft.getCommentNum() : statLeft.getSoldNum();
				Integer rightNum = statRight.getSoldNum() == null ? statRight.getCommentNum() : statRight.getSoldNum();
				if (leftNum == null) {
					return -1;
				}
				if (rightNum == null) {
					return 1;
				}
				return rightNum.compareTo(leftNum);
			}
		});
	}
}