package com.onelogin.saml2.jetty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Fields;
import org.junit.Test;

import com.onelogin.saml2.http.HttpRequestContext;

/**
 * Test class for JettyRequestContext.
 */
public class JettyRequestContextTest {

    @Test
    public void testGetRequestURL() {
        // Given
        Request mockRequest = mock(Request.class);
        HttpURI mockHttpURI = mock(HttpURI.class);
        when(mockRequest.getHttpURI()).thenReturn(mockHttpURI);
        when(mockHttpURI.toString()).thenReturn("http://localhost:8080/test");

        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When
        String requestURL = context.getRequestURL();

        // Then
        assertEquals("Request URL should match", "http://localhost:8080/test", requestURL);
    }
    
    @Test
    public void testGetParameter() {
        // Given
        Request mockRequest = mock(Request.class);
        Fields mockFields = mock(Fields.class);
        when(mockFields.getValue("param1")).thenReturn("value1");
        when(mockFields.getValue("param2")).thenReturn(null);

        // Mock the static method Request.getParameters
        try (var mockedStatic = mockStatic(Request.class)) {
            mockedStatic.when(() -> Request.getParameters(mockRequest)).thenReturn(mockFields);

            JettyRequestContext context = new JettyRequestContext(mockRequest);

            // When & Then
            assertEquals("Parameter should match", "value1", context.getParameter("param1"));
            assertNull("Non-existent parameter should be null", context.getParameter("param2"));
        }
    }
    
    @Test
    public void testGetParameters() {
        // Given
        Request mockRequest = mock(Request.class);
        Fields mockFields = mock(Fields.class);
        when(mockFields.getValues("param1")).thenReturn(Arrays.asList("value1", "value2"));
        when(mockFields.getValues("param2")).thenReturn(null);

        // Mock the static method Request.getParameters
        try (var mockedStatic = mockStatic(Request.class)) {
            mockedStatic.when(() -> Request.getParameters(mockRequest)).thenReturn(mockFields);

            JettyRequestContext context = new JettyRequestContext(mockRequest);

            // When
            List<String> param1Values = context.getParameters("param1");
            List<String> param2Values = context.getParameters("param2");

            // Then
            assertEquals("Should have 2 values", 2, param1Values.size());
            assertTrue("Should contain value1", param1Values.contains("value1"));
            assertTrue("Should contain value2", param1Values.contains("value2"));
            assertTrue("Non-existent parameter should return empty list", param2Values.isEmpty());
        }
    }
    
    @Test
    public void testGetAllParameters() {
        // Given
        Request mockRequest = mock(Request.class);
        Fields mockFields = mock(Fields.class);

        // Create mock Fields.Field objects
        Fields.Field field1 = mock(Fields.Field.class);
        when(field1.getName()).thenReturn("param1");
        when(field1.getValues()).thenReturn(Arrays.asList("value1", "value2"));

        Fields.Field field2 = mock(Fields.Field.class);
        when(field2.getName()).thenReturn("param2");
        when(field2.getValues()).thenReturn(Arrays.asList("value3"));

        when(mockFields.iterator()).thenReturn(Arrays.asList(field1, field2).iterator());

        // Mock the static method Request.getParameters
        try (var mockedStatic = mockStatic(Request.class)) {
            mockedStatic.when(() -> Request.getParameters(mockRequest)).thenReturn(mockFields);

            JettyRequestContext context = new JettyRequestContext(mockRequest);

            // When
            Map<String, List<String>> allParameters = context.getAllParameters();

            // Then
            assertEquals("Should have 2 parameters", 2, allParameters.size());
            assertEquals("param1 should have 2 values", 2, allParameters.get("param1").size());
            assertEquals("param2 should have 1 value", 1, allParameters.get("param2").size());
            assertTrue("param1 should contain value1", allParameters.get("param1").contains("value1"));
            assertTrue("param2 should contain value3", allParameters.get("param2").contains("value3"));
        }
    }
    
    @Test
    public void testGetHeader() {
        // Given
        Request mockRequest = mock(Request.class);
        HttpFields mockHeaders = mock(HttpFields.class);
        when(mockRequest.getHeaders()).thenReturn(mockHeaders);
        when(mockHeaders.get("Content-Type")).thenReturn("application/json");
        when(mockHeaders.get("Authorization")).thenReturn(null);

        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When & Then
        assertEquals("Header should match", "application/json", context.getHeader("Content-Type"));
        assertNull("Non-existent header should be null", context.getHeader("Authorization"));
    }
    
    @Test
    public void testGetAllHeaders() {
        // Given
        Request mockRequest = mock(Request.class);
        HttpFields mockHeaders = mock(HttpFields.class);
        when(mockRequest.getHeaders()).thenReturn(mockHeaders);

        // Create mock HttpField objects
        HttpField field1 = mock(HttpField.class);
        when(field1.getName()).thenReturn("Content-Type");
        when(field1.getValue()).thenReturn("application/json");

        HttpField field2 = mock(HttpField.class);
        when(field2.getName()).thenReturn("Authorization");
        when(field2.getValue()).thenReturn("Bearer token123");

        when(mockHeaders.iterator()).thenReturn(Arrays.asList(field1, field2).iterator());

        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When
        Map<String, String> allHeaders = context.getAllHeaders();

        // Then
        assertEquals("Should have 2 headers", 2, allHeaders.size());
        assertEquals("Content-Type should match", "application/json", allHeaders.get("Content-Type"));
        assertEquals("Authorization should match", "Bearer token123", allHeaders.get("Authorization"));
    }
    
    @Test
    public void testGetMethod() {
        // Given
        Request mockRequest = mock(Request.class);
        when(mockRequest.getMethod()).thenReturn("POST");

        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When
        String method = context.getMethod();

        // Then
        assertEquals("Method should match", "POST", method);
    }

    @Test
    public void testGetQueryString() {
        // Given
        Request mockRequest = mock(Request.class);
        HttpURI mockHttpURI = mock(HttpURI.class);
        when(mockRequest.getHttpURI()).thenReturn(mockHttpURI);
        when(mockHttpURI.getQuery()).thenReturn("param1=value1&param2=value2");

        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When
        String queryString = context.getQueryString();

        // Then
        assertEquals("Query string should match", "param1=value1&param2=value2", queryString);
    }
    
    @Test
    public void testGetEncodedParameter() {
        // Given
        Request mockRequest = mock(Request.class);
        Fields mockFields = mock(Fields.class);
        when(mockFields.getValue("param1")).thenReturn("value1");
        when(mockFields.getValue("nonexistent")).thenReturn(null);

        // Mock the static method Request.getParameters
        try (var mockedStatic = mockStatic(Request.class)) {
            mockedStatic.when(() -> Request.getParameters(mockRequest)).thenReturn(mockFields);

            JettyRequestContext context = new JettyRequestContext(mockRequest);

            // When & Then
            assertEquals("Encoded parameter should match", "value1", context.getEncodedParameter("param1"));
            assertEquals("Encoded parameter with default should match", "value1", context.getEncodedParameter("param1", "default"));
            assertEquals("Non-existent parameter should return default", "default", context.getEncodedParameter("nonexistent", "default"));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRequest() {
        // When
        new JettyRequestContext(null);
        
        // Then - exception expected
    }
    
    @Test
    public void testGetJettyRequest() {
        // Given
        Request mockRequest = mock(Request.class);
        JettyRequestContext context = new JettyRequestContext(mockRequest);

        // When
        Request request = context.getJettyRequest();

        // Then
        assertSame("Should return the same request instance", mockRequest, request);
    }
}
