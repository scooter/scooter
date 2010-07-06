/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord.tag;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Tagging class
 * 
 * @author (Fei) John Chen
 *
 */
public class Tagging extends ActiveRecord {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6294230155386715624L;

	public void registerRelations() {
        belongsTo(Tag.class, "mapping: tag_id=id;");
        belongsToCategory("taggable");
    }
    
    public String getTableName() {
        return "taggings";
    }
}
