package com.onelogin.saml2.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import com.onelogin.saml2.http.HttpContext;
import com.onelogin.saml2.http.HttpContextFactory;

/**
 * Factory for creating HttpContext instances from Jetty 12.1 Request and Response objects.
 * This factory supports both the standard Jetty 12.1 API (Request/Response) and the
 * internal Channel API (ChannelRequest/ChannelResponse) for maximum compatibility.
 *
 * @since 2.11.0
 */
public class JettyHttpContextFactory implements HttpContextFactory {
    
    /**
     * Creates a new JettyHttpContextFactory.
     */
    public JettyHttpContextFactory() {
        // Default constructor
    }
    
    @Override
    public HttpContext createContext(Object request, Object response) {
        // Try standard Jetty Request/Response first
        if (request instanceof Request && response instanceof Response) {
            return new JettyHttpContext((Request) request, (Response) response);
        }

        throw new IllegalArgumentException(
            "JettyHttpContextFactory requires Jetty Request and Response objects. " +
            "Got request: " + (request != null ? request.getClass().getName() : "null") +
            ", response: " + (response != null ? response.getClass().getName() : "null")
        );
    }
    
    /**
     * Creates an HttpContext from Jetty Request and Response objects.
     * This is a convenience method with proper type safety.
     *
     * @param request the Jetty Request
     * @param response the Jetty Response
     * @return a new HttpContext wrapping the Jetty objects
     * @throws IllegalArgumentException if request or response are null
     */
    public HttpContext create(Request request, Response response) {
        return new JettyHttpContext(request, response);
    }
}
