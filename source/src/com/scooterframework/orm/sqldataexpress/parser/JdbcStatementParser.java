/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatementParameter;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;


/**
 * JdbcStatementParser class.
 * 
 * @author (Fei) John Chen
 */
public class JdbcStatementParser extends JdbcStatementHelper {
    public JdbcStatementParser(UserDatabaseConnection udc, JdbcStatement st) {
        if (st == null) 
            throw new IllegalArgumentException("JdbcStatement input is null.");
        
        this.udc = udc;
        this.st = st;
        
        parseJdbcStatementString();
    }
    

    public void parse() {
        //get parameter property data if it has not been loaded
        st = furtherLookupJdbcStatement(udc, st);
        
        st.setLoadedParameterProperties(true);
        
        //display the parsed statement
        if (log.isDebugEnabled()) {
            log.debug(st);
        }
    }
    
    // populate more parameter properties for the JdbcStatement
    private JdbcStatement furtherLookupJdbcStatement(UserDatabaseConnection udc, JdbcStatement st) {
        try {
            Collection<Parameter> parameters = st.getParameters();
            Iterator<Parameter> it = parameters.iterator();
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
                    TableInfo ti = SqlExpressUtil.lookupTableInfo(udc, tableName);
                    
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
    
    
    /**
     * counts parameters (? marks)
     */
    private void parseJdbcStatementString() {
        if (st == null) return;
        
        String jdbcStatementString = st.getOriginalJdbcStatementString();
        
        //no need to parse if there is no dynamic parameter
        if (jdbcStatementString.indexOf('?') == -1) {
            return;
        }
        
        if (jdbcStatementString.startsWith("INSERT") && 
            jdbcStatementString.indexOf("SELECT") == -1) {
            // This is a pure insert statement.
            parseInsertStatement();
            return;
        }
        
        //
        // find columnName/tableName pair map
        //
        // step 1: convert the alias to regular table names
        String sql = jdbcStatementString;
        String modifiedSql = resetAlias(sql);
        
        //StringTokenizer sti = new StringTokenizer(modifiedSql, " ,><=(){}");
        StringTokenizer sti = new StringTokenizer(modifiedSql, " ,|><=(){}+-*/");
        
        int totalTokens = sti.countTokens();
        String[] tokens = new String[totalTokens];
        
        int i = 0;
        while(sti.hasMoreTokens()) {
            tokens[i] = sti.nextToken();
            i = i + 1;
        }
        
        // step 2: get the column name for each question mark
        int qmarkIndex = 1;
        for (int j = 0; j < totalTokens; j++) {
            String token = tokens[j];
            
            if (token.startsWith("?")) {
                JdbcStatementParameter param = new JdbcStatementParameter();
                param.setIndex(qmarkIndex);
                param.setName(getNameFromToken(qmarkIndex, token));
                st.addParameter(param);
                
                qmarkIndex = qmarkIndex + 1;
                
                //get inline sql data type
                int sqlDataType = getInlineSqlDataTypeFromToken(token);
                if (sqlDataType != JdbcStatementParameter.UNKNOWN_SQL_DATA_TYPE) {
                    param.setSqlDataType(sqlDataType);
                    continue;//no need to parse
                }
                
                String columnName = "";
                String tableName = "";
                int dotPosition = token.indexOf('.');
                if (dotPosition != -1) {
                    String[] results = getTableAndColumnFromDottedToken(token);
                    if (results != null) {
                        tableName = results[0];
                        columnName = results[1];
                        param.setColumnName(columnName);
                        param.setTableName(tableName);
                        continue;
                    }
                }
                
                String columnNameToken = "";
                if (j>=2 && "BETWEEN".equals(tokens[j-1])) {
                    columnNameToken = tokens[j-2];
                }
                else if (j>=4 && "BETWEEN".equals(tokens[j-3]) && "AND".equals(tokens[j-1])) {
                    columnNameToken = tokens[j-4];
                }
                else if (j>=2 && "COUNT".equals(tokens[j-1])) {
                    columnNameToken = "COUNT(*)";
                    param.setUsedByCount(true);
                }
                else if (j>=2 && "COUNT".equals(tokens[j-2])) {
                    columnNameToken = tokens[j-1];
                    param.setUsedByCount(true);
                }
                else if (j>=2 && "IN".equals(tokens[j-1])) {
                    columnNameToken = tokens[j-2];
                }
                else if (j>=2 && "LIKE".equals(tokens[j-1])) {
                    columnNameToken = tokens[j-2];
                }
                else if (!"?".equals(tokens[j-1])) {
                    columnNameToken = tokens[j-1];
                }
                else {
                    log.warn("Failed to detect column name for ? with index " + qmarkIndex + 
                             ", suggest to use inline sql type declaration if " + 
                             "the underline database doesn't support ParameterMetaData.");
                    continue;
                }
                
                dotPosition = columnNameToken.indexOf('.');
                if (dotPosition == -1) {
                    columnName = columnNameToken;
                    tableName = getTableName(j, tokens, columnName);
                }
                else {
                    columnName = columnNameToken.substring(dotPosition + 1, columnNameToken.length());
                    tableName  = columnNameToken.substring(0, dotPosition);
                }
                
                //set the found column and table names
                if (tableName != null) {
                    param.setColumnName(columnName);
                    param.setTableName(tableName);
                }
            }
        }
    }
    
    //token must a string that starts with ?
    private String[] getTableAndColumnFromDottedToken(String token) {
        if (token == null || !token.startsWith("?") ||
            (token.indexOf('.') == -1)) return null;
        
        String name = "";
        if (token.indexOf(':') != -1) {
            name = token.substring(1, token.indexOf(':'));
        }
        else {
            name = token.substring(1);
        }
        
        int dotPosition = name.indexOf('.');
        if (dotPosition == -1) {
            throw new IllegalArgumentException("Token string must be of format 'table.column:datatype' or 'table.column'.");
        }
        
        String[] results = new String[2];
        results[0] = name.substring(0, dotPosition);
        results[1] = name.substring(dotPosition + 1);
        
        return results;
    }
    
    //An insert statement has format like this:
    //
    //      INSERT INTO "table_name" ("column1", "column2", ...)
    //      VALUES ("value1", "value2", ...)
    //
    //      INSERT INTO tablename (col1, col2, col3) VALUES (?, ?, ?)
    //
    // Please note that this method does not cover those insert statements
    // with select subquery:
    //
    //      INSERT INTO "table_name1" ("column1", "column2", ...)
    //      SELECT "column3", "column4", ...
    //      FROM "table_name2"
    //
    private void parseInsertStatement() {
        if (st == null) return;
        
        String jdbcStatementString = st.getOriginalJdbcStatementString();
        
        //no need to parse if there is no dynamic parameter
        if (jdbcStatementString.indexOf('?') == -1) {
            return;
        }
        
        //
        // find columnName/tableName pair map
        //
        // step 1: convert the string to a word array
        String sql = jdbcStatementString;
        
        StringTokenizer sti = new StringTokenizer(sql, " ,()\"");
        int totalTokens = sti.countTokens();
        String[] tokens = new String[totalTokens];
        
        int i = 0;
        while(sti.hasMoreTokens()) {
            tokens[i] = sti.nextToken();
            i = i + 1;
        }
        
        if (tokens.length <= 3) 
            throw new IllegalArgumentException("Cannot parse sql statement: [" + sql + "]");
        
        if (tokens[3].equals("VALUES")) 
            throw new IllegalArgumentException("Parser for insert statement " +
                "without column names specified has yet to be developed.");
        
        // step 2: get the table name
        String tableName = tokens[2];
        
        // step 3: get the column name for each question mark
        int valuesIndex = 0;
        List<String> columns = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for (int j = 3; j < totalTokens; j++) {
            if (tokens[j].equals("VALUES")) {
                valuesIndex = j;
                break;
            }
            columns.add(tokens[j]);
        }
        
        for (int k=valuesIndex+1; k < totalTokens; k++) {
            values.add(tokens[k]);
        }
        
        // find matching column/value pair
        int length = columns.size();
        if (values.size() != length) 
            throw new IllegalArgumentException("The number of columns does not " + 
                    "match the number of values.");
        
        int qmarkIndex = 1;
        for (int l = 0; l < length; l++) {
            String columnName = columns.get(l);
            String value = values.get(l);
            if (value.startsWith("?")) {
                JdbcStatementParameter param = new JdbcStatementParameter();
                param.setIndex(qmarkIndex);
                param.setColumnName(columnName);
                param.setTableName(tableName);
                param.setName(getNameFromToken(qmarkIndex, value));
                st.addParameter(param);
                
                //get inline sql data type
                int sqlDataType = getInlineSqlDataTypeFromToken(value);
                if (sqlDataType != JdbcStatementParameter.UNKNOWN_SQL_DATA_TYPE) {
                    param.setSqlDataType(sqlDataType);
                    continue;//no need to parse
                }
                
                qmarkIndex = qmarkIndex + 1;
            }
        }
    }
    
    // find the table name for the column corresponding to the ? mark
    private String getTableName(int qmarkPosition, String[] tokens, String columnName) {
        String tableName = "";
        
        for (int i = qmarkPosition; i >= 0; i--) {
            String token = tokens[i];
            
            if ("INSERT".equals(token)) {
                tableName = tokens[i+2];
            }
            else if ("UPDATE".equals(token)) {
                tableName = tokens[i+1];
            }
            else if ("DELETE".equals(token)) {
                if ("FROM".equalsIgnoreCase(tokens[i+1])) {
                    tableName = tokens[i+2];
                }
                else {
                    tableName = tokens[i+1];
                }
            }
            else if ("SELECT".equals(token)) {
                tableName = getTableNameForSelectStatement(qmarkPosition, tokens, columnName);
            }
        }
        
        log.debug("Leave getTableName: found table name " + tableName + " for column " + columnName);
        
        return tableName;
    }
    
    private String getTableNameForSelectStatement(int qmarkPosition, String[] tokens, String columnName) {
        String tableName = null;
        
        int fromPosition = -1;
        int wherePosition = -1;
        for (int i = qmarkPosition; i >= 0; i--) {
            String token = tokens[i];
            if ("WHERE".equals(token)) {
                wherePosition = i;
            }
            else if ("FROM".equals(token)) {
                fromPosition = i;
                break;
            }
        }
        
        // get all words between from and where or between from and the next 
        // database key words like "order by", etc. 
        // This code is very painful.
        if (fromPosition != -1 && wherePosition != -1) {
            // both FROM and WHERE are present
            if (wherePosition - fromPosition == 2) {
                tableName = tokens[fromPosition + 1];
            }
            else {
                //need to check all tokens one by one
                //stop when the first table name match is found.
                int startIndex = fromPosition + 1;
                int endIndex = wherePosition -1;
                for (int i = startIndex; i <= endIndex; i++) {
                    String potentialTableName = tokens[i];
                    if (isColumnInTable(columnName, potentialTableName)) {
                        tableName = potentialTableName;
                        break;
                    }
                }
                
            }
        }
        else 
        if (fromPosition != -1 && wherePosition == -1) {
            if (qmarkPosition >= fromPosition + 3) {
                // case: FROM tbl ORDER BY
                if ("ORDER".equals(tokens[fromPosition + 2]) && 
                    "BY".equals(tokens[fromPosition + 3])) {
                    tableName = tokens[fromPosition + 1];
                }
                else
                // case: FROM tbl GROUP BY
                if ("GROUP".equals(tokens[fromPosition + 2]) && 
                    "BY".equals(tokens[fromPosition + 3])) {
                    tableName = tokens[fromPosition + 1];
                }
                
                if (!isColumnInTable(columnName, tableName)) {
                    tableName = null;
                }
            }
        }
        
        return tableName;
    }
    
    private boolean isColumnInTable(String columnName, String potentialTableName) {
        boolean bMatch = false;
        
        try {
            TableInfo ti = SqlExpressUtil.lookupTableInfo(udc, potentialTableName);
            bMatch = ti.getHeader().isValidColumnName(columnName);
        }
        catch(Exception ex) {
            log.error("Failed in isColumnInTable method for column \"" + 
            		columnName + "\" and table \"" + potentialTableName + 
            		"\" because " + ex.getMessage());
        }
        
        return bMatch;
    }

    private UserDatabaseConnection udc;
    private JdbcStatement st;
}
