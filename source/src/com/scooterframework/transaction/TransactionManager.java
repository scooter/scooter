/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

/**
 * TransactionManager interface specified services that are related 
 * to managing a transaction.
 * 
 * @author (Fei) John Chen
 */
public interface TransactionManager {
    /**
     * Return a Transaction instance associated with the current thread.
     * 
     * @return Transaction
     */
    public Transaction getTransaction();
    
    /**
     * Set a Transaction instance associated with the current thread.
     */
    public void setTransaction(Transaction tx);
    
    /**
     * Begin a transaction.
     */
    public void beginTransaction();
    
    /**
     * Begin a transaction of a specific type.
     */
    public void beginTransaction(String type);
    
    /**
     * Commit a transaction. 
     */
    public void commitTransaction();
    
    /**
     * Rollback a transaction. 
     */
    public void rollbackTransaction();
    
    /**
     * Release all resources hold by this transaction. 
     */
    public void releaseResources();
}
