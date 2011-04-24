/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;


/**
 * ActiveRecordConstants class holds all constants used in ActiveRecord.
 * 
 * @author (Fei) John Chen
 */
public class ActiveRecordConstants {
    
    /**
     * Key <tt>model</tt> key represents a model name.
     */
    public static final String key_model = "model";
    
    /**
     * Key <tt>source</tt> key represents the actual association in the join 
     * model of a has-many-through relation.
     */
    public static final String key_source = "source";
    
    /**
     * Key <tt>mapping</tt> key. It must be a string of name and value pairs 
     * separated by comma. For example, "<tt>id=customer_id</tt>" or 
     * "<tt>id=order_id, type=order_type</tt>".
     */
    public static final String key_mapping = "mapping";
    
    /**
     * <p>Key <tt>finder_sql</tt> represents select query to be used with other 
     * conditions and options in find related methods. </p>
     * 
     * <p>By using <tt>finder_sql</tt>, customerized SQL queries can be used. 
     * For example, <tt>hint</tt>.</p>
     */
    public static final String key_finder_sql = "finder_sql";
    
    /**
     * <tt>custom_sql</tt> represents customerized SQL query.
     */
    public static final String key_custom_sql = "custom_sql";
    
    /**
     * Key <tt>custom_sql_key</tt> represents customized SQL query.
     */
    public static final String key_custom_sql_key = "custom_sql_key";
    
    /**
     * <p>Key <tt>include</tt> is used to eagerly load associated target 
     * entities through left outer joins. </p>
     * 
     * <p>For example, "<tt>include: category, user</tt>" means find method will 
     * eagerly load associated <tt>category</tt> and <tt>user</tt> entities 
     * by using left outer joins. </p>
     */
    public static final String key_include = "include";
    
    /**
     * <p>Key <tt>key_strict_include</tt> is used to eagerly load associated 
     * target entities through inner joins. </p>
     * 
     * <p>For example, "<tt>include: category, user</tt>" means find method will 
     * eagerly load associated <tt>category</tt> and <tt>user</tt> entities 
     * by using inner joins.</p>
     */
    public static final String key_strict_include = "strict_include";
    
    /**
     * <p>Key <tt>join_type</tt> indicates the type of joins. Allowed values are 
     * relaxed and strict.</p>
     * <ul>
     * <li>relaxed: left-outer-join will be used. (default)</li>
     * <li>strict: inner-join will be used.</li>
     * </ul>
     */
    public static final String key_join_type = "join_type";
    
    /**
     * <p>Key <tt>conditions_sql</tt> represents extra conditional SQL string 
     * to be appended to a query.</p>
     * 
     * <p>For example, "<tt>conditions_sql: name='John'</tt>" will be appended to 
     * the end of a query string as "<tt> AND name='John'</tt>".</p>
     * 
     * <p>
     * Note: Because equal sign '=' may appear in the conditional sql, you can 
     * only use this key with a map. Therefore the use of this key is restricted
     * to those find and findAll methods that accept an inputOption map as an 
     * input parameter.</p>
     */
    public static final String key_conditions_sql = "conditions_sql";
    
    /**
     * <p>Key <tt>unique</tt> indicates if query results should be unique. 
     * This will add "<tt>distinct</tt>" in SQL query. </p>
     * 
     * <p>For example, "<tt>unique=true</tt>" will be translated to 
     * "<tt>SELECT DISTINCT</tt>" in a SQL query. </p>
     */
    public static final String key_unique = "unique";
    
    /**
     * Key <tt>cascade</tt> representing cascade property.
     */
    public static final String key_cascade = "cascade";
    
    /**
     * Key <tt>columns</tt> indicates the column names to be used for a
     * query.
     */
    public static final String key_columns = "columns";
    
    /**
     * Key <tt>ex_columns</tt> indicates the column names not to be used 
     * for a query.
     */
    public static final String key_ex_columns = "ex_columns";
    
    /**
     * Key <tt>counter_cache</tt> indicates that the parent model will keep 
     * a count of child model records.
     */
    public static final String key_counter_cache = "counter_cache";
    
    /**
     * Key <tt>limit</tt> indicates the number of records for a query.
     */
    public static final String key_limit = "limit";
    
    /**
     * Key <tt>offset</tt> indicates the number of records to skip for a query.
     */
    public static final String key_offset = "offset";
    
    /**
     * Key <tt>page</tt> indicates current page number in a pagination.
     */
    public static final String key_page = "page";
}
