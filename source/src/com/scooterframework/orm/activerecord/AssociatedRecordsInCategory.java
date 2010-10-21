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
import java.util.Set;

/**
 * AssociatedRecordsInCategory class contains a list of ActiveRecord objects and 
 * its owner for a category.
 * 
 * @author (Fei) John Chen
 */
public class AssociatedRecordsInCategory {
    /**
     * Owner record is the owner of the category association. For example, 
     * Tag is owner of the taggable category, the center class of the 
     * category may be Tagging.class. 
     * 
     * @param owner     owner record of the category
     * @param category  name of the category
     * @param type      specific record type in the category
     * @param refresh   true if retrieving data from database
     */
    public AssociatedRecordsInCategory(ActiveRecord owner, String category, String type, boolean refresh) {
        this.owner = owner;
        this.category = category;
        this.type = type;
        
        initialize(refresh);
    }
    
    /**
     * Returns AssociatedRecords of a type.
     * 
     * @param type type name in the category
     * @return AssociatedRecords instance for the specific type
     */
    public AssociatedRecords getAssociatedRecordsByType(String type) {
        String entity = categoryInstance.getEntityByType(type);
        return (AssociatedRecords)targetAssrMap.get(entity);
    }
    
    /**
     * Returns a specific associated record at index from the list for a 
     * specific type.
     * 
     * @param type type name in the category
     * @param index index position of the record
     * @return an ActiveRecord instance at the index
     */
    public ActiveRecord getRecord(String type, int index) {
        List records = getRecords(type);
        ActiveRecord record = null;
        if (records != null && index < records.size()) record = (ActiveRecord)records.get(index);
        return record;
    }
    
    /**
     * Returns the associated records for all types.
     * 
     * @return a list of ActiveRecord instances for all types
     */
    public List getRecords() {
        return getRecords(false);
    }
    
    /**
     * Returns the associated records of all types.
     * 
     * @param refresh <tt>true</tt> if database records are to be relaoded.
     * @return a list of ActiveRecord instances for all types
     */
    public List getRecords(boolean refresh) {
        List list = new ArrayList();
        Iterator it = targetAssrMap.keySet().iterator();
        while(it.hasNext()) {
            Object entity = it.next();
            AssociatedRecords ars = (AssociatedRecords)targetAssrMap.get(entity);
            List records = ars.getRecords(refresh);
            if (records != null) list.addAll(records);
        }
        
        if (refresh) latestRecordsLoaded = true;
        
        return list;
    }
    
    /**
     * Returns the associated records of a particular type.
     * 
     * @param type type name in the category
     * @return a list of ActiveRecord instances for the specific type
     */
    public List getRecords(String type) {
        return getRecords(type, false);
    }
    
    /**
     * Returns the associated records of a particular type.
     * 
     * @param type type name in the category
     * @return a list of ActiveRecord instances for the specific type
     */
    public List getRecords(String type, boolean refresh) {
        List list = new ArrayList();
        AssociatedRecords ars = getAssociatedRecordsByType(type);
        if (ars != null) {
            List records = ars.getRecords(refresh);
            if (records != null) list.addAll(records);
        }
        return list;
    }
    
    /**
     * Returns owner of the association.
     */
    public ActiveRecord getOwner() {
        return owner;
    }
    
    /**
     * Returns category.
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Returns category instance.
     */
    public Category getCategoryInstance() {
        return categoryInstance;
    }
    
    /**
     * Return true if this list contains no elements.
     * 
     * @return true if this list contains no elements.
     */
    public boolean isEmpty() {
        return (size()==0)?true:false;
    }
    
    /**
     * Returns the number of associated objects. 
     * 
     * @return the number of associated objects.
     */
    public int size() {
        int size = 0;
        Iterator it = targetAssrMap.keySet().iterator();
        while(it.hasNext()) {
            Object entity = it.next();
            AssociatedRecords ars = (AssociatedRecords)targetAssrMap.get(entity);
            size += ars.size();
        }
        return size;
    }
    
