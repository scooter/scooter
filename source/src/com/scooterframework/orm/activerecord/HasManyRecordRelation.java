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

import com.scooterframework.common.util.Converters;

/**
 * HasManyRecordRelation class represents a has-many relation instance. 
 * 
 * For example, A has many B. 
 * 
 * In has-many relation, foreign key is hold by class B. 
 * 
 * Class A is the owner of the relation.
 * 
 * @author (Fei) John Chen
 */
public class HasManyRecordRelation extends RecordRelation {
    public HasManyRecordRelation(ActiveRecord owner, HasManyRelation relation) {
        super(owner, relation);
    }
    
    /**
     * Find the associated record. 
     * 
     * For example, "order" has many "lineitem".
     * <pre>
     * SQL: select * from lineitem where lineitem.order_id = ${order.id}
     * </pre>
     * 
     * @param options A string of options.
     * @param refresh If true, refresh data from database
     * @return AssociatedRecords
     */
    public AssociatedRecords allAssociatedRecords(String options, boolean refresh) {
        AssociatedRecords relatedData = (AssociatedRecords)getAssociatedData();
        
        if (!refresh && !optionsChangedFromLastRetrieval(options)) {
            if (relatedData != null && relatedData.hasLoadedFromDatabase()) {
                return relatedData;
            }
            else {
                refresh = true;
            }
        }
        
        if (refresh || optionsChangedFromLastRetrieval(options)) {
            if (owner.isNewRecord()) {
                if (relatedData == null) {
                    relatedData = new AssociatedRecordsHM(this);
                    setAssociatedData(relatedData);
                }
            }
            else {
                if (relatedData == null) {
                    relatedData = new AssociatedRecordsHM(this, retrieveAssociatedDataList(options));
                    setAssociatedData(relatedData);
                }
                else {
                    relatedData.storeLoadedAssociatedRecords(retrieveAssociatedDataList(options));
                }
            }
        }
        
        super.setLastUsedOptions(options);
        
        return relatedData;
    }
    
    protected List retrieveAssociatedDataList(String options) {
        //set target's FK with owner's PK data
        Map fkData = getFKDataMapForOther();
        if (fkData == null) return null;
        
        //merge the two options maps
        Map opts = getRelation().getProperties();
        if (options != null && !"".equals(options)) {
            if (opts == null) opts = new HashMap();
            Map m = Converters.convertSqlOptionStringToMap(options);
            opts.putAll(m);
        }
        
        return ActiveRecordUtil.getHomeInstance(getRelation().getTargetClass()).findAll(fkData, opts);
    }
}
