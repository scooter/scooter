/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.exception.CreateConnectionFailureException;

/**
 * Abstract UserDatabaseConnectionImpl class 
 * 
 * @author (Fei) John Chen
 */
abstract public class UserDatabaseConnectionImpl implements UserDatabaseConnection {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
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
     * Return the underneath connection.
     * @throws SQLException 
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
     * Return the underlying DatabaseConnectionContext. 
     */
    public DatabaseConnectionContext getDatabaseConnectionContext() {
        return dcc;
    }
    
    /**
     * Retrieves the current auto-commit mode for this <tt>Connection</tt> object.
     * 
     * @return the current state of this <tt>Connection</tt> object's auto-commit mode 
     */
    public boolean getAutoCommit() {
    	if (conn == null) return autoCommit;
    	
		try {
			autoCommit = conn.getAutoCommit();
		}
		catch(Exception ex) {
			String errorMessage = "Failed to get auto commit for the underlying connection.";
			log.error(errorMessage, ex);
			throw new CreateConnectionFailureException(errorMessage, ex);
		}
    	
    	return autoCommit;
    }
    
    /**
     * Sets this connection's auto-commit mode to the given state. 
     * 
     * @param autoCommit <code>true</code> to enable auto-commit mode; 
     *         <code>false</code> to disable it
     * @see #getAutoCommit
     */
    public void setAutoCommit(boolean autoCommit) {
    	this.autoCommit = autoCommit;
    	if (conn == null) return;
    	
		try {
			boolean ac = conn.getAutoCommit();
			if (ac != autoCommit) {
				conn.setAutoCommit(autoCommit);
			}
		}
		catch(Exception ex) {
			String errorMessage = "Failed to set auto commit for the underlying connection.";
			log.error(errorMessage, ex);
			throw new CreateConnectionFailureException(errorMessage, ex);
		}
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
    private boolean autoCommit;
}
