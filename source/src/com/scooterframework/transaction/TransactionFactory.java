/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import javax.transaction.UserTransaction;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;

/**
 * TransactionFactory class creates Transaction instances of different types. 
 * 
 * @author (Fei) John Chen
 */
public class TransactionFactory {
    private static final TransactionFactory me = new TransactionFactory();

    private TransactionFactory() {
    }
    
    public static TransactionFactory getInstance() {
        return me;
    }
    
    public Transaction createTransaction() {
        return createTransaction(null);
    }
    
    public Transaction createTransaction(String type) {
        Transaction ts = null;
        boolean userTransactionAlreadyStarted = false;
        UserTransaction ut = null;
        
        if (type == null) {
        	try {
                ut = TransactionUtil.lookupUserTransaction();
        	}
        	catch(Exception ex) {
        		;
        	}
            
            if (TransactionUtil.isUserTransactionActive(ut)) {
                log.debug("UserTransaction has started: Use JtaTransaction");
                type = Transaction.JTA_TRANSACTION_TYPE;
                userTransactionAlreadyStarted = true;
            }
            else //ignore the UserTransaction
            {
                //check if there is any default connection name specified in property file
                type = DatabaseConfig.getInstance().getDefaultTransactionType();
                if (type == null || type.equals("")) {
                    log.debug("No default transaction type specified in " + 
                             "property file. Use JdbcTransaction as default.");
                    type = Transaction.JDBC_TRANSACTION_TYPE;
                }
            }
        }
        
        if (type.equalsIgnoreCase(Transaction.JDBC_TRANSACTION_TYPE)) {
            ts = new JdbcTransaction();
        }
        else if (type.equalsIgnoreCase(Transaction.JTA_TRANSACTION_TYPE)) {
            if (userTransactionAlreadyStarted) {
                ts = new JtaTransaction(ut);
            }
            else {
                ts = new JtaTransaction();
            }
        }
        else if (type.equalsIgnoreCase(Transaction.CMT_TRANSACTION_TYPE)) {
            ts = new CmtTransaction();
        }
        else {
            throw new TransactionException("TransactionFactory:createTransaction() failed. Type: " + type + ".");
        }
        
        return ts;
    }
    
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
