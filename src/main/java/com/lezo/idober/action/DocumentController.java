package com.lezo.idober.action;

import java.util.Date;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.ActionReturnVo;
import com.lezo.idober.vo.SolrDocListVo;
import com.lezo.idober.vo.SolrDocVo;
import com.lezo.idober.vo.SolrFieldVo;

@Log4j
@RequestMapping("doc")
@Controller
public class DocumentController {

    @RequestMapping(value = "{core}", method = RequestMethod.POST)
    @ResponseBody
    public ActionReturnVo addDocs(@PathVariable("core") String core, @RequestBody SolrDocListVo docs) {
        ActionReturnVo returnVo = new ActionReturnVo();
        try {
            // core = "core0";
            if (docs == null) {
                returnVo.setCode(ActionReturnVo.CODE_PARAM);
                returnVo.setMsg("非法参数");
                return returnVo;
            }
            log.info("docs:" + JSON.toJSONString(docs));
            SolrServer server = SolrUtils.getSolrServer(core);
            if ("1".equals(docs.getType())) {
                int count = 0;
                for (SolrDocVo docVo : docs.getDocs()) {
                    SolrInputDocument doc = new SolrInputDocument();
                    for (SolrFieldVo fld : docVo.getFields()) {
                        Object valObject = fld.getValue();
                        if (fld.getKey().equals("date") && NumberUtils.isNumber(fld.getValue())) {
                            valObject = new Date(Long.valueOf(fld.getValue()));
                        }
                        doc.addField(fld.getKey(), valObject);
                    }
                    if (core.equals("core0") || core.equals("core2")) {
                        Map<String, Object> fieldModifier = Maps.newHashMap();
                        fieldModifier.put("add", new Date());
                        doc.addField("creation", fieldModifier);
                    }
                    try {
                        server.add(doc);
                        count++;
                    } catch (Exception e) {
                        log.warn("add doc to core:" + core, e);
                    }
                }
                if (count > 0) {
                    server.commit();
                }
                log.info("core:" + core + ",create doc:" + count);
            }
        } catch (Exception e) {
            log.warn("core:" + core, e);
            returnVo.setCode(ActionReturnVo.CODE_FAIL);
            returnVo.setMsg(e.getMessage());
        }
        return returnVo;
    }
}
