/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * MySQLDBAdapter class applies to MySQL database.
 *
 * @author (Fei) John Chen
 */
public class MySQLDBAdapter extends DBAdapter {

	@Override
	public String[] getCatalogAndSchema(String connName) {
		String[] s2 = new String[2];
		s2[0] = getMySQLCatalog(connName);
		s2[1] = null;
		return s2;
	}

    protected static String getMySQLCatalog(String connName) {
    	Properties p = SqlExpressUtil.getConnectionProperties(connName);
    	String url = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_URL);
    	if (url == null || url.indexOf('/') == -1) return null;
    	int lastSlash = url.lastIndexOf('/');
    	int q = url.indexOf('?');
        return (q == -1)?
        	url.substring(lastSlash + 1):url.substring(lastSlash + 1, q);
    }

    /**
     * Override the super method because MySQL does not use schema.
     */
    @Override
    public String[] resolveCatalogAndSchemaAndTableFromTableName(String tableName) {
    	if (tableName == null)
    		throw new IllegalArgumentException("tableName cannot be null.");

    	String[] ss = tableName.split("\\.");

    	String[] s3 = new String[3];
    	if (ss.length == 3) {
    		throw new IllegalArgumentException("'tableName' cannot be of the "
    				+ "form {catalog}.{schema}.{table} for MySQL.");
    	} else if (ss.length == 2) {
    		s3[0] = ss[0];
    		s3[2] = ss[1];
    	} else if (ss.length == 1) {
    		s3[2] = ss[0];
    	}
    	return s3;
    }

    /**
     * MySQL does not use <tt>schema</tt>. Therefore it is ignored.
     */
    @Override
    public String getExpandedTableName(String catalog, String schema, String tableName) {
        return super.getExpandedTableName(catalog, IGNORE, tableName);
    }

    @Override
    public String getOneRowSelectSQL(String catalog, String schema, String tableName) {
    	String selectSQL = "SELECT * FROM ";
    	selectSQL += getExpandedTableName(catalog, schema, tableName);
    	selectSQL += " LIMIT 1";
    	return selectSQL;
    }

    @Override
	public String preparePaginationSql(String selectSql, Map<String, Object> inputs, Map<String, String> outputFilters) {
        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        boolean hasOffset = (offset > 0)?true:false;
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);
        if (limit == DataProcessor.NO_ROW_LIMIT) limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;

        StringBuilder newSelectSqlBF = new StringBuilder(selectSql.length());
        newSelectSqlBF.append(selectSql);
        newSelectSqlBF.append(" LIMIT ?").append(DataProcessor.input_key_records_limit).append(":INTEGER");
        inputs.put(DataProcessor.input_key_records_limit, Integer.valueOf(limit));

        if (hasOffset) {
            newSelectSqlBF.append(" OFFSET ?").append(DataProcessor.input_key_records_offset).append(":INTEGER");
            inputs.put(DataProcessor.input_key_records_offset, Integer.valueOf(offset));
        }

        return newSelectSqlBF.toString();
    }

    public Object getObjectFromResultSetByType(ResultSet rs, String javaClassType, int sqlDataType, int index)
    throws SQLException {
        Object theObj = null;

        if ("java.sql.Timestamp".equals(javaClassType) ||
            "java.sql.Date".equals(javaClassType) ||
            sqlDataType == 91 || sqlDataType == 93) {
            try {
                theObj = rs.getTimestamp(index);
                return theObj;
            }
            catch(SQLException ex) {
                String message = ex.getMessage();
                if ((message != null) && (
                    message.startsWith("Value '0000-00-00' can not be represented as java.sql.Timestamp") ||
                    (message.startsWith("Value '") && (message.indexOf("can not be represented as java.sql.Timestamp") != -1)))
                    ){
                    return null;
                }
                else {
                    throw ex;
                }
            }
        }

        return super.getObjectFromResultSetByType(rs, javaClassType, sqlDataType, index);
    }

    public Object getObjectFromStatementByType(CallableStatement cstmt, String javaClassType, int sqlDataType, int index)
    throws SQLException {
        Object theObj = null;

        if ("java.sql.Timestamp".equals(javaClassType) ||
            "java.sql.Date".equals(javaClassType) ||
            sqlDataType == 91 || sqlDataType == 93) {
            try {
                theObj = cstmt.getTimestamp(index);
                return theObj;
            }
            catch(SQLException ex) {
                String message = ex.getMessage();
                if ((message != null) && (
                    message.startsWith("Value '0000-00-00' can not be represented as java.sql.Timestamp") ||
                    (message.startsWith("Value '") && (message.indexOf("can not be represented as java.sql.Timestamp") != -1)))
                    ){
                    return null;
                }
                else {
                    throw ex;
                }
            }
        }

        return super.getObjectFromStatementByType(cstmt, javaClassType, sqlDataType, index);
    }
}
