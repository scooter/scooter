/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;

/**
 * class RecordValidationException
 * 
 * @author (Fei) John Chen
 */
public class RecordValidationException extends BaseSQLException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2992261217452324107L;

	public RecordValidationException(ValidationResults errors) {
        this.errors = errors;
    }
    
    public ValidationResults getRecordErrors() {
        return errors;
    }
    
    public String toString() {
        return (errors == null)?"":errors.toString();
    }
    
    public String getMessage() {
        return toString();
    }
    
    private ValidationResults errors;
}
