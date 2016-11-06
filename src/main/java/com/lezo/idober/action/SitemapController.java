package com.lezo.idober.action;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lezo.idober.solr.pojo.MovieSolr;
import com.lezo.idober.utils.SolrUtils;

@RequestMapping("sitemap")
@Controller
@Log4j
public class SitemapController {

	@RequestMapping(value = "create", method = RequestMethod.GET)
	@ResponseBody
	public String createSitemap() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element urlSetEle = doc.createElement("urlset");
		doc.appendChild(urlSetEle);
		urlSetEle.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
		urlSetEle.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		urlSetEle.setAttribute("xsi:schemaLocation", "http://www.sitemaps.org/schemas/sitemap/0.9");
		Element urlEle = doc.createElement("url");
		Element locEle = doc.createElement("loc");
		locEle.setTextContent("http://www.lezomao.com/");
		urlEle.appendChild(locEle);
		Element pEle = doc.createElement("priority");
		pEle.setTextContent("1.0");
		urlEle.appendChild(pEle);
		Element freqEle = doc.createElement("changefreq");
		freqEle.setTextContent("daily");
		urlEle.appendChild(freqEle);
		urlSetEle.appendChild(urlEle);

		addSitemap(urlSetEle, doc);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		File tempFile = new File("./sitemap.xml." + System.currentTimeMillis());
		File destFile = new File("./sitemap.xml");
		StreamResult streamResult = new StreamResult(tempFile);
		transformer.transform(source, streamResult);
		destFile.deleteOnExit();
		tempFile.renameTo(destFile);
		int count = urlSetEle.getChildNodes().getLength();
		return "OK," + count;
	}

	private void addSitemap(Element urlSetEle, Document doc) throws Exception {
		Integer offset = 0;
		Integer limit = 500;
		int total = 2000;
		SolrServer server = SolrUtils.getSolrServer(SolrUtils.CORE_ONLINE_MOVIE);
		while (offset < total) {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setStart(offset);
			solrQuery.setRows(limit);
			solrQuery.set("q", "(torrents_size:[1 TO *] OR shares_size:[1 TO *])");
			solrQuery.addField("id");
			solrQuery.addFilterQuery("type:movie");
			solrQuery.addSort("release", ORDER.desc);
			QueryResponse resp = server.query(solrQuery);
			List<MovieSolr> mSolrs = resp.getBeans(MovieSolr.class);
			log.info("sitemap,offset:" + offset + ",size:" + mSolrs.size());
			if (CollectionUtils.isNotEmpty(mSolrs)) {
				for (MovieSolr solr : mSolrs) {
					String sUrl = "http://www.lezomao.com/movie/detail/" + solr.getId() + ".html";
					addUrl(sUrl, urlSetEle, doc);
				}
				offset += mSolrs.size();
				if (mSolrs.size() < limit) {
					break;
				}
			} else {
				break;
			}
		}

	}

	private void addUrl(String sUrl, Element urlSetEle, Document doc) {
		if (StringUtils.isBlank(sUrl)) {
			return;
		}
		Element urlEle = doc.createElement("url");
		Element locEle = doc.createElement("loc");
		locEle.setTextContent(sUrl);
		urlEle.appendChild(locEle);
		urlSetEle.appendChild(urlEle);
	}
}
