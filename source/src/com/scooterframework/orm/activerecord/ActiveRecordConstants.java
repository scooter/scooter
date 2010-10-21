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
     * <p>By using <tt>finder_sql</tt>, customerized sql queries can be used. 
     * For example, <tt>hint</tt>.</p>
     */
    public static final String key_finder_sql = "finder_sql";
    
    /**
     * <tt>custom_sql</tt> represents customerized sql query.
     */
    public static final String key_custom_sql = "custom_sql";
    
    /**
     * Key <tt>custom_sql_key</tt> represents customized sql query.
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
     * <p>Key <tt>conditions_sql</tt> represents extra conditional sql string 
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
     * <p>Key <tt>group_by</tt> represents <tt>GROUP BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>group_by=id, name</tt>" will be translated to sql 
     * query as "GROUP BY id, name".</p>
     */
    public static final String key_group_by = "group_by";
    
    /**
     * <p>Key <tt>having</tt> represents <tt>HAVING</tt> clause in sql. This is 
     * usually used with <tt>group_by</tt> together.</p>
     * 
     * <p>The <tt>HAVING</tt> clause was added to SQL because the 
     * <tt>WHERE</tt> keyword could not be used with aggregate functions.</p>
     * 
     * <p>For example, "<tt>having=sum(price)<100</tt>" will be translated to 
     * sql query as "HAVING sum(price)<100".</p>
     */
    public static final String key_having = "having";
    
    /**
     * <p>Key <tt>order_by</tt> represents <tt>ORDER BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>order_by=age desc</tt>" will be translated to sql 
     * query as "ORDER BY age desc".</p>
     */
    public static final String key_order_by = "order_by";
    
    /**
     * <p>Key <tt>sort</tt> indicates column names to sort. </p>
     * 
     * <p>For example, "<tt>sort=first_name</tt>" will be translated to sql 
     * query as "<tt>order by first_name</tt>".</p>
     */
    public static final String key_sort = "sort";
    
    /**
     * <p>Key <tt>order</tt> represents direction of sort. If the query 
     * result set is in descending order, use value "<tt>Down</tt>". Otherwise 
     * by default the query results are in ascending order.</p>
     */
    public static final String key_order = "order";
    
    /**
     * <p>Key <tt>unique</tt> indicates if query results should be unique. 
     * This will add "<tt>distinct</tt>" in sql query. </p>
     * 
     * <p>For example, "<tt>unique=true</tt>" will be translated to 
     * "<tt>SELECT DISTINCT</tt>" in a sql query. </p>
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
}
