/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.PluginManager;

/**
 * ContentHandlerFactory creates a content handler.
 * 
 * @author (Fei) John Chen
 */
public class ContentHandlerFactory {
	private static final String CONTENT_HANDLER_PLUGIN_PREFIX = "content.handler.";
	private static ContentHandler defaultContentHandler = new DefaultContentHandler();
	
	/**
     * Returns the ContentHandler for a specific request <tt>format</tt>.
     * 
	 * @param format  The request format.
     * @return the ContentHandler
     */
    public static ContentHandler getContentHandler(String format) {
    	if (format == null) 
    		throw new IllegalArgumentException("format input cannot be null in getContentHandler().");
    	
    	String pluginName = CONTENT_HANDLER_PLUGIN_PREFIX + format;
        ContentHandler handler = (ContentHandler)PluginManager.getInstance().getPlugin(pluginName);
        if (handler == null) {
        	if (EnvConfig.getInstance().hasMimeTypeFor(format)) {
        		handler = defaultContentHandler;
        	}
        }
        return handler;
    }
}
