/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.builtin.BuiltinHelper;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.web.controller.ActionResult;
import com.scooterframework.web.util.R;

/**
 * RecordsController class handles record related access.
 * 
 * @author (Fei) John Chen
 */
public class RecordsController extends ApplicationController {
    private static LogUtil log = LogUtil.getLogger(RecordsController.class.getName());
    
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
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        if ("true".equals(params(Constants.PAGED))) {
            Paginator page = new Paginator(new TableSqlPageListSource(connName, table), params());
            page.setExcludedKeys("database, database_id, table, table_id");
            storeToRequest("paged_records", page);
            return forwardTo(viewPath("paged_list"));
        }
        
        storeToRequest("records", Record.getRows(connName, table));
        
        return null;
    }
    
    public String show() {
        String database = getDatabase();
        storeToRequest("database", database);
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        String restfulId = params("id");
        Object record = Record.getRow(connName, table, restfulId);
        if (record == null) {
            flash("error", "There is no record with id \"" + restfulId + "\".");
        }
        else {
            storeToRequest("record", record);
        }
        
        return null;
    }

    public String add() {
        String database = getDatabase();
        storeToRequest("database", database);
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        RowInfo ri = Record.getRowInfo(connName, table);
        RowData rd = new RowData(ri, null);
        storeToRequest("record", rd);
        
        return null;
    }
    
    public String create() {
        String database = getDatabase();
        storeToRequest("database", database);
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        Map inputs = super.params();
        RowData record = null;
        try {
            record = Record.createRecord(inputs, connName, table);
            if (record == null) {
                flash("notice", "There is no record inserted with inputs as " + inputs + ".");
            }
            else {
                flash("notice", "Successfully created a record.");
            }
            return ActionResult.redirectTo(resourcePath());
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the record.");
        }
        
        storeToRequest("record", record);
        return ActionResult.forwardTo(viewPath("add"));
    }
    
    public String edit() {
        return show();
    }
    
    public String update() {
        String database = getDatabase();
        storeToRequest("database", database);
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        String restfulId = params("id");
        Map inputs = super.params();
        RowData record = null;
        try {
            record = Record.getRow(connName, table, restfulId);
            if (record != null) {
                record.setData(inputs);
                
                int updateCount = Record.updateRecord(inputs, connName, table, restfulId);
                if (updateCount == 0) {
                    flash("notice", "There is no record updated in table \"" + table + 
                    "\" with restful id as \"" + restfulId + "\".");
                }
                else {
                    flash("notice", "Successfully updated a record.");
                }
            }
            else {
                flash("notice", "There is no record existing in table \"" + table + 
                "\" with restful id as \"" + restfulId + "\".");
            }
            return ActionResult.redirectTo(resourcePath(restfulId));
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the record.");
        }
        
        storeToRequest("record", record);
        return ActionResult.forwardTo(viewPath("edit"));
    }
    
    public String delete() {
        String database = getDatabase();
        storeToRequest("database", database);
        String table = getTable();
        storeToRequest("table", table);
        
        String connName = database;
        String restfulId = params("id");
        Map inputs = super.params();
        
        try {
            int deleteCount = Record.deleteRecord(inputs, connName, table, restfulId);
            
            if (deleteCount == 1) {
                flash("notice", "Successfully deleted a record.");
            }
            else if (deleteCount < 1) {
                flash("notice", "No record is deleted.");
            }
            else if (deleteCount > 1) {
                flash("error", "More than one record was deleted.");
            }
        }
        catch(Exception ex) {
            log.error("Error in delete() caused by " + ex.getMessage());
            flash("error", "There was a problem deleting the record.");
        }
        
        return ActionResult.redirectTo(resourcePath());
    }
    
    /**
     * Returns a restful action path. This method overrides the same method in 
     * super class to enforce restful rules.
     * 
     * @return a restful action path
     */
    protected String resourcePath() {
        return R.resourcePath("records");
    }
    
    /**
     * Returns a restful action path for an individual record. This method 
     * overrides the same method in super class to enforce restful rules.
     * 
     * @return a restful action path for an individual record
     */
    protected String resourcePath(String restfulId) {
        return R.resourceRecordPath("records", restfulId);
    }
    
    protected String viewPath(String action) {
        String controller = (String)params(Constants.CONTROLLER);
        return EnvConfig.getViewURI(controller, action);
    }
}
