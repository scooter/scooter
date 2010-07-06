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
 * Visit class represents a visit record in database.
 * 
 * @author (Fei) John Chen
 */
public class Visit extends ActiveRecord {
    public void registerRelations() {
        belongsTo("pet");
    }
}
