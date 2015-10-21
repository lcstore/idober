package com.lezo.idober.solr;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.lezo.idober.vo.ItemVo;

public class SolrQueryTest {
    HttpSolrServer server;

    @Before
    public void setup() {
        server = new HttpSolrServer("http://www.lezomao.com/");
    }

    @Test
    public void testQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery("牛奶");
        String queryString =
                "group=true&group.field=itemCode&group.query=stockNum:[1%20TO%20*]&group.main=true&group.sort=commentNum%20desc&group.sort=score%20desc";
        SolrParams params = SolrRequestParsers.parseQueryString(queryString);
        solrQuery.add(params);
        solrQuery.add("group.offset", "0");
        solrQuery.add("group.limit", "10");
        StringBuilder sb = new StringBuilder();
        for (Field fld : ItemVo.class.getDeclaredFields()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(fld.getName());
        }
        solrQuery.addField(sb.toString());
        // QueryRequest request = new QueryRequest(solrQuery);
        // QueryResponse resp = request.process(server);
        QueryResponse response = server.query(solrQuery);
        List<ItemVo> itemList = response.getBeans(ItemVo.class);
        System.err.println(JSON.toJSONString(itemList));
    }
}
