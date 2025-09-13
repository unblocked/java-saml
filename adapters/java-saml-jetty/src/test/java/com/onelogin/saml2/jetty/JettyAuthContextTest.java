package com.onelogin.saml2.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.Test;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.http.HttpContext;
import com.onelogin.saml2.http.HttpContextFactory;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

/**
 * Tests for Auth class using HttpContext and HttpContextFactory patterns with Jetty.
 * This validates that Auth tests properly validate servlet using context or factory
 * instead of direct servlet objects.
 */
public class JettyAuthContextTest {

    /**
     * Tests Auth constructor with HttpContext directly.
     * This validates that Auth can work with framework-agnostic HttpContext.
     */
    @Test
    public void testAuthWithHttpContext() throws IOException, SettingsException, Error {
        // Create mock Jetty objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Create HttpContext using Jetty objects
        JettyHttpContext httpContext = new JettyHttpContext(request, response);
        
        // Load settings
        Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();
        
        // Create Auth with HttpContext
        Auth auth = new Auth(settings, httpContext);
        
        // Verify Auth was created successfully
        assertNotNull(auth);
        assertNotNull(auth.getSettings());
        assertEquals(settings.getIdpEntityId(), auth.getSettings().getIdpEntityId());
        assertEquals(settings.getSpEntityId(), auth.getSettings().getSpEntityId());
    }

    /**
     * Tests Auth constructor with HttpContextFactory and separate request/response objects.
     * This validates that Auth can work with HttpContextFactory pattern for Jetty.
     */
    @Test
    public void testAuthWithHttpContextFactory() throws IOException, SettingsException, Error {
        // Create mock Jetty objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Create HttpContextFactory
        HttpContextFactory factory = new JettyHttpContextFactory();
        
        // Load settings
        Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();
        
        // Create Auth with HttpContextFactory
        Auth auth = new Auth(settings, factory, request, response);
        
        // Verify Auth was created successfully
        assertNotNull(auth);
        assertNotNull(auth.getSettings());
        assertEquals(settings.getIdpEntityId(), auth.getSettings().getIdpEntityId());
        assertEquals(settings.getSpEntityId(), auth.getSettings().getSpEntityId());
    }

    /**
     * Tests that HttpContextFactory properly validates Jetty objects.
     * This ensures the factory rejects non-Jetty objects.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHttpContextFactoryValidation() {
        HttpContextFactory factory = new JettyHttpContextFactory();
        
        // Try to create context with invalid objects
        factory.createContext("not a request", "not a response");
    }

    /**
     * Tests Auth functionality with HttpContext for SAML operations.
     * This validates that Auth can perform SAML operations using HttpContext.
     */
    @Test
    public void testAuthSamlOperationsWithContext() throws IOException, SettingsException, Error {
        // Create mock Jetty objects with some basic setup
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Mock some basic request properties
        when(request.getHttpURI()).thenReturn(org.eclipse.jetty.http.HttpURI.from("http://localhost:8080/saml/login"));
        
        // Create HttpContext using Jetty objects
        JettyHttpContext httpContext = new JettyHttpContext(request, response);
        
        // Load settings
        Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();
        
        // Create Auth with HttpContext
        Auth auth = new Auth(settings, httpContext);
        
        // Verify Auth can access basic properties
        assertNotNull(auth.getSettings());
        assertEquals("http://idp.example.com/simplesaml/saml2/idp/SSOService.php", auth.getSSOurl());
        assertEquals("http://idp.example.com/simplesaml/saml2/idp/SingleLogoutService.php", auth.getSLOurl());
    }

    /**
     * Tests Auth functionality with HttpContextFactory for SAML operations.
     * This validates that Auth can perform SAML operations using HttpContextFactory.
     */
    @Test
    public void testAuthSamlOperationsWithFactory() throws IOException, SettingsException, Error {
        // Create mock Jetty objects with some basic setup
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Mock some basic request properties
        when(request.getHttpURI()).thenReturn(org.eclipse.jetty.http.HttpURI.from("http://localhost:8080/saml/login"));
        
        // Create HttpContextFactory
        HttpContextFactory factory = new JettyHttpContextFactory();
        
        // Load settings
        Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();
        
        // Create Auth with HttpContextFactory
        Auth auth = new Auth(settings, factory, request, response);
        
        // Verify Auth can access basic properties
        assertNotNull(auth.getSettings());
        assertEquals("http://idp.example.com/simplesaml/saml2/idp/SSOService.php", auth.getSSOurl());
        assertEquals("http://idp.example.com/simplesaml/saml2/idp/SingleLogoutService.php", auth.getSLOurl());
    }

    /**
     * Tests that HttpContext properly wraps Jetty request/response functionality.
     * This validates the HttpContext abstraction works correctly with Jetty.
     */
    @Test
    public void testHttpContextWrapsJettyObjects() {
        // Create mock Jetty objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Create HttpContext
        HttpContext httpContext = new JettyHttpContext(request, response);
        
        // Verify HttpContext provides access to request and response contexts
        assertNotNull(httpContext.getRequest());
        assertNotNull(httpContext.getResponse());
        
        // Verify the contexts are the correct Jetty-specific implementations
        assertTrue(httpContext.getRequest() instanceof JettyRequestContext);
        assertTrue(httpContext.getResponse() instanceof JettyResponseContext);
    }

    /**
     * Tests that HttpContextFactory creates proper HttpContext instances.
     * This validates the factory pattern works correctly for Jetty.
     */
    @Test
    public void testHttpContextFactoryCreatesCorrectContext() {
        // Create mock Jetty objects
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        
        // Create HttpContextFactory
        HttpContextFactory factory = new JettyHttpContextFactory();
        
        // Create HttpContext using factory
        HttpContext httpContext = factory.createContext(request, response);
        
        // Verify HttpContext is created correctly
        assertNotNull(httpContext);
        assertNotNull(httpContext.getRequest());
        assertNotNull(httpContext.getResponse());
        
        // Verify the contexts are the correct Jetty-specific implementations
        assertTrue(httpContext.getRequest() instanceof JettyRequestContext);
        assertTrue(httpContext.getResponse() instanceof JettyResponseContext);
    }
}
