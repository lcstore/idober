package com.lezo.idober.action;

import java.util.Date;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("doc")
@Controller
@Log4j
public class HashController {

    @RequestMapping(value = "hash", method = RequestMethod.GET)
    @ResponseBody
    public String updateHashId() throws Exception {
        updateHash();
        return "OK";
    }

    private void updateHash() throws Exception {
        Integer offset = 0;
        Integer limit = 1000;
        SolrServer server = SolrUtils.getSolrServer("core3");
        Pattern oReg = Pattern.compile("^[0-9]+$");
        while (true) {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(offset);
            solrQuery.setRows(limit);
            solrQuery.set("q", "*:*");
            solrQuery.addField(MovieSolr.getSolrFields());
            QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
            for (SolrDocument doc : resp.getResults()) {
                SolrInputDocument inDoc = new SolrInputDocument();
                for (String sName : doc.getFieldNames()) {
                    Object value = doc.getFieldValue(sName);
                    inDoc.addField(sName, value);
                }
                // oMovie.year+';'+oMovie.directors+';'+oMovie.name;
                String idString = doc.getFieldValue("year") + ";"
                        + doc.getFieldValue("directors") + ";"
                        + doc.getFieldValue("name");
                Date date = (Date) doc.getFieldValue("date");
                String dateString = DateFormatUtils.format(date, "yyyy-MM-dd");
                String sCode = toHashCode(dateString, idString);
                inDoc.setField("id", sCode);
                String sNames = "";
                Object nObject = doc.getFieldValue("name");
                Object enObject = doc.getFieldValue("enname");
                if (nObject != null) {
                    sNames = nObject.toString();
                    sNames += " ";
                }
                if (enObject != null) {
                    sNames += enObject.toString();
                }
                inDoc.addField("names", sNames);
                server.add(inDoc);
            }
            server.commit();
            offset += resp.getResults().size();
            log.info("update hash,offset:" + offset + ",size:" + resp.getResults().size());
            if (resp.getResults().size() < limit) {
                break;
            }
        }

    }

    // var oNames = {};
    // addNames(oNames,oData.name);
    // addNames(oNames,oData.enname);
    // addNames(oNames,oData.alias_names);
    // logger.info('oNames:'+JSON.stringify(oNames));
    // var sNames;
    // for(var sNameKey in oNames){
    // sNames = sNames?sNames+' '+sNameKey:sNameKey;
    // }
    // oMovie.names = sNames;

    private String toHashCode(String date, String source) {
        String sCode = date + source.hashCode();
        sCode = "" + sCode.hashCode();
        sCode = sCode.replace("-", "0");
        return sCode;
    }
}
