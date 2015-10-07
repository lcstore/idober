package com.lezo.idober.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.rest.jos.JosRestClient;
import com.lezo.rest.yhd.YhdRestClient;

@Log4j
public class UnionUtils {
    private static String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
    private static String appSecret = "7b7d95759e594b2f89a553b350f3d131";
    private static String accessToken = "83de1487-026f-4a60-8dac-a9dd27abfeae";
    private static JosRestClient jdClient = new JosRestClient(appKey, appSecret, accessToken);

    private static String yhdAppKey = "10210015092300003589";
    private static String yhdAppSecret = "ebf4272cc568ac51f13aa52c852e97d2";
    private static String yhdAccessToken = "";
    private static YhdRestClient yhdClient = new YhdRestClient(yhdAppKey, yhdAppSecret, yhdAccessToken);
    private static Pattern jdCodeReg = Pattern.compile("item.jd.com/([0-9]+).html");
    private static Pattern yhdCodeReg = Pattern.compile("item.yhd.com/item/([0-9]+)");

    public static String getUnionByUrl(String url)
            throws Exception {
        // http://item.jd.com/1217500.html
        Matcher matcher = jdCodeReg.matcher(url);
        if (matcher.find()) {
            return getJdUnionByCode(matcher.group(1));
        }
        // http://item.yhd.com/item/66988?tp=8.0.1365.0.2.L0bedZ2-10-CT9Eh
        matcher = yhdCodeReg.matcher(url);
        if (matcher.find()) {
            return getYhdUnionByCode(matcher.group(1));
        }
        return null;
    }

    public static String getJdUnionByCode(String pCode) throws Exception {
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
        String result = jdClient.execute(method, argsObject.toString());
        JSONObject rsObj = JSONUtils.getJSONObject(result);
        rsObj = JSONUtils.getJSONObject(rsObj, "jingdong_service_promotion_batch_getcode_responce");
        Integer resCode = JSONUtils.getInteger(rsObj, "code");

        if (resCode != null && resCode.equals(0)) {
            rsObj = JSONUtils.getJSONObject(rsObj, "querybatch_result");
            if (rsObj != null) {
                JSONArray urlArray = JSONUtils.get(rsObj, "urlList");
                if (urlArray != null) {
                    for (int i = 0; i < urlArray.length(); i++) {
                        JSONObject urlObj = urlArray.getJSONObject(i);
                        String unionUrl = JSONUtils.getString(urlObj, "url");
                        if (StringUtils.isNotBlank(unionUrl)) {
                            return unionUrl;
                        }
                    }
                }
            }
        } else {
            log.warn("error union,code:" + pCode + ",respone:" + rsObj);
        }
        return null;
    }

    public static String getYhdUnionByCode(String pCode) throws Exception {
        String trackerU = "103663742";
        String serverUrl = "http://openapi.yhd.com/app/api/rest/router";
        String method = "yhd.union.single.product.get";
        Map<String, Object> paramMap = yhdClient.createSystemParaMap();
        paramMap.put("method", method);
        paramMap.put("trackerU", trackerU);
        paramMap.put("pmInfoId", pCode);
        paramMap.put("app_secret", appSecret);
        String result = yhdClient.execute(serverUrl, paramMap);

        JSONObject rsObj = JSONUtils.getJSONObject(result);
        rsObj = JSONUtils.getJSONObject(rsObj, "union_single_product_get_response");
        rsObj = JSONUtils.getJSONObject(rsObj, "single_product_info_outer_list");

        if (rsObj != null) {
            JSONArray urlArray = JSONUtils.get(rsObj, "single_product_info_outer");
            if (urlArray != null) {
                for (int i = 0; i < urlArray.length(); i++) {
                    JSONObject urlObj = urlArray.getJSONObject(i);
                    String unionUrl = JSONUtils.getString(urlObj, "product_url");
                    if (StringUtils.isNotBlank(unionUrl)) {
                        return unionUrl;
                    }
                }
            }
        }
        return null;
    }
}
