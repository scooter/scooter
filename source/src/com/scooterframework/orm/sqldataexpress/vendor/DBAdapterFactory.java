/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.util.DBStore;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * DBAdapterFactory class.
 * 
 * @author (Fei) John Chen
 */
public class DBAdapterFactory {
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    private static final DBAdapterFactory me;
    
    static {
        me = new DBAdapterFactory();
    }

    private DBAdapterFactory() {
    }
    
    public static synchronized DBAdapterFactory getInstance() {
        return me;
    }
    
    public synchronized DBAdapter getAdapter(String connName) {
    	if (connName == null) 
    		throw new IllegalArgumentException("connName cannot be null.");
    	
    	DBAdapter dba = DBStore.getInstance().getDBAdapter(connName);
    	
    	if (dba == null) {
    		Properties p = SqlExpressUtil.getConnectionProperties(connName);
        	String adapterClassName = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_ADAPTER_CLASS_NAME);
        	if (adapterClassName != null) {
        		dba = (DBAdapter)OrmObjectFactory.getInstance().newInstance(adapterClassName);
        	}
        	else {
        		dba = getBuiltInDBAdapterClass(connName);
        	}
        	DBStore.getInstance().addDBAdapter(connName, dba);
    	}
    	
    	return dba;
    }
    
    public DBAdapter getBuiltInDBAdapterClass(String connName) {
    	DBAdapter dba = null;
    	if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_MYSQL, connName)) {
    		dba = new MySQLDBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_POSTGRESQL, connName)) {
    		dba = new PostgreSQLDBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_ORACLE, connName)) {
    		dba = new OracleDBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_SQLSERVER, connName)) {
    		dba = new SQLServerDBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_HSQLDB, connName)) {
    		dba = new HsqlDBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_H2, connName)) {
    		dba = new H2DBAdapter();
    	}
    	else if (SqlExpressUtil.isBuiltinVendor(DatabaseConfig.BUILTIN_DATABASE_NAME_SYBASE, connName)) {
    		throw new IllegalArgumentException("Sybase is not supported yet.");
    	}
    	else {
    		throw new IllegalArgumentException("" + 
    				"Failed to find a DBAdapter for the type of database " + 
    				"associated with the connection named \"" + connName + "\". " + 
    				"You may add a adapterClassName property to specify your own adapter class.");
    	}
    	return dba;
    }
}
