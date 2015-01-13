package com.lezo.idober.action;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("data")
public class ChomeController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ChomeController.class);

	/**
	 * Saves the static list of ProductVos in model and renders it via freemarker template.
	 * 
	 * @param model
	 * @return The index view (FTL)
	 */
	@RequestMapping(value = "send", method = RequestMethod.POST)
	@ResponseBody
	public String index(@ModelAttribute("model") ModelMap model, @Param("data") String data) {
		logger.info(data);
		return "OK";
	}
}