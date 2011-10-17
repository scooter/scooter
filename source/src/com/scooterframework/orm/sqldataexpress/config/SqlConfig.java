/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.config;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import com.scooterframework.admin.PropertyFileChangeMonitor;
import com.scooterframework.admin.PropertyReader;
import com.scooterframework.common.logging.LogUtil;

/**
 * SqlConfig class.
 * 
 * @author (Fei) John Chen
 */
public class SqlConfig implements Observer {
    private static LogUtil log = LogUtil.getLogger(SqlConfig.class.getName());
    
    private static final SqlConfig me = new SqlConfig();
    private Properties appProperties = null;
    private Properties sqlProperties = new Properties();
    
    public static final String DATA_PROPERTIES_FILE = "sql.properties";

    private SqlConfig() {
        init();
        
        //register to monitor
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
    }
    
    public static SqlConfig getInstance() {
        return me;
    }
    
    public void update(Observable o, Object arg) {
        init();
    }
    
    /**
     * Returns the SQL statement string associated with the key.
     * 
     * @param key   the key to the SQL string
     * @return a SQL string
     */
    public String getSql(String key) {
        if (key == null) return null;
        String query = sqlProperties.getProperty(key.toUpperCase());
        return (query != null)?query:null;
    }
    
    /**
     * Initializes the application. This method can be overridden by the same 
     * method in subclass. 
     */
    protected void init() {
        loadProperties();
        
        sqlProperties.clear();
        
        //convert all keys to upper case
        Enumeration<?> en = appProperties.propertyNames();
        while(en.hasMoreElements()) {
        	Object key = en.nextElement();
        	if (key == null) continue;
            Object value = appProperties.get(key);
            sqlProperties.put(key.toString().toUpperCase(), value);
        }
        
        log.debug("Loaded sql properties: " + sqlProperties);
    }
    
    private void loadProperties() {
        if (appProperties != null) appProperties.clear();
        
        appProperties = PropertyReader.loadPropertiesFromFile(DATA_PROPERTIES_FILE);
        
        if (appProperties == null) appProperties = new Properties();
    }
}
