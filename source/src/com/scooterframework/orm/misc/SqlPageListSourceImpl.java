/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;

/**
 * <p>SqlPageListSourceImpl class retrieves paged record list by using 
 * a predefined finder SQL query.</p>
 * 
 * <p>Finder SQL query example: </p>
 * <pre>
 * 		SELECT * FROM employees
 * </pre>
 * 
 * <p>This class is responsible for adding page limit constraint in the query.</p>
 * 
 * <p>The caller of this class is responsible for setting proper values for limit, 
 * offset, recount, and inputs map. Default values will be used when they are 
 * not set. The default value for limit is defined in 
 * <tt>DataProcessor.DEFAULT_PAGINATION_LIMIT</tt>. The default value for 
 * offset is zero. The default value for recount is true./<p>
 * 
 * @author (Fei) John Chen
 */
public class SqlPageListSourceImpl extends PageListSource {
    /**
     * Constructs a PageListSource object. If database connection name 
     * <tt>connName</tt> is empty, default connection as defined in database 
     * properties file is used.
     * 
     * @param connName database connection name. 
     * @param finderSql the sql query for finding records. 
     */
    public SqlPageListSourceImpl(String connName, String finderSql) {
         this(connName, finderSql, null);
    }
    
    /**
     * Constructs a PageListSource object. If database connection name 
     * <tt>connName</tt> is empty, default connection as defined in database 
     * properties file is used.
     * 
     * @param connName database connection name. 
     * @param finderSql the SQL query for finding records. 
     * @param inputOptions Map of control information.
     */
    public SqlPageListSourceImpl(String connName, String finderSql, Map<String, String> inputOptions) {
         this(connName, finderSql, inputOptions, true);
    }
    
    /**
     * Constructs a PageListSource object. If database connection name 
     * <tt>connName</tt> is empty, default connection as defined in database 
     * properties file is used.
     * 
     * @param connName database connection name. 
     * @param finderSql the SQL query for finding records. 
     * @param inputOptions Map of control information.
     * @param recount <tt>true</tt> if recount of total records is allowed;
     *		    <tt>false</tt> otherwise.
     */
    public SqlPageListSourceImpl(String connName, String finderSql, Map<String, String> inputOptions, boolean recount) {
    	super(inputOptions, recount);
    	
    	this.connName = connName;
        this.finderSql = finderSql;
    }

    protected int countTotalRecords() {
        int totalRecords = 0;
        
        try {
        	Map<String, Object> inputs = new HashMap<String, Object>();
        	inputs.putAll(inputOptions);
        	inputs.put(DataProcessor.input_key_database_connection_name, connName);
        	
            String selectCountSQL = "SELECT count(*) FROM (" + finderSql + ") xxx";
            
            Object Total = SqlServiceClient.retrieveObjectBySQL(selectCountSQL, inputs);
            totalRecords = Util.getSafeIntValue(Total);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        return totalRecords;
    }
    
    protected List<RowData> retrieveList() {
    	Map<String, Object> inputs = new HashMap<String, Object>();
    	inputs.putAll(inputOptions);
    	inputs.put(DataProcessor.input_key_database_connection_name, connName);
        return SqlServiceClient.retrieveRowsBySQL(finderSql, inputs);
    }
    
    protected String connName;
    protected String finderSql;
}
