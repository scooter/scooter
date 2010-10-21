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

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConstants;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * OracleDBAdapter class.
 * 
 * @author (Fei) John Chen
 */
public class OracleDBAdapter extends DBAdapter {
	
	public String[] getCatalogAndSchema(String connName) {
		String s = SqlExpressUtil.getConnectionUser(connName);
        if (s != null) s = s.toUpperCase();
		String[] s2 = new String[2];
		s2[0] = null;
        s2[1] = s;
		return s2;
	}
    
    public String getOneRowSelectSQL(String catalog, String schema, String table) {
    	String selectSQL = "SELECT * FROM ";
    	selectSQL += getExpandedTableName(catalog, schema, table);
    	selectSQL += " WHERE ROWNUM = 1";
    	return selectSQL;
    }
    
    public String getExpandedTableName(String catalog, String schema, String table) {
    	if (table.indexOf('.') != -1) return table;
    	String selectSQL = "";
    	if (isEmpty(schema)) {
    		selectSQL = selectSQL + "\"" + table + "\"";
    	}
    	else {
    		selectSQL = selectSQL + schema + "." + 
    			SqlExpressUtil.checkSpecialCharacterInTableName(table);
    	}
        return selectSQL;
    }
    
    public String preparePaginationSql(String selectSql, Map inputs, Map outputFilters) {
        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        boolean hasOffset = (offset > 0)?true:false;
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);
        
        int maxRowIndex = limit;
        if (hasOffset) {
            maxRowIndex = limit + offset;
        }
        
        StringBuffer newSelectSqlBF = new StringBuffer(selectSql.length() + 150);
                
        if (hasOffset) {
            newSelectSqlBF.append("SELECT * FROM (SELECT a.*, rownum rnum FROM ( ");
        }
        else {
            newSelectSqlBF.append("SELECT * FROM ( ");
        }
        newSelectSqlBF.append(selectSql);
        if (hasOffset) {
			newSelectSqlBF.append(" ) a WHERE rownum <= ?").append(DataProcessor.input_key_max_row_index).append(":INTEGER ) ");
            newSelectSqlBF.append("WHERE rnum > ?").append(DataProcessor.input_key_records_offset).append(":INTEGER");
            inputs.put(DataProcessor.input_key_max_row_index, new Integer(maxRowIndex));
            inputs.put(DataProcessor.input_key_records_offset, new Integer(offset));
		}
		else {
			newSelectSqlBF.append(" ) WHERE rownum <= ?").append(DataProcessor.input_key_max_row_index).append(":INTEGER");
            inputs.put(DataProcessor.input_key_max_row_index, new Integer(maxRowIndex));
		}
        
        if (outputFilters == null) outputFilters = new HashMap();
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
    
    public boolean vendorSpecificSetObject(PreparedStatement pstmt, Object obj, Parameter p, Map inputs)
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
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