    /**
     * Adds target record to the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>add</tt> method.
     * 
     * @param record a record to be added to the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory add(ActiveRecord record) {
        if (record != null) {
            getAssociatedRecordsHMT(record).add(record);
        }
        return this;
    }
    
    /**
     * Adds target record to the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>add</tt> method.
     * 
     * @param record a record to be added to the association. 
     * @param joinInput A map of input data for the join record. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory add(ActiveRecord record, Map joinInput) {
        if (record != null) {
            getAssociatedRecordsHMT(record).add(record, joinInput);
        }
        return this;
    }
    
    /**
     * Adds a list of target records to the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>add</tt> method.
     * 
     * @param targets a list of target record to be added to the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory add(List targets) {
        if (targets == null || targets.size() == 0) return this;
        
        int size = targets.size();
        for (int i=0; i<size; i++) {
            add((ActiveRecord)targets.get(i));
        }
        return this;
    }
    
    /**
     * Adds a list of target records to the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>add</tt> method.
     * 
     * @param targets a list of target record to be added to the association. 
     * @param joinInputs a list of input data map for the through table. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory add(List targets, List joinInputs) {
        if (targets == null || targets.size() == 0) return this;
        
        if (joinInputs != null && joinInputs.size() != targets.size()) 
            throw new IllegalArgumentException("The size of joinInputs must be the same as records size.");
        
        int size = targets.size();
        for (int i=0; i<size; i++) {
            add((ActiveRecord)targets.get(i), (Map)joinInputs.get(i));
        }
        return this;
    }
    
    /**
     * Deletes target record from the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>delete</tt> method.
     * 
     * @param target a record to be deleted from the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory delete(ActiveRecord target) {
        if (target != null) {
            getAssociatedRecordsHMT(target).delete(target);
        }
        return this;
    }
    
    /**
     * Deletes a list of target records from the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>delete</tt> method.
     * 
     * @param targets a list of target record to be deleted from the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory delete(List targets) {
        if (targets == null || targets.size() == 0) return this;
        
        int size = targets.size();
        for (int i=0; i<size; i++) {
            delete((ActiveRecord)targets.get(i));
        }
        return this;
    }
    
    /**
     * Detaches target record from the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>detach</tt> method.
     * 
     * @param target a record to be detached from the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory detach(ActiveRecord target) {
        if (target != null) {
            getAssociatedRecordsHMT(target).detach(target);
        }
        return this;
    }
    
    /**
     * Detaches target record from the association. This method simply 
     * delegates the request to a corresponding HMT association's 
     *<tt>detach</tt> method.
     * 
     * @param target a record to be detached from the association. 
     * @param keepJoinRecord if true, keep the join record. Otherwise, delete it.
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory detach(ActiveRecord target, boolean keepJoinRecord) {
        if (target != null) {
            getAssociatedRecordsHMT(target).detach(target, keepJoinRecord);
        }
        return this;
    }
    
    /**
     * Detaches a list of target records from the association. This method 
     * simply delegates the request to a corresponding HMT association's 
     *<tt>detach</tt> method.
     * 
     * @param targets a list of target record to be deleted from the association. 
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory detach(List targets) {
        if (targets == null || targets.size() == 0) return this;
        
        int size = targets.size();
        for (int i=0; i<size; i++) {
            detach((ActiveRecord)targets.get(i));
        }
        return this;
    }
    
    /**
     * Detaches a list of target records from the association. This method 
     * simply delegates the request to a corresponding HMT association's 
     *<tt>detach</tt> method.
     * 
     * @param targets a list of target record to be deleted from the association. 
     * @param keepJoinRecord if true, keep the join record. Otherwise, delete it.
     * @return updated AssociatedRecordsInCategory
     */
    public AssociatedRecordsInCategory detach(List targets, boolean keepJoinRecord) {
        if (targets == null || targets.size() == 0) return this;
        
        int size = targets.size();
        for (int i=0; i<size; i++) {
            detach((ActiveRecord)targets.get(i), keepJoinRecord);
        }
        return this;
    }
    
    private void initialize(boolean refresh) {
        categoryInstance = RelationManager.getInstance().getCategory(category);
        if (categoryInstance == null) {
            throw new UnregisteredCategoryException(category);
        }
        
        if (type == null) {
            //get all types
            Set targets = categoryInstance.getEntitys();
            Iterator it = targets.iterator();
            while(it.hasNext()) {
                String target = (String)it.next();
                AssociatedRecords ars = owner.allAssociated(target, refresh);
                targetAssrMap.put(target, ars);
            }
        }
        else {
            String target = categoryInstance.getEntityByType(type);
            AssociatedRecords ars = owner.allAssociated(target, refresh);
            targetAssrMap.put(target, ars);
        }
        
        latestRecordsLoaded = true;
    }
    
    private AssociatedRecordsHMT getAssociatedRecordsHMT(ActiveRecord target) {
        if (target == null) return null;
        
        String entity = ActiveRecordUtil.getModelName(target.getClass());
        AssociatedRecordsHMT arsHMT = (AssociatedRecordsHMT)targetAssrMap.get(entity);
        if (arsHMT == null) {
            throw new UndefinedRelationException(owner.getClass(), target.getClass());
        }
        return arsHMT;
    }

    /**
     * owner of category.
     */
    private ActiveRecord owner;
    
    private String category;
    
    private String type;
    
    private Category categoryInstance;
    
    /**
     * Map of target and its related AssociatedRecords instance.
     * Key of the map is entity name of the target, value is the related 
     * AssociatedRecords instance.
     */
    private Map targetAssrMap = new HashMap();
    
    //indicate if associated records have been retrieved or not
    protected boolean latestRecordsLoaded = false;
}
