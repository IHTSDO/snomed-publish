package com.ihtsdo.snomed.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                                    throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");
        
        LOG.info("IN THE CORS FILTER CHAIN!!!");

        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Max-Age", "1");// 30 min
        }
        filterChain.doFilter(request, response);
    }
}