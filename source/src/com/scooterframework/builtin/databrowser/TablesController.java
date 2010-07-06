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
 * TablesController class handles table related access.
 * 
 * @author (Fei) John Chen
 */
public class TablesController extends ApplicationController {
	protected static LogUtil log = LogUtil.getLogger(TablesController.class.getName());

    public String index() {
        String database = getDatabase();
        storeToRequest("database", database);
        String connectionName = database;
        String schema = super.params("schema");
        if (schema != null) {
        	storeToRequest("schema", schema);
        	storeToRequest("tables", Table.getTables(connectionName, null, schema));
        }
        else {
        	storeToRequest("tables", Table.getTables(connectionName));
        }
        return null;
    }
    
    public String show() {
        String database = getDatabase();
        storeToRequest("database", database);
        String connectionName = database;
        
        String table = params("id");
        storeToRequest("table", table);
        storeToRequest(getTableKey(), table);
        try {
        	storeToRequest("records_count", Table.countRecords(connectionName, table));
        } catch(Exception ex) {
        	ex.printStackTrace();
        	String error = ex.getMessage();
        	String errorNotice = "Failed to browse table " + table + ".";
        	if (error.indexOf("table or view does not exist") != -1) {
        		errorNotice += " You may not have select privilege on it.";
        	}
        	super.flash("error", errorNotice);
        	log.error(errorNotice + " Details: " + error);
        }
        storeToRequest("header", Table.getTableHeaderInfo(connectionName, table));
        return null;
    }
}
