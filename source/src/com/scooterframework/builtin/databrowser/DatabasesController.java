/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import com.scooterframework.web.util.R;

/**
 * DatabasesController class handles database related access.
 * 
 * @author (Fei) John Chen
 */
public class DatabasesController extends ApplicationController {
    public String index() {
        storeToRequest("databases", Database.getConnectionNames());
        return null;
    }
    
    public String show() {
        String connName = params("id");
        storeToRequest("database", Database.getConnectionProperties(connName));
        
        String schema = super.params("schema");
        String submit = super.params("submit");
        if (submit != null) {
        	if (schema == null || "".equals(schema)) {
        		super.flash("error", "Please enter schame name.");
        	}
        	else {
        		String uri = R.nestedResourcePath("databases", connName, submit.toLowerCase());
        		return super.redirectTo(uri, "schema=" + schema);
        	}
        }
        
        return null;
    }
}
