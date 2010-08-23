/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.exception.GenericException;
import com.scooterframework.common.exception.UnsupportFeatureException;
import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;
import com.scooterframework.transaction.ImplicitTransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * <p>AssociatedRecordsHMT class contains a list of ActiveRecord objects and its 
 * owner in a has-many-through association.</p>
 * 
 * @author (Fei) John Chen
 */
public class AssociatedRecordsHMT extends AssociatedRecords {
    public AssociatedRecordsHMT(RecordRelation recordRelation) {
        super(recordRelation);
    }
    
    public AssociatedRecordsHMT(RecordRelation recordRelation, List records) {
        super(recordRelation, records);
    }
    
    
    /**
     * Adds a record to the association. If the owner object is 
     * already in the database, the record will be either inserted or updated in 
     * the database. If the owner record is not in the database, the record 
     * will not be saved to the database.
     * 
     * @param record A record to be added to the relation. 
     * @param joinInput A map of input data for the join record. 
     * @return updated AssociatedRecords
     */
    public AssociatedRecords add(ActiveRecord record, Map joinInput) {
        if (record == null) return this;
        
        List records = new ArrayList();
        records.add(record);
        
        List joinInputs = new ArrayList();
        joinInputs.add(joinInput);
        
        return add(records, joinInputs);
    }
    
