package com.lezo.idober.home.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
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
		ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);
		Long fromId = 0L;
		Integer shopId = 1002;
		int limit = 4;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date updateTime = calendar.getTime();
		List<ProductStatDto> statList = productStatService.getProductStatDtosLowestPrice(fromId, shopId, updateTime,
				limit);
		List<ProductVo> voList = new ArrayList<ProductVo>();
		for (ProductStatDto statDto : statList) {
			ProductVo pVo = new ProductVo();
			pVo.setImgUrl("http://d9.yihaodianimg.com/t20/2012/1022/286/0/330463e4d1bdb65ec77fecedee40a191_60x60.jpg");
			pVo.setMarketPrice(statDto.getMarketPrice());
			pVo.setProductUrl(statDto.getProductUrl());
			pVo.setProductCode(statDto.getProductCode());
			pVo.setProductName(statDto.getProductName());
			pVo.setShopId(statDto.getShopId());
			voList.add(pVo);
		}
		model.addAttribute("statList", voList);

		return "index";
	}

}