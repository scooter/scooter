/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorTypes;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConfig;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * Record model class handles record related access.
 * 
 * @author (Fei) John Chen
 */
public class Record {
    private static LogUtil log = LogUtil.getLogger(Record.class.getName());
    
    public static String getFinderSql(String connName, String table) {
    	return SqlExpressUtil.getFinderSQL(connName, Table.getSafeTableName(connName, table));
    }
    
    public static List<RowData> getRows(String connName, String table) {
        return getRows(connName, table, null);
    }
    
    public static List<RowData> getRows(String connName, String table, String whereClause) {
		RowInfo ri = getRowInfo(connName, table);

		// prepare inputs map
		Map inputs = new HashMap();
		String processorType = DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR;
		String processorName = getFinderSql(connName, table);
		if (whereClause != null) 
			processorName = processorName + " " + whereClause;

		// retrieve all records
		inputs.put(DataProcessor.input_key_database_connection_name, connName);
		TableData td = SqlServiceConfig.getSqlService().retrieveRows(inputs, processorType, processorName);
		if (td != null) {
			td.setHeader(ri);
		}
		return (td != null) ? td.getAllRows() : null;
	}
    
    public static RowData getRow(String connName, String table, String restfulId) {
        RowInfo ri = getRowInfo(connName, table);
        
        //prepare inputs map
        Map inputs = new HashMap();
        inputs.put(DataProcessor.input_key_database_connection_name, connName);
        inputs.put("id", restfulId);
        
        String processorType = DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR;
        String processorName = getFinderSql(connName, table);
        
        String condition = prepareDynamicWhereClauseForRestfulId(inputs, ri, restfulId);
        if (condition != null && !"".equals(condition)) processorName += " WHERE " + condition;
        
        log.debug("select sql = " + processorName);
        log.debug("select inputs = " + inputs);
        
        TableData td = SqlServiceConfig.getSqlService().retrieveRow(inputs, processorType, processorName);
        if (td != null) {
        	td.setHeader(ri);
        }
        return (td != null)?td.getFirstRow():null;
    }
    
    public static RowData createRecord(Map inputs, String connName, String table) {
        RowInfo ri = getRowInfo(connName, table);
        inputs = StringUtil.convertKeyToUpperCase(inputs);
        
        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("INSERT INTO ").append(SqlExpressUtil.getExpandedTableName(connName, table));
        insertSQL.append(" ").append(prepareInsertSQL(ri, inputs));
        
        log.debug("insert sql = " + insertSQL);
        log.debug("insert inputs = " + inputs);
        
        inputs.put(DataProcessor.input_key_database_connection_name, connName);
        OmniDTO returnTO = 
            SqlServiceConfig.getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, insertSQL.toString());
        int insertCount = returnTO.getUpdatedRowCount();
        
        RowData record = null;
        if (insertCount == 1) {
            record = new RowData(ri, null);
            record.setData(inputs);
        }
        
