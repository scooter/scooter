/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.connection.DatabaseConnectionContext;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnectionFactory;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;

/**
 * AbstractTransactionImpl class has common methods. 
 * 
 * @author (Fei) John Chen
 */
abstract public class AbstractTransactionImpl implements Transaction {
    /**
     * Initialize JdbcTransaction
     */
    public AbstractTransactionImpl() {
        //Note: Do not initiate a default connection here as a request may 
        //      need its own special connection. Only initialize a connection 
        //      at request time. 
    }
    
    /**
     * Return transaction type
     */
    public String getTransactionType() {
        return transactionType;
    }
    
    /**
     * Release all resources managed by this transaction.
     */
    public void releaseResources() {
        if (bAllResourcesReleased) return;
        
        bTransactionHasEnded = true;
        
        try {
            Iterator<UserDatabaseConnection> it = connList.iterator();
            while(it.hasNext()) {
                UserDatabaseConnection udc = it.next();
                DAOUtil.closeConnection(udc.getConnection());
            }
            connList.clear();
            nameConnMap.clear();
            
            bAllResourcesReleased = true;
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in releaseResources()", ex);
        }
    }
    
    /**
     * Start a transaction. 
     */
    public void begin() {
        bTransactionHasStarted = true;
    }
    
    /**
     * Commit a transaction.
     */
    public void commit() {
        if (bTransactionHasCommitted) {
        	throw new TransactionException("Transaciton has already been committed.");
        }
        else {
        	bTransactionHasCommitted = true;
        }
    }
    
    /**
     * Rollback a transaction.
     */
    public void rollback() {
        if (bTransactionHasRollbacked) {
        	throw new TransactionException("Transaciton has already been rollbacked.");
        }
        else {
        	bTransactionHasRollbacked = true;
        }
    }
    
    /**
     * Check if transaction has started.
     */
    public boolean isTransactionStarted() {
        return bTransactionHasStarted;
    }
    
    /**
     * Check if transaction has ended.
     */
    public boolean isTransactionEnded() {
        return bTransactionHasEnded;
    }
    
    /**
     * Check if all resources have been released.
     */
    public boolean isAllResourcesReleased() {
        return bAllResourcesReleased;
    }
    
    /**
     * Return a connection to the database
     *
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getConnection() {
        String connectionName = DatabaseConfig.getInstance().getDefaultDatabaseConnectionName();
        return getConnection(connectionName);
    }
    
    /**
     * Return a connection to the database
     *
     * @param connectionName     name of a connection
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getConnection(String connectionName) {
        UserDatabaseConnection udc = getCachedUserDatabaseConnection(connectionName);
        
        //create a new UserDatabaseConnection
        if (udc == null) {
            udc = UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(connectionName);
            registerResource(udc.getConnectionName(), udc);
        }
        
        return udc;
    }
    
    /**
     * Return a connection based on connection context
     *
     * @param dcc a DatabaseConnectionContext instance
     * @return UserDatabaseConnection
     */
    public UserDatabaseConnection getConnection(DatabaseConnectionContext dcc) {
        if (dcc == null) throw new IllegalArgumentException("Input DatabaseConnectionContext instance is null.");
        
        String connectionName = dcc.getConnectionName();
        UserDatabaseConnection udc = getCachedUserDatabaseConnection(connectionName);
        
        //create a new UserDatabaseConnection
        if (udc == null) {
            udc = UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(dcc);
            registerResource(connectionName, udc);
        }
        
        return udc;
    }
    
    private UserDatabaseConnection getCachedUserDatabaseConnection(String name) {
        return (UserDatabaseConnection)nameConnMap.get(name);
    }

    /**
     * Register a resource which is to be managed by this transaction.
     */
    private void registerResource(String name, UserDatabaseConnection resource) {
        if (nameConnMap.containsKey(name)) return;
        
        nameConnMap.putIfAbsent(name, resource);
        connList.add(resource);
    }

    protected ConcurrentHashMap<String, UserDatabaseConnection> nameConnMap = new ConcurrentHashMap<String, UserDatabaseConnection>();
    protected List<UserDatabaseConnection> connList = new ArrayList<UserDatabaseConnection>();
    protected String transactionType = null;
    protected boolean bTransactionHasStarted = false;
    protected boolean bTransactionHasEnded = false;
    protected boolean bTransactionHasCommitted = false;
    protected boolean bTransactionHasRollbacked = false;
    protected boolean bAllResourcesReleased = false;
}
