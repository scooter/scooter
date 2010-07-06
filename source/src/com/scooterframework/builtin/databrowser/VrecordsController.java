/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import com.scooterframework.admin.Constants;
import com.scooterframework.builtin.BuiltinHelper;
import com.scooterframework.orm.misc.Paginator;

/**
 * VrecordsController class handles view record related access.
 * 
 * @author (Fei) John Chen
 */
public class VrecordsController extends ApplicationController {
    
    /**
     * registerFilters
     */
    public void registerFilters() {
        beforeFilter(BuiltinHelper.class, "validateRequest");
        beforeFilter(BuiltinHelper.class, "displayParams");
    }
    
    public String index() {
        String database = getDatabase();
        storeToRequest("database", database);
        String view = getView();
        storeToRequest("view", view);
        
        String connName = database;
        if ("true".equals(params(Constants.PAGED))) {
            Paginator page = new Paginator(new TableSqlPageListSource(connName, view), params());
            page.setExcludedKeys("database, database_id, view, view_id");
            storeToRequest("paged_records", page);
            return forwardTo(viewPath("paged_list"));
        }
        
        storeToRequest("vrecords", Vrecord.getRows(connName, view));
        
        return null;
    }
}
