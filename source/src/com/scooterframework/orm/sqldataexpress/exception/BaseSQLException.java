/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

import com.scooterframework.common.exception.GenericException;

/**
 * class BaseSQLException
 * 
 * @author (Fei) John Chen
 */
public class BaseSQLException extends GenericException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 7872262430720121308L;

	public BaseSQLException() {
        super();
    }
    
    public BaseSQLException(String message) {
        super(message);
    }

    public BaseSQLException(Throwable exception) {
        super(exception);
    }

    public BaseSQLException(String message, Throwable exception) {
        super(message, exception);
    }
}
