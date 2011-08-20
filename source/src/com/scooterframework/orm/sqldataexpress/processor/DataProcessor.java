/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import java.util.Map;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;

/**
 * DataProcessor interface.
 * 
 * @author (Fei) John Chen
 */
public interface DataProcessor {
    /**
     * The following are reserved key words in inputs Map object. All keys must 
     * be in upper case.
     */
    public static final String framework_input_key_prefix = "SCOOTER.";
    
    /**
     * Key to the database connection name as specified in the <tt>config/database.properties</tt> file.
     */
    public static final String input_key_database_connection_name    = framework_input_key_prefix + "DATABASE_CONNECTION_NAME";
    
    /**
     * Key to an instance of <tt>DatabaseConnectionContext</tt>.
     */
    public static final String input_key_database_connection_context = framework_input_key_prefix + "DATABASE_CONNECTION_CONTEXT";
    
    public static final String input_key_max_row_index               = framework_input_key_prefix + "MAX_ROW_INDEX";
    
    public static final String input_key_min_row_index               = framework_input_key_prefix + "MIN_ROW_INDEX";
    
    /**
     * Specifies how many records to skip in the result set. This property 
     * must be used with <tt>input_key_records_limit</tt> property.
     */
    public static final String input_key_records_offset              = framework_input_key_prefix + "RECORDS_OFFSET";
    
    /**
     * Specifies the number of desired records to be retrieved. If the number 
     * of records retrieved is more than required, an exception will be thrown.
     */
    public static final String input_key_records_limit               = framework_input_key_prefix + "RECORDS_LIMIT";
    
    /**
     * Specifies that the retrieved records number is fixed. If the retrieved
     * records number is not equal to the number of records specified by 
     * <tt>input_key_records_limit</tt>, an exception will be thrown.
     */
    public static final String input_key_records_fixed               = framework_input_key_prefix + "RECORDS_FIXED";
    
    /**
     * Specified whether to use pagination. If the value is <tt>true</tt>, 
     * then pagination is used.
     */
    public static final String input_key_use_pagination              = framework_input_key_prefix + "USE_PAGINATION";
    
    /**
     * No retrieval limit.
     */
    public static final int NO_ROW_LIMIT = -1;
    
    /**
     * Default limit number of records for pagination.
     */
    public static final int DEFAULT_PAGINATION_LIMIT = 10;

    /**
     * execute
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs) throws BaseSQLException;
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs, Map<String, String> outputFilters) throws BaseSQLException;
}
