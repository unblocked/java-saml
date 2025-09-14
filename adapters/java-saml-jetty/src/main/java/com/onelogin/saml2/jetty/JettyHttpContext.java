package com.onelogin.saml2.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import com.onelogin.saml2.http.HttpContext;
import com.onelogin.saml2.http.HttpRequestContext;
import com.onelogin.saml2.http.HttpResponseContext;

/**
 * Implementation of HttpContext that wraps Jetty 12.1 Request and Response objects.
 * This provides framework-agnostic access to Jetty's HTTP functionality.
 * 
 * @since 2.11.0
 */
public class JettyHttpContext implements HttpContext {
    
    private final JettyRequestContext requestContext;
    private final JettyResponseContext responseContext;
    
    /**
     * Creates a new JettyHttpContext wrapping the given Jetty Request and Response.
     *
     * @param request the Jetty Request to wrap
     * @param response the Jetty Response to wrap
     * @throws IllegalArgumentException if request or response are null
     */
    public JettyHttpContext(Request request, Response response) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        
        this.requestContext = new JettyRequestContext(request);
        this.responseContext = new JettyResponseContext(response);
    }
    
    @Override
    public HttpRequestContext getRequest() {
        return requestContext;
    }
    
    @Override
    public HttpResponseContext getResponse() {
        return responseContext;
    }
    
    /**
     * Gets the underlying Jetty Request.
     * This can be useful for Jetty-specific operations.
     *
     * @return the Jetty Request
     */
    public Request getJettyRequest() {
        return requestContext.getJettyRequest();
    }
    
    /**
     * Gets the underlying Jetty Response.
     * This can be useful for Jetty-specific operations.
     *
     * @return the Jetty Response
     */
    public Response getJettyResponse() {
        return responseContext.getJettyResponse();
    }
}
