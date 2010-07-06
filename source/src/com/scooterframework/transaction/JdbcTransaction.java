/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import java.util.Iterator;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;

/**
 * JdbcTransaction class
 * 
 * @author (Fei) John Chen
 */
public class JdbcTransaction extends AbstractTransactionImpl {
    /**
     * Initialize JdbcTransaction
     */
    public JdbcTransaction() {
        super();
        transactionType = Transaction.JDBC_TRANSACTION_TYPE;
    }
    
    /**
     * Start a transaction. 
     */
    public void begin() {
        try {
            super.begin();
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in begin()", ex);
        }
    }
    
    /**
     * Commit a transaction.
     */
    public void commit() {
        try {
            super.commit();
            
            Iterator it = connList.iterator();
            while(it.hasNext()) {
                UserDatabaseConnection udc = (UserDatabaseConnection)it.next();
                DAOUtil.commit(udc.getConnection());
            }
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in commit()", ex);
        }
    }
    
    /**
     * Rollback a transaction.
     */
    public void rollback() {
        try {
            super.rollback();
            
            Iterator it = connList.iterator();
            while(it.hasNext()) {
                UserDatabaseConnection udc = (UserDatabaseConnection)it.next();
                DAOUtil.rollback(udc.getConnection());
            }
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in rollback()", ex);
        }
    }
}
