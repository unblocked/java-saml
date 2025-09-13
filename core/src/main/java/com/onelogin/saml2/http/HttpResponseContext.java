package com.onelogin.saml2.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Framework-agnostic representation of an HTTP response context.
 * This interface provides methods to send responses without depending on any specific HTTP framework.
 * 
 * @since 2.11.0
 */
public interface HttpResponseContext {
    
    /**
     * Sends a temporary redirect response to the client using the specified redirect location URL.
     *
     * @param location the redirect location URL
     * @throws IOException if an I/O error occurs
     */
    void sendRedirect(String location) throws IOException;
    
    /**
     * Sets a response header with the given name and value.
     *
     * @param name the header name
     * @param value the header value
     */
    void setHeader(String name, String value);
    
    /**
     * Sets the status code for this response.
     *
     * @param status the status code
     */
    void setStatus(int status);
    
    /**
     * Sets the content type of the response.
     *
     * @param contentType the content type
     */
    void setContentType(String contentType);
    
    /**
     * Returns a PrintWriter object that can send character text to the client.
     *
     * @return a PrintWriter for writing response content
     * @throws IOException if an I/O error occurs
     */
    PrintWriter getWriter() throws IOException;
    
    /**
     * Returns a ServletOutputStream suitable for writing binary data in the response.
     *
     * @return an OutputStream for writing binary response content
     * @throws IOException if an I/O error occurs
     */
    OutputStream getOutputStream() throws IOException;
    
    /**
     * Writes text content to the response.
     *
     * @param content the text content to write
     * @throws IOException if an I/O error occurs
     */
    void writeText(String content) throws IOException;
    
    /**
     * Writes binary content to the response.
     *
     * @param content the binary content to write
     * @throws IOException if an I/O error occurs
     */
    void writeBytes(byte[] content) throws IOException;
}
