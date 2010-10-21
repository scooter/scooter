/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Cursor meta data class which is basically a row meata data object. 
 * 
 * @author (Fei) John Chen
 */
public class Cursor extends RowInfo {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6133898740593522554L;
    
    public Cursor(String name, ResultSet rs) {
        super(name, rs);
    }
    
    public Cursor(String name, ResultSetMetaData rsmd) {
        super(name, rsmd);
    }
}
