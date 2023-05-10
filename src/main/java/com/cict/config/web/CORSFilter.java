package com.cict.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CORSFilter implements Filter {

    @Value("${application.cors.allowed.origins}")
    private String[] allowedDomains;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        Set<String> allowedOrigins = new HashSet<>(Arrays.asList(allowedDomains));
        String origin = request.getHeader("Origin");
        if (allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "4800");
        response.setHeader("Access-Control-Allow-Headers", "Accept-Charset, Accept-Encoding, Accept-Language, " +
                "Authorization, Origin, X-Requested-With, Content-Type, Accept, X-AUTH-APP, X-AUTH-TOKEN");
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}
}