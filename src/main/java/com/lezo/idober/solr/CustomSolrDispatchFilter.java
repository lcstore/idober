package com.lezo.idober.solr;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import com.lezo.idober.utils.SolrUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月12日
 */
public class CustomSolrDispatchFilter extends org.apache.solr.servlet.SolrDispatchFilter {
    private EmbeddedSolrServer solrServer;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config); // 然后调用父类的init
        /**
         * solr是作为一个内嵌的服务，并把它保存到servletContext里面，后面取很方便
         */
        this.solrServer = new EmbeddedSolrServer(getCores(), getCores().getDefaultCoreName());
        // config.getServletContext().setAttribute(SolrDispatchFilter.class.getName(), this.solrServer);
        // EmbeddedSolrServerHolder.getInstance().setEmbeddedSolrServer(solrServer);
        SolrUtils.setEmbeddedSolrServer(solrServer);
    }
}
