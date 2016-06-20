package com.lezo.idober.solr;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.apache.solr.util.TimeZoneUtils;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.lezo.idober.solr.pojo.ItemSolr;
import com.lezo.idober.solr.pojo.MovieSolr;

@Log4j
public class SolrQueryTest {
    HttpSolrServer server;

    @Before
    public void setup() throws Exception {
        server = new HttpSolrServer("http://www.lezomao.com/core2");
        // server = new HttpSolrServer("http://localhost:8081/core2");
        System.setProperty("solr.solr.home", "/apps/src/istore/solr_home");
        // CoreContainer.Initializer initializer = new CoreContainer.Initializer();
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
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "id:01413794354");
        // solrQuery.setFields("id");
        StringBuilder sb = new StringBuilder();
        solrQuery.setStart(0);
        solrQuery.setRows(500);
        while (true) {
            // QueryRequest request = new QueryRequest(solrQuery);
            // QueryResponse resp = request.process(server);
            QueryResponse response = server.query(solrQuery);
            for (SolrDocument rs : response.getResults()) {
                System.err.println(rs.toString());
                System.err.println(rs.getFieldValue("release"));
                String idString = rs.getFieldValue("id").toString();
                idString = idString.split(";")[1];
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(idString);
            }
            sb.append("\n");
            if (response.getResults().size() < solrQuery.getRows()) {
                break;
            }
            solrQuery.setStart(solrQuery.getStart() + solrQuery.getRows());
        }

        System.err.println(sb);
        File file = new File("./test.txt");
        FileUtils.writeStringToFile(file, sb.toString());
        // System.err.println(JSON.toJSONString(response.getResults()));
        // List<ItemSolr> itemList = response.getBeans(ItemSolr.class);
        // System.err.println(JSON.toJSONString(itemList));

    }

    // @Test
    // public void testSolrDelete() throws Exception {
    // String title = ClientUtils.escapeQueryChars("2015;李恩熙;纯情");
    // String queryStr = "(type:)";
    // String sQuery = ClientUtils.escapeQueryChars("type:xroxy-proxy");
    // System.err.println(sQuery);
    // server.deleteByQuery("*:*");
    // server.commit();
    // server.optimize();
    // }

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
}
