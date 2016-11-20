package com.lezo.idober.handler;

import java.io.File;
import java.io.IOException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author lezo
 * @since 2016年2月20日
 */
@Log4j
public class SolrJSONCallBackDataHandler {
	private HttpClient client = new DefaultHttpClient();

	@Test
	public void handleAccepts() throws Exception {
		File dataDir = new File("/Users/lezo/douban-movie-detail");
		File[] fileList = dataDir.listFiles();
		for (File dFile : fileList) {
			String sLine = FileUtils.readFileToString(dFile);
			JSONObject oObj = JSONObject.parseObject(sLine);
			String sData = oObj.getString("data");
			oObj = JSONObject.parseObject(sData);
			JSONObject gObj = oObj;
			JSONObject dObj = gObj.getJSONObject("data");
			if (dObj == null) {
				continue;
			}
			String coreName = dObj.getString("core");
			if (StringUtils.isBlank(coreName)) {
				continue;
			}
			Object docObject = dObj.get("docs");
			if (docObject == null) {
				continue;
			}
			String content = docObject.toString();
			commit(coreName, content);
		}
	}

	private void commit(String coreName, String content) {
		String rawData = content;
		String url = "http://www.lezomao.com/" + coreName + "/update/json?commit=true";
		url = "http://127.0.0.1:8081/" + coreName + "/update/json?commit=true";

		HttpPost request = new HttpPost(url);
		try {
			request.addHeader("Content-Type", "application/json");
			request.addHeader("User-Agent",
					"Mozilla/5.0 (compatible; snapper/1.0; +http://www.lezomao.com)");
			StringEntity entity = new StringEntity(rawData, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse resp = client.execute(request);
			// System.err.println("resp:" + resp.getStatusLine());
			EntityUtils.consume(resp.getEntity());
		} catch (Exception e) {
			log.warn("commit cause:", e);
			try {
				commit(coreName, content);
			} catch (Exception e1) {
				log.warn("commit retry fail:", e1);
			}
		}
	}
}