/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Category class holds information about a category of entities. 
 * 
 * @author (Fei) John Chen
 * 
 */
public class Category {

    /**
     * Creates a Category instance.
     * 
     * @param center    center class of the category
     * @param category  category name
     * @param idField   id field name for the category
     * @param typeField type field name for the category
     */
    public Category(Class<? extends ActiveRecord> center, String category, String idField, String typeField) {
        this.center = center;
        this.category = category;
        this.idField = idField;
        this.typeField = typeField;
    }
    
    /**
     * Returns center class of the category.
     * 
     * @return center class of the category.
     */
    public Class<? extends ActiveRecord> getCenterClass() {
        return center;
    }
    
    /**
     * Adds type and its corresponding entity name to a map.
     * 
     * @param type
     * @param entity
     */
    public void addEntity(String type, String entity) {
        if (!typeEntity.containsKey(type)) {
            typeEntity.put(type, entity);
        }
    }
    
    /**
     * Returns entity.
     * 
     * @param type type name of an entity
     * @return entity
     */
    public String getEntityByType(String type) {
        return typeEntity.get(type);
    }
    
    /**
     * Returns all entity names.
     * 
     * @return set of entity names
     */
    public Set<String> getEntitys() {
        Set<String> entities = new HashSet<String>();
        Iterator<String> it = typeEntity.keySet().iterator();
        while(it.hasNext()) {
            entities.add(typeEntity.get(it.next()));
        }
        return entities;
    }
    
    /**
     * Checks if an entity is in the category.
     * 
     * @param entity the entity to check
     * @return true if the entity is in the category
     */
    public boolean isEntityInCategory(String entity) {
        return (getTypeByEntity(entity) != null)?true:false;
    }
    
    /**
     * Checks if an entity type is in the category.
     * 
     * @param type the entity type to check
     * @return true if the entity is in the category
     */
    public boolean isTypeInCategory(String type) {
        return (typeEntity.keySet().contains(type))?true:false;
    }
    
    /**
     * Returns type name for the entity.
     * 
     * @return type name for the entity
     */
    public String getTypeByEntity(String entity) {
        if (entity == null) return null;
        
        String type = null;
        for (Map.Entry<String, String> entry : typeEntity.entrySet()) {
            String key = entry.getKey();
            if (entity.equals(entry.getValue())) {
                type = key;
                break;
            }
        }
        return type;
    }
    
    /**
     * Returns all type names.
     * 
     * @return set of type names
     */
    public Set<String> getTypes() {
        return typeEntity.keySet();
    }
    
    /**
     * Returns category.
     * 
     * @return category
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Returns idField.
     * 
     * @return idField
     */
    public String getIdField() {
        return idField;
    }
    
    /**
     * Returns typeField.
     * 
     * @return typeField
     */
    public String getTypeField() {
        return typeField;
    }
    
    /**
     * Center class of the category. For example, "Tagging.class"
     */
    private Class<? extends ActiveRecord> center;
    
    /**
     * Name of a category. For example, "taggable".
     */
    private String category;
    
    /**
     * Id field name of a category. For example, "taggable_id".
     */
    private String idField;
    
    /**
     * Type field name of a category. For example, "taggable_type".
     */
    private String typeField;
    
    /**
     * Map of type and entity, key is type and value is corresponding 
     * entity name.
     */
    private Map<String, String> typeEntity = new HashMap<String, String>();
}
