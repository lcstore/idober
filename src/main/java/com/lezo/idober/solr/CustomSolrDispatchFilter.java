package com.lezo.idober.solr;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.solr.core.CoreContainer;

import com.lezo.idober.utils.SolrUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月12日
 */
public class CustomSolrDispatchFilter extends org.apache.solr.servlet.SolrDispatchFilter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config); // 然后调用父类的init
        CoreContainer container = getCores();
        SolrUtils.setCoreContainer(container);
    }
}
