package com.lezo.idober.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.lezo.idober.utils.UnionUtils;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;

@RequestMapping("sku")
@Controller
public class SkuController {
    private static final Integer SITE_ID_JD = 1001;
    private static final Integer SITE_ID_YHD = 1002;
    @Autowired
    private MatchService matchService;

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
            List<String> skuCodes = Lists.newArrayList(skuCode);
            List<MatchDto> mDtoList = matchService.getDtoBySkuCodes(skuCodes, 0);
            if (!mDtoList.isEmpty()) {
                targetUrl = mDtoList.get(0).getProductUrl();
            } else {
                targetUrl = "/sku/error.html";
            }
        }
        return new ModelAndView("redirect:" + targetUrl, model);
    }
}
