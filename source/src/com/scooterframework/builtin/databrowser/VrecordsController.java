/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.builtin.BuiltinHelper;
import com.scooterframework.orm.misc.Paginator;

/**
 * VrecordsController class handles view record related access.
 * 
 * @author (Fei) John Chen
 */
public class VrecordsController extends ApplicationController {

	static {
		filterManagerFor(VrecordsController.class).declareBeforeFilter(
				BuiltinHelper.class, "validateRequest");
	}
    
    public String index() {
        String database = getDatabase();
        setViewData("database", database);
        String view = getView();
        setViewData("view", view);
        
        String connName = database;
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = new Paginator(new TableSqlPageListSource(connName, view), params());
            page.setExcludedKeys("database, database_id, view, view_id");
            setViewData("paged_records", page);
            return forwardTo(viewPath("paged_list"));
        }
        
        setViewData("vrecords", Vrecord.getRows(connName, view));
        
        return null;
    }
}
