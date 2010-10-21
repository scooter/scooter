/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import com.scooterframework.common.logging.LogUtil;

/**
 * PropertyFileUtil
 * 
 * @author (Fei) John Chen
 */
public class PropertyFileUtil {
    private static LogUtil log = LogUtil.getLogger(PropertyFileUtil.class.getName());
    
    /**
     * Load all properties from a file on classpath
     * 
     * @param resourceName a name for a resource on classpath
     * @return properties in the resource
     */
    public static Properties loadPropertiesFromResource(String resourceName) 
    throws Exception {
        Properties props = new Properties();
        try {
            // load all properties
            log.debug("loading properties from resource " + resourceName);
            String pFile = resourceName;
            if (!resourceName.startsWith("/")) pFile = "/" + resourceName;
            InputStream is = PropertyFileUtil.class.getResourceAsStream(pFile);
            if (is != null) {
                props.load(is);
                is.close();
            }
            else {
                throw new Exception("No resource file with name " + resourceName + " is found.");
            }
        } catch(Exception ex) {
            throw ex;
        }
        return props;
    }
    
    /**
     * Load all properties from a file 
     * 
     * @param fullFileName full name of the file
     * @return properties in the resource
     */
    public static Properties loadPropertiesFromFile(String fullFileName) throws Exception {
        Properties props = new Properties();
        loadPropertiesFromFile(props, fullFileName);
        return props;
	}
    
    /**
     * Load all properties from a file 
     * 
     * @param fullFileName full name of the file
     * @return properties in the resource
     */
    public static Properties loadOrderedPropertiesFromFile(String fullFileName) throws Exception {
        Properties props = new OrderedProperties();
        loadPropertiesFromFile(props, fullFileName);
        return props;
	}
    
    /**
     * Load all properties from a file 
     * 
     * @param props properties to load file
     * @param fullFileName full name of the file
     * @return properties in the resource
     */
    private static void loadPropertiesFromFile(Properties props, String fullFileName) throws Exception {
		InputStream is = null;
        try {
            // load all properties
            is = new FileInputStream(fullFileName);
            if (is != null) {
                props.load(is);
                is.close();
            }
        } catch(FileNotFoundException fnfe) {
            throw new Exception("File (" + fullFileName + ") does not exist.");
        } catch (IOException ioe) {
            throw new Exception("Error reading file " + fullFileName + ".");
        }
	}
	
	/**
     * print out all properties. 
     * 
     */
    public static void printAllProperties(ResourceBundle rb) {
        if (rb != null) {
            Enumeration en = rb.getKeys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                String value = rb.getString(key.toString());
                log.debug("key [" + key + "]  value =  [" + value + "]");
            }
        } 
        else {
            log.debug("printAllProperties: Resource Bundle is null");
        }
    }
    
    /**
     * print out all properties. 
     * 
     */
    public static void printAllProperties(Properties pp, String nameValueSpliter, String propertyDelimiter) {    
        if (pp != null) {
            Enumeration en = pp.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                String value = pp.getProperty(key.toString());
                //log.debug("key [" + key + "]  value =  [" + value + "]");
                
                log.debug("key [" + key + "]  value = [" + value + "]");
                log.debug("parsed: " + parseNestedPropertiesFromLine(value, nameValueSpliter, propertyDelimiter));
            }
        } 
        else {
            log.debug("printAllProperties: Properties is null");
        }
    }
    
    /**
     * Returns properties from a nested property line. 
     * 
     * <p>
     * Examples:
     *   For a property line <tt>routes.name.login=url=login, controller=home, action=new</tt>, 
     *   the key of the property is <tt>routes.name.login</tt>, the nested key/value 
     *   paires are <tt>url/login</tt>, <tt>controller/home</tt>, and <tt>action/new</tt>.
     * </p>
     * 
     * @param line a string of properties
     * @param nameValueSpliter a short string that separates name and value elements in a pair
     * @param propertyDelimiter a char that separates pairs in a string line
     * @return properties 
     */
    public static Properties parseNestedPropertiesFromLine(String line, String nameValueSpliter, String propertyDelimiter) {
        Properties properties = new Properties();
        if (line == null) return properties;
        
        try {
            properties = Converters.convertStringToProperties(line, nameValueSpliter, propertyDelimiter);
        } catch(Exception ex) {
            String message = "Error in parsing nested properties from line \"" + line + "\" because " + ex.getMessage();
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return properties;
    }
}
