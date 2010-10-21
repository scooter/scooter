/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * class UndefinedReverseRelationException
 * 
 * @author (Fei) John Chen
 */
public class UndefinedReverseRelationException extends RelationException  {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 235828128191779388L;

	public UndefinedReverseRelationException(String message) {
        super(message);
    }
    
    public UndefinedReverseRelationException(Class entity1, Class entity2) {
        super("There is no defined reverse relationship for the relation from " + 
                    ActiveRecordUtil.getModelName(entity1) + " to " + 
                    ActiveRecordUtil.getModelName(entity2) + ".");
    }
    
    public UndefinedReverseRelationException(String entity1, String entity2) {
        super("There is no defined reverse relation for the relation from " + 
                    entity1 + " to " + entity2 + ".");
    }
}
