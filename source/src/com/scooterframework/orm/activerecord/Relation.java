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

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;


/**
 * Relation class has information about relationship between objects. 
 * There are three types of binary relations supported:
 * <pre>
 * 1. has-one:      A has one B.
 * 2. has-many:     A has many B.
 * 3. belongs-to:   A belongs to B.
 * </pre>
 * 
 * For has-many-through relation, the HasManyThroughRelation subclass 
 * should be used. 
 * 
 * @author (Fei) John Chen
 */
abstract public class Relation {
    public Relation(Class<? extends ActiveRecord> ownerClass, String type, String associationId, String targetModel) {
        this.ownerClass = ownerClass;
        this.type = type;
        this.associationId = associationId;
        this.targetModel = targetModel;
    }
    
    /**
     * Returns the relation type.
     * 
     * @return the relation type
     */
    public String getRelationType() {
        return type;
    }
    
    /**
     * Returns the association target.
     * 
     * @return the association target
     */
    public String getAssociation() {
        return associationId;
    }
    
    /**
     * <p>Returns the type of the reverse relation. For <tt>has-one</tt> and 
     * <tt>has-many</tt> relations, the reverse relation type must always be of 
     * <tt>belongs-to</tt>.</p>
     * 
     * <p>To ease any confusion, it is better to state reverse relation by 
     * using the <tt>reverse</tt> key word in the properties attribute when 
     * declaring a relation.</p>
     * 
     * <p>This is mostly used by a belongs-to class to figure out if the 
     * reverse relation type is has-one or has-many.</p>
     * 
     * @return the reverse relation type
     */
    public String getReverseRelationType() {
        return getReverseRelation().getRelationType();
    }
    
    /**
     * <p>Returns reverse relation.</p>
     * 
     * <p>To ease any confusion, it is better to state reverse relation by 
     * using the <tt>reverse</tt> key word in the properties attribute when 
     * declaring a relation.</p>
     * 
     * <p>The reverse relation for a has-one or has-many relation is always 
     * of belongs-to type.</p>
     * 
     * @return reverse relation
     */
    public Relation getReverseRelation() {
        Relation reverseRelation = null;
        String reverseAssociationId = properties.get(REVERSE_RELATION);
        if (reverseAssociationId != null) {
            reverseRelation = RelationManager.getInstance().getRelation(getTargetClass(), reverseAssociationId);
        }
        else {
            if (HAS_ONE_TYPE.equals(getRelationType()) || HAS_MANY_TYPE.equals(getRelationType())) {
                //the reverse relation must be of beongs_to type
                reverseRelation = RelationManager.getInstance().getRelation(getTargetClass(), getOwnerModel());
                if (reverseRelation == null || !BELONGS_TO_TYPE.equals(reverseRelation.getRelationType())) {
                    reverseRelation = RelationManager.getInstance().getBelongsToRelationBetween(getTargetClass(), getOwnerClass());
                }
                
                if (reverseRelation == null) {
                    throw new UndefinedRelationException(getTargetModel(), getOwnerModel());
                }
            }
            else 
            if (BELONGS_TO_TYPE.equals(getRelationType())) {
                //the reverse relation must be of has-many or has-one type
                reverseRelation = RelationManager.getInstance().getRelation(getTargetClass(), WordUtil.pluralize(getOwnerModel()));
                if (reverseRelation == null) {
                    reverseRelation = RelationManager.getInstance().getRelation(getTargetClass(), getOwnerModel());
                    if (reverseRelation == null) {
                        reverseRelation = RelationManager.getInstance().getHasManyRelationBetween(getTargetClass(), getOwnerClass());
                        if (reverseRelation == null) {
                            reverseRelation = RelationManager.getInstance().getHasOneRelationBetween(getTargetClass(), getOwnerClass());
                        }
                    }
                }
            }
            //has-many-through type should use the reverse key word to define a reverse relation
        }
        
        if (reverseRelation == null) {
            throw new UndefinedReverseRelationException(getOwnerModel(), getTargetModel());
        }
        return reverseRelation;
    }
    
