package com.lezo.idober.action;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.idober.vo.ProductVo;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.rest.jos.JosRestClient;

@Controller
@RequestMapping("union")
@Log4j()
public class UnionController {
    private static String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
    private static String appSecret = "7b7d95759e594b2f89a553b350f3d131";
    private static String accessToken = "83de1487-026f-4a60-8dac-a9dd27abfeae";
    private static JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);

    @RequestMapping(value = "jd", method = RequestMethod.GET)
    public String getUnionUrl(@RequestParam("code") String pCode,
            @RequestParam(value = "qWord", required = false) String qWord,
            @ModelAttribute("model") ModelMap model)
            throws Exception {
        String method = "jingdong.service.promotion.getcode";
        method = "jingdong.service.promotion.batch.getcode";
        JSONObject argsObject = new JSONObject();
        argsObject.put("id", pCode);
        argsObject.put("url", "http://item.jd.com/" + pCode + ".html");
        argsObject.put("unionId", "51698052");
        argsObject.put("channel", "PC");
        argsObject.put("subUnionId", "");
        argsObject.put("webId", "220524281");
        argsObject.put("ext1", "");
        String result = client.execute(method, argsObject.toString());
        JSONObject rsObj = JSONUtils.getJSONObject(result);
        rsObj = JSONUtils.getJSONObject(rsObj, "jingdong_service_promotion_batch_getcode_responce");
        Integer resCode = JSONUtils.getInteger(rsObj, "code");

        ProductVo pVo = new ProductVo();
        pVo.setProductCode(pCode);
        pVo.setProductUrl(JSONUtils.getString(argsObject, "url"));
        if (resCode != null && resCode.equals(0)) {
            rsObj = JSONUtils.getJSONObject(rsObj, "querybatch_result");
            if (rsObj != null) {
                JSONArray urlArray = JSONUtils.get(rsObj, "urlList");
                if (urlArray != null) {
                    for (int i = 0; i < urlArray.length(); i++) {
                        JSONObject urlObj = urlArray.getJSONObject(i);
                        String unionUrl = JSONUtils.getString(urlObj, "url");
                        if (StringUtils.isNotBlank(unionUrl)) {
                            pVo.setUnionUrl(unionUrl);
                        }
                    }
                }
            }
        } else {
            log.warn("error union,code:" + pCode + ",respone:" + rsObj);
        }
        model.put("union", pVo);
        if (StringUtils.isNotBlank(qWord)) {
            model.put("qWord", qWord);
        }
        return "unions";
    }
}
