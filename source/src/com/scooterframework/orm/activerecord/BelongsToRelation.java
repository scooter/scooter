/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Map;

import com.scooterframework.common.util.WordUtil;

/**
 * BelongsToRelation class has information about belongs-to 
 * relationship between objects. 
 * 
 * For example, A belongs to B. 
 * 
 * In belongs-to relation, foreign key is held by class A. 
 * 
 * @author (Fei) John Chen
 */
public class BelongsToRelation extends Relation {
    public BelongsToRelation(Class endA, String associationId, String targetModel) {
        super(endA, BELONGS_TO_TYPE, associationId, targetModel);
    }
    
    /**
     * Set the property map.
     */
    public void setProperties(Map properties) {
        this.properties = properties;
        if (properties != null && properties.containsKey(ActiveRecordConstants.key_counter_cache)) {
            String counter = (String)properties.get(ActiveRecordConstants.key_counter_cache);
            if ("true".equalsIgnoreCase(counter)) {
                counterCacheName = getDefaultCounterCacheName();
                useCounterCache = true;
            }
            else if (!"false".equalsIgnoreCase(counter)) {
                counterCacheName = counter;
                
                //verify the counterCacheName field
                ActiveRecordUtil.verifyExistenceOfColumn(super.getTargetClass(), counterCacheName);
                useCounterCache = true;
            }
        }
    }
    
    /**
     * Checks if counter cache field exists.
     * 
     * @return true if exists.
     */
    public boolean hasCounterCache() {
        return useCounterCache;
    }
    
    /**
     * Returns counter cache field name
     * 
     * @return String counter cache name
     */
    public String getCounterCacheName() {
        return counterCacheName;
    }
    
    /**
     * Returns default counter cache field name. 
     * 
     * The default counter cache name is owner's model name in plural form  
     * appended with string "_count".
     * 
     * For example, the default counter cache column name in post table is 
     * <tt>comments_count</tt>. 
     * 
     * @return String default counter cache name
     */
    private String getDefaultCounterCacheName() {
        return WordUtil.pluralize(super.getOwnerModel()) + "_count";
    }
    
    private boolean useCounterCache = false;
    private String counterCacheName = "";
}
