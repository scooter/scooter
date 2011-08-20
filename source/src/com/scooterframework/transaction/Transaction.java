/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import com.scooterframework.orm.sqldataexpress.connection.DatabaseConnectionContext;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;

/**
 * Transaction interface provides methods for managing transaction boundaries.
 * 
 * @author (Fei) John Chen
 */
public interface Transaction {
    public static final String CMT_TRANSACTION_TYPE  = "CMT";
    public static final String JDBC_TRANSACTION_TYPE = "JDBC";
    public static final String JTA_TRANSACTION_TYPE  = "JTA";

    /**
     * Register a resource which is to be managed by this transaction.
     */
    public void registerResource(String name, UserDatabaseConnection resource);
    
    /**
     * Deregister a resource from a transaction.
     */
    public void deregisterResource(String name, UserDatabaseConnection resource);

    /**
     * Release all resources managed by this transaction.
     * This method should always be called at the end of a transaction block.
     */
    public void releaseResources();
    
    /**
     * Start a transaction. 
     */
    public void begin();
    
    /**
     * Commit a transaction.
     */
    public void commit();
    
    /**
     * Rollback a transaction.
     */
    public void rollback();
    
    /**
     * Check if transaction has started.
     */
    public boolean isTransactionStarted();
    
    /**
     * Check if transaction has ended.
     */
    public boolean isTransactionEnded();
    
    /**
     * Return the UserDatabaseConnection of the database
     *
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getCachedUserDatabaseConnection(String name);
    
    /**
     * Return a connection to the database
     *
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getConnection();
    
    /**
     * Return a connection to the database
     *
     * @param connectionName     name of a connection
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getConnection(String connectionName);
    
    /**
     * Return a connection based on connection context
     *
     * @param dcc           An DatanaseConnectionContext object
     * @return Connection
     */
    public UserDatabaseConnection getConnection(DatabaseConnectionContext dcc);
}
