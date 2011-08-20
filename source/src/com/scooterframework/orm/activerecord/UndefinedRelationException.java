/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * class UndefinedRelationException
 * 
 * @author (Fei) John Chen
 */
public class UndefinedRelationException extends RelationException  {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -2215382939584281763L;

	public UndefinedRelationException(String message) {
        super(message);
    }
    
    public UndefinedRelationException(Class<? extends ActiveRecord> entity1, Class<? extends ActiveRecord> entity2) {
        super("There is no defined relationship from " + 
                    ActiveRecordUtil.getModelName(entity1) + " to " + 
                    ActiveRecordUtil.getModelName(entity2) + ".");
    }
    
    public UndefinedRelationException(String entity1, String entity2) {
        super("There is no defined relation from " + 
                    entity1 + " to " + entity2 + ".");
    }
}
