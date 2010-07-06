/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.scooterframework.common.util.Converters;

/**
 * Helper class has helper methods for ActiveRecord. 
 * 
 * @author (Fei) John Chen
 */
public class AssociationHelper {
    /**
     * This method adds a bunch of methods in many classes.
     * <ol>
     * <li> A has-many-through association from owner to each target class.</li>
     * <li> A has-many association from each target to through class.</li>
     * <li> A has-many-through association from each target to owner class.</li>
     * <li> A belongs-to association from through to each target class.</li>
     * </ol>
     * 
     * In order to establish the associations, the method assumes the following:
     * <ol>
     * <li> The type value of the category type column is the model name of 
     *      each corresponding target class.</li>
     * <li> The primary key of each target class is "id".</li>
     * <li> The mapping string between each target class and through class is 
     *      "id= category's id column".</li>
     * <li> The association property from each target to through contains "cascade: delete".</li>
     * </ol>
     * 
     * <p>
     * If any of the above assumptions are not satisfied, you need to use the
     * other <tt>hasManyInCategoryThrough </tt> method which gives you more 
     * control on specifying the associations.
     * </p>
     * 
     * <p>Example usage: </p>
     * <p>Assuming there are image files and text files in a folder. We create 
     * three models: images, texts, folders. We also use linkings model to 
     * link folders with images and texts files. We will create the following 
     * classes:</p>
     * 
     * <pre>
     *  CREATE TABLE linkings (
     *      id INTEGER AUTO_INCREMENT,
     *      folder_id INTEGER,
     *      linkable_id INTEGER,
     *      linkable_type VARCHAR(20),
     *      PRIMARY KEY(id)
     *  )
     *  
     *  class Linking extends ActiveRecord {
     *      public void registerRelations() {
     *          belongsTo(Folder.class);
     *          belongsToCategory("linkable");
     *      }
     *  }
     *  
     *  class Folder extends ActiveRecord {
     *      public void registerRelations() {
     *          hasMany(Linking.class);
     *          AssociationHelper.hasManyInCategoryThrough(Folder.class, 
     *                                  new Class[]{Image.class, Text.class}, 
     *                                  "linkable", Linking.class);
     *      }
     *  }
     *  
     *  class Image extends ActiveRecord {
     *  }
     *  
     *  class Text extends ActiveRecord {
     *  }
     * </pre>
     * 
     * The following codes show how to get total of ownership for a customer:
     * <pre>
     *      //Find all ownerships of a customer:
     *      ActiveRecord customerHome = ActiveRecordUtil.getHomeInstance(Customer.class);
     *      ActiveRecord customer = customerHome.find("id=1");
     *      int total = customer.allAssociatedInCategory("ownerable").size();
     * </pre>
     * 
     * It is also easy to add a dvd to the ownership of the customer:
     * <pre>
     *      Assign a dvd to a customer:
     *      ActiveRecord dvdHome = ActiveRecordUtil.getHomeInstance(Dvd.class);
     *      ActiveRecord dvd = dvdHome.find("id=4");
     *      List dvds = customer.allAssociatedInCategory("ownerable").add(dvd).getRecords();
     * </pre>
     * 
     * @param owner         owner class
     * @param targets       array of target classes
     * @param category      the category which the targets act as
     * @param through       the middle join class between owner and targets
     */
    public static void hasManyInCategoryThrough(Class owner, Class[] targets, 
                                            String category, Class through) {
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Target array cannot be empty.");
        }
        
        //make sure category center is loaded first
        RelationManager.getInstance().registerRelations(through);
        
        Category categoryInstance = RelationManager.getInstance().getCategory(category);
        if (categoryInstance == null) {
            throw new UnregisteredCategoryException(category);
        }
        String idField = categoryInstance.getIdField();
        String typeField = categoryInstance.getTypeField();
        String cTableName = ActiveRecordUtil.getTableName(through);
        
        int targetTotal = targets.length;
        String[] abProperties = new String[targetTotal];
        String[] types = new String[targetTotal];
        String relationType = Relation.HAS_MANY_TYPE;
        String[] bcProperties = new String[targetTotal];
        Map[] joinInputs = new HashMap[targetTotal];
        String[] cbProperties = new String[targetTotal];
        String cbMapping = ActiveRecordConstants.key_mapping + ": " + idField + "=id; ";
        
