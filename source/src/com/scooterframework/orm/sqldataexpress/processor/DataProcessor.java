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
    public static final String input_key_database_connection_name    = framework_input_key_prefix + "DATABASE_CONNECTION_NAME";
    public static final String input_key_database_connection_context = framework_input_key_prefix + "DATABASE_CONNECTION_CONTEXT";
    public static final String input_key_max_row_index               = framework_input_key_prefix + "MAX_ROW_INDEX";
    public static final String input_key_min_row_index               = framework_input_key_prefix + "MIN_ROW_INDEX";
    public static final String input_key_records_offset              = framework_input_key_prefix + "RECORDS_OFFSET";
    public static final String input_key_records_limit               = framework_input_key_prefix + "RECORDS_LIMIT";
    public static final String input_key_records_fixed               = framework_input_key_prefix + "RECORDS_FIXED";
    public static final String input_key_use_pagination              = framework_input_key_prefix + "USE_PAGINATION";
    public static final int NO_ROW_LIMIT = -1;
    public static final int DEFAULT_PAGINATION_LIMIT = 10;

    /**
     * execute
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map inputs) throws BaseSQLException;
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map inputs, Map outputFilters) throws BaseSQLException;
}
