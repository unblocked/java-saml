package com.onelogin.saml2.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

import com.onelogin.saml2.http.HttpResponseContext;

/**
 * Implementation of HttpResponseContext that wraps an HttpServletResponse.
 * This provides framework-agnostic access to servlet response functionality.
 * 
 * @since 2.11.0
 */
public class ServletHttpResponseContext implements HttpResponseContext {
    
    private final HttpServletResponse response;
    
    /**
     * Creates a new ServletHttpResponseContext wrapping the given HttpServletResponse.
     *
     * @param response the HttpServletResponse to wrap (may be null for read-only operations)
     */
    public ServletHttpResponseContext(HttpServletResponse response) {
        this.response = response;
    }
    
    @Override
    public void sendRedirect(String location) throws IOException {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot send redirect");
        }
        response.sendRedirect(location);
    }
    
    @Override
    public void setHeader(String name, String value) {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot set header");
        }
        response.setHeader(name, value);
    }
    
    @Override
    public void setStatus(int status) {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot set status");
        }
        response.setStatus(status);
    }
    
    @Override
    public void setContentType(String contentType) {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot set content type");
        }
        response.setContentType(contentType);
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot get writer");
        }
        return response.getWriter();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot get output stream");
        }
        return response.getOutputStream();
    }
    
    @Override
    public void writeText(String content) throws IOException {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot write text");
        }
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
    }
    
    @Override
    public void writeBytes(byte[] content) throws IOException {
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse is null - cannot write bytes");
        }
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(content);
        outputStream.flush();
    }
}
