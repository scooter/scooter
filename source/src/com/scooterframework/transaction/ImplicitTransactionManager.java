/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

/**
 * ImplicitTransactionManager interface specified services that are related 
 * to managing a transaction which is started implicitly within a service 
 * method. 
 * 
 * @author (Fei) John Chen
 */
public interface ImplicitTransactionManager {   
    /**
     * Begin a transaction implicitly.
     */
    public void beginTransactionImplicit();
    
    /**
     * Commit a transaction implicitly. 
     */
    public void commitTransactionImplicit();
    
    /**
     * Rollback a transaction implicitly. 
     */
    public void rollbackTransactionImplicit();
    
    /**
     * Release all resources hold by this transaction implicitly. 
     */
    public void releaseResourcesImplicit();
}
