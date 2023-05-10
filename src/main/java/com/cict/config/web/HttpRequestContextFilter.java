package com.cict.config.web;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets up the {@link RequestContext} with the current request so that objects
 * that don't get passed the {@link HttpServletRequest} still have access to it.
 */
@Order
@Component
public class HttpRequestContextFilter extends OncePerRequestFilter {

    /**
     * Sets the current request into the request context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            RequestContext.set(request);
            filterChain.doFilter(request, response);
        } finally {
            RequestContext.set(null);
        }
    }
}