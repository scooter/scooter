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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.PrimaryKey;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.util.SqlConstants;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;
import com.scooterframework.security.LoginHelper;

/**
 * DBAdapter class. Subclass should implement all abstract methods listed in 
 * this class.
 * 
 * @author (Fei) John Chen
 */
public abstract class DBAdapter {
    private static LogUtil log = LogUtil.getLogger(DBAdapter.class.getName());
    
    protected static final String IGNORE = "_IGNORE_";
    protected static final String USE_LOGIN_USER_ID_AS_SCHEMA = "useLoginUserId";
    
    private String type;
    
    protected DBAdapter() {
    	type = this.getClass().getName();
    }
    
    /**
     * Checks if using login user id as schema. When the value of 
     * <tt>use_login_as_schema</tt> as defined in 
     * <tt>database.properties</tt> file for a database connection 
     * definition is <tt>true</tt>, this method should return <tt>true</tt>.
     * 
     * @param connName  the database connection name
     * @return true if using login user id as schema
     */
    protected static boolean useLoginAsSchema(String connName) {
    	Properties p = SqlExpressUtil.getConnectionProperties(connName);
    	String s = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_USE_LOGIN_AS_SCHEMA);
    	return ("true".equalsIgnoreCase(s))?true:false;
    }
    
    /**
     * Returns login user id.
     */
    protected static String getLoginUserId() {
    	return LoginHelper.loginUserId();
    }
    
    /**
     * Returns login user id.
     */
    protected static String getLoginPassword() {
    	return LoginHelper.loginPassword();
    }
    
    /**
     * Checks if a string is empty. A string containing all white spaces is 
     * treated as an empty string.
     */
    protected static boolean isEmpty(String s) {
    	return (s == null || "".equals(s.trim()))?true:false;
    }
    
    private static boolean ignore(String s) {
    	return (s != null && IGNORE.equals(s));
    }
	
    /**
     * Counts how many dots in a string.
     */
	protected static int dotCount(String s) {
		StringTokenizer st = new StringTokenizer(s, ".");
		return st.countTokens() - 1;
	}
	
	/**
	 * Returns <tt>ORDER BY</tt> clause from either parsing a SQL query or 
	 * constructing from an input map. This method looks for <tt>ORDER BY</tt> 
	 * clause from the following sources:
	 * <ol>
	 *   <li>value for key <tt>order_by</tt> in the <tt>inputs</tt> map</li>
	 *   <li>values for key <tt>sort</tt> and <tt>order</tt> in the <tt>inputs</tt> map</li>
	 *   <li>the <tt>ORDER BY</tt> clause of the <tt>selectSQL</tt> query</li>
	 *   <li>all columns in the <tt>SELECT</tt> clause of the <tt>selectSQL</tt> query</li>
	 *   <li>primary key column(s) associated with the key <tt>database</tt> and <tt>table</tt> in the <tt>inputs</tt> map</li>
	 *   <li>first column name associated with the key <tt>database</tt> and <tt>table</tt> in the <tt>inputs</tt> map</li>
	 * </ol>
	 * 
	 * <p>If there is no <tt>ORDER BY</tt> clause obtained after the above 
	 * efforts, an <tt>IllegalArgumentException</tt> will be thrown.
	 * 
	 * @param selectSql    the SQL query
	 * @param inputs       inputs data map
	 * @return <tt>ORDER BY</tt> clause
	 */
	static String getOrderByClause(String selectSql, Map<String, Object> inputs) {
		String orderByClause = getOrderByClauseFromInputs(inputs);
        if (orderByClause == null || "".equals(orderByClause)) {
        	orderByClause = getOrderByClauseFromSQL(selectSql);
        	if (orderByClause == null || "".equals(orderByClause)) {
        		orderByClause = getOrderByClauseFromSelectClause(selectSql);
        		if (orderByClause.indexOf('*') != -1) {
        			orderByClause = getOrderByClauseFromPK(inputs);
        			if (orderByClause == null || "".equals(orderByClause)) {
        				orderByClause = getOrderByClauseFromTableMetaInfo(inputs);
        			}
        		}
        	}
        }
        
        if (orderByClause == null || "".equals(orderByClause)) {
        	throw new IllegalArgumentException("Failed to obtain order-by " + 
        			"clause related to sql query '" + selectSql + 
        			"'. Please make sure that either your sql query " + 
        			"contains order-by clause, or the inputs map contain " + 
        			"value for key '" + SqlConstants.key_order_by + 
        			"' or key '" + SqlConstants.key_order + 
        			"' and key '" + SqlConstants.key_sort + 
        			"' or the table being queried has primary key " + 
        			"or the first column is sortable--not a BLOB or CLOB or XML data type.");
        }
        
        log.debug("getOrderByClause: orderByClause '" + orderByClause + "'.");
        return orderByClause;
	}
	
	/**
	 * Retrieves <tt>ORDER BY</tt> clause from a SQL query. The return value 
	 * is started with <tt>ORDER BY</tt> string. If there is no 
	 * <tt>ORDER BY</tt> clause in the original SQL query, <tt>null</tt> 
	 * is returned.
	 * 
	 * @param sql  the SQL query
	 * @return <tt>ORDER BY</tt> clause
	 */
	static String getOrderByClauseFromSQL(String sql) {
		if (sql == null) return null;
		
		sql = sql.trim();

		if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
		int iOrderBy = sql.toUpperCase().indexOf("ORDER BY");
		if (iOrderBy == -1) return null;
		
		int iForUpdate = sql.toUpperCase().indexOf("FOR UPDATE");
		
		String s = null;
		if (iForUpdate == -1) {
			s = sql.substring(iOrderBy);
		}
		else {
			s = sql.substring(iOrderBy, iForUpdate);
		}
		return s.trim();
	}
	
	static String getOrderByClauseFromInputs(Map<String, Object> inputs) {
		String s = SqlUtil.getOrderBy(inputs);
		if (s != null) {
			s = s.trim();
			if (s.toUpperCase().startsWith("ORDER BY")) {
				s = s.substring(8);
			}
		}
		return s;
	}
	
	static String getOrderByClauseFromSelectClause(String sql) {
		sql = sql.trim();
		String sqlU = sql.toUpperCase();
		int iFrom = sqlU.indexOf("FROM");
		
		String result = "";
		if (sqlU.startsWith("SELECT DISTINCT")) {
			result = sql.substring(16, iFrom);
		}
		else if (sqlU.startsWith("SELECT TOP")) {
			result = sql.substring(11, iFrom);
		}
		else if (sqlU.startsWith("SELECT")) {
			result = sql.substring(7, iFrom);
		}
		else {
			throw new IllegalArgumentException("SQL query '" + sql + 
					"' must start with (case ignored) " + 
					"either SELECT DISTINCT or SELECT TOP or SELECT.");
		}
		return result;
	}
	
	//This only works for the built-in Data Browser
	static String getOrderByClauseFromPK(Map<String, Object> inputs) {
		String connName = getConnectionName(inputs);
		String table = getTableOrViewName(inputs);
		if (table == null) return null;
		
		PrimaryKey pk = SqlExpressUtil.lookupPrimaryKey(connName, table);
		if (pk == null) return null;
		List<String> columns = pk.getColumns();
		return StringUtil.flattenArray(columns.toArray());
	}
	
	//This only works for the built-in Data Browser
	static String getOrderByClauseFromTableMetaInfo(Map<String, Object> inputs) {
		String connName = getConnectionName(inputs);
		String table = getTableOrViewName(inputs);
		if (table == null) return null;
		
		TableInfo ti = SqlExpressUtil.lookupTableInfo(connName, table);
		if (ti == null) return null;
		String[] columns = ti.getHeader().getColumnNames();
		return (columns != null && columns.length > 0)?columns[0]:null;
	}
	
	static String getRemainSQL(String sql) {
		if (sql == null) return null;
		
		sql = sql.trim();
		
		if (!sql.toUpperCase().startsWith("SELECT")) {
			throw new IllegalArgumentException("The input sql '" + sql + 
					"' must start with SELECT.");
		}
		
		String s = null;
		int iOrderBy = sql.toUpperCase().indexOf("ORDER BY");
		if (iOrderBy != -1) {
			s = sql.substring(7, iOrderBy);
		}
		else {
			s = sql.substring(7);
		}
		
		return s.trim();
	}
	
	private static String getConnectionName(Map<String, Object> inputs) {
		String connName = (String) inputs.get(DataProcessor.input_key_database_connection_name);
		if (connName == null) {
			connName = (String) inputs.get(SqlConstants.key_database);
		}
		return connName;
	}
	
	private static String getTableOrViewName(Map<String, Object> inputs) {
		String tableName = (String) inputs.get(SqlConstants.key_table);
		if (tableName == null) {
			tableName = (String) inputs.get(SqlConstants.key_view);
		}
		return tableName;
	}
	
	/**
	 * Returns type which is the class name of this adapter.
	 * @return class name of this adapter
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Checks if table name case can be changed.<br/>
	 * 
	 * Some databases such as Oracle must use uppercase of a table name in 
	 * order to get meta info of the table. Others, like MySQL in Linux 
	 * platform, would not work properly if table name's case is changed.
	 * 
	 * @return true if table name case can be changed.
	 */
	public boolean canChangeTableNameCase() {
		return true;
	}
    
    /**
     * Returns both catalog and schema of a connection.
     * 
     * @param connName database connection name
     * @return a string array containing catalog and schema
     */
	public abstract String[] getCatalogAndSchema(String connName);
    
    /**
     * Returns catalog, schema and table of a connection. 
     * 
     * @param connName   database connection name
     * @param tableName  table name
     * @return a string array containing catalog, schema and table
     */
	public String[] resolveCatalogAndSchemaAndTable(String connName, String tableName) {
    	String[] s2 = getCatalogAndSchema(connName);
    	return resolveCatalogAndSchemaAndTable(s2[0], s2[1], tableName);
    }
    
    /**
     * Returns atomic catalog, schema and table based on <tt>tableName</tt>. 
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * <p>If the <tt>catalog</tt> or <tt>schema</tt> is different from those 
     * defined with the <tt>tableName</tt>, an <tt>IllegalArgumentException</tt> 
     * will be thrown.</p>
     * 
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @return a string array containing catalog, schema and table
     */
    public String[] resolveCatalogAndSchemaAndTable(String catalog, String schema, String tableName) {
    	String[] s3 = resolveCatalogAndSchemaAndTableFromTableName(tableName);
    	String _catalog = s3[0];
    	String _schema = s3[1];
    	String _table = s3[2];
    	
    	if (catalog == null) {
    		catalog = _catalog;
    	} else {
    		if (_catalog != null && !catalog.equalsIgnoreCase(_catalog)) {
				throw new IllegalArgumentException(
						"Failed in resolveCatalogAndSchemaAndTable: " 
								+ " the input catalog is '"
								+ catalog
								+ "', while the catalog derived from tableName '"
								+ tableName + "' is '" + _catalog + "'.");
			}
    	}
    	if (schema == null) {
    		schema = _schema;
    	} else {
    		if (_schema != null && !schema.equalsIgnoreCase(_schema)) {
				throw new IllegalArgumentException(
						"Failed in resolveCatalogAndSchemaAndTable: " 
								+ " the input schema is '"
								+ schema
								+ "', while the schema derived from tableName '"
								+ tableName + "' is '" + _schema + "'.");
			}
    	}
    	
    	String[] result = new String[3];
    	result[0] = catalog;
    	result[1] = schema;
    	result[2] = _table;
    	return result;
    }
    
    /**
     * Returns atomic catalog, schema and table based on <tt>connName</tt>, and 
     * <tt>tableName</tt>. 
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * <p>If the <tt>catalog</tt> or <tt>schema</tt> is different from those 
     * defined with the <tt>connName</tt>, an <tt>IllegalArgumentException</tt> 
     * will be thrown.</p>
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @return a string array containing catalog, schema and table
     */
	public String[] resolveCatalogAndSchemaAndTable(String connName,
			String catalog, String schema, String tableName) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String _catalog = s2[0];
    	String _schema = s2[1];
    	
    	String[] s3 = resolveCatalogAndSchemaAndTable(catalog, schema, tableName);
    	catalog = s3[0];
    	schema = s3[1];
    	String _table = s3[2];
    	
    	if (catalog == null) {
    		catalog = _catalog;
    	} else {
    		if (_catalog != null && !catalog.equalsIgnoreCase(_catalog)) {
				throw new IllegalArgumentException(
						"Failed in resolveCatalogAndSchemaAndTable: "
								+ " the catalog for table '"
								+ tableName
								+ "' is '"
								+ catalog
								+ "', while the catalog derived from connName '"
								+ connName + "' is '" + _catalog + "'.");
			}
    	}
    	if (schema == null) {
    		schema = _schema;
    	} else {
    		if (_schema != null && !schema.equalsIgnoreCase(_schema)) {
				throw new IllegalArgumentException(
						"Failed in resolveCatalogAndSchemaAndTable: " 
								+ " the schema for table '"
								+ tableName
								+ "' is '"
								+ schema
								+ "', while the schema derived from connName '"
								+ connName + "' is '" + _schema + "'.");
			}
    	}
    	
    	String[] result = new String[3];
    	result[0] = catalog;
    	result[1] = schema;
    	result[2] = _table;
    	return result;
    }
    
    /**
     * Returns atomic catalog, schema and table based on <tt>tableName</tt>. 
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * <p>This method should be overridden for those databases that do not 
     * use <tt>schema</tt>, such as MySQL database.
     * 
     * @param tableName
     * @return a string array containing catalog, schema and table
     */
    public String[] resolveCatalogAndSchemaAndTableFromTableName(String tableName) {
    	if (tableName == null) 
    		throw new IllegalArgumentException("tableName cannot be null.");
    	
    	if (tableName.startsWith("\"")) tableName = tableName.substring(1);
    	if (tableName.endsWith("\"")) tableName = tableName.substring(0, tableName.length() - 1);
    	
    	String[] ss = tableName.split("\\.");
    	
    	String[] s3 = new String[3];
    	if (ss.length == 3) {
    		s3[0] = ss[0];
    		s3[1] = ss[1];
    		s3[2] = ss[2];
    	} else if (ss.length == 2) {
    		s3[1] = ss[0];
    		s3[2] = ss[1];
    	} else if (ss.length == 1) {
    		s3[2] = ss[0];
    	}
    	return s3;
    }
    
    /**
     * Returns a full table name which may include catalog and schema.
     * 
     * <p>The result table name may take one of the following cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {catalog}.{table} //for MySQL
     *   {schema}.{table}  //for Oracle
     *   {table}
     * </pre>
     * 
     * @param connName     connection name
     * @param tableName    table name
     * @return an expanded table name
     */
    public String getExpandedTableName(String connName, String tableName) {
    	String[] s3 = resolveCatalogAndSchemaAndTable(connName, tableName);
    	return getExpandedTableName(s3[0], s3[1], s3[2]);
    }
    
    /**
     * Returns a full-qualified table name which may include catalog and schema.
     * 
     * <p>The result table name may take one of the following cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {catalog}.{table} //for MySQL
     *   {schema}.{table}  //for Oracle
     *   {table}
     * </pre>
     * 
     * @param catalog      catalog name
     * @param schema       schema name
     * @param tableName    table name
     * @return an expanded table name
     */
    public String getExpandedTableName(String catalog, String schema, String tableName) {
    	String[] s3 = resolveCatalogAndSchemaAndTable(catalog, schema, tableName);
    	catalog = s3[0];
    	schema = s3[1];
    	String table = s3[2];
    	
    	tableName = "";
    	if (ignore(catalog) || isEmpty(catalog)) {
        	if (ignore(schema) || isEmpty(schema)) {
        		tableName = table;
        	}
        	else {
        		tableName = schema + "." + table;
        	}
    	}
    	else {
    		if (ignore(schema)) {
    			tableName = catalog + "." + table;//only for MySQL case
    		}
    		else if (isEmpty(schema)) {
    			tableName = table;
    		}
    		else {
    			tableName = catalog + "." + schema + "." + table;
    		}
    	}
    	return tableName;
    }
    
    /**
     * Returns a full-qualified table name which may include catalog and schema.
     * 
     * <p>The result table name may take one of the following cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {catalog}.{table} //for MySQL
     *   {schema}.{table}  //for Oracle
     *   {table}
     * </pre>
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @return an expanded table name
     */
	public String getExpandedTableName(String connName,
			String catalog, String schema, String tableName) {
		return getExpandedTableName(catalog, schema, getExpandedTableName(connName, tableName));
	}
    
    /**
     * Returns a SQL SELECT query which retrieves only one record from a table. 
     * This query is used for retrieving meta data of the underlining table.
     * 
     * @param catalog      catalog name
     * @param schema       schema name
     * @param tableName    table name
     * @return a SELECT query string
     */
    public abstract String getOneRowSelectSQL(String catalog, String schema, String tableName);
    
    /**
     * Returns a SQL SELECT query which retrieves only one record from a table. 
     * This query is used for retrieving meta data of the underlying table.
     * 
     * @param connName     database connection name
     * @param tableName    table name
     * @return a SELECT query string
     */
    public String getOneRowSelectSQL(String connName, String tableName) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getOneRowSelectSQL(catalog, schema, tableName);
    }
    
    /**
     * Returns a SQL query statement which is used to count all records of a 
     * table, such as <tt>SELECT count(*) total FROM users</tt>.
     * 
     * @param catalog      catalog name
     * @param schema       schema name
     * @param tableName    table name
     * @return a SQL query string for counting total
     */
    public String getTotalCountSQL(String catalog, String schema, String tableName) {
    	String countSQL = "SELECT count(*) total FROM ";
        return countSQL + getExpandedTableName(catalog, schema, tableName);
    }
    
    /**
     * Returns a SQL query statement which is used to count all records of a 
     * table, such as <tt>SELECT count(*) total FROM users</tt>.
     * 
     * @param connName     database connection name
     * @param tableName    table name
     * @return a SQL query string for counting total
     */
    public String getTotalCountSQL(String connName, String tableName) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getTotalCountSQL(catalog, schema, tableName);
    }
    
    /**
     * Returns a SQL query statement which is used to retrieve all records of a 
     * table, such as <tt>SELECT * total FROM users</tt>.
     * 
     * @param connName     database connection name
     * @param tableName    table name
     * @return a retrieve all SQL query string
     */
    public String getRetrieveAllSQL(String connName, String tableName) {
    	String[] s2 = getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
    	return getRetrieveAllSQL(catalog, schema, tableName);
    }
    
    /**
     * Returns a SQL query statement which is used to retrieve all records of a 
     * table, such as <tt>SELECT * total FROM users</tt>.
     * 
     * @param catalog      catalog name
     * @param schema       schema name
     * @param tableName    table name
     * @return a retrieve all SQL query string
     */
    public String getRetrieveAllSQL(String catalog, String schema, String tableName) {
    	String selectSQL = "SELECT * FROM ";
        return selectSQL + getExpandedTableName(catalog, schema, tableName);
    }
    
    /**
     * Returns a SQL query for pagination. This method converts a generic SQL 
     * query statement to a SQL query for pagination.
     * 
     * @param selectSql     the original SQL statement
     * @param inputs        inputs
     * @param outputFilters outputFilters
     * @return a SQL query for pagination
     */
    public abstract String preparePaginationSql(String selectSql, Map<String, Object> inputs, Map<String, String> outputFilters);
    
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
            
            StringBuilder sb = new StringBuilder();
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

    public boolean vendorSpecificSetObject(PreparedStatement pstmt, Object obj, Parameter p, Map<String, Object> inputs)
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
