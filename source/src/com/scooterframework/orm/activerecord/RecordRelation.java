/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecordRelation class represents a relation instance. The relation class
 * specifies a binary relation. The record object is the owner class in the
 * relation.
 *
 * @author (Fei) John Chen
 */
public class RecordRelation {
    public RecordRelation(ActiveRecord owner, Relation relation) {
        this.owner = owner;
        this.relation = relation;
    }

    /**
     * Returns the relation.
     */
    public Relation getRelation() {
        return relation;
    }

    /**
     * Returns the owner of the relation.
     */
    public ActiveRecord getOwner() {
        return owner;
    }

    /**
     * Finds the associated record
     *
     * @return AssociatedRecord
     */
    public AssociatedRecord associatedRecord() {
        return associatedRecord(false);
    }

    /**
     * Finds the associated record
     *
     * @param refresh   If true, refresh data from database
     * @return AssociatedRecord
     */
    public AssociatedRecord associatedRecord(boolean refresh) {
        return associatedRecord(null, refresh);
    }

    /**
     * <p>Finds the associated record with some <tt>options</tt>.</p>
     *
     * <p>See description on top of the 
     * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * for details about <tt>options</tt>.</p>
     *
     * <p>Note: When the same option has already been specified in the relation
     * method such as <tt>hasOne</tt> or <tt>belongsTo</tt>, the value listed
     * here overrides the value defined in the relation for the same option.</p>
     *
     * @param options A string of options.
     * @return AssociatedRecord
     */
    public AssociatedRecord associatedRecord(String options) {
        return associatedRecord(options, false);
    }

    /**
     * <p>Finds the associated record with some <tt>options</tt>. Subclass
     * of this class must override this method in order to provide specific
     * data retrieval mechanism.</p>
     *
     * <p>See description on top of the 
     * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * for details about <tt>options</tt>.</p>
     *
     * <p>Note: When the same option has already been specified in the relation
     * method such as <tt>hasOne</tt> or <tt>belongsTo</tt>, the value listed
     * here overrides the value defined in the relation for the same option.</p>
     *
     * @param options   A string of options.
     * @param refresh   If true, refresh data from database
     * @return AssociatedRecord
     */
    public AssociatedRecord associatedRecord(String options, boolean refresh) {
        throw new RelationException("The method does not support this type of relation.");
    }

    /**
     * Finds all the associated records. If there is data in cache, then return
     * the data in cache.
     *
     * @return AssociatedRecords contains list of ActiveRecord
     */
    public AssociatedRecords allAssociatedRecords() {
        return allAssociatedRecords(false);
    }

    /**
     * Finds all the associated records.
     *
     * @param refresh   If true, refresh data from database
     * @return AssociatedRecords contains list of ActiveRecord
     */
    public AssociatedRecords allAssociatedRecords(boolean refresh) {
        return allAssociatedRecords(null, refresh);
    }

    /**
     * <p>Finds all the associated records. If there is data in cache, then return
     * the data in cache.</p>
     *
     * <p>See description on top of the 
     * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * for details about <tt>options</tt>.</p>
     *
     * <p>Note: When the same option has already been specified in the relation
     * method such as <tt>hasMany</tt> or <tt>hasManyThrough</tt>, the value listed
     * here overrides the value defined in the relation for the same option.</p>
     *
     * @param options   A string of options.
     * @return AssociatedRecords contains list of ActiveRecord
     */
    public AssociatedRecords allAssociatedRecords(String options) {
        return allAssociatedRecords(options, false);
    }

    /**
     * <p>Finds all the associated records with some <tt>options</tt>. Subclass
     * of this class must override this method in order to provide specific
     * data retrieval mechanism.</p>
     *
     * <p>See description on top of the 
     * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * for details about <tt>options</tt>.</p>
     *
     * <p>Note: When the same option has already been specified in the relation
     * method such as <tt>hasMany</tt> or <tt>hasManyThrough</tt>, the value listed
     * here overrides the value defined in the relation for the same option.</p>
     *
     * @param options   A string of options.
     * @param refresh   If true, refresh data from database
     * @return AssociatedRecords contains list of ActiveRecord
     */
    public AssociatedRecords allAssociatedRecords(String options, boolean refresh) {
        throw new RelationException("The method does not support this type of relation.");
    }

    /**
     * Retrieves cached associated data.
     *
     * @return an AssociatedRecord or AssociatedRecords object.
     */
    public Object getAssociatedData() {
        return associationDataMap.get(relation.getAssociation());
    }

