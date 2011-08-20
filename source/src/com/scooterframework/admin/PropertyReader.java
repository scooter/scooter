/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.PropertyFileUtil;

/**
 * PropertyReader class loads property information.
 * 
 * @author (Fei) John Chen
 */
public class PropertyReader {
    private static LogUtil log = LogUtil.getLogger(PropertyReader.class.getName());
    
    /**
     * Load all properties from a file 
     * 
     * @param file property file
     * @return properties in the resource
     */
    public static Properties loadPropertiesFromFile(File file) {
        String fullFileName = file.getPath();
        return _loadPropertiesFromFile(fullFileName);
	}
    
    /**
     * Load all properties from a file 
     * 
     * @param fileName name of the property file, not full name
     * @return properties in the resource
     */
    public static Properties loadPropertiesFromFile(String fileName) {
        String fullFileName = PropertyFileChangeMonitor.getInstance().getFullFileName(fileName);
        return _loadPropertiesFromFile(fullFileName);
	}

    private static Properties _loadPropertiesFromFile(String fullFileName) {
        Properties appProperties = null;
        try {
            appProperties = PropertyFileUtil.loadPropertiesFromFile(fullFileName);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
            try {
                appProperties = PropertyFileUtil.loadPropertiesFromResource(fullFileName);
            }
            catch(Exception exr) {
            	ex.printStackTrace();
                String errorMessage = "ERROR ERROR ERROR -- Error loading " + fullFileName + ": " + exr.getMessage();
                log.fatal(errorMessage);
            }
        }
        return appProperties;
	}
    
    /**
     * Load all ordered properties from a file 
     * 
     * @param file  the property file
     * @return ordered properties in the resource
     */
    public static Properties loadOrderedPropertiesFromFile(File file) {
        String fullFileName = file.getPath();
        return _loadOrderedPropertiesFromFile(fullFileName);
	}
    
    /**
     * Load all ordered properties from a file 
     * 
     * @param fileName name of the property file
     * @return ordered properties in the resource
     */
    public static Properties loadOrderedPropertiesFromFile(String fileName) {
        String fullFileName = PropertyFileChangeMonitor.getInstance().getFullFileName(fileName);
        return _loadOrderedPropertiesFromFile(fullFileName);
	}

    private static Properties _loadOrderedPropertiesFromFile(String fullFileName) {
        Properties appProperties = null;
        try {
            appProperties = PropertyFileUtil.loadOrderedPropertiesFromFile(fullFileName);
        }
        catch(Exception ex) {
            String errorMessage = "ERROR ERROR ERROR -- Error loading " + fullFileName + ": " + ex.getMessage();
            log.fatal(errorMessage);
        }
        return appProperties;
	}
}
