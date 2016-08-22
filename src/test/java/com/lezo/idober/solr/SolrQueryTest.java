package com.lezo.idober.solr;

import java.io.File;
import java.lang.reflect.Field;
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
		server = new HttpSolrServer("http://www.lezomao.com/cuser");
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
		solrQuery.set("q", "(shares_size:1)");
		solrQuery.setFields("id,torrents,shares,code_s");
		Set<String> lineSet = Sets.newHashSet();
		solrQuery.setStart(0);
		solrQuery.setRows(500);
		Set<String> genreSet = Sets.newHashSet();
		while (true) {
			QueryResponse response = server.query(solrQuery);
			for (SolrDocument rs : response.getResults()) {
				String sName = rs.getFieldValue("id").toString();
				genreSet.add(sName);
			}
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
		String sContent =
				"[035809515, 01354641792, 0123538064, 1180219073, 1125773836, 856043567, 0604343594, 01706915939, 106413716, 0217864686, 01754827331, 207649478, 0636898498, 01274887978, 01745272152, 0214054118, 1153644261, 18575203, 1412772733, 86857484, 0468868751, 355172625, 1918457118, 0123541130, 0374389470, 0848170794, 02045135212, 1896323303, 0677315777, 01657421350, 1179321500, 134472182, 0734394323, 01476126, 01950084729, 02083988338, 195308282, 0123595008, 111270972, 0499010473, 306767683, 01361586945, 0214173425, 01720528904, 0188846666, 47454748, 929198885, 01944044454, 536292036, 02021120967, 0172754143, 1894285940, 017745101, 01040442507, 01612442911, 01040418486, 111300554, 01770708780, 018752128, 0466066568, 0655664836, 01127534916, 01248517961, 109423006, 01361502394, 02131793988, 0441045138, 1683857557, 0238721637, 207619687, 219205508, 01358127214, 01144936648, 0469818341, 0622477725, 01714467021, 1642382328, 0272266149, 774431710, 2017861046, 1539147195, 194835781, 391372229, 66452634, 36626359, 0266721026, 226645787, 01250900672, 1032432404, 0394679988, 01720593071, 0968648440, 536208398, 01931048390, 01705907750, 134327070, 0691331360, 0605497665, 092140863, 1916547611, 01951245694, 0967425988, 269458130, 0177760456, 01791172348, 66428486, 01040441732, 829235454, 226622716, 304897606, 2008789275, 01951365010, 0604374345, 767012762, 01951189147, 72977177, 0409671365, 0216879661, 01525121713, 0394734587, 01602933334, 1876133913, 0873789232, 330899290, 01754854174, 01131109902, 021922510, 1125741433, 0735493675, 1216234187, 726428590, 983537657, 0123621673, 01763323619, 184206337, 783478262, 0238513874, 447692988, 84609736, 01158069572, 01610949681, 452104317, 2092805036, 1790472675, 01713479364, 01266631594, 2081003185, 0438449437, 432095988, 391556836, 1447070258, 1322284645, 0414444657, 1609262188, 1333545073, 207618914, 0269524231, 49425825, 0288656508, 1310915430, 0591188509, 199005500, 01326877637, 1580270074, 01060970493, 01060970494, 192631001, 01303427749, 0122523500, 983510710, 01821884680, 0591076288, 0430917825, 01324885393, 01060943492, 01764270065, 1873907961, 1638304165, 263987295, 0239433798, 017740257, 1573202142, 84369607, 1447093383, 0410564202, 0441219017, 01845313475, 190837778, 0823968828, 01327407015, 1845657743, 190631289, 0465202562, 892305746, 0772049439, 202048025, 02134626595, 0239134498, 1663782429, 280109605, 247977902, 1003920490, 2076918176, 452844483, 190785886, 82699362, 01144844605, 223160019, 01315056881, 01714558438, 221077561, 0259638207, 0436692605, 01040419599, 391408753, 139871297, 0635831505, 305995133, 742245487, 01590623119, 732779562, 01994454207, 0963349360, 355597367, 01595293765, 01033059300, 766922457, 532626064, 0635714540, 01506592894, 0378053584, 301464728, 65345941, 01326761294, 158653410, 1729172188, 1216290047, 746484090, 0438273392, 0660738800, 01705107276, 1216231272, 1755505801, 01159862028, 746486819, 0474036965, 01361618688, 163269959, 0653162243, 01040441677, 1782524207, 01271306521, 01040419384, 86920041, 0328204615, 0499987716, 475424557, 01416082385, 84429313, 0628132969, 02041855710, 202047962, 269488825, 2142701178, 92954762, 0823967714, 1916633943, 01189952703, 1916608000, 0726828363, 766886958, 66539932, 0964388107, 0185866600, 662430370, 170431038, 198945824, 1412894692, 536355488, 1322440385, 1372565249, 536145123, 1874089591, 0347693674, 0607047497, 445698662, 1932025342, 1896441441, 0246647622, 01323926369, 1896286815, 01199339644, 0469847324, 308649445, 0143798952, 194298371, 0408928517, 01161613032, 0212143649, 01525055519, 452902018, 318383738, 1216234249, 0440239691, 0329913681, 0606274085, 01250990140, 0213192490, 01522290698, 0407143972, 01130340077, 01845227846, 0438509888, 0591226016, 1642211293, 0213194353, 02022851726, 01791054369, 0211347153, 01996155955, 0407143876, 304174871, 732809471, 2139070706, 225788538, 247922352, 275908804, 1873907060, 01250992100, 0627305312, 466004248, 0381998154, 01271075851, 0682740436, 01130368715, 2113274269, 0242923817, 1896496348, 225693304, 0243812798, 983657590, 766886092, 01804683950, 72974450, 440481761, 01648707742, 01987163559, 01335004196, 432099830, 1448768353, 222061747, 746694465, 01741067819, 0442201027, 0214287874, 01060967674, 207656361, 01595388773, 0412618818, 0187804012, 536381594, 201280123, 0857902591, 1463508518, 452100284, 86771987, 0562443165, 01624170468, 0329184676, 77251636, 642140745, 0440241522, 0219700210, 01741028772, 746512673, 202050634, 01558191716, 01932693351, 0407911809, 1896413542, 02047028229, 0143672165, 277428348, 536318999, 134472363, 1663568157, 725328913, 106655321, 182757135, 2066791322, 949454874, 0852846025, 01010947562, 746687791, 461564927, 1447093357, 0441225773, 1609288225, 359779652, 0213969517, 109336454, 104896601, 1003859731, 02131794677, 306833058, 1894193771, 02021513293, 0271370472, 225729884, 37928669, 0591193377, 01741028766, 01741002075, 303136026, 0266815331, 1894193748, 1412778503, 1476623863, 1805770912, 01496246543, 01740875907, 66425698, 01364403291, 02075851068, 0889347818, 1342660573, 66425690, 0211786819, 1873882981, 1593455208, 01683701, 918073042, 249710623, 1186825854, 0215190223, 693179589, 1873880097, 0464788961, 01456381513, 42348438, 72887769, 1428617501, 02018460614, 441427136, 2097387678, 01612443773, 01845222202, 109362332, 0674580557, 01734445638, 01740878015, 86894959, 1412749733, 0823853627, 1916581092, 0245659815, 1789968560, 0271487738, 0210453667, 1769714554, 1581252308, 556517234, 0271376176, 197250712, 0243699312, 01951339872, 01271279701, 02041770370, 108192002, 194472275, 01074762355, 0359551883, 1231797379, 01761087010, 01951364954, 0273304779, 01456533415, 1216380259, 0654941210, 01990707236, 2139098394, 86887155, 746483999, 64333275, 01531453455, 01040442476, 210368470, 0441073006, 01074795806, 301407189, 02014704064, 0972013055, 662454233, 0409763432, 0878255580, 1663750558, 78974560, 1447094159, 212272207, 078641160, 929226788, 0243638889, 277277568, 0467975270, 02125334121, 496097527, 529915089, 0635058119, 01951131457, 529915083, 01827526020, 01767138595, 1128531531, 01823261098, 0407047747, 798264723, 01935255380, 0823971583, 0704956201, 01127412904, 1163587288, 1180171992, 0165071980, 01060966498, 278255900, 0681616239, 642203118, 0676458480, 0843338091, 0269523428, 304181662, 536235337, 1755051984, 1391701817, 0222327404, 0908100931, 366856096, 0939777983, 01274944857, 0853589929, 752946237, 766976212, 237087181, 0209166917, 0850785002, 201309915, 0239194114, 108586718, 0907953779, 0464187909, 536171871, 01358104247, 66452416, 02021508582, 136114559, 1154292222, 190691799, 02021508584, 367033043, 0394677082, 01090763720, 0240422691, 0704031527, 01930869700, 1663780286, 01932629028, 02019390111, 66452420, 1896501057, 112137764, 02042506825, 01384133885, 111328573, 01040419600, 83564447, 306802334, 0374445986, 305875901, 01792834084, 359007934, 0225186348, 473942876, 1663571995, 732662473, 2092838735, 746483971, 163270015, 01250811150, 190838737, 01678008366]";
		JSONArray idArray = JSONArray.parseArray(sContent);
		String title = ClientUtils.escapeQueryChars("2015;李恩熙;纯情");
		server.deleteByQuery("*:*");
		server.commit();
		server.optimize();
	}

	@Test
	public void testCommit() throws Exception {
		String rawData = FileUtils.readFileToString(new File("/Users/lezo/Downloads/book.json"), "UTF-8");
		String url = "http://localhost:8081/cuser/update/json?commit=true";
		// url = "http://www.lezomao.com/omovie/update/json?commit=true";
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
