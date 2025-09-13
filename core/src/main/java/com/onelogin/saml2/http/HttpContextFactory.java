package com.onelogin.saml2.http;

/**
 * Factory interface for creating framework-agnostic HTTP contexts from framework-specific objects.
 * Implementations of this interface should handle the conversion from specific HTTP framework
 * request/response objects to the generic HttpContext interface.
 * 
 * @since 2.11.0
 */
public interface HttpContextFactory {
    
    /**
     * Creates an HttpContext from framework-specific request and response objects.
     * The exact types of the request and response parameters depend on the specific
     * HTTP framework being used.
     *
     * @param request the framework-specific request object
     * @param response the framework-specific response object (may be null for some frameworks)
     * @return an HttpContext wrapping the framework-specific objects
     * @throws IllegalArgumentException if the request or response objects are not of the expected type
     */
    HttpContext createContext(Object request, Object response);
    
    /**
     * Creates an HttpContext from a single framework-specific object that contains both
     * request and response functionality (e.g., Ktor's ApplicationCall).
     *
     * @param requestResponse the framework-specific object containing both request and response
     * @return an HttpContext wrapping the framework-specific object
     * @throws IllegalArgumentException if the object is not of the expected type
     */
    default HttpContext createContext(Object requestResponse) {
        return createContext(requestResponse, null);
    }
}
