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
 * ViewsController class handles view related access.
 * 
 * @author (Fei) John Chen
 */
public class ViewsController extends ApplicationController {
	private static final LogUtil log = LogUtil.getLogger(TablesController.class.getName());

	static {
		filterManagerFor(ViewsController.class).declareBeforeFilter(
				AdminSignonController.class, "loginRequired");
	}

    public String index() {
        String database = getDatabase();
        setViewData("database", database);
        String connectionName = database;
        String schema = p("schema");
        if (schema != null) {
        	setViewData("schema", schema);
        	setViewData("views", View.getViews(connectionName, schema, schema));
        }
        else {
        	setViewData("views", View.getViews(connectionName));
        }
        return null;
    }
    
    public String show() {
        String database = getDatabase();
        setViewData("database", database);
        String connectionName = database;
        
        String view = p("id");
        setViewData("view", view);
        setViewData(getViewKey(), view);
        try {
        	setViewData("records_count", View.countRecords(connectionName, view));
        } catch(Exception ex) {
        	String error = ex.getMessage();
        	String errorNotice = "Failed to browse view " + view + ".";
        	if (error.indexOf("table or view does not exist") != -1) {
        		errorNotice += " You may not have select privilege on it.";
        	}
        	flash("error", errorNotice);
        	log.error(errorNotice + " Details: " + error);
        }
        setViewData("header", Table.getTableHeaderInfo(connectionName, view));
        return null;
    }
}
