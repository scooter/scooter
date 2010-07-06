/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.util.Properties;

import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;

/**
 * JdbcConnectionContext class
 * 
 * @author (Fei) John Chen
 */
public class JdbcConnectionContext extends DatabaseConnectionContextImpl {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -2762750196947777740L;
	
	/**
     * Initializes database connection context
     */
    public JdbcConnectionContext(Properties prop) {
        super(prop);
        
        this.driverClassName = prop.getProperty(DatabaseConnectionContext.KEY_DRIVER);
        this.url = prop.getProperty(DatabaseConnectionContext.KEY_URL);
        
        this.maxPoolSize =      getInt(prop.getProperty(DatabaseConnectionContext.KEY_MAX_POOL_SIZE), DatabaseConfig.DEFAULT_VALUE_max_pool_size);
        this.minPoolSize =      getInt(prop.getProperty(DatabaseConnectionContext.KEY_MIN_POOL_SIZE), DatabaseConfig.DEFAULT_VALUE_min_pool_size);
        this.acquireIncrement = getInt(prop.getProperty(DatabaseConnectionContext.KEY_ACQUIRE_INCREMENT), DatabaseConfig.DEFAULT_VALUE_acquire_increment);
        this.initialPoolSize =  getInt(prop.getProperty(DatabaseConnectionContext.KEY_INITIAL_POOL_SIZE), DatabaseConfig.DEFAULT_VALUE_initial_pool_size);
        this.maxIdleTime =      getInt(prop.getProperty(DatabaseConnectionContext.KEY_MAX_IDLE_TIME), DatabaseConfig.DEFAULT_VALUE_max_idle_time);
        
        if (maxPoolSize > 0) useConnectionPool = true;
    }
    
    private int getInt(String value, int defaultInt) {
        if (value == null) return defaultInt;
        
        int result = defaultInt;
        try {
            result = Integer.parseInt(value);
        }
        catch(Exception ex) {
            ;
        }
        return result;
    }
    
    /**
     * Initializes database connection context
     */
    public JdbcConnectionContext(String connectionName, 
                                 String driverClassName, 
                                 String url, 
                                 String username, 
                                 String password) {
        this.connectionName = connectionName;
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        
        properties.setProperty(DatabaseConnectionContext.KEY_CONNECTION_NAME, connectionName);
        properties.setProperty(DatabaseConnectionContext.KEY_DRIVER, driverClassName);
        properties.setProperty(DatabaseConnectionContext.KEY_URL, url);
        properties.setProperty(DatabaseConnectionContext.KEY_USERNAME, username);
        properties.setProperty(DatabaseConnectionContext.KEY_PASSWORD, password);
    }
    
    /**
     * Initializes database connection context
     */
    public JdbcConnectionContext(String connectionName, 
                                 String username, 
                                 String password) {
        this.connectionName = connectionName;
        this.username = username;
        this.password = password;
        
        properties.setProperty(DatabaseConnectionContext.KEY_CONNECTION_NAME, connectionName);
        properties.setProperty(DatabaseConnectionContext.KEY_USERNAME, username);
        properties.setProperty(DatabaseConnectionContext.KEY_PASSWORD, password);
    }
    
    /**
     * Returns the driver class name of the database
     *
     * @return String
     */
    public String getDriverClassName() {
        return driverClassName;
    }
    
    /**
     * Returns the url of the database
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the vendor name of the database
     *
     * @return String
     */
    public String getVendor() {
        if (vendor == null) {
            //try to find vendor name from driver class name
            vendor = checkVendor(driverClassName);
            
            if (vendor == null) {
                vendor = checkVendor(url);
                
                if (vendor == null) {
                    vendor = checkVendor(connectionName);
                }
            }
        }
        return vendor;
    }
    
    /**
     * Returns maximum number of connections a pool will maintain at any given time
     * 
     * @return int
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    /**
     * Returns minimum number of connections a pool will maintain at any given time
     * 
     * @return int
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }
    
    /**
     * Returns number of connections at a time framework will try to acquire when the pool is exhausted
     * 
     * @return int
     */
    public int getAcquireIncrement() {
        return acquireIncrement;
    }
    
    /**
     * Returns number of connections a pool will try to acquire upon startup
     * 
     * @return int
     */
    public int getInitialPoolSize() {
        return initialPoolSize;
    }
    
    /**
     * Returns seconds a connection can remain pooled but unused before being discarded
     * 
     * @return int
     */
    public int getMaxIdleTime() {
        return maxIdleTime;
    }
    
    /**
     * Checks to see if connection pool needs to be used.
     * 
     * @return true for using connection pool
     */
    public boolean useConnectionPool() {
        return useConnectionPool;
    }

    private String driverClassName = null;
    private String url = null;
    private int maxPoolSize;
    private int minPoolSize;
    private int acquireIncrement;
    private int initialPoolSize;
    private int maxIdleTime;
    private boolean useConnectionPool;
}
