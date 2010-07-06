/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.admin.AutoLoadedObjectFactory;

/**
 * ActionControlFilter class contains a filter for a controller class.
 * 
 * @author (Fei) John Chen
 */
public class ActionControlFilter {

    /**
     * Constructs an ActionControlFilter instance.
     * 
     * @param owner   the declaring class of the filter method
     * @param filter  a public method name in the controller that acts as a filter.
     */
    public ActionControlFilter(Class owner, String filter) {
        this.owner = owner;
        this.filterMethod = filter;
        key = formatKey(owner, filter);
    }

    
    /**
     * Executes the filter
     */
    public String execute() {
        String r = null;
        Object o = AutoLoadedObjectFactory.getInstance().execute(owner.getName(), filterMethod, null);
        if (o != null) {
            if (o instanceof String) {
                r = o.toString();
            }
            else {
                throw new IllegalArgumentException("Filter method \"" + filterMethod + 
                    "\" of class \"" + owner.getName() + "\" should only return " + 
                    "a string, but here \"" + o.getClass().getName() + "\" is returned instead.");
            }
        }
        return r;
    }
    
    public String getACFKey() {
        return key;
    }
    
    public static String formatKey(Class owner, String filter) {
        return owner.getName() + "_" + filter + "_";
    }
    
    
    /**
     *  the declaring class of the filter method
     */
    private Class owner;
    
    /**
     * A public method name in the controller class that acts as a filter.
     */
    private String filterMethod;
    
    /**
     * A string to represent the uniqueness of this class
     */
    private String key;
}