        for (int i=0; i<targetTotal; i++) {
            types[i] = ActiveRecordUtil.getModelName(targets[i]);
            String throughTypeCondition = ActiveRecordConstants.key_conditions_sql + ": " + cTableName + "." + typeField + "='" + types[i] + "'";
            abProperties[i] = throughTypeCondition;
            bcProperties[i] = ActiveRecordConstants.key_mapping + ": id=" + idField + "; " + 
                              throughTypeCondition + "; " + ActiveRecordConstants.key_cascade + ": delete";
            Map inputs = new HashMap();
            inputs.put(typeField, types[i]);
            joinInputs[i] = inputs;
            cbProperties[i] = cbMapping;
        }
        
        //baProperties are null.
        hasManyInCategoryThrough(owner, targets, category, through, joinInputs, 
                                 abProperties, types, relationType, 
                                 bcProperties, joinInputs, null, null);
    }
    
    /**
     * This method adds a bunch of methods in many classes.
     * <pre>
     * <li> A has-many-through association from owner to each target class.</li>
     * <li> A has-many association from each target to through class.</li>
     * <li> A has-many-through association from each target to owner class.</li>
     * <li> A belongs-to association from through to each target class.</li>
     * </pre>
     * 
     * Assuming owner class is A, target class is B, through class is C, 
     * <pre>
     * <tt>abProperties</tt> is join properties from A to B, 
     * <tt>bcProperties</tt> is join properties from B to C, 
     * <tt>cbProperties</tt> is join properties from C to B, 
     * <tt>baProperties</tt> is join properties from B to A.
     * </pre>
     * 
     * @param owner         owner class
     * @param targets       array of target classes
     * @param category      the category which the targets act as
     * @param through       the middle join class between owner and targets
     * @param acJoinInputs array of data map for the join through table.
     * @param abProperties  properties from owner to target class
     * @param types         array of join types in the category, default to model name
     * @param relationType  either has-many or has-one
     * @param bcProperties  array of properties from each target to through class
     * @param bcJoinInputs array of data map for the join through table.
     * @param cbProperties  array of properties from through to each target class
     * @param baProperties  array of properties from each target to owner class
     */
    public static void hasManyInCategoryThrough(Class owner, Class[] targets, 
                 String category, Class through, Map[] acJoinInputs, 
                 String[] abProperties, String[] types, String relationType, 
                 String[] bcProperties, Map[] bcJoinInputs, String[] cbProperties, 
                 String[] baProperties) {
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Target array cannot be empty.");
        }
        
        //make sure category center is loaded first
        RelationManager.getInstance().registerRelations(through);
        
        Category categoryInstance = RelationManager.getInstance().getCategory(category);
        if (categoryInstance == null) {
            throw new UnregisteredCategoryException(category);
        }
        String idField = categoryInstance.getIdField();
        String typeField = categoryInstance.getTypeField();
        String cTableName = ActiveRecordUtil.getTableName(through);
        
        ActiveRecord ownerHome = ActiveRecordUtil.getHomeInstance(owner);
        
        //prepare
        int targetTotal = targets.length;
        if (abProperties == null) abProperties = new String[targetTotal];
        if (bcProperties == null) bcProperties = new String[targetTotal];
        if (cbProperties == null) cbProperties = new String[targetTotal];
        if (baProperties == null) baProperties = new String[targetTotal];
        
        String cbMappingProperty = ActiveRecordConstants.key_mapping + ": " + idField + "=id; ";
        
        //#1, #2, #4, #3
        for (int i=0; i<targetTotal; i++) {
            Class target = targets[i];
            String targetEntityName = ActiveRecordUtil.getModelName(targets[i]);
            
            String type = "";
            if (types != null) type = types[i];
            if (type == null) type = targetEntityName;
            String throughTypeCondition = ActiveRecordConstants.key_conditions_sql + ": " + cTableName + "." + typeField + "='" + type + "'";
            
            String abProperty = abProperties[i];
            if (abProperty == null) {
                abProperty = throughTypeCondition;
            }
            else {
                if (abProperty.indexOf(ActiveRecordConstants.key_conditions_sql) == -1) {
                    abProperty = throughTypeCondition + "; " + abProperty;
                }
            }
            
            String bcProperty = bcProperties[i];
            if (bcProperty == null) {
                bcProperty = ActiveRecordConstants.key_mapping + ": id=" + idField + "; " + 
                             throughTypeCondition + "; cascade: delete";
            }
            else {
                if (bcProperty.indexOf(ActiveRecordConstants.key_conditions_sql) == -1) {
                    bcProperty = throughTypeCondition + "; " + bcProperty;
                }
                
                if (bcProperty.indexOf(ActiveRecordConstants.key_mapping) == -1) {
                    bcProperty = ActiveRecordConstants.key_mapping + ": id=" + idField + "; " + bcProperty;
                }
                
                if (bcProperty.indexOf(ActiveRecordConstants.key_cascade) == -1) {
                    bcProperty = ActiveRecordConstants.key_cascade + ": delete" + "; " + bcProperty;
                }
            }
            
            Map acJoinInputsMap = acJoinInputs[i];
            if (acJoinInputsMap == null) {
                acJoinInputsMap = new HashMap();
            }
            if (acJoinInputsMap.size() == 0) {
                acJoinInputsMap.put(typeField, type);
            }
            
            String cbProperty = cbProperties[i];
            if (cbProperty == null) {
                cbProperty = cbMappingProperty;
            }
            else {
                if (cbProperty.indexOf(ActiveRecordConstants.key_mapping) == -1) {
                    cbProperty = cbMappingProperty + "; " + cbProperty;
                }
            }
            
            //#2. A has-many association from each target to through class.
            //#4. A belongs-to association from through to each target class.
            ActiveRecord targetHome = ActiveRecordUtil.getHomeInstance(target);
            targetHome.actAsInCategory(type, category, 
                    relationType, through, bcProperty, cbProperty);
            
            //#1. A has-many-through association from owner to each target class.
            //Note: need to add a has-many relation between owner and through 
            //      as this is a prerequisit for setting up a has-many-through relation.
            if (RelationManager.getInstance().existsHasManyRelationBetween(owner, through)) {
                ownerHome.hasMany(through);
            }
            ownerHome.hasManyThrough(target, through, abProperty, acJoinInputsMap);
            
            Map bcJoinInputsMap = bcJoinInputs[i];
            if (bcJoinInputsMap == null) { 
                bcJoinInputsMap = new HashMap();
            }
            if (bcJoinInputsMap.size() == 0) {
                bcJoinInputsMap.put(typeField, type);
            }
            
            //#3. A has-many-through association from each target to owner class.
            //Note: need to add a belongs-to relation between through and owner 
            //      as this is a prerequisit for setting up a has-many-through relation.
            if (RelationManager.getInstance().existsBelongsToRelationBetween(through, owner)) {
                ActiveRecord throughHome = ActiveRecordUtil.getHomeInstance(through);
                throughHome.belongsTo(owner);
            }
            targetHome.hasManyThrough(owner, through, baProperties[i], bcJoinInputsMap);
        }
    }
    
    /**
     * Populates foreign key value in a belongs-to relation. In a belongs-to 
     * relation, the <tt>owner</tt> record should hold the foreign key value. 
     * Therefore, the foreign-key fields in the <tt>owner</tt> record is going 
     * to be set with data of the corresponding fields from the <tt>target</tt> 
     * record.
     * 
     * @param owner       the owner record of the relation
     * @param mappingMap  relation mapping from owner to target
     * @param target      the target record in the relation
     */
    public static void populateFKInBelongsTo(ActiveRecord owner, Map mappingMap, ActiveRecord target) {
        if (owner == null || target == null) return;
        
        Iterator it = mappingMap.keySet().iterator();
        while(it.hasNext()) {
            String leftKey = (String)it.next();
            String rightValue = (String)mappingMap.get(leftKey);
            owner.setData(leftKey, target.getField(rightValue));
        }
    }
    
    /**
     * Populates foreign key value in a has-many relation. In a has-many 
     * relation, the <tt>target</tt> record should hold the foreign key value. 
     * Therefore, the foreign-key fields in the <tt>target</tt> record is going 
     * to be set with data of the corresponding fields from the <tt>owner</tt> 
     * record.
     * 
     * @param owner       the owner record of the relation
     * @param mappingMap  relation mapping from owner to target
     * @param target      the target record in the relation
     */
    public static void populateFKInHasMany(ActiveRecord owner, Map mappingMap, ActiveRecord target) {
        populateFKInBelongsTo(target, Converters.reverseMap(mappingMap), owner);
    }
    
    /**
     * Populates foreign key value in a has-one relation. In a has-one 
     * relation, the <tt>target</tt> record should hold the foreign key value. 
     * Therefore, the foreign-key fields in the <tt>target</tt> record is going 
     * to be set with data of the corresponding fields from the <tt>owner</tt> 
     * record.
     * 
     * @param owner       the owner record of the relation
     * @param mappingMap  relation mapping from owner to target
     * @param target      the target record in the relation
     */
    public static void populateFKInHasOne(ActiveRecord owner, Map mappingMap, ActiveRecord target) {
        populateFKInBelongsTo(target, Converters.reverseMap(mappingMap), owner);
    }
}