    /**
     * <p>Returns reverse relation association id.</p>
     * 
     * <p>To ease any confusion, it is better to state reverse relation by 
     * using the <tt>reverse</tt> key word in the properties attribute when 
     * declaring a relation.</p>
     * 
     * @return reverse relation name
     */
    public String getReverseRelationName() {
        return getReverseRelation().getAssociation();
    }
    
    /**
     * Returns owner class.
     */
    public Class<? extends ActiveRecord> getOwnerClass() {
        return ownerClass;
    }
    
    /**
     * Returns target class.
     */
    public Class<? extends ActiveRecord> getTargetClass() {
    	if (targetClass == null) {
    	    targetClass = ActiveRecordUtil.getHomeInstance(EnvConfig.getInstance().getModelClassName(getTargetModel())).getClass();
    	}
        return targetClass;
    }
    
    /**
     * Sets target class.
     * 
     * @param targetClass target class
     */
    public void setTargetClass(Class<? extends ActiveRecord> targetClass) {
        this.targetClass = targetClass;
    }
    
    /**
     * Returns owner model name.
     */
    public String getOwnerModel() {
        if (ownerModel == null) {
            ownerModel = ActiveRecordUtil.getModelName(ownerClass);
        }
        return ownerModel;
    }
    
    /**
     * Returns target model name.
     */
    public String getTargetModel() {
        return targetModel;
    }
    
    /**
     * Returns the mapping string. 
     * 
     * The mapping string is like: "id=order_id" where id is primary key of 
     * Order record and order_id is foreign-key of Invoice record. 
     */
    public String getMapping() {
        return mapping;
    }
    
    /**
     * Returns the reverse mapping.
     */
    public String getReverseMapping() {
        return StringUtil.reverseMapping(mapping);
    }
    
    /**
     * Sets the mapping string. 
     */
    void setMapping(String mapping) {
        this.mapping = mapping;
        properties.put(ActiveRecordConstants.key_mapping, mapping);
    }
    
    /**
     * Returns an array of all strings on the left-side of the mapping.
     * 
     * @return an array of all strings on the left-side of the mapping.
     */
    public String[] getLeftSideMappingItems() {
        Map<String, String> mappingMap = Converters.convertStringToMap(getMapping());
        String[] sa = new String[mappingMap.size()];
        int i = 0;
        for (String s : mappingMap.keySet()) {
        	sa[i++] = s;
        }
        return sa;
    }
    
    /**
     * Returns an array of all strings on the right-side of the mapping.
     * 
     * @return an array of all strings on the right-side of the mapping.
     */
    public String[] getRightSideMappingItems() {
        Map<String, String> mappingMap = Converters.convertStringToMap(getMapping());
        String[] sa = new String[mappingMap.size()];
        int i = 0;
        for (String s : mappingMap.values()) {
        	sa[i++] = s;
        }
        return sa;
    }
    
    /**
     * Returns a Map of mapping string
     * 
     * For belongs-to relation, the key is the foreign-key column of Class A, 
     * while the corresponding value is the primary-key field of Class B. 
     * 
     * For has-one and has-many relations, the key is the primary-key field 
     * of Class A, while the corresponding value is the foreign-key field 
     * of Class B.
     * 
     * @return a Map of mapping string
     */
    public Map<String, String> getMappingMap() {
        return Converters.convertStringToMap(getMapping());
    }
    
    public Map<String, String> getReverseMappingMap() {
        return Converters.convertStringToMap(getReverseMapping());
    }
    
    /**
     * Returns the property map.
     */
    public Map<String, String> getProperties() {
        return properties;
    }
    
    /**
     * Returns the conditions_sql in the property map.
     */
    public String getConditionsString() {
        return properties.get(ActiveRecordConstants.key_conditions_sql);
    }
    
    /**
     * Returns the conditions_sql in the property map. Replaces table name 
     * with mapping alias table name.
     */
    public String getConditionsString(String tableName, String aliasTableName) {
        String condition = properties.get(ActiveRecordConstants.key_conditions_sql);
        if (condition != null) {
            if (condition.toLowerCase().indexOf((tableName+".").toLowerCase()) != -1 &&
                !tableName.equalsIgnoreCase(aliasTableName)) {
                condition = StringUtil.replace(condition, tableName, aliasTableName);
            }
        }
        return condition;
    }
    
