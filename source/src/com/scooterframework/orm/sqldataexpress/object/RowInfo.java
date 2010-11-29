/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.exception.FailureDetectingRowMetaDataException;
import com.scooterframework.orm.sqldataexpress.exception.InvalidColumnNameException;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;

/**
 * <p>RowInfo class holds config information about a row.</p>
 * 
 * <p>The table field applies only to the sql query for a single table. If a 
 * query is related to multiple tables (joins), This field records the last 
 * table in the join statement. To find the table for an individual column, you 
 * need to navigate to the ColumnInfo object. </p>
 * 
 * @author (Fei) John Chen
 */
public class RowInfo implements Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 7482394946164624011L;
	
	public RowInfo() {}

    public RowInfo(String name) {
        this.name = name;
    }
    
    public RowInfo(String name, ResultSet rs) {
        this.name = name.toUpperCase();
        parseResultSet(rs);
    }
    
    public RowInfo(String name, ResultSetMetaData rsmd) {
        this.name = name.toUpperCase();
        parseResultSetMetaData(rsmd);
    }
    
    
    /**
     * returns name
     */
    public String getName() {
        return name;
    }
    
    /**
     * returns table name
     */
    public String getTable() {
        return table;
    }
    
    /**
     * sets table name
     */
    public void setTable(String table) {
    	if (isEmpty(table)) return;
        this.table = table;
    }
    
    /**
     * returns catalog
     */
    public String getCatalog() {
        return catalog;
    }
    
    /**
     * sets catalog
     */
    public void setCatalog(String catalog) {
    	if (isEmpty(catalog)) return;
        this.catalog = catalog;
    }
    
    /**
     * returns schema
     */
    public String getSchema() {
        return schema;
    }
    
    /**
     * sets schema
     */
    public void setSchema(String schema) {
    	if (isEmpty(schema)) return;
        this.schema = schema;
    }
    
    /**
     * sets meta data for the row
     */
    public void setResultSetMetaDataForView(ResultSet rs) {
        parseResultSetForView(rs);
    }
    
    /**
     * returns dimension
     */
    public int getDimension() {
        return dimension;
    }
    
    /**
     * returns columnName 
     * index - the first column is 0, the second is 1, ...
     */
    public String getColumnName(int index) {
        return columnNames[index];
    }

    /**
     * returns columnNames
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * returns primary key columnNames
     */
    public String[] getPrimaryKeyColumnNames() {
        return primaryKeyColumnNames;
    }

    /**
     * returns readonly columnNames
     */
    public List getReadOnlyColumnNames() {
        return readOnlyColumnNames;
    }
    
    /**
     * returns column data type
     * index - the first column is 0, the second is 1, ...
     */
    public int getColumnSqlDataType(int index) {
        return columnSqlDataTypes[index];
    }
    
    /**
     * returns columnSqlDataTypes
     */
    public int[] getSqlDataType() {
        return columnSqlDataTypes;
    }
    
    /**
     * returns column data type name
     * index - the first column is 0, the second is 1, ...
     */
    public String getColmnDataTypeName(int index) {
        return columnSqlDataTypeNames[index];
    }
    
    /**
     * returns columnSqlDataTypeNames
     */
    public String[] getColmnSqlDataTypeNames() {
        return columnSqlDataTypeNames;
    }
    
    /**
     * returns column java class name
     * index - the first column is 0, the second is 1, ...
     */
    public String getColumnJavaClassName(int index) {
        return columnJavaClassNames[index];
    }

    /**
     * returns columnJavaClassNames
     */
    public String[] getColumnJavaClassNames() {
        return columnJavaClassNames;
    }
    
    /**
     * returns column position index
     * 
     * The index for the first column is 0, the second is 1, ...
     */
    public int getColumnPositionIndex(String colName) {
        Integer index = (Integer)nameIndexMap.get(colName.toUpperCase());
        if (index == null) 
            throw new InvalidColumnNameException("There is no column named " + colName + ".");
        return index.intValue();
    }
    
    /**
     * returns columnInfo specified by column index.
     * 
     * The index for the first column is 0, the second is 1, ...
     */
    public ColumnInfo getColumnInfo(int index) {
        return (ColumnInfo)columnInfos.get(index);
    }
    
    /**
     * returns columnInfo specified by column name.
     * 
     */
    public ColumnInfo getColumnInfo(String columnName) {
        return getColumnInfo(getColumnPositionIndex(columnName));
    }
    
    /**
     * returns a list of ColumnInfo instances. 
     */
    public List columns() {
        return columnInfos;
    }
    
    /**
     * sets columnInfo list
     */
    public void setColumnInfoList(List newColumnInfoList) {
        clearContent();
        
        if (newColumnInfoList == null || newColumnInfoList.size() == 0) return;
        
        dimension = newColumnInfoList.size();
        
        columnNames = new String[dimension];
        columnSqlDataTypes = new int[dimension];
        columnSqlDataTypeNames = new String[dimension];
        columnJavaClassNames = new String[dimension];
        List primaryKeyColumns = new ArrayList();
        for (int i = 0; i < dimension; i++) {
            ColumnInfo ci = (ColumnInfo)newColumnInfoList.get(i);
            
            nameIndexMap.put(ci.getColumnName(), new Integer(i));
            
            columnNames[i] = ci.getColumnName();
            columnSqlDataTypes[i] = ci.getSQLDataType();
            columnSqlDataTypeNames[i] = ci.getColumnTypeName();
            columnJavaClassNames[i] = ci.getColumnClassName();

            setCatalog(ci.getCatalogName());
            setSchema(ci.getSchemaName());
            setTable(ci.getTableName());
            columnInfos.add(i, ci);
            
            if (ci.isPrimaryKey()) primaryKeyColumns.add(ci.getColumnName());
            if (ci.isReadOnly()) readOnlyColumnNames.add(ci.getColumnName());
        }
        
        int pkSize = primaryKeyColumns.size();
        if (pkSize > 0) {
            primaryKeyColumnNames = new String[pkSize];
            System.arraycopy(primaryKeyColumns.toArray(), 0, primaryKeyColumnNames, 0, pkSize);
        }
        
        //if (!hasPrimaryKey()) {
        //    detectPrimaryKey();
        //}
    }
    
    private void detectPrimaryKey() {
        if (table == null || "".equals(table)) return;
        PrimaryKey pk = SqlExpressUtil.lookupAndRegisterPrimaryKeyForDefaultConnection(catalog, schema, table);
        if (pk == null) return;
        
        List primaryKeyColumns = new ArrayList();
        
        for (int i = 0; i < dimension; i++) {
            ColumnInfo ci = (ColumnInfo)columnInfos.get(i);
            String column = ci.getColumnName();
            if (pk.hasColumn(column)) {
                ci.setPrimaryKey(true);
                primaryKeyColumns.add(column);
            }
        }
        
        int pkSize = primaryKeyColumns.size();
        if (pkSize > 0) {
            primaryKeyColumnNames = new String[pkSize];
            System.arraycopy(primaryKeyColumns.toArray(), 0, primaryKeyColumnNames, 0, pkSize);
        }
    }
    
    /**
     * sets primary key columns for the row
     */
    public void setPrimaryKeyColumns(String[] primaryKeyNames) {
        if (primaryKeyNames != null && primaryKeyNames.length > 0) {
            List pkNameSet = new ArrayList();
            int size = primaryKeyNames.length;
            for (int i=0; i<size; i++) {
                pkNameSet.add(primaryKeyNames[i]);
            }
            setPrimaryKeyColumns(pkNameSet);
        }
    }
    
    /**
     * sets primary key columns for the row
     */
    public void setPrimaryKeyColumns(List primaryKeyNames) {
        if (primaryKeyNames == null || primaryKeyNames.size() == 0) return;
        
        if (columnInfos == null || columnInfos.size() == 0) 
            throw new IllegalStateException("Columns must be populated first before adding primary keys.");
        
        List tmp = convertToUpperCase(primaryKeyNames);
        
        ColumnInfo ci = null;
        List acceptedPrimaryKeyNames = new ArrayList();
        for (int i = 0; i < dimension; i++) {
            ci = (ColumnInfo)columnInfos.get(i);
            if ( tmp.contains(ci.getColumnName()) ) {
                ci.setPrimaryKey(true);
                acceptedPrimaryKeyNames.add(ci.getColumnName());
            }
            else {
                ci.setPrimaryKey(false);
            }
        }
        
        int pkSize = acceptedPrimaryKeyNames.size();
        if (pkSize != tmp.size()) {
            throw new IllegalArgumentException("Failed in setting primary key for the record - " + 
                  "expected pk names: " + primaryKeyNames + "; " + 
                  "record allowed pk names: " + acceptedPrimaryKeyNames);
        }
        
        if (pkSize > 0) {
            primaryKeyColumnNames = new String[pkSize];
            System.arraycopy(acceptedPrimaryKeyNames.toArray(), 0, primaryKeyColumnNames, 0, pkSize);
        }
        else {
            primaryKeyColumnNames = null;
        }
    }
    
    /**
     * sets primary key columns for the row
     */
    public void setPrimaryKeyColumns(Set primaryKeyNames) {
        setPrimaryKeyColumns(convertToUpperCase(primaryKeyNames));
    }
    
    /**
     * sets read-only columns for the row
     * 
     * If the input column name is not a valid column name, this operation is 
     * ignored. 
     */
    public void setReadOnlyColumn(String columnName) {
        if (columnName == null) return;
        
        if (columnInfos == null || columnInfos.size() == 0) 
            throw new IllegalStateException("Columns must be populated first before setting readonly columns.");
        
        ColumnInfo ci = getColumnInfo(columnName);
        if (ci != null) {
            ci.setReadOnly(true);
            readOnlyColumnNames.add(columnName.toUpperCase());
        }
    }
    
    /**
     * sets read-only columns for the row
     * 
     * Any column names in the input that are not true column names will be 
     * discarded. 
     */
    public void setReadOnlyColumns(Set readOnlyNames) {
        if (readOnlyNames == null || readOnlyNames.size() == 0) return;
        
        if (columnInfos == null || columnInfos.size() == 0) 
            throw new IllegalStateException("Columns must be populated first before setting readonly columns.");
        
        List tmp = convertToUpperCase(readOnlyNames);
        Iterator it = tmp.iterator();
        while(it.hasNext()) {
            String columnName = (String)it.next();
            ColumnInfo ci = getColumnInfo(columnName);
            if (ci != null) {
                ci.setReadOnly(true);
                readOnlyColumnNames.add(columnName);
            }
        }
    }
    
    /**
     * Indicates if there is primary key defined.
     */
    public boolean hasPrimaryKey() {
        if (primaryKeyColumnNames != null && primaryKeyColumnNames.length > 0) return true;
        return false;
    }
    
    /**
     * Checks if a column is set to be audited for create operation.
     * @param colName the column name to be checked.
     * @return true if audited
     */
    public boolean isAuditedForCreate(String colName) {
    	boolean status = false;
    	
    	if (DatabaseConfig.getInstance().allowAutoAuditCreate() && 
    		DatabaseConfig.getInstance().isAutoAuditCreate(colName)) {
    		status = true;
    	}
    	
    	return status;
    }
    
    /**
     * Checks if a column is set to be audited for update operation.
     * @param colName the column name to be checked.
     * @return true if audited
     */
    public boolean isAuditedForUpdate(String colName) {
    	boolean status = false;
    	
    	if (DatabaseConfig.getInstance().allowAutoAuditUpdate() && 
    		DatabaseConfig.getInstance().isAutoAuditUpdate(colName)) {
    		status = true;
    	}
    	
    	return status;
    }
    
    /**
     * Checks if a column is set to be audited for create or update operation.
     * @param colName the column name to be checked.
     * @return true if audited
     */
    public boolean isAuditedForCreateOrUpdate(String colName) {
    	boolean status = false;
    	
    	if (isAuditedForCreate(colName) || isAuditedForUpdate(colName)) {
    		status = true;
    	}
    	
    	return status;
    }
    
    /**
     * checks whether a column is a date type column.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is of date type.
     */
    public boolean isDateColumn(String colName) {
        boolean status = false;
        
        Iterator it = columnInfos.iterator();
        while (it.hasNext()) {
            ColumnInfo ci = (ColumnInfo) it.next();
            if (colName.equalsIgnoreCase(ci.getColumnName())) {
            	if (ci.isDate()) {
            		status = true;
            	}
            	break;
            }
        }
        
        return status;
    }
    
    /**
     * checks whether a column is a timestamp type column.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is of date type.
     */
    public boolean isTimestampColumn(String colName) {
        boolean status = false;
        
        Iterator it = columnInfos.iterator();
        while (it.hasNext()) {
            ColumnInfo ci = (ColumnInfo) it.next();
            if (colName.equalsIgnoreCase(ci.getColumnName())) {
            	if (ci.isTimestamp()) {
            		status = true;
            	}
            	break;
            }
        }
        
        return status;
    }
    
    /**
     * checks whether a column is a numeric type column.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is of numeric type.
     */
    public boolean isNumericColumn(String colName) {
        boolean status = false;
        
        Iterator it = columnInfos.iterator();
        while (it.hasNext()) {
            ColumnInfo ci = (ColumnInfo) it.next();
            if (colName.equalsIgnoreCase(ci.getColumnName())) {
            	if (ci.isNumeric()) {
            		status = true;
            	}
            	break;
            }
        }
        
        return status;
    }
    
    /**
     * checks whether a column is primary key column.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is primary key column.
     */
    public boolean isPrimaryKeyColumn(String colName) {
        boolean found = false;
        if (primaryKeyColumnNames != null && colName != null) {
            int size = primaryKeyColumnNames.length;
            for (int i=0; i<size; i++) {
                String tmp = primaryKeyColumnNames[i];
                if (colName.equalsIgnoreCase(tmp)) {
                    found = true;
                    break;
                }
            }
        }
        
        return found;
    }
    
    /**
     * Checks whether a column is a readonly column.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is readonly
     */
    public boolean isReadOnlyColumn(String colName) {
        if (colName == null) return false;
        if (readOnlyColumnNames.contains(colName.toUpperCase())) return true;
        return false;
    }
    
    /**
     * checks whether a column name exists
     * 
     * @param testName the column name to be checked.
     * @return true if the column is valid.
     */
    public boolean isValidColumnName(String testName) {
        boolean found = false;
        if (columnNames != null && testName != null) {
            int size = columnNames.length;
            for (int i=0; i<size; i++) {
                String tmp = columnNames[i];
                if (testName.equalsIgnoreCase(tmp)) {
                    found = true;
                    break;
                }
            }
        }
        
        return found;
    }
    
    /**
     * checks whether a column is a required column. Data for a required 
     * column cannot be set to null.
     * 
     * @param colName the column name to be checked.
     * @return true if the column is required.
     */
    public boolean isRequiredColumn(String colName) {
        boolean status = false;
        
        Iterator it = columnInfos.iterator();
        while (it.hasNext()) {
            ColumnInfo ci = (ColumnInfo) it.next();
            if (colName.equalsIgnoreCase(ci.getColumnName()) &&
                (!ci.isNull() || ci.isNotNull() || ci.isPrimaryKey())) {
                status = true;
                break;
            }
        }
        
        return status;
    }
    
    /**
     * checks whether a column's length is longer than a specific length.
     * 
     * @param colName the column name to be checked.
     * @param length the specific length.
     * @return true if the column's length is longer than the specific length.
     */
    public boolean isLongTextColumn(String colName, int length) {
        ColumnInfo ci = getColumnInfo(colName);
        if (ci != null) {
            if (ci.getColumnDisplaySize() > length) return true;
        }
        return false;
    }
    
    /**
     * returns delete sql of jdbc style
     */
    public String getDeleteSqlInJDBCStyle() {
        if (deleteSQL_jdbc == null) {
            StringBuffer sb = new StringBuffer("DELETE FROM " + table);
            StringBuffer wheres = new StringBuffer();
    
            // organize where name/value pairs
            String[] pkNames = primaryKeyColumnNames;
            if (pkNames == null || pkNames.length == 0) 
                throw new IllegalArgumentException("There is no primary keys identified for table " + table);
            
            int whereTotalSize = pkNames.length;
            
            if (whereTotalSize > 0) {
                int i = 0;
    
                // first to next to the last
                for (i = 0; i < (whereTotalSize - 1); i++) {
                    String pkColumnName = pkNames[i];
                    wheres.append(pkColumnName + " = ? AND ");
                }
    
                // the last
                if (i == whereTotalSize -1) {
                    String pkColumnName = pkNames[i];
                    wheres.append(pkColumnName + " = ?");
                }
            }
            
            sb.append(" WHERE " + wheres.toString());
            
            deleteSQL_jdbc = sb.toString();
        }
        
        return deleteSQL_jdbc;
    }
       
    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String LINE_BREAK = "\r\n";
        
        returnString.append("name = " + name).append(LINE_BREAK);
        returnString.append("schema = " + schema).append(LINE_BREAK);
        returnString.append("catalog = " + catalog).append(LINE_BREAK);
        returnString.append("table = " + table).append(LINE_BREAK);
        returnString.append("dimension = " + dimension).append(LINE_BREAK);
        
        if (columnInfos != null) {
            Iterator it = columnInfos.iterator();
            while (it.hasNext()) {
                ColumnInfo ci = (ColumnInfo) it.next();
                returnString.append(ci.toString()).append( LINE_BREAK );
            }
        }
        
        return returnString.toString();
    }
    

    private void clearContent() {
        schema = null;
        catalog = null;
        table = null;
        dimension = 0;
        columnInfos.clear();
        
        //some convenience fields:
        deleteSQL_jdbc = null;
        
        //some convenience arraies:
        columnNames = null;
        primaryKeyColumnNames = null;
        readOnlyColumnNames.clear();
        columnSqlDataTypes = null;
        columnSqlDataTypeNames = null;
        columnJavaClassNames = null;
        nameIndexMap.clear();
    }
    
    private void parseResultSetMetaData(ResultSetMetaData rsmd) {
        if (rsmd == null) return;
        
        try {
            List columns = new ArrayList();
            
            dimension = rsmd.getColumnCount();
            if (dimension <= 0) {
                String errorMessage = "ResultSet for " + name + " has zero dimension.";
                throw new FailureDetectingRowMetaDataException(errorMessage);
            }
            
            for (int i = 1; i <= dimension; i++) {
                ColumnInfo ci = new ColumnInfo();
                ci.setSchemaName(rsmd.getSchemaName(i));
                ci.setCatalogName(rsmd.getCatalogName(i));
                ci.setTableName(rsmd.getTableName(i));
                ci.setColumnClassName(rsmd.getColumnClassName(i));
                ci.setColumnName(convertToUpperCase(rsmd.getColumnLabel(i)));
                ci.setColumnTypeName(rsmd.getColumnTypeName(i));
                ci.setColumnDisplaySize(rsmd.getColumnDisplaySize(i));
                ci.setSQLDataType(rsmd.getColumnType(i));
                ci.setPrecision(rsmd.getPrecision(i));
                ci.setScale(rsmd.getScale(i));
                ci.setNull(rsmd.isNullable(i));
                ci.setAutoIncrement(rsmd.isAutoIncrement(i));
                ci.setCaseSensitive(rsmd.isCaseSensitive(i));
                ci.setCurrency(rsmd.isCurrency(i));
                ci.setDefinitelyWritable(rsmd.isDefinitelyWritable(i));
                ci.setReadOnly(rsmd.isReadOnly(i));
                ci.setSearchable(rsmd.isSearchable(i));
                ci.setSigned(rsmd.isSigned(i));
                ci.setWritable(rsmd.isWritable(i));
                columns.add(i-1, ci);
            }
            
            //set properties
            setColumnInfoList(columns);
        }
        catch(SQLException ex) {
            throw new FailureDetectingRowMetaDataException(ex);
        }
    }
    
    private void parseResultSet(ResultSet rs) {
        if (rs == null) return;
        
        try {
            parseResultSetMetaData(rs.getMetaData());
        }
        catch(SQLException ex) {
            throw new FailureDetectingRowMetaDataException(ex);
        }
    }
    
    private void parseResultSetForView(ResultSet rs) {
        if (rs == null) return;
        
        try {
            List columns = new ArrayList();
            
            int index = 0;
            while (rs.next()) {
                ++index;
                ColumnInfo ci = new ColumnInfo();
                ci.setSchemaName(rs.getString("TABLE_SCHEM"));
                ci.setCatalogName(rs.getString("TABLE_CAT"));
                ci.setTableName(rs.getString("TABLE_NAME"));
                ci.setColumnName(convertToUpperCase(rs.getString("COLUMN_NAME")));
                ci.setColumnTypeName(rs.getString("TYPE_NAME"));
                ci.setColumnDisplaySize(Util.getSafeIntValue(rs.getString("CHAR_OCTET_LENGTH")));
                ci.setSQLDataType(Util.getSafeIntValue(rs.getString("DATA_TYPE")));
                ci.setColumnClassName(SqlUtil.getJavaType(ci.getSQLDataType()));
                ci.setPrecision(Util.getSafeIntValue(rs.getString("COLUMN_SIZE")));
                ci.setScale(Util.getSafeIntValue(rs.getString("DECIMAL_DIGITS")));
                ci.setNull(Util.getSafeIntValue(rs.getString("NULLABLE")));
                //ci.setAutoIncrement(rsmd.isAutoIncrement(i));
                //ci.setCaseSensitive(rsmd.isCaseSensitive(i));
                //ci.setCurrency(rsmd.isCurrency(i));
                //ci.setDefinitelyWritable(rsmd.isDefinitelyWritable(i));
                //ci.setReadOnly(rsmd.isReadOnly(i));
                //ci.setSearchable(rsmd.isSearchable(i));
                //ci.setSigned(rsmd.isSigned(i));
                //ci.setWritable(rsmd.isWritable(i));
                columns.add(index-1, ci);
            }
            
            dimension = index;
            if (dimension <= 0) {
                String errorMessage = "ResultSet for " + name + " has zero dimension.";
                throw new FailureDetectingRowMetaDataException(errorMessage);
            }
            
            //set properties
            setColumnInfoList(columns);
        }
        catch(SQLException ex) {
            throw new FailureDetectingRowMetaDataException(ex);
        }
    }
    
    private List convertToUpperCase(List list) {
        if (list == null) return null;
        
        List newList = new ArrayList();
        for(Iterator it=list.iterator(); it.hasNext();) {
            newList.add(((String)it.next()).toUpperCase());
        }
        return newList;
    }
    
    private List convertToUpperCase(Set stringSet) {
        if (stringSet == null) return null;
        
        List newList = new ArrayList();
        for(Iterator it=stringSet.iterator(); it.hasNext();) {
            newList.add(((String)it.next()).toUpperCase());
        }
        return newList;
    }
    
    private String convertToUpperCase(String word) {
        return (word == null)?null:word.toUpperCase();
    }
    
    private static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }

    public static final String DEFAULT_PRIMARY_KEY_COLUMN_NAME = "id";

    private String schema = null;
    private String catalog = null;
    private String table = null;
    private String name = "";
    private int dimension = 0;
    private List columnInfos = new ArrayList();
    
    //some convenience fields:
    private String deleteSQL_jdbc = null;
    
    //some convenience arraies:
    private String[] columnNames = null;
    private String[] primaryKeyColumnNames = null;
    private List readOnlyColumnNames = new ArrayList();
    private int[] columnSqlDataTypes = null;
    private String[] columnSqlDataTypeNames = null;
    private String[] columnJavaClassNames = null;
    private Map nameIndexMap = new HashMap();
}
