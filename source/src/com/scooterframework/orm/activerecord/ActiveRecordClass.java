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

import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;

/**
 * <p>
 * ActiveRecord defines static operations that each subclass of 
 * the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
 * class must implement. Scooter automatically add this method into the 
 * compiled model (subclass of ActiveRecord) class in a web container. 
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
    
	private static String UO = "This method should be invoked from a " + 
		"subclass of ActiveRecord which implements the method. Scooter " +
		"automatically add this method to the compiled subclass of " +
		"ActiveRecord class.";

    /**
     *
     * CREATE related
     *
     */
    
    /**
     * Creates a new instance.
     * 
     * <p>The created instance is based on meta info for table as returned by
     * <tt>{@link com.scooterframework.orm.activerecord.ActiveRecord#getTableName()}</tt> 
     * and database connection name as returned by
     * <tt>{@link com.scooterframework.orm.activerecord.ActiveRecord#getConnectionName()}</tt>.</p>
     * 
     * <p>See <tt>{@link com.scooterframework.orm.activerecord.ActiveRecord#ActiveRecord(String connectionName, String tableName)}</tt>.
     */
    public static <T extends ActiveRecord> T newRecord() {
        throw new RuntimeException(UO);
    }

    /**
     *
     * QueryBuilder related
     *
     */
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL  a valid SQL query where clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder where(String conditionsSQL) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL      a valid SQL query where clause string
     * @param conditionsSQLData  an array of data for the <tt>conditionsSQL</tt> string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder where(String conditionsSQL, Object[] conditionsSQLData) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL         a valid SQL query where clause string
     * @param conditionsSQLDataMap  a map of data for the keys in the <tt>conditionsSQL</tt> string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder where(String conditionsSQL, Map<String, Object> conditionsSQLDataMap) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup associated models for eager loading.</p>
     *
     * @param includes  a string of associated models
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder includes(String includes) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup associated models for eager loading.</p>
     *
     * @param includes  a string of associated models
     * @param joinType  type of join
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder includes(String includes, String joinType) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup associated models for eager loading.</p>
     * 
     * <p>If <tt>strict</tt> is true, then child records can only be accessed 
     * through their parent.</p>
     *
     * @param includes  a string of associated models
     * @param strict    true if strict
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder includes(String includes, boolean strict) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup group-by clause.</p>
     *
     * @param groupBy  a valid SQL query group-by clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder groupBy(String groupBy) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup having clause.</p>
     *
     * @param having  a valid SQL query having clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder having(String having) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup group-by clause.</p>
     *
     * @param orderBy  a valid SQL query order-by clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder orderBy(String orderBy) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup limit for number of records per retrieval.</p>
     *
     * @param limit  number of records for each retrieval
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder limit(int limit) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup number of records to skip.</p>
     *
     * @param offset  number of records to skip
     * @return current <tt>QueryBuilder</tt> instance
     */
    public static QueryBuilder offset(int offset) {
    	throw new RuntimeException(UO);
    }
    
    /**
     * <p>Setup current page number. 
     * All records in previous pages are skipped.</p>
     *
     * @param page  current page number
     * @return current <tt>QueryBuilder</tt> instance
     */
	public static QueryBuilder page(int page) {
    	throw new RuntimeException(UO);
	}

    /**
     *
     * FIND related
     *
     */

    /**
     * Finds the record with the given id, assuming ID is the primary key 
     * column.
     *
     * If there is no column name like "ID", an exception will be thrown.
     *
     * @param id the id of the record
     * @return the ActiveRecord associated with the <tt>id</tt>
     */
    public static <T extends ActiveRecord> T findById(long id) {
        throw new RuntimeException(UO);
    }

    /**
     * Finds the record with the given id, assuming ID is the primary key 
     * column.
     *
     * If there is no column name like "ID", an exception will be thrown.
     *
     * @param id the id of the record
     * @return the ActiveRecord associated with the <tt>id</tt>
     */
    public static <T extends ActiveRecord> T findById(Object id) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Finds the record with the given <tt>restfulId</tt>.
     * 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord#getRestfulId()} 
     * for definition of RESTfulId.
     *
     * If there is no primary key, a null record is returned.
     *
     * @param restfulId  the RESTful id of the record
     * @return the ActiveRecord associated with the <tt>restfulId</tt>
     */
    public static <T extends ActiveRecord> T findByRESTfulId(String restfulId) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Finds the record with the given <tt>pkString</tt>. This method is the
     * same as the {@link #findByRESTfulId findByRESTfulId} method.
     * 
     * See {@link com.scooterframework.orm.activerecord.ActiveRecord#getRestfulId()} 
     * for definition of RESTfulId which is the same as the primary key string.
     *
     * If there is no primary key, a null record is returned.
     *
     * @param pkString  the primary key string of the record
     * @return the ActiveRecord associated with the <tt>pkString</tt>
     */
    public static <T extends ActiveRecord> T findByPK(String pkString) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     *
     * @param sql       a valid SQL query string
     * @return a list of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBySQL(String sql) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the SQL query.</p>
     *
     * @param sql       a valid SQL query string
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBySQL(String sql, Map<String, Object> inputs) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the sql corresponding to the
     * sql key. </p>
     *
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @return a list of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBySQLKey(String sqlKey) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the sql corresponding to the
     * sql key. </p>
     *
     * @param sqlKey    a key to a sql string defined in <tt>sql.properties</tt> file
     * @param inputs    a map of name and value pairs
     * @return a list of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBySQLKey(String sqlKey, Map<String, Object> inputs) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds the first record that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public static <T extends ActiveRecord> T findFirstBy(String columns, Object[] values) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds the last record that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  ActiveRecord
     */
    public static <T extends ActiveRecord> T findLastBy(String columns, Object[] values) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @return  List of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBy(String columns, Object[] values) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a map of options
     * @return  List of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBy(String columns, Object[] values, Map<String, Object> options) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records that satisfy the conditions.</p>
     *
     * <p>This is a dynamic finder method.
     * See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for dynamic finder examples.</p>
     *
     * @param columns  a string of column names linked by "_and_".
     * @param values   an Object[] array
     * @param options  a string of options
     * @return  List of ActiveRecord objects
     */
    public static List<ActiveRecord> findAllBy(String columns, Object[] values, String options) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds all the records of a table.</p>
     *
     * @return a list of ActiveRecord objects
     */
    public static List<ActiveRecord> findAll() {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds the first record of a table.</p>
     *
     * @return the first ActiveRecord found
     */
    public static <T extends ActiveRecord> T findFirst() {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Finds the last record of a table.</p>
     *
     * @return the last ActiveRecord found
     */
    public static <T extends ActiveRecord> T findLast() {
        throw new RuntimeException(UO);
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
    public static int updateAll(Map<String, Object> fieldData) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Updates all the records that satisfy the conditions.</p>
     *
     * <p>See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
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
    public static int updateAll(Map<String, Object> fieldData, String conditionsSQL) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Updates all the records that satisfy the conditions.</p>
     *
     * <p>See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
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
    public static int updateAll(Map<String, Object> fieldData, String conditionsSQL, Map<String, Object> conditionsSQLData) {
        throw new RuntimeException(UO);
    }


    /**
     *
     * DELETE related
     *
     */

    /**
     * Deletes the record with the given id, assuming ID is the primary key 
     * column.
     *
     * If there is no column name like "ID", an exception will be thrown.
     *
     * @param id key to the object with field name "ID"
     * @return int number of records deleted
     */
    public static int deleteById(long id) {
        throw new RuntimeException(UO);
    }

    /**
     * Deletes the record with the given id, assuming ID is the primary key 
     * column.
     *
     * If there is no column name like "ID", an exception will be thrown.
     *
     * @param id key to the object with field name "ID"
     * @return int number of records deleted
     */
    public static int deleteById(Object id) {
        throw new RuntimeException(UO);
    }

    /**
     * Deletes the record with the given primary key string.
     * 
     * See <tt>{@link com.scooterframework.orm.activerecord.ActiveRecord#getPK()}</tt> 
     * for format of the primary key string.
     *
     * @param pkString  primary key string
     * @return int number of records deleted
     */
    public static int deleteByPK(String pkString) {
        throw new RuntimeException(UO);
    }

    /**
     * Deletes the record with the given data map containing primary keys.
     *
     * If not all primary key columns have data, an exception will be thrown.
     *
     * @param inputs a map of name and value pairs
     * @return int number of records deleted
     */
    public static int deleteByPrimaryKeyMap(Map<String, Object> inputs) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditions</tt> examples.</p>
     *
     * <p>This method sends a single SQL DELETE statement to database. No
     * callbacks are triggered. If callbacks are necessary for handling
     * associations, use <tt>findAll</tt> method to retrieve a list of records
     * and then call each record's <tt>delete()</tt> method.</p>
     *
     * @param conditions a map of column name and value pairs
     * @return int number of records deleted
     */
    public static int deleteAll(Map<String, Object> conditions) {
        throw new RuntimeException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> examples.</p>
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
        throw new RuntimeException(UO);
    }

    /**
     * <p>Deletes all the records that satisfy the conditions.</p>
     *
     * <p>See top of the {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord} 
     * class for <tt>conditionsSQL</tt> and <tt>conditionsSQLData</tt> examples.</p>
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
    public static int deleteAll(String conditionsSQL, Map<String, Object> conditionsSQLData) {
        throw new RuntimeException(UO);
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
        throw new RuntimeException(UO);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @return number of records.
     */
    public static long count(String field) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @param options options for calculation
     * @return number of records.
     */
    public static long count(String field, String options) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object sum(String field) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object sum(String field, String options) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object average(String field) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object average(String field, String options) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object maximum(String field) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object maximum(String field, String options) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates minimum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public static Object minium(String field) {
        throw new RuntimeException(UO);
    }
    
    /**
     * Calculates minimum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object minium(String field, String options) {
        throw new RuntimeException(UO);
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
        throw new RuntimeException(UO);
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
    public static <T extends ActiveRecord> T homeInstance() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns a list of primary key column names.
     */
    public static List<String> primaryKeyNames() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns a list of read-only column names.
     */
    public static List<String> readOnlyColumnNames() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns a list of column names.
     */
    public static List<String> columnNames() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns a list of <tt>{@link com.scooterframework.orm.sqldataexpress.object.ColumnInfo ColumnInfo}</tt> instances.
     */
    public static List<ColumnInfo> columns() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns the <tt>{@link com.scooterframework.orm.sqldataexpress.object.RowInfo RowInfo}</tt> of the record.
     */
    public static RowInfo rowInfo() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns the connection name of associated with the record type.
     */
    public static String connectionName() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns the table name of associated with the record type.
     */
    public static String tableName() {
    	throw new RuntimeException(UO);
    }
    
    /**
     * Returns the simple table name of associated with the record type.
     */
    public static String simpleTableName() {
    	throw new RuntimeException(UO);
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
