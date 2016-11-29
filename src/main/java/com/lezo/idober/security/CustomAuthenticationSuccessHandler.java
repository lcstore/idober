package com.lezo.idober.security;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.google.common.collect.Sets;

/**
 * SimpleUrlAuthenticationSuccessHandler,Descript.
 *
 * @author lilinchong
 * @since 2016年11月29日
 */
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException,
            ServletException {
        Set<String> roleSet = Sets.newHashSet();
        if (authentication.getAuthorities() != null) {
            for (GrantedAuthority author : authentication.getAuthorities()) {
                roleSet.add(author.getAuthority());
            }
        }
        if (request.getPathInfo().startsWith("/movie/edit/") && !roleSet.contains("ROLE_ADMIN")) {
            String targetUrl = "/";
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to "
                        + targetUrl);
                return;
            }
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            super.handle(request, response, authentication);
        }
    }
}
