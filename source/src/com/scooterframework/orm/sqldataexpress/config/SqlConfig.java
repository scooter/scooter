/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.config;

import java.util.Iterator;
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
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    private static SqlConfig me;
    private Properties appProperties = null;
    private Properties sqlProperties = new Properties();
    
    public static final String DATA_PROPERTIES_FILE = "sql.properties";
    
    static {
        me = new SqlConfig();
    }

    private SqlConfig() {
        init();
        
        //register to monitor
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
    }
    
    public static synchronized SqlConfig getInstance() {
        return me;
    }
    
    public void update(Observable o, Object arg) {
        init();
    }
    
    /**
     * Returns the sql statement string associated with the key.
     * 
     * @param key   the key to the sql string
     * @return a sql string
     */
    public String getSql(String key) {
        if (key == null) return null;
        String query = (String)sqlProperties.get(key.toUpperCase());
        return (query != null)?query:null;
    }
    
    /**
     * Initializes the application. This method can be overridden by the same 
     * method in subclass. 
     */
    protected void init() {
        loadProperties();
        
        sqlProperties.clear();
        
        Properties p = appProperties;
        
        //conver all keys to upper case
        for(Iterator it=p.keySet().iterator(); it.hasNext();) {
            String key = (String)it.next();
            Object value = p.get(key);
            sqlProperties.put(key.toUpperCase(), value);
        }
    }
    
    private void loadProperties() {
        if (appProperties != null) appProperties.clear();
        
        appProperties = PropertyReader.loadPropertiesFromFile(DATA_PROPERTIES_FILE);
        
        if (appProperties == null) appProperties = new Properties();
    }
}
