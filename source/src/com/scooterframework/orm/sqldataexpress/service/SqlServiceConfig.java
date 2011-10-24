/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

/**
 * SqlServiceConfig class creates and initializes an instance of SqlService. 
 * 
 * @author (Fei) John Chen
 */
public class SqlServiceConfig {
    private static final SqlService sqlSvc = new SqlServiceImpl();
    
    public static SqlService getSqlService() {
        return sqlSvc;
    }
}
