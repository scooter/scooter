/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import static com.scooterframework.web.controller.ActionControl.*;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.builtin.AdminSignonController;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.orm.misc.SqlPageListSourceImpl;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;
import com.scooterframework.web.controller.ACH;
import com.scooterframework.web.controller.ActionResult;
import com.scooterframework.web.util.W;

/**
 * DatabasesController class handles database related access.
 * 
 * @author (Fei) John Chen
 */
public class SQLWindowController extends ApplicationController {

	static {
		filterManagerFor(SQLWindowController.class).declareBeforeFilter(
				AdminSignonController.class, "loginRequired");
	}
	
    public String index() {
    	removeFromSessionData("database");
    	removeFromSessionData("limit");
    	removeFromSessionData("sql");
    	setViewData("databases", Database.getConnectionNames());
        return null;
    }
	
    public String execute() {
    	setViewData("databases", Database.getConnectionNames());
    	
    	String database = session("database");
    	String limit = session("limit");
    	String sql = session("sql");
    	String error = "";
    	
    	try {
        	if (sql != null && !"".equals(sql)) {
            	if (isSelectSQL(sql) && "true".equals(p(Constants.PAGED))) {
            		Map<String, String> pagingOptions = Converters.convertMapToMapSS(ACH.getAC().getParameterDataAsMap());
            		pagingOptions.put("limit", limit);
            		Paginator page = new Paginator(new SqlPageListSourceImpl(database, sql), pagingOptions);
                    setViewData("paged_records", page);
                    return ActionResult.forwardTo(viewPath("paged_list"));
            	}
            	else {
                	Map<String, Object> inputs = new HashMap<String, Object>();
                	inputs.put(DataProcessor.input_key_database_connection_name, database);
                	int count = SqlServiceClient.executeSQL(sql, inputs);
            		if (isDDL(sql)) {
                    	setViewData("sql_result", String.format("Success. Affected records count: %d", count));
            		}
            		else {
                    	setViewData("sql_result", "Success");
            		}
                    return ActionResult.forwardTo(viewPath("sql_result"));
            	}
        	}
        	else {
        		error = "Please enter a SQL statement.";
        	}
    	}
    	catch(Exception ex) {
    		error = "Error: " + ex.getMessage();
    		log.error("Error in execute()", ex);
    	}
    	
    	return html(color(error, "red"));
    }
    
    protected String viewPath(String action) {
        String controller = CurrentThreadCacheClient.controller();
        return EnvConfig.getViewURI(controller, action);
    }
    
    private boolean isSelectSQL(String sql) {
		return (sql.trim().toUpperCase().startsWith("SELECT")) ? true : false;
    }
    
    private boolean isDDL(String sql) {
		return ((sql.trim().toUpperCase().startsWith("CREATE")) || 
				(sql.trim().toUpperCase().startsWith("UPDATE")) || 
				(sql.trim().toUpperCase().startsWith("INSERT")) || 
				(sql.trim().toUpperCase().startsWith("DELETE"))) ? true : false;
    }
    
    private String session(String key) {
    	String value = p(key);
    	if (value == null || "".equals(value)) {
    		value = W.value(key);
    	}
    	else {
    		storeToSession(key, value);
    	}
    	return value;
    }
}
