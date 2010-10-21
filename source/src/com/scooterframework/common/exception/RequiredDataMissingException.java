/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class RequiredDataMissingException
 * 
 * @author (Fei) John Chen
 */
public class RequiredDataMissingException extends GenericException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8329024945471451977L;

	public RequiredDataMissingException() {
        super();
    }
    
    public RequiredDataMissingException( String message ) {
        super(message);
    }
    
    public String getRequiredDataName() {
        return theField;
    }
    
    public void setRequiredDataName(String field) {
        theField = field;
    }
    
    private String theField;
}
