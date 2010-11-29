/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.admin.PluginManager;

/**
 * TemplateHandlerFactory creates a template handler.
 * 
 * @author (Fei) John Chen
 */
public class TemplateHandlerFactory {
	private static final String TEMPLATE_HANDLER_PLUGIN_PREFIX = "template.handler.";
	private static final StringTemplateHandler stHandler = new StringTemplateHandler();
	
	/**
     * Returns the TemplateHandler for a specific template 
     * <tt>extension</tt> type.
     * 
	 * @param extension  The template extension.
     * @return the TemplateHandler
     */
    public static TemplateHandler getTemplateHandler(String extension) {
    	if (extension == null) 
    		throw new IllegalArgumentException("extension input cannot be null in getTemplateHandler().");
    	
    	if (extension.startsWith(".")) {
    		extension = extension.substring(1);
    	}
    	
    	if (extension.equalsIgnoreCase("st")) {
    		return stHandler;
    	}
    	
    	String pluginName = TEMPLATE_HANDLER_PLUGIN_PREFIX + extension;
    	TemplateHandler handler = (TemplateHandler)PluginManager.getInstance().getPlugin(pluginName);
        return handler;
    }
}
