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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;

/**
 * RelationManager class manages relations. 
 * 
 * @author (Fei) John Chen
 */
public class RelationManager {
    private static final RelationManager me = new RelationManager();

    private RelationManager() {
    }
    
    public static RelationManager getInstance() {
        return me;
    }
    
    /**
     * Loads relations for a class type.
     * 
     * @param recordClass class type
     */
    public void registerRelations(Class<? extends ActiveRecord> recordClass) {
        _registerRelations(recordClass);
    }
    
    /**
     * Sets up relation with other model(s). <tt>target</tt> parameter can be 
     * either the model name of the target or a descriptive string of the 
     * target. In the latter case, either the properties must contain key 
     * <tt>model</tt> to indicate the model name of the target or the 
     * <tt>targetClass</tt> input parameter is not null.
     * 
     * In a property string, each name-value pair is separated by ';' 
     * character, while within each name-value pair, name and value strings 
     * are separated by ':' character. 
     * 
     * For example, a property string like the following 
     * <blockquote><pre>
     *      conditions_sql: id in (1, 2, 3); include: category, user; 
     *      order_by: first_name, salary desc; cascade: delete
     * </pre></blockquote>
     * 
     * will be converted to a HashMap with the following entries:
     * <blockquote><pre>
     *      key             =>  value
     *      conditions_sql  =>  id in (1, 2, 3)
     *      include         =>  category, user
     *      order_by        =>  first_name, salary desc
     *      cascade         =>  delete
     * </pre></blockquote>
     * 
     * @param ownerClass    owner class
     * @param type          type of relation
     * @param associationId association name
     * @param targetClass   target class
     * @param properties    string of properties
     */
    public void setupRelation(Class<? extends ActiveRecord> ownerClass, String type, String associationId, 
                              Class<? extends ActiveRecord> targetClass, String properties) {
        if (ownerClass == null) 
            throw new IllegalArgumentException("Error in setupRelation: ownerClass is not specified.");
        
        if (type == null) 
            throw new IllegalArgumentException("Error in setupRelation: type is not specified.");
        
        if (associationId == null && targetClass == null) 
            throw new IllegalArgumentException("Error in setupRelation: either associationId or targetClass must be specified.");
        
        String targetModel = null;
        if (associationId == null) {
        	targetModel = ActiveRecordUtil.getModelName(targetClass);
        	associationId = (Relation.HAS_MANY_TYPE.equals(type))?WordUtil.pluralize(targetModel):targetModel;
        }
        
        String key = getRelationKey(ownerClass, associationId);
        if (relations.containsKey(key)) return;
        
        Map<String, String> pmap = Converters.convertSqlOptionStringToMap(properties);
        
        if (targetModel == null) {
	        if (pmap != null) {
	            targetModel = pmap.get(ActiveRecordConstants.key_model);
	        }
	        if (targetModel == null) {
	        	if (targetClass != null) {
	        		targetModel = ActiveRecordUtil.getModelName(targetClass);
	        	}
	        	else {
	        		targetModel = (Relation.HAS_MANY_TYPE.equals(type))?WordUtil.singularize(associationId):associationId;
	        	}
	        }
        }
        
    	if (targetClass == null) {
    		targetClass = ActiveRecordUtil.getHomeInstance(EnvConfig.getInstance().getModelClassName(targetModel)).getClass();
    	}
        
        String mapping = null;
        if (pmap != null) {
            mapping = pmap.get(ActiveRecordConstants.key_mapping);
        }
        if (mapping == null) {
            mapping = getDefaultMapping(ownerClass, type, associationId, targetClass);
        }
        
        Relation r = createRelation(ownerClass, type, associationId, targetModel);
        if (pmap != null) {
            validateCascade(pmap, type, key);
            r.setProperties(pmap);
        }
        r.setMapping(mapping);
        r.setTargetClass(targetClass);
        r.setRelationKey(key);
        cacheRelation(key, r);
        
        //register target class
        _registerRelations(targetClass);
    }
    
