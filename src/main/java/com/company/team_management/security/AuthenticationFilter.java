package com.company.team_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.Writer;

public class AuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = SecurityService.getAuthentication((HttpServletRequest) request);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}
