/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Map;

import com.scooterframework.orm.sqldataexpress.util.SqlUtil;


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
	public static String getAllSelectQueryClauses(Map<String, String> options) {
		String s = "";
        s += SqlUtil.getGroupBy(options);
        s += SqlUtil.getHaving(options);
        s += SqlUtil.getOrderBy(options);
        return s;
	}
}