    /**
     * Adds a list of records to the association. If the owner object is 
     * already in the database, the records will be either inserted or updated in 
     * the database. If the owner record is not in the database, the records 
     * will not be saved to the database.
     * 
     * @param records a list of records to be added to the relation. 
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords add(List records) {
        if (records == null || records.size() == 0) return this;
        
        HasManyThroughRelation hmtRelation = (HasManyThroughRelation)super.getRelation();
        Map inputsMap = hmtRelation.getJoinInputs();
        
        int size = records.size();
        List inputsMapList = new ArrayList(size);
        for (int i=0; i<size; i++) {
            inputsMapList.add(i, inputsMap);
        }
        return add(records, inputsMapList);
    }
    
    /**
     * Adds a list of records to the association. If the owner object is 
     * already in the database, the records will be either inserted or updated in 
     * the database. If the owner record is not in the database, the records 
     * will not be saved to the database.
     * 
     * @param records a list of records to be added to the relation. 
     * @param joinInputs a list of input data map for the through table. 
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords add(List records, List joinInputs) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_add(records, joinInputs);
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        return assocs;
    }
    
    private AssociatedRecords internal_add(List records, List joinInputs) {
        if (records == null || records.size() == 0) return this;
        
        if (joinInputs != null && joinInputs.size() != records.size()) 
            throw new IllegalArgumentException("The size of joinInputs must be the same as records size.");
        
        //retrieve current list
        associatedRecords = getRecords();
        
        // make sure the record type is valid
        validateRecordType(getRelation().getTargetClass(), records);
        
        // now add the records to db and memory by 
        if (associatedRecords == null) associatedRecords = new ArrayList();
        
        ActiveRecord owner = getOwner();
        if (!owner.isNewRecord()) {
            HasManyThroughRelation hmtRelation = (HasManyThroughRelation)getRelation();
            Relation acRelation = hmtRelation.getACRelation();
            Relation cbRelation = hmtRelation.getCBRelation();
            
            int inputSize = records.size();
            for(int i=0; i<inputSize; i++) {
                ActiveRecord record = (ActiveRecord)records.get(i);
                if (record == null) continue;
                
                if (record.isNewRecord() || record.isDirty()) record.save();
                
                //must check CB relation type
                if (Relation.BELONGS_TO_TYPE.equals(cbRelation.getRelationType())) {
                    //now construct a join class
                    ActiveRecord joinRecord = (ActiveRecord)OrmObjectFactory.getInstance().newInstance(hmtRelation.getMiddleC());
                    if (joinInputs != null) {
                        Map inputs = (Map)joinInputs.get(i);
                        joinRecord.setData(inputs);
                    }
                    
                    //need to populate ALL fks before linking to both ends
                    AssociationHelper.populateFKInBelongsTo(joinRecord, acRelation.getReverseMappingMap(), owner);
                    AssociationHelper.populateFKInBelongsTo(joinRecord, cbRelation.getMappingMap(), record);
                    
                    AssociatedRecord assocCB = joinRecord.associated(cbRelation.getAssociation());
                    assocCB.storeLoadedAssociatedRecord(record);
                    
                    //link the middleC record with both ends.
                    AssociatedRecord assocCA = joinRecord.associated(acRelation.getReverseRelationName());
                    assocCA.attach(owner);//join record saved, all counters updated
                    //assocCB.attach(record);//This is not necessary
                }
                else {
                    //must be a has-many relation
                    //Then there is no need to create a middleC class
                }
                
                //add the record to memory
                associatedRecords.add(record);
            }
        }
        else {
            associatedRecords.addAll(records);
        }
        
        return this;
    }
    
    /**
     * <p>Removes target record from the association. The record is not deleted. 
     * The join record is deleted if the value of the keepJoinRecord input 
     * is false or the join record depends on the record to be deleted. </p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.</p>
     * 
     * @param target a record to be detached from the association. 
     * @param keepJoinRecord if true, keep the join record. Otherwise, delete it.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords detach(ActiveRecord target, boolean keepJoinRecord) {
        List records = new ArrayList();
        if (target != null) records.add(target);
        return detach(records, keepJoinRecord);
    }
    
    /**
     * <p>Removes a list of records from the association. No record is deleted. 
     * Only the join record is deleted. </p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.</p>
     * 
     * @param records list of records to be detached.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords detach(List records) {
        return detach(records, false);
    }
    
    /**
     * <p>Removes a list of records from the association. No record is deleted. 
     * The join record is deleted if the value of the <tt>keepJoinRecord</tt> 
     * input is false or the join record depends on the record to be deleted.</p>
     * 
     * <p>Proper use of keepJoinRecord parameter is good for a three-way join 
     * association.</p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.</p>
     * 
     * @param records list of records to be detached.
     * @param keepJoinRecord if true, keep the join record. Otherwise, delete it.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords detach(List records, boolean keepJoinRecord) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_detach(records, keepJoinRecord);
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        return assocs;
    }
    
    private AssociatedRecords internal_detach(List records, boolean keepJoinRecord) {
        if (records == null || records.size() == 0) return this;
        
        //retrieve current list
        associatedRecords = getRecords();
        
        if (ownerIsNew()) {
            if (associatedRecords != null) associatedRecords.removeAll(records);
        }
        else {
            validateRecordType(getRelation().getTargetClass(), records);
            
            // update the record in db and remove it from memory
            if (associatedRecords != null && associatedRecords.size() > 0) {
                HasManyThroughRelation hmtRelation = (HasManyThroughRelation)getRelation();
                Relation acRelation = hmtRelation.getACRelation();
                Relation cbRelation = hmtRelation.getCBRelation();
                
                // check if the record is in the relation
                int inputSize = records.size();
                for (int i=0; i<inputSize; i++) {
                    ActiveRecord record = (ActiveRecord) records.get(i);
                    if (record == null) continue;
                    
                    if (record.isNewRecord()) {
                        associatedRecords.remove(record);
                    }
                    else {
                    	if (Relation.BELONGS_TO_TYPE.equals(cbRelation.getRelationType())) {
                            ActiveRecord joinRecord = getJoinRecord(getOwner(), record);
                            
                            //detach the middleC record from the link to the record.
                            if (joinRecord != null) {
	                            Relation caRelation = acRelation.getReverseRelation();
	                            BelongsToRecordRelation caBTRR = 
	                        		new BelongsToRecordRelation(joinRecord, (BelongsToRelation)caRelation);
	                        	AssociatedRecord caAssociatedRecord = new AssociatedRecord(caBTRR, getOwner());
	                        	caBTRR.setAssociatedData(caAssociatedRecord);
	                        	joinRecord.setRecordRelation(caRelation.getAssociation(), caBTRR);
	                            
	                        	BelongsToRecordRelation cbBTRR = 
	                        		new BelongsToRecordRelation(joinRecord, (BelongsToRelation)cbRelation);
	                        	AssociatedRecord cbAssociatedRecord = new AssociatedRecord(cbBTRR, record);
	                        	cbBTRR.setAssociatedData(cbAssociatedRecord);
	                        	joinRecord.setRecordRelation(cbRelation.getAssociation(), cbBTRR);
                        		
                                if (!keepJoinRecord || joinRecord.isDependentOf(record)) {
                                    joinRecord.delete();
                                }
                                else {
                                    joinRecord.associated(cbRelation.getAssociation()).detach();
                                }
                            }
                    	}
                    	else {
                            //must be a has-many relation
                    	}
                        
                        //remove the record from memory
                        associatedRecords = removeRecordFromList(associatedRecords, record);
                    }
                }
            }
        }
        
        return this;
    }
    
    /**
     * <p>Deletes a list objects from the associated list and delete the records in 
     * database whether the objects are dependent on the owner or not.</p>
     * 
     * <p>The associated records in the join table can either be deleted or 
     * have its foreign key nullified, depending on how you specify the cascade 
     * property in the "to-be-deleted" record class. </p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.</p>
     * 
     * @param records a list of records to be deleted.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords delete(List records) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_delete(records);
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        return assocs;
    }
    
    private AssociatedRecords internal_delete(List records) {
        if (records == null || records.size() == 0) return this;
        
        //retrieve current list
        associatedRecords = getRecords();
        
        if (ownerIsNew()) {
            if (associatedRecords != null) associatedRecords.removeAll(records);
        }
        else {
            validateRecordType(getRelation().getTargetClass(), records);
            
            // update the record in db and remove it from memory
            if (associatedRecords != null && associatedRecords.size() > 0) {
                // check if the record is in the relation
                int inputSize = records.size();
                for (int i=0; i<inputSize; i++) {
                    ActiveRecord record = (ActiveRecord) records.get(i);
                    if (record == null) continue;
                    
                    if (record.isNewRecord()) {
                        associatedRecords.remove(record);
                    }
                    else {
                    	record.delete();
                    	
                        //remove the record from memory
                        associatedRecords = removeRecordFromList(associatedRecords, record);
                    }
                }
            }
        }
        
        return this;
    }
    
    /**
     * <p>This feature is not supported for has-many-through relation.</p>
     * 
     * <p>This is equivalent to detach() and add(records).</p>
     * 
     * <p>Removes all existing associated objects from the associated list by 
     * setting their foreign keys to NULL. The records are not deleted 
     * unless they depend on the owner. </p>
     * 
     * <p>Adds the new records to the association.</p>
     * 
     * @return updated AssociatedRecords
     */
    public AssociatedRecords replace(List records) {
        throw new UnsupportFeatureException("replace() is not supported by has-many-through relation.");
    }
    
