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
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordConstants;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;

/**
 * <p>JdbcPageListSource class retrieves paged record list by using jdbc.</p>
 * 
 * <p>The caller of this class is responsible for setting proper values for limit, 
 * offset, recount, and inputs map. Default values will be used when they are 
 * not set. The default value for limit is defined in 
 * <tt>DataProcessor.DEFAULT_PAGINATION_LIMIT</tt>. The default value for 
 * offset is zero. The default value for recount is true./<p>
 * 
 * <p>You can specify more conditional sql query strings in the inputOptions map 
 * with key ActiveRecord.key_conditions_sql.</p>
 * 
 * @author (Fei) John Chen
 */
public class JdbcPageListSource extends PageListSource {
    /**
     * Constructs a PageListSource object.
     * 
     * @param modelClass the ActiveRecord entity type to be paginated. 
     */
    public JdbcPageListSource(Class modelClass) {
         this(modelClass, new HashMap());
    }
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param modelClass the ActiveRecord entity type to be paginated. 
     * @param inputOptions Map of control information.
     */
    public JdbcPageListSource(Class modelClass, Map inputOptions) {
         this(modelClass, inputOptions, true);
    }
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param modelClass the ActiveRecord entity type to be paginated. 
     * @param inputOptions Map of control information.
     * @param recount <tt>true</tt> if recount of total records is allowed;
     *		    <tt>false</tt> otherwise.
     */
    public JdbcPageListSource(Class modelClass, Map inputOptions, boolean recount) {
    	super(inputOptions, recount);
    	
    	if (modelClass == null) {
    		throw new IllegalArgumentException("modelClass cannot be null.");
    	}
    	
    	if (!(ActiveRecord.class.isAssignableFrom(modelClass))) {
    		throw new IllegalArgumentException("modelClass must be of ActiveRecord class type.");
    	}
    	
        this.modelClass = modelClass;
    }

    protected int countTotalRecords() {
    	Map options = inputOptions;
        int totalRecords = 0;
        
        try {
            // count records
            Map inputs = ActiveRecordUtil.getGateway(modelClass).constructFindSQL(options, options);
            String findSQL = (String)inputs.get(ActiveRecordConstants.key_finder_sql);
            String selectCountSQL = "SELECT count(*) total FROM (" + findSQL + ") xxx";
            
            Object Total = SqlServiceClient.retrieveObjectBySQL(selectCountSQL, inputs);
            totalRecords = Util.getSafeIntValue(Total);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return totalRecords;
    }
    
    protected List retrieveList() {
        Map options = inputOptions;
        return ActiveRecordUtil.getHomeInstance(modelClass).findAll(options, options);
    }

    private Class modelClass;
}
