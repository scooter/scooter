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
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorTypes;
import com.scooterframework.transaction.ImplicitTransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * AssociatedRecordsHM class contains a list of ActiveRecord objects and its
 * owner in a has-many association.
 * 
 * @author (Fei) John Chen
 */
public class AssociatedRecordsHM extends AssociatedRecords {
    public AssociatedRecordsHM(RecordRelation recordRelation) {
        super(recordRelation);
    }
    
    public AssociatedRecordsHM(RecordRelation recordRelation, List records) {
        super(recordRelation, records);
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
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_add(records);
            
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
    
    private AssociatedRecords internal_add(List records) {
        if (records == null || records.size() == 0) return this;
        
        //retrieve current list
        associatedRecords = getRecords();
        
        // make sure the record type is valid
        validateRecordType(getRelation().getTargetClass(), records);
        
        // now add the records to db and memory 
        if (associatedRecords == null) associatedRecords = new ArrayList();
        
        ActiveRecord owner = getOwner();
        if (!owner.isNewRecord()) {
            int inputSize = records.size();
            for(int i=0; i<inputSize; i++) {
                ActiveRecord record = (ActiveRecord)records.get(i);
                if (record == null) continue;
                
                //chenfei: Don't do this here: AssociationHelper.populateFKInHasMany(owner, getRelation().getMappingMap(), record);
                record.associated(getRelation().getReverseRelationName()).attach(owner);
                
                associatedRecords.add(record);
            }
        }
        else {
            associatedRecords.addAll(records);
        }
        
        return this;
    }
    
    /**
     * Removes a list of objects from the associated list by setting their foreign 
     * keys to NULL. The objects are not deleted unless they depend on the owner. 
     * 
     * If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.
     * 
     * @param records list of records to be detached.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords detach(List records) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_detach(records);
            
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
    
    private AssociatedRecords internal_detach(List records) {
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
                        Relation baRelation = getRelation().getReverseRelation();
                        BelongsToRecordRelation baBTRR = 
                    		new BelongsToRecordRelation(record, (BelongsToRelation)baRelation);
                    	AssociatedRecord baAssociatedRecord = new AssociatedRecord(baBTRR, getOwner());
                    	baBTRR.setAssociatedData(baAssociatedRecord);
                    	record.setRecordRelation(baRelation.getAssociation(), baBTRR);
                    	
                        record.associated(baRelation.getAssociation()).detach();
                        associatedRecords = removeRecordFromList(associatedRecords, record);
                    }
                }
            }
        }
        
        return this;
    }
    
    /**
     * Deletes a list objects from the associated list and delete the records in 
     * database whether the objects are dependent on the owner or not.
     * 
     * If the owner object doesn't exist in database, only those objects that 
     * have been added to the relation before can be detached.
     * 
     * @param records a list of records to be deleted.
     * @return updated AssociatedRecords.
     */
    public AssociatedRecords delete(List records) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = intenal_delete(records);
            
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
    
    private AssociatedRecords intenal_delete(List records) {
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
     * This is equivelent to clear() and add(records).
     * 
     * Removes all existing associated objects from the associated list by 
     * setting their foreign keys to NULL. The records are not deleted 
     * unless they depend on the owner. 
     * 
     * Adds the new records to the association.
     * 
     * @return updated AssociatedRecords
     */
    public AssociatedRecords replace(List records) {
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        AssociatedRecords assocs = null;
        try {
            tm.beginTransactionImplicit();
            
            assocs = internal_replace(records);
            
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
    
    private AssociatedRecords internal_replace(List records) {
        if (records == null || records.size() == 0) return this;
        
        AssociatedRecords assr = null;
        
        try {
            clear();
            assr = add(records);
        }
        catch(Exception ex) {
            throw new RelationException(ex);
        }
        return assr;
    }
    
    
    /**
     * Counts number of associated records in the database.
     * 
     * @return number of associated records
     */
    protected int countRecordsInDB() {
        Object returnObj = null;
        
        try {
            Relation rel = recordRelation.getRelation();
            Map properties = rel.getProperties();
            
            String conditionSql = rel.getConditionsString();
            
            ActiveRecord homeB = ActiveRecordUtil.getHomeInstance(rel.getTargetClass());
            String findSQL = Calculator.getCountSelectPart(homeB, properties);
            
            String whereClause = "";
            
            //set target's FK with owner's PK data
            Map fkData = recordRelation.getFKDataMapForOther();
            if (fkData == null) return 0;
            
            //construct where clause and data
            boolean hasWhere = false;
            Map inputs = new HashMap();
            if (fkData.size() > 0) {
                hasWhere = true;
                int position = 1;
                for(Iterator it = fkData.keySet().iterator(); it.hasNext();) {
                    String columnName = (String) it.next();
                    Object conditionData = fkData.get(columnName);
                    whereClause += columnName + " = ? AND ";
                    //inputs.put(columnName, conditionData);
                    inputs.put(position+"", conditionData);
                    
                    position = position + 1;
                }
                
                if (whereClause.endsWith("AND ")) {
                    int lastAnd = whereClause.lastIndexOf("AND ");
                    whereClause = whereClause.substring(0, lastAnd);
                }
            }
            
            if (hasWhere) {
                findSQL += " WHERE " + whereClause;
            
                if (conditionSql != null && !"".equals(conditionSql)) {
                    findSQL += " AND (" + conditionSql + ")";
                }
            }
            else {
                if (conditionSql != null && !"".equals(conditionSql)) {
                    findSQL += " WHERE " + conditionSql;
                }
            }
            
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL);
            
            if ( returnTO != null ) {
                TableData rt = returnTO.getTableData(findSQL);
                if (rt != null) {
                    returnObj = rt.getFirstObject();
                }
            }
        }
        catch (Exception ex) {
            throw new RelationException(ex);
        }
        
        return (returnObj != null)?Integer.parseInt(returnObj.toString()):-1;
    }

}
