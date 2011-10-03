/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassWorkSource class contains source code to be inserted to an 
 * application class.
 * 
 * @author (Fei) John Chen
 */
public class ClassWorkSource {
    public static final List<String> arMethods = new ArrayList<String>();
    
    static {
        arMethods.add("private static Class getMyClass() {return @@.class;}");
        
        //querybuilder related
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder where(String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).where(conditionsSQL);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder where(String conditionsSQL, Object[] conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).where(conditionsSQL, conditionsSQLData);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder where(String conditionsSQL, java.util.Map conditionsSQLDataMap) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).where(conditionsSQL, conditionsSQLDataMap);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder includes(String includes) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).includes(includes);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder includes(String includes, String joinType) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).includes(includes, joinType);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder includes(String includes, boolean strict) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).includes(includes, strict);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder groupBy(String groupBy) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).groupBy(groupBy);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder having(String having) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).having(having);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder orderBy(String orderBy) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).orderBy(orderBy);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder limit(int limit) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).limit(limit);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder offset(int offset) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).offset(offset);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.QueryBuilder page(int page) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).page(page);}");
        
    	//crud related
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord newRecord() {return new @@();}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findById(long id) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findById(id);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findById(Object id) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findById(id);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findByRESTfulId(String restfulId) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findByRESTfulId(restfulId);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findByPK(String pkString) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findByPK(pkString);}");
        arMethods.add("public static java.util.List findAllBySQL(String sql) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBySQL(sql);}");
        arMethods.add("public static java.util.List findAllBySQL(String sql, java.util.Map inputs) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBySQL(sql, inputs);}");
        arMethods.add("public static java.util.List findAllBySQLKey(String sqlKey) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBySQLKey(sqlKey);}");
        arMethods.add("public static java.util.List findAllBySQLKey(String sqlKey, java.util.Map inputs) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBySQLKey(sqlKey, inputs);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirstBy(String columns, Object[] values) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirstBy(columns, values);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLastBy(String columns, Object[] values) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLastBy(columns, values);}");
        arMethods.add("public static java.util.List findAllBy(String columns, Object[] values) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBy(columns, values);}");
        arMethods.add("public static java.util.List findAllBy(String columns, Object[] values, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBy(columns, values, options);}");
        arMethods.add("public static java.util.List findAllBy(String columns, Object[] values, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAllBy(columns, values, options);}");
        arMethods.add("public static java.util.List findAll() {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll();}");
