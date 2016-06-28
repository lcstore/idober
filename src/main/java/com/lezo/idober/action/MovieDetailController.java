package com.lezo.idober.action;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.AESCodecUtils;
import com.lezo.idober.utils.SolrUtils;
import com.lezo.idober.vo.movie.MovieVo;

//@RequestMapping("movie/detail")
//@Controller
public class MovieDetailController extends BaseController {

    @RequestMapping(value = "{itemCode}", method = RequestMethod.GET)
    public String getItem(@PathVariable String itemCode, ModelMap model) throws Exception {
        // String idString = AESCodecUtils.decrypt(itemCode);
        itemCode = Jsoup.clean(itemCode, Whitelist.basic());
        Pattern oReg = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = oReg.matcher(itemCode);
        List<MovieSolr> mSolrs = null;
        if (matcher.find()) {
            String idString = AESCodecUtils.decrypt(itemCode);
            String[] unitArr = idString.split(";");
            if (unitArr.length >= 3) {
                int index = -1;
                String sYear = unitArr[++index];
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < unitArr.length - 1; i++) {
                    if (sb.length() > 0) {
                        sb.append(";");
                    }
                    sb.append(unitArr[i]);
                }
                String sDirector = sb.toString();
                String sName = unitArr[unitArr.length - 1];
                sDirector = ClientUtils.escapeQueryChars(sDirector);
                sName = ClientUtils.escapeQueryChars(sName);
                SolrQuery solrQuery = new SolrQuery();
                solrQuery.setStart(0);
                solrQuery.setRows(1);
                solrQuery.set("q", "(year:" + sYear + " AND directors:" + sDirector + " AND names:" + sName + ")");
                solrQuery.addField(MovieSolr.getSolrFields());
                QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
                mSolrs = resp.getBeans(MovieSolr.class);
            }
        } else {
            String idString = itemCode;
            idString = ClientUtils.escapeQueryChars(idString);
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(0);
            solrQuery.setRows(1);
            solrQuery.set("q", "id:" + idString);
            solrQuery.addField(MovieSolr.getSolrFields());
            QueryResponse resp = SolrUtils.getMovieServer().query(solrQuery);
            mSolrs = resp.getBeans(MovieSolr.class);
        }
        if (CollectionUtils.isEmpty(mSolrs)) {
            // 404,跳转首页
        } else {
            MovieSolr solrVo = mSolrs.get(0);
            MovieVo movieVo = new MovieVo();
            BeanUtils.copyProperties(solrVo, movieVo);
            JSONObject ctObject = JSONObject.parseObject(solrVo.getContent());
            if (ctObject != null) {
                movieVo.setStory(ctObject.getString("story"));
                if (StringUtils.isBlank(movieVo.getImgUrl())) {
                    movieVo.setImgUrl(ctObject.getString("img_url"));
                }
            }
            if (StringUtils.isNotBlank(solrVo.getTorrents())) {
                JSONArray tArray = JSONArray.parseArray(solrVo.getTorrents());
                JSONArray sArray = new JSONArray();
                JSONArray newArray = new JSONArray();
                for (int i = 0; i < tArray.size(); i++) {
                    JSONObject tObject = tArray.getJSONObject(i);
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
                movieVo.setTorrents(newArray.toJSONString());
                movieVo.setShares(sArray.toJSONString());
            }
            model.addAttribute("oDoc", movieVo);
        }
        return "MovieDetail";
    }
}
