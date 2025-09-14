package com.onelogin.saml2.jetty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Fields;

import com.onelogin.saml2.http.HttpRequestContext;

/**
 * Implementation of HttpRequestContext that wraps a Jetty 12.1 Request.
 * This provides framework-agnostic access to Jetty request data.
 * 
 * @since 2.11.0
 */
public class JettyRequestContext implements HttpRequestContext {
    
    private final Request request;
    
    /**
     * Creates a new JettyRequestContext wrapping the given Request.
     *
     * @param request the Jetty Request to wrap
     * @throws IllegalArgumentException if request is null
     */
    public JettyRequestContext(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }
    
    @Override
    public String getRequestURL() {
        return request.getHttpURI().toString();
    }
    
    @Override
    public String getParameter(String name) {
        try {
            Fields parameters = Request.getParameters(request);
            return parameters.getValue(name);
        } catch (Exception e) {
            // If parameter parsing fails, return null
            return null;
        }
    }
    
    @Override
    public List<String> getParameters(String name) {
        try {
            Fields parameters = Request.getParameters(request);
            List<String> values = parameters.getValues(name);
            return values != null ? values : Collections.emptyList();
        } catch (Exception e) {
            // If parameter parsing fails, return empty list
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, List<String>> getAllParameters() {
        try {
            Fields parameters = Request.getParameters(request);
            Map<String, List<String>> result = new HashMap<>();
            
            for (Fields.Field field : parameters) {
                String name = field.getName();
                List<String> values = field.getValues();
                result.put(name, values != null ? values : Collections.emptyList());
            }
            
            return result;
        } catch (Exception e) {
            // If parameter parsing fails, return empty map
            return Collections.emptyMap();
        }
    }
    
    @Override
    public String getHeader(String name) {
        HttpFields headers = request.getHeaders();
        return headers.get(name);
    }
    
    @Override
    public Map<String, String> getAllHeaders() {
        Map<String, String> result = new HashMap<>();
        HttpFields headers = request.getHeaders();
        
        for (var field : headers) {
            result.put(field.getName(), field.getValue());
        }
        
        return result;
    }
    
    @Override
    public String getMethod() {
        return request.getMethod();
    }
    
    @Override
    public String getQueryString() {
        return request.getHttpURI().getQuery();
    }
    
    @Override
    public String getEncodedParameter(String name) {
        // Jetty handles parameter decoding automatically
        // Return the parameter as-is since we can't get the original encoded form
        return getParameter(name);
    }
    
    @Override
    public String getEncodedParameter(String name, String defaultValue) {
        String value = getEncodedParameter(name);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Gets the underlying Jetty Request.
     * This can be useful for Jetty-specific operations.
     *
     * @return the Jetty Request
     */
    public Request getJettyRequest() {
        return request;
    }
}
