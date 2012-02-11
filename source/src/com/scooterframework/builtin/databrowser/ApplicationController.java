/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;

/**
 * ApplicationController class contains common methods for DataBrowser.
 * 
 * @author (Fei) John Chen
 */
public class ApplicationController {
	protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    /**
     * Returns database name. 
     * 
     * If using plural names, the database name comes from <tt>database_id</tt>,
     * otherwise, it comes from <tt>databases_id</tt>;
     * 
     * @return database name
     */
    public String getDatabase() {
        return (DatabaseConfig.getInstance().usePluralTableName())?p("database_id"):p("databases_id");
    }
    
    /**
     * Returns table name. 
     * 
     * If using plural names, the table name comes from <tt>table_id</tt>,
     * otherwise, it comes from <tt>tables_id</tt>;
     * 
     * @return table name
     */
    public String getTable() {
        return (DatabaseConfig.getInstance().usePluralTableName())?p("table_id"):p("tables_id");
    }
    
    /**
     * Returns table key. 
     * 
     * If using plural names, the table key is <tt>table_id</tt>,
     * otherwise, it is <tt>tables_id</tt>;
     * 
     * @return table key
     */
    public String getTableKey() {
        return (DatabaseConfig.getInstance().usePluralTableName())?"table_id":"tables_id";
    }
    
    /**
     * Returns view name. 
     * 
     * If using plural names, the view name comes from <tt>view_id</tt>,
     * otherwise, it comes from <tt>views_id</tt>;
     * 
     * @return view name
     */
    public String getView() {
        return (DatabaseConfig.getInstance().usePluralTableName())?p("view_id"):p("views_id");
    }
    
    /**
     * Returns view key. 
     * 
     * If using plural names, the view key is <tt>view_id</tt>,
     * otherwise, it is <tt>views_id</tt>;
     * 
     * @return view key
     */
    public String getViewKey() {
        return (DatabaseConfig.getInstance().usePluralTableName())?"view_id":"views_id";
    }
}
