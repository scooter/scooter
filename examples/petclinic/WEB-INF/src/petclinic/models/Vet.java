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
 * Vet class represents a vet record in database.
 * 
 * @author (Fei) John Chen
 */
public class Vet extends ActiveRecord {
    public void registerRelations() {
        hasMany("vet_specialties");
        hasManyThrough("specialties", "vet_specialties");
    }
}
