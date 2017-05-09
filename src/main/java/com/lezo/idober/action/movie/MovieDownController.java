package com.lezo.idober.action.movie;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.lezo.idober.action.BaseController;
import com.lezo.idober.config.AppConfig;
import com.lezo.iscript.rest.http.HttpClientManager;

@Log4j
@Controller
@RequestMapping("movie")
public class MovieDownController extends BaseController {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final DefaultHttpClient HTTP_CLIENT = HttpClientManager.getDefaultHttpClient();
    @Autowired
    private AppConfig config;

    @RequestMapping("download")
    public ResponseEntity<byte[]> download(@RequestParam(value = "u", required = false) String url,
            @RequestParam("p") String params,
            @RequestParam(value = "n", required = false) String sName,
            @RequestParam(value = "m", defaultValue = "G") String sMethod,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "tid", defaultValue = "") String torrentId
            )
                    throws Exception {
        if (StringUtils.isBlank(url)) {
            url = "http://www.bttiantang.com/download3.php";
        } else {
            url = URLDecoder.decode(url, DEFAULT_CHARSET);
        }
        HttpUriRequest request = null;
        if ("P".equals(sMethod.toUpperCase())) {
            HttpPost post = new HttpPost(url);
            if (StringUtils.isNotEmpty(params)) {
                params = URLDecoder.decode(params, DEFAULT_CHARSET);
                params = params.replaceAll("&amp;", "&");
                params = params.replaceAll("ℑField", "&imageField");
                params = params.replaceAll("∾tion", "&action");
                if (url.contains(".bttiantang.com")) {
                    List<NameValuePair> paramList = Lists.newArrayList();
                    String[] paramArr = params.split("&");
                    for (String param : paramArr) {
                        String[] kvArr = param.split("=");
                        int index = -1;
                        String key = kvArr[++index];
                        String value = kvArr[++index];
                        NameValuePair nvPair = new BasicNameValuePair(key, value);
                        paramList.add(nvPair);
                    }
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, DEFAULT_CHARSET);
                    post.setEntity(entity);
                } else {
                    HttpEntity entity = new StringEntity(params, DEFAULT_CHARSET);
                    post.setEntity(entity);
                }
            }
            request = post;
        } else {
            if (StringUtils.isNotBlank(params)) {
                params = URLDecoder.decode(params, DEFAULT_CHARSET);
                url += url.contains("?") ? params : "?" + params;
                params = "";
            }
            request = new HttpGet(url);
        }
        if (url.contains(".rarbt.com")) {
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }
        request.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:53.0) Gecko/20100101 Firefox/53.0");
        torrentId = createTorrentId(url, params, torrentId);
        JSONObject torrentObject = loadTorrents(id, torrentId);
        if (torrentObject == null) {
            torrentObject = executeRequest(request, url, sName);
            saveTorrentFile(id, torrentId, torrentObject);
        }
        byte[] dataBytes = torrentObject.getBytes("raw");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String sFileName = torrentObject.getString("name");
        sFileName = new String(sFileName.getBytes(), "iso-8859-1");
        headers.setContentDispositionFormData("attachment", sFileName);
        return new ResponseEntity<byte[]>(dataBytes, headers, HttpStatus.OK);
    }

    private void saveTorrentFile(String id, String torrentId, JSONObject torrentObject) throws Exception {
        if (torrentObject == null || StringUtils.isBlank(id) || StringUtils.isBlank(torrentId)) {
            return;
        }
        File torrentFile = new File(config.getTorrentDir(), id + File.separator + torrentId + ".data");
        if (torrentFile.exists()) {
            return;
        }
        if (torrentFile.getParentFile() != null) {
            torrentFile.getParentFile().mkdirs();
        }
        FileUtils.writeStringToFile(torrentFile, torrentObject.toJSONString(), DEFAULT_CHARSET);
    }

    private JSONObject executeRequest(HttpUriRequest request, String url, String sName) throws Exception {
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
        String referrer = url;
        request.setHeader("Referer", referrer);
        request.setHeader("User-Agent", userAgent);
        HttpResponse resp = HTTP_CLIENT.execute(request);

        String disKey = "Content-Disposition";
        Header destHeader = resp.getLastHeader(disKey);
        String attach = destHeader == null ? null : destHeader.getValue();
        String fileName = attach;
        String sDomain = "lezomao.com-";
        if (StringUtils.isNotBlank(sName)) {
            sName = sName.endsWith(".torrent") ? sName : sName + ".torrent";
            sName = sName.replaceAll("\\s{2,}", "");
            sName = sDomain + sName;
            // sName = URLEncoder.encode(sName, "UTF-8");
            fileName = sName;
        } else if (StringUtils.isNotBlank(attach)) {
            fileName = sDomain + sName;
        } else {
            fileName = sDomain + System.currentTimeMillis() + ".torrent";
        }
        JSONObject tObject = null;
        if (resp.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
            byte[] dataBytes = EntityUtils.toByteArray(resp.getEntity());
            tObject = new JSONObject();
            tObject.put("raw", dataBytes);
            tObject.put("name", fileName);
        }
        return tObject;
    }

    private JSONObject loadTorrents(String id, String torrentId) {
        File torrentFile = new File(config.getTorrentDir(), id + File.separator + torrentId + ".data");
        JSONObject tObject = null;
        if (torrentFile.exists()) {
            try {
                String sContent = FileUtils.readFileToString(torrentFile, DEFAULT_CHARSET);
                tObject = JSONObject.parseObject(sContent);
            } catch (Exception e) {
                torrentFile.deleteOnExit();
                log.warn("load id:" + id + ",tid:" + torrentId + ",cause:", e);
            }
        }
        return tObject;
    }

    private String createTorrentId(String url, String params, String torrentId) {
        if (StringUtils.isBlank(torrentId)) {
            Hasher hasher = Hashing.md5().newHasher();
            hasher.putString(url);
            if (params != null) {
                hasher.putString(params);
            }
            torrentId = hasher.hash().toString();
        }
        return torrentId;
    }
}