//        arMethods.add("public static java.util.List findAll(java.util.Map conditions) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditions);}");
//        arMethods.add("public static java.util.List findAll(java.util.Map conditions, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditions, options);}");
//        arMethods.add("public static java.util.List findAll(java.util.Map conditions, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditions, options);}");
//        arMethods.add("public static java.util.List findAll(String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditionsSQL);}");
//        arMethods.add("public static java.util.List findAll(String conditionsSQL, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditionsSQL, options);}");
//        arMethods.add("public static java.util.List findAll(String conditionsSQL, java.util.Map conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditionsSQL, conditionsSQLData);}");
//        arMethods.add("public static java.util.List findAll(String conditionsSQL, java.util.Map conditionsSQLData, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditionsSQL, conditionsSQLData, options);}");
//        arMethods.add("public static java.util.List findAll(String conditionsSQL, java.util.Map conditionsSQLData, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findAll(conditionsSQL, conditionsSQLData, options);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst() {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst();}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(java.util.Map conditions) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditions);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(java.util.Map conditions, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditions, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(java.util.Map conditions, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditions, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditionsSQL);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(String conditionsSQL, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditionsSQL, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(String conditionsSQL, java.util.Map conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditionsSQL, conditionsSQLData);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(String conditionsSQL, java.util.Map conditionsSQLData, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditionsSQL, conditionsSQLData, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findFirst(String conditionsSQL, java.util.Map conditionsSQLData, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findFirst(conditionsSQL, conditionsSQLData, options);}");
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast() {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast();}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(java.util.Map conditions) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditions);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(java.util.Map conditions, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditions, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(java.util.Map conditions, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditions, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditionsSQL);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(String conditionsSQL, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditionsSQL, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(String conditionsSQL, java.util.Map conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditionsSQL, conditionsSQLData);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(String conditionsSQL, java.util.Map conditionsSQLData, java.util.Map options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditionsSQL, conditionsSQLData, options);}");
//        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord findLast(String conditionsSQL, java.util.Map conditionsSQLData, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).findLast(conditionsSQL, conditionsSQLData, options);}");
        arMethods.add("public static int updateAll(java.util.Map fieldData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).updateAll(fieldData);}");
        arMethods.add("public static int updateAll(java.util.Map fieldData, String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).updateAll(fieldData, conditionsSQL);}");
        arMethods.add("public static int updateAll(java.util.Map fieldData, String conditionsSQL, java.util.Map conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).updateAll(fieldData, conditionsSQL, conditionsSQLData);}");
        arMethods.add("public static int deleteById(long id) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteById(id);}");
        arMethods.add("public static int deleteById(Object id) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteById(id);}");
        arMethods.add("public static int deleteByPK(String pkString) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteByPK(pkString);}");
        arMethods.add("public static int deleteByPrimaryKeyMap(java.util.Map inputs) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteByPrimaryKeyMap(inputs);}");
        arMethods.add("public static int deleteAll(java.util.Map conditions) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteAll(conditions);}");
        arMethods.add("public static int deleteAll(String conditionsSQL) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteAll(conditionsSQL);}");
        arMethods.add("public static int deleteAll(String conditionsSQL, java.util.Map conditionsSQLData) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getGateway(getMyClass()).deleteAll(conditionsSQL, conditionsSQLData);}");
        
        //calculator related
        arMethods.add("public static long count() {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).count();}");
        arMethods.add("public static long count(String field) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).count(field);}");
        arMethods.add("public static long count(String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).count(field, options);}");
        arMethods.add("public static Object sum(String field) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).sum(field);}");
        arMethods.add("public static Object sum(String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).sum(field, options);}");
        arMethods.add("public static Object average(String field) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).average(field);}");
        arMethods.add("public static Object average(String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).average(field, options);}");
        arMethods.add("public static Object maximum(String field) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).maximum(field);}");
        arMethods.add("public static Object maximum(String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).maximum(field, options);}");
        arMethods.add("public static Object minium(String field) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).minium(field);}");
        arMethods.add("public static Object minium(String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).minium(field, options);}");
        arMethods.add("public static Object calculate(String function, String field, String options) {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getCalculator(getMyClass()).calculate(function, field, options);}");
        
        //meta info
        arMethods.add("public static com.scooterframework.orm.activerecord.ActiveRecord homeInstance() {return com.scooterframework.orm.activerecord.ActiveRecordUtil.getHomeInstance(getMyClass());}");
        arMethods.add("public static java.util.List primaryKeyNames() {return com.scooterframework.common.util.Converters.convertArrayToList(homeInstance().getPrimaryKeyNames());}");
        arMethods.add("public static java.util.List readOnlyColumnNames() {return homeInstance().getRowInfo().getReadOnlyColumnNames();}");
        arMethods.add("public static java.util.List columnNames() {return com.scooterframework.common.util.Converters.convertArrayToList(homeInstance().getRowInfo().getColumnNames());}");
        arMethods.add("public static java.util.List columns() {return homeInstance().getRowInfo().columns();}");
        arMethods.add("public static com.scooterframework.orm.sqldataexpress.object.RowInfo rowInfo() {return homeInstance().getRowInfo();}");
        arMethods.add("public static String connectionName() {return homeInstance().getConnectionName();}");
        arMethods.add("public static String tableName() {return homeInstance().getTableName();}");
        arMethods.add("public static String simpleTableName() {return homeInstance().getSimpleTableName();}");
        
    }
}
