/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConstants;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * SQLServerDBAdapter class applies to Microsoft's SQL Server database.
 * 
 * @author (Fei) John Chen
 */
public class SQLServerDBAdapter extends DBAdapter {
	
	@Override
	public String[] getCatalogAndSchema(String connName) {
		String[] s2 = new String[2];
		s2[0] = getSQLServerCatalog(connName);
		s2[1] = getSQLServerSchema(connName);
		return s2;
	}
    
    protected static String getSQLServerCatalog(String connName) {
    	Properties p = SqlExpressUtil.getConnectionProperties(connName);
    	String url = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_URL);
    	if (url == null || url.indexOf('/') == -1) return null;
		
		String s = "";
		String urlLC = url.toLowerCase();
		int i = urlLC.indexOf("/databases/");
		if (i == -1) {
			int j = urlLC.indexOf("databasename=");
			if (j == -1) {
				throw new IllegalArgumentException("url '" + url + 
						"' does not follow required format. See Scooter doc on SQL Server db url format.");
			}
			urlLC = url.substring(j + 13);
			int sc = urlLC.indexOf(';');
			if (sc != -1) {
				s = urlLC.substring(0, sc);
			}
			else {
				s = urlLC;
			}
		}
		else {
			urlLC = url.substring(i + 11);
			int sc = urlLC.indexOf(':');
			if (sc != -1) {
				s = urlLC.substring(0, sc);
			}
			else {
				s = urlLC;
			}
		}
		
		return s;
    }
    
	protected String getSQLServerSchema(String connName) {
		Properties p = SqlExpressUtil.getConnectionProperties(connName);
		String schema = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_SCHEMA);
		if (isEmpty(schema)) {
			if (useLoginAsSchema(connName)) {
				schema = getLoginUserId();
			}

			if (isEmpty(schema)) {
				schema = null;
			}
		}
		return schema;
	}
    
	@Override
    public String getOneRowSelectSQL(String catalog, String schema, String tableName) {
    	String selectSQL = "SELECT TOP 1 * FROM ";
    	selectSQL += getExpandedTableName(catalog, schema, tableName);
    	return selectSQL;
    }
    
	@Override
	public String preparePaginationSql(String selectSql, Map<String, Object> inputs, Map<String, String> outputFilters) {
        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);
        if (limit == DataProcessor.NO_ROW_LIMIT) limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;
        
        String orderByClause = getOrderByClause(selectSql, inputs);
        String remainSQL = getRemainSQL(selectSql);
        
        StringBuilder newSqlB = new StringBuilder(selectSql.length());
        newSqlB.append("SELECT TOP ").append(limit).append(" * FROM (");
        newSqlB.append("SELECT ROW_NUMBER() OVER (ORDER BY ");
        newSqlB.append(orderByClause).append(") AS rownumber, ");
        newSqlB.append(remainSQL).append(") AS result WHERE rownumber > ");
        newSqlB.append(offset);
        
        if (outputFilters == null) outputFilters = new HashMap<String, String>();
        outputFilters.put(SqlServiceConstants.OUTPUT_FILTER_EXCEPT, "rownumber");
        
        return newSqlB.toString();
    }
}
