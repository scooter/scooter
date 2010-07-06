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
public class UnsupportedEntityInCategoryException extends BaseSQLException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5121270435307080755L;
	
	public UnsupportedEntityInCategoryException() {
        super();
    }
    
    public UnsupportedEntityInCategoryException(String message) {
        super(message);
    }
    
    public UnsupportedEntityInCategoryException(String category, String unsupportedEntity) {
        this.category = category;
        this.unsupportedEntity = unsupportedEntity;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getUnsupportedEntity() {
        return unsupportedEntity;
    }
    
    public String toString() {
        if (category == null || unsupportedEntity == null) return super.toString();
        
        StringBuffer returnSB = new StringBuffer();
        returnSB.append("Entity ").append(unsupportedEntity);
        returnSB.append(" in not supported in category ").append(category).append(".");
        return returnSB.toString();
    }
    
    private String category;
    private String unsupportedEntity;
}
