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
     * Return the underline connection.
     */
    public Connection getConnection();
    
    /**
     * Return the underline DatabaseConnectionContext. 
     */
    public DatabaseConnectionContext getDatabaseConnectionContext();
}
