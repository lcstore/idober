package com.lezo.idober.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.lezo.iscript.rest.http.HttpClientUtils;

@Log4j
@RequestMapping("torrent")
@Controller
public class TorrentController {
    private static DefaultHttpClient client = HttpClientUtils.createHttpClient();

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(String id, String hash, HttpServletResponse res) throws IOException {
        OutputStream os = res.getOutputStream();
        try {
            id = "26631";
            hash = "edebf34deb6bd517ce998118";
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=dict.torrent");
            res.setContentType("application/octet-stream; charset=utf-8");
            List<NameValuePair> nvPairs = Lists.newArrayList();
            nvPairs.add(new BasicNameValuePair("id", id));
            nvPairs.add(new BasicNameValuePair("uhash", hash));
            nvPairs.add(new BasicNameValuePair("imageField.x", "71"));
            nvPairs.add(new BasicNameValuePair("imageField.x", "71"));
            nvPairs.add(new BasicNameValuePair("imageField.y", "29"));
            nvPairs.add(new BasicNameValuePair("action", "download"));
            String sUrl = "http://www.bttiantang.com/download3.php";
            HttpPost post = new HttpPost(sUrl);
            post.setEntity(new UrlEncodedFormEntity(nvPairs, "UTF-8"));
            HttpResponse resp = client.execute(post);
            InputStream in = resp.getEntity().getContent();
            FileCopyUtils.copy(in, os);
            EntityUtils.consumeQuietly(resp.getEntity());
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

}
