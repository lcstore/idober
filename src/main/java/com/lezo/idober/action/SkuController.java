package com.lezo.idober.action;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.idober.utils.UnionUtils;

//@RequestMapping("sku")
//@Controller
public class SkuController {
	private static final Integer SITE_ID_JD = 1001;
	private static final Integer SITE_ID_YHD = 1002;

	@RequestMapping(value = "{skuCode}", method = RequestMethod.GET)
	public ModelAndView getSku(@PathVariable String skuCode, @ModelAttribute("model") ModelMap model) throws Exception {
		String[] sCodeArr = skuCode.split("_");
		String targetUrl = null;
		if (SITE_ID_JD.toString().equals(sCodeArr[0])) {
			targetUrl = UnionUtils.getJdUnionByCode(sCodeArr[1]);
		} else if (SITE_ID_YHD.toString().equals(sCodeArr[0])) {
			targetUrl = UnionUtils.getYhdUnionByCode(sCodeArr[1]);
		}
		if (targetUrl == null) {
			model.addAttribute("errormsg", "未知SKU:" + skuCode);
			targetUrl = "error";
		}
		return new ModelAndView("redirect:" + targetUrl, model);
	}
}
