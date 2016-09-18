package com.lezo.idober.action.movie;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.lezo.idober.action.BaseController;
import com.lezo.iscript.rest.http.HttpClientManager;

@Controller
@RequestMapping("movie")
public class MovieDownController extends BaseController {
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final DefaultHttpClient HTTP_CLIENT = HttpClientManager.getDefaultHttpClient();

	@RequestMapping("download")
	public ResponseEntity<byte[]> download(@RequestParam(value = "u", required = false) String url,
			@RequestParam("p") String params,
			@RequestParam(value = "n", required = false) String sName,
			@RequestParam(value = "m", defaultValue = "G") String sMethod)
			throws IOException {
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
			}
			request = new HttpGet(url);
		}
		if (url.contains(".rarbt.com")) {
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		String referrer = url;
		request.setHeader("Referer", referrer);
		request.setHeader("User-Agent", userAgent);
		HttpResponse resp = HTTP_CLIENT.execute(request);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String disKey = "Content-Disposition";
		Header destHeader = resp.getLastHeader(disKey);
		String attach = destHeader == null ? null : destHeader.getValue();
		if (StringUtils.isNotBlank(sName)) {
			sName = sName.endsWith(".torrent") ? sName : sName + ".torrent";
			sName = sName.replaceAll("\\s{2,}", "");
			sName = "【lezomao.com】" + sName;
			// sName = URLEncoder.encode(sName, "UTF-8");
			sName = new String(sName.getBytes(), "iso-8859-1");
			headers.setContentDispositionFormData("attachment", sName);
		} else if (StringUtils.isNotBlank(attach)) {
			headers.set(disKey, attach);
		} else {
			headers.setContentDispositionFormData("attachment", System.currentTimeMillis() + ".torrent");
		}
		byte[] dataBytes = EntityUtils.toByteArray(resp.getEntity());
		return new ResponseEntity<byte[]>(dataBytes, headers, HttpStatus.OK);
	}
}
