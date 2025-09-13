package com.onelogin.saml2.jetty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.Test;

import com.onelogin.saml2.http.HttpContext;
import com.onelogin.saml2.http.HttpContextFactory;

/**
 * Test class for JettyHttpContextFactory.
 */
public class JettyHttpContextFactoryTest {

    @Test
    public void testFactoryInstantiation() {
        // Given & When
        JettyHttpContextFactory factory = new JettyHttpContextFactory();

        // Then
        assertNotNull("Factory should not be null", factory);
    }

    @Test
    public void testCreateWithJettyRequestResponse() {
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        Request mockRequest = mock(Request.class);
        Response mockResponse = mock(Response.class);

        // When
        HttpContext context = factory.createContext(mockRequest, mockResponse);

        // Then
        assertNotNull("Context should not be null", context);
        assertTrue("Context should be JettyHttpContext", context instanceof JettyHttpContext);
        assertNotNull("Request context should not be null", context.getRequest());
        assertNotNull("Response context should not be null", context.getResponse());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullRequest() {
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        Response mockResponse = mock(Response.class);

        // When
        factory.createContext(null, mockResponse);

        // Then - exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullResponse() {
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        Request mockRequest = mock(Request.class);

        // When
        factory.createContext(mockRequest, null);

        // Then - exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithInvalidObjects() {
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        String invalidRequest = "not a request";
        String invalidResponse = "not a response";

        // When
        factory.createContext(invalidRequest, invalidResponse);

        // Then - exception expected
    }
}
