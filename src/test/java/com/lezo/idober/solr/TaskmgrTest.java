package com.lezo.idober.solr;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TaskmgrTest {

	@Test
	public void test() throws Exception {
		String title = "夺命枪火";
		String url = "http://www.lezomao.com:8090/moviemgr/fetch?title=" + title;
		String referrer = "http://www.lezomao.com";
		Response resp =
				Jsoup.connect(url).referrer(referrer).method(Method.GET)
						.ignoreContentType(true).execute();
		String body = resp.body();
		System.err.println("body:" + body);
	}

	@Test
	public void testDown() throws Exception {
		String url =
				"http://www.hdpfans.com/forum.php?mod=attachment&aid=NTY0MzAxfGYxM2FkN2UxfDE0NjY0MzcxNTh8MjQ3Nzc5OXw3MDA4NTc%3D";
		Connection conn = Jsoup.connect(url);
		Connection.Method method = Method.GET;
		String value =
				"mf0t_2132_saltkey=UohmpYzn; mf0t_2132_lastvisit=1466431745; mf0t_2132_sid=B4mTmr; mf0t_2132_lastact=1466436318%09misc.php%09patch; mf0t_2132_pc_size_c=0; mf0t_2132_st_p=2477799%7C1466436316%7C29752a5bdd6e3eab4b75f9de980cd619; mf0t_2132_visitedfid=46; mf0t_2132_viewid=tid_598718; PHPSESSID=o4nvv15i6s76fi6f3esgff32p0; _fmdata=20F13279E0D2A13BDD58E388E3F0EBB74C22CDFBBA2AF032FC1679B725CFB2BF877C613FFF334EFD96F8B65E2EFBE42277E5B7AC4D2E7093; BAIDU_SSP_lcr=https://www.baidu.com/link?url=gw1BaUNySysteHwI4Zft2e_TO3Zc2QqZzQ2r6Hzix16es8ove0WyxK4Lk6gfwDi0dHAiZuATgdPOF8cv2M7CCq&wd=&eqid=c4f31269000079070000000457680706; pgv_pvi=3778071756; pgv_info=ssi=s3801071517; CNZZDATA2531405=cnzz_eid%3D1924129941-1466430069-null%26ntime%3D1466431439; Hm_lvt_f789e10ab94b9ef3c0f8eda705030c04=1466435346,1466435749; Hm_lpvt_f789e10ab94b9ef3c0f8eda705030c04=1466436318; mf0t_2132_st_t=2477799%7C1466435836%7C0e2ce4c87ff4b8acdd46f8c6790957c0; mf0t_2132_atarget=1; mf0t_2132_forum_lastvisit=D_46_1466435836; mf0t_2132_ulastactivity=d140O3MDCyoBncxA%2BwgHOg4T%2FZVDFAc2cMenn6%2BnOMv2cciArtVJ; mf0t_2132_auth=5b3ct12L0kJ0rqQe9USbMAlqB8cE%2FkBCzjr0GNEg5x6H5E%2F66iVIXxLZVlTelwvBggVry81BUjDTrVGQn5vAUm%2Bxu322; mf0t_2132_myrepeat_rr=R0; mf0t_2132_lip=101.229.119.214%2C1466435782; mf0t_2132_connect_is_bind=0; mf0t_2132_security_cookiereport=78a6q9BH6wCKlYbp93O63e0cdV5l8T1ayIEBdjZB3rc0VT5oiN0A; mf0t_2132_nofavfid=1; tjpctrl=1466437588685; mf0t_2132_smile=1D1; mf0t_2132_connect_not_sync_t=1; mf0t_2132_sendmail=1";
		String referrer = "http://www.hdpfans.com/forum-46-1.html";
		Response resp =
				conn.method(method).referrer(referrer).header("Cookie", value).ignoreContentType(true).execute();
		System.err.println("headers" + JSONObject.toJSONString(resp.headers()));
		byte[] dataBytes = resp.bodyAsBytes();
		System.err.println("dataBytes" + dataBytes.length + ",body:" + new String(dataBytes));
	}

	@Test
	public void testBttiantangDown() throws Exception {
		String params = "id=14176&uhash=d0466b991fca3125cb0f9b1c&imageField.x=71&imageField.y=29&action=download";
		String url = "http://www.bttiantang.com/download3.php";
		// url+=params;
		Connection conn = Jsoup.connect(url);
		Connection.Method method = Method.POST;
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
		Response resp =
				conn.ignoreContentType(true).method(method).execute();
		System.err.println("headers" + JSONObject.toJSONString(resp.headers()));
		byte[] dataBytes = resp.bodyAsBytes();
		System.err.println("dataBytes" + dataBytes.length + ",body:" + new String(dataBytes));
	}

	@Test
	public void testClient() throws Exception {
		HttpClient client = new DefaultHttpClient();
		String uri = "http://www.rarbt.com/index.php/dow/index.html?id=22111&zz=2";
		HttpPost post = new HttpPost(uri);
		String content = "id=22128&zz=zz2&imageField.x=45&imageField.y=30";
		HttpEntity entity = new StringEntity(content, "UTF-8");
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Referer", "http://www.rarbt.com/index.php/dow/index.html?id=22111&zz=2");
		HttpResponse resp = client.execute(post);

		System.err.println("getStatusLine:" + resp.getStatusLine());
		HeaderIterator it = resp.headerIterator();
		while (it.hasNext()) {
			Header header = it.nextHeader();
			System.err.println("header:" + header);
		}
		System.err.println("resp:" + JSON.toJSONString(EntityUtils.toString(resp.getEntity())));
	}
}
