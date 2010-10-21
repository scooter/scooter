/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.util.Enumeration;
import java.util.Properties;

import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.exception.CreateConnectionFailureException;

/**
 * UserDatabaseConnectionFactory class creates UserDatabaseConnection. 
 * 
 * @author (Fei) John Chen
 */
public class UserDatabaseConnectionFactory {
    private static UserDatabaseConnectionFactory me;
    
    static {
        me = new UserDatabaseConnectionFactory();
    }

    private UserDatabaseConnectionFactory() {
    }
    
    /**
     * Returns self.
     */
    public static synchronized UserDatabaseConnectionFactory getInstance() {
        return me;
    }
    
    /**
     * Create a UserDatabaseConnection instance based on the default 
     * connection name in the properties file. 
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection createUserDatabaseConnection() {
        //check if there is any default connection name specified in property file
        String connectionName = DatabaseConfig.getInstance().getDefaultDatabaseConnectionName();
        return createUserDatabaseConnection(connectionName);
    }
    
    /**
     * Create a UserDatabaseConnection instance based on connection name. The 
     * properties related to the name must be in the properties file. 
     * 
     * @param connectionName  a connection name
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection createUserDatabaseConnection(String connectionName) {
        if (connectionName == null || connectionName.equals("")) 
            throw new CreateConnectionFailureException("Failed to create a database connection: connection name is null.");
        
        Properties prop = DatabaseConfig.getInstance().getPredefinedDatabaseConnectionProperties(connectionName);
        
        return buildUserDatabaseConnection(connectionName, prop);
    }
    
    /**
     * Create a UserDatabaseConnection instance based on supplied 
     * DatabaseConnectionContext instance.
     * 
     * @param dcc a DatabaseConnectionContext instance
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection createUserDatabaseConnection(DatabaseConnectionContext dcc) {
        if (dcc == null) return null;
        
        UserDatabaseConnection udc = null;
        if (dcc instanceof DataSourceConnectionContext) {
            udc = new DataSourceConnection((DataSourceConnectionContext)udc);
        }
        else if (dcc instanceof JdbcConnectionContext) {
            udc = new JdbcConnection((JdbcConnectionContext)udc);
        }
        else {
            throw new CreateConnectionFailureException("Failed to create a database connection: " + 
                "input DatabaseConnectionContext instance is neither " + 
                "a DataSourceConnectionContext nor a JdbcConnectionContext type of instance.");
        }
        
        return udc;
    }
    
    /**
     * Create a UserDatabaseConnection instance based on supplied 
     * connection properties.
     * 
     * @return UserDatabaseConnection
     * @param prop Connection properties
     */
    /*public UserDatabaseConnection createUserDatabaseConnection(Properties supplied) {
        if (supplied == null || supplied.size() == 0) return null;
        
        UserDatabaseConnection udc = null;
        String connectionName = supplied.getProperty(DatabaseConnectionContext.CONNECTION_NAME);
        
        if (connectionName == null) {
            //check if there is any default connection name specified in property file
            connectionName = DatabaseConfig.getInstance().getDefaultDatabaseConnectionName();
            if (connectionName == null || connectionName.equals("")) 
                throw new CreateConnectionFailureException("Failed to create a database connection: connection name " + 
                    "in the properties is null.");
        }
        
        Properties predefined = DatabaseConfig.getInstance().getPredefinedDatabaseConnectionProperties(connectionName);
        
        // update the predefined properties with the latest supplied.
        return buildUserDatabaseConnection(updateProperties(predefined, supplied));
    }*/
    
    /**
     * Build a UserDatabaseConnection instance based on all available 
     * properties.
     * 
     * The properties is a mix of predefined properties with supplied. 
     * 
     * @return UserDatabaseConnection
     * @param prop Connection properties
     */
    private UserDatabaseConnection buildUserDatabaseConnection(String connectionName, Properties prop) {
        if (prop == null || prop.size() == 0) 
            throw new CreateConnectionFailureException("Failed to create a database connection: connection properties is null.");
        
        UserDatabaseConnection udc = null;
        
        //now find if it is dataSouce type
        if (isDataSourceConnectionContext(prop)) {
            udc = new DataSourceConnection(connectionName, new DataSourceConnectionContext(prop));
        }
        else {
            udc = new JdbcConnection(connectionName, new JdbcConnectionContext(prop));
        }
        
        return udc;
    }
    
    private boolean isDataSourceConnectionContext(Properties prop) {
        boolean isDataSource = false;
        String dataSourceName = prop.getProperty(DatabaseConnectionContext.KEY_DATASOURCENAME);
        String url = prop.getProperty(DatabaseConnectionContext.KEY_URL);
        if (dataSourceName != null && !dataSourceName.equals("")) {
            if (url != null && !url.equals("")) 
                throw new CreateConnectionFailureException("Failed to detect whether " + 
                    "the connection properties is for Jdbc connection or for datasource connection: url=" + url +
                    " datasource=" + dataSourceName);
            isDataSource = true;
        }
        else {
            if (url == null || url.equals("")) 
                throw new CreateConnectionFailureException("Failed to detect whether " + 
                    "the connection properties is for Jdbc connection or for datasource connection: " + 
                    "both url and dataSourceName are either null or empty.");
        }
        
        return isDataSource;
    }
    
    private Properties updateProperties(Properties oldProp, Properties newProp) {
        if (oldProp == null || oldProp.size() == 0) return newProp;
        if (newProp == null || newProp.size() == 0) return oldProp;
        
        Enumeration en = newProp.propertyNames();
        while(en.hasMoreElements()) {
            String key = (String)en.nextElement();
            oldProp.setProperty(key, newProp.getProperty(key));
        }
        return oldProp;
    }
}
