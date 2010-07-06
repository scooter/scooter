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
import java.util.Collection;
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
import com.scooterframework.orm.sqldataexpress.object.JdbcStatementParameter;
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
    	"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_$";
    
    private static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }
    
    private static String toUpperCase(String s) {
        return (s == null)?null:s.toUpperCase();
    }
    
    private static String validateValueAndToUpperCase(String s) {
        return isEmpty(s)?null:s.toUpperCase();
    }

    public static List getConnectionNames() {
        List dbs = new ArrayList();
        Iterator it = DatabaseConfig.getInstance().getPredefinedDatabaseConnectionNames();
        while(it.hasNext()) {
            dbs.add(it.next());
        }
        Collections.sort(dbs);
        return dbs;
    }
    
    /**
     * Returns properties associated with a database connection name.
     * 
     * @param connName database connection name
     * @return properties of the connection name
     */
    public static Properties getConnectionProperties(String connName) {
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
    
    /**
     * Checks if the underline connection is for Oracle database.
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
     * Returns a UserDatabaseConnection instance for default connection name.
     */
    public static UserDatabaseConnection getUserDatabaseConnection() throws SQLException {
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection();
    }
    
    /**
     * Returns a UserDatabaseConnection instance for a specific connection name.
     * 
     * @param connName     name of a connection
     */
    public static UserDatabaseConnection getUserDatabaseConnection(String connName) throws SQLException {
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(connName);
    }
    
    /**
     * Returns a UserDatabaseConnection instance for a specific database 
     * connection context.
     * 
     * @param dcc a DatabaseConnectionContext instance
     */
    public static UserDatabaseConnection getUserDatabaseConnection(DatabaseConnectionContext dcc) throws SQLException {
        return UserDatabaseConnectionFactory.getInstance().createUserDatabaseConnection(dcc);
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
     * Returns a list of TableInfo instances for the database connection.
     * 
     * @param conn              the database connection
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <tt>null</tt> means that the catalog name should not be used to narrow
     *        the search
     * @param schema a schema name; must match the schema name
     *        as it is stored in the database; "" retrieves those without a schema;
     *        <tt>null</tt> means that the schema name should not be used to narrow
     *        the search
     * @param tableName a table name; must match the
     *        table name as it is stored in the database 
     * @param types a list of table types to include; <tt>null</tt> returns all types 
     * @return a list of TableInfo instances
     * @throws java.sql.SQLException
     */
    public static List getDatabaseTables(Connection conn, 
                                                String catalog, 
                                                String schema, 
                                                String tableName, 
                                                String[] types) throws SQLException {
        List list = new ArrayList();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(toUpperCase(catalog), toUpperCase(schema), 
        		toUpperCase(tableName), types);
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
        return list;
    }
    
    /**
     * Returns a list of TableInfo instances for the database connection.
     * 
     * @param conn              the database connection
     * @return a list of TableInfo instances
     * @throws java.sql.SQLException
     */
    public static List getDatabaseTables(Connection conn) throws SQLException {
        return getDatabaseTables(conn, null, null, null, (String[])null);
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
            
            boolean foundPlSqlRecord = false;//will skip all output columns when a PL/SQL record is found.//TODO: This code is tied to Oracle. Change it.
            boolean startToRecord = false;
            int previousIndex = -1;
            
            DatabaseMetaData dbmd = connection.getMetaData();
            String vendor = getDatabaseVendor(dbmd);
            rs = dbmd.getProcedureColumns(toUpperCase(catalog), 
            		toUpperCase(schema), toUpperCase(api), null);
            
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
        
        if (sp == null) throw new UnsupportedStoredProcedureAPINameException("Failed to find db function with name " + name);
        
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
            
            boolean foundPlSqlRecord = false;//will skip all output columns when a PL/SQL record is found. //TODO: This code is tied to Oracle. Change it.
            boolean startToRecord = false;
            int previousIndex = -1;
            
            DatabaseMetaData dbmd = connection.getMetaData();
            String vendor = getDatabaseVendor(dbmd);
            rs = dbmd.getProcedureColumns(toUpperCase(catalog), 
            		toUpperCase(schema), toUpperCase(api), null);
            
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
        
        if (sp == null) throw new UnsupportedStoredProcedureAPINameException("Failed to find stored procedure with name " + name);
        
        return sp;
    }
    
    // find the jdbc statement from property file
    public static JdbcStatement createJdbcStatement(String name) {
        if (name == null) 
            throw new IllegalArgumentException("SQL statement name is empty.");
        
        String jdbcStatementString = SqlConfig.getInstance().getSql(name);
        if (jdbcStatementString == null || "".equals(jdbcStatementString.trim())) 
            throw new LookupFailureException("There is no sql statement for " + name + ".");
        
        JdbcStatement st = new JdbcStatement(name);
        st.setJdbcStatementString(jdbcStatementString);
        
        return st;
    }
    
    // find the jdbc statement from cache
    public static JdbcStatement createJdbcStatementDirect(String jdbcStatementString) {
        if (jdbcStatementString == null) 
            throw new IllegalArgumentException("SQL statement string is empty.");
        
        // Use the input sqlStatementString as name key
        JdbcStatement st = new JdbcStatement(jdbcStatementString);
        st.setJdbcStatementString(jdbcStatementString);
        
        return st;
    }
    
    // populate more parameter properties for the JdbcStatement
    public static JdbcStatement furtherLookupJdbcStatement(UserDatabaseConnection udc, JdbcStatement st) {
        if (st == null) 
            throw new IllegalArgumentException("JdbcStatement object is empty.");
        
        try {
            Collection parameters = st.getParameters();
            Iterator it = parameters.iterator();
            while(it.hasNext()) {
                JdbcStatementParameter jdbcParam = (JdbcStatementParameter)it.next();
                if (jdbcParam.isUsedByCount()) continue;
                if (jdbcParam.getSqlDataType() != Parameter.UNKNOWN_SQL_DATA_TYPE) {
                    //do not furtherLookup if the sql data type is already known.
                    continue;
                }
                
                String tableName = jdbcParam.getTableName();
                String columnName = jdbcParam.getColumnName();
                
                int sqlDataType = 0;
                String sqlDataTypeName = null;
                String javaClassName = null;
                
                if (tableName != null && columnName != null) {
                    // find more properties of this column
                    TableInfo ti = DBStore.getInstance().getTableInfo(tableName);
                    if (ti == null) {
                        // lookup 
                        ti = lookupTableInfo(udc, tableName);
                    }
                    
                    // add more properties for this column
                    RowInfo header = ti.getHeader();
                    int columnIndex = header.getColumnPositionIndex(columnName);
                    
                    sqlDataType = header.getColumnSqlDataType(columnIndex);
                    sqlDataTypeName = header.getColmnDataTypeName(columnIndex);
                    javaClassName = header.getColumnJavaClassName(columnIndex);
                    
                    jdbcParam.setSqlDataType(sqlDataType);
                    jdbcParam.setSqlDataTypeName(sqlDataTypeName);
                    jdbcParam.setJavaClassName(javaClassName);
                }
                else {
                    log.error("Can not detecting parameter properties because " + 
                              "either table name or column name is null for the " + 
                              "parameter with index " + jdbcParam.getIndex());
                }
            }
        }
        catch(Exception ex) {
            log.error("Error in furtherLookupJdbcStatement() because of " + ex.getMessage());
        }
        
        return st;
    }
    
    public static TableInfo lookupAndRegisterTable(String connName, String table) {
        if (table == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        String errorMessage = "Failed to get meta data info of table " + table + 
        		" with database connection named \"" + connName;
        TableInfo ti = DBStore.getInstance().getTableInfo(table);
        if (ti == null) {
        	UserDatabaseConnection udc = null;
            try {
                udc = SqlExpressUtil.getUserDatabaseConnection(connName);
                ti = lookupTableInfo(udc, table);
            }
            catch(Exception ex) {
            	ex.printStackTrace();
                errorMessage += ". Reason: " + ex.getMessage() + ".";
            }
            finally {
                DAOUtil.closeConnection(udc);
            }
        }
        
        if (ti == null) {
            throw new LookupFailureException(errorMessage);
        }
        return ti;
    }
    
    // find table or view info
    // 
    public static TableInfo lookupTableInfo(UserDatabaseConnection udc, String tableName) {
        if (udc == null) 
            throw new IllegalArgumentException("UserDatabaseConnection udc is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        // check if it is a table or a view
        String catalog = null;
        String schema = null;
        TableInfo ti = null;
        try {
        	String[] s2 = getCatalogAndSchema(udc.getConnectionName());
        	catalog = s2[0];
        	schema = s2[1];
        	
            String tableType = TableInfo.TYPE_TABLE; //default
            
            tableType = getTableType(udc.getConnection(), catalog, schema, 
            		tableName, TableInfo.SUPPORTED_TYPES);
            if (TableInfo.TYPE_TABLE.equals(tableType)) {
            	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
                ti = lookupTable(dba, udc.getConnection(), catalog, schema, tableName);
            }
            else if (TableInfo.TYPE_VIEW.equals(tableType)){
            	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
                ti = lookupView(dba, udc.getConnection(), catalog, schema, tableName);
            }
            else {
                throw new SQLException("Unknown table type: " + tableType + 
                		". Only TABLE and VIEW are supported.");
            }
            
            if (ti == null) 
                throw new LookupFailureException("Failed to find table info for table " + tableName + ".");
            
            DBStore.getInstance().addTableInfo(tableName, ti);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            log.error("Exception in lookupTableInfo: catalog=" + catalog + 
            		", schema=" + schema + ", table=" + tableName + 
            		", because " + ex);
            throw new LookupFailureException(ex);
        }
        
        return ti;
    }
    
    // find table type: TABLE or VIEW
    private static String getTableType(Connection conn, String catalog, 
    		String schema, String tableName, String[] supportedTypes) 
    throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        // check if it is a table or a view
        String tableType = TableInfo.TYPE_TABLE; //default
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(toUpperCase(catalog), toUpperCase(schema), 
            		toUpperCase(tableName), supportedTypes);
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
    	String catalog, String schema, String tableName) throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        TableInfo ti = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        
        try {
            String sqlString = dba.getOneRowSelectSQL(catalog, schema, tableName);
            
            stmt = conn.createStatement();

            // Query the table
            rs = stmt.executeQuery(sqlString);
            ti = new TableInfo();
            ti.setCatalog(catalog);
            ti.setSchema(schema);
            ti.setName(tableName);
            
            RowInfo header = new RowInfo(tableName, rs.getMetaData());
            header.setCatalog(catalog);
            header.setSchema(schema);
            header.setTable(tableName);
            
            ti.setHeader(header);
            DAOUtil.closeResultSet(rs);
            
            // set some table properties
            //ti.setSchema(ti.getHeader().getColumnInfo(0).getSchemaName());
            //ti.setCatalog(ti.getHeader().getColumnInfo(0).getCatalogName());
            
            // get primary keys
            if (!header.hasPrimaryKey()) {
                PrimaryKey pk = lookupTablePrimaryKey(conn, catalog, schema, tableName);
                if (pk != null) header.setPrimaryKeyColumns(pk.getColumns());
            }
        }
        catch(SQLException ex) {
            throw ex;
        }
        finally {
            DAOUtil.closeResultSet(rs);
            DAOUtil.closeResultSet(rs2);
            DAOUtil.closeStatement(stmt);
        }
        
        if (ti == null) throw new LookupFailureException("Failed to find table info with name " + tableName);
        
        return ti;
    }
    
    // find the view information
    private static TableInfo lookupView(DBAdapter dba, Connection conn, String catalog, 
    		String schema, String tableName) throws SQLException {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (tableName == null) 
            throw new IllegalArgumentException("Table name is empty.");
        
        TableInfo ti = null;
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getColumns(toUpperCase(catalog), toUpperCase(schema), 
            		toUpperCase(tableName), (String)null);
            
            ti = new TableInfo();
            ti.setCatalog(catalog);
            ti.setSchema(schema);
            ti.setName(tableName);
            
            RowInfo header = ti.getHeader();
            header.setResultSetMetaDataForView(rs);
            header.setCatalog(catalog);
            header.setSchema(schema);
            header.setTable(tableName);
            
            // set some table properties
            ti.setSchema(ti.getHeader().getColumnInfo(0).getSchemaName());
            ti.setCatalog(ti.getHeader().getColumnInfo(0).getCatalogName());
            ti.setType(TableInfo.TYPE_VIEW);
            
            header.setCatalog(ti.getCatalog());
            header.setSchema(ti.getSchema());
            header.setTable(ti.getName());
        }
        catch(SQLException ex) {
            throw ex;
        }
        finally {
            DAOUtil.closeResultSet(rs);
        }
        
        if (ti == null) throw new LookupFailureException("Failed to find table info with name " + tableName);
        
        return ti;
    }
    
    public static PrimaryKey lookupAndRegisterPrimaryKeyForDefaultConnection(String catalog, String schema, String table) {
        if (table == null || "".equals(table)) 
            throw new IllegalArgumentException("Table name is empty in lookupAndRegisterPrimaryKey().");
        
        PrimaryKey pk = null;
        if (isEmpty(catalog) && isEmpty(schema)) {
        	String defaultConnectionName = DatabaseConfig.getInstance().getDefaultDatabaseConnectionName();
        	String[] s2 = SqlExpressUtil.getCatalogAndSchema(defaultConnectionName);
        	catalog = s2[0];
        	schema = s2[1];
        }
    	
        pk = DBStore.getInstance().getPrimaryKey(catalog, schema, table);
        if (pk == null) {
            pk = lookupTablePrimaryKeyForDefaultConnection(catalog, schema, table);
            if (pk != null) {
                DBStore.getInstance().addPrimaryKey(catalog, schema, table, pk);
            }
        }
        return pk;
    }
    
    /**
     * Looks up primary key of a table.
     * 
     * @param catalog
     * @param schema
     * @param table
     * @return PrimaryKey
     */
    public static PrimaryKey lookupTablePrimaryKeyForDefaultConnection(String catalog, String schema, String table) {
        Connection connection = null;
        PrimaryKey pk = null;
        String errorMessage = "Failed to get primary key for table " + table;
        try {
            if (isEmpty(catalog) && isEmpty(schema)) {
            	String defaultConnectionName = DatabaseConfig.getInstance().getDefaultDatabaseConnectionName();
            	String[] s2 = SqlExpressUtil.getCatalogAndSchema(defaultConnectionName);
            	catalog = s2[0];
            	schema = s2[1];
            }
            
            connection = SqlExpressUtil.getConnection();
            connection.setReadOnly(false);
            pk = lookupTablePrimaryKey(connection, catalog, schema, table);
        }
        catch(Exception ex) {
            errorMessage += ", because " + ex.getMessage() + ".";
            log.error(errorMessage);
        }
        finally {
            DAOUtil.closeConnection(connection);
        }
        return pk;
    }
    
    /**
     * Looks up primary key of a table.
     * 
     * @param conn
     * @param catalog
     * @param schema
     * @param table
     * @return PrimaryKey
     */
    public static PrimaryKey lookupTablePrimaryKey(Connection conn, String catalog, String schema, String table) {
        if (conn == null) 
            throw new IllegalArgumentException("Connection is null.");
        
        if (isEmpty(table)) 
            throw new IllegalArgumentException("Table name is empty for lookupTablePrimaryKey().");
        
        List pkNames = new ArrayList();
        ResultSet rs = null;
        PrimaryKey pk = null;
        try {
            catalog = validateValueAndToUpperCase(catalog);
            schema = validateValueAndToUpperCase(schema);
            table = validateValueAndToUpperCase(table);
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getPrimaryKeys(catalog, schema, table);
            while (rs.next()) {
                //catalog = rs.getString("TABLE_CAT");
                //schema = rs.getString("TABLE_SCHEM");
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
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(connName);
    	String countSQL = dba.getTotalCountSQL(connName, table);
    	
        Map inputs = new HashMap();
        inputs.put(DataProcessor.input_key_database_connection_name, connName);

        Object result = SqlServiceClient.retrieveObjectBySQL(countSQL, inputs);
    	return result;
    }
    
    /**
     * Returns finder sql
     * 
     * @param connName database connection name
     * @param table table name
     * @return finder sql
     */
    public static String getFinderSQL(String connName, String table) {
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
    public static Map getTableKeyMapForRestfulId(RowInfo ri, String restfulId) {
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
        Map map = new HashMap(total);
        for (int i = 0; i < total; i++) {
            String column = columns[i];
            String value = ids[i];
            map.put(column.toUpperCase(), value);
        }
        
        return map;
    }
}