    /**
     * Sets the property map.
     */
    public void setProperties(Map<String, String> properties) {
        if (properties == null || properties.size() == 0) return;
        this.properties = properties;
        mapping = properties.get(ActiveRecordConstants.key_mapping);
        targetModel = properties.get(ActiveRecordConstants.key_model);
    }
    
    /**
     * Returns a representation of the relation.
     * 
     * @return relation key string
     */
    public String getRelationKey() {
        return key;
    }
    
    /**
     * Sets relation key.
     * 
     * @param key relation key string
     */
    public void setRelationKey(String key) {
        this.key = key;
    }
    
    /**
     * Checks if the value of cascade property is none.
     * @return true if the value is none
     */
    public boolean allowCascadeNone() {
        return allowCascade(CASCADE_NONE);
    }
    
    /**
     * Checks if the value of cascade property is nullify.
     * @return true if the value is nullify
     */
    public boolean allowCascadeNullify() {
        return allowCascade(CASCADE_NULLIFY);
    }
    
    /**
     * Checks if the value of cascade property is delete.
     * @return true if the value is delete
     */
    public boolean allowCascadeDelete() {
        return allowCascade(CASCADE_DELETE);
    }
    
    /**
     * Checks if the value of cascade property is simply_delete.
     * @return true if the value is delete
     */
    public boolean allowCascadeSimplyDelete() {
        return allowCascade(CASCADE_SIMPLY_DELETE);
    }
    
    /**
     * Checks if a cascade type is allowed.
     * 
     * @param cascadeType a cascade type string constant
     * @return true if the cascade type is allowed.
     */
    public boolean allowCascade(String cascadeType) {
        String cascade = properties.get(ActiveRecordConstants.key_cascade);
        if (cascade == null) cascade = CASCADE_NONE;
        return (cascadeType.equalsIgnoreCase(cascade))?true:false;
    }
    
    public String toString() {
        String separator = "; ";
        StringBuilder sb = new StringBuilder();
        sb.append("ownerClass: " + ownerClass.getName()).append(separator);
        sb.append("relation type: " + type).append(separator);
        sb.append("associationId: " + associationId).append(separator);
        sb.append("mapping: " + mapping).append(separator);
        sb.append("properties: " + properties).append(separator);
        sb.append("targetModel: " + targetModel).append(separator);
        sb.append("targetClass: " + getTargetClass());
        return sb.toString();
    }
    
    public static final String BELONGS_TO_TYPE = "belongs_to";
    public static final String HAS_ONE_TYPE = "has_one";
    public static final String HAS_MANY_TYPE = "has_many";
    public static final String HAS_MANY_THROUGH_TYPE = "has_many_through";
    
    /**
     * Cascade key to indicate no cascade effect. This is the default case.
     */
    public static final String CASCADE_NONE = "none";//default
    
    /**
     * Cascade key to indicate the nullifying of the foreign key field in its 
     * child record(s). 
     */
    public static final String CASCADE_NULLIFY = "nullify";
    
    /**
     * Cascade key to indicate the delete of child record(s). This cascade 
     * type will trigger actions caused by the removal of the child record 
     * such as counter decrement in its parent record.
     */
    public static final String CASCADE_DELETE = "delete";
    
    /**
     * Simply delete the children without triggering any actions caused by 
     * the removal of the child record such as counter decrement in its parent 
     * record.
     */
    public static final String CASCADE_SIMPLY_DELETE = "simply_delete";
    
    /**
     * Specifies reverse relation name.
     */
    public static final String REVERSE_RELATION = "reverse";

    protected Class<? extends ActiveRecord> ownerClass;
    protected String type;
    protected String associationId;
    protected Class<? extends ActiveRecord> targetClass;
    protected String ownerModel;
    protected String targetModel;
    protected String mapping;
    protected Map<String, String> properties = new HashMap<String, String>();
    protected String key;
}
