package com.lezo.idober.action.movie;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lezo.idober.action.BaseController;

@Controller
@RequestMapping("movie")
public class MovieDownController extends BaseController {

	@RequestMapping("download")
	public ResponseEntity<byte[]> download(@RequestParam(value = "u", required = false) String url,
			@RequestParam("p") String params,
			@RequestParam(value = "n", required = false) String sName,
			@RequestParam(value = "m", defaultValue = "GET") String sMethod)
			throws IOException {
		if (StringUtils.isBlank(url)) {
			url = "http://www.bttiantang.com/download3.php";
		}
		Connection conn = Jsoup.connect(url);
		Connection.Method method = Method.GET;
		if ("POST".equals(sMethod.toUpperCase())) {
			method = Method.POST;
			if (StringUtils.isNotEmpty(params)) {
				params = params.replaceAll("&amp;", "&");
				params = params.replaceAll("ℑField", "&imageField");
				params = params.replaceAll("∾tion", "&action");
				String[] paramArr = params.split("&");
				for (String param : paramArr) {
					String[] kvArr = param.split("=");
					int index = -1;
					String key = kvArr[++index];
					String value = kvArr[++index];
					conn.data(key, value);
				}
			}
		} else {
			url += url.contains("?") ? params : "?" + params;
		}
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:47.0) Gecko/20100101 Firefox/47.0";
		String referrer = "http://www.bttiantang.com/download.php?n=%E8%87%B4%E5%91%BD%E9%AD%94%E6%9C%AFbt%E7%A7%8D%E5%AD%90%E4%B8%8B%E8%BD%BD.720p%E9%AB%98%E6%B8%85.torrent&temp=yes&"
				+ params;
		Response resp = conn.method(method).timeout(30000).referrer(referrer).userAgent(userAgent)
				.ignoreContentType(true).execute();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String disKey = "Content-Disposition";
		String attach = resp.header(disKey);
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
		byte[] dataBytes = resp.bodyAsBytes();
		return new ResponseEntity<byte[]>(dataBytes, headers, HttpStatus.OK);
	}
}
