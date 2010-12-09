/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.scooterframework.common.exception.ObjectCreationException;
import com.scooterframework.common.exception.RequiredDataMissingException;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorTypes;
import com.scooterframework.orm.sqldataexpress.service.SqlService;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceConfig;

/**
 * <p>TableGateway class implements Table Data Gateway pattern. TableGateway 
 * handles access to all records of a table or view for a domain model: selects, 
 * updates, deletes.</p>
 * 
 * <p>There is no callback involved in methods in this class. To enable 
 * callbacks when deleting or updating a set of records, you can first 
 * retrieve them and then use the record instance's delete() or update() 
 * method which has callbacks. </p>
 * 
 * @author (Fei) John Chen
 */
public class TableGateway {
    
    private Class clazz;
    
    private ActiveRecord home;
    
    /**
     * Constructs an instance of TableGateway.
     * 
     * @param modelClazz a domain model class type
     */
    TableGateway(Class modelClazz) {
        this.clazz = modelClazz;
        this.home = ActiveRecordUtil.getHomeInstance(modelClazz);
    }
    
    /**
     * Constructs an instance of TableGateway.
     * 
     * @param modelHome a domain model home instance
     */
    TableGateway(ActiveRecord modelHome) {
        if (modelHome == null) throw new IllegalArgumentException("modelHome is null.");
        if (!modelHome.isHomeInstance()) 
            throw new IllegalArgumentException("modelHome must be a home instance first.");
        
        this.clazz = modelHome.getClass();
        this.home = modelHome;
    }
    
    /**
     * Returns the underlining home instance of this gateway.
     */
    public ActiveRecord getHomeInstance() {
    	return home;
    }
    
    /**
     * Returns the underlining model class type of this gateway.
     */
    public Class getModelClass() {
    	return clazz;
    }
    
    
    /**
     * 
     * FIND related
     * 
     */
     
    
    /**
     * Finds the record with the given id, assuming ID is a column.  
     * 
     * If there is no column name like "ID", an exception will be thrown.
     * 
     * @param id the id of the record
     * @return the ActiveRecord associated with the <tt>id</tt>
     */
    public ActiveRecord findById(Object id) {
        if (!home.getTableInfo().getHeader().isValidColumnName("ID")) {
            throw new IllegalArgumentException("There is no column name as ID");
        }
        
        ActiveRecord newRecord = null;
        
        String findSQL = "SELECT * FROM " + home.getTableName() + " WHERE id = ?";
        
        try {
            Map inputs = new HashMap();
            inputs.put("1", id);
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL);
            
            RowData tmpRd = returnTO.getTableData(findSQL).getRow(0);
            newRecord = (ActiveRecord) createNewInstance();
            
            newRecord.populateDataFromDatabase(tmpRd);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return newRecord;
    }
    
    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     * 
     * @param sql       a valid SQL query string
     * @return a list of ActiveRecord objects
     */
    public List findAllBySQL(String sql) {
        return findAllBySQL(sql, null);
    }
    
    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     * 
     * @param sql       a valid SQL query string
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public List findAllBySQL(String sql, Map inputs) {
        List list = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, sql);
            
