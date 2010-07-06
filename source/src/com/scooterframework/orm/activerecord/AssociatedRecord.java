/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.common.exception.GenericException;
import com.scooterframework.common.exception.InvalidOperationException;
import com.scooterframework.transaction.ImplicitTransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * AssociatedRecord class contains an associated ActiveRecord object and 
 * its owner. It also has methods for the association.
 * 
 * @author (Fei) John Chen
 */
public class AssociatedRecord {

    public AssociatedRecord(RecordRelation recordRelation) {
        if (recordRelation == null) 
            throw new IllegalArgumentException("Input for recordRelation is null.");
        
        this.recordRelation = recordRelation;
        
        latestRecordsLoaded = false;
    }

    public AssociatedRecord(RecordRelation recordRelation, ActiveRecord associatedRecord) {
        this(recordRelation);
        this.associatedRecord = associatedRecord;
        
        latestRecordsLoaded = true;
    }
    
    /**
     * Returns the associated record.
     */
    public ActiveRecord getRecord() {
        return getRecord(false);
    }
    
    /**
     * Returns the associated record.
     * 
     * If <tt>refresh</tt> is <tt>true</tt>, a database retrieval will be 
     * fired for existing owner object.
     */
    public ActiveRecord getRecord(boolean refresh) {
        if (refresh) {
            associatedRecord = recordRelation.retrieveAssociatedData();
            latestRecordsLoaded = true;
        }
        return associatedRecord;
    }
    
    /**
     * Returns owner of the association.
     */
    public ActiveRecord getOwner() {
        return recordRelation.getOwner();
    }
    
    /**
     * Returns relation of the association.
     */
    public Relation getRelation() {
        return recordRelation.getRelation();
    }
    
    /**
     * <p>Associates with another record object.</p>
     * 
     * <p>How does attach(record) work for has-one relation?</p>
     * 
     * <pre>
     * Example: Person has-one Address
     *          person1 <--- address1
     *          person2 <--- address2
     *          
     *          If we assign address2 to person1, the following will happen:
     *          1. The foreign key person_id in address2 should switch from 
     *             person2.pk to person1.pk.
     *          2. person1 must detach from address1. Whether address1 should be  
     *             deleted or not, depends on how you specify the dependent 
     *             attribute. 
     * </pre>
     * <p>
     * Attaching a child object to a parent object (relation owner) in a 
     * has-one relation automatically saves the child object unless the parent  
     * object is new (not in database, therefore can't set up FK relation). If 
     * the parent object is new, parent.save() must be used if you want to 
     * store the association into database. The parent's previously associated 
     * child object is also updated by setting its FK value to null.
     * </p>
     * 
     * <p>How does attach(record) work for belongs-to relation?</p>
     * 
     * <pre>
     * Example: Address belongs-to Person
     *          address1 ---> person1
     *          address2 ---> person2
     *          
     *          If we assign person2 to address1, the following will happen:
     *          1. The foreign key person_id in address1 should switch from 
     *             person1.pk to person2.pk.
     *          2. person2 must detach from address2. Whether address2 should be  
     *             deleted or not, depends on how you specify the dependent 
     *             attribute. 
     *          3. If the reverse relation is has-many type, the address 
     *             counter in person1 must be decremented, while incremented in 
     *             person2.
     * </pre>
     * <p>
     * Attaching a parent object to a child object (relation owner) in a 
     * belongs-to relation automatically saves the child object unless the 
     * parent object is new (not in database, therefore can't set up FK 
     * relation). If the parent object is new, child.save() must be used if you 
     * want to store the association into database. The parent object's 
     * previously associated child object is also detached if the reverse 
     * relation is has-one.
     * </p>
     * 
     * @param newTarget a record to be attached
     * @return updated AssociatedRecord
     */
    public AssociatedRecord attach(ActiveRecord newTarget) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecord assoc = null;
        try {
            tm.beginTransactionImplicit();
            
            assoc = internal_attach(newTarget);
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        return assoc;
    }
    
