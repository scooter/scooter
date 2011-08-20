/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * ContentHandlerHelper provides helper methods for content handlers. 
 * 
 * The default MIME type for text data is <tt>text/plain</tt>, 
 * while <tt>application/octet-stream</tt> for binary data.
 * 
 * @author (Fei) John Chen
 */
public class ContentHandlerHelper {
	
	/**
	 * Sends content to the requestor. This method delegates to other methods 
	 * when the content is an instance of <tt>String</tt>, <tt>byte[]</tt>, 
	 * <tt>InputStream</tt> respectively. In all other cases, the content is 
	 * sent as a string.
	 * 
	 * @param response  The HTTP response object.
	 * @param content  The content to be sent.
	 * @param mimeType  The content MIME type.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void publish(HttpServletResponse response, Object content, String mimeType) 
    throws IOException, ServletException {
		if (content == null) {
			publish(response, "", mimeType);
		}
		else if (content instanceof String) {
			publish(response, (String)content, mimeType);
		}
		else if (content instanceof byte[]) {
			publish(response, (byte[])content, mimeType);
		}
		else if (content instanceof InputStream) {
			publish(response, (InputStream)content, mimeType);
		}
		else {
			publish(response, content.toString(), mimeType);
		}
	}

	/**
	 * Sends content string to the requestor.
	 * Default <tt>mimeType</tt> is <tt>text/plain</tt>.
	 * 
	 * @param response  The HTTP response object.
	 * @param content  The content to be sent.
	 * @param mimeType  The content MIME type.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void publish(HttpServletResponse response, String content, String mimeType) 
    throws IOException, ServletException {
        String encoding = response.getCharacterEncoding();
    	if (encoding == null) encoding = "utf-8";
    	
    	if (mimeType == null || "".equals(mimeType)) mimeType = "text/plain";
    	
    	mimeType = mimeType.toLowerCase();
    	if (mimeType.indexOf("charset") == -1) {
    		response.setContentType(mimeType + "; charset=" + encoding);
    	}
    	else {
    		response.setContentType(mimeType);
    	}
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = response.getWriter();
        out.println(content);
        out.flush();
    }
	
	/**
	 * Sends content bytes to the requestor. 
	 * Default <tt>mimeType</tt> is <tt>application/octet-stream</tt>.
	 * 
	 * @param response  The HTTP response object.
	 * @param content  The content to be sent.
	 * @param mimeType  The content MIME type.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void publish(HttpServletResponse response, byte[] content, String mimeType) 
    throws IOException, ServletException {
        String encoding = response.getCharacterEncoding();
    	if (encoding == null) encoding = "utf-8";
    	
    	if (mimeType == null || "".equals(mimeType)) mimeType = "application/octet-stream";
        
    	mimeType = mimeType.toLowerCase();
    	if (mimeType.indexOf("charset") == -1) {
    		response.setContentType(mimeType + "; charset=" + encoding);
    	}
    	else {
    		response.setContentType(mimeType);
    	}
        response.setContentLength(content.length);
        //response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        
        ServletOutputStream out = response.getOutputStream();
        out.write(content);
        out.close();
    }
	
	/**
	 * Sends an input stream to the requestor.
	 * 
	 * @param response  The HTTP response object.
	 * @param is  The input stream to be sent.
	 * @param mimeType  The content MIME type.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void publish(HttpServletResponse response, InputStream is, String mimeType) 
    throws IOException, ServletException {
        String encoding = response.getCharacterEncoding();
    	if (encoding == null) encoding = "utf-8";

    	byte[] bufSpace = new byte[8096];
		BufferedInputStream in = new BufferedInputStream(is);
		ByteArrayOutputStream os = new ByteArrayOutputStream(in.available());
		int k;
		while ((k = in.read(bufSpace)) != -1) {
			os.write(bufSpace, 0, k);
		}
		os.close();
		byte[] buf = os.toByteArray();
		is.close();
		in.close();
        
		publish(response, buf, mimeType);
    }
	
	/**
	 * Sends a file to the requestor. 
	 * 
	 * @param response  The HTTP response object.
	 * @param file  The file to be sent.
	 * @param displayableName  The display name of the file in the download dialog.
	 * @param mimeType  The content MIME type.
	 * @param forDownload  indicates whether this is for file download or display.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void publish(HttpServletResponse response, File file, 
			String displayableName, String mimeType, boolean forDownload) 
    throws IOException, ServletException {
		if (forDownload) {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ displayableName + "\"");
		} else {
			response.setHeader("Content-Disposition", "inline; filename=\""
					+ displayableName + "\"");
		}
		
		publish(response, new FileInputStream(file), mimeType);
	}
}
