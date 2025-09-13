package com.onelogin.saml2.http;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for HttpResponseContext interface and implementations.
 */
public class HttpResponseContextTest {
    
    @Test
    public void testHttpResponseContextBasicMethods() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        response.setStatus(200);
        response.setHeader("Content-Type", "application/xml");
        response.setContentType("text/html");
        
        // Then
        assertEquals("Status should be set", 200, response.getStatus());
        assertEquals("Header should be set", "text/html", response.getHeader("Content-Type"));
        assertTrue("Should have Content-Type header", response.getHeaders().containsKey("Content-Type"));
    }
    
    @Test
    public void testHttpResponseContextRedirect() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        response.sendRedirect("http://example.com/redirect");
        
        // Then
        assertEquals("Should have redirect location", "http://example.com/redirect", response.getRedirectLocation());
        assertEquals("Should have redirect status", 302, response.getStatus());
    }
    
    @Test
    public void testHttpResponseContextWriter() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        PrintWriter writer = response.getWriter();
        writer.write("Hello World");
        writer.flush();
        
        // Then
        assertEquals("Writer content should match", "Hello World", response.getWriterContent());
    }
    
    @Test
    public void testHttpResponseContextOutputStream() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        OutputStream outputStream = response.getOutputStream();
        outputStream.write("Hello Bytes".getBytes());
        outputStream.flush();
        
        // Then
        assertEquals("OutputStream content should match", "Hello Bytes", response.getOutputStreamContent());
    }
    
    @Test
    public void testHttpResponseContextWriteText() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        response.writeText("Test Content");
        
        // Then
        assertEquals("Text content should match", "Test Content", response.getTextContent());
    }
    
    @Test
    public void testHttpResponseContextWriteBytes() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        byte[] testBytes = "Test Bytes".getBytes();
        
        // When
        response.writeBytes(testBytes);
        
        // Then
        assertArrayEquals("Byte content should match", testBytes, response.getBytesContent());
    }
    
    @Test
    public void testHttpResponseContextWriteNullText() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        response.writeText(null);
        
        // Then
        assertNull("Text content should be null", response.getTextContent());
    }
    
    @Test
    public void testHttpResponseContextWriteNullBytes() throws IOException {
        // Given
        TestHttpResponseContext response = new TestHttpResponseContext();
        
        // When
        response.writeBytes(null);
        
        // Then
        assertNull("Bytes content should be null", response.getBytesContent());
    }
    
    /**
     * Test implementation of HttpResponseContext for testing purposes.
     */
    private static class TestHttpResponseContext implements HttpResponseContext {
        private int status = 200;
        private Map<String, String> headers = new HashMap<>();
        private String redirectLocation;
        private StringWriter stringWriter = new StringWriter();
        private PrintWriter printWriter = new PrintWriter(stringWriter);
        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        private String textContent;
        private byte[] bytesContent;
        
        @Override
        public void sendRedirect(String location) throws IOException {
            this.redirectLocation = location;
            this.status = 302;
        }
        
        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }
        
        @Override
        public void setStatus(int status) {
            this.status = status;
        }
        
        @Override
        public void setContentType(String contentType) {
            setHeader("Content-Type", contentType);
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            return printWriter;
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            return byteArrayOutputStream;
        }
        
        @Override
        public void writeText(String content) throws IOException {
            this.textContent = content;
        }
        
        @Override
        public void writeBytes(byte[] content) throws IOException {
            this.bytesContent = content;
        }
        
        // Test helper methods
        public int getStatus() {
            return status;
        }
        
        public String getHeader(String name) {
            return headers.get(name);
        }
        
        public Map<String, String> getHeaders() {
            return headers;
        }
        
        public String getRedirectLocation() {
            return redirectLocation;
        }
        
        public String getWriterContent() {
            printWriter.flush();
            return stringWriter.toString();
        }
        
        public String getOutputStreamContent() {
            return byteArrayOutputStream.toString();
        }
        
        public String getTextContent() {
            return textContent;
        }
        
        public byte[] getBytesContent() {
            return bytesContent;
        }
    }
}
