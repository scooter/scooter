/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;

/**
 * class RelationException
 * 
 * @author (Fei) John Chen
 */
public class RelationException extends BaseSQLException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8622180416554219161L;

	public RelationException() {
        super();
    }
    
    public RelationException(String message) {
        super(message);
    }

    public RelationException(Throwable exception) {
        super(exception);
    }

    public RelationException(String message, Throwable exception) {
        super(message, exception);
    }
}
