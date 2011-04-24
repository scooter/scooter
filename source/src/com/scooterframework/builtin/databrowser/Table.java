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
    public static List<TableInfo> getTables(String connName) {
    	return getTables(connName, null, null);
    }
    
    public static List<TableInfo> getTables(String connName, String catalog, String schema) {
        String[] types = {"TABLE"};
        return getTables(connName, catalog, schema, types);
    }
    
    public static List<TableInfo> getTables(String connName, String catalog, String schema, String[] types) {
        Connection conn = null;
        List<TableInfo> tableInfos = new ArrayList<TableInfo>();
        try {
        	if (catalog == null || schema == null) {
            	String[] s2 = Database.getCatalogAndSchema(connName);
            	String _catalog = s2[0];
            	String _schema = s2[1];
            	if (catalog == null) {
            		catalog = _catalog;
            	} else {
            		if (_catalog != null && !catalog.equalsIgnoreCase(_catalog)) {
						throw new IllegalArgumentException(
								"Failed in "
										+ "getTables: the input catalog is '"
										+ catalog
										+ "', while the catalog derived from connName is '"
										+ _catalog + "'.");
					}
            	}
            	if (schema == null) {
            		schema = _schema;
            	} else {
            		if (_schema != null && !schema.equalsIgnoreCase(_schema)) {
						throw new IllegalArgumentException(
								"Failed in "
										+ "getTables: the input schema is '"
										+ schema
										+ "', while the schema derived from connName is '"
										+ _schema + "'.");
					}
            	}
        	}
        	
            conn = SqlExpressUtil.getReadonlyConnection(connName);
            List<TableInfo> tmp = SqlExpressUtil.getDatabaseTables(conn, catalog, schema, null, types, false);
            if (tmp != null) {
            	Iterator<TableInfo> it = tmp.iterator();
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
    	TableInfo ti = SqlExpressUtil.lookupTableInfo(connName, table);
    	return (ti != null)?ti.getHeader():null;
    }
    
    public static String getSafeTableName(String connName, String table) {
    	return SqlExpressUtil.getSafeTableName(connName, table);
    }
}
