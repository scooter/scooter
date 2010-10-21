/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ContentHandler interface defines methods of a content handler. A content 
 * handler is responsible for publishing content to an http client which 
 * initiates the request.
 * 
 * @author (Fei) John Chen
 */
public interface ContentHandler {
	
	/**
     * Handles result content of a http request. The <tt>extension</tt> is the
     * extension corresponding to a mime type such as "html", "xml" etc.
	 * 
	 * @param request  The http request object.
	 * @param response  The http response object.
	 * @param content  The content to be sent.
	 * @param extension  The content extension.
	 * @throws IOException
	 * @throws ServletException
     */
    public void handle(
    		HttpServletRequest request,
			HttpServletResponse response,
			Object content,
			String extension) throws IOException, ServletException;
}
