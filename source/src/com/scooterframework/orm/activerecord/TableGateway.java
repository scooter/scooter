/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.cache.Cache;
import com.scooterframework.cache.CacheProvider;
import com.scooterframework.cache.CacheProviderUtil;
import com.scooterframework.cache.NamedCurrentThreadCache;
import com.scooterframework.common.exception.ObjectCreationException;
import com.scooterframework.common.exception.RequiredDataMissingException;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
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
import com.scooterframework.orm.sqldataexpress.util.SqlConstants;

/**
 * <p>
 * TableGateway class implements Table Data Gateway pattern. TableGateway
 * handles access to all records of a table or view for a domain model: selects,
 * updates, deletes.
 * </p>
 * 
 * <p>
 * There is no callback involved in methods in this class. To enable callbacks
 * when deleting or updating a set of records, you can first retrieve them and
 * then use the record instance's delete() or update() method which has
 * callbacks.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class TableGateway {

	private Class<? extends ActiveRecord> clazz;

	private ActiveRecord home;

	private boolean useThreadCache = true;
	private boolean useSecondLevelCache = false;
	private boolean flushCacheOnChange = true;
	private Collection<String> localUseCacheExceptions;
	private Collection<String> localFlushCacheExceptions;
	private Cache modelCache;

	// /**
	// * Constructs an instance of TableGateway.
	// *
	// * @param modelClazz a domain model class type
	// */
	// TableGateway(Class<? extends ActiveRecord> modelClazz) {
	// this.clazz = modelClazz;
	// this.home = ActiveRecordUtil.getHomeInstance(modelClazz);
	// }

	/**
	 * Constructs an instance of TableGateway.
	 * 
	 * @param modelHome
	 *            a domain model home instance
	 */
	TableGateway(ActiveRecord modelHome) {
		if (modelHome == null)
			throw new IllegalArgumentException("modelHome is null.");
		if (!modelHome.isHomeInstance())
			throw new IllegalArgumentException("modelHome must be a home instance.");

		this.clazz = modelHome.getClass();
		this.home = modelHome;
		
		useThreadCache = EnvConfig.getInstance().getUseThreadCache();
		useSecondLevelCache = EnvConfig.getInstance().getUseSecondLevelCache();
		flushCacheOnChange = EnvConfig.getInstance().getFlushCacheOnChange();
		
		localUseCacheExceptions = EnvConfig.getInstance().getLocalUseCacheExceptions(clazz.getName());
		localFlushCacheExceptions = EnvConfig.getInstance().getLocalFlushCacheExceptions(clazz.getName());
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
	public Class<? extends ActiveRecord> getModelClass() {
		return clazz;
	}

	/**
	 * 
	 * QueryBuilder related
	 * 
	 */

	/**
	 * <p>
	 * Setup where clause.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a valid SQL query where clause string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder where(String conditionsSQL) {
		return (new QueryBuilder(this)).where(conditionsSQL);
	}

	/**
	 * <p>
	 * Setup where clause.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a valid SQL query where clause string
	 * @param conditionsSQLData
	 *            an array of data for the <tt>conditionsSQL</tt> string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder where(String conditionsSQL,
			Map<String, Object> conditionsSQLData) {
		return (new QueryBuilder(this)).where(conditionsSQL, conditionsSQLData);
	}

	/**
	 * <p>
	 * Setup where clause.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a valid SQL query where clause string
	 * @param conditionsSQLData
	 *            an array of data for the <tt>conditionsSQL</tt> string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder where(String conditionsSQL, Object[] conditionsSQLData) {
		return (new QueryBuilder(this)).where(conditionsSQL, conditionsSQLData);
	}

	/**
	 * <p>
	 * Setup associated models for eager loading.
	 * </p>
	 * 
	 * @param includes
	 *            a string of associated models
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder includes(String includes) {
		return (new QueryBuilder(this)).includes(includes);
	}

	/**
	 * <p>
	 * Setup associated models for eager loading.
	 * </p>
	 * 
	 * @param includes
	 *            a string of associated models
	 * @param joinType
	 *            type of join
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder includes(String includes, String joinType) {
		return (new QueryBuilder(this)).includes(includes, joinType);
	}

	/**
	 * <p>
	 * Setup associated models for eager loading.
	 * </p>
	 * 
	 * <p>
	 * If <tt>strict</tt> is true, then child records can only be accessed
	 * through their parent.
	 * </p>
	 * 
	 * @param includes
	 *            a string of associated models
	 * @param strict
	 *            true if strict
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder includes(String includes, boolean strict) {
		return (new QueryBuilder(this)).includes(includes, strict);
	}

	/**
	 * <p>
	 * Setup group-by clause.
	 * </p>
	 * 
	 * @param groupBy
	 *            a valid SQL query group-by clause string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder groupBy(String groupBy) {
		return (new QueryBuilder(this)).groupBy(groupBy);
	}

	/**
	 * <p>
	 * Setup having clause.
	 * </p>
	 * 
	 * @param having
	 *            a valid SQL query having clause string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder having(String having) {
		return (new QueryBuilder(this)).having(having);
	}

	/**
	 * <p>
	 * Setup group-by clause.
	 * </p>
	 * 
	 * @param orderBy
	 *            a valid SQL query order-by clause string
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder orderBy(String orderBy) {
		return (new QueryBuilder(this)).orderBy(orderBy);
	}

	/**
	 * <p>
	 * Setup limit for number of records per retrieval.
	 * </p>
	 * 
	 * @param limit
	 *            number of records for each retrieval
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder limit(int limit) {
		return (new QueryBuilder(this)).limit(limit);
	}

	/**
	 * <p>
	 * Setup number of records to skip.
	 * </p>
	 * 
	 * @param offset
	 *            number of records to skip
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder offset(int offset) {
		return (new QueryBuilder(this)).offset(offset);
	}

	/**
	 * <p>
	 * Setup current page number. All records in previous pages are skipped.
	 * </p>
	 * 
	 * @param page
	 *            current page number
	 * @return current <tt>QueryBuilder</tt> instance
	 */
	public QueryBuilder page(int page) {
		return (new QueryBuilder(this)).page(page);
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
	 * @param id
	 *            the id of the record
	 * @return the ActiveRecord associated with the <tt>id</tt>
	 */
	public ActiveRecord findById(long id) {
		return findById(Long.valueOf(id));
	}

	/**
	 * Finds the record with the given id, assuming ID is the primary key
	 * column.
	 * 
	 * If there is no column name like "ID", an exception will be thrown.
	 * 
	 * @param id
	 *            the id of the record
	 * @return the ActiveRecord associated with the <tt>id</tt>
	 */
	public ActiveRecord findById(Object id) {
		if (!home.getRowInfo().isValidColumnName("ID")) {
			throw new IllegalArgumentException("There is no column name as ID");
		}

		ActiveRecord ar = null;
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("1", id);
		inputs = addMoreProperties(inputs, null);
		
		String cacheKey = null;
		if (useCache("findById")) {
			cacheKey = getCacheKey("findById", inputs);
			ar = (ActiveRecord) getCache().get(cacheKey);
			if (ar != null) return ar;
		}
		
		String findSQL = "SELECT * FROM " + home.getTableName()	+ " WHERE id = ?";

		try {
			OmniDTO returnTO = getSqlService().execute(inputs,
					DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL);

			RowData tmpRd = returnTO.getTableData(findSQL).getRow(0);
			if (tmpRd != null) {
				ar = (ActiveRecord) createNewInstance();
				ar.populateDataFromDatabase(tmpRd);
				
				if (useCache("findById")) {
					getCache().put(cacheKey, ar);
				}
			}
		} catch (Exception ex) {
			throw new BaseSQLException(ex);
		}
		
		return ar;
	}

	/**
	 * Finds the record with the given <tt>restfulId</tt>.
	 * 
	 * See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord#getRestfulId()}
	 * for definition of RESTfulId.
	 * 
	 * If there is no primary key, a null record is returned.
	 * 
	 * @param restfulId
	 *            the RESTful id of the record
	 * @return the ActiveRecord associated with the <tt>restfulId</tt>
	 */
	public ActiveRecord findByRESTfulId(String restfulId) {
		Map<String, Object> pkMap = convertToPrimaryKeyDataMap(restfulId);
		if (pkMap == null) return null;
		
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findByRESTfulId")) {
			cacheKey = getCacheKey("findByRESTfulId", restfulId);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		record = findFirst(pkMap);
		if (record != null) {
			if (useCache("findByRESTfulId")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * Finds the record with the given <tt>pkString</tt>. This method is the
     * same as <tt>findByRESTfulId(String restfulId)</tt> method.
	 * 
	 * See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord#getRestfulId()}
	 * for definition of RESTfulId which is the same as the primary key string.
	 * 
	 * If there is no primary key, a null record is returned.
	 * 
	 * @param pkString
	 *            primary key string
	 * @return the ActiveRecord associated with the <tt>restfulId</tt>
	 */
	public ActiveRecord findByPK(String pkString) {
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findByPK")) {
			cacheKey = getCacheKey("findByPK", pkString);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		record = findByRESTfulId(pkString);
		if (record != null) {
			if (useCache("findByPK")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the SQL query.
	 * </p>
	 * 
	 * @param sql
	 *            a valid SQL query string
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAllBySQL(String sql) {
		return findAllBySQL(sql, null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the SQL query.
	 * </p>
	 * 
	 * @param sql
	 *            a valid SQL query string
	 * @param inputs
	 *            a map of name and value pairs
	 * @return a list of ActiveRecord objects
	 */
	@SuppressWarnings("unchecked")
	public List<ActiveRecord> findAllBySQL(String sql, Map<String, Object> inputs) {
		List<ActiveRecord> list = null;
		inputs = addMoreProperties(inputs, null);
		
		String cacheKey = null;
		if (useCache("findAllBySQL")) {
			cacheKey = getCacheKey("findAllBySQL", sql, inputs);
			list = (List<ActiveRecord>) getCache().get(cacheKey);
			if (list != null) return list;
		}

		try {
			OmniDTO returnTO = getSqlService().execute(inputs, 
					DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, sql);

			if (returnTO != null) {
				TableData rt = returnTO.getTableData(sql);
				if (rt != null) {
					int records = rt.getTableSize();
					if (records > 0) {
						list = new ArrayList<ActiveRecord>();
						for (int i = 0; i < records; i++) {
							ActiveRecord newRecord = (ActiveRecord) createNewInstance();
							newRecord.populateDataFromDatabase(rt.getRow(i));
							list.add(newRecord);
						}
						
						if (useCache("findAllBySQL")) {
							getCache().put(cacheKey, list);
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new BaseSQLException(ex);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the SQL corresponding to the SQL key.
	 * </p>
	 * 
	 * @param sqlKey
	 *            a key to a SQL string defined in <tt>sql.properties</tt> file
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAllBySQLKey(String sqlKey) {
		return findAllBySQLKey(sqlKey, null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the SQL corresponding to the SQL key.
	 * </p>
	 * 
	 * @param sqlKey
	 *            a key to a SQL string defined in <tt>sql.properties</tt> file
	 * @param inputs
	 *            a map of name and value pairs
	 * @return a list of ActiveRecord objects
	 */
	@SuppressWarnings("unchecked")
	public List<ActiveRecord> findAllBySQLKey(String sqlKey, 
			Map<String, Object> inputs) {
		List<ActiveRecord> list = null;
		inputs = addMoreProperties(inputs, null);
		
		String cacheKey = null;
		if (useCache("findAllBySQLKey")) {
			cacheKey = getCacheKey("findAllBySQLKey", sqlKey, inputs);
			list = (List<ActiveRecord>) getCache().get(cacheKey);
			if (list != null) return list;
		}

		try {
			OmniDTO returnTO = getSqlService().execute(inputs, 
					DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, sqlKey);

			if (returnTO != null) {
				TableData rt = returnTO.getTableData(sqlKey);
				if (rt != null) {
					int records = rt.getTableSize();
					if (records > 0) {
						list = new ArrayList<ActiveRecord>();
						for (int i = 0; i < records; i++) {
							ActiveRecord newRecord = (ActiveRecord) createNewInstance();
							newRecord.populateDataFromDatabase(rt.getRow(i));
							list.add(newRecord);
						}
						
						if (useCache("findAllBySQLKey")) {
							getCache().put(cacheKey, list);
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new BaseSQLException(ex);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * This is a dynamic finder method. See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord}
	 * class for dynamic finder examples.
	 * </p>
	 * 
	 * @param columns
	 *            a string of column names linked by "_and_".
	 * @param values
	 *            an Object[] array
	 * @return ActiveRecord
	 */
	public ActiveRecord findFirstBy(String columns, Object[] values) {
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findFirstBy")) {
			cacheKey = getCacheKey("findFirstBy", columns, values);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		List<ActiveRecord> all = findAllBy(columns, values);
		if (all != null && all.size() > 0) {
			record = (ActiveRecord) all.get(0);
			if (record != null) {
				if (useCache("findFirstBy")) {
					getCache().put(cacheKey, record);
				}
			}
		}

		return record;
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * This is a dynamic finder method. See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord}
	 * class for dynamic finder examples.
	 * </p>
	 * 
	 * @param columns
	 *            a string of column names linked by "_and_".
	 * @param values
	 *            an Object[] array
	 * @return ActiveRecord
	 */
	public ActiveRecord findLastBy(String columns, Object[] values) {
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findLastBy")) {
			cacheKey = getCacheKey("findLastBy", columns, values);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		List<ActiveRecord> all = findAllBy(columns, values);
		if (all != null && all.size() > 0) {
			record = (ActiveRecord) all.get(all.size() - 1);
			if (record != null) {
				if (useCache("findLastBy")) {
					getCache().put(cacheKey, record);
				}
			}
		}

		return record;
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * This is a dynamic finder method. See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord}
	 * class for dynamic finder examples.
	 * </p>
	 * 
	 * @param columns
	 *            a string of column names linked by "_and_".
	 * @param values
	 *            an Object[] array
	 * @return List of ActiveRecord objects
	 */
	public List<ActiveRecord> findAllBy(String columns, Object[] values) {
		return findAllBy(columns, values, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * This is a dynamic finder method. See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord}
	 * class for dynamic finder examples.
	 * </p>
	 * 
	 * @param columns
	 *            a string of column names linked by "_and_".
	 * @param values
	 *            an Object[] array
	 * @param options
	 *            a map of options
	 * @return List of ActiveRecord objects
	 */
	@SuppressWarnings("unchecked")
	public List<ActiveRecord> findAllBy(String columns, Object[] values,
			Map<String, String> options) {
		List<String> names = StringUtil.splitString(columns, "_and_");
		if (names == null || values.length != names.size())
			throw new IllegalArgumentException(
					"Number of input values does not match number of columns.");

		int size = values.length;
		Map<String, Object> map = new HashMap<String, Object>(size);
		for (int i = 0; i < size; i++) {
			map.put(names.get(i), values[i]);
		}
		
		List<ActiveRecord> list = null;
		
		String cacheKey = null;
		if (useCache("findAllBy")) {
			cacheKey = getCacheKey("findAllBy", map, options);
			list = (List<ActiveRecord>) getCache().get(cacheKey);
			if (list != null) return list;
		}
		
		list = findAll(map, options);
		if (useCache("findAllBy")) {
			if (list != null && list.size() > 0)
				getCache().put(cacheKey, list);
		}

		return list;
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * This is a dynamic finder method. See
	 * {@link com.scooterframework.orm.activerecord.ActiveRecord ActiveRecord}
	 * class for dynamic finder examples.
	 * </p>
	 * 
	 * @param columns
	 *            a string of column names linked by "_and_".
	 * @param values
	 *            an Object[] array
	 * @param options
	 *            a string of options
	 * @return List of ActiveRecord objects
	 */
	public List<ActiveRecord> findAllBy(String columns, Object[] values, String options) {
		return findAllBy(columns, values, Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds all the records of a table.
	 * </p>
	 * 
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll() {
		return findAll((String) null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(Map<String, Object> conditions) {
		return findAll(conditions, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a map of options
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(Map<String, Object> conditions, 
			Map<String, String> options) {
		if (options == null) options = new HashMap<String, String>();
		List<ActiveRecord> list = null;

		if (options.size() > 0
				&& (options.containsKey(ActiveRecordConstants.key_include) || 
					options.containsKey(ActiveRecordConstants.key_strict_include))) {
			list = internal_findAll_include(conditions, options);
		} else {
			list = internal_findAll(conditions, options);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a string of options
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(Map<String, Object> conditions, String options) {
		return findAll(conditions, Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(String conditionsSQL) {
		return findAll(conditionsSQL, (Map<String, Object>) null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param options
	 *            a string of options.
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(String conditionsSQL, String options) {
		return findAll(conditionsSQL, (Map<String, Object>) null, 
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(String conditionsSQL, 
			Map<String, Object> conditionsSQLData) {
		return findAll(conditionsSQL, conditionsSQLData, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a map of options.
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(String conditionsSQL,
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		if (options == null)
			options = new HashMap<String, String>();
		List<ActiveRecord> list = null;

		if (options.size() > 0
				&& (options.containsKey(ActiveRecordConstants.key_include) || 
					options.containsKey(ActiveRecordConstants.key_strict_include))) {
			list = internal_findAll_include(conditionsSQL, conditionsSQLData, options);
		} else {
			list = internal_findAll(conditionsSQL, conditionsSQLData, options);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	/**
	 * <p>
	 * Finds all the records that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a string of options.
	 * @return a list of ActiveRecord objects
	 */
	public List<ActiveRecord> findAll(String conditionsSQL, 
			Map<String, Object> conditionsSQLData, String options) {
		return findAll(conditionsSQL, conditionsSQLData, 
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the first record of a table.
	 * </p>
	 * 
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst() {
		return findFirst((String) null);
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(Map<String, Object> conditions) {
		return findFirst(conditions, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a map of options
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(Map<String, Object> conditions, 
			Map<String, String> options) {
		if (options == null) options = new HashMap<String, String>();
		
		options.put(DataProcessor.input_key_records_offset, "0");
		options.put(DataProcessor.input_key_records_limit, "1");
		options.put(DataProcessor.input_key_records_fixed, "true");
		
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findFirst")) {
			cacheKey = getCacheKey("findFirst", conditions, options);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		List<ActiveRecord> list = findAll(conditions, options);
		record = (list != null && list.size() > 0) ? (list.get(0)) : null;
		
		if (record != null) {
			if (useCache("findFirst")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a string of options
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(Map<String, Object> conditions, String options) {
		return findFirst(conditions, Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(String conditionsSQL) {
		return findFirst(conditionsSQL, (Map<String, Object>) null);
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param options
	 *            a string of options.
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(String conditionsSQL, String options) {
		return findFirst(conditionsSQL, (Map<String, Object>) null, 
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(String conditionsSQL,	
			Map<String, Object> conditionsSQLData) {
		return findFirst(conditionsSQL, conditionsSQLData, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a map of options.
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(String conditionsSQL,	
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		if (options == null) options = new HashMap<String, String>();
		if (!options.containsKey(ActiveRecordConstants.key_include) && 
			!options.containsKey(ActiveRecordConstants.key_strict_include)) {
			options.put(DataProcessor.input_key_records_offset, "0");
			options.put(DataProcessor.input_key_records_limit, "1");
			options.put(DataProcessor.input_key_records_fixed, "true");
		}
		
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findFirst")) {
			cacheKey = getCacheKey("findFirst", conditionsSQL, conditionsSQLData, options);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}

		List<ActiveRecord> list = findAll(conditionsSQL, conditionsSQLData, options);
		record = (list != null && list.size() > 0) ? (list.get(0)) : null;
		
		if (record != null) {
			if (useCache("findFirst")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * <p>
	 * Finds the first record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a string of options.
	 * @return the first ActiveRecord found
	 */
	public ActiveRecord findFirst(String conditionsSQL,
			Map<String, Object> conditionsSQLData, String options) {
		return findFirst(conditionsSQL, conditionsSQLData,
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the last record of a table.
	 * </p>
	 * 
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast() {
		return findLast((String) null);
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(Map<String, Object> conditions) {
		return findLast(conditions, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a map of options
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(Map<String, Object> conditions, Map<String, String> options) {
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findLast")) {
			cacheKey = getCacheKey("findLast", conditions, options);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}
		
		List<ActiveRecord> list = findAll(conditions, options);
		int size = list.size();
		record = (size > 0) ? ((ActiveRecord) list.get(size - 1)) : null;
		
		if (record != null) {
			if (useCache("findLast")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @param options
	 *            a string of options
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(Map<String, Object> conditions, String options) {
		return findLast(conditions,	Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(String conditionsSQL) {
		return findLast(conditionsSQL, (Map<String, Object>) null);
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and <tt>options</tt>
	 * examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param options
	 *            a string of options.
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(String conditionsSQL, String options) {
		return findLast(conditionsSQL, (Map<String, Object>) null,
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(String conditionsSQL, 
			Map<String, Object> conditionsSQLData) {
		return findLast(conditionsSQL, conditionsSQLData, (Map<String, String>) null);
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a map of options.
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(String conditionsSQL,
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		ActiveRecord record = null;
		
		String cacheKey = null;
		if (useCache("findLast")) {
			cacheKey = getCacheKey("findLast", conditionsSQL, conditionsSQLData, options);
			record = (ActiveRecord) getCache().get(cacheKey);
			if (record != null) return record;
		}

		List<ActiveRecord> list = findAll(conditionsSQL, conditionsSQLData, options);
		int size = list.size();
		record = (size > 0) ? ((ActiveRecord) list.get(size - 1)) : null;
		
		if (record != null) {
			if (useCache("findLast")) {
				getCache().put(cacheKey, record);
			}
		}
		
		return record;
	}

	/**
	 * <p>
	 * Finds the last record that satisfy the conditions and options.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @param options
	 *            a string of options.
	 * @return the last ActiveRecord found
	 */
	public ActiveRecord findLast(String conditionsSQL,
			Map<String, Object> conditionsSQLData, String options) {
		return findLast(conditionsSQL, conditionsSQLData, 
				Converters.convertSqlOptionStringToMap(options));
	}

	/**
	 * Finds a list of records that satisfy the conditions and options.
	 */
	@SuppressWarnings("unchecked")
	private List<ActiveRecord> internal_findAll(Map<String, Object> conditions,
			Map<String, String> options) {
		List<ActiveRecord> list = null;

		try {
			Map<String, Object> inputs = constructFindSQL(conditions, options);
			String findSQL = (String) inputs.get(ActiveRecordConstants.key_finder_sql);
			int offset = getOffset(options);
			int limit = getLimit(options);

			inputs = addMoreProperties(inputs, options);
			
			String cacheKey = null;
			if (useCache("findAll")) {
				cacheKey = getCacheKey("findAll", findSQL, inputs, limit, offset);
				list = (List<ActiveRecord>) getCache().get(cacheKey);
				if (list != null) return list;
			}

			TableData td = getSqlService().retrieveRows(inputs,
					DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL,
					limit, offset);

			if (td != null) {
				int records = td.getTableSize();
				if (records > 0) {
					list = new ArrayList<ActiveRecord>();
					for (int i = 0; i < records; i++) {
						ActiveRecord newRecord = (ActiveRecord) createNewInstance();
						newRecord.populateDataFromDatabase(td.getRow(i));
						list.add(newRecord);
					}
					
					if (useCache("findAll")) {
						getCache().put(cacheKey, list);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new BaseSQLException(ex);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	@SuppressWarnings("unchecked")
	private List<ActiveRecord> internal_findAll(String conditionsSQL,
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		List<ActiveRecord> list = null;

		try {
			Map<String, Object> inputs = 
				constructFindSQL(conditionsSQL, conditionsSQLData, options);
			String findSQL = (String) inputs.get(ActiveRecordConstants.key_finder_sql);
			int offset = getOffset(options);
			int limit = getLimit(options);

			inputs = addMoreProperties(inputs, options);
			
			String cacheKey = null;
			if (useCache("findAll")) {
				cacheKey = getCacheKey("findAll", findSQL, inputs, limit, offset);
				list = (List<ActiveRecord>) getCache().get(cacheKey);
				if (list != null) return list;
			}

			TableData td = getSqlService().retrieveRows(inputs,
					DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL,
					limit, offset);

			if (td != null) {
				int records = td.getTableSize();
				if (records > 0) {
					list = new ArrayList<ActiveRecord>();
					for (int i = 0; i < records; i++) {
						ActiveRecord newRecord = (ActiveRecord) createNewInstance();
						newRecord.populateDataFromDatabase(td.getRow(i));
						list.add(newRecord);
					}
					
					if (useCache("findAll")) {
						getCache().put(cacheKey, list);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new BaseSQLException(ex);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
	}

	private int getOffset(Map<String, String> options) {
		int offset = 0;
		if (options.containsKey(ActiveRecordConstants.key_offset)) {
			offset = Util.getIntValue(options,
					ActiveRecordConstants.key_offset, 0);
		} else if (options.containsKey(DataProcessor.input_key_records_offset)) {
			offset = Util.getIntValue(options,
					DataProcessor.input_key_records_offset, 0);
		} else if (options.containsKey(ActiveRecordConstants.key_page)) {
			int current_page = Util.getIntValueForKey(options,
					ActiveRecordConstants.key_page);
			int limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;
			if (options.containsKey(ActiveRecordConstants.key_limit)) {
				limit = Util.getIntValueForKey(options,
						ActiveRecordConstants.key_limit);
			}
			offset = (current_page - 1 ) * limit;
		}
		return offset;
	}

	private int getLimit(Map<String, String> options) {
		int limit = DataProcessor.NO_ROW_LIMIT;
		if (options.containsKey(ActiveRecordConstants.key_limit)) {
			limit = Util.getIntValue(options, ActiveRecordConstants.key_limit,
					DataProcessor.NO_ROW_LIMIT);
		} else if (options.containsKey(DataProcessor.input_key_records_limit)) {
			limit = Util.getIntValue(options,
					DataProcessor.input_key_records_limit,
					DataProcessor.NO_ROW_LIMIT);
		} else if (options.containsKey(ActiveRecordConstants.key_page)) {
			limit = Util.getIntValue(options, ActiveRecordConstants.key_limit,
					DataProcessor.DEFAULT_PAGINATION_LIMIT);
		}
		return limit;
	}

	Object createNewInstance() throws Exception {
		Object o = null;
		try {
			o = home.getClass().newInstance();
		} catch (Exception ex) {
			throw new ObjectCreationException(home.getClass().getName(), ex);
		}
		return o;
	}

	// This method is mostly used by internal and JdbcPageListSource
	public Map<String, Object> constructFindSQL(Map<String, Object> conditions,
			Map<String, String> options) {
		Map<String, Object> inputsAndSql = new HashMap<String, Object>();

		String findSQL = getFinderSql(options);
		boolean finderHasWhere = checkFinderSqlWhere(findSQL);

		String conditionSql = null;
		if (options != null && options.size() > 0) {
			conditionSql = options.get(ActiveRecordConstants.key_conditions_sql);
		}

		// construct where clause
		Map<String, Object> inputs = new HashMap<String, Object>();
		String whereClause = "";
		boolean useWhere = false;
		if (conditions != null && conditions.size() > 0) {
			whereClause = "";
			int position = 1;
			for (Map.Entry<String, Object> entry : conditions.entrySet()) {
				String columnName = entry.getKey();

				// skip system keys
				if (columnName == null
						|| columnName.startsWith("_")
						|| columnName.toUpperCase().startsWith(
								DataProcessor.framework_input_key_prefix
										.toUpperCase())
						|| !home.isColumnField(columnName))
					continue;

				Object conditionData = entry.getValue();
				whereClause += columnName + " = ? AND ";
				// inputs.put(columnName, conditionData);
				inputs.put(position + "", conditionData);
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
		} else {
			if (useWhere) {
				findSQL += " WHERE " + whereClause;

				if (conditionSql != null && !"".equals(conditionSql)) {
					findSQL += " AND (" + conditionSql + ")";
				}
			} else {
				if (conditionSql != null && !"".equals(conditionSql)) {
					findSQL += " WHERE " + conditionSql;
				}
			}
		}

		findSQL += QueryHelper.getAllSelectQueryClauses(options);
		if (options != null)
			inputsAndSql.putAll(options);

		inputsAndSql.put(ActiveRecordConstants.key_finder_sql, findSQL);
		inputsAndSql.putAll(inputs);

		return inputsAndSql;
	}

	private Map<String, Object> constructFindSQL(String conditionsSQL,
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		Map<String, Object> inputsAndSql = new HashMap<String, Object>();

		String findSQL = getFinderSql(options);
		boolean finderHasWhere = checkFinderSqlWhere(findSQL);

		if (finderHasWhere) {
			if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
				findSQL += " AND (" + conditionsSQL + ")";
				if (conditionsSQLData != null) {
					inputsAndSql.putAll(conditionsSQLData);
				}
			}
		} else {
			if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
				findSQL += " WHERE " + conditionsSQL;
				if (conditionsSQLData != null) {
					inputsAndSql.putAll(conditionsSQLData);
				}
			}
		}

		findSQL += QueryHelper.getAllSelectQueryClauses(options);
		if (options != null)
			inputsAndSql.putAll(options);

		inputsAndSql.put(ActiveRecordConstants.key_finder_sql, findSQL);

		return inputsAndSql;
	}

	public String getFinderSql(Map<String, String> options) {
		String finderSQL = "";
		if (options != null
				&& options.containsKey(ActiveRecordConstants.key_finder_sql)) {
			finderSQL = options.get(ActiveRecordConstants.key_finder_sql);
			return finderSQL;
		}

		// construct finger SQL query
		finderSQL = "SELECT ";
		if (options != null && options.size() > 0) {
			String unique = options.get(ActiveRecordConstants.key_unique);
			if ("true".equalsIgnoreCase(unique)) {
				finderSQL = "SELECT DISTINCT ";
			}
		}
		
		if (options == null) options = new HashMap<String, String>();
		String table = home.getTableName();
		options.put(SqlConstants.key_table, table);

		boolean useColumns = false;
		boolean exColumns = false;
		if (options != null && options.size() > 0) {
			String columns = options.get(ActiveRecordConstants.key_columns);
			String excolumns = options
					.get(ActiveRecordConstants.key_ex_columns);
			if (columns != null) {
				useColumns = true;
			}
			if (excolumns != null) {
				exColumns = true;
			}
		}

		if (!useColumns && !exColumns) {
			finderSQL += table + ".*";
		} else if (useColumns) {
			String columnsStr = options.get(ActiveRecordConstants.key_columns);
			List<String> columns = Converters
					.convertStringToUniqueList(columnsStr.toUpperCase());
			Iterator<String> it = columns.iterator();
			while (it.hasNext()) {
				finderSQL += table + "." + it.next() + ", ";
			}
			finderSQL = StringUtil.removeLastToken(finderSQL, ", ");
		} else if (exColumns) {
			String excolumnsStr = options
					.get(ActiveRecordConstants.key_ex_columns);
			List<String> excolumns = Converters
					.convertStringToUniqueList(excolumnsStr.toUpperCase());

			String[] columns = home.getRowInfo().getColumnNames();
			int length = columns.length;
			for (int i = 0; i < length; i++) {
				String column = columns[i];
				if (excolumns.contains(column))
					continue;
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
			// make sure the "where" is valid
			boolean foundFrom = false;
			int countP = 0;
			StringTokenizer st = new StringTokenizer(finderSQL);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!foundFrom) {
					if ("FROM".equals(token))
						foundFrom = true;
				} else {
					if (token.startsWith("("))
						countP = countP + 1;
					else if (token.startsWith(")"))
						countP = countP - 1;
					else if ("WHERE".equals(token) && countP == 0)
						status = true;
				}
			}
		}
		return status;
	}

	/**
	 * Finds a list of records that satisfy the conditions and options.
	 */
	List<ActiveRecord> internal_findAll_include(Map<String, Object> conditions,
			Map<String, String> options) {
		IncludeHelper sqlHelper = new IncludeHelper(getModelClass(),
				conditions, options);
		return internal_findAll_include_fetch(sqlHelper, options);
	}

	/**
	 * Finds a list of records that satisfy the conditions and options.
	 */
	List<ActiveRecord> internal_findAll_include(String conditionsSQL,
			Map<String, Object> conditionsSQLData, Map<String, String> options) {
		IncludeHelper sqlHelper = new IncludeHelper(getModelClass(),
				conditionsSQL, conditionsSQLData, options);
		return internal_findAll_include_fetch(sqlHelper, options);
	}

	/**
	 * Finds a list of records that satisfy the conditions and options in a
	 * has-many-through relation.
	 */
	List<ActiveRecord> internal_findAll_include_hmt(
			Map<String, Object> conditions, Map<String, String> options,
			String innerSQL, String midCMapping,
			Map<String, Object> midCMapData, String conditionsSQL) {
		IncludeHelper sqlHelper = new IncludeHelper(getModelClass(),
				conditions, options, innerSQL, midCMapping, midCMapData,
				conditionsSQL);
		return internal_findAll_include_fetch(sqlHelper, options);
	}

	@SuppressWarnings("unchecked")
	private List<ActiveRecord> internal_findAll_include_fetch(
			IncludeHelper sqlHelper, Map<String, String> options) {
		List<ActiveRecord> list = null;

		try {
			Map<String, Object> inputs = sqlHelper.getConstructedSqlQuery();
			String findSQL = (String) inputs.get(ActiveRecordConstants.key_finder_sql);
			int offset = getOffset(options);
			int limit = getLimit(options);

			inputs = addMoreProperties(inputs, options);
			
			String cacheKey = null;
			if (useCache("findAll") && allowCacheAssociatedObjects()) {
				cacheKey = getCacheKey("findAll", findSQL, inputs, limit, offset);
				list = (List<ActiveRecord>) getCache().get(cacheKey);
				if (list != null) return list;
			}

			TableData td = getSqlService().retrieveRows(inputs,
					DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, findSQL,
					limit, offset);

			if (td != null) {
				list = sqlHelper.organizeData(td);
				
				if (useCache("findAll") && allowCacheAssociatedObjects()) {
					getCache().put(cacheKey, list);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new BaseSQLException(ex);
		}

		return (list != null) ? list : (new ArrayList<ActiveRecord>());
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
	 * @param id
	 *            key to the object with field name "ID"
	 * @return int number of records deleted
	 */
	public int deleteById(long id) {
		return deleteById(Long.valueOf(id));
	}

	/**
	 * Deletes the record with the given id, assuming ID is the primary key
	 * column.
	 * 
	 * If there is no column name like "ID", an exception will be thrown.
	 * 
	 * @param id
	 *            key to the object with field name "ID"
	 * @return int number of records deleted
	 */
	public int deleteById(Object id) {
		if (!home.getRowInfo().isValidColumnName("ID")) {
			throw new IllegalArgumentException("There is no column name as ID");
		}
		
		clearCache("deleteById");

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs = addMoreProperties(inputs, null);

		String deleteSQL = "DELETE FROM " + home.getTableName() + " WHERE id = ?";
		return SqlServiceClient.executeSQL(deleteSQL, inputs);
	}

	/**
	 * Deletes the record with the given <tt>pkString</tt>.
	 * 
	 * If there is no primary key defined, this method returns <tt>0</tt>.
	 * 
	 * @param pkString
	 *            primary key string
	 * @return int number of records deleted
	 */
	public int deleteByPK(String pkString) {
		Map<String, Object> pkMap = convertToPrimaryKeyDataMap(pkString);
		if (pkMap == null) return 0;
		
		clearCache("deleteByPK");
		return deleteByPrimaryKeyMap(pkMap);
	}

	/**
	 * Deletes the record with the given data map containing primary keys.
	 * 
	 * If not all primary key columns have data, an exception will be thrown.
	 * 
	 * @return int number of records deleted
	 */
	public int deleteByPrimaryKeyMap(Map<String, Object> dataMap) {
		if (dataMap == null || dataMap.size() == 0)	return -1;
		
		clearCache("deleteByPrimaryKeyMap");

		// construct a map of primary keys
		Map<String, Object> pkMap = new HashMap<String, Object>();
		String[] pkNames = home.getPrimaryKeyNames();
		int length = pkNames.length;
		for (int i = 0; i < length; i++) {
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
	 * Deletes all the records that satisfy the SQL statement.
	 * 
	 * @param sql
	 *            a key to a SQL string
	 * @return int number of records deleted
	 */
	public int deleteBySQL(String sql) {
		return deleteBySQL(sql, null);
	}

	/**
	 * Deletes all the records that satisfy the SQL statement.
	 * 
	 * The inputs is a map of name and value pairs related to the SQL statement.
	 * 
	 * @param sql
	 *            a key to a SQL string
	 * @param inputs
	 *            a map of name and value pairs
	 * @return int number of records deleted
	 */
	public int deleteBySQL(String sql, Map<String, Object> inputs) {
		clearCache("deleteBySQL");
		return SqlServiceClient.executeSQL(sql, inputs);
	}

	/**
	 * Deletes all the records that satisfy the SQL specified by the
	 * <tt>sqlKey</tt>.
	 * 
	 * @param sqlKey
	 *            a key to a SQL string
	 * @return int number of records deleted
	 */
	public int deleteBySQLKey(String sqlKey) {
		return deleteBySQLKey(sqlKey, null);
	}

	/**
	 * Deletes all the records that satisfy the SQL specified by the
	 * <tt>sqlKey</tt>.
	 * 
	 * The inputs is a map of name and value pairs related to the SQL statement.
	 * 
	 * @param sqlKey
	 *            a key to a SQL string
	 * @param inputs
	 *            a map of name and value pairs
	 * @return int number of records deleted
	 */
	public int deleteBySQLKey(String sqlKey, Map<String, Object> inputs) {
		clearCache("deleteBySQLKey");
		return SqlServiceClient.executeSQLByKey(sqlKey, inputs);
	}

	/**
	 * <p>
	 * Deletes all the records that satisfy the condition.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditions</tt> examples.
	 * </p>
	 * 
	 * @param conditions
	 *            a map of column name and value pairs
	 * @return int number of records deleted
	 */
	public int deleteAll(Map<String, Object> conditions) {
		clearCache("deleteAll");
		return internal_deleteAll(conditions);
	}

	/**
	 * <p>
	 * Deletes all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @return int number of records deleted
	 */
	public int deleteAll(String conditionsSQL) {
		return deleteAll(conditionsSQL, null);
	}

	/**
	 * <p>
	 * Deletes all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @return int number of records deleted
	 */
	public int deleteAll(String conditionsSQL, Map<String, Object> conditionsSQLData) {
		clearCache("deleteAll");
		return internal_deleteAll(conditionsSQL, conditionsSQLData);
	}

	private int internal_deleteAll(Map<String, Object> conditions) {
		int count = -1;
		String deleteSQL = "DELETE FROM " + home.getTableName();

		try {
			Map<String, Object> inputs = new HashMap<String, Object>();

			// construct where clause
			if (conditions != null && conditions.size() > 0) {
				String whereClause = " WHERE ";
				int position = 1;
				for (Map.Entry<String, Object> entry : conditions.entrySet()) {
					whereClause += entry.getKey() + " = ? AND ";
					inputs.put(position + "", entry.getValue());

					position = position + 1;
				}

				if (whereClause.endsWith("AND ")) {
					int lastAnd = whereClause.lastIndexOf("AND ");
					whereClause = whereClause.substring(0, lastAnd);
				}

				deleteSQL += whereClause;
			}

			inputs = addMoreProperties(inputs, null);

			count = SqlServiceClient.executeSQL(deleteSQL, inputs);
		} catch (Exception ex) {
			throw new BaseSQLException(ex);
		}

		return count;
	}

	private int internal_deleteAll(String conditionsSQL, Map<String, Object> conditionsSQLData) {
		int count = -1;
		String deleteSQL = "DELETE FROM " + home.getTableName();

		try {
			Map<String, Object> inputs = new HashMap<String, Object>();

			// construct where clause
			if (conditionsSQL != null && !"".equals(conditionsSQL.trim())) {
				deleteSQL += " WHERE " + conditionsSQL;
				if (conditionsSQLData != null) {
					inputs.putAll(conditionsSQLData);
				}
			}

			inputs = addMoreProperties(inputs, null);

			count = SqlServiceClient.executeSQL(deleteSQL, inputs);
		} catch (Exception ex) {
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
	 * <p>
	 * Updates all the records of a table.
	 * </p>
	 * 
	 * <p>
	 * <tt>fieldData</tt> map is used to construct SET clause of the generated
	 * SQL. It consists of column name and its value pairs in the map. Primary
	 * key column and read-only columns are not updatable.
	 * </p>
	 * 
	 * @param fieldData
	 *            a map of field name and its data to be set on any records
	 * @return int number of records updated
	 */
	public int updateAll(Map<String, Object> fieldData) {
		return updateAll(fieldData, null, null);
	}

	/**
	 * <p>
	 * Updates all the records that satisfy a set of conditions supplied.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> examples.
	 * </p>
	 * 
	 * <p>
	 * <tt>fieldData</tt> map is used to construct SET clause of the generated
	 * SQL. It consists of column name and its value pairs in the map. Primary
	 * key column and read-only columns are not updatable.
	 * </p>
	 * 
	 * @param fieldData
	 *            a map of field name and its data to be set on any records that
	 *            satisfy the conditions.
	 * @param conditionsSQL
	 *            A SQL fragment string
	 * @return int number of records updated
	 */
	public int updateAll(Map<String, Object> fieldData, String conditionsSQL) {
		return updateAll(fieldData, conditionsSQL, null);
	}

	/**
	 * <p>
	 * Updates all the records that satisfy the conditions.
	 * </p>
	 * 
	 * <p>
	 * See {@link com.scooterframework.orm.activerecord.ActiveRecord
	 * ActiveRecord} class for <tt>conditionsSQL</tt> and
	 * <tt>conditionsSQLData</tt> examples.
	 * </p>
	 * 
	 * <p>
	 * <tt>fieldData</tt> map is used to construct SET clause of the generated
	 * SQL. It consists of column name and its value pairs in the map. Primary
	 * key column and read-only columns are not updatable.
	 * </p>
	 * 
	 * @param fieldData
	 *            a map of field name and its data to be set on any records that
	 *            satisfy the conditions.
	 * @param conditionsSQL
	 *            a SQL fragment string
	 * @param conditionsSQLData
	 *            a data map for dynamic attributes in <tt>conditionsSQL</tt>
	 * @return int number of records updated
	 */
	public int updateAll(Map<String, Object> fieldData, String conditionsSQL, Map<String, Object> conditionsSQLData) {
		if (fieldData == null || fieldData.size() == 0)
			throw new IllegalArgumentException(
					"fieldData cannot be empty for updateAll()");
		
		clearCache("updateAll");

		int count = -1;
		String updateSQL = "UPDATE " + home.getTableName();

		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			StringBuilder strBuffer = new StringBuilder();
			ColumnInfo ci = null;
			RowInfo ri = home.getRowInfo();
			for (Map.Entry<String, Object> entry : fieldData.entrySet()) {
				String field = entry.getKey();
				if (field == null) continue;

				ci = ri.getColumnInfo(field);
				if (!ri.isValidColumnName(field) || ci.isReadOnly()
						|| !ci.isWritable() || ci.isPrimaryKey())
					continue;

				String token = getUniqueToken(field, conditionsSQLData, true);
				strBuffer.append(field).append(" = ?").append(token).append(", ");
				inputs.put(token, entry.getValue());
			}

			updateSQL += " SET " + StringUtil.removeLastToken(strBuffer, ", ");

			if (conditionsSQL != null) {
				updateSQL += " WHERE " + conditionsSQL;
			}

			if (conditionsSQLData != null) {
				inputs.putAll(conditionsSQLData);
			}

			inputs = addMoreProperties(inputs, null);

			count = SqlServiceClient.executeSQL(updateSQL, inputs);
		} catch (Exception ex) {
			throw new BaseSQLException(ex);
		}

		return count;
	}

	private String getUniqueToken(String field,	Map<String, Object> conditionsSQLData, boolean convertToUpper) {
		if (conditionsSQLData == null || conditionsSQLData.size() == 0)	return field;

		Map<String, Object> conditionsSQLDataCopy = conditionsSQLData;
		if (convertToUpper) {
			conditionsSQLDataCopy = new HashMap<String, Object>(
					conditionsSQLData.size());
			for (Map.Entry<String, Object> entry : conditionsSQLData.entrySet()) {
				String key = entry.getKey();
				if (key == null) continue;
				
				conditionsSQLDataCopy.put(key.toUpperCase(), entry.getValue());
			}
		}

		if (conditionsSQLDataCopy.containsKey(field.toUpperCase())) {
			field = "_" + field;
			return getUniqueToken(field, conditionsSQLDataCopy, false);
		}
		return field;
	}

	/**
	 * Updates all the records that satisfy the SQL statement.
	 * 
	 * @param sql
	 *            A valid SQL string
	 * @return int number of records updated
	 */
	public int updateBySQL(String sql) {
		return updateBySQL(sql, new HashMap<String, Object>());
	}

	/**
	 * Updates all the records that satisfy the SQL statement.
	 * 
	 * @param sql
	 *            A valid SQL string
	 * @param inputs
	 *            a map of name and value pairs
	 * @return int number of records updated
	 */
	public int updateBySQL(String sql, Map<String, Object> inputs) {
		clearCache("updateBySQL");
		return SqlServiceClient.executeSQL(sql, inputs);
	}

	/**
	 * Updates all the records that satisfy the SQL specified by <tt>sqlKey</tt>
	 * .
	 * 
	 * @param sqlKey
	 *            a key to a SQL string.
	 * @return int number of records updated
	 */
	public int updateBySQLKey(String sqlKey) {
		return updateBySQLKey(sqlKey, new HashMap<String, Object>());
	}

	/**
	 * Updates all the records that satisfy the SQL specified by <tt>sqlKey</tt>
	 * .
	 * 
	 * @param sqlKey
	 *            a key to a SQL string
	 * @param inputs
	 *            a map of name and value pairs
	 * @return int number of records updated
	 */
	public int updateBySQLKey(String sqlKey, Map<String, Object> inputs) {
		clearCache("updateBySQLKey");
		return SqlServiceClient.executeSQLByKey(sqlKey, inputs);
	}

	/**
	 * Converts a primary key RESTful id string to primary key map.
	 * 
	 * @return map of primary key data
	 */
	public Map<String, Object> convertToPrimaryKeyDataMap(String pkString) {
		String[] pkNames = home.getPrimaryKeyNames();
		if (pkNames == null || pkNames.length == 0) {
			return null;
		}

		String[] pkValues = Converters.convertStringToStringArray(pkString,
				DatabaseConfig.PRIMARY_KEY_SEPARATOR);
		if (pkValues.length != pkNames.length) {
			throw new IllegalArgumentException(
					"Failed in convertToPrimaryKeyDataMap, "
							+ "the input string '" + pkString + "' has "
							+ pkValues.length + " parts, while there are "
							+ pkNames.length + " parts for PK.");
		}

		Map<String, Object> pkMap = new HashMap<String, Object>();
		if (pkNames != null) {
			for (int i = 0; i < pkNames.length; i++) {
				String name = pkNames[i];
				String value = pkValues[i];
				pkMap.put(name, value);
			}
		}
		return pkMap;
	}

	private Map<String, Object> addMoreProperties(Map<String, Object> inputs,
			Map<String, String> options) {
		return home.addMoreProperties(inputs, options);
	}

	private static SqlService getSqlService() {
		return SqlServiceConfig.getSqlService();
	}
	
	private String getCacheKey(String request, Object... elements) {
		return CacheProviderUtil.getCacheKey(clazz.getName(), request, elements);
	}
	
	private boolean useCache(String method) {
		boolean useCheck = useThreadCache || useSecondLevelCache;
		if (useCheck) {
			if (localUseCacheExceptions != null && localUseCacheExceptions.contains(method)) {
				useCheck = false;
			}
		}
		else {
			if (localUseCacheExceptions != null && localUseCacheExceptions.contains(method)) {
				useCheck = true;
			}
		}
		return useCheck;
	}
	
	void clearCache(String method) {
		if (flushCache(method)) getCache().clear();
	}
	
	private boolean flushCache(String method) {
		boolean flushCheck = flushCacheOnChange;
		if (flushCheck) {
			if (localFlushCacheExceptions != null && localFlushCacheExceptions.contains(method)) {
				flushCheck = false;
			}
		}
		else {
			if (localFlushCacheExceptions != null && localFlushCacheExceptions.contains(method)) {
				flushCheck = true;
			}
		}
		return flushCheck;
	}
	
	private boolean allowCacheAssociatedObjects() {
		return EnvConfig.getInstance().allowCacheAssociatedObjects(clazz.getName());
	}
	
	private Cache getCache() {
		if (modelCache != null) return modelCache;
		
		if (useSecondLevelCache) {
			CacheProvider dcp = CacheProviderUtil.getDefaultCacheProvider();
			if (dcp != null) {
				modelCache = dcp.getCache(clazz.getName());
			}
		}
		else if (useThreadCache) {
			modelCache = new NamedCurrentThreadCache(clazz.getName());
		}
		
		return modelCache;
	}
}
