/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.util.List;
import java.util.Map;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorTypes;


/**
 * SqlServiceClient class has helper methods on using SqlService. 
 * 
 * @author (Fei) John Chen
 */
public class SqlServiceClient {
    
    /**
     * Retrieves TableData from database based on the SQL input.
     * 
     * @param sql a valid SQL statement string
     * @return a TableData instance returned from the database.
     */
    public static TableData retrieveTableDataBySQL(String sql) {
        return retrieveTableDataBySQL(sql, null);
    }
    
    /**
     * Retrieves TableData from database based on the SQL input.
     * 
     * @param sql a valid SQL statement string
     * @param inputs a map of name and value pairs
     * @return a TableData instance returned from the database.
     */
    public static TableData retrieveTableDataBySQL(String sql, Map<String, Object> inputs) {
        TableData td = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, sql);
            
            if ( returnTO != null ) {
                td = returnTO.getTableData(sql);
            }
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return td;
    }
    
    /**
     * Retrieves TableData from database based on the SQL query related to 
     * the SQL key.
     * 
     * @param sqlKey    key to a SQL statement
     * @return a TableData instance returned from the database.
     */
    public static TableData retrieveTableDataBySQLKey(String sqlKey) {
        return retrieveTableDataBySQLKey(sqlKey, null);
    }
    
    /**
     * Retrieves TableData from database based on the SQL query related to 
     * the SQL key.
     * 
     * @param sqlKey    key to a SQL statement
     * @param inputs a map of name and value pairs
     * @return a TableData instance returned from the database.
     */
    public static TableData retrieveTableDataBySQLKey(String sqlKey, Map<String, Object> inputs) {
        TableData td = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, sqlKey);
            
            if ( returnTO != null ) {
                td = returnTO.getTableData(sqlKey);
            }
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return td;
    }
    
    /**
     * Retrieves RowData list from database based on the SQL query related to 
     * the SQL key.
     * 
     * @param sqlKey    key to a SQL statement
     * @return a list of RowData objects returned from the database.
     */
    public static List<RowData> retrieveRowsBySQLKey(String sqlKey) {
        return retrieveRowsBySQLKey(sqlKey, null);
    }
    
    /**
     * Retrieves RowData list from database based on the SQL query related to 
     * the SQL key.
     * 
     * @param sqlKey    key to a SQL statement
     * @param inputs    a map of name and value pairs
     * @return a lList of RowData objects returned from the database.
     */
    public static List<RowData> retrieveRowsBySQLKey(String sqlKey, Map<String, Object> inputs) {
        TableData td = retrieveTableDataBySQLKey(sqlKey, inputs);
        if (td != null) return td.getAllRows();
        return null;
    }
    
    /**
     * Retrieves RowData list from database based on the SQL input.
     * 
     * @param sql       a SQL statement
     * @return a list of RowData objects returned from the database.
     */
    public static List<RowData> retrieveRowsBySQL(String sql) {
        return retrieveRowsBySQL(sql, null);
    }
    
    /**
     * Retrieves RowData list from database based on the SQL input.
     * 
     * @param sql       a SQL statement
     * @param inputs    a map of name and value pairs
     * @return a list of RowData objects returned from the database.
     */
    public static List<RowData> retrieveRowsBySQL(String sql, Map<String, Object> inputs) {
        TableData td = retrieveTableDataBySQL(sql, inputs);
        if (td != null) return td.getAllRows();
        return null;
    }
    
    
    
    /**
     * Retrieves one RowData from database based on the SQL query related to 
     * the SQL key. The first row of the query result is returned.
     * 
     * @param sqlKey    key to a SQL statement
     * @return a RowData object returned from the database.
     */
    public static RowData retrieveOneRowsBySQLKey(String sqlKey) {
        return retrieveOneRowBySQLKey(sqlKey, null);
    }
    
    /**
     * Retrieves one RowData from database based on the SQL query related to 
     * the SQL key. The first row of the query result is returned.
     * 
     * @param sqlKey    key to a SQL statement
     * @param inputs    a map of name and value pairs
     * @return a RowData object returned from the database.
     */
    public static RowData retrieveOneRowBySQLKey(String sqlKey, Map<String, Object> inputs) {
        TableData td = retrieveTableDataBySQLKey(sqlKey, inputs);
        if (td != null) return td.getFirstRow();
        return null;
    }
    
    /**
     * Retrieves one RowData from database based on the SQL input. 
     * The first row of the query result is returned.
     * 
     * @param sql       a SQL statement
     * @return a RowData object returned from the database.
     */
    public static RowData retrieveOneRowBySQL(String sql) {
        return retrieveOneRowBySQL(sql, null);
    }
    
    /**
     * Retrieves one RowData from database based on the SQL input. 
     * The first row of the query result is returned.
     * 
     * @param sql       a SQL statement
     * @param inputs    a map of name and value pairs
     * @return a RowData object returned from the database.
     */
    public static RowData retrieveOneRowBySQL(String sql, Map<String, Object> inputs) {
        TableData td = retrieveTableDataBySQL(sql, inputs);
        if (td != null) return td.getFirstRow();
        return null;
    }
    
    
    /**
     * Retrieves one object from database based on the SQL input. The object 
     * of the first column of the first row is returned.
     * 
     * @param sql       a SQL statement
     * @return an Object value returned from the database.
     */
    public static Object retrieveObjectBySQL(String sql) {
        return retrieveObjectBySQL(sql, null);
    }
    
    /**
     * Retrieves one object from database based on the SQL input. The object 
     * of the first column of the first row is returned.
     * 
     * @param sql       a SQL statement
     * @param inputs    a map of name and value pairs
     * @return an Object value returned from the database.
     */
    public static Object retrieveObjectBySQL(String sql, Map<String, Object> inputs) {
        Object returnObj = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, sql);
            
            returnObj = processResult(returnTO, sql);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        return returnObj;
    }
    
    /**
     * Retrieves an object from database based on the SQL query related to 
     * the SQL key. The object of the first column of the first row is returned.
     * 
     * @param sqlKey    key to a SQL statement
     * @return an Object value returned from the database.
     */
    public static Object retrieveObjectBySQLKey(String sqlKey) {
        return retrieveObjectBySQLKey(sqlKey, null);
    }
    
    /**
     * Retrieves an object from database based on the SQL query related to 
     * the sql key. The object of the first column of the first row is returned.
     * 
     * @param sqlKey    key to a SQL statement
     * @param inputs    a map of name and value pairs
     * @return an Object value returned from the database.
     */
    public static Object retrieveObjectBySQLKey(String sqlKey, Map<String, Object> inputs) {
        Object returnObj = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, sqlKey);
            
            returnObj = processResult(returnTO, sqlKey);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return returnObj;
    }
    
    /**
     * Executes a non-select SQL statement and returns count of updated rows.
     * 
     * @param sql       a SQL statement
     * @return updated row count
     */
    public static int executeSQL(String sql) {
        return executeSQL(sql, null);
    }
    
    /**
     * Executes a non-select SQL statement and returns count of updated rows.
     *
     * @param sql       a SQL statement
     * @param inputs    a map of name and value pairs
     * @return updated row count
     */
    public static int executeSQL(String sql, Map<String, Object> inputs) {
        int rowCount = -1;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, sql);
            
            rowCount = returnTO.getUpdatedRowCount();
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        return rowCount;
    }
    
    /**
     * Retrieves an object from database based on the SQL query related to 
     * the SQL key. 
     * 
     * @param sqlKey    key to a SQL statement
     * @return updated row count
     */
    public static int executeSQLByKey(String sqlKey) {
        return executeSQLByKey(sqlKey, null);
    }
    
    /**
     * Executes a non-select SQL statement corresponding to a name key and 
     * returns count of updated rows. 
     *
     * @param sqlKey    key to a SQL statement
     * @param inputs    a map of name and value pairs
     * @return updated row count
     */
    public static int executeSQLByKey(String sqlKey, Map<String, Object> inputs) {
        int rowCount = -1;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, sqlKey);
            
            rowCount = returnTO.getUpdatedRowCount();
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        return rowCount;
    }
    
    /**
     * Returns SqlService
     * 
     * @return SqlService
     */
    public static SqlService getSqlService() {
        return SqlServiceConfig.getSqlService();
    }
    
    private static Object processResult(OmniDTO returnTO, String name) {
        Object returnObj = null;
        
        if ( returnTO != null ) {
            TableData rt = returnTO.getTableData(name);
            if (rt != null) {
                returnObj = rt.getFirstObject();
            }
        }
        
        return returnObj;
    }
}
