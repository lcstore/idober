package com.lezo.idober.action;

import lombok.extern.log4j.Log4j;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ActionReturnVo;

@RequestMapping("solr")
@Controller
@Log4j
public class SolrController {

    @ResponseBody
    @RequestMapping(value = "{coreName}/update", method = RequestMethod.POST)
    public Object doRequest(@PathVariable String coreName, @RequestBody(required = true) String body) throws Exception {
        ActionReturnVo returnVo = new ActionReturnVo();
        try {
            // coreName = "core0";
            if (coreName == null) {
                returnVo.setCode(ActionReturnVo.CODE_PARAM);
                returnVo.setMsg("非法参数");
                return returnVo;
            }
            SolrServer server = SolrUtils.getSolrServer(coreName);
            String url = "/update";
            // 没搞定
            final ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/json/docs");
            final ContentStream cs = new ContentStreamBase.StringStream(body);
            request.addContentStream(cs);
            long startTime = System.currentTimeMillis();
            CoreAdminResponse res = new CoreAdminResponse();
            res.setResponse(server.request(request));
            res.setElapsedTime(System.currentTimeMillis() - startTime);
            returnVo.setData(res);
        } catch (Exception e) {
            log.warn("core:" + coreName, e);
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
            returnVo.setMsg(e.getMessage());
        }
        return returnVo;
    }
}
