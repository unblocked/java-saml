package com.onelogin.saml2.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of HttpRequestContext for testing purposes.
 * This allows testing SAML functionality without requiring any specific HTTP framework.
 * 
 * @since 2.11.0
 */
public class MockHttpRequestContext implements HttpRequestContext {
    
    private String requestURL;
    private Map<String, List<String>> parameters;
    private Map<String, String> headers;
    private String method;
    private String queryString;
    
    /**
     * Creates a new MockHttpRequestContext with default values.
     */
    public MockHttpRequestContext() {
        this("http://localhost:8080/test", new HashMap<>());
    }
    
    /**
     * Creates a new MockHttpRequestContext with the specified URL and parameters.
     *
     * @param requestURL the request URL
     * @param parameters the request parameters (values will be converted to lists)
     */
    public MockHttpRequestContext(String requestURL, Map<String, String> parameters) {
        this.requestURL = requestURL;
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
        this.method = "GET";
        this.queryString = null;
        
        // Convert single-value parameters to lists
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            List<String> values = new ArrayList<>();
            values.add(entry.getValue());
            this.parameters.put(entry.getKey(), values);
        }
    }
    
    /**
     * Creates a new MockHttpRequestContext with full control over all properties.
     *
     * @param requestURL the request URL
     * @param parameters the request parameters as lists
     * @param headers the request headers
     * @param method the HTTP method
     * @param queryString the query string
     */
    public MockHttpRequestContext(String requestURL, Map<String, List<String>> parameters, 
                                  Map<String, String> headers, String method, String queryString) {
        this.requestURL = requestURL;
        this.parameters = new HashMap<>(parameters);
        this.headers = new HashMap<>(headers);
        this.method = method;
        this.queryString = queryString;
    }
    
    @Override
    public String getRequestURL() {
        return requestURL;
    }
    
    @Override
    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }
    
    @Override
    public List<String> getParameters(String name) {
        List<String> values = parameters.get(name);
        return values != null ? new ArrayList<>(values) : Collections.emptyList();
    }
    
    @Override
    public Map<String, List<String>> getAllParameters() {
        return new HashMap<>(parameters);
    }
    
    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    @Override
    public Map<String, String> getAllHeaders() {
        return new HashMap<>(headers);
    }
    
    @Override
    public String getMethod() {
        return method;
    }
    
    @Override
    public String getQueryString() {
        return queryString;
    }
    
    @Override
    public String getEncodedParameter(String name) {
        return getParameter(name);
    }
    
    @Override
    public String getEncodedParameter(String name, String defaultValue) {
        String value = getEncodedParameter(name);
        return value != null ? value : defaultValue;
    }
    
    // Setter methods for testing
    
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
    
    public void setParameter(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        parameters.put(name, values);
    }
    
    public void setParameters(String name, List<String> values) {
        parameters.put(name, new ArrayList<>(values));
    }
    
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
