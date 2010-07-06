/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Iterator;
import java.util.Map;

import com.scooterframework.common.util.Converters;

/**
 * IncludeData holds details about an include option element.
 * 
 * @author (Fei) John Chen
 */
public class IncludeNode {
    /**
     * Constructs an IncludeNode instance.
     * 
     * 
     * @param include           name of the include node
     * @param controlHome       home instance of the main entity of the sql query
     * @param order             index of the include node starting from 1
     * @param previous          previous include node instance
     * @param relation          relation linking to this include node
     * @param useTableAlias     true if table alias is used
     * @param joinType          type of join
     */
    public IncludeNode(String include, ActiveRecord controlHome, int order, 
                       IncludeNode previous, Relation relation, 
                       boolean useTableAlias, String joinType) {
        if (include == null || order <= 0 || relation == null) {
            throw new IllegalArgumentException("Either entity name or " + 
                        "include order or relation instance is missing.");
        }
        
        this.include = include;
        this.controlHome = controlHome;
        this.order = order;
        this.previousIncludeNode = previous;
        this.relation = relation;
        this.useTableAlias = useTableAlias;
        this.joinType =joinType;
        
        this.home = ActiveRecordUtil.getHomeInstance(relation.getTargetClass());
        
        if (useTableAlias) {
            tableAlias = "t" + order;
        }
    }
    
    public String getIncludeName() {
        return include;
    }
    
    /**
     * Returns home intance of the control entity.
     * 
     * @return home intance of the control entity
     */
    public ActiveRecord getControlHome() {
        return controlHome;
    }
    
    /**
     * Returns include order.
     * 
     * @return include order
     */
    public int getOrder() {
        return order;
    }
    
    /**
     * Returns relation from endA entity to (this) entity.
     * 
     * @return relation from endA entity to (this) entity
     */
    public Relation getRelation() {
        return relation;
    }
    
    /**
     * Returns home instance.
     * 
     * @return home instance
     */
    public ActiveRecord getHomeInstance() {
        return home;
    }
    
