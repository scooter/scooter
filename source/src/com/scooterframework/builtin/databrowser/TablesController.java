/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.builtin.AdminSignonController;
import com.scooterframework.common.logging.LogUtil;

/**
 * TablesController class handles table related access.
 * 
 * @author (Fei) John Chen
 */
public class TablesController extends ApplicationController {
	private static final LogUtil log = LogUtil.getLogger(TablesController.class.getName());

	static {
		filterManagerFor(TablesController.class).declareBeforeFilter(
				AdminSignonController.class, "loginRequired");
	}

    public String index() {
        String database = getDatabase();
        setViewData("database", database);
        String connectionName = database;
        String schema = p("schema");
        if (schema != null) {
        	setViewData("schema", schema);
        	setViewData("tables", Table.getTables(connectionName, null, schema));
        }
        else {
        	setViewData("tables", Table.getTables(connectionName));
        }
        return null;
    }
    
    public String show() {
        String database = getDatabase();
        setViewData("database", database);
        String connectionName = database;
        
        String table = p("id");
        setViewData("table", table);
        setViewData(getTableKey(), table);
        try {
        	setViewData("records_count", Table.countRecords(connectionName, table));
        } catch(Exception ex) {
        	String error = ex.getMessage();
        	String errorNotice = "Failed to browse table " + table + ".";
        	if (error.indexOf("table or view does not exist") != -1) {
        		errorNotice += " You may not have select privilege on it.";
        	}
        	flash("error", errorNotice);
        	log.error(errorNotice + " Details: " + error);
        }
        setViewData("header", Table.getTableHeaderInfo(connectionName, table));
        return null;
    }
}
