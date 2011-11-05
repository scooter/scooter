/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.scooterframework.orm.sqldataexpress.exception.TransactionException;

/**
 * TransactionUtil class provides convenient methods about transaction. 
 * 
 * @author (Fei) John Chen
 */
public class TransactionUtil {
    public static boolean isUserTransactionActive(UserTransaction ut) {
        boolean utActive = false;
        try {
            if (ut != null && ut.getStatus() == Status.STATUS_ACTIVE) {
                utActive = true;
            }
        }
        catch (Exception ex) {
            utActive = false;
        }
        return utActive;
    }
    
    /**
     * Looks up or creates a <tt>UserTransaction</tt> instance. 
     * 
     * @throws TransactionException  if not found or not able to create a <tt>UserTransaction</tt> instance.
     * @return an instance of <tt>UserTransaction</tt>
     */
    public static UserTransaction lookupUserTransaction() {
        UserTransaction ut = null;
        InitialContext ctx = null;
        try {
        	ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup(Transaction.USER_TRANSACTION_JNDI_STRING);
        }
        catch(Exception ex) {
        	String transName = System.getProperty("jta.UserTransaction");
            try {
            	ut = (UserTransaction) ctx.lookup(transName);
            }
            catch(Exception ex2) {
                throw new TransactionException("Failed to create UserTransaction by using either " + 
                		Transaction.USER_TRANSACTION_JNDI_STRING + 
                        "or System.getProperty for jta.UserTransaction '" + 
                        transName + "'.", ex);
            }
        }

        return ut;
    }
}
