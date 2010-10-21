/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;

/**
 * IncludeHelper class has helper methods for sql queries with include option. 
 * 
 * @author (Fei) John Chen
 */
public class IncludeHelper {
    IncludeHelper(Class recordClz, Map conditions, Map options) {
        if (options == null) throw new IllegalArgumentException("options cannot be null for IncludeHelper.");
        
        mainHome = ActiveRecordUtil.getHomeInstance(recordClz);
        this.conditions = conditions;
        this.options = options;
        
        String jType = (String)options.get(ActiveRecordConstants.key_join_type);
        if (JOIN_TYPE_INNER.equalsIgnoreCase(jType)) {
            joinType = JOIN_TYPE_INNER;
        }
        
        String includeString = (String)options.get(ActiveRecordConstants.key_include);
        String strictIncludeString = (String)options.get(ActiveRecordConstants.key_strict_include);
        if (!isEmpty(includeString)) {
        	if (!isEmpty(strictIncludeString)) {
        		throw new IllegalArgumentException("include and strict_include cannot appear together in options " + options);
        	}
        }
        else {
        	if (!isEmpty(strictIncludeString)) {
        		includeString = strictIncludeString;
        		joinType = JOIN_TYPE_INNER;
        	}
        	else {
        		throw new IllegalArgumentException("There must be either include or strict_include in options " + options);
        	}
        }
        
        initializeIncludeNodeList(includeString);
    }
    
    IncludeHelper(Class recordClz, String conditionsSQL, Map conditionsSQLData, Map options) {
    	this(recordClz, null, options);
    	this.conditionsSQL = conditionsSQL;
    	this.conditionsSQLData = conditionsSQLData;
    }
    
    IncludeHelper(Class recordClz, Map conditions, Map options, String hmtInnerJoinSQL, String hmtMidCMapping, Map hmtMidCMapData, String hmtConditionsSQL) {
    	this(recordClz, conditions, options);
    	this.hmtInnerJoinSQL = hmtInnerJoinSQL;
    	this.hmtMidCMapping = hmtMidCMapping;
    	this.hmtMidCMapData = hmtMidCMapData;
    	this.hmtConditionsSQL = hmtConditionsSQL;
    }
    
