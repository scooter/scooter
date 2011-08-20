/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;

/**
 * ThreadLocalTransactionManager class implements TransactionStarterType, 
 * TransactionManager and ImplicitTransactionManager interfaces. 
 * 
 * @author (Fei) John Chen
 */
public class ThreadLocalTransactionManager 
implements TransactionStarterType, TransactionManager, ImplicitTransactionManager
{
    private static final String KEY_Transactions = "key.Transactions";
    private static final String KEY_TransactionStarterType = "key.TransactionStarterType";
    
    public ThreadLocalTransactionManager() {
    }
    
    /**
     * Begin a transaction.
     */
    public void beginTransaction() {
        beginTransaction(null);
    }
    
    /**
     * Begin a transaction of a specific type.
     */
    public void beginTransaction(String type) {
        Transaction tx = getTransaction();
        
        if (tx != null && tx.isTransactionStarted()) {
            throw new TransactionException("Failed in beginTransaction: transaction already started.");
        }
        else if (tx == null) {
            tx = TransactionFactory.getInstance().createTransaction(type);
            setTransaction(tx);
        }
        
        if (!tx.isTransactionStarted()) tx.begin();
    }
    
    /**
     * Commit a transaction. 
     */
    public void commitTransaction() {
        Transaction tx = getTransaction();
        if (tx != null) tx.commit();
    }
    
    /**
     * Rollback a transaction. 
     */
    public void rollbackTransaction() {
        Transaction tx = getTransaction();
        if (tx != null) tx.rollback();
    }
    
    /**
     * Release all resources hold by this transaction. 
     */
    public void releaseResources() {
        Transaction tx = getTransaction();
        if (tx != null) {
            tx.releaseResources();
            tx = null;
            setTransaction(tx);
        }
    }
    
    /**
     * Returns a Transaction instance associated with current thread.
     * 
     * @return Transaction
     */
    public Transaction getTransaction() {
        return (Transaction)CurrentThreadCache.get(KEY_Transactions);
    }
    
    /**
     * Set a Transaction instance associated with the current thread.
     */
    public void setTransaction(Transaction tx) {
        CurrentThreadCache.set(KEY_Transactions, tx);
    }
    
    /**
     * Return true if the transaction is started automatically, 
     *        false if the transaction is started by client calls 
     *        beginTransaciton() or beginTransaciton(type) explicitly.
     * 
     * @return true if auto started transaction, false otherwise.
     */
    public boolean isAutoTransaction() {
        boolean autoTx = false;
        if (TRANSACTION_STARTER_IMPLICIT.equals(getTransactionStarterType())) {
            autoTx = true;
        }
        return autoTx;
    }

    /**
     * Set a type of transaction starter. 
     */
    public String getTransactionStarterType() {
        return (String)CurrentThreadCache.get(KEY_TransactionStarterType);
    }
    
    /**
     * Set a type of transaction starter. 
     */
    public void setTransactionStarterType(String type) {
        CurrentThreadCache.set(KEY_TransactionStarterType, type);
    }
    
    /**
     * Begin a transaction implicitly.
     */
    public void beginTransactionImplicit() {
        if (getTransaction() == null) {
            beginTransaction();
            setTransactionStarterType(TRANSACTION_STARTER_IMPLICIT);
        }
    }
    
    /**
     * Commit a transaction implicitly. 
     */
    public void commitTransactionImplicit() {
        if (isAutoTransaction()) commitTransaction();
    }
    
    /**
     * Rollback a transaction implicitly. 
     */
    public void rollbackTransactionImplicit() {
        if (isAutoTransaction()) rollbackTransaction();
    }
    
    /**
     * Release all resources hold by this transaction implicitly. 
     */
    public void releaseResourcesImplicit() {
        if (isAutoTransaction()) {
            setTransactionStarterType(null);
            releaseResources();
        }
    }
}
