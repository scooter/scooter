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

import com.scooterframework.common.util.Converters;

/**
 * HasOneRecordRelation class represents a has-one relation instance. 
 * 
 * For example, A has one B. 
 * 
 * In has-one relation, foreign key is held by class B. 
 * 
 * Class A is the owner of the relation.
 * 
 * @author (Fei) John Chen
 */
public class HasOneRecordRelation extends RecordRelation {
    public HasOneRecordRelation(ActiveRecord owner, HasOneRelation relation) {
        super(owner, relation);
    }
    
    /**
     * Find the associated record. 
     * 
     * For example, "order" has one "invoice".
     * 
     * SQL: select * from invoice where invoice.order_id = ${order.id}
     * 
     * @param options A string of options.
     * @param refresh If true, refresh data from database
     * @return AssociatedRecord
     */
    public AssociatedRecord associatedRecord(String options, boolean refresh) {
        AssociatedRecord relatedData = (AssociatedRecord)getAssociatedData();
        
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
                    relatedData = new AssociatedRecord(this);
                    setAssociatedData(relatedData);
                }
            }
            else {
                if (relatedData == null) {
                    relatedData = new AssociatedRecord(this, retrieveAssociatedData(options));
                    setAssociatedData(relatedData);
                }
                else {
                    relatedData.storeLoadedAssociatedRecord(retrieveAssociatedData(options));
                }
            }
        }
        
        super.setLastUsedOptions(options);
        
        return relatedData;
    }

    protected ActiveRecord retrieveAssociatedData(String options) {
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

        return ActiveRecordUtil.getHomeInstance(getRelation().getTargetClass()).findFirst(fkData, opts);
    }
}
