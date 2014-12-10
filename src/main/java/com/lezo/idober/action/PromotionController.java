package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.iscript.service.crawler.dto.PromotionTrackDto;
import com.lezo.iscript.service.crawler.service.PromotionTrackService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Controller
@RequestMapping("track")
public class PromotionController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(PromotionController.class);
	private PromotionTrackService promotionTrackService = SpringBeanUtils.getBean(PromotionTrackService.class);

	@RequestMapping("list")
	public String listPromotionTrackers(@ModelAttribute("model") ModelMap model, Date fromDate, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize) {
		List<Integer> siteList = new ArrayList<Integer>();
		siteList.add(1001);
		Date sellDate = new Date();
		List<PromotionTrackDto> trackList = promotionTrackService.getPromotionTrackDtoByDate(sellDate, siteList);
		model.addAttribute("trackList", trackList);
		return "promotionTrackList";
	}
}