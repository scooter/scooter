/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * HasManyRelation class has information about has-many 
 * relationship between objects. 
 * 
 * For example, A has many B. 
 * 
 * In has-many relation, foreign key is held by class B. 
 * 
 * @author (Fei) John Chen
 */
public class HasManyRelation extends Relation {
    public HasManyRelation(Class endA, String associationId, String targetModel) {
        super(endA, HAS_MANY_TYPE, associationId, targetModel);
    }
}
