package com.lezo.idober.action;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("shopmgr")
public class BrandShopController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(BrandShopController.class);

	@RequestMapping("query")
	public String listShops(@ModelAttribute("model") ModelMap model, Integer curPage, Integer pageSize, String brandName) {
		Long lastCode = 0L;
		return "similars";
	}
}