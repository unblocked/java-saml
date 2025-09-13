package com.onelogin.saml2.http;

/**
 * Framework-agnostic representation of an HTTP request/response context.
 * This interface combines request and response contexts for convenient access.
 * 
 * @since 2.11.0
 */
public interface HttpContext {
    
    /**
     * Returns the HTTP request context.
     *
     * @return the request context
     */
    HttpRequestContext getRequest();
    
    /**
     * Returns the HTTP response context.
     *
     * @return the response context
     */
    HttpResponseContext getResponse();
}
