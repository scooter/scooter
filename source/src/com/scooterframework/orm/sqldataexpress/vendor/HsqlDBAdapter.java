/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * HsqlDBAdapter class.
 * 
 * @author (Fei) John Chen
 */
public class HsqlDBAdapter extends DBAdapter {
	
	public String[] getCatalogAndSchema(String connName) {
		String s = getHsqldbSchema(connName);
        if (s != null) s = s.toUpperCase();
        String[] s2 = new String[2];
		s2[0] = null;
        s2[1] = s;
		return s2;
	}
    
	protected String getHsqldbSchema(String connName) {
		Properties p = SqlExpressUtil.getConnectionProperties(connName);
		String schema = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_SCHEMA);
		if (isEmpty(schema)) {
			if (useLoginAsSchema(connName)) {
				schema = getLoginUserId();
			}

			if (isEmpty(schema)) {
				String url = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_URL);
				if (url == null || url.indexOf('/') == -1)
					return null;

				int lastSlash = url.lastIndexOf('/');
				int q = url.indexOf('?');
				schema = (q == -1) ?
				url.substring(lastSlash + 1) : url.substring(lastSlash + 1, q);
			}
		}
		return schema;
	}
    
    public String getOneRowSelectSQL(String catalog, String schema, String table) {
    	String selectSQL = "SELECT TOP 1 * FROM ";
        return selectSQL + getExpandedTableName(catalog, schema, table);
    }
    
    public String getExpandedTableName(String catalog, String schema, String table) {
    	if (table.indexOf('.') != -1) return table;
    	String selectSQL = "";
    	if (isEmpty(catalog)) {
    		if (isEmpty(schema)) {
        		selectSQL = selectSQL + table;
    		}
    		else {
        		selectSQL = selectSQL + schema + "." + table;
    		}
    	}
    	else {
    		if (isEmpty(schema)) {
        		selectSQL = selectSQL + table;
    		}
    		else {
        		selectSQL = selectSQL + catalog + "." + schema + "." + table;
    		}
    	}
        return selectSQL;
    }
    
	public String preparePaginationSql(String selectSql, Map inputs, Map outputFilters) {
		if (selectSql == null) 
			throw new IllegalArgumentException("Input selectSql is null.");
		
		if (!selectSql.trim().toUpperCase().startsWith("SELECT")) {
			throw new IllegalArgumentException("Input selectSql must start with SELECT: " + selectSql);
		}
		
        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        boolean hasOffset = (offset > 0)?true:false;
        if (!hasOffset) {
        	offset = 0;
        }
        
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);
        if (limit == DataProcessor.NO_ROW_LIMIT) limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;
        
        StringBuffer newSelectSqlBF = new StringBuffer(selectSql.length());
        newSelectSqlBF.append("SELECT LIMIT");
        newSelectSqlBF.append(" ?").append(DataProcessor.input_key_records_offset).append(":INTEGER");
        inputs.put(DataProcessor.input_key_records_offset, new Integer(offset));
        newSelectSqlBF.append(" ?").append(DataProcessor.input_key_records_limit).append(":INTEGER");
        inputs.put(DataProcessor.input_key_records_limit, new Integer(limit));
        newSelectSqlBF.append(selectSql.substring(6));
        
        return newSelectSqlBF.toString();
    }
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
