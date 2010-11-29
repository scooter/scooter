/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.util.Map;

/**
 * TemplateHandler interface defines methods of a template handler. A template 
 * handler is responsible for processing the template with input data.
 * 
 * @author (Fei) John Chen
 */
public interface TemplateHandler {
	
	/**
     * Handles processing the <tt>content</tt> with <tt>dataMap</tt>.
	 * 
	 * @param content  The content to be processed.
	 * @param dataMap  data (name/value pairs) to be used to process the content
	 * @return processed content as string
     */
    public String handle(String content, Map dataMap);
    
    /**
     * Handles processing the <tt>viewTemplate</tt> with <tt>dataMap</tt>.
     * 
     * @param viewTemplate
     * @param dataMap
     * @return processed content as string
     */
    public String handle(File viewTemplate, Map dataMap);
}
