package com.lezo.idober.action.movie;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.utils.ParamUtils;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.utils.TaskUtils;
import com.lezo.idober.vo.ActionReturnVo;

@Log4j
@Controller
@RequestMapping("movie/edit")
public class MovieEditListController extends BaseController {

    @ResponseBody
    @RequestMapping(value = { "search" }, method = RequestMethod.POST)
    public ActionReturnVo searchTorrents(@RequestBody JSONObject paramObject) throws Exception {
        ActionReturnVo returnVo = new ActionReturnVo();
        JSONArray nameArr = paramObject.getJSONArray("names");
        if (CollectionUtils.isEmpty(nameArr)) {
            returnVo.setMsg("empty name array");
            returnVo.setCode(ActionReturnVo.CODE_PARAM);
            return returnVo;
        }
        JSONArray taskArray = new JSONArray();
        List<String> typeList = Lists.newArrayList("sogou-article-search", "weibo-search-torrent", "query-movie");
        for (int index = 0, size = nameArr.size(); index < size; index++) {
            String name = nameArr.getString(index);
            if (StringUtils.isBlank(name)) {
                continue;
            }
            for (String type : typeList) {
                JSONObject taskObject = new JSONObject();
                taskObject.put("type", type);
                taskObject.put("url", "");
                taskObject.put("level", 1000);
                taskObject = TaskUtils.withParam(taskObject, "retry", "0");
                taskObject = TaskUtils.withParam(taskObject, "title", name);
                taskArray.add(taskObject);
                if (taskArray.size() >= 100) {
                    TaskUtils.createTasks(taskArray);
                    taskArray = new JSONArray();
                }
            }
        }
        TaskUtils.createTasks(taskArray);
        return returnVo;
    }

    @RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
    public ModelAndView listFix(ModelMap model, HttpServletRequest request)
            throws Exception {
        RedirectView red = new RedirectView("/movie/edit/list.html", true);
        red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return new ModelAndView(red);
    }

    @RequestMapping(value = { "list.html" }, method = RequestMethod.GET)
    public ModelAndView listFirstEditPage(ModelMap model, HttpServletRequest request) throws Exception {
        return listEditPage(1, model, request);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = { "list/{curPage}.html" }, method = RequestMethod.GET)
    public ModelAndView listEditPage(@PathVariable("curPage") Integer curPage, ModelMap model,
            HttpServletRequest request) throws Exception {
        JSONObject paramObject = new JSONObject(request.getParameterMap());
        int beforeDay = paramObject.getIntValue("beforeDay");
        beforeDay = beforeDay < 1 ? 90 : beforeDay;
        curPage = ParamUtils.inRange(curPage);
        int start = (curPage - 1) * ParamUtils.PAGE_SIZE;
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(start);
        solrQuery.setRows(ParamUtils.PAGE_SIZE);
        // solrQuery.set("q", "(release:[NOW-" + beforeDay +
        // "DAY/DAY TO NOW/DAY+7DAY])");
        solrQuery.set("q", "(release:[* TO NOW/DAY+7DAY])");
        solrQuery.addFilterQuery("type:movie");
        solrQuery.addFilterQuery("(torrents_size:0 AND shares_size:0)");
        solrQuery.addSort("release", ORDER.desc);

        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
        SolrDocumentList docList = resp.getResults();
        long total = docList.getNumFound();
        long totalPage = ParamUtils.batchNum(total, ParamUtils.PAGE_SIZE);
        totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);

        String sPath = request.getPathInfo();
        sPath = sPath.replaceAll("[0-9/]*\\.html.*$", "");
        sPath = sPath.replaceAll("/$", "");

        fillFeedInfo(docList);
        // 按照src_count倒排
        Comparator<SolrDocument> c = new Comparator<SolrDocument>() {
            @Override
            public int compare(SolrDocument o1, SolrDocument o2) {
                String key = "src_count";
                Object scObject1 = o1.getFieldValue(key);
                Object scObject2 = o2.getFieldValue(key);
                if (scObject1 == null) {
                    return 1;
                } else if (scObject2 == null) {
                    return -1;
                }
                return Integer.valueOf(scObject2.toString()).compareTo(Integer.valueOf(scObject1.toString()));
            }
        };
        Collections.sort(docList, c);
        model.addAttribute("oDocList", docList);
        model.addAttribute("curPage", curPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("curPath", sPath);
        addNewlyEditDoc(model, beforeDay);
        model.addAttribute("qAction", "/search/movie/edit");
        return new ModelAndView("MovieEditList");
    }

    @RequestMapping(value = { "wait" }, method = RequestMethod.GET)
    public ModelAndView waitEditPage(ModelMap model,
            HttpServletRequest request) throws Exception {
        return waitEditPage(1, model, request);
    }

