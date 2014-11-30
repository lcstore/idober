package com.lezo.idober.action;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;
import com.lezo.iscript.service.crawler.service.ProxyHomeService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Controller
@RequestMapping("proxy")
public class ProxyController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ProxyController.class);
	private ProxyHomeService proxyHomeService = SpringBeanUtils.getBean(ProxyHomeService.class);

	@RequestMapping("list")
	public String buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize) {
		List<ProxyHomeDto> proxyList = proxyHomeService.getProxyHomeDtoByStatus(null);
		model.addAttribute("proxyList", proxyList);
		return "proxyList";
	}

}