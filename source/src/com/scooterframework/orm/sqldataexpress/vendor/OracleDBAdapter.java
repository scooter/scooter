/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConstants;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * OracleDBAdapter class applies to Oracle database.
 *
 * @author (Fei) John Chen
 */
public class OracleDBAdapter extends DBAdapter {

	@Override
	public String[] getCatalogAndSchema(String connName) {
		String s = getOracleSchema(connName);
        if (s != null) s = s.toUpperCase();
		String[] s2 = new String[2];
		s2[0] = null;
        s2[1] = s;
		return s2;
	}

	protected String getOracleSchema(String connName) {
		Properties p = SqlExpressUtil.getConnectionProperties(connName);
		String schema = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_SCHEMA);
		if (isEmpty(schema)) {
			if (useLoginAsSchema(connName)) {
				schema = getLoginUserId();
			}

			if (isEmpty(schema)) {
				schema = SqlExpressUtil.getConnectionUser(connName);
			}
		}
		return schema;
	}

    /**
     * Oracle does not use <tt>catalog</tt>. Therefore it is ignored.
     */
    @Override
    public String getExpandedTableName(String catalog, String schema, String tableName) {
    	String[] s3 = resolveCatalogAndSchemaAndTable(catalog, schema, tableName);
    	schema = s3[1];
    	String table = s3[2];

    	if (isEmpty(schema)) {
    		tableName = "\"" + table + "\"";
    	}
    	else {
    		tableName = schema + "." +
    			SqlExpressUtil.checkSpecialCharacterInTableName(table);
    	}
        return tableName;
    }

    @Override
    public String getOneRowSelectSQL(String catalog, String schema, String table) {
    	String selectSQL = "SELECT * FROM ";
    	selectSQL += getExpandedTableName(catalog, schema, table);
    	selectSQL += " WHERE ROWNUM = 1";
    	return selectSQL;
    }

    @Override
    public String preparePaginationSql(String selectSql, Map<String, Object> inputs, Map<String, String> outputFilters) {
        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        boolean hasOffset = (offset > 0)?true:false;
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);

        int maxRowIndex = limit;
        if (hasOffset) {
            maxRowIndex = limit + offset;
        }

        StringBuilder newSelectSqlBF = new StringBuilder(selectSql.length() + 150);

        if (hasOffset) {
            newSelectSqlBF.append("SELECT * FROM (SELECT /*+ FIRST_ROWS(").append(limit).append(") */ a.*, rownum rnum FROM ( ");
        }
        else {
            newSelectSqlBF.append("SELECT * FROM ( ");
        }
        newSelectSqlBF.append(selectSql);
        if (hasOffset) {
			newSelectSqlBF.append(" ) a WHERE rownum <= ?").append(DataProcessor.input_key_max_row_index).append(":INTEGER ) ");
            newSelectSqlBF.append("WHERE rnum > ?").append(DataProcessor.input_key_records_offset).append(":INTEGER");
            inputs.put(DataProcessor.input_key_max_row_index, Integer.valueOf(maxRowIndex));
            inputs.put(DataProcessor.input_key_records_offset, Integer.valueOf(offset));
		}
		else {
			newSelectSqlBF.append(" ) WHERE rownum <= ?").append(DataProcessor.input_key_max_row_index).append(":INTEGER");
            inputs.put(DataProcessor.input_key_max_row_index, Integer.valueOf(maxRowIndex));
		}

        if (outputFilters == null) outputFilters = new HashMap<String, String>();
        outputFilters.put(SqlServiceConstants.OUTPUT_FILTER_EXCEPT, "RNUM");

        return newSelectSqlBF.toString();
    }

    public Object getObjectFromResultSetByType(ResultSet rs, String javaClassType, int sqlDataType, int index)
    throws SQLException {
        if ("oracle.sql.BLOB".equals(javaClassType)) {
            try {
            	Blob blob = rs.getBlob(index);
                return getBlobData(blob);
            }
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else if ("oracle.sql.CLOB".equals(javaClassType)) {
            try {
            	Clob clob = rs.getClob(index);
                return getClobData(clob);
            }
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }

        return super.getObjectFromResultSetByType(rs, javaClassType, sqlDataType, index);
    }

    public Object getObjectFromStatementByType(CallableStatement cstmt, String javaClassType, int sqlDataType, int index)
    throws SQLException {
        Object theObj = null;

        if ("oracle.sql.BLOB".equals(javaClassType)) {
            try {
            	Blob blob = cstmt.getBlob(index);
                return getBlobData(blob);
            }
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else if ("oracle.sql.CLOB".equals(javaClassType) ) {
            try {
            	Clob clob = cstmt.getClob(index);
                return getClobData(clob);
            }
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }

        return theObj;
    }

    @Override
    public boolean vendorSpecificSetObject(PreparedStatement pstmt, Object obj, Parameter p, Map<String, Object> inputs)
    throws Exception {
        boolean status = false;

        if ("oracle.sql.BLOB".equals(p.getJavaClassName())) {
        	if (obj != null) {
	            InputStream is = getInputStream(obj);
	            pstmt.setBinaryStream(p.getIndex(), is, is.available());
	            is.close();
        	}
        	else {
        		pstmt.setBinaryStream(p.getIndex(), (InputStream)null, 0);
        	}

            status = true;
        }
        else if ("oracle.sql.CLOB".equals(p.getJavaClassName())) {
        	if (obj != null) {
	            String tmp = (String)obj;
	            int strLength = tmp.length();
	            StringReader r = new StringReader(tmp);
	            pstmt.setCharacterStream(p.getIndex(), r, strLength);
	            r.close();
        	}
        	else {
        		pstmt.setCharacterStream(p.getIndex(), (Reader)null, 0);
        	}

            status = true;
        }

        return status;
    }
}
