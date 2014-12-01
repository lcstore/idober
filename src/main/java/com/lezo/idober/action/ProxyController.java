package com.lezo.idober.action;

import java.util.Collections;
import java.util.Comparator;
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
	private static final Comparator<ProxyHomeDto> DEL_ASC_COMPARATOR = new Comparator<ProxyHomeDto>() {
		@Override
		public int compare(ProxyHomeDto o1, ProxyHomeDto o2) {
			return o1.getIsDelete().compareTo(o2.getIsDelete());
		}
	};

	@RequestMapping("home")
	public String buildSearch(@ModelAttribute("model") ModelMap model, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize) {
		List<ProxyHomeDto> proxyList = proxyHomeService.getProxyHomeDtoByStatus(null, null);
		Collections.sort(proxyList, DEL_ASC_COMPARATOR);
		model.addAttribute("proxyList", proxyList);
		return "proxyList";
	}

}