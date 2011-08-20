/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.logging;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.PropertyConfigurator;

import com.scooterframework.admin.PropertyFileChangeMonitor;

/**
 * LogConfig class manages log4j.properties file.
 * 
 * 
 * @author (Fei) John Chen
 */
public class LogConfig implements Observer {
    private static LogConfig me;
    private String propertyFilePath;
    private String logPropertyFile;
    public static final String DATA_PROPERTIES_FILE = "log4j.properties";
    
    private LogConfig(String logPropertyFilePath) {
        this.propertyFilePath = logPropertyFilePath;
        logPropertyFile = getFullFileName(DATA_PROPERTIES_FILE);
        
        init();
        
        LogUtil.enableLogger();
    }
    
    private void init() {
        PropertyConfigurator.configure(logPropertyFile);
    }
    
    private String getFullFileName(String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        return propertyFilePath + File.separatorChar + fileName;
    }


    public static LogConfig getInstance(String logPropertyFilePath) {
        if (me == null) me = new LogConfig(logPropertyFilePath);
        return me;
    }
    
    public void enableMonitoring() {
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
    }
    
    public void update(Observable o, Object arg) {
        init();
    }
}
