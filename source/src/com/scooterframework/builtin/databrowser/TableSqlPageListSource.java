/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scooterframework.orm.misc.SqlPageListSourceImpl;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;

/**
 * <p>TableSqlPageListSource class retrieves paged record list for a specific 
 * table. Finder SQL query is derived from the table.</p>
 * 
 * <p>Finder SQL query example: </p>
 * <pre>
 * 		SELECT * FROM employees
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class TableSqlPageListSource extends SqlPageListSourceImpl {
    /**
     * Constructs a TableSqlPageListSource object. If database connection name 
     * <tt>connName</tt> is empty, default connection as defined in database 
     * properties file is used.
     * 
     * @param connName database connection name. 
     * @param tableName table name. 
     */
    public TableSqlPageListSource(String connName, String tableName) {
    	super(connName, Record.getFinderSql(connName, tableName));
    	this.tableName = tableName;
    }
    
    protected List<RowData> retrieveList() {
    	RowInfo ri = Record.getRowInfo(connName, tableName);
    	
    	Map<String, Object> inputs = new HashMap<String, Object>();
    	inputs.putAll(inputOptions);
    	inputs.put(DataProcessor.input_key_database_connection_name, connName);
    	
        TableData td = SqlServiceClient.retrieveTableDataBySQL(finderSql, inputs);
        if (td != null) {
        	td.setHeader(ri);
        }
        return (td != null)?td.getAllRows():null;
    }
    
    protected String tableName;
}
