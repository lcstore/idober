package com.lezo.idober.solr;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.apache.solr.util.TimeZoneUtils;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.idober.solr.pojo.ItemSolr;
import com.lezo.idober.solr.pojo.MovieSolr;

@Log4j
public class SolrQueryTest {
	HttpSolrServer server;

	@Before
	public void setup() throws Exception {
		server = new HttpSolrServer("http://www.lezomao.com/omovie");
		// server = new HttpSolrServer("http://localhost:8081/core2");
		System.setProperty("solr.solr.home", "/apps/src/istore/solr_home");
		// CoreContainer.Initializer initializer = new
		// CoreContainer.Initializer();
		// CoreContainer coreContainer = initializer.initialize();
		// // server = new EmbeddedSolrServer(coreContainer, "collection1");
		// server = new EmbeddedSolrServer(coreContainer, "core0");
	}

	@Test
	public void testQuery() throws Exception {
		SolrQuery solrQuery = new SolrQuery("牛奶");
		String queryString =
				"group=true&group.field=itemCode&group.query=stockNum:[1%20TO%20*]&group.main=true&group.sort=commentNum%20desc&group.sort=score%20desc";
		SolrParams params = SolrRequestParsers.parseQueryString(queryString);
		solrQuery.add(params);
		solrQuery.add("group.offset", "0");
		solrQuery.add("group.limit", Integer.MAX_VALUE + "");
		StringBuilder sb = new StringBuilder();
		for (Field fld : ItemSolr.class.getDeclaredFields()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(fld.getName());
		}
		solrQuery.addField(sb.toString());
		// QueryRequest request = new QueryRequest(solrQuery);
		// QueryResponse resp = request.process(server);
		QueryResponse response = server.query(solrQuery);
		System.err.println(JSON.toJSONString(response.getResponse()));
		// List<ItemSolr> itemList = response.getBeans(ItemSolr.class);
		// System.err.println(JSON.toJSONString(itemList));
	}

