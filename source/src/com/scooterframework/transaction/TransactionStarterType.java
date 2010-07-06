/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

/**
 * TransactionStarterType interface specified methods about transaction starter. 
 * 
 * @author (Fei) John Chen
 */
public interface TransactionStarterType {
    /**
     * value for automatically started transaction.
     */
    public static final String TRANSACTION_STARTER_IMPLICIT = "AUTO";
    
    /**
     * Return true if the transaction is started automatically, 
     *        false if the transaction is started by client calls 
     *        beginTransaciton() or beginTransaciton(type) explicitly.
     * 
     * @return true if auto started transaction.
     */
    public boolean isAutoTransaction();

    /**
     * Set a type of transaction starter. 
     */
    public String getTransactionStarterType();
    
    /**
     * Set a type of transaction starter. 
     */
    public void setTransactionStarterType(String type);
}
