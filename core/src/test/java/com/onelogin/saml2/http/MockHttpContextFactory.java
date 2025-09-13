package com.onelogin.saml2.http;

import java.util.Map;

/**
 * Factory for creating MockHttpContext instances for testing.
 * This provides a convenient way to create test contexts without framework dependencies.
 * 
 * @since 2.11.0
 */
public class MockHttpContextFactory implements HttpContextFactory {

    /**
     * Creates a MockHttpContext from the provided objects.
     * This implementation expects the request to be either:
     * - A String (treated as request URL)
     * - A Map<String, String> (treated as parameters with default URL)
     * - A MockHttpRequestContext
     * 
     * The response parameter is ignored unless it's a MockHttpResponseContext.
     *
     * @param request the request object (String URL, Map parameters, or MockHttpRequestContext)
     * @param response the response object (ignored unless MockHttpResponseContext)
     * @return a MockHttpContext
     */
    @Override
    public HttpContext createContext(Object request, Object response) {
        MockHttpRequestContext requestContext;
        MockHttpResponseContext responseContext;
        
        // Handle request parameter
        if (request instanceof String) {
            requestContext = new MockHttpRequestContext((String) request, Map.of());
        } else if (request instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> params = (Map<String, String>) request;
            requestContext = new MockHttpRequestContext("http://localhost:8080/test", params);
        } else if (request instanceof MockHttpRequestContext) {
            requestContext = (MockHttpRequestContext) request;
        } else {
            throw new IllegalArgumentException("Request must be String, Map<String,String>, or MockHttpRequestContext, got: " + 
                (request != null ? request.getClass().getName() : "null"));
        }
        
        // Handle response parameter
        if (response instanceof MockHttpResponseContext) {
            responseContext = (MockHttpResponseContext) response;
        } else {
            responseContext = new MockHttpResponseContext();
        }
        
        return new MockHttpContext(requestContext, responseContext);
    }
    
    /**
     * Creates a MockHttpContext with default values.
     *
     * @return a MockHttpContext with default request and response
     */
    public static MockHttpContext createDefault() {
        return new MockHttpContext();
    }
    
    /**
     * Creates a MockHttpContext with the specified URL.
     *
     * @param url the request URL
     * @return a MockHttpContext with the specified URL
     */
    public static MockHttpContext createWithUrl(String url) {
        return new MockHttpContext(url, Map.of());
    }
    
    /**
     * Creates a MockHttpContext with the specified parameters.
     *
     * @param parameters the request parameters
     * @return a MockHttpContext with the specified parameters
     */
    public static MockHttpContext createWithParameters(Map<String, String> parameters) {
        return new MockHttpContext("http://localhost:8080/test", parameters);
    }
}
