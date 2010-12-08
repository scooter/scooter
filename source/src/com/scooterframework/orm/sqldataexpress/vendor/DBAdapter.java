/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;
import com.scooterframework.security.LoginHelper;

/**
 * DBAdapter class. Subclass should implement all abstract methods listed in 
 * this class.
 * 
 * @author (Fei) John Chen
 */
public abstract class DBAdapter {
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    public static final String USE_LOGIN_USER_ID_AS_SCHEMA = "useLoginUserId";
    
    /**
     * Checks if using login user id as schema. When the value of 
     * <tt>use_login_as_schema</tt> as defined in 
     * <tt>database.properties</tt> file for a database connection 
     * definition is <tt>true</tt>, this method should return <tt>true</tt>.
     * 
     * @param connName  the database connection name
     * @return true if using login user id as schema
     */
    public boolean useLoginAsSchema(String connName) {
    	Properties p = SqlExpressUtil.getConnectionProperties(connName);
    	String s = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_USE_LOGIN_AS_SCHEMA);
    	return ("true".equalsIgnoreCase(s))?true:false;
    }
    
    /**
     * Returns login user id.
     */
    public static String getLoginUserId() {
    	return LoginHelper.loginUserId();
    }
    
    /**
     * Returns login user id.
     */
    public static String getLoginPassword() {
    	return LoginHelper.loginPassword();
    }
    
    /**
     * Returns both catalog and schema of a connection.
     * 
     * @param connName database connection name
     * @return a string array containing catalog and schema
     */
    public abstract String[] getCatalogAndSchema(String connName);
    
    /**
     * Returns a full table name which includes catalog and schema. 
     * 
     * @param catalog catalog name
     * @param schema  schema name
     * @param table   table name
     * @return an expanded table name
     */
    public abstract String getExpandedTableName(String catalog, String schema, String table);
    
    public String getExpandedTableName(String connName, String table) {
    	if (table.indexOf('.') != -1) return table;
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getExpandedTableName(catalog, schema, table);
    }
    
    /**
     * Returns a SQL SELECT query which retrieves only one record from a table. 
     * This query is used for retrieving meta data of the underlining table.
     * 
     * @param catalog catalog name
     * @param schema  schema name
     * @param table   table name
     * @return a SELECT query string
     */
    public abstract String getOneRowSelectSQL(String catalog, String schema, String table);
    
    /**
     * Returns a SQL SELECT query which retrieves only one record from a table. 
     * This query is used for retrieving meta data of the underlining table.
     * 
     * @param connName database connection name
     * @param table   table name
     * @return a SELECT query string
     */
    public String getOneRowSelectSQL(String connName, String table) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getOneRowSelectSQL(catalog, schema, table);
    }
    
    /**
     * Returns a SQL query statement which is used to count all records of a 
     * table, such as <tt>SELECT count(*) total FROM users</tt>.
     * 
     * @param catalog catalog name
     * @param schema  schema name
     * @param table   table name
     * @return a SQL query string for counting total
     */
    public String getTotalCountSQL(String catalog, String schema, String table) {
    	String countSQL = "SELECT count(*) total FROM ";
        return countSQL + getExpandedTableName(catalog, schema, table);
    }
    
    /**
     * Returns a SQL query statement which is used to count all records of a 
     * table, such as <tt>SELECT count(*) total FROM users</tt>.
     * 
     * @param connName database connection name
     * @param table   table name
     * @return a SQL query string for counting total
     */
    public String getTotalCountSQL(String connName, String table) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getTotalCountSQL(catalog, schema, table);
    }
    
    /**
     * Returns a SQL query statement which is used to retrieve all records of a 
     * table, such as <tt>SELECT * total FROM users</tt>.
     * 
     * @param connName connection name
     * @param table    table name
     * @return a retrieve all SQL query string
     */
    public String getRetrieveAllSQL(String connName, String table) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getRetrieveAllSQL(catalog, schema, table);
    }
    
    /**
     * Returns a SQL query statement which is used to retrieve all records of a 
     * table, such as <tt>SELECT * total FROM users</tt>.
     * 
     * @param catalog catalog name
     * @param schema  schema name
     * @param table   table name
     * @return a retrieve all SQL query string
     */
    public String getRetrieveAllSQL(String catalog, String schema, String table) {
    	String selectSQL = "SELECT * FROM ";
        return selectSQL + getExpandedTableName(catalog, schema, table);
    }
    
    public abstract String preparePaginationSql(String selectSql, Map inputs, Map outputFilters);
    
    protected static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }
    
    public Object getObjectFromResultSetByType(ResultSet rs, String javaClassType, int sqlDataType, int index) 
    throws SQLException {
        Object theObj = null;
           
        if ("java.sql.Timestamp".equals(javaClassType) || 
            "java.sql.Date".equals(javaClassType) || 
            sqlDataType == 91 || sqlDataType == 93) {
            theObj = rs.getTimestamp(index);
        }
        else if (sqlDataType == Types.BLOB) {
            try {
            	Blob blob = rs.getBlob(index);
                return getBlobData(blob);
            } 
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else if (sqlDataType == Types.CLOB) {
            try {
            	Clob clob = rs.getClob(index);
                return getClobData(clob);
            } 
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else {
            theObj = rs.getObject(index);
        }
        
        return theObj;
    }
    
    public Object getObjectFromStatementByType(CallableStatement cstmt, String javaClassType, int sqlDataType, int index) 
    throws SQLException {
        Object theObj = null;
        
        if ("java.sql.Timestamp".equals(javaClassType) ||
            "java.sql.Date".equals(javaClassType) || 
            sqlDataType == 91 || sqlDataType == 93) {
            theObj = cstmt.getTimestamp(index);
        }
        else if (sqlDataType == Types.BLOB) {
            try {
            	Blob blob = cstmt.getBlob(index);
                return getBlobData(blob);
            } 
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else if (sqlDataType == Types.CLOB) {
            try {
            	Clob clob = cstmt.getClob(index);
                return getClobData(clob);
            } 
            catch(Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        else {
            theObj = cstmt.getObject(index);
        }
        
        return theObj;
    }
    
    protected Object getBlobData(Blob blob) throws SQLException, IOException {
    	byte[] bytes = null;
    	if (blob == null) return bytes;
    	
    	ByteArrayOutputStream baos = null;
    	InputStream is = null;
    	try {
    		baos = new ByteArrayOutputStream();
    		is = blob.getBinaryStream();
			
			byte[] bytebuf = new byte[4096];
			int i = 0;
			while((i = is.read(bytebuf)) != -1) {
				baos.write(bytebuf, 0, i);
			}
			bytes = baos.toByteArray();
			
			is.close();
			baos.close();
			is = null;
			baos = null;
    	}
	    catch(Exception ex) {
	        throw new SQLException(ex.getMessage());
	    }
	    finally {
	    	if (is != null) {
	    		try {
	    			is.close();
	    		}
	    		catch(Exception ex)	{
	    			is = null;
	    		}
	    	}
	    	if (baos != null) {
	    		try {
	    			baos.close();
	    		}
	    		catch(Exception ex)	{
	    			baos = null;
	    		}
	    	}
	    }
	    
        return bytes;
    }
    
    protected Object getClobData(Clob clob) throws SQLException, IOException {
    	if (clob == null) return null;
    	
    	Object data = null;
    	Reader reader = null;
    	try {
    		reader = clob.getCharacterStream();
            if ( reader == null ) return null;
            
            StringBuffer sb = new StringBuffer();
            char[] charbuf = new char[4096];
            int i = 0;
            while ((i=reader.read(charbuf)) != -1) {
                sb.append(charbuf, 0, i);
            }
            
            data= sb.toString();
            reader.close();
            reader = null;
    	}
	    catch(Exception ex) {
	        throw new SQLException(ex.getMessage());
	    }
	    finally {
	    	if (reader != null) {
	    		try {
	    			reader.close();
	    		}
	    		catch(Exception ex)	{
	    			reader = null;
	    		}
	    	}
	    }
        return data;
    }

    public boolean vendorSpecificSetObject(PreparedStatement pstmt, Object obj, Parameter p, Map inputs)
    throws Exception {
        return false;
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
}