    /**
     * Sets alias name of the underline table.
     */
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        useTableAlias = true;
    }
    
    /**
     * Checks if there is a previous IncludeNode.
     */
    public boolean hasPrevious() {
        return (previousIncludeNode != null)?true:false;
    }
    
    /**
     * Returns previous IncludeNode.
     */
    public IncludeNode previous() {
        return previousIncludeNode;
    }
    
    /**
     * Checks if there is a next IncludeNode.
     */
    public boolean hasNext() {
        return (nextIncludeNode != null)?true:false;
    }
    
    /**
     * Returns next IncludeNode.
     */
    public IncludeNode next() {
        return nextIncludeNode;
    }
    
    /**
     * Sets next IncludeNode.
     */
    public void setNext(IncludeNode next) {
        nextIncludeNode = next;
    }
    
    /**
     * Returns mapping name of the underline table.
     * 
     * @return mapping name
     */
    public String getMappingName() {
        return (useTableAlias)?tableAlias:home.getTableName();
    }
    
    /**
     * Returns a sql select part of an ActiveRecord class in the following format:
     *      tableName.columnName AS tableName_columnName, ...
     * for all columns of this entity.
     * 
     * @return part of select statement
     */
    public StringBuffer toSqlSelectPart() {
        String tableMappingName = getMappingName();
        String[] columnNames = home.getRowInfo().getColumnNames();
        return getSqlSelectPart(tableMappingName, columnNames);
    }
    
    public static StringBuffer getSqlSelectPart(String tableMappingName, String[] columnNames) {
        StringBuffer sqlPartSB = new StringBuffer();
        int dimension = columnNames.length;
        int lastIndex = dimension - 1;
        for (int i=0; i<lastIndex; i++) {
            sqlPartSB.append(tableMappingName).append(".").append(columnNames[i]);
            sqlPartSB.append(" AS ");
            sqlPartSB.append(tableMappingName).append("_").append(columnNames[i]);
            sqlPartSB.append(", ");
        }
        
        sqlPartSB.append(tableMappingName).append(".").append(columnNames[lastIndex]);
        sqlPartSB.append(" AS ");
        sqlPartSB.append(tableMappingName).append("_").append(columnNames[lastIndex]);
        
        return sqlPartSB;
    }
    
    public StringBuffer toSqlJoinPart() {
        StringBuffer joinSqlSB = new StringBuffer();
        if (relation instanceof HasManyThroughRelation) {
            HasManyThroughRelation hmt = (HasManyThroughRelation)relation;
            ActiveRecord middleC = ActiveRecordUtil.getHomeInstance(hmt.getMiddleC());
            Relation acRel = hmt.getACRelation();
            Relation cbRel = hmt.getCBRelation();
            
            String tableAMappingName = getEndAMappingName();
            String tableBName = home.getTableName();
            String tableCName = middleC.getTableName();
            String tableCAlias = (useTableAlias)?(tableAlias + "_"+ tableCName):tableCName;
            
            joinSqlSB.append(constructSqlJoinPart(tableAMappingName, tableCName, tableCAlias, acRel, useTableAlias, joinType))
                     .append(constructSqlJoinPart(tableCAlias, tableBName, tableAlias, cbRel, useTableAlias, joinType));
            
            String conditions = relation.getConditionsString(tableCName, tableCAlias);
            if (conditions != null && !"".equals(conditions)) joinSqlSB.append(" AND ").append(conditions);
        }
        else {
            String tableAMappingName = getEndAMappingName();
            String tableBName = home.getTableName();
            joinSqlSB.append(constructSqlJoinPart(tableAMappingName, tableBName, tableAlias, relation, useTableAlias, joinType));
        }
        return joinSqlSB;
    }
    
    public String getEndAMappingName() {
        String endA = "";
        if (previousIncludeNode != null) {
            endA = previousIncludeNode.getMappingName();
        }
        else {
            endA = controlHome.getTableName();
        }
        return endA;
    }
    
    private static StringBuffer constructSqlJoinPart(String tableAMappingName, 
                    String tableBName, String tableBAlias, Relation abRelation,
                    boolean useAlias, String joinType) {
        StringBuffer sqlJoinSB = new StringBuffer();
        sqlJoinSB.append(" ").append(joinType).append(" ");
        sqlJoinSB.append(tableBName).append(" ");
        String tableBMappingName = tableBName;
        if (useAlias) {
            sqlJoinSB.append(tableBAlias).append(" ");
            tableBMappingName = tableBAlias;
        }
        sqlJoinSB.append("ON ");
        sqlJoinSB.append(getTableLinks(tableAMappingName, abRelation.getMapping(), tableBMappingName));
        String conditions = abRelation.getConditionsString(tableBName, tableBMappingName);
        if (conditions != null && !"".equals(conditions)) sqlJoinSB.append(" AND ").append(conditions);
        return sqlJoinSB;
    }
    
    protected static String getTableLinks(String tableA, String mappingAB, String tableB) {
        StringBuffer sb = new StringBuffer();
        Map mappingMap = Converters.convertStringToMap(mappingAB);
        int total = mappingMap.size();
        int count = 0;
        Iterator it = mappingMap.keySet().iterator();
        while(it.hasNext()) {
            count++;
            
            String leftField = (String)it.next();
            String rightField = (String)mappingMap.get(leftField);
            
            if (leftField.indexOf('.') == -1) {
                sb.append(tableA).append(".").append(leftField).append("=");
            }
            else {
                sb.append(leftField).append("=");
            }
            
            if (rightField.indexOf('.') == -1) {
                sb.append(tableB).append(".").append(rightField);
            }
            else {
                sb.append(rightField);
            }
            
            if (count < total) sb.append(" AND ");
        }
        
        return sb.toString();
    }
    
    public String toString() {
        String separator = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("entity: " + include).append(separator);
        sb.append("order: " + order).append(separator);
        if (previousIncludeNode != null) {
            sb.append("previous entity: " + previousIncludeNode.getIncludeName()).append(separator);
        }
        else {
            sb.append("previous entity: null").append(separator);
        }
        sb.append("relation: " + relation.getRelationType()).append(separator);
        sb.append("useTableAlias: " + useTableAlias).append(separator);
        sb.append("tableAlias: " + tableAlias).append(separator);
        sb.append("mappingName: " + getMappingName()).append(separator);
        sb.append("joinType: " + joinType);
        return sb.toString();
    }
    
    
    
    /**
     * Entity name
     */
    private String include;
    
    /**
     * Home instance of the control entity
     */
    private ActiveRecord controlHome;
    
    /**
     * Include order of the table
     */
    private int order;
    
    /**
     * Relation from endA entity to (this) entity
     */
    private Relation relation;
    
    /**
     * Home instance of the entity
     */
    private ActiveRecord home;
    
    /**
     * Join type
     */
    private String joinType;
    
    /**
     * Alias name of the table
     */
    private String tableAlias;
    
    /**
     * Boolean indicator on if alias is used
     */
    private boolean useTableAlias = false;
    
    private IncludeNode previousIncludeNode;
    private IncludeNode nextIncludeNode;
}
