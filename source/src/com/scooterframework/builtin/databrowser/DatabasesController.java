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
import com.scooterframework.web.util.R;

/**
 * DatabasesController class handles database related access.
 * 
 * @author (Fei) John Chen
 */
public class DatabasesController extends ApplicationController {

	static {
		filterManagerFor(DatabasesController.class).declareBeforeFilter(
				AdminSignonController.class, "loginRequired");
	}
	
    public String index() {
    	setViewData("databases", Database.getConnectionNames());
        return null;
    }
    
    public String show() {
        String connName = p("id");
        setViewData("database", Database.getConnectionProperties(connName));
        
        String schema = p("schema");
        String submit = p("submit");
        if (submit != null) {
        	if (schema == null || "".equals(schema)) {
        		flash("error", "Please enter schame name.");
        	}
        	else {
        		String uri = R.nestedResourcePath("databases", connName, submit.toLowerCase());
        		return redirectTo(uri, "schema=" + schema);
        	}
        }
        
        return null;
    }
}
