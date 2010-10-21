/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import java.util.Iterator;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;

/**
 * JtaTransaction class
 * 
 * @author (Fei) John Chen
 */
public class JtaTransaction extends AbstractTransactionImpl {
    /**
     * Initialize JtaTransaction
     */
    public JtaTransaction() {
        super();
        transactionType = Transaction.JTA_TRANSACTION_TYPE;
        
        UserTransaction ut = createUserTransaction();
        this.ut = ut;
    }
    
    /**
     * Initialize JtaTransaction by injecting an UserTransaction instance. 
     */
    public JtaTransaction(UserTransaction ut) {
        super();
        
        if (ut == null)  throw new IllegalArgumentException("Failed to create JtaTransaction: UserTransaction is null.");
        transactionType = Transaction.JTA_TRANSACTION_TYPE;
        
        this.ut = ut;
        
        //notify the TransactionStarter that transaction is not started automatically. 
        if (TransactionUtil.isUserTransactionActive(ut)) {
            TransactionManagerUtil.getTransactionStarterType().setTransactionStarterType(null);
        }
    }
        
    /**
     * Start a transaction. 
     */
    public void begin() {
        try {
            super.begin();
            
            ut.begin();
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in begin()", ex);
        }
    }
    
    /**
     * Commit a transaction. This is the end of the transaction. This method 
     * also commits all JdbcConnections. 
     * All resources are released. When this method completes, the thread is 
     * no longer associated with a transaction. 
     */
    public void commit() {
        try {
            super.commit();
            
            Iterator it = connList.iterator();
            while(it.hasNext()) {
                UserDatabaseConnection udc = (UserDatabaseConnection)it.next();
                if (UserDatabaseConnection.JDBC_CONNECTION.equals(udc.getConnectionType()))
                    DAOUtil.commit(udc.getConnection());
            }
            
            ut.commit();
            
            bTransactionHasEnded = true;
            
            releaseResources();
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in commit()", ex);
        }
    }
    
    /**
     * Rollback a transaction. This method also rollback all JdbcConnections. 
     * All resources are released. When this method completes, the thread is 
     * no longer associated with a transaction. 
     */
    public void rollback() {
        try {
            super.rollback();
            
            Iterator it = connList.iterator();
            while(it.hasNext()) {
                UserDatabaseConnection udc = (UserDatabaseConnection)it.next();
                if (UserDatabaseConnection.JDBC_CONNECTION.equals(udc.getConnectionType()))
                    DAOUtil.rollback(udc.getConnection());
            }
            
            ut.rollback();
            
            releaseResources();
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in rollback()", ex);
        }
    }

    private UserTransaction createUserTransaction() {
        UserTransaction ut = null;
        
        try {
            InitialContext ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup(TransactionFactory.USER_TRANSACTION_JNDI_STRING);
        }
        catch(Exception ex) {
            throw new TransactionException("Failed to create UserTransaction by using " + 
            TransactionFactory.USER_TRANSACTION_JNDI_STRING + ".", ex);
        }

        return ut;
    }

    private UserTransaction ut;
}
