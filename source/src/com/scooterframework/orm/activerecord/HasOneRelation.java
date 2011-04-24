/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * HasOneRelation class has information about has-one 
 * relationship between objects. 
 * 
 * For example, A has one B. 
 * 
 * In has-one relation, foreign key is held by class B. 
 * 
 * @author (Fei) John Chen
 */
public class HasOneRelation extends Relation {
    public HasOneRelation(Class<? extends ActiveRecord> endA, String associationId, String targetModel) {
        super(endA, HAS_ONE_TYPE, associationId, targetModel);
    }
}
