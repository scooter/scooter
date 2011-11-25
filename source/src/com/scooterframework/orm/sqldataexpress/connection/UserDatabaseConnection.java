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


/**
 * UserDatabaseConnection interface allows access to both connection and 
 * connection context. 
 * 
 * @author (Fei) John Chen
 */
public interface UserDatabaseConnection
{

    public static final String JDBC_CONNECTION = "JDBC";
    
    public static final String DATASOURCE_CONNECTION = "DS";
    
    /**
     * Return the connection name.
     */
    public String getConnectionName();
    
    /**
     * Return the connection type.
     */
    public String getConnectionType();
    
    /**
     * Return the underlying connection.
     */
    public Connection getConnection();
    
    /**
     * Return the underlying DatabaseConnectionContext. 
     */
    public DatabaseConnectionContext getDatabaseConnectionContext();
    
    /**
     * Retrieves the current auto-commit mode for this <tt>Connection</tt> object.
     * 
     * @return the current state of this <tt>Connection</tt> object's auto-commit mode 
     */
    public boolean getAutoCommit();
    
    /**
     * Sets this connection's auto-commit mode to the given state. 
     * 
     * @param autoCommit <code>true</code> to enable auto-commit mode; 
     *         <code>false</code> to disable it
     * @exception SQLException if a database access error occurs
     * @see #getAutoCommit
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException;
}
