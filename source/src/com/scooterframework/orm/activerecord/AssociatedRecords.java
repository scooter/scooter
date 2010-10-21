/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.service.SqlService;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConfig;

/**
 * AssociatedRecords class contains a list of ActiveRecord objects and 
 * its owner. It also has methods for the association.
 * 
 * @author (Fei) John Chen
 */
abstract public class AssociatedRecords {
    public AssociatedRecords(RecordRelation recordRelation) {
        if (recordRelation == null) 
            throw new IllegalArgumentException("Input for recordRelation is null.");
        
        this.recordRelation = recordRelation;
        
        latestRecordsLoaded = false;
    }
    
    public AssociatedRecords(RecordRelation recordRelation, List records) {
        this(recordRelation);
        this.associatedRecords = records;
        
        latestRecordsLoaded = true;
    }
    
    
    /**
     * Returns a specific associated record at index from the list.
     * 
     * @return ActiveRecord at the index
     */
    public ActiveRecord getRecord(int index) {
        List records = getRecords(!latestRecordsLoaded);//refresh the list if necessary
        ActiveRecord record = null;
        if (records != null && index < records.size()) record = (ActiveRecord)records.get(index);
        return record;
    }
    
    /**
     * Returns the associated records.
     */
    public List getRecords() {
        return getRecords(false);
    }
    
