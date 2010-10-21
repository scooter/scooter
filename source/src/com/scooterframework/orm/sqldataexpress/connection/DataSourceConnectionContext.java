/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.util.Properties;

/**
 * DataSourceConnectionContext class
 * 
 * @author (Fei) John Chen
 */
public class DataSourceConnectionContext extends DatabaseConnectionContextImpl {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6191021388206230252L;

	/**
     * Initializes database connection context
     */
    public DataSourceConnectionContext(Properties prop) {
        super(prop);
        
        this.dataSourceName = prop.getProperty(DatabaseConnectionContext.KEY_DATASOURCENAME);
    }
    
    /**
     * Initialize database connection context
     */
    public DataSourceConnectionContext(String connectionName, 
                                       String dataSourceName) {
        this(connectionName, dataSourceName, null, null);
    }
    
    /**
     * Initialize database connection context
     */
    public DataSourceConnectionContext(String connectionName, 
                                       String dataSourceName, 
                                       String username, 
                                       String password) {
        this.connectionName = connectionName;
        this.dataSourceName = dataSourceName;
        this.username = username;
        this.password = password;
        
        properties.setProperty(DatabaseConnectionContext.KEY_CONNECTION_NAME, connectionName);
        properties.setProperty(DatabaseConnectionContext.KEY_DATASOURCENAME, dataSourceName);
        properties.setProperty(DatabaseConnectionContext.KEY_USERNAME, username);
        properties.setProperty(DatabaseConnectionContext.KEY_PASSWORD, password);
    }
    
    /**
     * Return the jndi data source name of the database
     *
     * @return String
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * Return the vendor name of the database
     *
     * @return String
     */
    public String getVendor() {
        if (vendor == null) {
            vendor = checkVendor(dataSourceName);
            
            if (vendor == null) {
                vendor = checkVendor(connectionName);
            }
        }
        return vendor;
    }

    private String dataSourceName = null;
}
