package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.vo.PageReturnVo;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

//@Controller
//@RequestMapping("similar")
public class SimilarController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(SimilarController.class);
	private SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
	private static final Comparator<SimilarDto> PRICE_ASC_COMPARATOR = new Comparator<SimilarDto>() {
		@Override
		public int compare(SimilarDto o1, SimilarDto o2) {
			if (o1.getMarketPrice() == null || o1.getMarketPrice() < 0) {
				return -1;
			}
			if (o2.getMarketPrice() == null || o1.getMarketPrice() < 0) {
				return 1;
			}
			return o1.getMarketPrice().compareTo(o2.getMarketPrice());
		}

	};

	@RequestMapping("query")
	@ResponseBody
	public PageReturnVo<List<SimilarDto>> getSimilarList(@ModelAttribute("model") ModelMap model,
			@RequestParam("sCode") Long similarCode, @RequestParam("pCode") String productCode,
			@RequestParam("fromPrice") Float fromPrice, @RequestParam("toPrice") Float toPrice,
			@RequestParam(value = "page", defaultValue = "1") Integer curPage,
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
		return null;
		// return PageReturnVo.convert2PageReturn(dtoList, pageReturnVo);
	}

	private void doSortSiteAvgPriceAsc(List<Entry<Integer, List<SimilarDto>>> entryList) {
		Collections.sort(entryList, new Comparator<Entry<Integer, List<SimilarDto>>>() {
			@Override
			public int compare(Entry<Integer, List<SimilarDto>> o1, Entry<Integer, List<SimilarDto>> o2) {
				Float priceSumLeft = 0F;
				int countLeft = 0;
				for (SimilarDto dto : o1.getValue()) {
					if (dto.getMarketPrice() != null) {
						priceSumLeft += dto.getMarketPrice();
						countLeft++;
					}
				}
				if (countLeft == 0) {
					return -1;
				}
				Float priceSumRight = 0F;
				int countRight = 0;
				for (SimilarDto dto : o2.getValue()) {
					if (dto.getMarketPrice() != null) {
						priceSumRight += dto.getMarketPrice();
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

}