    private AssociatedRecord internal_attach(ActiveRecord newTarget) {
        // make sure the record type is valid
        Class classB = recordRelation.getRelation().getTargetClass();
        ActiveRecordUtil.validateRecordType(classB, newTarget);
        
        // now we can attach the record
        ActiveRecord owner = recordRelation.getOwner();
        String relationType = recordRelation.getRelation().getRelationType();
        String reverseRelationType = recordRelation.getRelation().getReverseRelationType();
        
        if (Relation.BELONGS_TO_TYPE.equals(relationType)) {
            //Note: In this case, owner is child and target is parent.
            
            if (newTarget == null) {
                if (owner.isPKDependentOf(classB)) {
                    throw new InvalidOperationException("Cannot nullify a primary key field.");
                }
                
                //decrement counter of old parent if there is one when the reverse relation is has-many.
                if (!owner.isNewRecord() && Relation.HAS_MANY_TYPE.equals(reverseRelationType) && !isEmpty()) {
                    owner.decrementCounterInParent((BelongsToRelation)recordRelation.getRelation());
                }
                
                //nullify FK in child object whether the child is new or not
                Map fkMap = new HashMap();
                Object[] colNames = recordRelation.getRelation().getLeftSideMappingItems();
                for (int i = 0; i < colNames.length; i++) {
                    fkMap.put(colNames[i], null);
                }
                owner.setData(fkMap);
                
                if (!owner.isNewRecord()) {
                    owner.update();
                }
                
                //link to new parent
                associatedRecord = newTarget;
            }
            else if (!newTarget.isNewRecord()) {
                //decrement counter of old parent if there is one when the reverse relation is has-many.
                if (!owner.isNewRecord() && Relation.HAS_MANY_TYPE.equals(reverseRelationType) && !isEmpty()) {
                    owner.decrementCounterInParent((BelongsToRelation)recordRelation.getRelation());
                }
                
                //detach new parent's previous child
                if (Relation.HAS_ONE_TYPE.equals(reverseRelationType)) {
                    AssociatedRecord ar = newTarget.associated(owner.getClass());
                    if (ar != null) ar.detach();
                }
                
                if (newTarget.isDirty()) {
                	newTarget.update();
                }
                
                //link to new parent
                storeLoadedAssociatedRecord(newTarget);
                
                //set up new FK and save
                AssociationHelper.populateFKInBelongsTo(owner, recordRelation.getRelation().getMappingMap(), newTarget);
                if (owner.isNewRecord()) {
                    owner.create(); //child saved, counter updated.
                }
                else {
                    owner.update();
                    
                    //increment counter of new parent when the reverse relation is has-many.
                    if (Relation.HAS_MANY_TYPE.equals(reverseRelationType)) {
                        owner.incrementCounterInParent((BelongsToRelation)recordRelation.getRelation());
                    }
                }
            }
        }
        else if (Relation.HAS_ONE_TYPE.equals(relationType)) {
            //Note: In this case, owner is parent and target is child.
            
            if (!owner.isNewRecord() && newTarget != null) {
                ActiveRecord current_associatedRecord = getRecord();
                if (current_associatedRecord != null) {
                    AssociatedRecord ar = current_associatedRecord.associated(owner.getClass());
                    if (ar != null) ar.detach();
                }
                
                //set FK in target, based on owner's PK data
                Map fkData = recordRelation.getFKDataMapForOther();
                newTarget.setData(fkData);
                newTarget.save();
            }
        }
        
        associatedRecord = newTarget;
        latestRecordsLoaded = true;
        
        return this;
    }
    
    /**
     * <p>Disassociates the associated object in the association by setting its 
     * foreign key to NULL. The child object in the association is not deleted 
     * even if it depends on the parent object. </p>
     * 
     * <p>See the method {@link com.scooterframework.orm.activerecord.ActiveRecord#isDependentOf(ActiveRecord)}
     * for definition of dependent record.</p>
     * 
     * <p>To delete a dependent record in detach operation, use <tt>detach(true)</tt>.</p>
     */
    public void detach() {
        detach(false);
    }
    
    /**
     * <p>Disassociates the associated object in the association by setting its 
     * foreign key to NULL. The child object in the association may be deleted 
     * if it depends on the parent object and the removeDependent flag is true. </p>
     * 
     * <p>See the method {@link com.scooterframework.orm.activerecord.ActiveRecord#isDependentOf(ActiveRecord)}
     * for definition of dependent record.</p>
     * 
     * @param removeDependent whether dependent record should be deleted or now.
     */
    public void detach(boolean removeDependent) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        try {
            tm.beginTransactionImplicit();
            
            internal_detach(removeDependent);
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
    }
    
