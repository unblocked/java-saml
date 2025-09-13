package com.onelogin.saml2.http;

import java.util.Map;

/**
 * Mock implementation of HttpContext for testing purposes.
 * This combines MockHttpRequestContext and MockHttpResponseContext for convenient testing.
 * 
 * @since 2.11.0
 */
public class MockHttpContext implements HttpContext {
    
    private final MockHttpRequestContext requestContext;
    private final MockHttpResponseContext responseContext;
    
    /**
     * Creates a new MockHttpContext with default request and response contexts.
     */
    public MockHttpContext() {
        this.requestContext = new MockHttpRequestContext();
        this.responseContext = new MockHttpResponseContext();
    }
    
    /**
     * Creates a new MockHttpContext with the specified URL and parameters.
     *
     * @param requestURL the request URL
     * @param parameters the request parameters
     */
    public MockHttpContext(String requestURL, Map<String, String> parameters) {
        this.requestContext = new MockHttpRequestContext(requestURL, parameters);
        this.responseContext = new MockHttpResponseContext();
    }
    
    /**
     * Creates a new MockHttpContext with the specified request and response contexts.
     *
     * @param requestContext the request context
     * @param responseContext the response context
     */
    public MockHttpContext(MockHttpRequestContext requestContext, MockHttpResponseContext responseContext) {
        this.requestContext = requestContext;
        this.responseContext = responseContext;
    }
    
    @Override
    public HttpRequestContext getRequest() {
        return requestContext;
    }
    
    @Override
    public HttpResponseContext getResponse() {
        return responseContext;
    }
    
    // Convenience methods for testing
    
    /**
     * Gets the mock request context for setting up test data.
     *
     * @return the mock request context
     */
    public MockHttpRequestContext getMockRequest() {
        return requestContext;
    }
    
    /**
     * Gets the mock response context for making test assertions.
     *
     * @return the mock response context
     */
    public MockHttpResponseContext getMockResponse() {
        return responseContext;
    }
}