            if ( returnTO != null ) {
                TableData rt = returnTO.getTableData(sql);
                if (rt != null) {
                    list = new ArrayList();
                    int records = rt.getTableSize();
                    for (int i = 0; i < records; i++) {
                        ActiveRecord newRecord = (ActiveRecord) createNewInstance();
                        newRecord.populateDataFromDatabase(rt.getRow(i));
                        list.add(newRecord);
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    /**
     * <p>Finds all the records that satisfy the sql corresponding to the 
     * sql key. </p>
     * 
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @return a list of ActiveRecord objects
     */
    public List findAllBySQLKey(String sqlKey) {
        return findAllBySQLKey(sqlKey, null);
    }
    
    /**
     * <p>Finds all the records that satisfy the sql corresponding to the 
     * sql key. </p>
     * 
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public List findAllBySQLKey(String sqlKey, Map inputs) {
        List list = null;
        
        try {
            OmniDTO returnTO = 
                getSqlService().execute(inputs, DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, sqlKey);
            
            if ( returnTO != null ) {
                TableData rt = returnTO.getTableData(sqlKey);
                if (rt != null) {
                    list = new ArrayList();
                    int records = rt.getTableSize();
                    for (int i = 0; i < records; i++) {
                        ActiveRecord newRecord = (ActiveRecord) createNewInstance();
                        newRecord.populateDataFromDatabase(rt.getRow(i));
                        list.add(newRecord);
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions.</p> 
     * 
     * <p>This is a dynamic finder method. 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     * 
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public ActiveRecord findFirstBy(String columns, Object[] values) {
        ActiveRecord theRecord = null;
        List all = findAllBy(columns, values);
        if (all != null && all.size() > 0) {
            theRecord = (ActiveRecord)all.get(0);
        }
        
        return theRecord;
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions.</p> 
     * 
     * <p>This is a dynamic finder method. 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     * 
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public ActiveRecord findLastBy(String columns, Object[] values) {
        ActiveRecord theRecord = null;
        List all = findAllBy(columns, values);
        if (all != null && all.size() > 0) {
            theRecord = (ActiveRecord)all.get(all.size()-1);
        }
        
        return theRecord;
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p> 
     * 
     * <p>This is a dynamic finder method. 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     * 
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  List of ActiveRecord objects
     */
    public List findAllBy(String columns, Object[] values) {
        return findAllBy(columns, values, (Map)null);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p> 
     * 
     * <p>This is a dynamic finder method. 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     * 
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a map of options
     * @return  List of ActiveRecord objects
     */
    public List findAllBy(String columns, Object[] values, Map options) {
        List names = StringUtil.splitString(columns, "_and_");
        if (names == null || values.length != names.size()) 
            throw new IllegalArgumentException("Number of input values does not match number of columns.");
        
        int size = values.length;
        Map map = new HashMap(size);
        for (int i = 0; i < size; i++ ) {
            map.put(names.get(i), values[i]);
        }
        
        return findAll(map, options);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p> 
     * 
     * <p>This is a dynamic finder method. 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     * 
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a string of options
     * @return  List of ActiveRecord objects
     */
    public List findAllBy(String columns, Object[] values, String options) {
        return findAllBy(columns, values, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds all the records of a table.</p>
     * 
     * @return a list of ActiveRecord objects
     */
    public List findAll() {
        return findAll((String)null);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @return a list of ActiveRecord objects
     */
    public List findAll(Map conditions) {
        return findAll(conditions, (Map)null);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return a list of ActiveRecord objects
     */
    public List findAll(Map conditions, Map options) {
        List list = null;
        
        if (options != null && (
    		options.containsKey(ActiveRecordConstants.key_include) ||
    		options.containsKey(ActiveRecordConstants.key_strict_include))) {
            list = internal_findAll_include(conditions, options);
        }
        else {
            list = internal_findAll(conditions, options);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return a list of ActiveRecord objects
     */
    public List findAll(Map conditions, String options) {
    	return findAll(conditions, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @return a list of ActiveRecord objects
     */
    public List findAll(String conditionsSQL) {
        return findAll(conditionsSQL, (Map)null);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return a list of ActiveRecord objects
     */
    public List findAll(String conditionsSQL, String options) {
        return findAll(conditionsSQL, (Map)null, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return a list of ActiveRecord objects
     */
    public List findAll(String conditionsSQL, Map conditionsSQLData) {
        return findAll(conditionsSQL, conditionsSQLData, (Map)null);
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return a list of ActiveRecord objects
     */
    public List findAll(String conditionsSQL, Map conditionsSQLData, Map options) {
        List list = null;
        
        if (options != null && (
    		options.containsKey(ActiveRecordConstants.key_include) ||
    		options.containsKey(ActiveRecordConstants.key_strict_include))) {
            list = internal_findAll_include(conditionsSQL, conditionsSQLData, options);
        }
        else {
            list = internal_findAll(conditionsSQL, conditionsSQLData, options);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return a list of ActiveRecord objects
     */
    public List findAll(String conditionsSQL, Map conditionsSQLData, String options) {
        return findAll(conditionsSQL, conditionsSQLData, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the first record of a table.</p>
     * 
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst() {
        return findFirst((String)null);
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(Map conditions) {
        return findFirst(conditions, (Map)null);
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(Map conditions, Map options) {
    	if (options == null) options = new HashMap();
        options.put(DataProcessor.input_key_records_offset, "0");
        options.put(DataProcessor.input_key_records_limit,  "1");
        List list = findAll(conditions, options);
        return (list != null && list.size() > 0)?((ActiveRecord)list.get(0)):null;
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(Map conditions, String options) {
    	return findFirst(conditions, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(String conditionsSQL) {
        return findFirst(conditionsSQL, (Map)null);
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(String conditionsSQL, String options) {
        return findFirst(conditionsSQL, (Map)null, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData) {
        return findFirst(conditionsSQL, conditionsSQLData, (Map)null);
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData, Map options) {
    	if (options == null) options = new HashMap();
    	if (!options.containsKey(ActiveRecordConstants.key_include) && 
    			!options.containsKey(ActiveRecordConstants.key_strict_include)) {
            options.put(DataProcessor.input_key_records_offset, "0");
            options.put(DataProcessor.input_key_records_limit,  "1");
    	}
        List list = findAll(conditionsSQL, conditionsSQLData, options);
        return (list != null && list.size() > 0)?((ActiveRecord)list.get(0)):null;
    }
    
    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return the first ActiveRecord found
     */
    public ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData, String options) {
        return findFirst(conditionsSQL, conditionsSQLData, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the last record of a table.</p>
     * 
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast() {
        return findLast((String)null);
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(Map conditions) {
        return findLast(conditions, (Map)null);
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(Map conditions, Map options) {
        List list = findAll(conditions, options);
        int size = list.size();
        return (size > 0)?((ActiveRecord)list.get(size-1)):null;
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(Map conditions, String options) {
    	return findLast(conditions, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(String conditionsSQL) {
        return findLast(conditionsSQL, (Map)null);
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(String conditionsSQL, String options) {
        return findLast(conditionsSQL, (Map)null, Converters.convertSqlOptionStringToMap(options));
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData) {
        return findLast(conditionsSQL, conditionsSQLData, (Map)null);
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData, Map options) {
        List list = findAll(conditionsSQL, conditionsSQLData, options);
        int size = list.size();
        return (size > 0)?((ActiveRecord)list.get(size-1)):null;
    }
    
    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return the last ActiveRecord found
     */
    public ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData, String options) {
        return findLast(conditionsSQL, conditionsSQLData, Converters.convertSqlOptionStringToMap(options));
    }
    
    
    
    
    /**
     * Finds a list of records that satisify the conditions and options.
     */
    protected List internal_findAll(Map conditions, Map options) {
        List list = null;
        
        try {
            Map inputs = constructFindSQL(conditions, options);
            String findSQL = (String)inputs.get(ActiveRecordConstants.key_finder_sql);
            int offset = Util.getIntValue(options, DataProcessor.input_key_records_offset, 0);
            int limit = Util.getIntValue(options, DataProcessor.input_key_records_limit, DataProcessor.NO_ROW_LIMIT);
            
            TableData td = getSqlService().retrieveRows(inputs, 
                                                        DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, 
                                                        findSQL, 
                                                        limit, 
                                                        offset);
            
            if (td != null) {
                list = new ArrayList();
                int records = td.getTableSize();
                for (int i = 0; i < records; i++) {
                    ActiveRecord newRecord = (ActiveRecord) createNewInstance();
                    newRecord.populateDataFromDatabase(td.getRow(i));
                    list.add(newRecord);
                }
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new BaseSQLException(ex);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    private List internal_findAll(String conditionsSQL, Map conditionsSQLData, Map options) {
        List list = null;
        
        try {
            Map inputs = constructFindSQL(conditionsSQL, conditionsSQLData, options);
            String findSQL = (String)inputs.get(ActiveRecordConstants.key_finder_sql);
            int offset = Util.getIntValue(options, DataProcessor.input_key_records_offset, 0);
            int limit = Util.getIntValue(options, DataProcessor.input_key_records_limit, DataProcessor.NO_ROW_LIMIT);
            
            TableData td = getSqlService().retrieveRows(inputs, 
                                                        DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, 
                                                        findSQL, 
                                                        limit, 
                                                        offset);
            
            if (td != null) {
                list = new ArrayList();
                int records = td.getTableSize();
                for (int i = 0; i < records; i++) {
                    ActiveRecord newRecord = (ActiveRecord) createNewInstance();
                    newRecord.populateDataFromDatabase(td.getRow(i));
                    list.add(newRecord);
                }
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new BaseSQLException(ex);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    Object createNewInstance() 
    throws Exception {
        Object o = null;
        Class[] parameterTypes = {String.class};
        Object[] initargs = {home.getTableName()};
        
        try {
            o = ActiveRecordUtil.newInstance(home.getClass(), parameterTypes, initargs);
        }
        catch(Exception ex) {
            throw new ObjectCreationException(home.getClass().getName(), ex);
        }
        return o;
    }
    
    //This method is mostly used by internal and JdbcPageListSource
    public Map constructFindSQL(Map conditions, Map options) {
        Map inputsAndSql = new HashMap();
        
        String findSQL = getFinderSql(options);
        boolean finderHasWhere = checkFinderSqlWhere(findSQL);
        
        String conditionSql = null;
        if (options != null && options.size() > 0) {
            conditionSql = (String)options.get(ActiveRecordConstants.key_conditions_sql);
        }
        
        //construct where clause
        Map inputs = new HashMap();
        String whereClause = "";
        boolean useWhere = false;
        if (conditions != null && conditions.size() > 0) {
            whereClause = "";
            int position = 1;
            for(Iterator it = conditions.keySet().iterator(); it.hasNext();) {
                String columnName = (String) it.next();
                
                //skip system keys
                if (columnName == null || columnName.startsWith("_") || 
                    columnName.toUpperCase().startsWith(DataProcessor.framework_input_key_prefix.toUpperCase()) ||
                    !home.isColumnField(columnName)) continue;
                
                Object conditionData = conditions.get(columnName);
                whereClause += columnName + " = ? AND ";
                //inputs.put(columnName, conditionData);
                inputs.put(position+"", conditionData);
                useWhere = true;
                
                position = position + 1;
            }
            
            if (whereClause.endsWith("AND ")) {
                int lastAnd = whereClause.lastIndexOf("AND ");
                whereClause = whereClause.substring(0, lastAnd);
            }
            
            inputsAndSql.putAll(conditions);
        }
        
        if (finderHasWhere) {
            if (useWhere) {
                findSQL += " AND " + whereClause;
            }
            
            if (conditionSql != null && !"".equals(conditionSql)) {
                findSQL += " AND (" + conditionSql + ")";
            }
        }
        else {
            if (useWhere) {
                findSQL += " WHERE " + whereClause;
                
                if (conditionSql != null && !"".equals(conditionSql)) {
                    findSQL += " AND (" + conditionSql + ")";
                }
            }
            else {
                if (conditionSql != null && !"".equals(conditionSql)) {
                    findSQL += " WHERE " + conditionSql;
                }
            }
        }
        
        findSQL += QueryHelper.getAllSelectQueryClauses(options);
        if (options != null) inputsAndSql.putAll(options);
        
        inputsAndSql.put(ActiveRecordConstants.key_finder_sql, findSQL);
        inputsAndSql.putAll(inputs);
        
        return inputsAndSql;
    }
    
    private Map constructFindSQL(String conditionsSQL, Map conditionsSQLData, Map options) {
        Map inputsAndSql = new HashMap();
        
        String findSQL = getFinderSql(options);
        boolean finderHasWhere = checkFinderSqlWhere(findSQL);
        
        if (finderHasWhere) {
            if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
                findSQL += " AND (" + conditionsSQL + ")";
                if (conditionsSQLData != null) {
                	inputsAndSql.putAll(conditionsSQLData);
                }
            }
        }
        else {
        	 if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
                findSQL += " WHERE " + conditionsSQL;
                if (conditionsSQLData != null) {
                	inputsAndSql.putAll(conditionsSQLData);
                }
            }
        }
        
        findSQL += QueryHelper.getAllSelectQueryClauses(options);
        if (options != null) inputsAndSql.putAll(options);
        
        inputsAndSql.put(ActiveRecordConstants.key_finder_sql, findSQL);
        
        return inputsAndSql;
    }
    
    public String getFinderSql(Map options) {
        String finderSQL = "";
        if (options != null && options.containsKey(ActiveRecordConstants.key_finder_sql)) {
            finderSQL = (String)options.get(ActiveRecordConstants.key_finder_sql);
            return finderSQL;
        }
        
        //construct finger SQL query
        finderSQL = "SELECT ";
        if (options != null && options.size() > 0) {
            String unique = (String)options.get(ActiveRecordConstants.key_unique);
            if ("true".equalsIgnoreCase(unique)) {
                finderSQL = "SELECT DISTINCT ";
            }
        }
        String table = home.getTableName();
        
        boolean useColumns = false;
        boolean exColumns = false;
        if (options != null && options.size() > 0) {
            String columns = (String)options.get(ActiveRecordConstants.key_columns);
            String excolumns = (String)options.get(ActiveRecordConstants.key_ex_columns);
            if (columns != null) {
                useColumns = true;
            }
            if (excolumns != null) {
                exColumns = true;
            }
        }
        
        if (!useColumns && !exColumns) {
            finderSQL += table + ".*";
        }
        else if (useColumns) {
            String columnsStr = (String)options.get(ActiveRecordConstants.key_columns);
            List columns = Converters.convertStringToUniqueList(columnsStr.toUpperCase());
            Iterator it = columns.iterator();
            while(it.hasNext()) {
                finderSQL += table + "." + it.next() + ", ";
            }
            finderSQL = StringUtil.removeLastToken(finderSQL, ", ");
        }
        else if (exColumns) {
            String excolumnsStr = (String)options.get(ActiveRecordConstants.key_ex_columns);
            List excolumns = Converters.convertStringToUniqueList(excolumnsStr.toUpperCase());
            
            String[] columns = home.getRowInfo().getColumnNames();
            int length = columns.length;
            for (int i=0; i<length; i++) {
                String column = columns[i];
                if (excolumns.contains(column)) continue;
                finderSQL += table + "." + column + ", ";
            }
            finderSQL = StringUtil.removeLastToken(finderSQL, ", ");
        }
        
        finderSQL += " FROM " + table;
        
        return finderSQL;
    }
    
    static boolean checkFinderSqlWhere(String finderSQL) {
        boolean status = false;
        finderSQL = finderSQL.toUpperCase();
        if (finderSQL.indexOf("WHERE") != -1) {
            //make sure the "where" is valid
            boolean foundFrom = false;
            int countP = 0;
            StringTokenizer st = new StringTokenizer(finderSQL);
            while(st.hasMoreTokens()) {
                String token = st.nextToken();
                if (!foundFrom) {
                    if ("FROM".equals(token)) foundFrom = true;
                }
                else {
                    if (token.startsWith("(")) countP = countP + 1;
                    else 
                    if (token.startsWith(")")) countP = countP - 1;
                    else
                    if ("WHERE".equals(token) && countP == 0) status = true;
                }
            }
        }
        return status;
    }
    
    /**
     * Finds a list of records that satisfy the conditions and options.
     */
    List internal_findAll_include(Map conditions, Map options) {
        IncludeHelper sqlHelper = new IncludeHelper(getModelClass(), conditions, options);
        return internal_findAll_include_fetch(sqlHelper, options);
    }
    
    /**
     * Finds a list of records that satisfy the conditions and options.
     */
    List internal_findAll_include(String conditionsSQL, Map conditionsSQLData, Map options) {
        IncludeHelper sqlHelper = new IncludeHelper(getModelClass(), conditionsSQL, conditionsSQLData, options);
        return internal_findAll_include_fetch(sqlHelper, options);
    }
    
    /**
     * Finds a list of records that satisfy the conditions and options in 
     * a has-many-through relation.
     */
    List internal_findAll_include_hmt(Map conditions, Map options, String innerSQL, 
    		String midCMapping, Map midCMapData, String conditionsSQL) {
        IncludeHelper sqlHelper = new IncludeHelper(getModelClass(), conditions, 
        		options, innerSQL, midCMapping, midCMapData, conditionsSQL);
        return internal_findAll_include_fetch(sqlHelper, options);
    }
    
    private List internal_findAll_include_fetch(IncludeHelper sqlHelper, Map options) {
        List list = null;
        
        try {
            Map inputs = sqlHelper.getConstructedSqlQuery();
            String findSQL = (String)inputs.get(ActiveRecordConstants.key_finder_sql);
            int offset = Util.getIntValue(options, DataProcessor.input_key_records_offset, 0);
            int limit = Util.getIntValue(options, DataProcessor.input_key_records_limit, DataProcessor.NO_ROW_LIMIT);
            
            TableData td = getSqlService().retrieveRows(inputs, 
                                                        DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, 
                                                        findSQL, 
                                                        limit, 
                                                        offset);
            
            if (td != null) {
                list = sqlHelper.organizeData(td);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new BaseSQLException(ex);
        }

        return (list != null)?list:(new ArrayList());
    }
    
    
    /**
     * 
     * DELETE related
     * 
     */
    
    
    /**
     * deletes the record with the given id. 
     * 
     * If there is no column name like "ID", an exception will be thrown.
     * 
     * @param id key to the object with field name "ID"
     * @return int number of records deleted
     */
    public int deleteById(Object id) {
        if (!home.getTableInfo().getHeader().isValidColumnName("ID")) {
            throw new IllegalArgumentException("There is no column name as ID");
        }
        
        String deleteSQL = "DELETE FROM " + home.getTableName() + " WHERE id = ?";
       return SqlServiceClient.executeSQL(deleteSQL);
    }
    
    /**
     * deletes the record with the given data map containing primary keys.
     * 
     * If not all primary key columns have data, an exception will be thrown.
     * 
     * @return int number of records deleted
     */
    public int deleteByPrimaryKeyMap(Map dataMap) {
        if (dataMap == null || dataMap.size() == 0) return -1;
        
        //construct a map of primary keys
        Map pkMap = new HashMap();
        String[] pkNames = home.getPrimaryKeyNames();
        int length = pkNames.length;
        for (int i=0; i<length; i++) {
            String name = pkNames[i];
            Object data = Util.decode(name, dataMap, null, true);
            if (data == null) {
                RequiredDataMissingException rdmEx = new RequiredDataMissingException();
                rdmEx.setRequiredDataName(name);
                throw rdmEx;
            }
            pkMap.put(name, data);
        }
        
        return deleteAll(pkMap);
    }
    
    /**
     * deletes all the records that satisfy the sql.
     * 
     * @param sql    a key to a sql string
     * @return int number of records deleted
     */
    public static int deleteBySQL(String sql) {
        return deleteBySQL(sql, null);
    }
    
    /**
     * deletes all the records that satisfy the sql. 
     * 
     * The inputs is a map of name and value pairs related to the sql. 
     * 
     * @param sql    a key to a sql string
     * @param inputs a map of name and value pairs
     * @return int number of records deleted
     */
    public static int deleteBySQL(String sql, Map inputs) {
       return SqlServiceClient.executeSQL(sql, inputs);
    }
    
    /**
     * deletes all the records that satisfy the sql specified by the <tt>sqlKey</tt>. 
     * 
     * @param sqlKey a key to a sql string
     * @return int number of records deleted
     */
    public static int deleteBySQLKey(String sqlKey) {
        return deleteBySQLKey(sqlKey, null);
    }
    
    /**
     * deletes all the records that satisfy the sql specified by the <tt>sqlKey</tt>. 
     * 
     * The inputs is a map of name and value pairs related to the sql. 
     * 
     * @param sqlKey a key to a sql string
     * @param inputs a map of name and value pairs
     * @return int number of records deleted
     */
    public static int deleteBySQLKey(String sqlKey, Map inputs) {
       return SqlServiceClient.executeSQLByKey(sqlKey, inputs);
    }
    
    /**
     * <p>Deletes all the records that satisfy the condition.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> examples.</p>
     * 
     * @param conditions a map of column name and value pairs
     * @return int number of records deleted
     */
    public int deleteAll(Map conditions) {
        return internal_deleteAll(conditions);
    }
    
    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @return int number of records deleted
     */
    public int deleteAll(String conditionsSQL) {
        return deleteAll(conditionsSQL, null);
    }
    
    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return int number of records deleted
     */
    public int deleteAll(String conditionsSQL, Map conditionsSQLData) {
        return internal_deleteAll(conditionsSQL, conditionsSQLData);
    }
    
    private int internal_deleteAll(Map conditions) {
        int count = -1;
        String deleteSQL = "DELETE FROM " + home.getTableName();
        
        try {
            Map inputs = new HashMap();
            
            //construct where clause
            if (conditions != null && conditions.size() > 0) {
            	String whereClause = " WHERE ";
                int position = 1;
                for(Iterator it = conditions.keySet().iterator(); it.hasNext();) {
                    String columnName = (String) it.next();
                    Object conditionData = conditions.get(columnName);
                    whereClause += columnName + " = ? AND ";
                    inputs.put(position+"", conditionData);
                    
                    position = position + 1;
                }
                
                if (whereClause.endsWith("AND ")) {
                    int lastAnd = whereClause.lastIndexOf("AND ");
                    whereClause = whereClause.substring(0, lastAnd);
                }
                
                deleteSQL += whereClause;
            }
            
            count = SqlServiceClient.executeSQL(deleteSQL, inputs);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return count;
    }
    
    private int internal_deleteAll(String conditionsSQL, Map conditionsSQLData) {
        int count = -1;
        String deleteSQL = "DELETE FROM " + home.getTableName();
        
        try {
            Map inputs = new HashMap();
            
            //construct where clause
            if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
                deleteSQL += " WHERE " + conditionsSQL;
                if (conditionsSQLData != null) {
                	inputs.putAll(conditionsSQLData);
                }
            }
            
            count = SqlServiceClient.executeSQL(deleteSQL, inputs);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return count;
    }
    
    
    /**
     * 
     * UPDATE related
     * 
     */
     
    
    /**
     * <p>Updates all the records of a table.</p>
     * 
     * <p><tt>fieldData</tt> map is used to construct SET clause of the 
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     * 
     * @param fieldData a map of field name and its data to be set on any records
     * @return int number of records updated
     */
    public int updateAll(Map fieldData) {
        return updateAll(fieldData, null, null);
    }
    
    /**
     * <p>Updates all the records that satisfy a set of conditions supplied.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
     * 
     * <p><tt>fieldData</tt> map is used to construct SET clause of the 
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     * 
     * @param fieldData a map of field name and its data to be set on any 
     *          records that satisfy the conditions. 
     * @param conditionsSQL A SQL fragment string
     * @return int number of records updated
     */
    public int updateAll(Map fieldData, String conditionsSQL) {
        return updateAll(fieldData, conditionsSQL, null);
    }
     
    /**
     * <p>Updates all the records that satisfy the conditions.</p>
     * 
     * <p>See {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
     * 
     * <p><tt>fieldData</tt> map is used to construct SET clause of the 
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     * 
     * @param fieldData a map of field name and its data to be set on any 
     *          records that satisfy the conditions. 
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return int number of records updated
     */
    public int updateAll(Map fieldData, String conditionsSQL, Map conditionsSQLData) {
        if (fieldData == null || fieldData.size() == 0) 
            throw new IllegalArgumentException("fieldData cannot be empty for updateAll()");

        int count = -1;
        String updateSQL = "UPDATE " + home.getTableName();
        
        try {
            Map inputs = new HashMap();
            StringBuffer strBuffer = new StringBuffer();
            ColumnInfo ci = null;
            RowInfo ri = home.getRowInfo();
            Iterator it = fieldData.keySet().iterator();
            while(it.hasNext()) {
                String field = (String)it.next();
                ci = ri.getColumnInfo(field);
                if (!ri.isValidColumnName(field) || 
                    ci.isReadOnly() || !ci.isWritable() || ci.isPrimaryKey()) continue;
                
                String token = getUniqueToken(field, conditionsSQLData, true);
                strBuffer.append(field).append(" = ?").append(token).append(", ");
                inputs.put(token, fieldData.get(field));
            }
            
            updateSQL += " SET " + StringUtil.removeLastToken(strBuffer, ", ");
            
            if (conditionsSQL != null) {
                updateSQL += " WHERE " + conditionsSQL;
            }
            
            if (conditionsSQLData != null) {
                inputs.putAll(conditionsSQLData);
            }
            
            count = SqlServiceClient.executeSQL(updateSQL, inputs);
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return count;
    }
    
    private String getUniqueToken(String field, Map conditionsSQLData, boolean convertToUpper) {
        if (conditionsSQLData == null || conditionsSQLData.size() == 0) return field;
        
        Map conditionsSQLDataCopy = conditionsSQLData;
        if (convertToUpper) {
            conditionsSQLDataCopy = new HashMap(conditionsSQLData.size());
            Iterator it = conditionsSQLData.keySet().iterator();
            while(it.hasNext()) {
                String key = (String)it.next();
                conditionsSQLDataCopy.put(key.toUpperCase(), conditionsSQLData.get(key));
            }
        }
        
        if (conditionsSQLDataCopy.containsKey(field.toUpperCase())) {
            field = "_" + field;
            return getUniqueToken(field, conditionsSQLDataCopy, false);
        }
        return field;
    }
    
    /**
     * updates all the records that satisfy the sql. 
     * 
     * @param sql A valid sql string
     * @return int number of records updated
     */
    public static int updateBySQL(String sql) {
        return updateBySQL(sql, new HashMap());
    }
    
    /**
     * updates all the records that satisfy the sql. 
     * 
     * @param sql A valid sql string
     * @param inputs a map of name and value pairs
     * @return int number of records updated
     */
    public static int updateBySQL(String sql, Map inputs) {
       return SqlServiceClient.executeSQL(sql, inputs);
    }
    
    /**
     * updates all the records that satisfy the sql specified by <tt>sqlKey</tt>. 
     * 
     * @param sqlKey a key to a sql string.
     * @return int number of records updated
     */
    public static int updateBySQLKey(String sqlKey) {
        return updateBySQLKey(sqlKey, new HashMap());
    }
    
    /**
     * updates all the records that satisfy the sql specified by <tt>sqlKey</tt>. 
     * 
     * @param sqlKey a key to a sql string
     * @param inputs a map of name and value pairs
     * @return int number of records updated
     */
    public static int updateBySQLKey(String sqlKey, Map inputs) {
       return SqlServiceClient.executeSQLByKey(sqlKey, inputs);
    }
    
    
    
    
    private static SqlService getSqlService() {
        return SqlServiceConfig.getSqlService();
    }
}
