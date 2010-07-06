/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin.databrowser;

import java.util.List;

/**
 * View model class handles view related access.
 * 
 * @author (Fei) John Chen
 */
public class View extends Table {
    public static List getViews(String connName) {
    	String[] s2 = Database.getCatalogAndSchema(connName);
    	String catalog = s2[0];
    	String schema = s2[1];
        return getViews(connName, catalog, schema);
    }
    
    public static List getViews(String connName, String catalog, String schema) {
        String[] types = {"VIEW"};
        return getTables(connName, catalog, schema, types);
    }
}
