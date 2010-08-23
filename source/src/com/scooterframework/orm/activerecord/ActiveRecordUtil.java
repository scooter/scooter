/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.exception.GenericException;
import com.scooterframework.common.exception.ObjectCreationException;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.Util;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;


/**
 * ActiveRecordUtil class has helper methods. 
 * 
 * @author (Fei) John Chen
 */
public class ActiveRecordUtil {
    
    /**
     * Checks if the two records are actually the same record. 
     * 
     * If the two records are the same, they are in the same table and 
     * their primary keys are the same.
     * 
     * @param r1 record 1
     * @param r2 record 2
     * @return true if the two records have the same primary key
     */
    public static boolean isSameRecord(ActiveRecord r1, ActiveRecord r2) {
        if (r1 == null || r2 == null || r1.isNewRecord() || r2.isNewRecord()) return false;
        
        if (!r1.getTableName().equalsIgnoreCase(r2.getTableName())) return false;
        
        boolean same = true;
        Map m1 = r1.getPrimaryKeyDataMap();
        Map m2 = r2.getPrimaryKeyDataMap();
        if (m1.size() == m2.size()) {
            Iterator it1 = m1.keySet().iterator();
            while(it1.hasNext()) {
                Object key = it1.next();
                Object value1 = m1.get(key);
                Object value2 = m2.get(key);
                if (value1 == null || value2 == null || 
                    !value1.toString().equalsIgnoreCase(value2.toString())) {
                    same = false;
                    break;
                }
            }
        }
        
        return same;
    }
    
    /**
     * Validates a record is of the expected type.
     * 
     * @param expected
     * @param record
     * @throws WrongRecordTypeException exception if unexpected.
     */
    public static void validateRecordType(Class expected, ActiveRecord record) {
        if (expected == null || record == null) return;
        
        // make sure the record type is valid
        String expectedTypeName = expected.getName();
        String inputType = record.getClass().getName();
        if (!inputType.equals(expectedTypeName)) {
            String message = "Expected " + expectedTypeName + ", not " + inputType + ".";
            WrongRecordTypeException wrtEx = new WrongRecordTypeException(message);
            wrtEx.setCorrectType(expectedTypeName);
            wrtEx.setWrongType(inputType);
            throw wrtEx;
        }
    }

    /**
     * Removes duplicated items from list one.
     * 
     * @param one   the original list
     * @param two   the list to be substracted
     * @return new list
     */
    public static List remains(List one, List two) {
        if (one == null) return one;
        if (two == null) return one;
        List tmp = new ArrayList();
        Iterator it1 = one.iterator();
        while(it1.hasNext()) {
            ActiveRecord r1 = (ActiveRecord)it1.next();
            String pk1 = r1.getPrimaryKeyDataMap().toString();
            boolean itemInListTwo = false;
            Iterator it2 = two.iterator();
            while(it2.hasNext()) {
                ActiveRecord r2 = (ActiveRecord)it2.next();
                String pk2 = r2.getPrimaryKeyDataMap().toString();
                if (pk1.equals(pk2)) {
                    itemInListTwo = true;
                    break;
                }
            }
            if (itemInListTwo) tmp.add(r1);
        }
        
        List nl = new ArrayList();
        Iterator it3 = one.iterator();
        while(it3.hasNext()) {
            ActiveRecord r1 = (ActiveRecord)it3.next();
            if (!tmp.contains(r1)) nl.add(r1);
        }
        return nl;
    }
    
    /**
     * Returns the full class name
     * 
     * @return String
     */
    public static String getFullClassName(Class c) {
        return (c == null)?"":c.getName();
    }
    
    /**
     * Returns model name of an active record class. 
     * 
     * Model name is actually the short class name of the model class in 
     * lowercase unless the class is ActiveRecord class itself. In that case, 
     * it is the slim table name of the class. Model name should always be in 
     * singularized form. 
     * 
     * @param modelClass    an ActiveRecord class type
     * @return model name
     */
    public static String getModelName(Class modelClass) {
        String model = "";
        String className = modelClass.getName();
        if (className.equals(ActiveRecord.class.getName())) {
            String tableName = ActiveRecordUtil.getSlimTableName(modelClass);
            if (tableName != null) {
                tableName = tableName.toLowerCase();
                if (DatabaseConfig.getInstance().usePluralTableName()) {
                    model = WordUtil.singularize(tableName);
                }
                else {
                    model = tableName;
                }
            }
            else {
                throw new IllegalArgumentException("Failed to get table name from class  \"" + className + "\".");
            }
        }
        else {
            model = WordUtil.underscore(Util.getShortClassName(modelClass));
        }
        if (model != null) model = model.toLowerCase();
        return model;
    }
    
