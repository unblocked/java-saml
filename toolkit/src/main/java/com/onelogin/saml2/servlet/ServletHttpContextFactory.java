package com.onelogin.saml2.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.onelogin.saml2.http.HttpContext;
import com.onelogin.saml2.http.HttpContextFactory;
import com.onelogin.saml2.http.SimpleHttpContext;

/**
 * Factory for creating HttpContext from Jakarta Servlet objects.
 * This maintains backward compatibility with existing servlet-based code.
 * 
 * @since 2.11.0
 */
public class ServletHttpContextFactory implements HttpContextFactory {

    /**
     * Creates an HttpContext from HttpServletRequest and HttpServletResponse objects.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object (may be null)
     * @return an HttpContext wrapping the servlet objects
     * @throws IllegalArgumentException if request is not an HttpServletRequest
     */
    @Override
    public HttpContext createContext(Object request, Object response) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("Request must be an HttpServletRequest, got: " + 
                (request != null ? request.getClass().getName() : "null"));
        }
        
        if (response != null && !(response instanceof HttpServletResponse)) {
            throw new IllegalArgumentException("Response must be an HttpServletResponse, got: " + 
                response.getClass().getName());
        }
        
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        
        return new SimpleHttpContext(
            new ServletHttpRequestContext(servletRequest),
            new ServletHttpResponseContext(servletResponse)
        );
    }
}