    @RequestMapping(value = { "wait/{curPage}.html" }, method = RequestMethod.GET)
    public ModelAndView waitEditPage(@PathVariable("curPage") Integer curPage, ModelMap model,
            HttpServletRequest request) throws Exception {
        curPage = ParamUtils.inRange(curPage);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(1);
        solrQuery.set("q", "type:idober-movie-ids");
        solrQuery.addFilterQuery("group_s:wait4edit");

        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META).query(solrQuery);
        SolrDocumentList docList = resp.getResults();
        if (!docList.isEmpty()) {
            String sContent = docList.get(0).getFieldValue("content").toString();
            sContent = sContent.replaceAll("\\[", "");
            sContent = sContent.replaceAll("\\]", "");
            String[] unitArr = sContent.split(",");

            int fromIndex = (curPage - 1) * ParamUtils.PAGE_SIZE;
            int toIndex = fromIndex + ParamUtils.PAGE_SIZE;
            String sHead = "id:(";
            StringBuilder sb = new StringBuilder(sHead);
            for (int index = fromIndex, size = unitArr.length; index < size && index <= toIndex; index++) {
                if (sb.length() > sHead.length()) {
                    sb.append(" OR ");
                }
                String idString = unitArr[index].trim();
                sb.append(idString);
            }
            sb.append(")");

            solrQuery = new SolrQuery();
            solrQuery.setStart(0);
            solrQuery.setRows(ParamUtils.PAGE_SIZE);
            solrQuery.set("q", sb.toString());
            solrQuery.addFilterQuery("type:movie");
            solrQuery.addFilterQuery("(torrents_size:0 AND shares_size:0)");
            solrQuery.addSort("release", ORDER.desc);
            resp = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_MOVIE).query(solrQuery);
            docList = resp.getResults();
            fillFeedInfo(docList);
            long total = unitArr.length;
            long totalPage = ParamUtils.batchNum(total, ParamUtils.PAGE_SIZE);
            totalPage = Math.min(totalPage, ParamUtils.MAX_PAGE_NUM);

            model.addAttribute("oDocList", docList);
            model.addAttribute("curPage", curPage);
            model.addAttribute("totalPage", totalPage);
        } else {
            model.addAttribute("curPage", curPage);
            model.addAttribute("totalPage", 0);
        }
        String sPath = request.getPathInfo();
        sPath = sPath.replaceAll("[0-9/]*\\.html.*$", "");
        sPath = sPath.replaceAll("/$", "");

        model.addAttribute("curPath", sPath);
        model.addAttribute("qAction", "/search/movie/edit");
        return new ModelAndView("MovieEditList");
    }

    private void fillFeedInfo(SolrDocumentList docList) {
        if (CollectionUtils.isEmpty(docList)) {
            return;
        }
        SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_SOURCE_META);
        SolrQuery solrQuery = new SolrQuery("id:pan.baidu.com;*");
        solrQuery.setRows(0);
        solrQuery.addFilterQuery("!delete_ti:1");
        for (SolrDocument doc : docList) {
            String name = doc.getFieldValue("name").toString();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            name = ClientUtils.escapeQueryChars(name);
            solrQuery.set("q", "title:" + name);
            try {
                QueryResponse resp = server.query(solrQuery);
                long srcCount = resp.getResults().getNumFound();
                doc.setField("src_count", srcCount);
            } catch (Exception e) {
                log.warn("fillFeedInfo,name:" + name + ",cause:", e);
            }
        }
    }

    private void addCrumbs(ModelMap model, String sRegion, String sCNRegion) {
        List<JSONObject> crumbList = Lists.newArrayList();
        JSONObject oCrumbObj = new JSONObject();
        oCrumbObj.put("title", "电影");
        oCrumbObj.put("link", "/movie.html");
        crumbList.add(oCrumbObj);
        oCrumbObj = new JSONObject();
        oCrumbObj.put("title", sCNRegion);
        oCrumbObj.put("link", "/movie/region/" + sRegion + ".html");
        crumbList.add(oCrumbObj);
        model.addAttribute("oCrumbList", crumbList);
    }

    private void addNewlyEditDoc(ModelMap model, int beforeDay) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        solrQuery.addField("cover,id,name,rate");
        solrQuery.set("q", "editor:1");
        solrQuery.addSort("timestamp", ORDER.desc);
        solrQuery.addFilterQuery("type:movie");
        solrQuery.addFilterQuery("(release:[NOW-" + beforeDay + "DAY/DAY TO *])");

        QueryResponse resp = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE).query(solrQuery);
        SolrDocumentList docList = resp.getResults();

        model.addAttribute("oStarList", docList);
    }

}
