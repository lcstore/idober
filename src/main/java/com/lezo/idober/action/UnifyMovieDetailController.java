package com.lezo.idober.action;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.solr.pojo.UnifyMovieSolr;
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("movie/detail")
@Controller
public class UnifyMovieDetailController extends BaseController {
    private static final Pattern NUM_REG = Pattern.compile("^[0-9]+$");
    private static final String CORE_MOVIE = "cmovie";

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public ModelAndView loadDetail(@PathVariable String itemCode, ModelMap model) throws Exception {
        // String idString = AESCodecUtils.decrypt(itemCode);
        itemCode = Jsoup.clean(itemCode, Whitelist.basic());
        Matcher matcher = NUM_REG.matcher(itemCode);
        if (!matcher.find()) {
            throw new NotFoundException();
        }
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(1);
        solrQuery.set("q", "(id:" + itemCode + " OR old_id_s:" + itemCode + ")");
        // String fields = SolrUtils.getSolrFields(UnifyMovieSolr.class);
        // solrQuery.addField(fields);
        QueryResponse resp = SolrUtils.getSolrServer(CORE_MOVIE).query(solrQuery);
        SolrDocumentList docList = resp.getResults();
        if (CollectionUtils.isEmpty(docList)) {
            throw new NotFoundException();
        }
        SolrDocument doc = docList.get(0);
        String oldCode = ObjectUtils.toString(doc.getFieldValue("old_id_s"), null);
        String idString = ObjectUtils.toString(doc.getFieldValue("id"), StringUtils.EMPTY);
        if (!idString.equals(itemCode) && StringUtils.isNotBlank(oldCode)) {
            RedirectView red = new RedirectView("/movie/detail/" + idString, true);
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return new ModelAndView(red);
        }
        JSONObject dObject = convert2JSON(doc);
        model.addAttribute("oDoc", dObject);
        return new ModelAndView("MovieDetail");
    }

    private JSONObject convert2JSON(SolrDocument doc) {
        SolrDocumentList documentList = new SolrDocumentList();
        documentList.add(doc);
        List<UnifyMovieSolr> solrList = new DocumentObjectBinder().getBeans(UnifyMovieSolr.class, documentList);
        // JSONObject dObject = (JSONObject) JSONObject.toJSON(doc);
        // UnifyMovieSolr solrVo = JSONObject.toJavaObject(dObject, UnifyMovieSolr.class);
        UnifyMovieSolr solrVo = solrList.get(0);
        List<String> tArray = solrVo.getTorrents();
        JSONArray sArray = new JSONArray();
        JSONArray newArray = new JSONArray();
        // assortTorrents(dObject);
        for (int i = 0; i < tArray.size(); i++) {
            JSONObject tObject = JSONObject.parseObject(tArray.get(i));
            String type = tObject.getString("type");
            String source = tObject.getString("source");
            if (StringUtils.isBlank(type) && source.equals("bttiantang-torrent")) {
                tObject.remove("source");
                tObject.put("type", source);
                JSONObject pObject = tObject.getJSONObject("param");
                StringBuilder sb = new StringBuilder();
                for (String key : pObject.keySet()) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(pObject.getString(key));
                }
                tObject.put("param", sb.toString());
                tObject.put("url", "");
                newArray.add(tObject);
            } else if (type != null && type.endsWith("-share")) {
                sArray.add(tObject);
            } else if (type != null) {
                newArray.add(tObject);
            }
        }
        JSONObject dObject = (JSONObject) JSONObject.toJSON(solrVo);
        dObject.put("torrents", newArray);
        dObject.put("shares", sArray);
        return dObject;
    }

    private void assortTorrents(JSONObject dObject) {

    }
}
