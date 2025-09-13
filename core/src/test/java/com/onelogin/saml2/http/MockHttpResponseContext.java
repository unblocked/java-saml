package com.onelogin.saml2.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of HttpResponseContext for testing purposes.
 * This allows testing SAML functionality without requiring any specific HTTP framework.
 * 
 * @since 2.11.0
 */
public class MockHttpResponseContext implements HttpResponseContext {
    
    private String redirectLocation;
    private Map<String, String> headers;
    private int status;
    private String contentType;
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private ByteArrayOutputStream byteArrayOutputStream;
    private String textContent;
    private byte[] byteContent;
    
    /**
     * Creates a new MockHttpResponseContext.
     */
    public MockHttpResponseContext() {
        this.headers = new HashMap<>();
        this.status = 200;
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(stringWriter);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }
    
    @Override
    public void sendRedirect(String location) throws IOException {
        this.redirectLocation = location;
        this.status = 302; // Set redirect status
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
        this.contentType = contentType;
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
        printWriter.write(content);
        printWriter.flush();
    }
    
    @Override
    public void writeBytes(byte[] content) throws IOException {
        this.byteContent = content.clone();
        byteArrayOutputStream.write(content);
        byteArrayOutputStream.flush();
    }
    
    // Getter methods for testing assertions
    
    /**
     * Gets the redirect location that was set via sendRedirect().
     *
     * @return the redirect location, or null if no redirect was sent
     */
    public String getRedirectLocation() {
        return redirectLocation;
    }
    
    /**
     * Gets all headers that were set.
     *
     * @return a map of header names to values
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
    
    /**
     * Gets the HTTP status code that was set.
     *
     * @return the status code
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Gets the content type that was set.
     *
     * @return the content type, or null if not set
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Gets the text content that was written via writeText() or the PrintWriter.
     *
     * @return the text content
     */
    public String getTextContent() {
        if (textContent != null) {
            return textContent;
        }
        return stringWriter.toString();
    }
    
    /**
     * Gets the binary content that was written via writeBytes() or the OutputStream.
     *
     * @return the binary content
     */
    public byte[] getByteContent() {
        if (byteContent != null) {
            return byteContent.clone();
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    /**
     * Checks if a redirect was sent.
     *
     * @return true if sendRedirect() was called, false otherwise
     */
    public boolean wasRedirectSent() {
        return redirectLocation != null;
    }
    
    /**
     * Checks if any text content was written.
     *
     * @return true if text content was written, false otherwise
     */
    public boolean hasTextContent() {
        return (textContent != null && !textContent.isEmpty()) || 
               (stringWriter.getBuffer().length() > 0);
    }
    
    /**
     * Checks if any binary content was written.
     *
     * @return true if binary content was written, false otherwise
     */
    public boolean hasByteContent() {
        return (byteContent != null && byteContent.length > 0) || 
               (byteArrayOutputStream.size() > 0);
    }
}
