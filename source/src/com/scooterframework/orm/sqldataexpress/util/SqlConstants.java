/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;


/**
 * SqlConstants class holds all constants used in SQL Data Express.
 * 
 * @author (Fei) John Chen
 */
public class SqlConstants {
    
    /**
     * Key <tt>table</tt> key represents a database connection name.
     */
    public static final String key_database = "database";
    
    /**
     * Key <tt>table</tt> key represents a table name.
     */
    public static final String key_table = "table";
    
    /**
     * Key <tt>table</tt> key represents a view name.
     */
    public static final String key_view = "view";

    /**
     * <p>Key <tt>group_by</tt> represents <tt>GROUP BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>group_by=id, name</tt>" will be translated to SQL 
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
     * SQL query as "HAVING sum(price)<100".</p>
     */
    public static final String key_having = "having";
    
    /**
     * <p>Key <tt>order_by</tt> represents <tt>ORDER BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>order_by=age desc</tt>" will be translated to SQL 
     * query as "ORDER BY age desc".</p>
     */
    public static final String key_order_by = "order_by";
    
    /**
     * <p>Key <tt>sort</tt> indicates column names to sort. </p>
     * 
     * <p>For example, "<tt>sort=first_name</tt>" will be translated to SQL 
     * query as "<tt>order by first_name</tt>".</p>
     */
    public static final String key_sort = "sort";
    
    /**
     * <p>Key <tt>order</tt> represents direction of sort. If the query 
     * result set is in descending order, use value "<tt>Down</tt>". Otherwise 
     * by default the query results are in ascending order.</p>
     */
    public static final String key_order = "order";
}
