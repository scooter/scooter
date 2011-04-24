/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Message;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ReferenceData;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.RESTified;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.util.DataAccessUtil;
import com.scooterframework.web.controller.ACH;

/**
 * O(ObjectHelper) class has helper methods for objects including ActiveRecord 
 * related instances. 
 * 
 * @author (Fei) John Chen
 */
public class O {
    
    /**
     * Gets data represented by the key from the first scope it is found.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     * @return Object
     */
    public static Object getObjectForKey(String key) {
        return ACH.getAC().get(key);
    }
    
    /**
     * Gets property value from the object. The object can be of type 
     * <tt>ActiveRecord</tt>, <tt>RowData</tt>, <tt>Properties</tt>, 
     * <tt>Map</tt>, or simply a java bean (POJO). 
     * 
     * <p>
     * If the object is an ActiveRecord instance, and the property string 
     * consists of dots, this method will treat the dotted string as a path 
     * in association. 
     * </p>
     * 
     * <pre>
     * Examples:
     *     //post belongsTo user
     *     getProperty(post, "user.first_name") 
     *     => returns first name of the post author
     *     
     *     //lineitem belongsTo order belongsTo customer
     *     getProperty(lineitem, "order.customer.first_name") 
     *     => returns first name of the customer who ordered the line item
     * </pre>
     * 
     * <p>
     * It is not recommended to use dotted property string unless you are sure
     * the object is in a belongs-to or has-one relation chain among all 
     * elements of the dotted property string.
     * </p>
     * 
     * @param object
     * @param property
     * @return Object
     */
    public static Object getProperty(Object object, String property) {
        return DataAccessUtil.getProperty(object, property);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param keyDotModel type of associated model.
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(String keyDotModel) {
        return allAssociatedRecordsOf(keyDotModel, false);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param keyDotModel type of associated model.
     * @param refresh true if reload database data
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(String keyDotModel, boolean refresh) {
        if (keyDotModel == null || keyDotModel.indexOf('.') == -1) {
            throw new IllegalArgumentException("There must be a dot in input string keyDotModel.");
        }
        
        int dotIndex = keyDotModel.indexOf('.');
        String key = keyDotModel.substring(0, dotIndex);
        String associatedModel = keyDotModel.substring(dotIndex + 1);
        return allAssociatedRecordsOf(key, associatedModel, refresh);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param key the key representing the ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(String key, String associatedModel) {
        return allAssociatedRecordsOf(key, associatedModel, false);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param key the key representing the ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @param refresh true if reload database data
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(String key, String associatedModel, boolean refresh) {
        Object o = getObjectForKey(key);
        if (o == null) return null;
        
        List<ActiveRecord> records = null;
        if (o instanceof ActiveRecord) {
            records = allAssociatedRecordsOf((ActiveRecord)o, associatedModel, refresh);
        }
        else {
            throw new IllegalArgumentException("Object represented by " + key + 
            " must be of ActiveRecord type, but it is of \"" + o.getClass().getName() + "\" type.");
        }
        return records;
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance.
     * 
     * <p>
     * If the <tt>associatedModel</tt> string consists of dots, this method 
     * will treat the dotted string as a path in associaiton. 
     * </p>
     * 
     * <pre>
     * Examples:
     *     //customer hasMany orders
     *     allAssociatedRecordsOf(customer, "order") 
     *     => returns a list of orders of the customer
     *     
     *     //customer hasMany orders hasMany lineitems
     *     allAssociatedRecordsOf(customer, "order.lineitem") 
     *     => returns a list of lineitems for all orders of the customer
     * </pre>
     * 
     * <p>
     * It is not recommended to use dotted <tt>allAssociatedRecordsOf</tt> string 
     * unless you are sure the object is in a hasMany or hasManyThrough relation 
     * chain among all elements of the dotted string.
     * </p>
     * 
     * @param record an ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(ActiveRecord record, String associatedModel) {
        return allAssociatedRecordsOf(record, associatedModel, false);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param record an ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @param refresh true if reload database data
     * @return list of associated records.
     */
    public static List<ActiveRecord> allAssociatedRecordsOf(ActiveRecord record, String associatedModel, boolean refresh) {
        return DataAccessUtil.allAssociatedRecordsOf(record, associatedModel, refresh);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param keyDotModel type of associated model.
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(String keyDotModel) {
        return associatedRecordOf(keyDotModel, false);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance represented by 
     * a key.
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param keyDotModel type of associated model.
     * @param refresh true if reload database data
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(String keyDotModel, boolean refresh) {
        if (keyDotModel == null || keyDotModel.indexOf('.') == -1) {
            throw new IllegalArgumentException("There must be a dot in input string keyDotModel.");
        }
        
        int dotIndex = keyDotModel.indexOf('.');
        String key = keyDotModel.substring(0, dotIndex);
        String associatedModel = keyDotModel.substring(dotIndex + 1);
        return associatedRecordOf(key, associatedModel, refresh);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance represented by 
     * a key. 
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param key the key representing the ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(String key, String associatedModel) {
        return associatedRecordOf(key, associatedModel, false);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance represented by 
     * a key. 
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param key the key representing the ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @param refresh true if reload database data
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(String key, String associatedModel, boolean refresh) {
        Object o = getObjectForKey(key);
        if (o == null) return null;
        
        ActiveRecord record = null;
        if (o instanceof ActiveRecord) {
            record = associatedRecordOf((ActiveRecord)o, associatedModel, refresh);
        }
        else {
            throw new IllegalArgumentException("Object represented by " + key + 
            " must be of ActiveRecord type, but it is of \"" + o.getClass().getName() + "\" type.");
        }
        return record;
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance. 
     * 
     * <p>
     * If the <tt>associatedModel</tt> string consists of dots, this method 
     * will treat the dotted string as a path in association. 
     * </p>
     * 
     * <pre>
     * Examples:
     *     //post belongsTo user
     *     associatedRecordOf(post, "user") 
     *     => returns the post author
     *     
     *     //lineitem belongsTo order belongsTo customer
     *     associatedRecordOf(lineitem, "order.customer") 
     *     => returns the customer who ordered the line item
     * </pre>
     * 
     * <p>
     * It is not recommended to use dotted <tt>associatedModel</tt> string 
     * unless you are sure the object is in a belongs-to or has-one relation 
     * chain among all elements of the dotted string.
     * </p>
     * 
     * @param record an ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(ActiveRecord record, String associatedModel) {
        return associatedRecordOf(record, associatedModel, false);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance. 
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param record an ActiveRecord instance.
     * @param associatedModel type of associated model.
     * @param refresh true if reload database data
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(ActiveRecord record, String associatedModel, boolean refresh) {
        return DataAccessUtil.associatedRecordOf(record, associatedModel, refresh);
    }
    
    /**
     * <p>Returns html-escaped text of an object's property. The object is 
     * associated with the key.</p>
     * <p>
     * This method applies the method {@link com.scooterframework.web.util.W#htmlEsacpe(String)}
     * to the result of {@link #value(java.lang.String) value} method.</p>
     * <p>
     * See description of {@link com.scooterframework.web.util.W#htmlEsacpe(String)} 
     * method for which characters are escaped.</p>
     * <p>
     * See description of {@link #value(java.lang.String) value} method for how 
     * the <tt>keyProperty</tt> is formatted.</p>
     * 
     * @param keyProperty a "key.property" string
     * @return html-escaped text
     */
    public static String hv(String keyProperty) {
        return W.htmlEsacpe(value(keyProperty));
    }
    
    /**
     * <p>Returns html-escaped text of an object's property. </p>
     * <p>
     * This method applies the method {@link com.scooterframework.web.util.W#htmlEsacpe(String)}
     * to the result of {@link #property(java.lang.Object, java.lang.String) property} method.</p>
     * <p>
     * See description of {@link com.scooterframework.web.util.W#htmlEsacpe(String)} 
     * method for which characters are escaped.</p>
     * <p>
     * See description of {@link #property(java.lang.Object, java.lang.String) property} 
     * method for how the <tt>property</tt> is formatted.</p>
     * 
     * @param object the object
     * @param property the property string
     * @return html-escaped text
     */
    public static String hp(Object object, String property) {
        return W.htmlEsacpe(property(object, property));
    }
    
    /**
     * Returns text of a <tt>key.property</tt> value. If data for the property 
     * is null, an empty string is returned.
     * 
     * Key is a constant representing an object instance in a scope. Property is 
     * an attribute name of the object or a dotted path to an attribute. 
     * 
     * <pre>
     * Examples:
     *      value("comment.created_at")   returns created date text of an object represented by key "comment".
     *      value("post.title")           returns title text of an object represented by key "post".
     *      value("user.first_name")      returns first name text of an object represented by key "user".
     *      value("post.user.first_name") returns first name text of the author of the post.
     * </pre>
     * 
     * @param keyProperty a "key.property" string
     * @return text of a key property
     */
    public static String value(String keyProperty) {
        return value(keyProperty, null);
    }
    
    /**
     * Returns text of a <tt>key.property</tt> value. 
     * 
     * <p>See description of {@link #value(java.lang.String) value} method for 
     * more details.</p>
     * 
     * @param keyProperty a "key.property" string
     * @param pattern pattern of the value
     * @return text of a key property
     */
    public static String value(String keyProperty, String pattern) {
        return value(keyProperty, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * Returns text of a <tt>key.property</tt> value. 
     * 
     * <p>See description of {@link #value(java.lang.String) value} method for 
     * more details and examples.</p>
     * 
     * @param keyProperty a "key.property" string
     * @param pattern pattern of the value
     * @param locale locale of the value
     * @return text of a key property
     */
    public static String value(String keyProperty, String pattern, Locale locale) {
        if (keyProperty == null) return "";
        
        if (keyProperty.indexOf('.') == -1) return keyProperty;
        
        int dotIndex = keyProperty.indexOf('.');
        String key = keyProperty.substring(0, dotIndex);
        String property = keyProperty.substring(dotIndex + 1);
        return property(getObjectForKey(key), property, pattern, locale);
    }
    
    /**
     * Returns text of an object's property. 
     * 
     * Property is an attribute name of the object or a dotted path to an attribute. 
     * 
     * <pre>
     * Examples:
     *      property(comment, "created_at")   returns created date text of a comment record.
     *      property(post, "title")           returns title text of a post record.
     *      property(user, "first_name")      returns first name text of a user record.
     *      property(post, "user.first_name") returns first name text of the author of the post record.
     * </pre>
     * 
     * @param object the object
     * @param property the property string
     * @return text of an object's property
     */
    public static String property(Object object, String property) {
        return property(object, property, null);
    }
    
    /**
     * Returns text of an object's property. 
     * 
     * <p>See description of {@link #property(java.lang.Object, java.lang.String) property} 
     * method for more details and examples.</p>
     * 
     * @param object the object
     * @param property the property string
     * @param pattern pattern of the property
     * @return text of an object's property
     */
    public static String property(Object object, String property, String pattern) {
        return property(object, property, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * Returns text of an object's property. 
     * 
     * <p>See description of {@link #property(java.lang.Object, java.lang.String) property} 
     * method for more details and examples.</p>
     * 
     * @param object the object
     * @param property the property string
     * @param pattern pattern of the pattern
     * @param locale locale of the pattern
     * @return text of an object's property
     */
    public static String property(Object object, String property, String pattern, Locale locale) {
        if (object == null) return "";
        return T.text(getProperty(object, property), pattern, locale);
    }
    
    /**
     * Returns text of an object's property. 
     * 
     * <p>See description of {@link #property(java.lang.Object, java.lang.String) property} 
     * method for more details and examples.</p>
     * 
     * @param object the object
     * @param property the property string
     * @param type data type (1=Currency, 2=Date, 3=Number)
     * @param pattern pattern of the property
     * @param locale locale of the property
     * @return text of an object's property
     */
    public static String property(Object object, String property, int type, String pattern, Locale locale) {
        if (object == null) return "";
        return T.text(getProperty(object, property), type, pattern, locale);
    }
    
    /**
     * Returns all error messages associated with a model instance.
     * 
     * @param model   model name
     * @return List of error messages.
     */
    public static List<Message> getErrorMessages(String model) {
        Object data = getObjectForKey(model);
        if (data != null && data instanceof ActiveRecord) {
        	return getErrorMessages((ActiveRecord)data);
        }
        return null;
    }
    
    /**
     * Returns all error messages associated with a record instance.
     * 
     * @param record an ActiveRecord instance
     * @return List of error messages.
     */
    public static List<Message> getErrorMessages(ActiveRecord record) {
        return DataAccessUtil.getErrorMessages(record);
    }
    
    /**
     * Gets a list of ReferenceData instances for a certain type.
     * 
     * @return List
     */
    public static List<ReferenceData> getReferenceDataList(String type) {
        return DataAccessUtil.getReferenceDataList(type);
    }
    
    /**
     * Gets ReferenceData by type and key
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndKey(String type, String keyData) {
        return DataAccessUtil.getReferenceDataByTypeAndKey(type, keyData);
    }
    
    /**
     * Gets ReferenceData by type and value
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndValue(String type, Object valueData) {
        return DataAccessUtil.getReferenceDataByTypeAndValue(type, valueData);
    }
    
    /**
     * Returns home instance of the model.
     * 
     * @param model model name
     * @return home instance of the model
     */
    public static ActiveRecord homeInstance(String model) {
        return EnvConfig.getInstance().getHomeInstance(model);
    }
    
    /**
     * <p>
     * Returns an iterator of column names of the model. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param model model name
     * @return iterator
     */
    public static Iterator<String> columnNames(String model) {
        return columnNames(homeInstance(model));
    }
    
    /**
     * <p>
     * Returns an iterator of column names of the record. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param record an active record instance or home instance
     * @return iterator
     */
    public static Iterator<String> columnNames(ActiveRecord record) {
        return DataAccessUtil.columnNames(record);
    }
    
    /**
     * <p>
     * Returns an iterator of column names. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param rowInfo a RowInfo instance
     * @return iterator
     */
    public static Iterator<String> columnNames(RowInfo rowInfo) {
        return DataAccessUtil.columnNames(rowInfo);
    }
    
    /**
     * <p>
     * Returns an iterator of column names. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param td a TableData instance
     * @return iterator
     */
    public static Iterator<String> columnNames(TableData td) {
        return DataAccessUtil.columnNames(td);
    }
    
    /**
     * <p>
     * Returns an iterator of column names. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param rd a RowData instance
     * @return iterator
     */
    public static Iterator<String> columnNames(RowData rd) {
        return DataAccessUtil.columnNames(rd);
    }
    
    /**
     * <p>
     * Returns an iterator of column names. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param record    a restified record
     * @return iterator
     */
    public static Iterator<String> columnNames(RESTified record) {
        return DataAccessUtil.columnNames(record);
    }
    
    /**
     * <p>
     * Returns an iterator of column names of the record in the <tt>records</tt>
     * collection. This is a safe method. If input record is null, an empty 
     * iterator is still returned.
     * </p>
     * 
     * @param records a collection of records
     * @return iterator
     */
    public static Iterator<String> columnNames(Collection<?> records) {
        return DataAccessUtil.columnNames(records);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances of the model. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param model model name
     * @return iterator
     */
    public static Iterator<ColumnInfo> columns(String model) {
        return DataAccessUtil.columns(model);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances of the record. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param record an ActiveRecord record instance or home instance
     * @return an iterator of ColumnInfo instances
     */
    public static Iterator<ColumnInfo> columns(ActiveRecord record) {
        return DataAccessUtil.columns(record);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param rowInfo a RowInfo instance
     * @return iterator
     */
    public static Iterator<ColumnInfo> columns(RowInfo rowInfo) {
        return DataAccessUtil.columns(rowInfo);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances of the record. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param record    a restified record
     * @return an iterator of ColumnInfo instances
     */
    public static Iterator<ColumnInfo> columns(RESTified record) {
        return DataAccessUtil.columns(record);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances of the record. This is a safe 
     * method. If input record is null, an empty iterator is still returned.
     * </p>
     * 
     * @param rd a RowData record instance
     * @return an iterator of ColumnInfo instances
     */
    public static Iterator<ColumnInfo> columns(RowData rd) {
        return DataAccessUtil.columns(rd);
    }
    
    /**
     * <p>
     * Returns an iterator of ColumnInfo instances of the <tt>records</tt>
     * collection. This is a safe method. If input record is null, an empty 
     * iterator is still returned.
     * </p>
     * 
     * @param records a collection of records
     * @return an iterator of ColumnInfo instances
     */
    public static Iterator<ColumnInfo> columns(Collection<?> records) {
        return DataAccessUtil.columns(records);
    }
    
    /**
     * <p>
     * Returns an iterator of a collection represented by a key. This is a 
     * safe method. If the collection is null, an empty iterator is still 
     * returned.
     * </p>
     * 
     * @param key   key of a collection saved in request
     * @return an iterator of the collection
     */
    public static Iterator<?> iteratorOf(String key) {
        Object items = getObjectForKey(key);
        
        if (items != null) {
            if (items instanceof Collection) {
                return iteratorOf((Collection<?>)items);
            }
            else if (items instanceof Map) {
            	return iteratorOf((Map<?, ?>)items); 
            }
            else if (items instanceof Object[]) {
                return Arrays.asList(((Object[])items)).iterator(); 
            }
            else {
                throw new IllegalArgumentException("Error in iteratorOf(String): " + 
                    "The object associated with key \"" + key + "\" must be of " + 
                    "type Collection or Map or Object[], not of type \"" + 
                    items.getClass().getName() + "\".");
            }
        }
        
        return (new ArrayList<Object>()).iterator();
    }
    
    /**
     * <p>
     * Returns an iterator of a collection. This is a safe method. If the 
     * collection is null, an empty iterator is still returned.
     * </p>
     * 
     * @param items a collection of items
     * @return an iterator of the collection
     */
    public static Iterator<?> iteratorOf(Collection<?> items) {
        return DataAccessUtil.iteratorOf(items);
    }
    
    /**
     * <p>
     * Returns an iterator of a map. This is a safe method. If the 
     * map is null, an empty iterator is still returned.
     * </p>
     * 
     * @param map a map
     * @return an iterator of the collection
     */
    public static <K, V> Iterator<K> iteratorOf(Map<K, V> map) {
    	return DataAccessUtil.iteratorOf(map);
    }
    
    /**
     * <p>
     * Returns a RowInfo instance of the model. This is a safe 
     * method. If input model is null, an empty RowInfo instance is still returned.
     * </p>
     * 
     * @param model model name
     * @return RowInfo for the model
     */
    public static RowInfo getRowInfo(String model) {
        return DataAccessUtil.getRowInfo(model);
    }
    
    /**
     * Counts total number of items in an array.
     * 
     * @param items array of items
     * @return total number of items
     */
    public static int count(Object[] items) {
        return (items != null)?items.length:0;
    }
    
    /**
     * Counts total number of items in a collection.
     * 
     * @param items collection of items
     * @return total number of items
     */
    public static int count(Collection<?> items) {
        return (items != null)?items.size():0;
    }
    
    /**
     * Counts total number of items in a map.
     * 
     * @param items map of items
     * @return total number of items
     */
    public static <K, V> int count(Map<K, V> items) {
        return (items != null)?items.size():0;
    }
    
    /**
     * <p>
     * Returns an iterator of a collection represented by a key. This is a 
     * safe method. If the collection is null, an empty iterator is still 
     * returned.
     * </p>
     * 
     * @param key   key of a collection saved in request
     * @return an iterator of the collection
     */
    public static <K, V> int count(String key) {
        int result = -1;
        Object items = getObjectForKey(key);
        if (items == null) return 0;
        
        if (items instanceof Collection) {
            result = count((Collection<?>)items); 
        }
        else if (items instanceof Map) {
            result = count((Map<?, ?>)items); 
        }
        else if (items instanceof Object[]) {
            result = count((Object[])items); 
        }
        else {
            throw new IllegalArgumentException("Error in count(String): " + 
                "The object associated with key \"" + key + "\" must be of " + 
                "type Collection or Map or Object[], not of type \"" + 
                items.getClass().getName() + "\".");
        }
        return result;
    }
    
    /**
     * Returns restful id of the record. If it is null, return empty string.
     * 
     * @param record a RESTified record
     * @return restful id of the record.
     */
    public static String restfulIdOf(RESTified record) {
        return (record != null)?record.getRestfulId():"";
    }
    
    /**
     * Returns url-encoded restful id of a record.
     * 
     * @param record a RESTified record
     * @return encoded restful id of the record.
     */
    public static String encodedRestfulIdOf(RESTified record) {
        return W.encode(restfulIdOf(record));
    }
    
    /**
     * Returns RowInfo attribute of the record.
     * 
     * @param record a record object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(ActiveRecord record) {
        return DataAccessUtil.rowInfoOf(record);
    }
    
    /**
     * Returns RowInfo attribute of the record.
     * 
     * @param record a record object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(RowData record) {
        return DataAccessUtil.rowInfoOf(record);
    }
    
    /**
     * Returns RowInfo attribute of the TableData instance.
     * 
     * @param tableData a TableData object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(TableData tableData) {
        return DataAccessUtil.rowInfoOf(tableData);
    }
    
    /**
     * Returns RowInfo attribute of the TableInfo instance.
     * 
     * @param tableInfo a TableInfo object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(TableInfo tableInfo) {
        return DataAccessUtil.rowInfoOf(tableInfo);
    }
}