    /**
     * Returns model name of an active record instance.
     * 
     * @param record    ActiveRecord instance
     * @return model name
     */
    public static String getModelName(ActiveRecord record) {
        String model = "";
        String className = record.getClass().getName();
        if (className.equals(ActiveRecord.class.getName())) {
            String tableName = record.getSlimTableName();
            if (tableName != null) {
                tableName = tableName.toLowerCase();
                if (DatabaseConfig.getInstance().usePluralTableName()) {
                    model = WordUtil.singularize(tableName);
                }
                else {
                    model = tableName;
                }
            }
            else {
                throw new IllegalArgumentException("Failed to get table name from record \"" + record + "\".");
            }
        }
        else {
            model = WordUtil.underscore(Util.getShortClassNameInLowerCase(record.getClass()));
        }
        return model;
    }
    
    /**
     * Returns model name for a table name. 
     * 
     * Model name should be a singularized form of a table name without any 
     * prefix or suffix element. 
     * 
     * @param tableName table name for the model
     * @return String
     */
    public static String getModelName(String tableName) {
        String slimTableName = DatabaseConfig.getInstance().getSlimTableName(tableName).toLowerCase();
        String model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(slimTableName):slimTableName;
        return model;
    }
    
    /**
     * Returns full table name of an active record class. A full table name 
     * includes table prefix and suffix elements. 
     * 
     * @param c an ActiveRecord class type
     * @return String
     */
    public static String getTableName(Class c) {
        return getHomeInstance(c).getTableName();
    }
    
    /**
     * Returns slim table name of an active record class. A slim table name 
     * is a name without any prefix or suffix element.
     * 
     * @param c an ActiveRecord class type
     * @return String
     */
    public static String getSlimTableName(Class c) {
        return getHomeInstance(c).getSlimTableName();
    }
    
    /**
     * <p>Generates an instance of ActiveRecord. <tt>model</tt> value is used 
     * to deduce the table name related to the ActiveRecord instance.</p>
     * 
     * @param className class name of the ActiveRecord instance to be created.
     * @param model    model name of the ActiveRecord class.
     * @return an ActiveRecord instance
     */
    public static ActiveRecord generateActiveRecordInstance(String className, String model) {
        ActiveRecord record = null;
        Class c = null;
        try {
            c = OrmObjectFactory.getInstance().loadClass(className);
            if ( c != null ) {
                String tableName = model;
                if (DatabaseConfig.getInstance().usePluralTableName()) {
                    tableName = WordUtil.pluralize(model);
                }
                
                tableName = DatabaseConfig.getInstance().getFullTableName(tableName);
                
                Class[] parameterTypes = {String.class};
                Object[] initargs = {tableName};
                record = (ActiveRecord)newInstance(c, parameterTypes, initargs);
            }
        } catch (Exception ex) {
            throw new ObjectCreationException(className, ex);
        }
        return record;
    }
    
    /**
     * Creates a new instance.
     * 
     * @param c class type of the new instance
     * @param parameterTypes constructor parameter types
     * @param initargs constructor parameter values
     * @return a new instance
     * @throws java.lang.Exception
     */
    public static Object newInstance(Class c, Class[] parameterTypes, Object[] initargs) 
    throws Exception {
        if (c == null) return null;
        
        Object o = null;
        try {
        	o = OrmObjectFactory.getInstance().newInstance(c.getName(), parameterTypes, initargs);
        }
        catch(Exception nsmEx) {
            o = OrmObjectFactory.getInstance().newInstance(c);
        }
        return o;
    }
    
    /**
     * Returns an ActiveRecord home instance. 
     * 
     * This method first tries to retrieve an ActiveRecord home instance of 
     * type <tt>fullModelClassName</tt>. If a null value is received, it then
     * generates the home instance. If this fails too, a default home instance 
     * of type <tt>defaultModelClassName</tt> will be created and returned.
     * 
     * <p>See description of 
     * {@link #getHomeInstance(java.lang.String)} method for more details.</p>
     * 
     * @param fullModelClassName    class name of the model
     * @param modelName             model name
     * @param defaultModelClassName default model class name
     * @return an ActiveRecord home instance
     */
    public static ActiveRecord getHomeInstance(String fullModelClassName, String modelName, String defaultModelClassName) {
        ActiveRecord home = null;
        try {
            home = getHomeInstance(fullModelClassName);
        }
        catch(Exception ex) {
            home = generateActiveRecordInstance(defaultModelClassName, modelName);
            setHomeInstance(home);
        }
        return home;
    }
    
