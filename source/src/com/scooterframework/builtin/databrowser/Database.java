/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.util.List;
import java.util.Properties;

import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;


/**
 * Database model class handles database related access.
 * 
 * @author (Fei) John Chen
 */
public class Database {

    public static List<String> getConnectionNames() {
        return SqlExpressUtil.getConnectionNames();
    }
    
    public static Properties getConnectionProperties(String connName) {
    	return SqlExpressUtil.getConnectionProperties(connName);
    }
    
    public static boolean isOracle(String connName) {
    	return SqlExpressUtil.isOracle(connName);
    }
    
    public static String[] getCatalogAndSchema(String connName) {
    	return SqlExpressUtil.getCatalogAndSchema(connName);
    }
}