    /**
     * <p>Returns the associated records.</p>
     * 
     * <p>If <tt>refresh</tt> is <tt>true</tt>, a database retrieval will be 
     * tried for the owner object.</p>
     */
    public List getRecords(boolean refresh) {
        //retrieve current list
        if (refresh) {
            if (recordRelation instanceof HasManyRecordRelation) {
                associatedRecords = ((HasManyRecordRelation)recordRelation).retrieveAssociatedDataList();
            }
            else 
            if (recordRelation instanceof HasManyThroughRecordRelation) {
                associatedRecords = ((HasManyThroughRecordRelation)recordRelation).retrieveAssociatedDataList();
            }
            
            latestRecordsLoaded = true;
        }
        
        return associatedRecords;
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
     * Adds a record to the association. If the owner object is 
     * already in the database, the record will be either inserted or updated in 
     * the database. If the owner record is not in the database, the record 
     * will not be saved to the database.
     * 
     * @param record a record to be added to the relation. 
     * @return updated AssociatedRecords
     */
    public AssociatedRecords add(ActiveRecord record) {
        List records = new ArrayList();
        if (record != null) records.add(record);
        return add(records);
    }
    
    /**
     * Adds a list of records to the association. If the owner object is 
     * already in the database, the records will be either inserted or updated 
     * in the database. If the owner record is not in the database, the records 
     * will not be saved to the database.
     * 
     * @param records a list of records to be added to the relation. 
     * @return updated AssociatedRecords.
     */
    abstract public AssociatedRecords add(List records);
    
    /**
     * <p>Removes an object from the associated list. If the object is a 
     * record in database, its foreign key will be set to NULL. The record is 
     * not deleted unless it depends on the owner.</p>
     * 
     * <p>If the owner object doesn't exist in database, only the object that 
     * has been added to the relation before can be detached.</p>
     * 
     * @param record a record to be detached.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords detach(ActiveRecord record) {
        List records = new ArrayList();
        if (record != null) records.add(record);
        return detach(records);
    }
    
    /**
     * <p>Removes a list of objects from the associated list by setting their 
     * foreign keys to NULL. The objects are not deleted unless they depend on 
     * the owner.</p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects 
     * that have been added to the relation before can be detached.</p>
     * 
     * @param records list of records to be detached.
     * @return updated AssociatedRecords.
     */
    abstract public AssociatedRecords detach(List records);
    
    /**
     * <p>Deletes an object from the associated list and delete the record in 
     * database whether the object is dependent on the owner or not.</p>
     * 
     * <p>If the owner object doesn't exist in database, only the object that 
     * has been added to the relation before can be deleted.</p>
     * 
     * @param record The record to be deleted.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords delete(ActiveRecord record) {
        List records = new ArrayList();
        if (record != null) records.add(record);
        return delete(records);
    }
    
    /**
     * <p>Deletes a list objects from the associated list and delete the records in 
     * database whether the objects are dependent on the owner or not.</p>
     * 
     * <p>If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.</p>
     * 
     * @param records a list of records to be deleted.
     * @return updated AssociatedRecords.
     */
    abstract public AssociatedRecords delete(List records);
    
    /**
     * Removes all associated objects from the associated list by setting 
     * their foreign keys to NULL. The records are not deleted unless they 
     * depend on the owner. 
     * 
     * @return updated AssociatedRecords
     */
    public AssociatedRecords clear() {
        return detach(associatedRecords);
    }
    
    /**
     * <p>This is equivelent to clear() and add(records).</p>
     * 
     * <p>Removes all existing associated objects from the associated list by 
     * setting their foreign keys to NULL. The records are not deleted 
     * unless they depend on the owner. </p>
     * 
     * <p>Adds the new records to the association.</p>
     * 
     * @return updated AssociatedRecords
     */
    abstract public AssociatedRecords replace(List records);
    
    /**
     * Return true if this list contains no elements.
     * 
     * @return true if this list contains no elements.
     */
    public boolean isEmpty() {
        return (size()==0)?true:false;
    }
    
    /**
     * <p>Returns the number of associated objects. If the associated collection 
     * has already been loaded or the owner is new, this method simply returns 
     * the length of the current associated collection. If it hasn't been 
     * loaded yet, it calls count to get the length.</p>
     * 
     * <p>If you want to force a data load before calling size(), call 
     * <tt>getRecords(true)</tt> first.</p>
     * 
     * @return the number of associated objects.
     */
    public int size() {
        if (!latestRecordsLoaded && !ownerIsNew()) {
            int recordsSize = countRecordsInDB();
            
            if (recordsSize == 0) {
                latestRecordsLoaded = true;//actually no need to load
            }
            
            return recordsSize;
        }
        
        return (associatedRecords != null)?associatedRecords.size():0;
    }
    
    /**
     * Count number of associated records in the database.
     * 
     * @return number of associated records
     */
    public int count() {
        return countRecordsInDB();
    }
    
    /**
     * Cleans up cached data.
     */
    public void cleanCache() {
        associatedRecords = null;
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
     * Stores newly loaded database records from database.
     * 
     * @param records   newly loaded database records from database.
     */
    public void storeLoadedAssociatedRecords(List records) {
        associatedRecords = records;
        latestRecordsLoaded = true;
    }
    
    /**
     * Checks if a field's value of a record in the association is equal to a 
     * specific value.
     * 
     * @param field a field name
     * @param value a specific value to compare
     * @return true if the value is found
     */
    public boolean findBy(String field, Object value) {
        if (value == null) return false;
        
        List records = getRecords();
        if (records == null) return false;
        
        boolean found = false;
        
        //loop thru roles to see if the role id is in the list
        Iterator it = records.iterator();
        while(it.hasNext()) {
            ActiveRecord record = (ActiveRecord)it.next();
            Object fvalue = record.getField(field);
            if (fvalue != null && fvalue.toString().equalsIgnoreCase(value.toString())) {
                found = true;
                break;
            }
        }
        
        return found;
    }
    
    
    /**
     * Returns gateway to database
     */
    protected SqlService getSqlService() {
        return SqlServiceConfig.getSqlService();
    }
    
    /**
     * Counts number of associated records in the database.
     * 
     * @return number of associated records
     */
    abstract protected int countRecordsInDB();
    
    /**
     * Checks if the owner record is a new record.
     * @return true if new record
     */
    protected boolean ownerIsNew() {
        return getOwner().isNewRecord();
    }
    
    /**
     * Validates that the <tt>records</tt> are of the same type as 
     * <tt>expected</tt>.
     * 
     * @param expected expected class type
     * @param records  list of records to be checked
     */
    protected void validateRecordType(Class expected, List records) {
        if (expected == null || records == null) return;
        
        // make sure the record type is valid
        for(Iterator it = records.iterator(); it.hasNext();) {
            ActiveRecord testRecord = (ActiveRecord)it.next();
            ActiveRecordUtil.validateRecordType(expected, testRecord);
        }
    }
    
    /**
     * Removes a record from a list of records.
     * 
     * @param records list of records
     * @param record  the record to be removed
     * @return the remaining list
     */
    protected List removeRecordFromList(List records, ActiveRecord record) {
        if (records == null || record == null) return records;
        
        int currentSize = records.size();
        List newList = new ArrayList();
        for(int i=0; i<currentSize; i++) {
            ActiveRecord tmp = (ActiveRecord)records.get(i);
            if (!ActiveRecordUtil.isSameRecord(tmp, record)) {
                newList.add(tmp);
            }
        }
        
        return newList;
    }
    
    /**
     * The RecordRelation instance for this association.
     */
    protected RecordRelation recordRelation;
    
    /**
     * The list of associated records.
     */
    protected List associatedRecords = new ArrayList();
    
    /**
     * Indicates if associated records have been retrieved or not
     */
    protected boolean latestRecordsLoaded = false;
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
