/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.scooterframework.common.exception.GenericException;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * Table model class handles table related access.
 * 
 * @author (Fei) John Chen
 */
public class Table {
    public static List getTables(String connName) {
    	String[] s2 = Database.getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getTables(connName, catalog, schema);
    }
    
    public static List getTables(String connName, String catalog, String schema) {
        String[] types = {"TABLE"};
        return getTables(connName, catalog, schema, types);
    }
    
    public static List getTables(String connName, String catalog, String schema, String[] types) {
        Connection conn = null;
        List tableInfos = new ArrayList();
        try {
            conn = SqlExpressUtil.getReadonlyConnection(connName);
            List tmp = SqlExpressUtil.getDatabaseTables(conn, catalog, schema, null, types);
            if (tmp != null) {
            	Iterator it = tmp.iterator();
            	while(it.hasNext()) {
            		TableInfo ti = (TableInfo)it.next();
            		if (!SqlExpressUtil.isNiceDBString(ti.getName())) continue;
            		tableInfos.add(ti);
            	}
            }
        }
        catch(Exception ex) {
            throw new GenericException(ex);
        }
        finally {
            DAOUtil.closeConnection(conn);
        }
        return tableInfos;
    }
    
    public static String countRecords(String connName, String table) {
        Object result = SqlExpressUtil.countTotalRecords(connName, table);
        return (result != null)?result.toString():"0";
    }
    
    public static RowInfo getTableHeaderInfo(String connName, String table) {
    	TableInfo ti = SqlExpressUtil.lookupAndRegisterTable(connName, table);
    	return (ti != null)?ti.getHeader():null;
    }
    
    public static String getSafeTableName(String connName, String table) {
    	return SqlExpressUtil.getSafeTableName(connName, table);
    }
}
