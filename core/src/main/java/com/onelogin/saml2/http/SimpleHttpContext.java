package com.onelogin.saml2.http;

/**
 * Simple implementation of HttpContext that combines separate request and response contexts.
 * This is useful for frameworks that provide separate request and response objects.
 * 
 * @since 2.11.0
 */
public class SimpleHttpContext implements HttpContext {
    
    private final HttpRequestContext request;
    private final HttpResponseContext response;
    
    /**
     * Creates a new SimpleHttpContext with the given request and response contexts.
     *
     * @param request the request context
     * @param response the response context
     * @throws IllegalArgumentException if request or response is null
     */
    public SimpleHttpContext(HttpRequestContext request, HttpResponseContext response) {
        if (request == null) {
            throw new IllegalArgumentException("Request context cannot be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response context cannot be null");
        }
        this.request = request;
        this.response = response;
    }
    
    @Override
    public HttpRequestContext getRequest() {
        return request;
    }
    
    @Override
    public HttpResponseContext getResponse() {
        return response;
    }
}
