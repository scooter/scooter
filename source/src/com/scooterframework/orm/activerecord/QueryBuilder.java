/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scooterframework.orm.misc.JdbcPageListSource;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.orm.sqldataexpress.util.SqlConstants;

/**
 * <p>QueryBuilder class provides flexible methods for building a query.</p>
 * 
 * @author (Fei) John Chen
 */
public class QueryBuilder {
	private TableGateway tg;
    private String conditionsSQL;
    private Map<String, Object> conditionsSQLDataMap;
    private Map<String, String> options = new HashMap<String, String>();
    
    private boolean usedWhere;
    private boolean usedIncludes;
    private boolean usedGroupBy;
    private boolean usedHaving;
    private boolean usedOrderBy;
    private boolean usedLimit;
    private boolean usedOffset;
    private boolean usedPage;
	
	QueryBuilder(TableGateway tg) {
        this.tg = tg;
	}

    /**
     * <p>Returns all the records that satisfy the query built by 
     * the <tt>QueryBuilder</tt>.</p>
     *
     * @return a list of ActiveRecord objects
     */
	public List<ActiveRecord> getRecords() {
		validateBuild();
		return tg.findAll(conditionsSQL, conditionsSQLDataMap, options);
	}

    /**
     * <p>Returns the record that satisfies the query built by 
     * the <tt>QueryBuilder</tt>.</p>
     *
     * @return the ActiveRecord object
     */
	public ActiveRecord getRecord() {
		validateBuild();
		return tg.findFirst(conditionsSQL, conditionsSQLDataMap, options);
	}

    /**
     * <p>Returns a Paginator that satisfy the query built by 
     * the <tt>QueryBuilder</tt>. Total number of records and paged list of 
     * records can be obtained from the returned Paginator instance.</p>
     *
     * @return a Paginator instance
     */
	public Paginator getPaginator() {
		validateBuild();
		Class<? extends ActiveRecord> modelClass = tg.getModelClass();
		
		Map<String, String> inputOptions = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : options.entrySet()) {
			String key = entry.getKey();
			if (Paginator.key_limit.equals(key) ||
					Paginator.key_offset.equals(key) ||
					Paginator.key_npage.equals(key)) continue;
			inputOptions.put(key, entry.getValue());
		}
		
		Map<String, String> pagingControl = new HashMap<String, String>();
		
		if (options.containsKey(ActiveRecordConstants.key_limit))
			pagingControl.put(Paginator.key_limit, options.get(ActiveRecordConstants.key_limit));
		
		if (options.containsKey(ActiveRecordConstants.key_offset))
			pagingControl.put(Paginator.key_offset, options.get(ActiveRecordConstants.key_offset));
		
		if (options.containsKey(ActiveRecordConstants.key_page))
			pagingControl.put(Paginator.key_npage, options.get(ActiveRecordConstants.key_page));
		
