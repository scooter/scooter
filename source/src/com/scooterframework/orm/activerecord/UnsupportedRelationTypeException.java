/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * class UnsupportedRelationTypeException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportedRelationTypeException extends RelationException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5145330744998645414L;

	public UnsupportedRelationTypeException(String unsupportedType) {
        this.unsupportedType = unsupportedType;
    }
    
    public String getUnsupportedType() {
        return unsupportedType;
    }
    
    public String toString() {
        StringBuffer returnSB = new StringBuffer();
        returnSB.append("Relation type ").append(unsupportedType);
        returnSB.append(" is not allowed.");
        return returnSB.toString();
    }
    
    private String unsupportedType;
}
