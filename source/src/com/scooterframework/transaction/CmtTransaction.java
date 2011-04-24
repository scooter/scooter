/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;

/**
 * CmtTransaction class allows container to control transaction. 
 * 
 * @author (Fei) John Chen
 */
public class CmtTransaction extends AbstractTransactionImpl {
    /**
     * Initialize CmtTransaction
     */
    public CmtTransaction() {
        super();
        transactionType = Transaction.CMT_TRANSACTION_TYPE;
    }
    
    /**
     * Start a transaction. 
     */
    public void begin() {
        super.begin();
    }
    
    /**
     * Commit a transaction. This is the end of the transaction. This method 
     * commits all JdbcConnections. 
     */
    public void commit() {
        try {
            super.commit();
            
            for (UserDatabaseConnection udc : connList) {
                if (UserDatabaseConnection.JDBC_CONNECTION.equals(udc.getConnectionType()))
                    DAOUtil.commit(udc.getConnection());
            }
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in commit()", ex);
        }
    }
    
    /**
     * Rollback a transaction. This method commits all JdbcConnections. 
     */
    public void rollback() {
        try {
            super.rollback();
            
            for (UserDatabaseConnection udc : connList) {
                if (UserDatabaseConnection.JDBC_CONNECTION.equals(udc.getConnectionType()))
                    DAOUtil.rollback(udc.getConnection());
            }
        }
        catch(Exception ex) {
            throw new TransactionException("eroror in rollback()", ex);
        }
    }
}