    /**
     * Sets up has-many-through relation. 
     * 
     * @param ownerClass         relation owner class
     * @param targets            target association name
     * @param throughAssociation the through association name
     * @param properties         string of properties
     * @param joinInputs         map of input key/value pairs for the join model
     */
    public void setupHasManyThroughRelation(Class<? extends ActiveRecord> ownerClass, 
    		String targets, String throughAssociation, String properties, Map<String, Object> joinInputs) {
        if (ownerClass == null) 
            throw new IllegalArgumentException("Error in setupHasManyThroughRelation: ownerClass is not specified.");
        
        if (targets == null) 
            throw new IllegalArgumentException("Error in setupHasManyThroughRelation: targets association is not specified.");
        
        if (throughAssociation == null) 
            throw new IllegalArgumentException("Error in setupHasManyThroughRelation: through association is not specified.");
        
        String key = getRelationKey(ownerClass, targets);
        if (relations.containsKey(key)) return;
        
        String acKey = getRelationKey(ownerClass, throughAssociation);
        Relation acRelation = (Relation)relations.get(acKey);
        if (acRelation == null) 
            throw new IllegalArgumentException("Error in setupHasManyThroughRelation: " + 
            throughAssociation + " association must be specified in class " + ownerClass + ".");
        
        Map<String, String> pmap = Converters.convertSqlOptionStringToMap(properties);
        Class<? extends ActiveRecord> middleC = acRelation.getTargetClass();
        _registerRelations(middleC);
        
        String source = pmap.get(ActiveRecordConstants.key_source);
        Relation cbRelation = null;
        if (source == null) {
	        String cbKey = getRelationKey(middleC, targets);
	        cbRelation = (Relation)relations.get(cbKey);
	        if (cbRelation == null) {
	            String target = WordUtil.singularize(targets);
	            cbKey = getRelationKey(middleC, target);
	            cbRelation = (Relation)relations.get(cbKey);
	            if (cbRelation == null) {
	                throw new IllegalArgumentException("Error in setupHasManyThroughRelation: " + 
	                targets + " or " + target + " association must be specified in class " + middleC + ".");
	            }
	        }
        }
        else {
        	String cbKey = getRelationKey(middleC, source);
	        cbRelation = (Relation)relations.get(cbKey);
            if (cbRelation == null) {
                throw new IllegalArgumentException("Error in setupHasManyThroughRelation: " + 
                source + " association must be specified in class " + middleC + ".");
            }
        }
        
        HasManyThroughRelation r = new HasManyThroughRelation(ownerClass, targets, throughAssociation, acRelation, cbRelation);
        if (pmap != null) r.setProperties(pmap);
        r.setJoinInputs(joinInputs);
        r.setRelationKey(key);
        cacheRelation(key, r);
    }
    
    /**
     * Creates a RecordRelation between owner (record instance) and its 
     * associated model.
     * 
     * @param record           ActiveRecord instance of the owner
     * @param associationId    association name for the target
     * @return RecordRelation a specific RecordRelation
     */
    public RecordRelation createRecordRelation(ActiveRecord record, String associationId) {
        if (record == null || associationId == null) return null;
        
        Relation relation = findOrRegisterRelation(record, associationId);
        if (relation == null) 
            throw new UndefinedRelationException(
            		ActiveRecordUtil.getModelName(record.getClass()), associationId);
        
        return createRecordRelation(record, relation);
    }
    
    /**
     * Return a list of name type combination for a model class. 
     * 
     * An item in the list is like: 
     *      order:invoice = has-one
     *      order:item = has-many
     * 
     * @param clz ActiveRecord class type
     * @return List
     */
    public List<String> getAllRelationNameTypes(Class<? extends ActiveRecord> clz) {
        _registerRelations(clz);
        
        String model = ActiveRecordUtil.getModelName(clz);
        String relationOwnerKey = getRelationOwnerKey(model);
        List<String> nameTypes = new ArrayList<String>();
        
    	for (Map.Entry<String, Relation> entry : relations.entrySet()) {
            String key = entry.getKey();
            if (key == null) continue;
            if (key.startsWith(relationOwnerKey)) {
                Relation r = entry.getValue();
                String rType = (r != null)?r.getRelationType():null;
                nameTypes.add(key + " = " + rType);
            }
        }
        
        return nameTypes;
    }
    
