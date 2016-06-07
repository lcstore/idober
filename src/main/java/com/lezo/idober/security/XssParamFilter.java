package com.lezo.idober.security;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 只能处理请求里面的参数，既?p1=v1
 * 
 * <filter>
 *       <filter-name>XssParamFilter</filter-name>
 *       <filter-class>com.lezo.idober.security.XssParamFilter</filter-class>
 *  </filter>
 *   <filter-mapping>
 *       <filter-name>XssParamFilter</filter-name>
 *       <url-pattern>/*</url-pattern>
 *       <dispatcher>REQUEST</dispatcher>
 *   </filter-mapping>
 * @author lezo
 * @since 2016年6月7日
 */
public class XssParamFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }


    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
    }

}
