package com.onelogin.saml2.http;

import java.util.List;
import java.util.Map;

/**
 * Framework-agnostic representation of an HTTP request context.
 * This interface provides access to request data without depending on any specific HTTP framework.
 * 
 * @since 2.11.0
 */
public interface HttpRequestContext {
    
    /**
     * Returns the URL the client used to make the request. 
     * Includes protocol, server name, port number, and server path, but not query string parameters.
     *
     * @return the request URL
     */
    String getRequestURL();
    
    /**
     * Returns the first value of the specified request parameter, or null if the parameter does not exist.
     *
     * @param name the parameter name
     * @return the first parameter value, or null if not found
     */
    String getParameter(String name);
    
    /**
     * Returns all values of the specified request parameter.
     *
     * @param name the parameter name
     * @return a list containing all values for the parameter, empty list if not found
     */
    List<String> getParameters(String name);
    
    /**
     * Returns a map of all request parameters.
     *
     * @return a map where keys are parameter names and values are lists of parameter values
     */
    Map<String, List<String>> getAllParameters();
    
    /**
     * Returns the value of the specified request header, or null if the header does not exist.
     *
     * @param name the header name (case-insensitive)
     * @return the header value, or null if not found
     */
    String getHeader(String name);
    
    /**
     * Returns a map of all request headers.
     *
     * @return a map where keys are header names and values are header values
     */
    Map<String, String> getAllHeaders();
    
    /**
     * Returns the HTTP method of the request (GET, POST, etc.).
     *
     * @return the HTTP method
     */
    String getMethod();
    
    /**
     * Returns the query string that is contained in the request URL after the path.
     *
     * @return the query string, or null if none exists
     */
    String getQueryString();
    
    /**
     * Returns an URL-encoded parameter value, preserving the original encoding when possible.
     * This is useful for SAML parameters that need to maintain their exact encoding.
     *
     * @param name the parameter name
     * @return the URL-encoded parameter value, or null if not found
     */
    String getEncodedParameter(String name);
    
    /**
     * Returns an URL-encoded parameter value, or the encoded default value if the parameter doesn't exist.
     *
     * @param name the parameter name
     * @param defaultValue the default value to return if parameter is not found
     * @return the URL-encoded parameter value, or encoded default value
     */
    String getEncodedParameter(String name, String defaultValue);
}