    private static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }
    
    private void initializeIncludeNodeList(String includeString) {
        List includes = Converters.convertStringToUniqueList(includeString.toLowerCase());
        
        Iterator it = includes.iterator();
        while(it.hasNext()) {
            String include = (String)it.next();
            if (include.indexOf(INCLUDE_LINK) != -1) {
                constructIncludeNodes(include);
            }
            else {
                includeNodes.add(constructIncludeNode(include, mainHome, null, mainHome.getClass()));
            }
        }
    }
    
    private void constructIncludeNodes(String includes) {
        List nodes = Converters.convertStringToUniqueList(includes, INCLUDE_LINK);
        int index = 0;
        IncludeNode previous = null;
        IncludeNode current = null;
        Iterator it = nodes.iterator();
        while(it.hasNext()) {
            String include = (String)it.next();
            if (index == 0) {
                current = constructIncludeNode(include, mainHome, null, mainHome.getClass());
            }
            else {
                current = constructIncludeNode(include, mainHome, previous, previous.getHomeInstance().getClass());
                previous.setNext(current);
            }
            
            includeNodes.add(current);
            previous = current;
            index++;
        }
    }
    
    private IncludeNode constructIncludeNode(String include, ActiveRecord controlHome, 
                            IncludeNode previousIncludeNode, Class endAClz) {
        String includeName = include.toLowerCase();
        int order = getOrder();
        boolean useTableAlias = !checkUnique(includeName);
        Relation relation = RelationManager.getInstance().getRelation(endAClz, includeName);
        if (relation == null) {
            throw new UndefinedRelationException(ActiveRecordUtil.getModelName(endAClz), include);
        }
        IncludeNode node = new IncludeNode(includeName, controlHome, order, previousIncludeNode, relation, useTableAlias, joinType);
        
        //try a better looking alias name
        if (useTableAlias) {
            String endAMapping = node.getEndAMappingName();
            String alias = endAMapping + "_" + node.getHomeInstance().getTableName();
            if (checkUnique(alias)) {
                node.setTableAlias(alias);
            }
        }
        return node;
    }
    
    private int getOrder() {
        return ++order;
    }
    
    private boolean checkUnique(String entity) {
        boolean unique = false;
        if (!uniqueIncludes.contains(entity)) {
            unique = true;
            uniqueIncludes.add(entity);
        }
        return unique;
    }
    
    /**
     * Constructs a sql query for the include case.
     * 
     * @return a Map containing both the sql and input data
     */
    public Map getConstructedSqlQuery() {
        Map inputsAndSql = new HashMap();
        String findSQL = "";
        String conditionSql = null;
        
        StringBuffer sqlSelectSB = new StringBuffer();
        
        //construct select query
        boolean useUnique = false;
        if (options != null && options.size() > 0) {
            String unique = (String)options.get(ActiveRecordConstants.key_unique);
            if ("true".equalsIgnoreCase(unique)) {
                useUnique = true;
            }
            
            conditionSql = (String)options.get(ActiveRecordConstants.key_conditions_sql);
        }
        
        if (useUnique) {
            sqlSelectSB.append("SELECT DISTINCT ");
        }
        else {
            sqlSelectSB.append("SELECT ");
        }
        
        StringBuffer sqlJoinSB = new StringBuffer();
        
        String mainTableName = mainHome.getTableName();
        String[] columnNames = getAllowedColumnNames();
        sqlSelectSB.append(IncludeNode.getSqlSelectPart(mainTableName, columnNames));
        
        Iterator itx = includeNodes.iterator();
        while(itx.hasNext()) {
            IncludeNode node = (IncludeNode)itx.next();
            sqlSelectSB.append(", ").append(node.toSqlSelectPart());
            sqlJoinSB.append(node.toSqlJoinPart());
        }
        
        sqlSelectSB.append(" FROM ").append(mainTableName).append(sqlJoinSB);
        
        findSQL = sqlSelectSB.toString();
        
        //Section of code for conditionsSQL
        boolean isConditionsSQL = (conditionsSQL != null && !"".equals(conditionsSQL.trim()))?true:false;
        
        //Section of code for hmt
        boolean isHMT = (hmtInnerJoinSQL != null)?true:false;
        if (isHMT) {
        	findSQL = findSQL + " " + hmtInnerJoinSQL;
        }
        
        //construct where clause
        Map inputs = new HashMap();
        String whereClause = "";
        boolean useWhere = false;
        if (conditions != null && conditions.size() > 0) {
            int position = 1;
            for(Iterator it = conditions.keySet().iterator(); it.hasNext();) {
                String columnName = (String) it.next();
                
                //skip system keys
                if (columnName == null || columnName.startsWith("_") || 
                    columnName.toUpperCase().startsWith(DataProcessor.framework_input_key_prefix.toUpperCase()) ||
                    ((columnName.indexOf('.') == -1) && (!mainHome.getRowInfo().isValidColumnName(columnName)))
                    ) continue;
                
                Object conditionData = conditions.get(columnName);
                
                if (columnName.indexOf('.') == -1) {
                    whereClause += mainTableName + "." + columnName + " = ? AND ";
                }
                else {
                    whereClause += columnName + " = ? AND ";
                }
                
                //inputs.put(columnName, conditionData);
                inputs.put(position+"", conditionData);
                useWhere = true;
                
                position = position + 1;
            }
            
            if (whereClause.endsWith("AND ")) {
                int lastAnd = whereClause.lastIndexOf("AND ");
                whereClause = whereClause.substring(0, lastAnd);
            }
            
            inputsAndSql.putAll(conditions);
        }
        
        boolean whereUsed = false;
        if (useWhere) {
            findSQL += " WHERE " + whereClause;
        
            if (conditionSql != null && !"".equals(conditionSql)) {
                findSQL += " AND (" + conditionSql + ")";
            }
        	whereUsed = true;
        }
        else {
            if (conditionSql != null && !"".equals(conditionSql)) {
                findSQL += " WHERE (" + conditionSql + ")";
            	whereUsed = true;
            }
        }
        
        if (isConditionsSQL) {
        	if (whereUsed) {
        		findSQL = findSQL + " AND (" + conditionsSQL + ")";
        	}
        	else {
        		findSQL = findSQL + " WHERE (" + conditionsSQL + ")";
        		whereUsed = true;
        	}
        	
        	if (conditionsSQLData != null) {
        		inputsAndSql.putAll(conditionsSQLData);
            }
        }
        
        //Section of code for hmt
        if (isHMT) {
        	String hmtString = hmtMidCMapping;
        	if (hmtConditionsSQL != null && !"".equals(hmtConditionsSQL)) {
        		hmtString = hmtString + hmtConditionsSQL;
        	}
        	
        	if (whereUsed) {
        		findSQL = findSQL + " AND (" + hmtString + ")";
        	}
        	else {
        		findSQL = findSQL + " WHERE (" + hmtString + ")";
        	}
        	
        	inputsAndSql.putAll(hmtMidCMapData);
        }
        
        findSQL += QueryHelper.getAllSelectQueryClauses(options);
        if (options != null) inputsAndSql.putAll(options);
        
        log.debug("find sql = " + findSQL);
        
        inputsAndSql.put(ActiveRecordConstants.key_finder_sql, findSQL);
        inputsAndSql.putAll(inputs);
        
        return inputsAndSql;
    }
    
    /**
     * Returns join part of select query.
     * 
     * @return a sql fragment for join 
     */
    public String getConstructedJoinQuery() {
        StringBuffer sqlJoinSB = new StringBuffer();
        Iterator itx = includeNodes.iterator();
        while(itx.hasNext()) {
            IncludeNode node = (IncludeNode)itx.next();
            sqlJoinSB.append(node.toSqlJoinPart());
        }
        return sqlJoinSB.toString();
    }
    
    private String[] getAllowedColumnNames() {
        String[] columnNames = null;
        
        boolean useColumns = false;
        boolean exColumns = false;
        if (options != null && options.size() > 0) {
            String columns = (String)options.get(ActiveRecordConstants.key_columns);
            String excolumns = (String)options.get(ActiveRecordConstants.key_ex_columns);
            if (columns != null) {
                useColumns = true;
            }
            if (excolumns != null) {
                exColumns = true;
            }
        }
        
        if (!useColumns && !exColumns) {
            columnNames = mainHome.getRowInfo().getColumnNames();
        }
        else if (useColumns) {
            String columnsStr = (String)options.get(ActiveRecordConstants.key_columns);
            List columns = Converters.convertStringToUniqueList(columnsStr.toUpperCase());
            int length = columns.size();
            columnNames = new String[length];
            for (int i=0; i<length; i++) {
                columnNames[i] = (String)columns.get(i);
            }
        }
        else if (exColumns) {
            String excolumnsStr = (String)options.get(ActiveRecordConstants.key_ex_columns);
            List excolumns = Converters.convertStringToUniqueList(excolumnsStr.toUpperCase());
            
            String[] columns = mainHome.getRowInfo().getColumnNames();
            int length = columns.length;
            columnNames = new String[length-excolumns.size()];
            int index = 0;
            for (int i=0; i<length; i++) {
                String column = columns[i];
                if (excolumns.contains(column)) continue;
                columnNames[index] = column;
                index++;
            }
        }
        
        return columnNames;
    }
    
    /**
     * Organizes raw data retrieved from database into a list of associated 
     * ActiveRecord instances.
     * 
     * @param retrievedTableData TableData from database
     * @return List of associated records.
     */
    public List organizeData(TableData retrievedTableData) {
        if (retrievedTableData == null) return null;
        
        //scan for main home
        String mainTableName = mainHome.getTableName();
        recordDataMap.put(mainTableName, retrieveRecordDataList(mainTableName, mainHome, retrievedTableData));
        
        //scan for each include node
        int totalNodes = includeNodes.size();
        for (int i=0; i<totalNodes; i++) {
            IncludeNode node = (IncludeNode)includeNodes.get(i);
            String tableMappingName = node.getMappingName();
            recordDataMap.put(tableMappingName, retrieveRecordDataList(tableMappingName, node.getHomeInstance(), retrievedTableData));
        }
        
        //associate everything
        constructAssociation(mainTableName);
        
        //now return the main list
        List mainRecordData = (List)recordDataMap.get(mainTableName);
        if (mainRecordData == null) return null;
        
        List list = new ArrayList();
        int size = mainRecordData.size();
        for (int i=0; i<size; i++) {
            list.add(((RecordData)mainRecordData.get(i)).getRecord());
        }
        return list;
    }
    
    private List retrieveRecordDataList(String tableMappingName, ActiveRecord entityHome, TableData retrievedTableData) {
        int totalRows = retrievedTableData.getTableSize();
        if (totalRows == 0) return null;
        
        //key is keyString, value is RecordData
        Map rowKeyMap = new HashMap();
        
        List results = new ArrayList();
        for (int i=0; i < totalRows; i++) {
            RowData resultRow = (RowData)retrievedTableData.getRow(i);
            RowData entityRow = constructRowData(resultRow, tableMappingName, entityHome);
            if (entityRow == null) continue;
            
            String keyDataString = getKeyDataString(entityRow);
            if (rowKeyMap.containsKey(keyDataString)) {
                RecordData recordData = (RecordData)rowKeyMap.get(keyDataString);
                recordData.addIndex(i);
            }
            else {
                RecordData recordData = new RecordData(entityHome, entityRow);
                rowKeyMap.put(keyDataString, recordData);
                recordData.addIndex(i);
                results.add(recordData);
            }
        }
        
        return results;
    }
    
    //Creates a new RowData based on a retrieved row for an entity.
    private RowData constructRowData(RowData retrievedRow, String tableMappingName, ActiveRecord entityHome) {
        RowInfo ri = entityHome.getRowInfo();
        boolean hasPrimaryKey = ri.hasPrimaryKey();
        boolean hasOnlyNullData = true;
        String[] columnNames = ri.getColumnNames();
        int dimension = columnNames.length;
        Object[] colData = new Object[dimension];
        for (int i=0; i<dimension; i++) {
            String columnName = columnNames[i];
            colData[i] = retrievedRow.getField(tableMappingName + "_" + columnName);
            if (hasPrimaryKey) {
                if ((colData[i] == null) && ri.isPrimaryKeyColumn(columnName)) {
                    return null;
                }
            }
            
            if (colData[i] != null) {
                hasOnlyNullData = false;
            }
        }
        
        if (hasOnlyNullData) return null;
        
        return new RowData(ri, colData);
    }
    
    private String getKeyDataString(RowData rd) {
        String key = "";
        if (rd.hasPrimaryKey()) {
            key = rd.getPrimaryKeyDataString();
        }
        else {
            key = rd.getDataMap().toString();
        }
        return key;
    }
    
    //return map: key is row index, value is ActiveRecord instance.
    private Map constructIndexRecordMap(List recDataList) {
        if (recDataList == null) return null;
        
        Map indexRecordMap = new HashMap();
        int recDataSize = recDataList.size();
        for (int i=0; i<recDataSize; i++) {
            RecordData recordData = (RecordData)recDataList.get(i);
            ActiveRecord record = recordData.getRecord();
            List indices = recordData.getRowIndexList();
            int totalRows = indices.size();
            for (int j = 0; j<totalRows; j++) {
                Integer index = (Integer)indices.get(j);
                indexRecordMap.put(index, record);
            }
        }
        return indexRecordMap;
    }
    
    private void constructAssociation(String endATableMappingName) {
        List recordDataList = (List)recordDataMap.get(endATableMappingName);
        if (recordDataList == null) return;
        //WARNING: This condition should only be used for left outer join.
        
        List nextNodes = getTargetNodeList(endATableMappingName);
        if (nextNodes == null || nextNodes.size() == 0) return;
        
        Iterator it = recordDataList.iterator();
        while(it.hasNext()) {
            RecordData recordData = (RecordData)it.next();
            ActiveRecord owner = recordData.getRecord();
            
            Iterator itNode = nextNodes.iterator();
            while(itNode.hasNext()) {
                IncludeNode includeNode = (IncludeNode)itNode.next();
                String endBTableMappingName = includeNode.getMappingName();
                Map targets = constructIndexRecordMap((List)recordDataMap.get(endBTableMappingName));
                
                Relation relation = includeNode.getRelation();
                if (Relation.BELONGS_TO_TYPE.equals(relation.getRelationType()) ||
                    Relation.HAS_ONE_TYPE.equals(relation.getRelationType())) {
                    processAssociatedRecord(owner, recordData.getFirstIndex(), includeNode.getIncludeName(), targets);
                }
                else if (Relation.HAS_MANY_TYPE.equals(relation.getRelationType()) ||
                         Relation.HAS_MANY_THROUGH_TYPE.equals(relation.getRelationType())) {
                    processAssociatedRecordsHM(owner, recordData.getRowIndexList(), includeNode.getIncludeName(), targets);
                }
                
                constructAssociation(endBTableMappingName);
            }
        }
    }
    
    //get a list of include nodes whose previous node's mapping name
    //matches the input mapping name.
    private List getTargetNodeList(String mappingName) {
        List result = new ArrayList();
        int totalIncludes = includeNodes.size();
        for (int i=0; i<totalIncludes; i++) {
            IncludeNode in = (IncludeNode)includeNodes.get(i);
            if (mappingName.equalsIgnoreCase(in.getEndAMappingName())) {
                result.add(in);
            }
        }
        return result;
    }
    
    private void processAssociatedRecord(ActiveRecord owner, Integer rowIndex, 
    									 String include, Map targets) {
        //create a new ActiveRecord instance
        ActiveRecord target = (ActiveRecord)targets.get(rowIndex);
        
        //create RecordRelation
        RecordRelation rr = owner.getRecordRelation(include);
        
        //create AssociatedRecord
        AssociatedRecord ar = new AssociatedRecord(rr, target);
        
        //store the AssociatedRecord
        rr.setAssociatedData(ar);
    }
    
    private void processAssociatedRecordsHM(ActiveRecord owner, List rowIndexList, 
                                            String include, Map targets) {
        int listLength = rowIndexList.size();
        List targetRecords = new ArrayList(listLength);
        for (int i=0; i<listLength; i++) {
            Integer rowIndex = (Integer)rowIndexList.get(i);
            ActiveRecord target = (ActiveRecord)targets.get(rowIndex);
            if (target != null && !targetRecords.contains(target)) targetRecords.add(target);
        }
        
        //create RecordRelation
        RecordRelation rr = owner.getRecordRelation(include);
        
        //create AssociatedRecords
        String relationType = rr.getRelation().getRelationType();
        AssociatedRecords ars = null;
        if (Relation.HAS_MANY_TYPE.equals(relationType)) {
            ars = new AssociatedRecordsHM(rr, targetRecords);
        }
        else if (Relation.HAS_MANY_THROUGH_TYPE.equals(relationType)) {
            ars = new AssociatedRecordsHMT(rr, targetRecords);
        }
        
        //store the AssociatedRecords
        rr.setAssociatedData(ars);
    }
    
    private ActiveRecord findOrCreateRecord(Class recordHomeClass, RowData rowData) {
        if (rowData == null) return null;
        
        String rowKey = "";
        if (rowData.hasPrimaryKey()) {
            rowKey = rowData.getPrimaryKeyDataString();
            if (rowKey == null) return null;
        }
        else {
            rowKey = rowData.getDataMap().toString();
        }
        
        String recordKey = recordHomeClass.getName() + "_" + rowKey;
        ActiveRecord record = (ActiveRecord)allRecords.get(recordKey);
        if (record == null) {
            record = (ActiveRecord)OrmObjectFactory.getInstance().newInstance(recordHomeClass);
            record.populateDataFromDatabase(rowData);
            allRecords.put(recordKey, record);
        }
        
        return record;
    }
    
    public static final String JOIN_TYPE_INNER = "INNER JOIN";
    public static final String JOIN_TYPE_LEFT_OUTER = "LEFT OUTER JOIN";
    public static final String INCLUDE_LINK = "=>";
    
    private String joinType = JOIN_TYPE_LEFT_OUTER;
    private ActiveRecord mainHome;
    private Map conditions;
    private String conditionsSQL;
    private Map conditionsSQLData;
    private Map options;
    
    private int order = 0;
    private Set uniqueIncludes = new HashSet();
    private List includeNodes = new ArrayList();
    
    //recordDataMap: key is mapping name, value is list of RecordData
    Map recordDataMap = new HashMap();
    
    /**
     * A map holds 
     */
    private Map allRecords = new HashMap();
    
    private String hmtInnerJoinSQL;
    private String hmtMidCMapping;
    private Map hmtMidCMapData;
    private String hmtConditionsSQL;
    
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    class RecordData {
        private ActiveRecord rowRecord;
        private List rowIndexList = new ArrayList();
        
        RecordData(ActiveRecord recordHome, RowData rowData) {
            rowRecord = (ActiveRecord)findOrCreateRecord(recordHome.getClass(), rowData);
        }
        
        void addIndex(int i) {
            rowIndexList.add(new Integer(i));
        }
        
        Integer getFirstIndex() {
            if (rowIndexList.size() == 0) return null;
            return (Integer)rowIndexList.get(0);
        }
        
        List getRowIndexList() {
            return rowIndexList;
        }
        
        ActiveRecord getRecord() {
            return rowRecord;
        }
    }
}
