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
 * Tag class
 * 
 * @author (Fei) John Chen
 *
 */
public class Tag extends ActiveRecord {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2726081819889697487L;

	public void registerRelations() {
        hasMany(Tagging.class, "mapping: id=tag_id; cascade: delete");
    }

    public void validatesRecord() {
        validators().validatesPresenceOf("name");
    }
    
    public String getTableName() {
        return "tags";
    }
}
