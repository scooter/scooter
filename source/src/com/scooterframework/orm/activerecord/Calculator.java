/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Map;

import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;

/**
 * Calculator class contains basic calculation methods for an entity.
 * 
 * @author (Fei) John Chen
 */
public class Calculator {

    /**
     * <p>Construct a calculator.</p>
     * 
     * @param recordHome home instance of an entity
     */
    Calculator(ActiveRecord recordHome) {
        this.recHome = recordHome;
    }

    /**
     * <p>Gets a calculator for a model.</p>
     * 
     * @param modelClass  the model class
     */
    public static Calculator getCalculator(Class modelClass) {
        return ActiveRecordUtil.getCalculator(modelClass);
    }
    
    /**
     * Counts number of records.
     * 
     * @return number of records.
     */
    public long count() {
        return count(null);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @return number of records.
     */
    public long count(String field) {
        return count(field, null);
    }
    
    /**
     * Counts number of records for a field.
     * 
     * @param field name of the field
     * @param options options for calculation
     * @return number of records.
     */
    public long count(String field, String options) {
        Object result = calculate("count", field, options);
        return Util.getSafeLongValue(result);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public Object sum(String field) {
        return calculate("sum", field, null);
    }
    
    /**
     * Calculates sum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public Object sum(String field, String options) {
        return calculate("sum", field, options);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public Object average(String field) {
        return calculate("avg", field, null);
    }
    
    /**
     * Calculates average of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public Object average(String field, String options) {
        return calculate("avg", field, options);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public Object maximum(String field) {
        return calculate("max", field, null);
    }
    
    /**
     * Calculates maximum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public Object maximum(String field, String options) {
        return calculate("max", field, options);
    }
    
    /**
     * Calculates minimum of a field.
     * 
     * @param field the column name
     * @return result of calculation
     */
    public Object minium(String field) {
        return calculate("min", field, null);
    }
    
    /**
     * Calculates minmum of a field.
     * 
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public Object minium(String field, String options) {
        return calculate("min", field, options);
    }
    
    /**
     * Calculates by a function on a field.
     * 
     * @param function the sql function name
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public Object calculate(String function, String field, String options) {
        return calculate(recHome, function, field, options);
    }
    
    /**
     * Calculates by a function on a field.
     * 
     * @param recordHome home instance of an entity
     * @param function the sql function name
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object calculate(ActiveRecord recordHome, String function, String field, String options) {
        Map optionMap = Converters.convertSqlOptionStringToMap(options);
        return calculate(recordHome, function, field, optionMap);
    }
    
    /**
     * Calculates by a function on a field.
     * 
     * @param recordHome home instance of an entity
     * @param function the sql function name
     * @param field the column name
     * @param options options of calculation
     * @return result of calculation
     */
    public static Object calculate(ActiveRecord recordHome, String function, String field, Map options) {
        Object result= null;
        if (options != null && options.containsKey(ActiveRecordConstants.key_custom_sql)) {
            String sql = (String)options.get(ActiveRecordConstants.key_custom_sql);
            result = SqlServiceClient.retrieveObjectBySQL(sql, options);
        }
        else if (options != null && options.containsKey(ActiveRecordConstants.key_custom_sql_key)) {
            String sqlKey = (String)options.get(ActiveRecordConstants.key_custom_sql_key);
            result = SqlServiceClient.retrieveObjectBySQLKey(sqlKey, options);
        }
        else if (options != null && (
        		options.containsKey(ActiveRecordConstants.key_include)||
        		options.containsKey(ActiveRecordConstants.key_strict_include))) {
            String sql = constructSqlWithInclude(recordHome, function, field, options);
            result = SqlServiceClient.retrieveObjectBySQL(sql, options);
        }
        else {
            String sql = constructSql(recordHome, function, field, options);
            result = SqlServiceClient.retrieveObjectBySQL(sql, options);
        }
        return result;
    }
    
    private static String constructSqlWithInclude(ActiveRecord recordHome, String function, String field, Map options) {
        String theSql = constructSelectPart(recordHome, function, field, options);
        
        //get the include join
        IncludeHelper sqlHelper = new IncludeHelper(recordHome.getClass(), null, options);
        theSql += sqlHelper.getConstructedJoinQuery();
        
        String conditionSql = null;
        if (options != null && options.containsKey(ActiveRecordConstants.key_conditions_sql)) {
            conditionSql = (String)options.get(ActiveRecordConstants.key_conditions_sql);
            if (conditionSql != null && !"".equals(conditionSql)) {
                theSql += " WHERE " + conditionSql;
            }
        }
        
        //construct all query clauses
        theSql += QueryHelper.getAllSelectQueryClauses(options);
        return theSql;
    }
    
    private static String constructSql(ActiveRecord recordHome, String function, String field, Map options) {
        String theSql = constructSelectPart(recordHome, function, field, options);
        String conditionSql = null;
        if (options != null && options.containsKey(ActiveRecordConstants.key_conditions_sql)) {
            conditionSql = (String)options.get(ActiveRecordConstants.key_conditions_sql);
            if (conditionSql != null && !"".equals(conditionSql)) {
                theSql += " WHERE " + conditionSql;
            }
        }
        
        //construct all query clauses
        theSql += QueryHelper.getAllSelectQueryClauses(options);
        return theSql;
    }
    
    /**
     * Returns a sql fragment for a calculator function.
     * 
     * @param recordHome home instance of an entity
     * @param function calculator function
     * @param field the column name
     * @param options properties of sql query
     * @return a sql fragment for a function
     */
    private static String constructSelectPart(ActiveRecord recordHome, String function, String field, Map options) {
        StringBuffer sb = new StringBuffer();
        if ("count".equals(function) && field == null) {
            sb.append(getCountSelectPart(recordHome, options));
        }
        else {
            String table = recordHome.getTableName();
            sb.append("SELECT ").append(function).append("(");
            boolean unique = hasKey(options, ActiveRecordConstants.key_unique, "true");
            if (unique) {
                sb.append("DISTINCT ");
            }
            sb.append(table).append('.').append(field).append(") FROM ").append(table);
        }
        return sb.toString();
    }
    
    /**
     * Returns a sql fragment for count.
     * 
     * @param recordHome home instance of an entity
     * @param properties properties of sql query
     * @return a sql fragment for count
     */
    public static String getCountSelectPart(ActiveRecord recordHome, Map properties) {
        String table = recordHome.getTableName();
        String countSQL = "SELECT count(";
        
        boolean useUnique = false;
        if (properties != null && properties.size() > 0) {
            String unique = (String)properties.get(ActiveRecordConstants.key_unique);
            if ("true".equalsIgnoreCase(unique)) {
                useUnique = true;
            }
        }
        
        if (useUnique) {
            String[] pks = recordHome.getPrimaryKeyNames();
            String fields = "";
            if (pks == null || pks.length ==0) {
                fields = StringUtil.flatenArray(table, recordHome.getRowInfo().getColumnNames(), ",");
            }
            else {
                fields = StringUtil.flatenArray(table, pks, ",");
            }
            countSQL += "DISTINCT " + fields;
        }
        else {
            countSQL += "*";
        }
        countSQL += ") FROM " + table;
        return countSQL;
    }
    
    private static boolean hasKey(Map properties, String key, String expectedValue) {
        if (properties == null || properties.size() == 0) return false;
        boolean hasit = false;
        String value = (String)properties.get(key);
        if (expectedValue.equalsIgnoreCase(value)) hasit = true;
        return hasit;
    }
    
    /**
     * <p>record home instance.</p>
     */
    protected ActiveRecord recHome;
}
