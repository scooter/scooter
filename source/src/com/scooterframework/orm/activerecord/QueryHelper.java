/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Map;


/**
 * QueryHelper class provides helper methods for query construction.
 * 
 * @author (Fei) John Chen
 */
public class QueryHelper {
	
	/**
	 * Returns a query clause for <tt>SELECT</tt> statement by constructing 
	 * a clause which consists of <tt>GROUP BY</tt>, <tt>HAVING</tt> and 
	 * <tt>ORDER BY</tt> clauses if there is any as specified in the <tt>options</tt>.
	 * 
	 * @param options
	 * @return a SQL fragment
	 */
	public static String getAllSelectQueryClauses(Map options) {
		String s = "";
        s += QueryHelper.getGroupBy(options);
        s += QueryHelper.getHaving(options);
        s += QueryHelper.getOrderBy(options);
        return s;
	}

    /**
     * Constructs <tt>ORDER BY</tt> clause in a SQL statement. Empty string 
     * is returned if there is no options for this clause.
     * 
     * @param options
	 * @return a SQL fragment
     */
    public static String getOrderBy(Map options) {
        String orderByClause = "";
        
        if (options != null && options.size() > 0) {
            String order_by = (String)options.get(ActiveRecordConstants.key_order_by);
            String sort = (String)options.get(ActiveRecordConstants.key_sort);
            String order = (String)options.get(ActiveRecordConstants.key_order);
            if (order_by == null || "".equals(order_by)) {
                if (sort != null && !"".equals(sort)) {
                    orderByClause += " ORDER BY " + sort;
                    if (order != null && "down".equalsIgnoreCase(order)) {
                        orderByClause += " DESC";
                    }
                }
            }
            else {
                orderByClause += " ORDER BY " + order_by;
            }
        }
        
        return orderByClause;
    }
    
    /**
     * Constructs <tt>GROUP BY</tt> clause in a SQL statement. Empty string 
     * is returned if there is no options for this clause.
     * 
     * @param options
	 * @return a SQL fragment
     */
    public static String getGroupBy(Map options) {
        String groupByClause = "";
        
        if (options != null && options.size() > 0) {
            String group_by = (String)options.get(ActiveRecordConstants.key_group_by);
            if (group_by != null && !"".equals(group_by)) {
                groupByClause += " GROUP BY " + group_by;
            }
        }
        
        return groupByClause;
    }
    
    /**
     * Constructs <tt>HAVING</tt> clause in a SQL statement. Empty string 
     * is returned if there is no options for this clause.
     * 
     * @param options
	 * @return a SQL fragment
     */
    public static String getHaving(Map options) {
        String havingClause = "";
        
        if (options != null && options.size() > 0) {
            String having = (String)options.get(ActiveRecordConstants.key_having);
            if (having != null && !"".equals(having)) {
                havingClause += " HAVING " + having;
            }
        }
        
        return havingClause;
    }
}
