/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.util.Properties;

/**
 * <tt>NamedProperties</tt> class holds a <tt>Properties</tt> object and its 
 * name.
 * 
 * @author (Fei) John Chen
 */
public class NamedProperties {
    public static final String KEY_NAME = "name";
    
    private String theName = "";
    private Properties theProp = null;
    
    public NamedProperties(String name, Properties prop) {
        if (name == null) throw new IllegalArgumentException("\"name\" can not be null for NamedProperties.");
        
        theName = name;
        theProp = prop;
        if (prop != null && prop.getProperty(KEY_NAME) == null) {
            prop.setProperty(KEY_NAME, name);
        }
    }
    
    public String getName() {return theName;}
    public Properties getProperties() {return theProp;}
    public void setProperties(Properties prop) {
        theProp = prop;
    }
}
