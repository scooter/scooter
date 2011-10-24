/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.sql.Connection;

/**
 * Abstract UserDatabaseConnectionImpl class 
 * 
 * @author (Fei) John Chen
 */
abstract public class UserDatabaseConnectionImpl implements UserDatabaseConnection {
    public UserDatabaseConnectionImpl(DatabaseConnectionContext dcc) {
        this(null, dcc);
    }
    
    public UserDatabaseConnectionImpl(String connectionName, DatabaseConnectionContext dcc) {
        if (dcc == null) 
            throw new IllegalArgumentException("Failed to initialize UserDataConnection: " + 
                "DatabaseConnectionContext instance is null.");
        
        if (connectionName == null) {
            connectionName = dcc.getConnectionName();
            if (connectionName == null) 
            throw new IllegalArgumentException("Failed to initialize UserDataConnection: " + 
                "connection name is null.");
        }
        
        this.dcc = dcc;
        this.connName = connectionName;
    }
    
    /**
     * Return the connection name.
     */
    public String getConnectionName() {
        return connName;
    }
    
    /**
     * Return the underline connection.
     */
    public Connection getConnection() {
    	if (conn == null) {
    		conn = createConnection();
    	}
        return conn;
    }
    
    /**
     * Return status
     */
    public boolean connectionIsOpen() {
        boolean connIsOpen = true;
        try {
            if (conn == null || conn.isClosed()) connIsOpen = false;
        }
        catch(Exception ex) {
            connIsOpen = false;
        }
        
        return connIsOpen;
    }
    
    /**
     * Return the underline DatabaseConnectionContext. 
     */
    public DatabaseConnectionContext getDatabaseConnectionContext() {
        return dcc;
    }

    /**
     * Create a connection based on specific database connection context.
     * 
     * @return Connection
     */
    abstract protected Connection createConnection();

    private String connName;
    private DatabaseConnectionContext dcc;
    private Connection conn;
}
