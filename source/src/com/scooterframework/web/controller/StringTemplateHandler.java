/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.FileUtil;
import com.scooterframework.tools.common.GeneratorHelper;

/**
 * DefaultContentHandler is responsible for handling content of a request 
 * format when the specific handler for the format is not available.
 * 
 * <p>
 * The content for the following request format is treated as text:
 * json, txt, text, and xml.
 * 
 * @author (Fei) John Chen
 */
public class StringTemplateHandler implements TemplateHandler {
	private static LogUtil log = LogUtil.getLogger(StringTemplateHandler.class.getName());
	
	/**
     * Handles processing the <tt>content</tt> with <tt>dataMap</tt>.
	 * 
	 * @param content  The content to be processed.
	 * @param props  properties (name/value pairs) to be used to process the content
	 * @return processed content as string
     */
    public String handle(String content, Map props) {
    	StringTemplate st = new StringTemplate(content);
		st.setAttributes(props);
    	return st.toString();
    }
    
    /**
     * Handles processing the <tt>viewTemplate</tt> with <tt>dataMap</tt>.
     * 
     * @param viewTemplate
     * @param dataMap
     * @return processed content as string
     */
    public String handle(File viewTemplate, Map dataMap) {
    	String content = getTemplateFileContent(viewTemplate);
    	return handle(content, dataMap);
    }

	protected String getTemplateFileContent(File templateFile) {
		String templateContent = "";
		try {
			List<String> lines = GeneratorHelper.loadToStringListFromFile(templateFile.getAbsolutePath());
			if (lines == null)
				return (String) null;
			String linebreak = System.getProperty("line.separator", "\r\n");
			StringBuffer sb = new StringBuffer();
			Iterator<String> it = lines.iterator();
			while (it.hasNext()) {
				sb.append(it.next()).append(linebreak);
			}
			templateContent = sb.toString();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to load template file '" 
					+ templateFile + "': " + ex.getMessage());
		}
		return templateContent;
	}
}
