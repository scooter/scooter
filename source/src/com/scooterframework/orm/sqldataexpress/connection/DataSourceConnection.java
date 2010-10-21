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
 * DataSourceConnection class has information about database connection 
 * created by DataSource which has been registered to the container or server 
 * through JNDI APIs. 
 * 
 * @author (Fei) John Chen
 */
public class DataSourceConnection extends UserDatabaseConnectionImpl {   
    public DataSourceConnection(DataSourceConnectionContext dcc) {
        super(dcc);
    }
    
    public DataSourceConnection(String connectionName, DataSourceConnectionContext dcc) {
        super(connectionName, dcc);
    }
    
    /**
     * Return the connection type.
     */
    public String getConnectionType() {
        return UserDatabaseConnection.DATASOURCE_CONNECTION;
    }

    /**
     * Create a connection based on specific database connection context.
     * 
     * @return Connection
     */
    protected Connection createConnection() {
        Connection connection = ConnectionUtil.createConnection((DataSourceConnectionContext)getDatabaseConnectionContext());
        
        if (connection == null) 
            throw new CreateConnectionFailureException( "DataSourceConnection:createConnection() failure.");
        
        return connection;
    }

}
