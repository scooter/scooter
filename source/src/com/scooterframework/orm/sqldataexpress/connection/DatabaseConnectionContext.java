/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.io.Serializable;
import java.util.Properties;

import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;

/**
 * DatabaseConnectionContext interface
 * 
 * @author (Fei) John Chen
 */
public interface DatabaseConnectionContext extends Serializable
{
    /**
     * Returns database connection name
     *
     * @return String
     */
    public String getConnectionName();

    /**
     * Returns before-connection callback class.
     */
    public String getBeforeConnectionClassName();

    /**
     * Returns before-connection callback method.
     */
    public String getBeforeConnectionMethodName();

    /**
     * Returns after-connection callback class.
     */
    public String getAfterConnectionClassName();

    /**
     * Returns after-connection callback method.
     */
    public String getAfterConnectionMethodName();

    /**
     * Returns database username
     */
    public String getUsername();

    /**
     * Returns database password
     */
    public String getPassword();

    /**
     * Returns the maximum time in seconds that this data source can wait while 
     * attempting to connect to a database
     */
    public Integer getLoginTimeout();
    
    /**
     * Checks if the connection is readonly.
     *
     * @return true if readonly connection
     */
    public boolean isReadonly();
    
    /**
     * Sets readonly connection
     *
     * @param readonly <tt>true</tt> if read only connection is desired
     */
    public void setReadonly(boolean readonly);
    
    /**
     * Returns the vendor name of the database
     *
     * @return String
     */
    public String getVendor();
    
    /**
     * Returns the schema of the database connection
     *
     * @return String
     */
    public String getSchema();
    
    /**
     * Checks whether to use login id as schema.
     *
     * @return true if using login id as schema
     */
    public boolean useLoginAsSchema();
    
    /**
     * Checks whether to use login username and password for 
     * making a connection.
     *
     * @return true if using username and password for making a connection
     */
    public boolean useLoginForConnection();
    
    /**
     * Checks if transaction isolation level is specified. If not, the 
     * database's default transaction isolation level is used.
     * 
     * @return true if specified
     */
    public boolean hasSpecifiedTransactionIsolationLevel();
    
    /**
     * Returns the specified transaction isolation level.
     *
     * @return String
     */
    public int getTransactionIsolationLevel();
    
    /**
     * Returns all database connection properties
     */
    public Properties getProperties();
    
    /**
     * Sets all database connection properties
     */
    public void setProperties(Properties properties);
    
    /**
     * Returns connection role name and password properties
     */
    public Properties getConnectionRoles();
    
    /**
     * Sets connection role name and password properties
     */
    public void setConnectionRoles(Properties roles);
    
    /**
     * Database connection properties keys
     */
    public static final String KEY_CONNECTION_NAME = DatabaseConfig.KEY_DB_CONNECTION_NAME;
    public static final String KEY_DATASOURCENAME = DatabaseConfig.KEY_DB_CONNECTION_DATASOURCE_NAME;
    public static final String KEY_DRIVER = DatabaseConfig.KEY_DB_CONNECTION_DRIVER;
    public static final String KEY_URL = DatabaseConfig.KEY_DB_CONNECTION_URL;
    public static final String KEY_BEFORE_CONNECTION = DatabaseConfig.KEY_DB_CONNECTION_BEFORE;
    public static final String KEY_AFTER_CONNECTION = DatabaseConfig.KEY_DB_CONNECTION_AFTER;
    public static final String KEY_USERNAME = DatabaseConfig.KEY_DB_CONNECTION_USERNAME;
    public static final String KEY_PASSWORD = DatabaseConfig.KEY_DB_CONNECTION_PASSWORD;
    public static final String KEY_LOGINTIMEOUT = DatabaseConfig.KEY_DB_CONNECTION_TIMEOUT;
    public static final String KEY_READONLY = DatabaseConfig.KEY_DB_CONNECTION_READONLY;
    public static final String KEY_TRANSACTION_ISOLATION_LEVEL = DatabaseConfig.KEY_DB_CONNECTION_TRANSACTION_ISOLATION_LEVEL;
    public static final String KEY_VENDOR = DatabaseConfig.KEY_DB_CONNECTION_VENDOR;
    public static final String KEY_SCHEMA = DatabaseConfig.KEY_DB_CONNECTION_SCHEMA;
    public static final String KEY_USE_LOGIN_AS_SCHEMA = DatabaseConfig.KEY_DB_CONNECTION_USE_LOGIN_AS_SCHEMA;
    public static final String KEY_USE_LOGIN_FOR_CONNECTION = DatabaseConfig.KEY_DB_CONNECTION_USE_LOGIN_FOR_CONNECTION;
    public static final String KEY_MAX_POOL_SIZE = DatabaseConfig.KEY_DB_CONNECTION_MAX_POOL_SIZE;
    public static final String KEY_MIN_POOL_SIZE = DatabaseConfig.KEY_DB_CONNECTION_MIN_POOL_SIZE;
    public static final String KEY_ACQUIRE_INCREMENT = DatabaseConfig.KEY_DB_CONNECTION_ACQUIRE_INCREMENT;
    public static final String KEY_INITIAL_POOL_SIZE = DatabaseConfig.KEY_DB_CONNECTION_INITIAL_POOL_SIZE;
    public static final String KEY_MAX_IDLE_TIME = DatabaseConfig.KEY_DB_CONNECTION_MAX_IDLE_TIME;
}