    /**
     * Retrieves the join record between the owner and the <tt>target</tt>.
     * 
     * @param target the associated record
     * @return the join record
     */
    public ActiveRecord getJoinRecord(ActiveRecord target) {
        return getJoinRecord(getOwner(), target);
    }
    
    
    /**
     * Count number of associated records in the database.
     * 
     * @return number of associated records
     */
    protected int countRecordsInDB() {
        return ((HasManyThroughRecordRelation)recordRelation).countRecordsInDB();
    }
    
    /**
     * Retrieves the join record between records A and B. This method is only 
     * good for the case where CB relation is belongs-to.
     * 
     * @param recordA endA record
     * @param recordB endB record
     * @return the join record
     */
    private ActiveRecord getJoinRecord(ActiveRecord recordA, ActiveRecord recordB) {
        HasManyThroughRelation rel = (HasManyThroughRelation)getRelation();
        
        //construct a conditions map
        Map conditions = new HashMap();
        
        Map acMappingMap = getMappingMap(rel.getACMapping());
        Iterator it1 = acMappingMap.keySet().iterator();
        while(it1.hasNext()) {
            Object aFld = it1.next();
            Object cFld = acMappingMap.get(aFld);
            conditions.put(cFld, recordA.getField((String)aFld));
        }
        
        Map cbMappingMap = getMappingMap(rel.getCBMapping());
        Iterator it2 = cbMappingMap.keySet().iterator();
        while(it2.hasNext()) {
            Object cFld = it2.next();
            Object bFld = cbMappingMap.get(cFld);
            conditions.put(cFld, recordB.getField((String)bFld));
        }
        
        //If this join record is in a category and the endB class is a type of 
        //the category, then add more to the conditions map.
        Class joinClass = rel.getMiddleC();
        List categories = RelationManager.getInstance().getRegisteredCategory(joinClass);
        if (categories != null && categories.size() > 0) {
            boolean inCategory = false;
            Category category = null;
            Iterator itc = categories.iterator();
            while(itc.hasNext()) {
                category = (Category)itc.next();
                if (category.isEntityInCategory(rel.getTargetModel())) {
                    inCategory = true;
                    break;
                }
            }
            
            if (inCategory) {
                conditions.put(category.getTypeField(), category.getTypeByEntity(rel.getTargetModel()));
            }
        }
        
        ActiveRecord joinRecord = ActiveRecordUtil.getGateway(joinClass).findFirst(conditions);
        
        return joinRecord;
    }
    
    private Map getMappingMap(String mapping) {
        return Converters.convertStringToMap(mapping);
    }
}