    /**
     * <p>Returns a home instance of a class type.</p>
     * 
     * <p>A home instance of a record is a read-only instance for a model type. 
     * Its main function is to provide meta information of the model and some
     * finder methods.</p>
     * 
     * <p>In this method, the value of <tt>fullModelClassName</tt> cannot be 
     * {@link #DEFAULT_RECORD_CLASS}, because each home instance of a record
     * class type is cached for performance.</p>
     * 
     * <p>This method creates a home instance the first time it is called.</p>
     * 
     * @param fullModelClassName class type name of the model
     * @return a home instance of a model
     */
    public static ActiveRecord getHomeInstance(String fullModelClassName) {
        ActiveRecord record = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            String modelKey = getHomeInstanceKeyForCurrentThreadCache(fullModelClassName);
            record = (ActiveRecord)CurrentThreadCache.get(modelKey);
            if (record != null) return record;
        }
        
        Map homeMap = getHomeInstanceMap();
        record = (ActiveRecord)homeMap.get(fullModelClassName);
        if (record == null) {
            if (DEFAULT_RECORD_CLASS.equals(fullModelClassName)) {
                throw new IllegalArgumentException("Home instance for type " + fullModelClassName + " must be created first.");
            }
            record = (ActiveRecord)OrmObjectFactory.getInstance().newInstance(fullModelClassName);
            setHomeInstance(record);
        }
        return record;
    }
    
    /**
     * <p>Returns a home instance of a class type. See description of 
     * {@link #getHomeInstance(java.lang.String)} method for more details.</p>
     * 
     * @param clz   class of the model
     * @return a home instance of a model
     */
    public static ActiveRecord getHomeInstance(Class clz) {
        return getHomeInstance(clz.getName());
    }
    
    /**
     * Sets home instance.
     * 
     * @param record a home instance
     */
    public static void setHomeInstance(ActiveRecord record) {
        if (record != null) {
            record.freeze();
            record.setAsHomeInstance();
            
            //cleanup cached relations
            RelationManager.getInstance().removeRelationsFor(getModelName(record));
            
            setGateInstance(record.getClass().getName(), new TableGateway(record));
            
            if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
                String modelKey = getHomeInstanceKeyForCurrentThreadCache(record.getClass().getName());
                CurrentThreadCache.set(modelKey, record);
                return;
            }
            
            Map homeMap = getHomeInstanceMap();
            homeMap.put(getHomeInstanceKey(record), record);
        }
    }
    
    public static TableGateway getGateway(Class modelClass) {
    	return getGateway(modelClass.getName());
    }
    
    public static TableGateway getGateway(String fullModelClassName) {
    	TableGateway gate = null;
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            String gateKey = getGateInstanceKey(fullModelClassName);
            gate = (TableGateway)CurrentThreadCache.get(gateKey);
            if (gate != null) return gate;
        }
        
        Map gateMap = getGateInstanceMap();
        gate = (TableGateway)gateMap.get(fullModelClassName);
        if (gate == null) {
            if (DEFAULT_RECORD_CLASS.equals(fullModelClassName)) {
                throw new IllegalArgumentException("TableGateway instance for type " + fullModelClassName + " must be created first.");
            }
            ActiveRecord home = getHomeInstance(fullModelClassName);
            gate = new TableGateway(home);
            setGateInstance(fullModelClassName, gate);
        }
        
        return gate;
    }
    
    public static TableGateway getGateway(ActiveRecord home) {
    	TableGateway gate = null;
        String fullModelClassName = home.getClass().getName();
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            String gateKey = getGateInstanceKey(fullModelClassName);
            gate = (TableGateway)CurrentThreadCache.get(gateKey);
            if (gate != null) return gate;
        }
        
        Map gateMap = getGateInstanceMap();
        gate = (TableGateway)gateMap.get(fullModelClassName);
        if (gate == null) {
            if (DEFAULT_RECORD_CLASS.equals(fullModelClassName)) {
                throw new IllegalArgumentException("TableGateway instance for type " + fullModelClassName + " must be created first.");
            }
            gate = new TableGateway(home);
            setGateInstance(fullModelClassName, gate);
        }
        
        return gate;
    }
    
    /**
     * Sets gateway instance.
     * 
     * @param fullModelClassName model class name
     * @param gate a gateway instance
     */
    public static void setGateInstance(String fullModelClassName, TableGateway gate) {
        if (gate != null) {
            String gateKey = getGateInstanceKey(fullModelClassName);
            
            if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
                CurrentThreadCache.set(gateKey, gate);
                return;
            }
            
            Map gateMap = getGateInstanceMap();
            gateMap.put(gateKey, gate);
        }
    }
    
    /**
     * Returns a Calculator instance for the model class.
     * 
     * @param clz model class type
     * @return a Calculator instance for the model class
     */
    public static Calculator getCalculator(Class clz) {
    	return getCalculator(clz.getName());
    }
    
    /**
     * Returns a Calculator instance for the model class.
     * 
     * @param fullModelClassName
     * @return a Calculator instance for the model class
     */
    public static Calculator getCalculator(String fullModelClassName) {
    	Calculator cal = null;
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            String calKey = getCalculatorInstanceKey(fullModelClassName);
            cal = (Calculator)CurrentThreadCache.get(calKey);
            if (cal != null) return cal;
        }
        
        Map calMap = getCalculatorInstanceMap();
        cal = (Calculator)calMap.get(fullModelClassName);
        if (cal == null) {
            if (DEFAULT_RECORD_CLASS.equals(fullModelClassName)) {
                throw new IllegalArgumentException("Calculator instance for type " + fullModelClassName + " must be created first.");
            }
            ActiveRecord home = getHomeInstance(fullModelClassName);
            cal = new Calculator(home);
            setCalculatorInstance(fullModelClassName, cal);
        }
        
        return cal;
    }
    
    /**
     * Sets Calculator instance.
     * 
     * @param fullModelClassName model class name
     * @param cal a Calculator instance
     */
    public static void setCalculatorInstance(String fullModelClassName, Calculator cal) {
        if (cal != null) {
            String calKey = getCalculatorInstanceKey(fullModelClassName);
            
            if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
                CurrentThreadCache.set(calKey, cal);
                return;
            }
            
            Map calMap = getCalculatorInstanceMap();
            calMap.put(calKey, cal);
        }
    }
    
    
    /**
     * Checks if a field name exists in a database table. 
     * 
     * @param clazz         the class type of an ActiveRecord record
     * @param fieldName     a field name
     */
    public static void verifyExistenceOfColumn(Class clazz, String fieldName) {
        ActiveRecord record = getHomeInstance(clazz);
        if (!record.isColumnField(fieldName)) {
            throw new GenericException("Field [" + fieldName + "] is not a column of table " + record.getTableName() + ".");
        }
    }

    private static String getCalculatorInstanceKey(String modelClassName) {
        return "cal_" + modelClassName;
    }
    
    private static Map getCalculatorInstanceMap() {
        return calculatorInstanceMap;
    }
    
    private static String getHomeInstanceKey(ActiveRecord record) {
        //String uniqueKey = record.getClass().getName() + "-" + record.getConnectionName() + "-" + record.getTableName();
        return getHomeInstanceKeyForCurrentThreadCache(record.getClass().getName());
    }

    private static String getHomeInstanceKeyForCurrentThreadCache(String modelClassName) {
        return "model_" + modelClassName;
    }
    
    private static Map getHomeInstanceMap() {
        return homeInstanceMap;
    }

    private static String getGateInstanceKey(String modelClassName) {
        return "gate_" + modelClassName;
    }
    
    private static Map getGateInstanceMap() {
        return gateInstanceMap;
    }
    
    /**
     * calculatorInstanceMap stores Calculator instances.
     */
    private static Map calculatorInstanceMap = Collections.synchronizedMap(new HashMap());
    
    /**
     * gateInstanceMap stores TableGateway instances.
     */
    private static Map gateInstanceMap = Collections.synchronizedMap(new HashMap());
    
    /**
     * homeInstanceMap stores ActiveRecord home instances.
     */
    private static Map homeInstanceMap = Collections.synchronizedMap(new HashMap());
    
    public static final String DEFAULT_RECORD_CLASS = "com.scooterframework.orm.activerecord.ActiveRecord";
}
