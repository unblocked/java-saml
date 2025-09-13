package com.onelogin.saml2.http;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

/**
 * Test class for HttpContext interface and implementations.
 */
public class HttpContextTest {
    
    @Test
    public void testHttpContextInterface() {
        // Given
        HttpRequestContext mockRequest = mock(HttpRequestContext.class);
        HttpResponseContext mockResponse = mock(HttpResponseContext.class);
        
        HttpContext context = new HttpContext() {
            @Override
            public HttpRequestContext getRequest() {
                return mockRequest;
            }
            
            @Override
            public HttpResponseContext getResponse() {
                return mockResponse;
            }
        };
        
        // When & Then
        assertNotNull("Request should not be null", context.getRequest());
        assertNotNull("Response should not be null", context.getResponse());
        assertSame("Should return the same request instance", mockRequest, context.getRequest());
        assertSame("Should return the same response instance", mockResponse, context.getResponse());
    }
    
    @Test
    public void testHttpContextFactoryInterface() {
        // Given
        HttpContextFactory factory = new HttpContextFactory() {
            @Override
            public HttpContext createContext(Object request, Object response) {
                if (request == null || response == null) {
                    throw new IllegalArgumentException("Request and response cannot be null");
                }
                return mock(HttpContext.class);
            }
        };
        
        // When
        HttpContext context = factory.createContext("mockRequest", "mockResponse");
        
        // Then
        assertNotNull("Context should not be null", context);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHttpContextFactoryWithNullRequest() {
        // Given
        HttpContextFactory factory = new HttpContextFactory() {
            @Override
            public HttpContext createContext(Object request, Object response) {
                if (request == null || response == null) {
                    throw new IllegalArgumentException("Request and response cannot be null");
                }
                return mock(HttpContext.class);
            }
        };
        
        // When
        factory.createContext(null, "mockResponse");
        
        // Then - exception expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHttpContextFactoryWithNullResponse() {
        // Given
        HttpContextFactory factory = new HttpContextFactory() {
            @Override
            public HttpContext createContext(Object request, Object response) {
                if (request == null || response == null) {
                    throw new IllegalArgumentException("Request and response cannot be null");
                }
                return mock(HttpContext.class);
            }
        };
        
        // When
        factory.createContext("mockRequest", null);
        
        // Then - exception expected
    }
}
