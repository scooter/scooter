/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Map;

/**
 * HasManyThroughRelation class has information about has-many-through 
 * relationship between objects. 
 * 
 * @author (Fei) John Chen
 */
public class HasManyThroughRelation extends Relation {
    public HasManyThroughRelation(Class ownerClass, String associationId, String throughAssociation,
    		Relation acRelation, Relation cbRelation) {
        super(ownerClass, HAS_MANY_THROUGH_TYPE, associationId, cbRelation.getTargetModel());
        this.throughAssociation = throughAssociation;
        this.acRelation = acRelation;
        this.cbRelation = cbRelation;
        super.setTargetClass(cbRelation.getTargetClass());
    }
    
    /**
     * Returns the through association target.
     * 
     * @return the through association target
     */
    public String getThroughAssociation() {
        return throughAssociation;
    }
    
    /**
     * Returns data map for the middleC join table.
     * 
     * @return joinInputs data map for the middleC join table
     */
    public Map getJoinInputs() {
        return joinInputs;
    }
    
    /**
     * Sets data map for the middleC join table.
     * 
     * @param joinInputs data map for the middleC join table
     */
    public void setJoinInputs(Map joinInputs) {
        this.joinInputs = joinInputs;
    }
    
    /**
     * Return AC relation.
     */
    public Relation getACRelation() {
        return acRelation;
    }
    
    /**
     * Return CB relation.
     */
    public Relation getCBRelation() {
        return cbRelation;
    }
    
    /**
     * Returns the middle C class.
     * 
     * @return the middle C class
     */
    public Class getMiddleC() {
        return cbRelation.getOwnerClass();
    }
    
    /**
     * Returns mapping between endA and middleC.
     * @return acMapping string
     */
    public String getACMapping() {
    	return acRelation.getMapping();
    }
    
    /**
     * Returns mapping between middleC and endB.
     * @return cbMapping string
     */
    public String getCBMapping() {
    	return cbRelation.getMapping();
    }
    
    protected String throughAssociation;
    protected Relation acRelation;
    protected Relation cbRelation;
    
    protected Map joinInputs;
}
