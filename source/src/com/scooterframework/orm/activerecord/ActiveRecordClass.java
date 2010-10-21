/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.List;
import java.util.Map;

import com.scooterframework.orm.sqldataexpress.object.RowInfo;

/**
 * <p>
 * ActiveRecord defines static operations that each subclass of 
 * the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
 * class must implement.
 * </p>
 *
 * @author (Fei) John Chen
 */
public abstract class ActiveRecordClass {

    /************************************************************************
     *
     * The following static methods declaration should be implemented in 
     * subclass by method injection.
     *
     ************************************************************************/
    
	private static String UO = "This method should be invoked from a subclass of ActiveRecord which implements the method.";

    /**
     *
     * CREATE related
     *
     */
    
    /**
     * Creates a new instance.
     */
    public static ActiveRecord newRecord() {
        throw new UnsupportedOperationException(UO);
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
    public static ActiveRecord findById(Object id) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     *
     * @param sql       a valid SQL query string
     * @return a list of ActiveRecord objects
     */
    public static List findAllBySQL(String sql) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     *
     * @param sql       a valid SQL query string
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public static List findAllBySQL(String sql, Map inputs) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the sql corresponding to the
     * sql key. </p>
     *
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @return a list of ActiveRecord objects
     */
    public static List findAllBySQLKey(String sqlKey) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the sql corresponding to the
     * sql key. </p>
     *
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public static List findAllBySQLKey(String sqlKey, Map inputs) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of this class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public static ActiveRecord findFirstBy(String columns, Object[] values) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of this class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public static ActiveRecord findLastBy(String columns, Object[] values) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of this class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  List of ActiveRecord objects
     */
    public static List findAllBy(String columns, Object[] values) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of this class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a map of options
     * @return  List of ActiveRecord objects
     */
    public static List findAllBy(String columns, Object[] values, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of this class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a string of options
     * @return  List of ActiveRecord objects
     */
    public static List findAllBy(String columns, Object[] values, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records of a table.</p>
     *
     * @return a list of ActiveRecord objects
     */
    public static List findAll() {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @return a list of ActiveRecord objects
     */
    public static List findAll(Map conditions) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return a list of ActiveRecord objects
     */
    public static List findAll(Map conditions, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return a list of ActiveRecord objects
     */
    public static List findAll(Map conditions, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @return a list of ActiveRecord objects
     */
    public static List findAll(String conditionsSQL) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return a list of ActiveRecord objects
     */
    public static List findAll(String conditionsSQL, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and
     * <tt>conditionsSQLData</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return a list of ActiveRecord objects
     */
    public static List findAll(String conditionsSQL, Map conditionsSQLData) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return a list of ActiveRecord objects
     */
    public static List findAll(String conditionsSQL, Map conditionsSQLData, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return a list of ActiveRecord objects
     */
    public static List findAll(String conditionsSQL, Map conditionsSQLData, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record of a table.</p>
     *
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst() {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(Map conditions) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(Map conditions, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(Map conditions, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(String conditionsSQL) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(String conditionsSQL, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and
     * <tt>conditionsSQLData</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return the first ActiveRecord found
     */
    public static ActiveRecord findFirst(String conditionsSQL, Map conditionsSQLData, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record of a table.</p>
     *
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast() {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(Map conditions) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a map of options
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(Map conditions, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditions a map of column name and value pairs
     * @param options a string of options
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(Map conditions, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(String conditionsSQL) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param options a string of options.
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(String conditionsSQL, String options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> and
     * <tt>conditionsSQLData</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a map of options.
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData, Map options) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions and options.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>,
     * <tt>conditionsSQLData</tt> and <tt>options</tt> examples.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @param options a string of options.
     * @return the last ActiveRecord found
     */
    public static ActiveRecord findLast(String conditionsSQL, Map conditionsSQLData, String options) {
        throw new UnsupportedOperationException(UO);
    }

    
    /**
     *
     * UPDATE related
     *
     */
    
    /**
     * <p>Updates all the records of a table.</p>
     *
     * <p>This method sends a single SQL UPDATE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>update()</tt> method.</p>
     *
     * <p><tt>fieldData</tt> map is used to construct SET clause of the
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     *
     * @param fieldData a map of field name and its data to be set on any records
     * @return int number of records updated
     */
    public static int updateAll(Map fieldData) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Updates all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> examples.</p>
     *
     * <p>This method sends a single SQL UPDATE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>update()</tt> method.</p>
     *
     * <p><tt>fieldData</tt> map is used to construct SET clause of the
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     *
     * @param fieldData a map of field name and its data to be set on any
     *          records that satisfy the conditions.
     * @param conditionsSQL a SQL fragment string
     * @return int number of records updated
     */
    public static int updateAll(Map fieldData, String conditionsSQL) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Updates all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>
     * and <tt>conditionsSQLData</tt> examples.</p>
     *
     * <p>This method sends a single SQL UPDATE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>update()</tt> method.</p>
     *
     * <p><tt>fieldData</tt> map is used to construct SET clause of the
     * generated SQL. It consists of column name and its value pairs in the map.
     * Primary key column and read-only columns are not updatable.</p>
     *
     * @param fieldData a map of field name and its data to be set.
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return int number of records updated
     */
    public static int updateAll(Map fieldData, String conditionsSQL, Map conditionsSQLData) {
        throw new UnsupportedOperationException(UO);
    }


    /**
     *
     * DELETE related
     *
     */

    /**
     * Deletes the record with the given id.
     *
     * If there is no column name like "ID", an exception will be thrown.
     *
     * @param id key to the object with field name "ID"
     * @return int number of records deleted
     */
    public static int deleteById(Object id) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * Deletes the record with the given data map containing primary keys.
     *
     * If not all primary key columns have data, an exception will be thrown.
     *
     * @param inputs a map of name and value pairs
     * @return int number of records deleted
     */
    public static int deleteByPrimaryKeyMap(Map inputs) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditions</tt> examples.</p>
     *
     * <p>This method sends a single SQL DELETE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>delete()</tt> method.</p>
     *
     * @param conditions a map of column name and value pairs
     * @return int number of records deleted
     */
    public static int deleteAll(Map conditions) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt> examples.</p>
     *
     * <p>This method sends a single SQL DELETE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>delete()</tt> method.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @return int number of records deleted
     */
    public static int deleteAll(String conditionsSQL) {
        throw new UnsupportedOperationException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of this class for <tt>conditionsSQL</tt>
     * and <tt>conditionsSQLData</tt> examples.</p>
     *
     * <p>This method sends a single SQL DELETE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>delete()</tt> method.</p>
     *
     * @param conditionsSQL a SQL fragment string
     * @param conditionsSQLData a data map for dynamic attributes in <tt>conditionsSQL</tt>
     * @return int number of records deleted
     */
    public static int deleteAll(String conditionsSQL, Map conditionsSQLData) {
        throw new UnsupportedOperationException(UO);
    }


    /**
     *
     * Calculator related
     *
     */
    
    /**
     * Counts number of records.
     * 
     * @return number of records.
     */
    public static long count() {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @return number of records.
     */
    public static long count(String field) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @param options options for calculation
     * @return number of records.
     */
    public static long count(String field, String options) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object sum(String field) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object sum(String field, String options) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object average(String field) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object average(String field, String options) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object maximum(String field) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object maximum(String field, String options) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates minimum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object minium(String field) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates minimum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object minium(String field, String options) {
        throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Calculates by a function on a field.
     * 
     * @param function the sql function name
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object calculate(String function, String field, String options) {
        throw new UnsupportedOperationException(UO);
    }


    /**
     *
     * Meta info
     *
     */
    
    /**
     * Returns the home instance of the record type. The home instance is the
     * singleton instance that represents the record type.
     */
    public static ActiveRecord homeInstance() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns a list of primary key column names.
     */
    public static List primaryKeyNames() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns a list of read-only column names.
     */
    public static List readOnlyColumnNames() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns a list of column names.
     */
    public static List columnNames() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns a list of <tt>{@link com.scooterframework.orm.sqldataexpress.object.ColumnInfo ColumnInfo}</tt> instances.
     */
    public static List columns() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns the <tt>{@link com.scooterframework.orm.sqldataexpress.object.RowInfo RowInfo}</tt> of the record.
     */
    public static RowInfo rowInfo() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns the table name of associated with the record type.
     */
    public static String tableName() {
    	throw new UnsupportedOperationException(UO);
    }
    
    /**
     * Returns the simple table name of associated with the record type.
     */
    public static String simpleTableName() {
    	throw new UnsupportedOperationException(UO);
    }
    
    


    /**
     *
     * OTHER methods to be inserted in subclass
     *
     */


    /************************************************************************
     *
     * End of static methods declaration for subclass
     *
     ************************************************************************/
}
