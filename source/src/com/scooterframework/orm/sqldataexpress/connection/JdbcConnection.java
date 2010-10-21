/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.sql.Connection;

import com.scooterframework.orm.sqldataexpress.exception.CreateConnectionFailureException;

/**
 * JdbcConnection class has information about database connection created 
 * by DriverManager. 
 * 
 * @author (Fei) John Chen
 */
public class JdbcConnection extends UserDatabaseConnectionImpl {
    public JdbcConnection(JdbcConnectionContext dcc) {
        super(dcc);
    }
    
    public JdbcConnection(String connectionName, JdbcConnectionContext dcc) {
        super(connectionName, dcc);
    }
    
    /**
     * Return the connection type.
     */
    public String getConnectionType() {
        return UserDatabaseConnection.JDBC_CONNECTION;
    }
    
    /**
     * Create a connection based on specific database connection context.
     * 
     * @return Connection
     */
    protected Connection createConnection() {
        JdbcConnectionContext jdcc = (JdbcConnectionContext)getDatabaseConnectionContext();
        Connection connection = null;
        if (jdcc.useConnectionPool()) {
            connection = ConnectionUtil.createPooledConnection(jdcc);
        }
        else {
            connection = ConnectionUtil.createConnection(jdcc);
        }
        
        if (connection == null) 
            throw new CreateConnectionFailureException( "JdbcConnection:createConnection() failure for " + getConnectionName() + ".");
        
        return connection;
    }
}