        return record;
    }
    
    public static int updateRecord(Map inputs, String connName, String table, String restfulId) {
        RowInfo ri = getRowInfo(connName, table);
        inputs = StringUtil.convertKeyToUpperCase(inputs);
        
        StringBuffer updateSQL = new StringBuffer();
        updateSQL.append("UPDATE ").append(SqlExpressUtil.getExpandedTableName(connName, table));
        updateSQL.append(" SET ").append(prepareSetSQL(ri, inputs));
        
        String condition = prepareDynamicWhereClauseForRestfulId(inputs, ri, restfulId);
        if (condition != null && !"".equals(condition)) updateSQL.append(" WHERE ").append(condition);
        
        log.debug("updates sql = " + updateSQL);
        log.debug("updates inputs = " + inputs);
        
        inputs.put(DataProcessor.input_key_database_connection_name, connName);
        OmniDTO returnTO = 
            SqlServiceConfig.getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, updateSQL.toString());
        int updateCount = returnTO.getUpdatedRowCount();
        
        return updateCount;
    }
    
    public static int deleteRecord(Map inputs, String connName, String table, String restfulId) {
        RowInfo ri = getRowInfo(connName, table);
        
        StringBuffer deleteSQL = new StringBuffer();
        deleteSQL.append("DELETE FROM ").append(SqlExpressUtil.getExpandedTableName(connName, table));
        
        String condition = prepareDynamicWhereClauseForRestfulId(inputs, ri, restfulId);
        if (condition != null && !"".equals(condition)) deleteSQL.append(" WHERE ").append(condition);
        
        log.debug("delete sql = " + deleteSQL);
        log.debug("delete inputs = " + inputs);
        
        inputs.put(DataProcessor.input_key_database_connection_name, connName);
        OmniDTO returnTO = 
            SqlServiceConfig.getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, deleteSQL.toString());
        int deleteCount = returnTO.getUpdatedRowCount();
        
        return deleteCount;
    }
    
    public static RowInfo getRowInfo(String connName, String table) {
        RowInfo ri = null;
        TableInfo ti = SqlExpressUtil.lookupAndRegisterTable(connName, table);
        if (ti != null) {
            ri = ti.getHeader();
        }
        
        if (ri == null) {
            throw new IllegalArgumentException("Failed to retrieve column header " + 
                "information from table \"" + table + "\" with " + 
                "connection \"" + connName + "\"");
        }
        
        return ri;
    }
    
    public static String prepareInsertSQL(RowInfo ri, Map inputs) {
        Set inputKeys = inputs.keySet();
        StringBuffer names = new StringBuffer();
        StringBuffer values = new StringBuffer();
        int maxSize = ri.getDimension();
        ColumnInfo ci = null;
        for(int i = 0; i < maxSize; i++) {
            ci = ri.getColumnInfo(i);
            String columnName = ci.getColumnName();
            if (columnName == null) continue;
            
            columnName = columnName.toUpperCase();
            if (!inputKeys.contains(columnName)) {
            	if (!ri.isAuditedForCreate(columnName)) {
            		continue;
            	}
            	else {
            		inputs.put(columnName, getCurrentTimestamp());
            	}
            }
            
            names.append(columnName).append(", ");
            values.append("?").append(columnName).append(", ");
        }
        
        StringBuffer result = new StringBuffer();
        result.append("(").append(StringUtil.removeLastToken(names, ", "));
        result.append(") VALUES (").append(StringUtil.removeLastToken(values, ", ")).append(")");
        return result.toString();
    }
    
    public static String prepareSetSQL(RowInfo ri, Map inputs) {
        Set inputKeys = inputs.keySet();
        StringBuffer sb = new StringBuffer();
        int maxSize = ri.getDimension();
        ColumnInfo ci = null;
        for(int i = 0; i < maxSize; i++) {
            ci = ri.getColumnInfo(i);
            String columnName = ci.getColumnName();
            if (columnName == null) continue;
            
            columnName = columnName.toUpperCase();
            if (ci.isReadOnly() || !ci.isWritable() || ci.isPrimaryKey()) continue;

            if (!inputKeys.contains(columnName)) {
            	if (!ri.isAuditedForUpdate(columnName)) {
            		continue;
            	}
            	else {
            		inputs.put(columnName, getCurrentTimestamp());
            	}
            }
            
            sb.append(columnName).append(" = ?").append(columnName).append(", ");
        }
        
        return StringUtil.removeLastToken(sb, ", ").toString();
    }
    
    public static String prepareDynamicWhereClauseForRestfulId(Map inputs, RowInfo ri, String restfulId) {
        String condition = "";
        Map map = SqlExpressUtil.getTableKeyMapForRestfulId(ri, restfulId);
        if (map != null && map.size() > 0) {
            condition = getDynamicWhereClauseForTableKeyMap(ri.getTable(), map);
            inputs.putAll(map);
        }
        return condition;
    }
    
    private static String getDynamicWhereClauseForTableKeyMap(String table, Map tableKeyMap) {
        Map map = new HashMap();
        StringBuffer sb = new StringBuffer();
        if (tableKeyMap != null && tableKeyMap.size() > 0) {
            Iterator it = tableKeyMap.keySet().iterator();
            while(it.hasNext()) {
                String column = (String)it.next();
                String token = table + "." + column;
                sb.append(token).append("= ?").append(token).append(" AND ");
                map.put(token, tableKeyMap.get(column));
            }
            tableKeyMap.putAll(map);
        }
        
        return StringUtil.removeLastToken(sb.toString(), "AND ");
    }
    
    private static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
