/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.BeanUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ReferenceData;
import com.scooterframework.orm.activerecord.ReferenceDataStore;
import com.scooterframework.orm.sqldataexpress.object.RESTified;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;

/**
 * DataAccessUtil class has helper methods for objects including ActiveRecord 
 * and SQL data express related instances. 
 * 
 * @author (Fei) John Chen
 */
public class DataAccessUtil {
    
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
        if (object == null) return null;
        if (property == null) return object;
        
        Object data = null;
        if (object instanceof ActiveRecord) {
            data = getActiveRecordField((ActiveRecord)object, property);
        }
        else if (object instanceof RowData) {
            data = ((RowData)object).getField(property);
        }
        else if (object instanceof Properties) {
            data = ((Properties)object).getProperty(property);
        }
        else if (object instanceof Map) {
            data = ((Map)object).get(property);
        }
        else {
            data = BeanUtil.getBeanProperty(object, property);
        }
        
        return data;
    }
    
    /**
     * </p>
     * Returns field value of a record. If the property string consists of dots,
     * this method will treat the dotted string as a path in association. 
     * </p>
     * 
     * <pre>
     * Examples:
     *     //post belongsTo user
     *     getActiveRecordField(post, "user.first_name")
     *     => returns first name of the post author
     *     
     *     //lineitem belongsTo order belongsTo customer
     *     getActiveRecordField(lineitem, "order.customer.first_name") 
     *     => returns first name of the customer who ordered the line item
     * </pre>
     * 
     * <p>
     * It is not recommended to use dotted property string unless you are sure
     * the object is in a belongs-to or has-one relation chain among all 
     * elements of the dotted property string.
     * </p>
     * 
     * @param record
     * @param property
     * @return value of the property of the record
     */
    private static Object getActiveRecordField(ActiveRecord record, String property) {
        if (record == null) return null;
        if (property.indexOf('.') == -1) return record.getField(property);
        
        StringTokenizer st = new StringTokenizer(property, " .");
        int total = st.countTokens();
        Object tmp = null;
        int count = 0;
        ActiveRecord r = record;
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            count = count + 1;
            if (count == total) {
                tmp = r.getField(token);//The last token is a field name.
                break;
            }
            else {
                r = r.associated(token).getRecord();
            }
        }
        
        return tmp;
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance.
     * 
     * <p>
     * If the <tt>associationName</tt> string consists of dots, this method 
     * will treat the dotted string as a path in association. 
     * </p>
     * 
     * <pre>
     * Examples:
     *     //customer hasMany orders
     *     allAssociatedRecordsOf(customer, "orders") 
     *     => returns a list of orders of the customer
     *     
     *     //customer hasMany orders hasMany lineitems
     *     allAssociatedRecordsOf(customer, "orders.lineitems") 
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
     * @param associationName association name.
     * @return list of associated records.
     */
    public static List allAssociatedRecordsOf(ActiveRecord record, String associationName) {
        return allAssociatedRecordsOf(record, associationName, false);
    }
    
    /**
     * Gets all the associated records of an ActiveRecord instance.
     * 
     * <p>See description of {@link #allAssociatedRecordsOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param record an ActiveRecord instance.
     * @param associationName association name.
     * @param refresh true if reload database data
     * @return list of associated records.
     */
    public static List allAssociatedRecordsOf(ActiveRecord record, String associationName, boolean refresh) {
        if (record == null || associationName == null) return null;
        if (associationName.indexOf('.') == -1) return record.allAssociated(associationName, refresh).getRecords();
        
        List records = new ArrayList();
        List associations = Converters.convertStringToList(associationName, ".");
        
        int totalAssociations = associations.size();
        records = record.allAssociated((String)associations.get(0), refresh).getRecords();
        if (records == null) return null;
        
        for (int i = 1; i < totalAssociations; i++) {
            String associationId = (String)associations.get(i);
            List tmp = new ArrayList();
            int totalRecords = records.size();
            for (int j = 0; j < totalRecords; j++) {
                List rds = ((ActiveRecord)records.get(j)).allAssociated(associationId, refresh).getRecords();
                if (rds != null && rds.size() > 0) tmp.addAll(rds);
            }
            records = tmp;
        }
        
        return records;
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance. 
     * 
     * <p>
     * If the <tt>associationName</tt> string consists of dots, this method 
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
     * It is not recommended to use dotted <tt>associationName</tt> string 
     * unless you are sure the object is in a belongs-to or has-one relation 
     * chain among all elements of the dotted string.
     * </p>
     * 
     * @param record an ActiveRecord instance.
     * @param associationName association name.
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(ActiveRecord record, String associationName) {
        return associatedRecordOf(record, associationName, false);
    }
    
    /**
     * Gets the associated record of an ActiveRecord instance. 
     * 
     * <p>See description of {@link #associatedRecordOf(com.scooterframework.orm.activerecord.ActiveRecord, 
     * java.lang.String)} method for more details and examples.</p>
     * 
     * @param record an ActiveRecord instance.
     * @param associationName association name.
     * @param refresh true if reload database data
     * @return the associated record
     */
    public static ActiveRecord associatedRecordOf(ActiveRecord record, String associationName, boolean refresh) {
        if (record == null || associationName == null) return null;
        
        if (associationName.indexOf('.') == -1) return record.associated(associationName, refresh).getRecord();
        
        StringTokenizer st = new StringTokenizer(associationName, ".");
        int total = st.countTokens();
        int count = 0;
        ActiveRecord r = record;
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            r = r.associated(token, refresh).getRecord();
            
            count = count + 1;
            if (count == total) {
                break;
            }
        }
        
        return r;
    }
    
    /**
     * Returns all error messages associated with a record instance.
     * 
     * @param record an ActiveRecord instance
     * @return List of error messages.
     */
    public static List getErrorMessages(ActiveRecord record) {
        if (record != null) {
            ValidationResults vr = record.getValidationResults();
            if (vr.failed()) {
                return vr.getErrorMessages();
            }
        }
        return null;
    }
    
    /**
     * Gets a list of ReferenceData instances for a certain type.
     * 
     * @return List
     */
    public static List getReferenceDataList(String type) {
        return ReferenceDataStore.getReferenceDataList(type);
    }
    
    /**
     * Gets ReferenceData by type and key
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndKey(String type, String keyData) {
        return ReferenceDataStore.getReferenceDataByTypeAndKey(type, keyData);
    }
    
    /**
     * Gets ReferenceData by type and value
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndValue(String type, Object valueData) {
        return ReferenceDataStore.getReferenceDataByTypeAndValue(type, valueData);
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
    public static Iterator columnNames(String model) {
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
    public static Iterator columnNames(ActiveRecord record) {
        return (record != null)?columnNames(record.getRowInfo()):(new ArrayList()).iterator();
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
    public static Iterator columnNames(RowInfo rowInfo) {
        Iterator it = null;
        if (rowInfo != null) {
            String[] columnNames = rowInfo.getColumnNames();
            it = Converters.convertArrayToList(columnNames).iterator();
        }
        else {
            it = (new ArrayList()).iterator();
        }
        return it;
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
    public static Iterator columnNames(TableData td) {
        return (td != null)?columnNames(td.getHeader()):(new ArrayList()).iterator();
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
    public static Iterator columnNames(RowData rd) {
        return (rd != null)?columnNames(rd.getRowInfo()):(new ArrayList()).iterator();
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
    public static Iterator columnNames(RESTified record) {
        if (record != null) {
            if (record instanceof ActiveRecord) return columnNames((ActiveRecord)record);
            if (record instanceof RowData) return columnNames((RowData)record);
        }
        return (new ArrayList()).iterator();
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
    public static Iterator columnNames(Collection records) {
        Object record = null;
        if (records != null) {
            Iterator it = records.iterator();
            if (it.hasNext()) record = it.next();
        }
        
        if (record instanceof ActiveRecord) return columnNames((ActiveRecord)record);
        if (record instanceof RowData) return columnNames((RowData)record);
        return (new ArrayList()).iterator();
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
    public static Iterator columns(String model) {
        return columns(homeInstance(model));
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
    public static Iterator columns(ActiveRecord record) {
        return (record != null)?columns(record.getRowInfo()):(new ArrayList()).iterator();
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
    public static Iterator columns(RowInfo rowInfo) {
        Iterator it = null;
        if (rowInfo != null) {
            it = rowInfo.columns().iterator();
        }
        
        if (it == null) {
            it = (new ArrayList()).iterator();
        }
        return it;
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
    public static Iterator columns(RESTified record) {
        if (record != null) {
            if (record instanceof ActiveRecord) return columns((ActiveRecord)record);
            if (record instanceof RowData) return columns((RowData)record);
        }
        return (new ArrayList()).iterator();
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
    public static Iterator columns(RowData rd) {
        return (rd != null)?columns(rd.getRowInfo()):(new ArrayList()).iterator();
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
    public static Iterator columns(Collection records) {
        Object record = null;
        if (records != null) {
            Iterator it = records.iterator();
            if (it.hasNext()) record = it.next();
        }
        
        if (record instanceof ActiveRecord) return columns((ActiveRecord)record);
        if (record instanceof RowData) return columns((RowData)record);
        return (new ArrayList()).iterator();
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
    public static Iterator iteratorOf(Collection items) {
        return (items != null)?items.iterator():(new ArrayList()).iterator();
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
    public static Iterator iteratorOf(Map map) {
        return (map != null)?map.keySet().iterator():(new ArrayList()).iterator();
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
        RowInfo ri = homeInstance(model).getRowInfo();
        return (ri != null)?ri:(new RowInfo());
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
     * Returns RowInfo attribute of the record.
     * 
     * @param record a record object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(ActiveRecord record) {
        return (record != null)?record.getRowInfo():(new RowInfo());
    }
    
    /**
     * Returns RowInfo attribute of the record.
     * 
     * @param record a record object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(RowData record) {
        return (record != null)?record.getRowInfo():(new RowInfo());
    }
    
    /**
     * Returns RowInfo attribute of the TableData instance.
     * 
     * @param tableData a TableData object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(TableData tableData) {
        return (tableData != null)?tableData.getHeader():(new RowInfo());
    }
    
    /**
     * Returns RowInfo attribute of the TableInfo instance.
     * 
     * @param tableInfo a TableInfo object
     * @return RowInfo attribute of the record.
     */
    public static RowInfo rowInfoOf(TableInfo tableInfo) {
        return (tableInfo != null)?tableInfo.getHeader():(new RowInfo());
    }
}