    private void internal_detach(boolean removeDependent) {
        if (associatedRecord == null) return;//nothing to detach
        
        ActiveRecord owner = recordRelation.getOwner();
        String relationType = recordRelation.getRelation().getRelationType();
        String reverseRelationType = recordRelation.getRelation().getReverseRelationType();
        
        if (Relation.BELONGS_TO_TYPE.equals(relationType)) {
            //Note: In this case, owner(the child) is detached from its parent. The 
            //associatedRecord is the parent.
            
            //nullify FK in child object whether the child is new or not
            Map fkMap = new HashMap();
            Object[] colNames = recordRelation.getRelation().getLeftSideMappingItems();
            for (int i = 0; i < colNames.length; i++) {
                fkMap.put(colNames[i], null);
            }
            owner.setData(fkMap);
            
            if (associatedRecord.isNewRecord()) {
                //remove parent
                associatedRecord = null;
                
                //once the associated is detached, the association should no longer be there. 
                recordRelation.setAssociatedData((AssociatedRecord)null);
            }
            else {
                if (!owner.isNewRecord()) {
                    if (owner.isDependentOf(associatedRecord) && removeDependent) {
                        //delete the child as it cannot exist without a parent.
                        owner.delete();
                    }
                    else {
                        //decrement counter of parent when the reverse relation is has-many.
                        if (!owner.isNewRecord() && Relation.HAS_MANY_TYPE.equals(reverseRelationType)) {
                            owner.decrementCounterInParent((BelongsToRelation)recordRelation.getRelation());
                        }
                        
                        //remove parent first so that callback methods will not touch parent.
                        associatedRecord = null;
                        
                        owner.update();
                    }
                }
                
                //once the associated is detached, the record association should no longer be there. 
                recordRelation.setAssociatedData((AssociatedRecord)null);
            }
        }
        else if (Relation.HAS_ONE_TYPE.equals(relationType)) {
            //Note: In this case, owner(the parent) is detached from its child.
            
            //nullify FK in child object whether the child is new or not
            Map fkMap = new HashMap();
            Object[] colNames = recordRelation.getRelation().getRightSideMappingItems();
            for (int i = 0; i < colNames.length; i++) {
                fkMap.put(colNames[i], null);
            }
            associatedRecord.setData(fkMap);
            
            if (!owner.isNewRecord()) {
                if (!associatedRecord.isNewRecord()) {
                    if (associatedRecord.isDependentOf(owner) && removeDependent) {
                        //delete the child as it cannot exist without a parent.
                        associatedRecord.delete();
                    }
                    else {
                        associatedRecord.update();
                    }
                }
            }
            
            //remove child
            associatedRecord = null;
            
            //once the associated is detached, the record association should no longer be there. 
            recordRelation.setAssociatedData((AssociatedRecord)null);
        }
    }
    
    /**
     * <p>Deletes the associated record in database whether the object is 
     * dependent on the owner or not. </p>
     * 
     * <p>This method has no effect if the reverse relation is has-many. </p>
     */
    public void delete() {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        try {
            tm.beginTransactionImplicit();
            
            internal_delete();
            
            tm.commitTransactionImplicit();
        }
        catch(GenericException ex) {
            tm.rollbackTransactionImplicit();
            throw ex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
    }
    
    private void internal_delete() {
        String reverseRelationType = recordRelation.getRelation().getReverseRelationType();
        
        if (Relation.HAS_MANY_TYPE.equals(reverseRelationType)) {
            return;
        }
        
        if (associatedRecord != null) {
            if (!associatedRecord.isNewRecord()) associatedRecord.delete();
            associatedRecord = null;
        }
        
        //once the associated is deleted, the association should no longer be there. 
        recordRelation.setAssociatedData((AssociatedRecord)null);
    }
    
    /**
     * This is equivalent to detach() first and attach(record) later.
     * 
     * @return updated AssociatedRecord
     */
    public AssociatedRecord replace(ActiveRecord record) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecord assr = null;
        
        try {
            tm.beginTransactionImplicit();
            detach(false);
            assr = attach(record);
            tm.commitTransactionImplicit();
        }
        catch(Exception ex) {
            tm.rollbackTransactionImplicit();
            throw new RelationException(ex);
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        return assr;
    }
    
    /**
     * Returns true if there is no associated record.
     * 
     * @return true if there is no associated record.
     */
    public boolean isEmpty() {
        if (!latestRecordsLoaded && !getOwner().isNewRecord()) {
            associatedRecord = getRecord(true);
            latestRecordsLoaded = true;
        }
        
        return (associatedRecord == null)?true:false;
    }
    
    /**
     * Cleans up cached data.
     */
    public void cleanCache() {
        associatedRecord = null;
        latestRecordsLoaded = false;
    }
    
    /**
     * Checks if the associated record has been loaded from database.
     * 
     * @return true if the associated record has been loaded from database.
     */
    public boolean hasLoadedFromDatabase() {
        return latestRecordsLoaded;
    }
    
    /**
     * Stores loaded database record from database.
     * 
     * @param record newly loaded database record from database.
     */
    public void storeLoadedAssociatedRecord(ActiveRecord record) {
        associatedRecord = record;
        latestRecordsLoaded = true;
    }
    
    /**
     * The RecordRelation instance for this association.
     */
    protected RecordRelation recordRelation;
    
    /**
     * The associated record
     */
    protected ActiveRecord associatedRecord;
    
    /**
     * Indicates if the associated record have been retrieved or not
     */
    protected boolean latestRecordsLoaded = false;
}
