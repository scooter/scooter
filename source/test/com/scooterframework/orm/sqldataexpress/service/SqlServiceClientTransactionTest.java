/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.test.ApplicationTest;
import com.scooterframework.transaction.TransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * SqlServiceClientTransactionTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class SqlServiceClientTransactionTest extends ApplicationTest {
	
	protected void setUp() {
		super.setUp();
	}
	
	public void test_retrieveTableDataBySQL() {
		String countSql = "SELECT count(*) FROM pets";
		
        TransactionManager tm =  TransactionManagerUtil.getTransactionManager();
        try{
        	tm.beginTransaction();
			
			Object countBeforeInsert = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countBeforeInsert", "13", countBeforeInsert.toString());
			
			Object nextID = getNextPetID();
			String sql = "INSERT INTO pets (id, name, type_id, owner_id) VALUES (?id, ?name, 1, 10)";
			Map inputs = new HashMap();
			inputs.put("id", nextID);
			inputs.put("name", "Lingling");
			int insertCount = SqlServiceClient.executeSQL(sql, inputs);
			assertEquals("number of rows inserted", 1, insertCount);
			
			String sql2 = "SELECT name FROM pets WHERE name = 'Lingling'";
			Object data = SqlServiceClient.retrieveObjectBySQL(sql2);
			assertEquals("name of the new pet", "Lingling", data.toString());
			
			Object countAfterInsert = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countAfterInsert", "14", countAfterInsert.toString());
			
			//artificially creating an exception 
			int i = 1;
			int j = 0;
			System.out.println("You should not see this line: " + i/j);
			
	    	tm.commitTransaction();
	    }
	    catch (Exception ex) {
	    	tm.rollbackTransaction();
			
			Object countAfterRollback = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countAfterRollback Lingling", "13", countAfterRollback.toString());
	    }
	    finally {
	    	tm.releaseResources();
	    }
		
		Object countTheEnd = SqlServiceClient.retrieveObjectBySQL(countSql);
		assertEquals("Total rows countTheEnd", "13", countTheEnd.toString());
	}
	
	public void test_transactional_executeSQL_inputs() {
		String countSql = "SELECT count(*) FROM pets";
		String tType = (String)CurrentThreadCache.get("key.TransactionStarterType");
		Object tr = CurrentThreadCache.get("key.Transactions");
		
        TransactionManager tm =  TransactionManagerUtil.getTransactionManager();
    	
        try{
        	tm.beginTransaction();
			
			Object countBeforeInsert = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countBeforeInsert", "13", countBeforeInsert.toString());

			Object nextID = getNextPetID();
			String sql = "INSERT INTO pets (id, name, type_id, owner_id) VALUES (?id, ?name, 1, 10)";
			Map inputs = new HashMap();
			inputs.put("id", nextID);
			inputs.put("name", "Pingping");
			int insertCount = SqlServiceClient.executeSQL(sql, inputs);
			assertEquals("number of rows inserted", 1, insertCount);
			
			String sql2 = "SELECT name FROM pets WHERE name = 'Pingping'";
			Object data = SqlServiceClient.retrieveObjectBySQL(sql2);
			assertEquals("name of the new pet", "Pingping", data.toString());
			
			Object countAfterInsert = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countAfterInsert", "14", countAfterInsert.toString());
			
			//artificially creating an exception 
			int i = 1;
			int j = 0;
			System.out.println("You should not see this line: " + i/j);
			
	    	tm.commitTransaction();
	    }
	    catch (Exception ex) {
	    	tm.rollbackTransaction();
			
			Object countAfterRollback = SqlServiceClient.retrieveObjectBySQL(countSql);
			assertEquals("Total rows countAfterRollback Pingping", "13", countAfterRollback.toString());
	    }
	    finally {
	    	tm.releaseResources();
	    }
		
		Object countTheEnd = SqlServiceClient.retrieveObjectBySQL(countSql);
		assertEquals("Total rows countTheEnd", "13", countTheEnd.toString());
	}
	
	private Object getNextPetID() {
		String findNextID = "SELECT (max(id)+1) FROM pets";
		return SqlServiceClient.retrieveObjectBySQL(findNextID);
	}
}
