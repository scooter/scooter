/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.Cursor;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConstants;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;

/**
 * DataProcessorImpl class.
 * 
 * @author (Fei) John Chen
 */
abstract public class DataProcessorImpl implements DataProcessor {

    protected DatabaseMetaData dbmd;
    private boolean hasCheckedSupportsGetGeneratedKeys;
    private boolean supportsGetGeneratedKeys;
    
    /**
     * execute
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs) 
    throws BaseSQLException {
        return execute(udc, inputs, null);
    }
    
    /**
     * execute with output filter
     */
    abstract public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs, Map<String, String> outputFilters) 
    throws BaseSQLException;
    
    /**
     * Returns DatabaseMetaData.
     * @return an instance of DatabaseMetaData.
     */
    public DatabaseMetaData getDatabaseMetaData() {
        return dbmd;
    }
    
    /**
     * Sets DatabaseMetaData.
     * @param dbmd
     */
    public void setDatabaseMetaData(DatabaseMetaData dbmd) {
        this.dbmd = dbmd;
    }
    
    /**
     * Checks to see if the connection supports generated keys.
     * @return true if supports.
     */
    protected boolean supportsGetGeneratedKeys() {
    	if (hasCheckedSupportsGetGeneratedKeys) return supportsGetGeneratedKeys;
    	
        try {
        	supportsGetGeneratedKeys = dbmd.supportsGetGeneratedKeys();
            hasCheckedSupportsGetGeneratedKeys = true;
        }
        catch(Exception ex) {
            ;
        }
        return supportsGetGeneratedKeys;
    }
    
    protected Set<String> getAllowedColumns(Map<String, String> outputFilter, Cursor cursor) {
    	Set<String> allowedColumns = new HashSet<String>();
        if (outputFilter == null) outputFilter = new HashMap<String, String>();
        
        Set<String> exceptColumns = null;
        String exceptColumnsStr = outputFilter.get(SqlServiceConstants.OUTPUT_FILTER_EXCEPT);
        if (exceptColumnsStr != null && exceptColumnsStr.trim().length() > 0)
        	exceptColumns = Converters.convertStringToSet(exceptColumnsStr.toUpperCase());
        
        Set<String> onlyColumns = null;
        String onlyColumnsStr = outputFilter.get(SqlServiceConstants.OUTPUT_FILTER_ONLY);
        if (onlyColumnsStr != null && onlyColumnsStr.trim().length() > 0)
        	onlyColumns = Converters.convertStringToSet(onlyColumnsStr.toUpperCase());
        
        int columnWidth = cursor.getDimension();
        for (int i = 0; i < columnWidth; i++) {
            ColumnInfo ci = cursor.getColumnInfo(i);
            String columnName = ci.getColumnName();
            if ((exceptColumns != null && exceptColumns.contains(columnName)) || 
                (onlyColumns != null && !onlyColumns.contains(columnName))) {
            	continue;
            }
            allowedColumns.add(columnName);
        }
    	return allowedColumns;
    }
                    
    // add those column names in the outputs to a new string array. 
    protected RowInfo getFilteredHeaderInfo(Set<String> allowedColumns, Cursor cursor) {
        RowInfo header = new RowInfo();
        int columnWidth = cursor.getDimension();
        List<ColumnInfo> allowedColumnInfos = new ArrayList<ColumnInfo>();
        
        for (int i = 0; i < columnWidth; i++) {
            ColumnInfo ci = cursor.getColumnInfo(i);
            if (allowedColumns.contains(ci.getColumnName())) {
            	allowedColumnInfos.add(ci);
            }
        }
        
        header.setColumnInfoList(allowedColumnInfos);
        return header;
    }
    
    protected void setNull(PreparedStatement pstmt, int parameterIndex, int targetSqlType) 
    throws SQLException {
		if (Parameter.UNKNOWN_SQL_DATA_TYPE != targetSqlType) {
            pstmt.setNull(parameterIndex, targetSqlType);
        }
        else {
            pstmt.setNull(parameterIndex, java.sql.Types.OTHER);
        }
	}
    
    protected void setObject(PreparedStatement pstmt, Object obj, Parameter p) 
    throws SQLException {
    	int parameterIndex = p.getIndex();
    	int targetSqlType = p.getSqlDataType();
		if (obj == null) {
            setNull(pstmt, parameterIndex, targetSqlType);
        }
        else {
            if (targetSqlType == Types.DATE ||
                targetSqlType == Types.TIME ||
                targetSqlType == Types.TIMESTAMP) {
                //This is date/time type.
                setDateTimeObject(pstmt, parameterIndex, obj, targetSqlType);
            }
            else if (targetSqlType == Types.BOOLEAN) {
                setBooleanObject(pstmt, parameterIndex, obj, targetSqlType);
            }
            else if (targetSqlType == Types.BIT ||
                     targetSqlType == Types.TINYINT ||
                     targetSqlType == Types.SMALLINT ||
                     targetSqlType == Types.INTEGER ||
                     targetSqlType == Types.BIGINT ||
                     targetSqlType == Types.REAL ||
                     targetSqlType == Types.FLOAT ||
                     targetSqlType == Types.DOUBLE ||
                     targetSqlType == Types.DECIMAL ||
                     targetSqlType == Types.NUMERIC
                     ) {
                setNumericObject(pstmt, obj, p);
            }
            else if (targetSqlType == Types.BINARY ||
                     targetSqlType == Types.VARBINARY ||
                     targetSqlType == Types.LONGVARBINARY
                     ) {
                setBinaryObject(pstmt, obj, p);
            }
            else if (targetSqlType == Types.BLOB ||
                     targetSqlType == Types.CLOB
                    ) {
               setBigData(pstmt, obj, p);
           }
            else {
                //delegate to the underlying jdbc-driver implementation
                pstmt.setObject(parameterIndex, obj, targetSqlType);
            }
        }
    }
    
    protected void setBooleanObject(PreparedStatement pstmt, int parameterIndex, 
                                    Object parameterObj, int targetSqlType) 
    throws SQLException {
        if (parameterObj instanceof Boolean) {
            pstmt.setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
        } else if (parameterObj instanceof String) {
            pstmt.setBoolean(parameterIndex, "true".equalsIgnoreCase((String)parameterObj) ||
                                             "t".equalsIgnoreCase((String)parameterObj) ||
                                             !"0".equalsIgnoreCase((String)parameterObj) ||
                                             "y".equalsIgnoreCase((String)parameterObj) ||
                                             "yes".equalsIgnoreCase((String)parameterObj));
        } else if (parameterObj instanceof Number) {
            int intValue = ((Number)parameterObj).intValue();
            pstmt.setBoolean(parameterIndex, intValue != 0);
        } else {
            throw new SQLException("Cannot convert from " + parameterObj.getClass().getName() + 
                    " to BOOLEAN for object " + parameterObj + 
                    " at index " + parameterIndex + ".");
        }
    }
    
    protected void setNumericObject(PreparedStatement pstmt, Object obj, Parameter p) 
    throws SQLException {
    	int parameterIndex = p.getIndex();
    	int targetSqlType = p.getSqlDataType();
    	if (targetSqlType == Types.TINYINT || 
    		targetSqlType == Types.SMALLINT ||
    		targetSqlType == Types.INTEGER) {
    		int x = convert2int(obj, p);
    		pstmt.setInt(parameterIndex, x);
    	}
    	else if (targetSqlType == Types.BIGINT) {
    		long x = convert2long(obj, p);
    		pstmt.setLong(parameterIndex, x);
    	}
    	else if (targetSqlType == Types.FLOAT) {
    		float x = convert2float(obj, p);
    		pstmt.setFloat(parameterIndex, x);
    	}
    	else if (targetSqlType == Types.DOUBLE || targetSqlType == Types.REAL) {
    		double x = convert2double(obj, p);
    		pstmt.setDouble(parameterIndex, x);
    	}
    	else if (targetSqlType == Types.DECIMAL) {
    		BigDecimal x = convert2BigDecimal(obj, p);
    		pstmt.setBigDecimal(parameterIndex, x);
    	}
    	else {
    		pstmt.setObject(parameterIndex, obj, targetSqlType);
    	}
    }
    
    /**
     * Stores object as binary type in database.
     * 
     * Acceptable types of <tt>obj</tt> are <tt>java.io.InputStream</tt>, 
     * <tt>byte[]</tt>, <tt>java.io.File</tt>, <tt>java.lang.String</tt> and 
     * any object whose content can be obtained from its 
     * <tt>toString()</tt> method.
     * 
     * @param pstmt PreparedStatement statement
     * @param obj   The data to be persisted
     * @param p		Parameter instance
     * @throws SQLException
     */
    protected void setBinaryObject(PreparedStatement pstmt, Object obj, Parameter p) 
    throws SQLException {
        try {
        	if (obj != null) {
	            InputStream is = getInputStream(obj);
	            pstmt.setBinaryStream(p.getIndex(), is, is.available());
	            is.close();
        	}
        	else {
        		pstmt.setBinaryStream(p.getIndex(), (InputStream)null, 0);
        	}
    	}
    	catch(Exception ex) {
    		throw new SQLException(ex.getMessage());
    	}
    }
    
    /**
     * Stores object as either blob or clob type in database.
     * 
     * Acceptable types of <tt>obj</tt> are <tt>java.io.InputStream</tt>, 
     * <tt>byte[]</tt>, <tt>java.io.File</tt>, <tt>java.lang.String</tt> and 
     * any object whose content can be obtained from its 
     * <tt>toString()</tt> method.
     * 
     * @param pstmt PreparedStatement statement
     * @param obj   The data to be persisted
     * @param p		Parameter instance
     * @throws SQLException
     */
    protected void setBigData(PreparedStatement pstmt, Object obj, Parameter p) 
    throws SQLException {
    	try {
	    	int targetSqlType = p.getSqlDataType();
	    	if (targetSqlType == Types.BLOB) {
	        	if (obj != null) {
		            InputStream is = getInputStream(obj);
		            pstmt.setBinaryStream(p.getIndex(), is, is.available());
		            is.close();
	        	}
	        	else {
	        		pstmt.setBinaryStream(p.getIndex(), (InputStream)null, 0);
	        	}
	        }
	        else if (targetSqlType == Types.CLOB) {
	        	if (obj != null) {
		            String tmp = (String)obj;
		            int strLength = tmp.length();
		            Reader r = new StringReader(tmp);
		            pstmt.setCharacterStream(p.getIndex(), r, strLength);
		            r.close();
	        	}
	        	else {
	        		pstmt.setCharacterStream(p.getIndex(), (Reader)null, 0);
	        	}
	        }
    	}
    	catch(Exception ex) {
    		throw new SQLException(ex.getMessage());
    	}
    }
    
    protected InputStream getInputStream(Object data) throws FileNotFoundException {
    	InputStream is = null;
    	if (data != null) {
    		if (data instanceof InputStream) {
    			is = (InputStream)data;
    		}
    		else if (data instanceof byte[]) {
    			is = new ByteArrayInputStream((byte[])data);
    		}
    		else if (data instanceof File) {
    			is = new FileInputStream((File)data);
    		}
    		else if (data instanceof String) {
    			is = new ByteArrayInputStream(((String)data).getBytes());
    		}
    		else {
    			is = new ByteArrayInputStream((data.toString()).getBytes());
    		}
    	}
    	return is;
    }
    
    protected int convert2int(Object obj, Parameter p) {
    	Integer i = null;
    	try {
    		i = Integer.valueOf(obj.toString());
    	}
    	catch(Exception ex) {
    		throw new IllegalArgumentException("Failed to convert object of " + 
    				"value \"" + obj + "\" to Integer for parameter \"" + 
    				p + "\".");
    	}
    	
    	return i.intValue();
    }
    
    protected long convert2long(Object obj, Parameter p) {
    	Long l = null;
    	try {
    		l = new Long(obj.toString());
    	}
    	catch(Exception ex) {
    		throw new IllegalArgumentException("Failed to convert object of " + 
    				"value \"" + obj + "\" to Long for parameter \"" + 
    				p + "\".");
    	}
    	
    	return l.longValue();
    }
    
    protected float convert2float(Object obj, Parameter p) {
    	Float f = null;
    	try {
    		f = new Float(obj.toString());
    	}
    	catch(Exception ex) {
    		throw new IllegalArgumentException("Failed to convert object of " + 
    				"value \"" + obj + "\" to Float for parameter \"" + 
    				p + "\".");
    	}
    	
    	return f.floatValue();
    }
    
    protected double convert2double(Object obj, Parameter p) {
    	Double d = null;
    	try {
    		d = new Double(obj.toString());
    	}
    	catch(Exception ex) {
    		throw new IllegalArgumentException("Failed to convert object of " + 
    				"value \"" + obj + "\" to Double for parameter \"" + 
    				p + "\".");
    	}
    	
    	return d.doubleValue();
    }
    
    protected BigDecimal convert2BigDecimal(Object obj, Parameter p) {
    	BigDecimal b = null;
    	try {
    		b = new BigDecimal(obj.toString());
    	}
    	catch(Exception ex) {
    		throw new IllegalArgumentException("Failed to convert object of " + 
    				"value \"" + obj + "\" to BigDecimal for parameter \"" + 
    				p + "\".");
    	}
    	
    	return b;
    }
    
    protected void setDateTimeObject(PreparedStatement pstmt, int parameterIndex, 
                                     Object parameterObj, int targetSqlType) 
    throws SQLException {
        try {
            switch (targetSqlType) {
            case Types.DATE:
                java.sql.Date parameterAsSQLDate = null;
                if (parameterObj instanceof String) {
                    parameterAsSQLDate = SqlUtil.convertStringToSQLDate((String)parameterObj);
                }
                else if (parameterObj instanceof java.sql.Date) {
                    parameterAsSQLDate = (java.sql.Date) parameterObj;
                }
                else if (parameterObj instanceof java.util.Date) {//This includes Time, Timestamp cases.
                    parameterAsSQLDate = new java.sql.Date(((java.util.Date)parameterObj).getTime());
                }
                else {
                    parameterAsSQLDate = SqlUtil.convertStringToSQLDate(parameterObj.toString());
                }
                
                pstmt.setDate(parameterIndex, parameterAsSQLDate);
                
                break;
            case Types.TIMESTAMP:
                java.sql.Timestamp parameterAsSQLTimestamp = null;
                if (parameterObj instanceof String) {
                    parameterAsSQLTimestamp = SqlUtil.convertStringToSQLTimestamp((String)parameterObj);
                }
                else if (parameterObj instanceof java.sql.Timestamp) {
                    parameterAsSQLTimestamp = (java.sql.Timestamp) parameterObj;
                }
                else if (parameterObj instanceof java.util.Date) {//This includes Date, Time cases.
                    parameterAsSQLTimestamp = new java.sql.Timestamp(((java.util.Date)parameterObj).getTime());
                }
                else {
                    parameterAsSQLTimestamp = SqlUtil.convertStringToSQLTimestamp(parameterObj.toString());
                }
                
                pstmt.setTimestamp(parameterIndex, parameterAsSQLTimestamp);
                
                break;
            case Types.TIME:
                java.sql.Time parameterAsSQLTime = null;
                if (parameterObj instanceof String) {
                    parameterAsSQLTime = SqlUtil.convertStringToSQLTime((String)parameterObj);
                }
                else if (parameterObj instanceof java.sql.Time) {
                    parameterAsSQLTime = (java.sql.Time) parameterObj;
                }
                else if (parameterObj instanceof java.util.Date) {//This includes Date, Timestamp cases.
                    parameterAsSQLTime = new java.sql.Time(((java.util.Date)parameterObj).getTime());
                }
                else {
                    parameterAsSQLTime = SqlUtil.convertStringToSQLTime(parameterObj.toString());
                }
                
                pstmt.setTime(parameterIndex, parameterAsSQLTime);
                
                break;
            default:
                pstmt.setObject(parameterIndex, parameterObj, targetSqlType);
            }
        }
        catch(Exception ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            }
            else {
                String message = "Failed to setObject: " + ex.getMessage() +
                                 ", Param Index = " + parameterIndex + 
                                 ", TargetSqlType = " + targetSqlType + 
                                 ", parameterObj = " + parameterObj + ".";
                throw new SQLException(message);
            }
        }
    }
}
