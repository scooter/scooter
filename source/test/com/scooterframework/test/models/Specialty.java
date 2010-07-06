/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Specialty class represents a specialty record in database.
 * 
 * @author (Fei) John Chen
 */
public class Specialty extends ActiveRecord {
    public void registerRelations() {
        //hasMany(VetSpecialty.class); //fk violation exception from mysql when deleting a specialty.
        //hasMany(VetSpecialty.class, "cascade: none"); //same as above
        hasMany("vet_specialties", "cascade: delete"); //fixed
        //hasMany(VetSpecialty.class, "cascade: simply_delete"); //ok when deleting a specialty
    }
}
