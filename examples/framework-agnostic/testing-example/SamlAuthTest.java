package com.onelogin.saml2.examples.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.http.MockHttpContext;
import com.onelogin.saml2.http.MockHttpContextFactory;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

/**
 * Example showing how to test SAML functionality using the framework-agnostic API.
 * 
 * This demonstrates:
 * 1. Testing SAML login initiation
 * 2. Testing SAML response processing
 * 3. Testing logout functionality
 * 4. Using mock contexts for isolated testing
 */
public class SamlAuthTest {
    
    private Saml2Settings settings;
    private MockHttpContextFactory factory;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Load test SAML settings
        settings = new SettingsBuilder().fromFile("test-saml.properties").build();
        factory = new MockHttpContextFactory();
    }
    
    @Test
    public void testSamlLoginInitiation() throws Exception {
        // Create mock HTTP context
        MockHttpContext context = new MockHttpContext("http://localhost:8080/saml/login", new HashMap<>());
        
        // Create Auth instance with mock context
        Auth auth = new Auth(settings, context);
        
        // Initiate SAML login
        auth.login("/dashboard");
        
        // Verify redirect was sent
        assertTrue(context.getMockResponse().wasRedirectSent(), "Should have sent a redirect");
        
        String redirectUrl = context.getMockResponse().getRedirectLocation();
        assertNotNull(redirectUrl, "Redirect URL should not be null");
        assertTrue(redirectUrl.contains("SAMLRequest="), "Redirect URL should contain SAMLRequest parameter");
        assertTrue(redirectUrl.contains("RelayState="), "Redirect URL should contain RelayState parameter");
        
        // Verify the redirect goes to the IdP
        assertTrue(redirectUrl.startsWith(settings.getIdpSingleSignOnServiceUrl()), 
                   "Redirect should go to IdP SSO URL");
    }
    
    @Test
    public void testSamlLoginWithCustomReturnUrl() throws Exception {
        MockHttpContext context = new MockHttpContext("http://localhost:8080/saml/login", new HashMap<>());
        Auth auth = new Auth(settings, context);
        
        String customReturnUrl = "/custom/dashboard";
        auth.login(customReturnUrl);
        
        assertTrue(context.getMockResponse().wasRedirectSent());
        String redirectUrl = context.getMockResponse().getRedirectLocation();
        
        // The return URL should be encoded in the RelayState parameter
        assertTrue(redirectUrl.contains("RelayState="), "Should contain RelayState with return URL");
    }
    
    @Test
    public void testSamlResponseProcessing() throws Exception {
        // Create mock context with SAML response parameter
        Map<String, String> parameters = new HashMap<>();
        parameters.put("SAMLResponse", createMockSamlResponse());
        parameters.put("RelayState", "/dashboard");
        
        MockHttpContext context = new MockHttpContext("http://localhost:8080/saml/acs", parameters);
        context.getMockRequest().setMethod("POST");
        
        Auth auth = new Auth(settings, context);
        
        // Process the SAML response
        auth.processResponse();
        
        // In a real test, you would use a valid SAML response
        // For this example, we're just testing the flow
        // The actual authentication result depends on the response validity
        
        // Verify that processing completed without exceptions
        assertNotNull(auth.getErrors(), "Errors list should be initialized");
    }
    
    @Test
    public void testSamlLogoutInitiation() throws Exception {
        MockHttpContext context = new MockHttpContext("http://localhost:8080/saml/logout", new HashMap<>());
        Auth auth = new Auth(settings, context);
        
        // Initiate SAML logout
        auth.logout("/");
        
        // Verify redirect was sent
        assertTrue(context.getMockResponse().wasRedirectSent(), "Should have sent a redirect");
        
        String redirectUrl = context.getMockResponse().getRedirectLocation();
        assertNotNull(redirectUrl, "Redirect URL should not be null");
        assertTrue(redirectUrl.contains("SAMLRequest="), "Redirect URL should contain SAMLRequest parameter");
        
        // Verify the redirect goes to the IdP SLO endpoint
        assertTrue(redirectUrl.startsWith(settings.getIdpSingleLogoutServiceUrl()), 
                   "Redirect should go to IdP SLO URL");
    }
    
    @Test
    public void testFactoryBasedContextCreation() throws Exception {
        // Test creating context via factory
        Map<String, String> parameters = new HashMap<>();
        parameters.put("test", "value");
        
        MockHttpContext context = (MockHttpContext) factory.createContext(parameters, null);
        
        assertEquals("value", context.getRequest().getParameter("test"));
        assertNotNull(context.getResponse());
    }
    
    @Test
    public void testMockContextManipulation() throws Exception {
        MockHttpContext context = new MockHttpContext();
        
        // Test request manipulation
        context.getMockRequest().setRequestURL("http://example.com/test");
        context.getMockRequest().setParameter("param1", "value1");
        context.getMockRequest().setHeader("Authorization", "Bearer token");
        context.getMockRequest().setMethod("POST");
        
        assertEquals("http://example.com/test", context.getRequest().getRequestURL());
        assertEquals("value1", context.getRequest().getParameter("param1"));
        assertEquals("Bearer token", context.getRequest().getHeader("Authorization"));
        assertEquals("POST", context.getRequest().getMethod());
        
        // Test response verification
        context.getResponse().sendRedirect("http://example.com/redirect");
        context.getResponse().setHeader("Custom-Header", "custom-value");
        context.getResponse().setStatus(302);
        
        assertTrue(context.getMockResponse().wasRedirectSent());
        assertEquals("http://example.com/redirect", context.getMockResponse().getRedirectLocation());
        assertEquals("custom-value", context.getMockResponse().getHeaders().get("Custom-Header"));
        assertEquals(302, context.getMockResponse().getStatus());
    }
    
    @Test
    public void testErrorHandling() throws Exception {
        // Test with invalid settings
        MockHttpContext context = new MockHttpContext();
        
        try {
            // This should throw an exception due to invalid settings
            Saml2Settings invalidSettings = new SettingsBuilder().build();
            Auth auth = new Auth(invalidSettings, context);
            fail("Should have thrown SettingsException");
        } catch (Exception e) {
            // Expected - invalid settings should cause an exception
            assertTrue(e.getMessage().contains("Invalid settings") || 
                      e.getMessage().contains("settings"));
        }
    }
    
    /**
     * Creates a mock SAML response for testing.
     * In a real test, you would use a properly signed and valid SAML response.
     */
    private String createMockSamlResponse() {
        // This is a simplified mock response for testing purposes
        // In real tests, you would use a valid, properly signed SAML response
        String mockResponse = "<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\">" +
                             "<samlp:Status><samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></samlp:Status>" +
                             "</samlp:Response>";
        
        // Base64 encode the response (as it would be in a real SAML response)
        return java.util.Base64.getEncoder().encodeToString(mockResponse.getBytes());
    }
}
