/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

/**
 * TransactionManagerUtil class creates one TransactionManager instance for
 * the whole application. 
 * 
 * @author (Fei) John Chen
 */
public class TransactionManagerUtil {
    //default TransactionManager
    private static final ThreadLocalTransactionManager tm;
    
    static {
        tm = new ThreadLocalTransactionManager();
    }

    private TransactionManagerUtil() {
    }
    
    
    public static TransactionManager getTransactionManager() {
        return (TransactionManager)tm;
    }
    
    public static ImplicitTransactionManager getImplicitTransactionManager() {
        return (ImplicitTransactionManager)tm;
    }
    
    public static TransactionStarterType getTransactionStarterType() {
        return (TransactionStarterType)tm;
    }
}
