package com.onelogin.saml2.http;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for HttpRequestContext interface and implementations.
 */
public class HttpRequestContextTest {
    
    @Test
    public void testHttpRequestContextBasicMethods() {
        // Given
        HttpRequestContext request = new TestHttpRequestContext();
        
        // When & Then
        assertEquals("Request URL should match", "http://localhost:8080/test", request.getRequestURL());
        assertEquals("Parameter should match", "value1", request.getParameter("param1"));
        assertEquals("Header should match", "application/json", request.getHeader("Content-Type"));
        assertEquals("Method should match", "POST", request.getMethod());
        assertEquals("Query string should match", "param1=value1&param2=value2", request.getQueryString());
    }
    
    @Test
    public void testHttpRequestContextParameterMethods() {
        // Given
        HttpRequestContext request = new TestHttpRequestContext();
        
        // When
        List<String> param1Values = request.getParameters("param1");
        List<String> param2Values = request.getParameters("param2");
        Map<String, List<String>> allParams = request.getAllParameters();
        
        // Then
        assertEquals("Should have 2 values for param1", 2, param1Values.size());
        assertTrue("Should contain value1", param1Values.contains("value1"));
        assertTrue("Should contain value1b", param1Values.contains("value1b"));
        
        assertEquals("Should have 1 value for param2", 1, param2Values.size());
        assertTrue("Should contain value2", param2Values.contains("value2"));
        
        assertEquals("Should have 2 parameters", 2, allParams.size());
        assertTrue("Should contain param1", allParams.containsKey("param1"));
        assertTrue("Should contain param2", allParams.containsKey("param2"));
    }
    
    @Test
    public void testHttpRequestContextHeaderMethods() {
        // Given
        HttpRequestContext request = new TestHttpRequestContext();
        
        // When
        Map<String, String> allHeaders = request.getAllHeaders();
        
        // Then
        assertEquals("Should have 2 headers", 2, allHeaders.size());
        assertEquals("Content-Type should match", "application/json", allHeaders.get("Content-Type"));
        assertEquals("Authorization should match", "Bearer token123", allHeaders.get("Authorization"));
    }
    
    @Test
    public void testHttpRequestContextEncodedParameterMethods() {
        // Given
        HttpRequestContext request = new TestHttpRequestContext();
        
        // When & Then
        assertEquals("Encoded parameter should match", "value1", request.getEncodedParameter("param1"));
        assertEquals("Encoded parameter with default should match", "value1", request.getEncodedParameter("param1", "default"));
        assertEquals("Encoded parameter with default should return default", "default", request.getEncodedParameter("nonexistent", "default"));
    }
    
    /**
     * Test implementation of HttpRequestContext for testing purposes.
     */
    private static class TestHttpRequestContext implements HttpRequestContext {
        
        @Override
        public String getRequestURL() {
            return "http://localhost:8080/test";
        }
        
        @Override
        public String getParameter(String name) {
            if ("param1".equals(name)) return "value1";
            if ("param2".equals(name)) return "value2";
            return null;
        }
        
        @Override
        public List<String> getParameters(String name) {
            if ("param1".equals(name)) return Arrays.asList("value1", "value1b");
            if ("param2".equals(name)) return Arrays.asList("value2");
            return Arrays.asList();
        }
        
        @Override
        public Map<String, List<String>> getAllParameters() {
            Map<String, List<String>> params = new HashMap<>();
            params.put("param1", Arrays.asList("value1", "value1b"));
            params.put("param2", Arrays.asList("value2"));
            return params;
        }
        
        @Override
        public String getHeader(String name) {
            if ("Content-Type".equals(name)) return "application/json";
            if ("Authorization".equals(name)) return "Bearer token123";
            return null;
        }
        
        @Override
        public Map<String, String> getAllHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer token123");
            return headers;
        }
        
        @Override
        public String getMethod() {
            return "POST";
        }
        
        @Override
        public String getQueryString() {
            return "param1=value1&param2=value2";
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
    }
}
