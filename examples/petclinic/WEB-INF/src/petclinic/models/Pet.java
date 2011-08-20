/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package petclinic.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Pet class represents a pet record in database.
 * 
 * @author (Fei) John Chen
 */
public class Pet extends ActiveRecord {
    public void registerRelations() {
        belongsTo("owner");
        belongsTo("type");
        hasMany("visits");
    }
}