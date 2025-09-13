package com.onelogin.saml2.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.onelogin.saml2.http.HttpRequestContext;

/**
 * Implementation of HttpRequestContext that wraps an HttpServletRequest.
 * This provides framework-agnostic access to servlet request data.
 * 
 * @since 2.11.0
 */
public class ServletHttpRequestContext implements HttpRequestContext {
    
    private final HttpServletRequest request;
    
    /**
     * Creates a new ServletHttpRequestContext wrapping the given HttpServletRequest.
     *
     * @param request the HttpServletRequest to wrap
     * @throws IllegalArgumentException if request is null
     */
    public ServletHttpRequestContext(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null");
        }
        this.request = request;
    }
    
    @Override
    public String getRequestURL() {
        return request.getRequestURL().toString();
    }
    
    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }
    
    @Override
    public List<String> getParameters(String name) {
        String[] values = request.getParameterValues(name);
        if (values == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String value : values) {
            result.add(value);
        }
        return result;
    }
    
    @Override
    public Map<String, List<String>> getAllParameters() {
        Map<String, List<String>> result = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            List<String> values = new ArrayList<>();
            for (String value : entry.getValue()) {
                values.add(value);
            }
            result.put(entry.getKey(), values);
        }
        return result;
    }
    
    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }
    
    @Override
    public Map<String, String> getAllHeaders() {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            result.put(headerName, headerValue);
        }
        return result;
    }
    
    @Override
    public String getMethod() {
        return request.getMethod();
    }
    
    @Override
    public String getQueryString() {
        return request.getQueryString();
    }
    
    @Override
    public String getEncodedParameter(String name) {
        // For servlet requests, parameters are already decoded
        // We return the parameter as-is since we can't get the original encoded form
        return getParameter(name);
    }
    
    @Override
    public String getEncodedParameter(String name, String defaultValue) {
        String value = getEncodedParameter(name);
        return value != null ? value : defaultValue;
    }
}
