package com.lezo.idober.home.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	/**
	 * Static list of ProductVos to simulate Database
	 */
	private static List<ProductVo> statList = new ArrayList<ProductVo>();

	static {
		ProductVo statDto = new ProductVo();
		statDto.setProductName("Toblerone瑞士三角 黑巧克力含蜂蜜及奶油杏仁 50g 瑞士进口");
		statDto.setProductUrl("http://item.yhd.com/item/1845898");
		statDto.setProductPrice(9.8F);
		statDto.setImgUrl("http://d9.yihaodianimg.com/t20/2012/1022/286/0/330463e4d1bdb65ec77fecedee40a191_60x60.jpg");
		statList.add(statDto);
		statList.add(statDto);
		statList.add(statDto);
		statList.add(statDto);
	}

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
		int len = pCodeList.size() <= 16 ? pCodeList.size() : 16;
		List<String> codeList = pCodeList;//.subList(0, len);
		ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);
		ProductService productService = SpringBeanUtils.getBean(ProductService.class);
		
		List<ProductStatDto> statList = productStatService.getProductStatDtos(codeList, siteId);
		List<ProductVo> voList = new ArrayList<ProductVo>();
		Map<String, ProductStatDto> statMap = new HashMap<String, ProductStatDto>();
		Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
		for (ProductStatDto statDto : statList) {
			String key = statDto.getShopId() + statDto.getProductCode();
			statMap.put(key, statDto);

			Set<String> codeSet = siteCodeMap.get(statDto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(statDto.getShopId(), codeSet);
			}
			codeSet.add(statDto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : siteCodeMap.entrySet()) {
			List<ProductDto> productList = productService.getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			for (ProductDto pDto : productList) {
				String key = pDto.getShopId() + pDto.getProductCode();
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
		model.addAttribute("statList", voList);
		return "index";
	}
}