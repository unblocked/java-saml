package com.onelogin.saml2.jetty;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.onelogin.saml2.http.HttpContextFactory;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

/**
 * Integration test for Jetty adapter with the framework-agnostic SAML library.
 */
public class JettyIntegrationTest {
    
    @Test
    public void testJettyHttpContextFactoryCreation() {
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        
        // When & Then
        assertNotNull("Factory should be created successfully", factory);
    }
    
    @Test
    public void testSaml2SettingsCreation() throws Exception {
        // Given
        Properties props = new Properties();
        props.setProperty("onelogin.saml2.sp.entityid", "http://localhost:8080/metadata");
        props.setProperty("onelogin.saml2.sp.assertion_consumer_service.url", "http://localhost:8080/acs");
        props.setProperty("onelogin.saml2.sp.single_logout_service.url", "http://localhost:8080/sls");
        props.setProperty("onelogin.saml2.idp.entityid", "http://idp.example.com/metadata");
        props.setProperty("onelogin.saml2.idp.single_sign_on_service.url", "http://idp.example.com/sso");
        props.setProperty("onelogin.saml2.idp.single_logout_service.url", "http://idp.example.com/sls");
        
        // When
        Saml2Settings settings = new SettingsBuilder().fromProperties(props).build();
        
        // Then
        assertNotNull("Settings should be created successfully", settings);
        assertEquals("SP Entity ID should match", "http://localhost:8080/metadata", settings.getSpEntityId());
    }
    
    @Test
    public void testFrameworkAgnosticDesign() {
        // This test verifies that the Jetty adapter follows the framework-agnostic design
        // by checking that the factory implements the correct interface
        
        // Given
        JettyHttpContextFactory factory = new JettyHttpContextFactory();
        
        // When & Then
        assertTrue("Factory should implement HttpContextFactory",
                   factory instanceof HttpContextFactory);
    }
}
