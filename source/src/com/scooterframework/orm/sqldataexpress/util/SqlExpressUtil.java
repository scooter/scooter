/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.exception.GenericException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.config.SqlConfig;
import com.scooterframework.orm.sqldataexpress.connection.DatabaseConnectionContext;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnectionFactory;
import com.scooterframework.orm.sqldataexpress.exception.LookupFailureException;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedStoredProcedureAPINameException;
import com.scooterframework.orm.sqldataexpress.object.Function;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.ParameterFactory;
import com.scooterframework.orm.sqldataexpress.object.PrimaryKey;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.StoredProcedure;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapter;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapterFactory;

/**
 * SqlExpressUtil class holds utility methods for meta data lookup.
 * 
 * @author (Fei) John Chen
 */
public class SqlExpressUtil {
    private static LogUtil log = LogUtil.getLogger(SqlExpressUtil.class.getName());
    
    private static final String niceChars = 
    	".ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_$";
    
    private static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }
    
    private static String toUpperCaseIfAllowed(DBAdapter dba, String s) {
    	if (!dba.canChangeTableNameCase()) return s;
        return (s == null || "".equals(s))?s:s.toUpperCase();
    }

    public static List<String> getConnectionNames() {
        List<String> dbs = new ArrayList<String>();
        Iterator<String> it = DatabaseConfig.getInstance().getPredefinedDatabaseConnectionNames();
        while(it.hasNext()) {
            dbs.add(it.next());
        }
        Collections.sort(dbs);
        return dbs;
    }
    
    /**
     * Returns properties associated with a default database connection name.
     * 
     * @return properties of the connection name
     */
    public static Properties getDefaultConnectionProperties() {
        Properties p = DatabaseConfig.getInstance().getDefaultDatabaseConnectionProperties();
        return p;
    }
    
    /**
     * Returns properties associated with a database connection name.
     * 
     * @param connName database connection name
     * @return properties of the connection name
     */
    public static Properties getConnectionProperties(String connName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
        Properties p = DatabaseConfig.getInstance().getPredefinedDatabaseConnectionProperties(connName);
        return p;
    }
    
    /**
     * Returns <tt>username</tt> of a database connection.
     * 
     * @param connName database connection name
     * @return connection username
     */
    public static String getConnectionUser(String connName) {
        Properties p = getConnectionProperties(connName);
        return p.getProperty("username");
    }
    
    /**
     * Returns <tt>url</tt> of a database connection.
     * 
     * @param connName database connection name
     * @return connection url
     */
    private static String getConnectionURL(String connName) {
        Properties p = getConnectionProperties(connName);
        String url = p.getProperty("url");
        if (url == null || "".equals(url))
        	throw new IllegalArgumentException("url field for database " + 
        			"connection " + connName + " is empty.");
        return url;
    }
    
    public static String getExtendedTableName(String connName, TableInfo ti) {
		return DBAdapterFactory.getInstance().getAdapter(connName)
				.getExpandedTableName(connName, ti.getCatalog(),
						ti.getSchema(), ti.getName());
    }
    
    /**
     * Returns the extended table name.
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
    public static String getExtendedTableName(String connName,
			String catalog, String schema, String tableName) {
		return DBAdapterFactory.getInstance().getAdapter(connName)
				.getExpandedTableName(connName, catalog, schema, tableName);
    }
    
    /**
     * Checks if the underlying connection is for Oracle database.
     * 
     * @param connName database connection name
     * @return true if it is Oracle
     */
    public static boolean isOracle(String connName) {
    	return isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_ORACLE, connName);
    }
    
    /**
     * Checks if a <tt>vendor</tt> is a built-in vendor supported.
     * 
     * @param vendor   vendor name supported
     * @param connName database connection name
     * @return true if it is built-in vendor
     */
    public static boolean isBuiltinVendor(String vendor, String connName) {
    	if (vendor == null) 
    		throw new IllegalArgumentException("Vendor input is empty.");
    	
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
    	Properties p = getConnectionProperties(connName);
    	String v = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_VENDOR);
    	if (!isEmpty(v)) {
    		if (DatabaseConfig.isBuiltInVendorName(v)) {
    			return v.equalsIgnoreCase(vendor);
    		}
    		else {
    			log.warn("vendor is defined for connection named \"" + 
    					connName + "\", but it is not in the allowed vendor names " + 
    					"which is \"" + DatabaseConfig.ALL_BUILTIN_DATABASE_VENDORS + "\"");
    		}
    	}
    	
    	String url = getConnectionURL(connName);
    	if (url == null)
    		throw new IllegalArgumentException("url is empty for connection named " + 
    				"\"" + connName + "\".");
    	return (url.toLowerCase().indexOf(vendor.toLowerCase()) != -1)?true:false;
    }
    
    /**
     * Returns both catalog and schema of a connection.
     * 
     * @param connName database connection name
     * @return a string array containing catalog and schema
     */
    public static String[] getCatalogAndSchema(String connName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	return dba.getCatalogAndSchema(connName);
    }
    
    public static String checkSpecialCharacterInTableName(String table) {
    	String t = table;
    	if (!isNiceDBString(table)) {
    		t = "\"" + table + "\"";
    	}
    	return t;
    }
    
    /**
     * <p>Verifies that if the input string contains some special characters 
     * that are not friendly in database table names or column names.</p>
     * 
     * <p>Only the following are treated as nice characters:</p>
     * <ul>
     *   <li>Letters</li>
     *   <li>Numbers</li>
     *   <li>Underscore</li>
     *   <li>$</li>
     * </ul>
     * 
     * @param s a string to check
     * @return true if the string contains no special chars
     */
    public static boolean isNiceDBString(String s) {
    	boolean b = true;
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
    		if (niceChars.indexOf(c) == -1) {
    			b = false;
    			break;
    		}
    	}
    	return b;
    }
    
    public static String getSafeTableName(String connName, String tableName) {
        String s = tableName;
        if (isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_ORACLE, connName) && 
        	(tableName != null && !tableName.startsWith("\"") && !tableName.endsWith("\""))
        	) {
        	s = checkSpecialCharacterInTableName(tableName);
        }
        return s;
    }
    
    /**
     * Returns a UserDatabaseConnection instance for default database 
     * connection name.
     */
    public static UserDatabaseConnection getUserDatabaseConnection() {
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection();
    }
    
    /**
     * Returns a UserDatabaseConnection instance for a specific database 
     * connection context.
     * 
     * @param dcc  a DatabaseConnectionContext instance
     */
    public static UserDatabaseConnection getUserDatabaseConnection(DatabaseConnectionContext dcc) {
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(dcc);
    }
    
    /**
     * Returns a UserDatabaseConnection instance for a specific database 
     * connection name.
     * 
     * @param connName  name of a connection
     */
    public static UserDatabaseConnection getUserDatabaseConnection(String connName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(connName);
    }
    
    /**
     * Returns a database connection.
     * 
     * @return a database connection
     */
    public static Connection getConnection() throws SQLException {
        return getUserDatabaseConnection().getConnection();
    }
    
    /**
     * Returns a database connection.
     * 
     * @param connName     name of a connection
     * @return a database connection
     */
    public static Connection getConnection(String connName) throws SQLException {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
    	return getUserDatabaseConnection(connName).getConnection();
    }
    
    /**
     * Returns a database connection.
     * 
     * @param dcc a DatabaseConnectionContext instance
     * @return a database connection
     */
    public static Connection getConnection(DatabaseConnectionContext dcc) throws SQLException {
    	return getUserDatabaseConnection(dcc).getConnection();
    }
    
    /**
     * Returns a real-only database connection.
     * 
     * @return a read-only database connection
     */
    public static Connection getReadonlyConnection() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.setReadOnly(true);
        }
        return conn;
    }
    
    /**
     * Returns a real-only database connection.
     * 
     * @param connName     name of a connection
     * @return a read-only database connection
     */
    public static Connection getReadonlyConnection(String connName) throws SQLException {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
        Connection conn = getConnection(connName);
        if (conn != null) {
            conn.setReadOnly(true);
        }
        return conn;
    }
    
    /**
     * Returns a real-only database connection.
     * 
     * @param dcc a DatabaseConnectionContext instance
     * @return a read-only database connection
     */
    public static Connection getReadonlyConnection(DatabaseConnectionContext dcc) throws SQLException {
        Connection conn = getConnection(dcc);
        if (conn != null) {
            conn.setReadOnly(true);
        }
        return conn;
    }

	/**
	 * Returns a list of TableInfo instances for a database connection name.
	 * 
	 * @param connName
	 *            the database connection name
	 * @param catalog
	 *            a catalog name; must match the catalog name as it is stored in
	 *            the database; "" retrieves those without a catalog;
	 *            <tt>null</tt> means that the catalog name should not be used
	 *            to narrow the search
	 * @param schema
	 *            a schema name; must match the schema name as it is stored in
	 *            the database; "" retrieves those without a schema;
	 *            <tt>null</tt> means that the schema name should not be used to
	 *            narrow the search
	 * @param tableName
	 *            a table name; must match the table name as it is stored in the
	 *            database
	 * @param types
	 *            a list of table types to include; <tt>null</tt> returns all
	 *            types
	 * @return a list of TableInfo instances
	 * @throws java.sql.SQLException
	 */
    public static List<TableInfo> getDatabaseTables(String connName, 
                                                String catalog, 
                                                String schema, 
                                                String tableName, 
                                                String[] types) throws SQLException {
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	catalog = toUpperCaseIfAllowed(dba, catalog);
    	schema = toUpperCaseIfAllowed(dba, schema);
    	tableName = toUpperCaseIfAllowed(dba, tableName);
        
        List<TableInfo> list = new ArrayList<TableInfo>();
        Connection conn = null;
        ResultSet rs = null;
        try {
        	conn = getConnection(connName);
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(catalog, schema, tableName, types);
            while (rs.next()) {
                TableInfo ti = new TableInfo();
                ti.setCatalog(rs.getString("TABLE_CAT"));
                ti.setName(rs.getString("TABLE_NAME"));
                ti.setRemarks(rs.getString("REMARKS"));
                ti.setSchema(rs.getString("TABLE_SCHEM"));
                ti.setType(rs.getString("TABLE_TYPE"));
                list.add(ti);
            }
            rs.close();
        }
        catch(SQLException ex) {
        	throw ex;
        }
        finally {
        	DAOUtil.closeResultSet(rs);
        	DAOUtil.closeConnection(conn);
        }
        return list;
    }
    
    /**
     * Returns a list of TableInfo instances for a database connection name.
     * 
     * @param connName  the database connection name
     * @return a list of TableInfo instances
     * @throws java.sql.SQLException
     */
    public static List<TableInfo> getDatabaseTables(String connName) throws SQLException {
        return getDatabaseTables(connName, (String)null, (String)null, (String)null, (String[])null);
    }
    
    /**
     * Returns database vendor name.
     * 
     * @param dbmd a DatabaseMetaData instance
     * @return vendor name
     */
    public static String getDatabaseVendor(DatabaseMetaData dbmd) {
        if (dbmd == null) return null;
        
        String vendor = null;
        
        try {
            vendor = dbmd.getDatabaseProductName();
            if (vendor != null) vendor = vendor.toUpperCase();
        }
        catch(Exception ex) {
            throw new GenericException("Error in pulling database meta data: " + ex.getMessage());
        }
        
        return vendor;
    }
    
    
    
    public static Function lookupAndRegisterFunction(String function) {
        if (function == null) 
            throw new IllegalArgumentException("Function name is empty.");
        
        String errorMessage = "Failed to get meta data info for function " + function;
        Function f = DBStore.getInstance().getFunction(function);
        if (f == null) {
        	UserDatabaseConnection udc = null;
            try {
                udc = SqlExpressUtil.getUserDatabaseConnection();
                f = lookupFunction(udc, function);
            }
            catch(Exception ex) {
                errorMessage += ", because " + ex.getMessage() + ".";
            }
            finally {
                DAOUtil.closeConnection(udc.getConnection());
            }
            
            if (f != null) {
                DBStore.getInstance().addFunction(function, f);
            }
        }
        
        if (f == null) {
            throw new LookupFailureException(errorMessage);
        }
        return f;
    }
    
    public static Function lookupFunction(UserDatabaseConnection udc, String name) {
    	Connection connection = udc.getConnection();
    	
        if (connection == null || name == null) 
            throw new IllegalArgumentException("connection or name is empty.");
        
        Function sp = new Function(name);
        ResultSet rs = null;
        
        try {
            String catalog = sp.getCatalog();
            String schema = sp.getSchema();
            String api = sp.getApi();
            DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
            
            boolean foundPlSqlRecord = false;//will skip all output columns when a PL/SQL record is found.//TODO: This code is tied to Oracle. Change it.
            boolean startToRecord = false;
            int previousIndex = -1;
            
            DatabaseMetaData dbmd = connection.getMetaData();
            String vendor = getDatabaseVendor(dbmd);
            rs = dbmd.getProcedureColumns(toUpperCaseIfAllowed(dba, catalog), 
            		toUpperCaseIfAllowed(dba, schema), toUpperCaseIfAllowed(dba, api), null);
            
            while (rs.next()) {
                catalog = rs.getString("PROCEDURE_CAT");
                schema = rs.getString("PROCEDURE_SCHEM");
                int index = rs.getInt("SEQUENCE");
                String columnName = rs.getString("COLUMN_NAME");
                String mode = rs.getString("COLUMN_TYPE");
                int sqlDataType = rs.getInt("DATA_TYPE");
                String sqlDataTypeName = rs.getString("TYPE_NAME");
                
                // turn on foundPlSqlRecord
                if (Parameter.MODE_OUT.equals(mode) && 
                     Types.OTHER == sqlDataType && //cursor type
                     columnName == null && 
                     "PL/SQL RECORD".equals(sqlDataTypeName)) {//TODO: This code is tied to Oracle. Change it.
                    foundPlSqlRecord = true;
                }
                
                // The next few rows are definition of this ref cursor
                // ignore it as there is no way to detect the end of this 
                // cursor columns exactly. 
                // will get the output cursor info in other place. 
                if (foundPlSqlRecord) {
                    if (Parameter.MODE_OUT.equals(mode)) {
                        continue;
                    }
                    else {
                        // turn off foundPlSqlRecord
                        foundPlSqlRecord = false;
                    }
                }
                
                //check if start to record
                if (index == 1 && Parameter.MODE_RETURN.equals(mode)) {
                    startToRecord = true;
                    previousIndex = -1;//clear position
                }
                
                if (index <= previousIndex) {
                    startToRecord = false;
                }
                
                if (startToRecord) {
                    Parameter p = ParameterFactory.getInstance().createParameter(vendor, index, columnName, mode, sqlDataType, sqlDataTypeName);
                    p.setCatalog(catalog);
                    p.setSchema(schema);
                    sp.addParameter(p);
                }
                
                previousIndex = index;
            }
            sp.setCataloge(catalog);
            sp.setSchema(schema);
            
            rs.close();
        }
        catch(SQLException sqlEx) {
            throw new UnsupportedStoredProcedureAPINameException(sqlEx);
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        return sp;
    }
    
    public static StoredProcedure lookupAndRegisterStoredProcedure(String storedProcedure) {
        if (storedProcedure == null) 
            throw new IllegalArgumentException("Stored procedure name is empty.");
        
        String errorMessage = "Failed to get meta data info for stored procedur " + storedProcedure;
        StoredProcedure sp = DBStore.getInstance().getStoredProcedure(storedProcedure);
        if (sp == null) {
        	UserDatabaseConnection udc = null;
            try {
                udc = SqlExpressUtil.getUserDatabaseConnection();
                sp = lookupStoredProcedure(udc, storedProcedure);
            }
            catch(Exception ex) {
                errorMessage += ", because " + ex.getMessage() + ".";
            }
            finally {
                DAOUtil.closeConnection(udc.getConnection());
            }
            
            if (sp != null) {
                DBStore.getInstance().addStoredProcedure(storedProcedure, sp);
            }
        }
        
        if (sp == null) {
            throw new LookupFailureException(errorMessage);
        }
        return sp;
    }
    
    public static StoredProcedure lookupStoredProcedure(UserDatabaseConnection udc, String name) {
    	Connection connection = udc.getConnection();
    	
        if (connection == null || name == null) 
            throw new IllegalArgumentException("connection or name is empty.");
        
        StoredProcedure sp = new StoredProcedure(name);
        ResultSet rs = null;
        
        try {
            String catalog = sp.getCatalog();
            String schema = sp.getSchema();
            String api = sp.getApi();
            DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
            
            boolean startToRecord = false;
            int previousIndex = -1;
            
            DatabaseMetaData dbmd = connection.getMetaData();
            String vendor = getDatabaseVendor(dbmd);
            rs = dbmd.getProcedureColumns(toUpperCaseIfAllowed(dba, catalog), 
            		toUpperCaseIfAllowed(dba, schema), toUpperCaseIfAllowed(dba, api), null);
            
            while (rs.next()) {
                catalog = rs.getString("PROCEDURE_CAT");
                schema = rs.getString("PROCEDURE_SCHEM");
                int index = rs.getInt("SEQUENCE");
                String columnName = rs.getString("COLUMN_NAME");
                String mode = rs.getString("COLUMN_TYPE");
                int sqlDataType = rs.getInt("DATA_TYPE");
                String sqlDataTypeName = rs.getString("TYPE_NAME");
                
                //check if start to record
                if (index == 1 && !Parameter.MODE_RETURN.equals(mode)) {
                    startToRecord = true;
                    previousIndex = -1;//clear position
                }
                
                if (index <= previousIndex) {
                    startToRecord = false;
                }
                
                if (startToRecord) {
                    Parameter p = ParameterFactory.getInstance().createParameter(vendor, index, columnName, mode, sqlDataType, sqlDataTypeName);
                    p.setCatalog(catalog);
                    p.setSchema(schema);
                    sp.addParameter(p);
                }
                
                previousIndex = index;
            }
            sp.setCataloge(catalog);
            sp.setSchema(schema);
            
            rs.close();
        }
        catch(SQLException sqlEx) {
            throw new UnsupportedStoredProcedureAPINameException(sqlEx);
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        return sp;
    }
    
    // find the jdbc statement from property file
    public static JdbcStatement createJdbcStatement(String name) {
        if (name == null) 
            throw new IllegalArgumentException("SQL statement name is empty.");
        
        String jdbcStatementString = SqlConfig.getInstance().getSql(name);
        if (jdbcStatementString == null || "".equals(jdbcStatementString.trim())) 
            throw new LookupFailureException("There is no sql statement for " + name + ".");
        
        return new JdbcStatement(name, jdbcStatementString);
    }
    
    // find the jdbc statement from cache
    public static JdbcStatement createJdbcStatementDirect(String jdbcStatementString) {
        if (jdbcStatementString == null) 
            throw new IllegalArgumentException("SQL statement string is empty.");
        
        return new JdbcStatement(jdbcStatementString, jdbcStatementString);
    }
    
    /**
     * Looks up <tt>{@link com.scooterframework.orm.sqldataexpress.object.TableInfo TableInfo}</tt>.
     * The input <tt>tableName</tt> can represent either a table name or 
     * a view name.
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * @param connName   database connection name
     * @param tableName  table or view name
     * @return <tt>{@link com.scooterframework.orm.sqldataexpress.object.TableInfo TableInfo}</tt> instance.
     */
    public static TableInfo lookupTableInfo(String connName, String tableName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
        if (tableName == null) 
            throw new IllegalArgumentException("tableName cannot be null.");
        
        TableInfo ti = DBStore.getInstance().getTableInfo(connName, tableName);
        if (ti != null) return ti;
        
        UserDatabaseConnection udc = null;
        try {
        	udc = SqlExpressUtil.getUserDatabaseConnection(connName);
        	ti = _lookupAndRegisterTableInfo(udc, tableName);
        }
        finally {
        	DAOUtil.closeConnection(udc);
        }
        
        return ti;
    }
    
    /**
     * Looks up <tt>{@link com.scooterframework.orm.sqldataexpress.object.TableInfo TableInfo}</tt>.
     * The input <tt>tableName</tt> can represent either a table name or 
     * a view name.
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * @param udc        instance of <tt>{@link com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection UserDatabaseConnection}</tt>
     * @param tableName  table or view name
     * @return <tt>{@link com.scooterframework.orm.sqldataexpress.object.TableInfo TableInfo}</tt> instance.
     */
    public static TableInfo lookupTableInfo(UserDatabaseConnection udc, String tableName) {
        if (udc == null) 
            throw new IllegalArgumentException("UserDatabaseConnection udc is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        String connName = udc.getConnectionName();
        TableInfo ti = DBStore.getInstance().getTableInfo(connName, tableName);
        if (ti != null) return ti;
        
        return _lookupAndRegisterTableInfo(udc, tableName);
    }
    
    private static TableInfo _lookupAndRegisterTableInfo(UserDatabaseConnection udc, String tableName) {
        if (udc == null) 
            throw new IllegalArgumentException("UserDatabaseConnection udc is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        String connName = udc.getConnectionName();
        DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
        String[] s3 = dba.resolveCatalogAndSchemaAndTable(connName, tableName);
        String catalog = s3[0];
        String schema = s3[1];
        String table = s3[2];
        
        TableInfo ti = null;
        
        try {
            ti = createTableInfo(dba, udc, catalog, schema, table);
            DBStore.getInstance().addTableInfo(connName, tableName, ti);
        }
        catch(LookupFailureException lfEx) {
        	throw lfEx;
        }
        catch (Exception ex) {
            String errorMessage = "Failed to get meta data info of '" + tableName + 
    					"' with database connection '" + connName + "'" + 
    					" catalog '" + catalog + "', schema '" + schema + "'.";
            errorMessage += " Reason: " + ex.getMessage() + ".";
            
            log.error("Exception in lookupTableInfo(): " + errorMessage);
            throw new LookupFailureException(errorMessage, ex);
        }
        
        return ti;
    }
    
    private static TableInfo createTableInfo(DBAdapter dba, UserDatabaseConnection udc, String catalog, String schema, String table) {
        String connName = udc.getConnectionName();
        
        TableInfo ti = null;
        
        try {
            String tableType = TableInfo.TYPE_TABLE; //default
            tableType = getTableType(dba, udc.getConnection(), catalog, schema, table, TableInfo.getSupportedTypes());
            
            if (TableInfo.TYPE_TABLE.equals(tableType)) {
                ti = lookupTable(dba, udc.getConnection(), catalog, schema, table);
            }
            else if (TableInfo.TYPE_VIEW.equals(tableType)){
                ti = lookupView(dba, udc.getConnection(), catalog, schema, table);
            }
            else {
                throw new SQLException("Unknown table type: " + tableType + 
                		". Supported types are TABLE and VIEW.");
            }
            
            if (ti == null) 
                throw new LookupFailureException("Failed to find table info for '" + table + "'.");
        }
        catch (SQLException ex) {
            String errorMessage = "Failed to get meta data info of '" + table + 
    					"' with database connection '" + connName + "'" + 
    					" catalog '" + catalog + "', schema '" + schema + "'.";
            errorMessage += " Reason: " + ex.getMessage() + ".";
            
            log.error("Exception in createTableInfo(): " + errorMessage);
            throw new LookupFailureException(errorMessage, ex);
        }
        
        return ti;
    }
    
    // find table type: TABLE or VIEW
    private static String getTableType(DBAdapter dba, Connection conn, String catalog, 
    		String schema, String table, String[] supportedTypes) 
    throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (table == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        table = DatabaseConfig.getInstance().getFullTableName(table);
        
        // check if it is a table or a view
        String tableType = TableInfo.TYPE_TABLE; //default
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(toUpperCaseIfAllowed(dba, catalog), toUpperCaseIfAllowed(dba, schema), 
            		toUpperCaseIfAllowed(dba, table), supportedTypes);
            if (rs.next()) {
            	tableType = rs.getString("TABLE_TYPE");
            }
            rs.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        return tableType;
    }
    
    // find the table information
    private static TableInfo lookupTable(DBAdapter dba, Connection conn, 
    	String catalog, String schema, String table) throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (table == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        String fullTableName = DatabaseConfig.getInstance().getFullTableName(table);
        
        TableInfo ti = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        
        try {
            String sqlString = dba.getOneRowSelectSQL(catalog, schema, fullTableName);
            log.debug("lookupTable   catalog: " + catalog);
            log.debug("lookupTable    schema: " + schema);
            log.debug("lookupTable sqlString: " + sqlString);
            
            ti = new TableInfo();
            ti.setCatalog(catalog);
            ti.setSchema(schema);
            ti.setName(table);
            
            stmt = conn.createStatement();

            // Query the table
            rs = stmt.executeQuery(sqlString);
            
            RowInfo header = new RowInfo(table, rs.getMetaData());
            header.setCatalog(catalog);
            header.setSchema(schema);
            header.setTable(table);
            
            ti.setHeader(header);
            DAOUtil.closeResultSet(rs);

            //set more properties
            DatabaseMetaData dbmd = conn.getMetaData();
            rs2 = dbmd.getColumns(toUpperCaseIfAllowed(dba, catalog), 
            		toUpperCaseIfAllowed(dba, schema), 
            		toUpperCaseIfAllowed(dba, table), (String)null);
            header.setResultSetMetaDataForTable(rs2);
            
            // set some table properties
            //ti.setSchema(ti.getHeader().getColumnInfo(0).getSchemaName());
            //ti.setCatalog(ti.getHeader().getColumnInfo(0).getCatalogName());
            
            // get primary keys
            if (!header.hasPrimaryKey()) {
                PrimaryKey pk = lookupPrimaryKey(dba, conn, catalog, schema, table);
                if (pk != null) header.setPrimaryKeyColumns(pk.getColumns());
            }
        }
        finally {
            DAOUtil.closeResultSet(rs);
            DAOUtil.closeResultSet(rs2);
            DAOUtil.closeStatement(stmt);
        }
        
        return ti;
    }
    
    // find the view information
    private static TableInfo lookupView(DBAdapter dba, Connection conn, 
    		String catalog, String schema, String viewName) throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (viewName == null) 
            throw new IllegalArgumentException("View name is empty.");
        
        TableInfo ti = null;
        ResultSet rs = null;
        try {
            ti = new TableInfo();
            ti.setCatalog(catalog);
            ti.setSchema(schema);
            ti.setName(viewName);
            
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getColumns(toUpperCaseIfAllowed(dba, catalog), 
            		toUpperCaseIfAllowed(dba, schema), 
            		toUpperCaseIfAllowed(dba, viewName), (String)null);
            
            RowInfo header = ti.getHeader();
            header.setResultSetMetaDataForView(rs);
            header.setCatalog(catalog);
            header.setSchema(schema);
            header.setTable(viewName);
            
            // set some table properties
            ti.setSchema(ti.getHeader().getColumnInfo(0).getSchemaName());
            ti.setCatalog(ti.getHeader().getColumnInfo(0).getCatalogName());
            ti.setType(TableInfo.TYPE_VIEW);
            
            header.setCatalog(ti.getCatalog());
            header.setSchema(ti.getSchema());
            header.setTable(ti.getName());
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        return ti;
    }
    
    /**
     * Looks up <tt>{@link com.scooterframework.orm.sqldataexpress.object.PrimaryKey PrimaryKey}</tt>.
     * 
     * <p>This method assumes that value of the <tt>tableName</tt> may 
     * take one of the following three cases:
     * <pre>
     *   {catalog}.{schema}.{table}
     *   {schema}.{table}
     *   {table}
     * </pre>
     * 
     * @param connName   db connection name
     * @param tableName  table name
     * @return <tt>{@link com.scooterframework.orm.sqldataexpress.object.PrimaryKey PrimaryKey}</tt> instance.
     */
    public static PrimaryKey lookupPrimaryKey(String connName, String tableName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
        if (tableName == null) 
            throw new IllegalArgumentException("tableName cannot be null.");

        DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
        String[] s3 = dba.resolveCatalogAndSchemaAndTable(connName, tableName);
        String catalog = s3[0];
        String schema = s3[1];
        String table = s3[2];
        
        PrimaryKey pk = DBStore.getInstance().getPrimaryKey(connName, catalog, schema, table);
        if (pk != null) return pk;
        
        String errorMessage = "Failed to get primary key for table '" + tableName + 
        		"' with database connection '" + connName + "'.";
        
        Connection connection = null;
        try {
        	connection = SqlExpressUtil.getConnection(connName);
            connection.setReadOnly(false);
            pk = lookupPrimaryKey(dba, connection, catalog, schema, table);
            
            if (pk == null) {
            	log.info(errorMessage);
            } else {
                DBStore.getInstance().addPrimaryKey(connName, catalog, schema, table, pk);
            }
        }
        catch(LookupFailureException lfEx) {
        	throw lfEx;
        }
        catch(Exception ex) {
            errorMessage += " Reason: " + ex.getMessage() + ".";
            throw new LookupFailureException(errorMessage, ex);
        }
        finally {
        	DAOUtil.closeConnection(connection);
        }
        
        return pk;
    }
    
    private static PrimaryKey lookupPrimaryKey(DBAdapter dba, Connection conn, String catalog, String schema, String table) {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (isEmpty(table)) 
            throw new IllegalArgumentException("Table name is empty for lookupTablePrimaryKey().");
        
        String fullTableName = DatabaseConfig.getInstance().getFullTableName(table);
        
        List<String> pkNames = new ArrayList<String>();
        ResultSet rs = null;
        PrimaryKey pk = null;
        try {
            catalog = toUpperCaseIfAllowed(dba, catalog);
            schema = toUpperCaseIfAllowed(dba, schema);
            fullTableName = toUpperCaseIfAllowed(dba, fullTableName);
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getPrimaryKeys(catalog, schema, fullTableName);
            while (rs.next()) {
                String _catalog = rs.getString("TABLE_CAT");
                if (catalog == null) catalog = _catalog;
                
                String _schema = rs.getString("TABLE_SCHEM");
                if (schema == null) schema = _schema;
                
                //table = rs.getString("TABLE_NAME");
                String column = rs.getString("COLUMN_NAME");
                pkNames.add(column);
            }
            if (pkNames.size() > 0) pk = new PrimaryKey(catalog, schema, table, pkNames);
        }
        catch(Exception ex) {
            throw new LookupFailureException(ex);
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        return pk;
    }
    
    /**
     * Returns total number of records in a table.
     * 
     * @param connName database connection name
     * @param table table name
     * @return total record count
     */
    public static Object countTotalRecords(String connName, String table) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");

    	if (table == null) 
    		throw new IllegalArgumentException("table cannot be null.");
    	
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	String countSQL = dba.getTotalCountSQL(connName, table);
    	
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DataProcessor.input_key_database_connection_name, connName);

        Object result = SqlServiceClient.retrieveObjectBySQL(countSQL, inputs);
    	return result;
    }
    
    /**
     * Returns finder SQL query statement.
     * 
     * @param connName database connection name
     * @param table table name
     * @return finder SQL query
     */
    public static String getFinderSQL(String connName, String table) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");

    	if (table == null) 
    		throw new IllegalArgumentException("table cannot be null.");
    	
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	return dba.getRetrieveAllSQL(connName, table);
    }
    
    /**
     * Returns expanded table name which includes catalog and schema.
     * 
     * @param connName database connection name
     * @param table table name
     * @return expanded table name
     */
    public static String getExpandedTableName(String connName, String table) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");

    	if (table == null) 
    		throw new IllegalArgumentException("table cannot be null.");
    	
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	return dba.getExpandedTableName(connName, table);
    }
    
    /**
     * Returns a map of primary key fields with values from <tt>restfuId</tt>.
     * 
     * <p>
     * For a composite primary key, the <tt>restfuId</tt> value should have 
     * separators defined by the {@link com.scooterframework.orm.sqldataexpress.config.DatabaseConfig#PRIMARY_KEY_SEPARATOR} 
     * constant.
     * </p>
     * 
     * @param ri         a RowInfo object
     * @param restfulId  restful id
     * @return map
     */
    public static Map<String, String> getTableKeyMapForRestfulId(RowInfo ri, String restfulId) {
        if (restfulId == null) throw new IllegalArgumentException("restfulId cannot be null in getTableKeyMap().");
        
        String[] ids = Converters.convertStringToStringArray(restfulId, DatabaseConfig.PRIMARY_KEY_SEPARATOR, false);
        String[] columns = ri.getPrimaryKeyColumnNames();
        
        if (columns == null) {
            columns = ri.getColumnNames();
        }
        
        if (ids.length != columns.length) {
        	if (columns.length == 1) {
        		ids[0] = restfulId;
        	}
        	else {
	            log.debug("    ids array length: " + ids.length);
	            log.debug("columns array length: " + columns.length);
	            log.debug("ri: " + ri);
	            throw new IllegalArgumentException("Input restfulId value \"" + 
	                restfulId + "\" with length " + ids.length + " does not " + 
	                "match key columns of its related table with length " + 
	                columns.length + ".");
        	}
        }
        
        int total = columns.length;
        Map<String, String> map = new HashMap<String, String>(total);
        for (int i = 0; i < total; i++) {
            String column = columns[i];
            String value = ids[i];
            map.put(column.toUpperCase(), value);
        }
        
        return map;
    }
}