    /**
     * Returns a list of relation instances owned by an owner class type.
     * 
     * @param owner ActiveRecord class type
     * @return List of relation instances
     */
    public List<Relation> getOwnedRelations(Class<? extends ActiveRecord> owner) {
        _registerRelations(owner);
        
        String model = ActiveRecordUtil.getModelName(owner);
        String relationOwnerKey = getRelationOwnerKey(model);
        List<Relation> rls = new ArrayList<Relation>();

        for (Map.Entry<String, Relation> entry : relations.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(relationOwnerKey)) {
                rls.add(entry.getValue());
            }
        }
        return rls;
    }
    
    /**
     * Returns a list of relation instances owned by an owner class type 
     * with the specific target class type.
     * 
     * @param owner  owner ActiveRecord class type
     * @param target target ActiveRecord class type
     * @return List of relation instances
     */
    public List<Relation> getRelations(Class<? extends ActiveRecord> owner, Class<? extends ActiveRecord> target) {
        _registerRelations(owner);
        
        String model = ActiveRecordUtil.getModelName(owner);
        String relationOwnerKey = getRelationOwnerKey(model);
        List<Relation> rls = new ArrayList<Relation>();

    	for (Map.Entry<String, Relation> entry : relations.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(relationOwnerKey)) {
            	Relation r = (Relation)relations.get(key);
            	if (target.getName().equals(r.getTargetClass().getName())) {
            		rls.add(r);
            	}
            }
        }
        return rls;
    }
    
    /**
     * Removes all cached relations owned by a model.
     * 
     * @param model the owner of the relation
     */
    public void removeRelationsFor(String model) {
        String relationOwnerKey = getRelationOwnerKey(model);

    	for (Map.Entry<String, Relation> entry : relations.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(relationOwnerKey)) {
            	relations.remove(key);
            }
        }
    }
    
    /**
     * Returns relation between owner and target.
     * 
     * @param owner         relation owner class
     * @param associationId association id for target model in lower case
     * @return relation
     */
    public Relation getRelation(Class<? extends ActiveRecord> owner, String associationId) {
        _registerRelations(owner);
        return (Relation)relations.get(getRelationKey(owner, associationId));
    }
    
    /**
     * Returns relation type from owner class to target class.
     * 
     * @param owner  class type for relation owner
     * @param target class type for relation target
     * @return relation type
     */
    public String getRelationType(Class<? extends ActiveRecord> owner, Class<? extends ActiveRecord> target) {
    	List<Relation> list = getRelations(owner, target);
    	if (list == null) return null;
    	
    	String type = null;
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = it.next();
    		if (r != null) {
	    		type = r.getRelationType();
	    		break;
    		}
    	}
    	return type;
    }
    
    /**
     * Checks if there is a belongs-to relation between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return true if endA class belongs-to endB class
     */
    public boolean existsBelongsToRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return false;
    	
    	boolean status = false;
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (Relation.BELONGS_TO_TYPE.equals(r.getRelationType())) {
    			status = true;
    			break;
            }
    	}
    	return status;
    }
    
    /**
     * Checks if there is a has-one relation between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return true if endA class has-one endB class
     */
    public boolean existsHasOneRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return false;
    	
    	boolean status = false;
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_ONE_TYPE.equals(r.getRelationType())) {
    			status = true;
    			break;
            }
    	}
    	return status;
    }
    
    /**
     * Checks if there is a has-many relation between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return true if endA class has-many endB class
     */
    public boolean existsHasManyRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return false;
    	
    	boolean status = false;
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_MANY_TYPE.equals(r.getRelationType())) {
    			status = true;
    			break;
            }
    	}
    	return status;
    }
    
    /**
     * Checks if there is a has-many-through relation between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return true if endA class has-many-through endB class
     */
    public boolean existsHasManyThroughRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return false;
    	
    	boolean status = false;
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_MANY_THROUGH_TYPE.equals(r.getRelationType())) {
    			status = true;
    			break;
            }
    	}
    	return status;
    }
    
    /**
     * Returns a belongs-to relation which exists between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return a belongs-to relation
     */
    public Relation getBelongsToRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	Relation rel = null;
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return null;
    	
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.BELONGS_TO_TYPE.equals(r.getRelationType())) {
    			rel = r;
    			break;
            }
    	}
    	return rel;
    }
    
    /**
     * Returns a has-one relation which exists between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return a has-one relation
     */
    public Relation getHasOneRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	Relation rel = null;
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return null;
    	
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_ONE_TYPE.equals(r.getRelationType())) {
    			rel = r;
    			break;
            }
    	}
    	return rel;
    }
    
    /**
     * Returns a has-many relation which exists between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return a has-many relation
     */
    public Relation getHasManyRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	Relation rel = null;
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return null;
    	
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_MANY_TYPE.equals(r.getRelationType())) {
    			rel = r;
    			break;
            }
    	}
    	return rel;
    }
    
    /**
     * Returns a has-many-through relation which exists between the two class types.
     * 
     * @param endA class type for owner class
     * @param endB class type for target class
     * @return a has-many-through relation
     */
    public Relation getHasManyThroughRelationBetween(Class<? extends ActiveRecord> endA, Class<? extends ActiveRecord> endB) {
    	Relation rel = null;
    	List<Relation> list = getRelations(endA, endB);
    	if (list == null) return null;
    	
    	Iterator<Relation> it = list.iterator();
    	while(it.hasNext()) {
    		Relation r = (Relation)it.next();
    		if (r != null && Relation.HAS_MANY_THROUGH_TYPE.equals(r.getRelationType())) {
    			rel = r;
    			break;
            }
    	}
    	return rel;
    }
    
    
    
    
    
    /**
     * Registers a Category.
     * 
     * @param center    center class of the category
     * @param category  category name
     * @param idField   id field name for the category
     * @param typeField type field name for the category
     */
    public void registerCategory(Class<? extends ActiveRecord> center, String category, String idField, String typeField) {
        Category cat = (Category)categoryMap.get(category);
        if (cat == null) {
            cat = new Category(center, category, idField, typeField);
            categoryMap.put(category, cat);
        }
    }
    
    /**
     * Gets a category list that all have the same center class.
     * 
     * @param center    center class of the category
     * @return a category list
     */
    public List<Category> getRegisteredCategory(Class<? extends ActiveRecord> center) {
        String centerClassName = center.getName();
        List<Category> categories = new ArrayList<Category>();
        
        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            Category category = entry.getValue();
            if (category != null && centerClassName.equals(category.getCenterClass().getName())) {
                categories.add(category);
            }
        }
        return categories;
    }
    
    /**
     * Returns a declared Category instance. If the category is not declared, 
     * null will be returned.
     * 
     * @param category  category name
     * @return category a Category instance if there is one
     */
    public Category getCategory(String category) {
        return (Category)categoryMap.get(category);
    }
    
    /**
     * Returns model name of a type in a category.
     * 
     * @param category  category name
     * @param type type name of the model
     * @return model model name for the type
     */
    public String getCategoryEntity(String category, String type) {
        Category cat = getCategory(category);
        if (cat == null) return null;
        return cat.getEntityByType(type);
    }
    
    /**
     * returns default FK mapping.
     * 
     * The default FK mapping follows these rules:
     * 
     * For belongsTo relation, class A is the owner of the relation and 
     * class A holds the foreign key FK:
     * <pre>
     * 1. If id is not class B's primary key, the mapping is 
     *    <tt>{Class B's primary key}={Class B's primary key}</tt>. For 
     *    example, if class B's PK is order_id, then the mapping is 
     *    order_id=order_id. Here we assume that <tt>order_id</tt> is a 
     *    foreign key column of Class A.
     * 2. If id is class B's primary key, the mapping is 
     *    {Class B's model name in lower case}_id=id
     *    
     *    Example: Lines belongsTo Order. The default mapping is "order_id=id".
     * </pre>
     * 
     * For hasMany or hasOne relation, class A is the owner of the relation and 
     * class B holds the FK:
     * <pre>
     * 1. If id is not class A's primary key, the mapping is 
     *    <tt>{Class A's primary key}={Class A's primary key}</tt>. For 
     *    example, if class A's PK is order_id, then the mapping is 
     *    order_id=order_id. Here we assume that <tt>order_id</tt> is a 
     *    foreign key column of Class B.
     * 2. If id is class A's primary key, the mapping is 
     *    id={Class A's model name in lower case}_id
     *    
     *    Example: Order hasMany Lines. The default mapping is "id=order_id".
     * </pre>
     * 
     * @param a             The end a class
     * @param type          String of type of relation
     * @param target        target name of the associated class.
     * @param b             The end b class
     */
    public String getDefaultMapping(Class<? extends ActiveRecord> a, String type, String target, Class<? extends ActiveRecord> b) {
        String mapping = "";
        
        // In belongs-to relation, class A holds FK and is also the owner.
        if (Relation.BELONGS_TO_TYPE.equalsIgnoreCase(type)) {
            ActiveRecord targetHome = (ActiveRecord)ActiveRecordUtil.getHomeInstance(b);
            RowInfo ri = targetHome.getRowInfo();
            if ( ri == null) {
                throw new RelationException("The RowInfo for class " + b.getName() + " cannot be null.");
            }
            
            String[] pkNames = ri.getPrimaryKeyColumnNames();
            if (StringUtil.isStringInArray("ID", pkNames, true)) {
                String fk = target + "_id";
                verifyExistenceOfColumn(a, fk);
                mapping = fk + "=id";
            }
            else {
                int size = pkNames.length;
                for (int i=0; i<size-1; i++) {
                    String fk = pkNames[i];
                    verifyExistenceOfColumn(a, fk);
                    mapping += fk + "=" + fk + ",";
                }
                String fk = pkNames[size-1];
                verifyExistenceOfColumn(a, fk);
                mapping += fk + "=" + fk;
            }
        }
        else 
        // In has-one and has-many relation, class B holds FK.
        if (Relation.HAS_ONE_TYPE.equalsIgnoreCase(type) || 
            Relation.HAS_MANY_TYPE.equalsIgnoreCase(type)) {
            ActiveRecord ownerHome = (ActiveRecord)ActiveRecordUtil.getHomeInstance(a);
            RowInfo ri = ownerHome.getRowInfo();
            if ( ri == null) {
                throw new RelationException("The RowInfo for class " + a.getName() + " cannot be null.");
            }
            
            String[] pkNames = ri.getPrimaryKeyColumnNames();
            if (StringUtil.isStringInArray("ID", pkNames, true)) {
                String fk = ActiveRecordUtil.getModelName(a) + "_id";
                verifyExistenceOfColumn(b, fk);
                mapping = "id=" + fk;
            }
            else {
                int size = pkNames.length;
                for (int i=0; i<size-1; i++) {
                    String fk = pkNames[i];
                    verifyExistenceOfColumn(b, fk);
                    mapping += fk + "=" + fk + ",";
                }
                String fk = pkNames[size-1];
                verifyExistenceOfColumn(b, fk);
                mapping += fk + "=" + fk;
            }
        }
        return mapping;
    }
    
    private void verifyExistenceOfColumn(Class<? extends ActiveRecord> clz, String columnName) {
        try {
            ActiveRecordUtil.verifyExistenceOfColumn(clz, columnName);
        }
        catch(Exception ex) {
            throw new RelationException("Failed to create default relation " +
            "mapping because " + ex.getMessage() + ". You might have to " + 
            "specify mapping explicitly.");
        }
    }
    
    private Relation createRelation(Class<? extends ActiveRecord> a, String type, String associationId, String targetModel) {
        Relation r = null;
        
        if (Relation.BELONGS_TO_TYPE.equalsIgnoreCase(type)) {
            r = new BelongsToRelation(a, associationId, targetModel);
        }
        else if (Relation.HAS_ONE_TYPE.equalsIgnoreCase(type)) {
            r = new HasOneRelation(a, associationId, targetModel);
        }
        else if (Relation.HAS_MANY_TYPE.equalsIgnoreCase(type)) {
            r = new HasManyRelation(a, associationId, targetModel);
        } 
        else {
            throw new UnsupportedRelationTypeException(type);
        }
        return r;
    }
    
    private Relation findOrRegisterRelation(ActiveRecord record, String associationId) {
        String key = getRelationKey(record.getClass(), associationId);
        Relation r = (Relation)relations.get(key);
        
        //register relation
        if (r == null) {
            registerRelations(record.getClass());
            r = (Relation)relations.get(key);
        }
        return r;
    }
    
    /**
     * Check if a class has been set up relations.
     */
    private boolean hasCompletedRelationSetup(String className) {
        return completedClasses.contains(className);
    }
    
    private void completeRegistration(String className) {
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) return;
        completedClasses.add(className);
    }

    private void _registerRelations(Class<? extends ActiveRecord> clz) {
        String fullClassName = clz.getName();
        if (!hasCompletedRelationSetup(fullClassName)) {
            ActiveRecord home = ActiveRecordUtil.getHomeInstance(fullClassName);
            home.registerRelations();
            completeRegistration(fullClassName);
        }
    }

    private RecordRelation createRecordRelation(ActiveRecord record, Relation relation) {
        RecordRelation rr = null;
        String type = relation.getRelationType();
        
        if (Relation.BELONGS_TO_TYPE.equalsIgnoreCase(type)) {
            rr = new BelongsToRecordRelation(record, (BelongsToRelation)relation);
        }
        else if (Relation.HAS_ONE_TYPE.equalsIgnoreCase(type)) {
            rr = new HasOneRecordRelation(record, (HasOneRelation)relation);
        }
        else if (Relation.HAS_MANY_TYPE.equalsIgnoreCase(type)) {
            rr = new HasManyRecordRelation(record, (HasManyRelation)relation);
        }
        else if (Relation.HAS_MANY_THROUGH_TYPE.equalsIgnoreCase(type)) {
            rr = new HasManyThroughRecordRelation(record, (HasManyThroughRelation)relation);
        }
        else {
            throw new UnsupportedRelationTypeException(type);
        }
        
        return rr;
    }
    
    /**
     * Returns formated relation key.
     * @param owner         relation owner class
     * @param associationId association id for target model in lower case
     * @return relation key
     */
    private String getRelationKey(Class<? extends ActiveRecord> owner, String associationId) {
        return getRelationKey(ActiveRecordUtil.getModelName(owner), associationId);
    }
    
    /**
     * Returns formated relation key. The relation key uses the following format:<br/> 
     * <tt>{owner}:{associationId}</tt> where <tt>{owner}</tt> is model name of owner 
     * class and <tt>{associationId}</tt> is association id for target class. 
     * 
     * <pre>
     * Examples:
     * Class A    Relationship    Class B    Relation Key
     * -------    ------------    -------    -------------
     * item       belongs-to      order      item:order
     * order      has-many        item       order:items
     * </pre>
     * 
     * @param owner         model name for owner class in lower case
     * @param associationId association id for target model in lower case
     * @return relation key
     */
    private String getRelationKey(String owner, String associationId) {
        return getRelationOwnerKey(owner) + associationId.toLowerCase();
    }
    
    private static String getRelationOwnerKey(String a) {
        return (a + ":").toLowerCase();
    }
    
    private void validateCascade(Map<String, String> properties, String rtype, String relationKey) {
        if (properties == null) return;
        
        String cascade = properties.get(ActiveRecordConstants.key_cascade);
        cascade = (cascade == null)?Relation.CASCADE_NONE:cascade;
        
        if (Relation.BELONGS_TO_TYPE.equals(rtype) && !Relation.CASCADE_NONE.equals(cascade)) {
            throw new IllegalArgumentException("The cascade is not allowed for " + rtype + " type in relation " + relationKey + ".");
        }
        
        if (!Relation.CASCADE_NONE.equals(cascade) &&
            !Relation.CASCADE_DELETE.equals(cascade) &&
            !Relation.CASCADE_SIMPLY_DELETE.equals(cascade) &&
            !Relation.CASCADE_NULLIFY.equals(cascade)
           ) {
            throw new IllegalArgumentException("The cascade attribute is not supported: [" + cascade + "] in relation " + relationKey + ".");
        }
    }
    
    private void cacheRelation(String key, Relation relation) {
        relations.put(key, relation);
    }

    /**
     * Map of relations
     * 
     * The key in the map is a combination of class a name and class b name. 
     * Value is a relation object. 
     * 
     * See {@link #getRelationKey(String a, String b)} method.
     */
    private Map<String, Relation> relations = new ConcurrentHashMap<String, Relation>();
    
    //List of setup classes. Each entry in the list is a full class name.
    private List<String> completedClasses = new ArrayList<String>();
    
    /**
     * Map of category name and corresponding category instance, key is 
     * category name and value is the Category instance.
     */
    private Map<String, Category> categoryMap = new ConcurrentHashMap<String, Category>();
}