		return new Paginator(new JdbcPageListSource(modelClass, inputOptions), pagingControl);
	}
	
	private void validateBuild() {
		if (usedHaving && !usedGroupBy) {
			throw new RuntimeException("Group-by clause must be used with having clause.");
		}

		if (usedOffset && usedPage) {
			throw new RuntimeException("Page and offset cannot be set both at the same time.");
		}
	}
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL  a valid SQL query where clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder where(String conditionsSQL) {
        return where(conditionsSQL, (Map<String, Object>)null);
    }
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL      a valid SQL query where clause string
     * @param conditionsSQLData  an array of data for the <tt>conditionsSQL</tt> string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder where(String conditionsSQL, Object[] conditionsSQLData) {
		if (conditionsSQLData == null || conditionsSQLData.length == 0) {
			return where(conditionsSQL);
		}
		Map<String, Object> map = new HashMap<String, Object>(conditionsSQLData.length);
		int index = 1;
		for(Object o : conditionsSQLData) {
			map.put("" + index, o);
			index++;
		}
        return where(conditionsSQL, map);
    }
    
    /**
     * <p>Setup where clause.</p>
     *
     * @param conditionsSQL         a valid SQL query where clause string
     * @param conditionsSQLDataMap  a map of data for the keys in the <tt>conditionsSQL</tt> string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder where(String conditionsSQL, Map<String, Object> conditionsSQLDataMap) {
		if (usedWhere)
			throw new RuntimeException("where() can only be called once.");
		usedWhere = true;
		
		this.conditionsSQL = conditionsSQL;
		this.conditionsSQLDataMap = conditionsSQLDataMap;
        return this;
    }
    
    /**
     * <p>Setup associated models for eager loading.</p>
     *
     * @param includes  a string of associated models
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder includes(String includes) {
		if (usedIncludes)
			throw new RuntimeException("includes() can only be called once.");
		usedIncludes = true;
		
		options.put(ActiveRecordConstants.key_include, includes);
		return this;
	}
    
    /**
     * <p>Setup associated models for eager loading.</p>
     *
     * @param includes  a string of associated models
     * @param joinType  type of join
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder includes(String includes, String joinType) {
		if (usedIncludes)
			throw new RuntimeException("includes() can only be called once.");
		usedIncludes = true;
		
		options.put(ActiveRecordConstants.key_include, includes);
		options.put(ActiveRecordConstants.key_join_type, joinType);
		return this;
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
	public QueryBuilder includes(String includes, boolean strict) {
		if (usedIncludes)
			throw new RuntimeException("includes() can only be called once.");
		usedIncludes = true;
		
		if (strict) {
			options.put(ActiveRecordConstants.key_strict_include, includes);
		}
		else {
			options.put(ActiveRecordConstants.key_include, includes);
		}
		return this;
	}
    
    /**
     * <p>Setup group-by clause.</p>
     *
     * @param groupBy  a valid SQL query group-by clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder groupBy(String groupBy) {
		if (usedGroupBy)
			throw new RuntimeException("groupBy() can only be called once.");
		usedGroupBy = true;
		
		options.put(SqlConstants.key_group_by, groupBy);
		return this;
	}
    
    /**
     * <p>Setup having clause.</p>
     *
     * @param having  a valid SQL query having clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder having(String having) {
		if (usedHaving)
			throw new RuntimeException("having() can only be called once.");
		usedHaving = true;
		
		options.put(SqlConstants.key_having, having);
		return this;
	}
    
    /**
     * <p>Setup group-by clause.</p>
     *
     * @param orderBy  a valid SQL query order-by clause string
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder orderBy(String orderBy) {
		if (usedOrderBy)
			throw new RuntimeException("orderBy() can only be called once.");
		usedOrderBy = true;
		
		options.put(SqlConstants.key_order_by, orderBy);
		return this;
	}
    
    /**
     * <p>Setup limit for number of records per retrieval.</p>
     *
     * @param limit  number of records for each retrieval
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder limit(int limit) {
		if (usedLimit)
			throw new RuntimeException("limit() can only be called once.");
		usedLimit = true;
		
		options.put(ActiveRecordConstants.key_limit, limit + "");
		return this;
	}
    
    /**
     * <p>Setup number of records to skip.</p>
     *
     * @param offset  number of records to skip
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder offset(int offset) {
		if (usedOffset)
			throw new RuntimeException("offset() can only be called once.");
		usedOffset = true;
		
		options.put(ActiveRecordConstants.key_offset, offset + "");
		return this;
	}
    
    /**
     * <p>Setup current page number. 
     * All records in previous pages are skipped.</p>
     *
     * @param page  current page number
     * @return current <tt>QueryBuilder</tt> instance
     */
	public QueryBuilder page(int page) {
		if (usedPage)
			throw new RuntimeException("page() can only be called once.");
		usedPage = true;
		
		options.put(ActiveRecordConstants.key_page, page + "");
		return this;
	}
}
