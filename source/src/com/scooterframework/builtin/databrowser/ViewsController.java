/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import com.scooterframework.common.logging.LogUtil;

/**
 * ViewsController class handles view related access.
 * 
 * @author (Fei) John Chen
 */
public class ViewsController extends ApplicationController {
	protected static LogUtil log = LogUtil.getLogger(TablesController.class.getName());

    public String index() {
        String database = getDatabase();
        storeToRequest("database", database);
        String connectionName = database;
        String schema = super.params("schema");
        if (schema != null) {
        	storeToRequest("schema", schema);
        	storeToRequest("views", View.getViews(connectionName, schema, schema));
        }
        else {
        	storeToRequest("views", View.getViews(connectionName));
        }
        return null;
    }
    
    public String show() {
        String database = getDatabase();
        storeToRequest("database", database);
        String connectionName = database;
        
        String view = params("id");
        storeToRequest("view", view);
        storeToRequest(getViewKey(), view);
        try {
        	storeToRequest("records_count", View.countRecords(connectionName, view));
        } catch(Exception ex) {
        	String error = ex.getMessage();
        	String errorNotice = "Failed to browse view " + view + ".";
        	if (error.indexOf("table or view does not exist") != -1) {
        		errorNotice += " You may not have select privilege on it.";
        	}
        	super.flash("error", errorNotice);
        	log.error(errorNotice + " Details: " + error);
        }
        storeToRequest("header", Table.getTableHeaderInfo(connectionName, view));
        return null;
    }
}
