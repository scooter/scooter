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
import com.scooterframework.common.exception.GenericException;
import com.scooterframework.common.exception.ObjectCreationException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
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
	
	private static LogUtil log = LogUtil.getLogger(ActiveRecordUtil.class.getName());
    
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
        Map<String, Object> m1 = r1.getPrimaryKeyDataMap();
        Map<String, Object> m2 = r2.getPrimaryKeyDataMap();
        if (m1.size() == m2.size()) {
            for (Map.Entry<String, Object> entry : m1.entrySet()) {
                String key = entry.getKey();
                Object value1 = entry.getValue();
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
    public static void validateRecordType(Class<? extends ActiveRecord> expected, ActiveRecord record) {
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
     * @param two   the list to be subtracted
     * @return new list
     */
    public static List<? extends ActiveRecord> remains(List<? extends ActiveRecord> one, List<? extends ActiveRecord> two) {
        if (one == null) return one;
        if (two == null) return one;
        List<ActiveRecord> tmp = new ArrayList<ActiveRecord>();
        Iterator<? extends ActiveRecord> it1 = one.iterator();
        while(it1.hasNext()) {
            ActiveRecord r1 = (ActiveRecord)it1.next();
            String pk1 = r1.getPrimaryKeyDataMap().toString();
            boolean itemInListTwo = false;
            Iterator<? extends ActiveRecord> it2 = two.iterator();
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
        
        List<ActiveRecord> nl = new ArrayList<ActiveRecord>();
        Iterator<? extends ActiveRecord> it3 = one.iterator();
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
    public static String getFullClassName(Class<? extends ActiveRecord> c) {
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
    public static String getModelName(Class<? extends ActiveRecord> modelClass) {
        String model = "";
        String className = modelClass.getName();
        if (className.equals(ActiveRecord.class.getName())) {
            String tableName = ActiveRecordUtil.getSimpleTableName(modelClass);
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
            String tableName = record.getSimpleTableName();
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
        String slimTableName = DatabaseConfig.getInstance().getSimpleTableName(tableName).toLowerCase();
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
    public static String getTableName(Class<? extends ActiveRecord> c) {
        return getHomeInstance(c).getTableName();
    }
    
    /**
     * Returns the simple table name of an active record class. A simple table 
     * name is a name without any prefix or suffix element.
     * 
     * @param c an ActiveRecord class type
     * @return String
     */
    public static String getSimpleTableName(Class<? extends ActiveRecord> c) {
        return getHomeInstance(c).getSimpleTableName();
    }
    
    /**
     * <p>Generates an instance of ActiveRecord. <tt>model</tt> value is used 
     * to deduce the table name related to the ActiveRecord instance. The 
     * meta data of the <tt>model</tt> is obtained from the default database
     * connection name as specified by the <tt>database.properties</tt> file 
     * or from the {@link com.scooterframework.orm.activerecord.ActiveRecord#getConnectionName() getConnectionName()} method of the underlying model class.</p>
     * 
     * @param className  class name of the ActiveRecord instance to be created.
     * @param model      model name of the ActiveRecord class.
     * @return an ActiveRecord instance
     */
    public static ActiveRecord generateActiveRecordInstance(String className, String model) {
        return generateActiveRecordInstance(className, null, model);
    }
    
    /**
     * <p>Generates an instance of ActiveRecord. <tt>connName</tt> is the 
     * database connection name from where the meta data of the <tt>model</tt> 
     * is obtained. <tt>model</tt> value is used to deduce the table name 
     * related to the ActiveRecord instance.</p>
     * 
     * <p>The table name related to this model is going to be derived from 
     * the model name.</p>
     * 
     * @param className  class name of the ActiveRecord instance to be created
     * @param connName   db connection name
     * @param model      model name of the ActiveRecord class
     * @return an ActiveRecord instance
     */
    public static ActiveRecord generateActiveRecordInstance(String className, String connName, String model) {
        return generateActiveRecordInstance(className, connName, model, null);
    }
    
    /**
     * <p>Generates an instance of ActiveRecord. <tt>connName</tt> is the 
     * database connection name from where the meta data of the <tt>model</tt> 
     * is obtained. <tt>model</tt> value is used to deduce the table name 
     * related to the ActiveRecord instance.</p>
     * 
     * @param className  class name of the ActiveRecord instance to be created
     * @param connName   db connection name
     * @param model      model name of the ActiveRecord class
     * @param table      table name related to the ActiveRecord class
     * @return an ActiveRecord instance
     */
    public static ActiveRecord generateActiveRecordInstance(String className, String connName, String model, String table) {
		if (model == null)
			throw new IllegalArgumentException(
					"model cannot be null in generateActiveRecordInstance().");
    	
        ActiveRecord record = null;
        Class<?> c = null;
        try {
            c = OrmObjectFactory.getInstance().loadClass(className);
            if ( c != null ) {
                String tableName = table;
                if (table == null) {
                    tableName = model;
                    if (DatabaseConfig.getInstance().usePluralTableName()) {
                        tableName = WordUtil.pluralize(model);
                    }
                    
                    tableName = DatabaseConfig.getInstance().getFullTableName(tableName);
                }
                
                if (connName == null) {
                    Class<?>[] parameterTypes = {String.class};
                    Object[] initargs = {tableName};
                    record = (ActiveRecord)newInstance(c, parameterTypes, initargs);
                }
                else {
                    Class<?>[] parameterTypes = {String.class, String.class};
                    Object[] initargs = {connName, tableName};
                    record = (ActiveRecord)newInstance(c, parameterTypes, initargs);
                }
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
    public static Object newInstance(Class<?> c, Class<?>[] parameterTypes, Object[] initargs) 
    throws Exception {
        if (c == null) return null;
        
        Object o = null;
        try {
        	o = OrmObjectFactory.getInstance().newInstance(c.getName(), parameterTypes, initargs);
        }
        catch(Exception nsmEx) {
        	if (c.getName().equals(ActiveRecord.class.getName())) {
				String error = "Failed to create an ActiveRecord instance with args ["
						+ Converters.convertObjectArrayToString(initargs, ", ")
						+ "].";
				log.error(error, nsmEx);
        		throw new Exception(c.getName(), nsmEx);
        	}
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
        String modelKey = getHomeInstanceKey(fullModelClassName);
        ActiveRecord record = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            record = (ActiveRecord)CurrentThreadCache.get(modelKey);
            if (record != null) return record;
        }
        
        Map<String, ActiveRecord> homeMap = getHomeInstanceMap();
        record = (ActiveRecord)homeMap.get(modelKey);
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
    public static ActiveRecord getHomeInstance(Class<? extends ActiveRecord> clz) {
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
            
            setGateInstance(record.getClass().getName(), new TableGateway(record));

            String modelKey = getHomeInstanceKey(record.getClass().getName());
            if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
                CurrentThreadCache.set(modelKey, record);
                
                //cleanup cached relations
                RelationManager.getInstance().removeRelationsFor(getModelName(record));
                return;
            }
            
            Map<String, ActiveRecord> homeMap = getHomeInstanceMap();
            homeMap.put(modelKey, record);
        }
    }
    
    public static TableGateway getGateway(Class<? extends ActiveRecord> modelClass) {
    	return getGateway(modelClass.getName());
    }
    
    public static TableGateway getGateway(String fullModelClassName) {
    	TableGateway gate = null;

        String gateKey = getGateInstanceKey(fullModelClassName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            gate = (TableGateway)CurrentThreadCache.get(gateKey);
            if (gate != null) return gate;
        }
        
        Map<String, TableGateway> gateMap = getGateInstanceMap();
        gate = gateMap.get(gateKey);
        if (gate == null) {
            if (DEFAULT_RECORD_CLASS.equals(fullModelClassName)) {
                throw new IllegalArgumentException("TableGateway instance for type " + fullModelClassName + " must be created first.");
            }
            ActiveRecord home = getHomeInstance(fullModelClassName);
            gate = gateMap.get(gateKey);
            if (gate == null) {
                gate = new TableGateway(home);
                setGateInstance(fullModelClassName, gate);
            }
        }
        
        return gate;
    }
    
    public static TableGateway getGateway(ActiveRecord home) {
    	TableGateway gate = null;
        String fullModelClassName = home.getClass().getName();
        String gateKey = getGateInstanceKey(fullModelClassName);
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            gate = (TableGateway)CurrentThreadCache.get(gateKey);
            if (gate != null) return gate;
        }
        
        Map<String, TableGateway> gateMap = getGateInstanceMap();
        gate = gateMap.get(gateKey);
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
            
            Map<String, TableGateway> gateMap = getGateInstanceMap();
            gateMap.put(gateKey, gate);
        }
    }
    
    /**
     * Returns a Calculator instance for the model class.
     * 
     * @param modelClass model class type
     * @return a Calculator instance for the model class
     */
    public static Calculator getCalculator(Class<? extends ActiveRecord> modelClass) {
    	return getCalculator(modelClass.getName());
    }
    
    /**
     * Returns a Calculator instance for the model class.
     * 
     * @param fullModelClassName
     * @return a Calculator instance for the model class
     */
    public static Calculator getCalculator(String fullModelClassName) {
    	Calculator cal = null;
        String calKey = getCalculatorInstanceKey(fullModelClassName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment() || EnvConfig.getInstance().allowAutoCRUD()) {
            cal = (Calculator)CurrentThreadCache.get(calKey);
            if (cal != null) return cal;
        }
        
        Map<String, Calculator> calMap = getCalculatorInstanceMap();
        cal = calMap.get(calKey);
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
            
            Map<String, Calculator> calMap = getCalculatorInstanceMap();
            calMap.put(calKey, cal);
        }
    }
    
    
    /**
     * Checks if a field name exists in a database table. 
     * 
     * @param clazz         the class type of an ActiveRecord record
     * @param fieldName     a field name
     */
    public static void verifyExistenceOfColumn(Class<? extends ActiveRecord> clazz, String fieldName) {
        ActiveRecord record = getHomeInstance(clazz);
        if (!record.isColumnField(fieldName)) {
            throw new GenericException("Field [" + fieldName + "] is not a column of table " + record.getTableName() + ".");
        }
    }

    private static String getCalculatorInstanceKey(String modelClassName) {
        return "cal_" + modelClassName;
    }
    
    private static Map<String, Calculator> getCalculatorInstanceMap() {
        return calculatorInstanceMap;
    }

    private static String getHomeInstanceKey(String modelClassName) {
        return "model_" + modelClassName;
    }
    
    private static Map<String, ActiveRecord> getHomeInstanceMap() {
        return homeInstanceMap;
    }

    private static String getGateInstanceKey(String modelClassName) {
        return "gate_" + modelClassName;
    }
    
    private static Map<String, TableGateway> getGateInstanceMap() {
        return gateInstanceMap;
    }
    
    /**
     * calculatorInstanceMap stores Calculator instances.
     */
    private static Map<String, Calculator> calculatorInstanceMap = new ConcurrentHashMap<String, Calculator>();
    
    /**
     * gateInstanceMap stores TableGateway instances.
     */
    private static Map<String, TableGateway> gateInstanceMap = new ConcurrentHashMap<String, TableGateway>();
    
    /**
     * homeInstanceMap stores ActiveRecord home instances.
     */
    private static Map<String, ActiveRecord> homeInstanceMap = new ConcurrentHashMap<String, ActiveRecord>();
    
    public static final String DEFAULT_RECORD_CLASS = "com.scooterframework.orm.activerecord.ActiveRecord";
}
