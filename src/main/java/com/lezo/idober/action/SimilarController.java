package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.vo.PageReturnVo;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

import freemarker.template.utility.StringUtil;

@Controller
@RequestMapping("similar")
public class SimilarController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(SimilarController.class);
	private SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
	private static final Comparator<SimilarDto> PRICE_ASC_COMPARATOR = new Comparator<SimilarDto>() {
		@Override
		public int compare(SimilarDto o1, SimilarDto o2) {
			if (o1.getProductPrice() == null || o1.getProductPrice() < 0) {
				return -1;
			}
			if (o2.getProductPrice() == null || o1.getProductPrice() < 0) {
				return 1;
			}
			return o1.getProductPrice().compareTo(o2.getProductPrice());
		}

	};

	@RequestMapping("query")
	@ResponseBody
	public PageReturnVo<List<SimilarDto>> getSimilarList(@ModelAttribute("model") ModelMap model, @RequestParam("sCode") Long similarCode, @RequestParam("pCode") String productCode,
			@RequestParam("fromPrice") Float fromPrice, @RequestParam("toPrice") Float toPrice, @RequestParam(value = "page", defaultValue = "1") Integer curPage,
			@RequestParam(defaultValue = "36") Integer pageSize) {
		List<Long> similarCodeList = null;
		if (similarCode != null) {
			similarCodeList = new ArrayList<Long>(1);
			similarCodeList.add(similarCode);
		}
		Integer offset = (curPage - 1) * pageSize;
		offset = offset < 0 ? 0 : offset;
		offset = 0;
		List<String> pCodeList = new ArrayList<String>();
		if (StringUtils.isNotEmpty(productCode)) {
			pCodeList.add(productCode);
		}
		Integer count = similarService.getCountSimilarDtoByCodeAndPrice(similarCodeList, pCodeList, fromPrice, toPrice);
		List<SimilarDto> dtoList = similarService.getSimilarDtoByCodeAndPrice(similarCodeList, pCodeList, fromPrice, toPrice, offset, Integer.MAX_VALUE);
		// Collections.sort(dtoList, PRICE_ASC_COMPARATOR);
		Map<Integer, List<SimilarDto>> siteDtoMap = new HashMap<Integer, List<SimilarDto>>();
		for (SimilarDto dto : dtoList) {
			List<SimilarDto> siteList = siteDtoMap.get(dto.getSiteId());
			if (siteList == null) {
				siteList = new ArrayList<SimilarDto>();
				siteDtoMap.put(dto.getSiteId(), siteList);
			}
			siteList.add(dto);
		}
		List<Entry<Integer, List<SimilarDto>>> entryList = new ArrayList<Entry<Integer, List<SimilarDto>>>(siteDtoMap.entrySet());
		doSortSiteAvgPriceAsc(entryList);
		dtoList.clear();
		for (Entry<Integer, List<SimilarDto>> entry : entryList) {
			Collections.sort(entry.getValue(), PRICE_ASC_COMPARATOR);
			for (SimilarDto dto : entry.getValue()) {
				dtoList.add(dto);
			}
		}
		int fromIndex = (curPage - 1) * pageSize;
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		int toIndex = fromIndex + pageSize;
		toIndex = toIndex < dtoList.size() ? toIndex : dtoList.size();
		dtoList = dtoList.subList(fromIndex, toIndex);
		PageReturnVo<List<SimilarDto>> pageReturnVo = new PageReturnVo<List<SimilarDto>>(curPage, pageSize);
		pageReturnVo.setTotalRow(count);
		pageReturnVo.setData(dtoList);
		return pageReturnVo;
		// return PageReturnVo.convert2PageReturn(dtoList, pageReturnVo);
	}

	private void doSortSiteAvgPriceAsc(List<Entry<Integer, List<SimilarDto>>> entryList) {
		Collections.sort(entryList, new Comparator<Entry<Integer, List<SimilarDto>>>() {
			@Override
			public int compare(Entry<Integer, List<SimilarDto>> o1, Entry<Integer, List<SimilarDto>> o2) {
				Float priceSumLeft = 0F;
				int countLeft = 0;
				for (SimilarDto dto : o1.getValue()) {
					if (dto.getProductPrice() != null) {
						priceSumLeft += dto.getProductPrice();
						countLeft++;
					}
				}
				if (countLeft == 0) {
					return -1;
				}
				Float priceSumRight = 0F;
				int countRight = 0;
				for (SimilarDto dto : o2.getValue()) {
					if (dto.getProductPrice() != null) {
						priceSumRight += dto.getProductPrice();
						countRight++;
					}
				}
				if (countRight == 0) {
					return 1;
				}
				Float avgLeft = priceSumLeft / countLeft;
				Float avgRight = priceSumRight / countRight;
				return avgLeft.compareTo(avgRight);
			}
		});
	}

	@RequestMapping("")
	public String getSimilarSummery(@ModelAttribute("model") ModelMap model) {
		Long lastCode = 0L;
		List<Long> similarCodeList = similarService.getSimilarCodeByCodeAsc(lastCode, Integer.MAX_VALUE);
		model.addAttribute("codeList", new JSONArray(similarCodeList).toString());
		return "similars";
	}
}