	@Test
	public void testQueryKeyWord() throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "*");
		solrQuery.setStart(0);
		solrQuery.setRows(1000);
		QueryResponse response = server.query(solrQuery);
		System.err.println(JSON.toJSONString(response.getResults()));
		List<MovieSolr> itemList = response.getBeans(MovieSolr.class);
		System.err.println(JSON.toJSONString(itemList));
		System.err.println(TimeZoneUtils.KNOWN_TIMEZONE_IDS);
		for (MovieSolr item : itemList) {
			Date newDate = item.getDate();
			Calendar c = Calendar.getInstance(TimeZoneUtils.getTimeZone("UTC"));
			c.setTime(newDate);
			System.err.println(DateFormatUtils.format(c, DateFormatUtils.ISO_DATE_FORMAT.getPattern()));
		}
	}

	@Test
	public void testQueryId() throws Exception {
		String sRegions =
				"巴林,埃及,伊朗,伊拉克,以色列,约旦,科威特,黎巴嫩,阿曼,卡塔尔,沙特,叙利亚,阿联酋,也门,巴勒斯坦,阿尔及利亚,利比亚,摩洛哥,突尼斯,苏丹,毛里塔尼亚,索马里,土耳其,塞浦路斯,阿富汗";
		StringBuilder sb = new StringBuilder();
		String sHead = "(";
		String[] regionArr = sRegions.split(",");
		sb.append(sHead);
		for (String region : regionArr) {
			if (sb.length() > sHead.length()) {
				sb.append(" OR ");
			}
			sb.append("regions:" + region);
		}
		sb.append(")");

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "(!shares:*)");
		solrQuery.setFields("id,torrents,shares,code_s");
		Set<String> lineSet = Sets.newHashSet();
		solrQuery.setStart(0);
		solrQuery.setRows(500);
		Set<String> genreSet = Sets.newHashSet();
		while (true) {
			QueryResponse response = server.query(solrQuery);
			for (SolrDocument rs : response.getResults()) {
				String sName = rs.getFieldValue("code_s").toString();
				genreSet.add(sName);

				List<String> tObject = null;
				try {
					tObject = (List<String>) rs.getFieldValue("torrents");
				} catch (Exception e) {
					System.err.println("bad,code_s:" + sName);
					e.printStackTrace();
					continue;
				}
				tObject = tObject == null ? new ArrayList<String>() : tObject;

				List<String> sObject = (List<String>) rs.getFieldValue("shares");
				sObject = sObject == null ? new ArrayList<String>() : sObject;

				SolrInputDocument inDoc = new SolrInputDocument();
				inDoc.addField("id", rs.getFieldValue("id"));
				inDoc.addField("torrents", tObject);
				// inDoc.addField("torrents_size", tObject.size());
				inDoc.addField("shares", sObject);
				// inDoc.addField("shares_size", sObject.size());

				Map<String, Object> map = Maps.newHashMap();
				inDoc.remove("code_s");
				map.put("set", sName);
				inDoc.addField("code_s", map);
				server.add(inDoc);
			}
			server.commit();
			if (response.getResults().size() < solrQuery.getRows()) {
				break;
			}
			solrQuery.setStart(solrQuery.getStart() + solrQuery.getRows());
		}
		File file = new File("./regionid.txt");
		// FileUtils.writeLines(file, lineSet);
		System.err.println("genreSet:" + ArrayUtils.toString(genreSet));
		// System.err.println(JSON.toJSONString(response.getResults()));
		// List<ItemSolr> itemList = response.getBeans(ItemSolr.class);
		// System.err.println(JSON.toJSONString(itemList));

	}

	@Test
	public void testSolrDelete() throws Exception {
		String title = ClientUtils.escapeQueryChars("2015;李恩熙;纯情");
		server.deleteByQuery("type:douban-movie-sort12");
		server.commit();
		server.optimize();
	}

	@Test
	public void testCommit() throws Exception {
		String rawData = FileUtils.readFileToString(new File("/Users/lezo/Downloads/book.json"), "UTF-8");
		String url = "http://localhost:8081/cmeta/update/json?commit=true";
		url = "http://www.lezomao.com/cmovie/update/json?commit=true";
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		try {
			request.addHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(rawData, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse resp = client.execute(request);
			System.err.println("resp:" + resp.getStatusLine());
			EntityUtils.consume(resp.getEntity());
		} catch (Exception e) {
			log.warn("commit cause:", e);
		}
	}

	@Test
	public void testKvCommit() throws Exception {
		String type = "idober-group-region";
		List<String> lines = FileUtils.readLines(new File("src/test/resources/region.txt"), "UTF-8");
		String splitor = ",";
		JSONArray tArray = new JSONArray();
		for (String line : lines) {
			line = line.trim();
			String[] mainArr = line.split("=");
			String regionName = mainArr[0].trim();
			String[] countryArr = mainArr[1].split(splitor);
			JSONObject dObject = new JSONObject();
			dObject.put("type", type);
			dObject.put("title", regionName);
			String sCode = PinyinHelper.convertToPinyinString(regionName, "", PinyinFormat.WITHOUT_TONE);
			dObject.put("short_s", sCode);
			Set<String> synSet = Sets.newHashSet();
			for (String country : countryArr) {
				synSet.add(country);
			}
			dObject.put("group_ss", synSet);
			dObject.put("id", type + ";" + regionName);
			tArray.add(dObject);
		}
		String sContent = tArray.toJSONString();
		System.err.println(sContent);
		doCommit(sContent);
	}

	public void doCommit(String rawData) {
		System.err.println(rawData);
		String url = "http://localhost:8081/cmeta/update/json?commit=true";
		url = "http://www.lezomao.com/cmeta/update/json?commit=true";
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		try {
			request.addHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(rawData, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse resp = client.execute(request);
			System.err.println("resp:" + resp.getStatusLine());
			EntityUtils.consume(resp.getEntity());
		} catch (Exception e) {
			log.warn("commit cause:", e);
		}
	}

	@Test
	public void testSyncCountryCommit() throws Exception {
		String type = "idober-synonym-country";
		List<String> lines = FileUtils.readLines(new File("src/test/resources/country.txt"), "UTF-8");
		String splitor = "\t";
		JSONArray tArray = new JSONArray();
		for (String line : lines) {
			line = line.trim();
			int index = line.indexOf(splitor);
			String sCode = line.substring(0, index);
			int nextIndex = line.indexOf(splitor, index + 1);
			String sCnName = line.substring(index + 1, nextIndex);
			String sEnName = line.substring(nextIndex + 1);
			sEnName = sEnName.toLowerCase();
			if (sCnName.equals("东帝汶")) {
				sCode = "TLS";
			}
			sCnName = sCnName.replace("（中国）", "");
			System.err.println("sCode:" + sCode + ",sCnName:" + sCnName + ",sEnName:" + sEnName);
			sCode = sCode.trim().toLowerCase();
			sEnName = sEnName.trim().toLowerCase();
			sCnName = sCnName.trim().toLowerCase();
			JSONObject dObject = new JSONObject();
			dObject.put("type", type);
			dObject.put("title", sCnName);
			dObject.put("short_s", sCode);
			Set<String> synSet = Sets.newHashSet();
			synSet.add(sCode);
			synSet.add(sCnName);
			synSet.add(sEnName);
			if (sCode.equals("us")) {
				synSet.add("usa");
			} else if (sCode.equals("cn")) {
				synSet.add("中国大陆");
				synSet.add("大陆");
			} else if (sCode.equals("ru")) {
				synSet.add("俄罗斯");
				synSet.add("苏联");
				synSet.add("俄国");
				synSet.add("russia");
			} else if (sCode.equals("id")) {
				synSet.add("印尼");
			} else if (sCode.equals("au")) {
				synSet.add("澳洲");
			} else if (sCode.equals("hk")) {
				synSet.add("中国香港");
			}
			dObject.put("synonym_ss", synSet);
			dObject.put("id", type + ";" + sCode);
			tArray.add(dObject);

		}
		String rawData = tArray.toJSONString();
		System.err.println(rawData);
		String url = "http://localhost:8081/cmeta/update/json?commit=true";
		url = "http://www.lezomao.com/cmeta/update/json?commit=true";
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		try {
			request.addHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(rawData, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse resp = client.execute(request);
			System.err.println("resp:" + resp.getStatusLine());
			EntityUtils.consume(resp.getEntity());
		} catch (Exception e) {
			log.warn("commit cause:", e);
		}
	}

	@Test
	public void testTxt() {
		String srcString =
				"Belarus, , Philippines, 丹麦  Denmark, Morocco, Kyrgyzstan, 巴西Brazil, 比利時, 蒙古人民共和国, U.K., 哥斯达黎加, 約旦, France, Puerto Rico, 美国/加拿大, 中国大陆, Latvia, Bangladesh, Bosnia and Herzegovina, US, 开曼群岛, 墨西哥 Mexico, 马耳他, 多伦多电影节, UK, U.S.A., 卢森堡, 以色列 Israel, Mexico, 秘鲁, Canada, 亚美尼亚 | 德国, 老挝, 土耳其 Turkey, USA | UK | Thailand | Swa, 塞浦路斯, 委内瑞拉 Venezuela, 台灣, 瑞士 Switzerland, 苏丹, 比利时, Lebanon, 韓國, 菲律賓, 约旦, 瑞典 Sweden, usa, brasil, 星加坡, 点子工作室, 中国香港, 新加坡, 戛纳电影节, 印度 Idian, 伊拉克, italy, 墨西哥, 挪威 Norway, 朝鲜, 前西德, uk, Slovenia, 沙特阿拉伯, 乍得, Burkina Faso, 希臘, 阿根廷, 奥地利, 联邦德国, 埃及, poland, 瑞典, 格陵兰, Egypt, 丹麦, brazil, 西班牙/英国/法国, 塞尔维亚和黑山, Canada | Mexico, 阿联酋, 荷蘭 Netherland, 英国, 爱沙尼亚, Russia, 普通话, 荷兰, 阿根廷Argentina, 孟加拉, 立陶宛, Angola, Iran, Slovakia, Greece, 博茨瓦纳, Ukraine, 阿塞拜疆, 以色列Israel, 奥地利 Austria, 馬來西亞, 芬兰 Finland, 印度尼西亚 Indonesia, Italy, Armania, 新西兰 New Zealand, 蘇聯, 英國 UK, 意大利, 阿尔巴尼亚, 玻利维亚, 台湾, 匈牙利 Hungary, Cyprus, Denmark, 东德 Eastgermany, Pakistan, 柬埔寨, 斯洛伐克, 巴勒斯坦被占领区, Australia / USA, 巴勒斯坦, 环球国际唱片股份有限公司, 纳米比亚, 英语, 新西兰, Croatia, Cameroon, Sweden, 亚美尼亚, 中国台湾, Serbia, 俄罗斯, 克罗地亚, Nigeria, Estonia, 澳大利亚 Australia, 瑞典 Sverige, 菲律宾, North Korea, 冰岛, 塞内加尔 Sénégal, Bolivia, argentina, 丹麦 Denmark, 也门, 斯里兰卡 Sri Lanka, 匈牙利, Poland | Czech Republic, 哥伦比亚, 吉尔吉斯斯坦, 美国, 波多黎各, 英国BBC, 香港, 巴布亚新几内亚, English, 马来西亚 Malaysia, 智利, 土耳其, Cambodia, 加拿大, 比利时 Belgium, 印度 india, West Germany, 越南Vietnam, Belgium, 巴基斯坦, Dominican Republic, 阿富汗, 斯里兰卡, 印度, 南斯拉夫, Portugal, 苏联 Soviet Union, Mali, 马里, Tanzania, 塔吉克斯坦, 愛爾蘭, Ecuador, 罗马尼亚, 美国 USA（NBC电视网）, 尼日利亚, 匈牙利  Hungary, Indonesia, Lebanon/France, 乌拉圭, Luxembourg, 塞黑, Chile, 荷兰 the Netherlands, 摩纳哥, 捷克, 委内瑞拉, 摩尔多瓦, 智利 Chile, 波兰, Republic of Macedonia, 菲律宾Philippine, Sierra Leone, 阿鲁巴岛, 捷克/法国/德国, Palestine, 南斯拉夫联邦共和国, 新加坡 Singapore, Federal Republic of Yugoslavia, 保加利亞 Bulgaria, 克羅地亞, 俄罗斯 Russia, 芬蘭 Finland, 德国/奥地利, 危地马拉, 俄国, 原西德, 爱尔兰  Ireland, 尼泊尔, Czech Republic, 埃塞俄比亚, Iran 伊朗, USSSSA, 印度  indian, 菲律宾 Filipino, Romania, 捷克斯洛伐克, New Zealand | Canada, Algeria, 德国/瑞士, 瑞士, 澳大利亚, 前捷克斯洛伐克, 南斯拉夫 Yugoslavia, 牙买加 Jamaica, Syria, 丹麦 Danmark, 泰国, Russian Federation, Colombia, 捷克共和国, Switzerland, 奧地利, 爱尔兰 Ireland, 拉脱维亚, Malaysia, 古巴, Aruba, 法国, 芬兰, Albania, South Africa, 台灣 Taiwan, 民主德国, 黎巴嫩, 泰國, 荷兰Netherlands, East Germany, 波兰语, 阿尔及利亚, Austria, 前苏联, 波蘭, USA, Mauritius, 加拿大 Canada, Argentina, 东德, Poland, 西德, 荷蘭, 阿尔及尼亚, Bulgaria, Georgia, Italy | Brazil | France |, Germany, 北朝鲜, 荷兰 Netherlands, Japan, 尼泊尔nepal, (U.S., 愛沙尼亞Estonia, 巴西 Brazil, Cuba, 波黑, Denmark / China, 日本, 萨米地区 Sápmi, 爱尔兰, 加蓬, 希腊, 孟加拉国, 澳门, Sri Lanka, spain, India, Austria, Germany, Luxembu, 巴基斯坦 Pakistan, 罗马尼亚 Romania, 马来西亚  Malaysia, Senegal, 越南, 巴哈马, 多米尼加共和国, 印度 India, 突尼斯, 南非 South Africa, 中国, 大陆, 喀麦隆, 捷克斯洛伐克 Czechoslovakia, 哈萨克斯坦, 台灣Taiwan, 牙买加, 挪威, 波兰 Poland, 印度 Indian, 瑞典语, 苏联, 越南 Vietnam, 德国, 荷兰 Netherland, 乌克兰, 马来西亚, 韩国, 塞尔维亚, Venezuela, 法國 France, 乌干达, Venezuela | Spain, 伊朗, Serbia and Montenegro, 波兰Poland, 赞比亚, 美國 USA, 保加利亚, Spain, Canada / USA, 波斯尼亚和黑塞哥维那, 尼日尔部落语, Bhutan, Israel, 黑山共和国, 巴西, 斯洛文尼亚, 西班牙, 葡萄牙, 列支敦士登, 澳洲, 巴拿马, 印度尼西亚, 尼日尔, Australia, Netherlands, 尼加拉瓜, 印尼, 威尼斯电影节, Finland, 立陶宛 Lithuania, 俄羅斯, Portugal | Germany, Lithuania, 厄瓜多尔, 白俄罗斯, 希腊 Greece, 阿根廷 Argentina, United Kingdom, 塞爾維亞, 马其顿, 格鲁吉亚, Czechoslovakia, 澳大利亚Australia, 以色列, 南非, 荷兰Neatherland, Singapore, 孟加拉國, 马拉西亚, Soviet Union, 摩洛哥";
		String[] srcArr = srcString.split(",");
		Set<String> gSet = Sets.newHashSet(srcArr);
		Map<String, Set<String>> synonymMap = Maps.newHashMap();
		Pattern cnReg = Pattern.compile("([\u4E00-\u9FA5]+)");
		for (String txt : gSet) {
			if (StringUtils.isBlank(txt)) {
				continue;
			}
			txt = txt.toLowerCase();
			String[] txtArr = txt.split("[/|]");
			if (txtArr.length > 1) {
				continue;
			}
			Matcher matcher = cnReg.matcher(txt);
			if (matcher.find()) {
				String cnName = matcher.group(1);
				String sLeave = matcher.replaceFirst("").trim();
				Set<String> synSet = synonymMap.get(cnName);
				synSet = synSet == null ? synonymMap.get(sLeave) : synSet;
				if (synSet == null) {
					synSet = Sets.newHashSet();
				}
				if (StringUtils.isNotBlank(sLeave)) {
					synSet.add(sLeave);
					synonymMap.put(sLeave, synSet);
				}
				synSet.add(cnName);
				synonymMap.put(cnName, synSet);
			}
		}
		for (Entry<String, Set<String>> entry : synonymMap.entrySet()) {
			System.err.println(ArrayUtils.toString(entry.getValue()));
		}
	}

	@Test
	public void testConvert() {
		String region = "Italy | Brazil | France ";
		Pattern cnReg = Pattern.compile("([\u4E00-\u9FA5]+)");
		region = region.replaceAll("\\.", "");
		Set<String> regionSet = Sets.newHashSet();
		Matcher matcher = cnReg.matcher(region);
		int index = 0;
		if (matcher.find()) {
			String cnName = matcher.group(1);
			index = matcher.end(1);
			cnName = cnName.trim();
			cnName = ChineseHelper.convertToSimplifiedChinese(cnName);
			regionSet.add(cnName);

		}
		if (index < region.length()) {
			region = region.substring(index).toLowerCase();
			String[] txtArr = region.split("[/|]");
			for (String txt : txtArr) {
				if (StringUtils.isBlank(txt)) {
					continue;
				}
				txt = txt.trim();
				txt = ChineseHelper.convertToSimplifiedChinese(txt);
				regionSet.add(txt);
			}
		}
		System.err.println("regionSet:" + ArrayUtils.toString(regionSet));
	}
}