    /**
     * Stores associated data to cache.
     *
     * @param data should be an AssociatedRecord object.
     */
    public void setAssociatedData(AssociatedRecord data) {
        associationDataMap.put(relation.getAssociation(), data);
    }

    /**
     * Stores associated data to cache.
     *
     * @param data should be an AssociatedRecords object.
     */
    public void setAssociatedData(AssociatedRecords data) {
        associationDataMap.put(relation.getAssociation(), data);
    }

    /**
     * Retrieves associated ActiveRecord data.
     * @return retrieved active record instance
     */
    protected ActiveRecord retrieveAssociatedData() {
        return retrieveAssociatedData(lastUsedOptions);
    }

    /**
     * Retrieves associated ActiveRecord data.
     *
     * This method must be implemented by the subclass of this class for
     * either belongs-to type or has-one type.
     *
     * @param options  options used in retrieving the record
     * @return retrieved active record instance
     */
    protected ActiveRecord retrieveAssociatedData(String options) {
        throw new RelationException("The method must be called by subclass of HasOne or BelongsTo type.");
    }

    /**
     * Retrieves a list of associated ActiveRecord data.
     *
     * @return retrieved active record instance list
     */
    protected List<ActiveRecord> retrieveAssociatedDataList() {
        return retrieveAssociatedDataList(lastUsedOptions);
    }

    /**
     * Retrieves a list of associated ActiveRecord data.
     *
     * This method must be implemented by the subclass of this class for
     * either has-many type or has-many-through type.
     *
     * @param options options used in retrieving the record
     * @return retrieved active record instance list
     */
    protected List<ActiveRecord> retrieveAssociatedDataList(String options) {
        throw new RelationException("The method must be called by subclass of HasMany or HasManyThrough type.");
    }

    /**
     * Returns the last used options in retrieving data.
     *
     * @return options used last time
     */
    protected String getLastUsedOptions() {
        return lastUsedOptions;
    }

    /**
     * Sets options used last time when retrieving data.
     *
     * @param options  options used last time
     */
    protected void setLastUsedOptions(String options) {
        lastUsedOptions = options;
    }

    /**
     * Checks if options changed after last used.
     *
     * @param options  options to be used in retrieving data
     * @return true if changed
     */
    protected boolean optionsChangedFromLastRetrieval(String options) {
        boolean state= false;
        if (options != null) {
            if (!options.equals(lastUsedOptions)) {
                state = true;
            }
        }
        else {
            if (lastUsedOptions != null) state = true;
        }
        return state;
    }

    /**
     * <p>Creates a data map of which key is the foreign key in child record,
     * while the value is the corresponding data from parent record.
     * But if the data from parent record is <tt>null</tt>, then a null map
     * is returned.</p>
     *
     * <p>Note: This method is used in a belongs-to, has-one, has-many or
     * has-many-through relation.</p>
     *
     * <p>Example, order has-one status: order.oid = status.order_id. In this
     * case, order is owner and status is target. This method sets target's
     * FK with owner's PK field for relationship mapping:
     * <tt>owner.oid = status.order_id</tt>. Therefore, in the created map, the
     * key is order_id, while the value is the data from owner.oid field.</p>
     */
    protected Map<String, Object> getFKDataMapForOther() {
    	boolean noParent = false;
        Map<String, Object> fkData = new HashMap<String, Object>();
        Map<String, String> mappingMap = relation.getMappingMap();
        for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
            String leftKey = entry.getKey();
            String rightValue = entry.getValue();
            if (rightValue == null) continue;
            Object parentValue = owner.getField(leftKey);
            if (parentValue == null) {
            	noParent = true;
            	break;
            }
            fkData.put(rightValue, parentValue);
        }

        return (noParent)?null:fkData;
    }

    /**
     * Creates a FK map with null value.
     *
     * @return map
     */
    protected Map<String, Object> getNullFKDataMapForOther() {
        Map<String, Object> fkData = new HashMap<String, Object>();
        Map<String, String> mappingMap = relation.getMappingMap();
        for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
        	String rightValue = entry.getValue();
            if (rightValue != null) fkData.put(rightValue, null);
        }

        return fkData;
    }

    protected ActiveRecord owner;
    protected Relation relation;

    /**
     * Value of <tt>options</tt> input in the latest method call of either
     * <tt>associatedRecord</tt> or <tt>allAssociatedRecords</tt> method.
     */
    protected String lastUsedOptions;

    /**
     * A cache of associated data.
     *
     * Key is entity name. Value is AssociatedRecord object for has-one, and
     * belongs-to relation; or AssociatedRecords object for has-many and
     * has-many-through relation.
     */
    protected Map<String, Object> associationDataMap = new HashMap<String, Object>();
}
