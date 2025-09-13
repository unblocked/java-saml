package com.onelogin.saml2.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import com.onelogin.saml2.http.HttpResponseContext;

/**
 * Implementation of HttpResponseContext that wraps a Jetty 12 Response.
 * This provides framework-agnostic access to Jetty response functionality.
 * 
 * Note: This adapter converts Jetty's asynchronous callback-based API to synchronous
 * operations required by the SAML library. This is done using CompletableFuture
 * to wait for async operations to complete.
 * 
 * @since 2.11.0
 */
public class JettyResponseContext implements HttpResponseContext {
    
    private final Response response;
    private final Request request;
    
    /**
     * Creates a new JettyResponseContext wrapping the given Response.
     *
     * @param response the Jetty Response to wrap
     * @throws IllegalArgumentException if response is null
     */
    public JettyResponseContext(Response response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
        this.request = response.getRequest();
    }
    
    @Override
    public void sendRedirect(String location) throws IOException {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            
            Response.sendRedirect(request, response, new Callback() {
                @Override
                public void succeeded() {
                    future.complete(null);
                }
                
                @Override
                public void failed(Throwable x) {
                    future.completeExceptionally(x);
                }
            }, location);
            
            // Wait for the redirect to complete (with timeout)
            future.get(30, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("Failed to send redirect", e);
        }
    }
    
    @Override
    public void setHeader(String name, String value) {
        HttpFields.Mutable headers = response.getHeaders();
        headers.put(name, value);
    }
    
    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }
    
    @Override
    public void setContentType(String contentType) {
        setHeader(HttpHeader.CONTENT_TYPE.asString(), contentType);
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        // Create a buffered output stream from the response
        OutputStream outputStream = getOutputStream();
        return new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        // Use Jetty's buffered output stream which handles the async conversion
        return Response.asBufferedOutputStream(request, response);
    }
    
    @Override
    public void writeText(String content) throws IOException {
        if (content == null) {
            return;
        }
        
        try (PrintWriter writer = getWriter()) {
            writer.write(content);
            writer.flush();
        }
    }
    
    @Override
    public void writeBytes(byte[] content) throws IOException {
        if (content == null || content.length == 0) {
            return;
        }
        
        try (OutputStream outputStream = getOutputStream()) {
            outputStream.write(content);
            outputStream.flush();
        }
    }
    
    /**
     * Gets the underlying Jetty Response.
     * This can be useful for Jetty-specific operations.
     *
     * @return the Jetty Response
     */
    public Response getJettyResponse() {
        return response;
    }
}
