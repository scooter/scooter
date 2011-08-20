/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;


/**
 * <p>
 * SqlService interface specified services that are called by clients.</p>
 * 
 * <p>
 * A SqlService instance is a thread-safe service object. Only one such 
 * instance is needed for an application. The SqlService object has a 
 * TransactionManager object. The TransactionManager object is responsible 
 * for managing the transaction associated with the current thread. This 
 * transaction instance can be automatically started if it has not been started 
 * externally. </p>
 * 
 * <p>
 * You cannot start a new transaction when the current transaction is still 
 * active. In other words, you have to end a transaction, either through 
 * <tt>commitTransaction()</tt> or through <tt>rollbackTransaction()</tt>, 
 * before you can call <tt>beginTransaction()</tt>. Therefore, you should use
 * <tt>beginTransaction()</tt> in the client code, not in service methods. </p>
 * 
 * <p>If you expect your service methods or utility methods which may be 
 * invoked by other methods or transactions, you should use implicit 
 * transaction in the service or utility methods. See Example 5 below for
 *  details.</p>
 * 
 * <p>
 * If a transaction is not started, a default implicit transaction will be 
 * started when any service method is called. The type of default transaction 
 * is specified in property file. If the type is not specified, 
 * JdbcTransaction will be used as default. </p>
 * 
 * <p>
 * If a transaction has already started, no implicit transaction will be 
 * started.</p>
 * 
 * <pre>
 * Examples:
 * 
 * Example 1: Start a new transaction explicitly
 * 
 *      SqlService sqlSvc = SqlServiceConfig.getSqlService();
 *      try{
 *          sqlSvc.beginTransaction();//start transaction explicitly
 *          
 *          sqlSvc.update(..);
 *          sqlSvc.update(..);
 *          
 *          sqlSvc.commitTransaction();
 *      }
 *      catch (Exception ex) {
 *          sqlSvc.rollbackTransaction();
 *      }
 *      finally {
 *          sqlSvc.releaseResources();
 *      }
 * 
 * 
 * Example 2: Start a new transaction explicitly by using TransactionManager
 * 
 *      TransactionManager tm =  TransactionManagerUtil.getTransactionManager();
 *      try{
 *          tm.beginTransaction();//start transaction explicitly
 *          
 *          sqlSvc.update(..);
 *          sqlSvc.update(..);
 *          
 *          tm.commitTransaction();
 *      }
 *      catch (Exception ex) {
 *          tm.rollbackTransaction();
 *      }
 *      finally {
 *          tm.releaseResources();
 *      }
 * 
 * 
 * Example 3: Start a new transaction explicitly by using UserTransaction
 * 
 *      UserTransaction utx = ...;
 *      try{
 *          utx.beginTransaction();//start transaction explicitly
 *          
 *          sqlSvc.update(..);
 *          sqlSvc.update(..);
 *          
 *          utx.commitTransaction();
 *      }
 *      catch (Exception ex) {
 *          utx.rollbackTransaction();
 *      }
 * 
 * 
 * Example 4: Start a new transaction automatically when a service method is called
 * 
 *      sqlSvc.update(..); //automatically start a transaction. 
 *      sqlSvc.insert(..); //automatically start another transaction. 
 * 
 * 
 * Example 5: Start a new implicit transaction by using ImplicitTransactionManager
 *  //We use implicit transaction here because we expect <tt>transfer()</tt> 
 *  //be used in other transaction context.
 * 	public void transfer(...) {
 *      ImplicitTransactionManager itm = TransactionManagerUtil.getImplicitTransactionManager();
 *      try{
 *          itm.beginTransactionImplicit();//start transaction implicitly
 *          
 *          sqlSvc.withdraw(..);
 *          sqlSvc.deposit(..);
 *          
 *          itm.commitTransactionImplicit();
 *      }
 *      catch (Exception ex) {
 *          itm.rollbackTransactionImplicit();
 *      }
 *      finally {
 *          itm.releaseResourcesImplicit();
 *      }
 *  }
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public interface SqlService extends SqlServiceGeneric, SqlServiceSpecific, SqlServiceTransactionManager
{
}
