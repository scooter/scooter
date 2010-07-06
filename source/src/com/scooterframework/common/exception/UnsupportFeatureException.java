/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class UnsupportFeatureException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportFeatureException extends GenericException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 3353399897820458776L;

	public UnsupportFeatureException() {
        super();
    }
    
    public UnsupportFeatureException(String message) {
        super(message);
    }
}
