package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
		List<String> pCodeList = new ArrayList<String>();
		if (StringUtils.isNotEmpty(productCode)) {
			pCodeList.add(productCode);
		}
		Integer count = similarService.getCountSimilarDtoByCodeAndPrice(similarCodeList, pCodeList, fromPrice, toPrice);
		List<SimilarDto> dtoList = similarService.getSimilarDtoByCodeAndPrice(similarCodeList, pCodeList, fromPrice, toPrice, offset, pageSize);
		Collections.sort(dtoList, PRICE_ASC_COMPARATOR);
		PageReturnVo<List<SimilarDto>> pageReturnVo = new PageReturnVo<List<SimilarDto>>(curPage, pageSize);
		pageReturnVo.setTotalRow(count);
		pageReturnVo.setData(dtoList);
		return pageReturnVo;
		// return PageReturnVo.convert2PageReturn(dtoList, pageReturnVo);
	}

	@RequestMapping("")
	public String getSimilarSummery(@ModelAttribute("model") ModelMap model) {
		Long lastCode = 0L;
		List<Long> similarCodeList = similarService.getSimilarCodeByCodeAsc(lastCode, Integer.MAX_VALUE);
		model.addAttribute("codeList", new JSONArray(similarCodeList).toString());
		return "similars";
	}
}