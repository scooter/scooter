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

import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;


/**
 * HasManyThroughRecordRelation class represents a has-many-trough
 * relation instance.
 *
 * For example, A has many C and B has many C. Then we can say A has many B
 * through C.
 *
 * In has-many-through relation, class C should have primary keys of both
 * classes A and B held as foreign keys.
 *
 * Class A is the owner of the relation.
 *
 * @author (Fei) John Chen
 */
public class HasManyThroughRecordRelation extends RecordRelation {
    public HasManyThroughRecordRelation(ActiveRecord record, HasManyThroughRelation relation) {
        super(record, relation);
    }

    /**
     * Find the associated record.
     *
     * For example, "projects" has many "employees" thru "assignments".
     *
     * <pre>
     * SQL: SELECT employees.*
     *        FROM employees INNER JOIN assignments ON employees.id = assignments.eid
     *       WHERE (assignments.pid = ?1 AND (other conditions))
     * </pre>
     *
     * @param options A string of options.
     * @param refresh If true, refresh data from database
     * @return AssociatedRecords
     */
    public AssociatedRecords allAssociatedRecords(String options, boolean refresh) {
        AssociatedRecords relatedData = (AssociatedRecords)getAssociatedData();

        if (!refresh && !optionsChangedFromLastRetrieval(options)) {
            if (relatedData != null && relatedData.hasLoadedFromDatabase()) {
                return relatedData;
            }
            else {
                refresh = true;
            }
        }

        if (refresh || optionsChangedFromLastRetrieval(options)) {
            if (owner.isNewRecord()) {
                if (relatedData == null) {
                    relatedData = new AssociatedRecordsHMT(this);
                    setAssociatedData(relatedData);
                }
            }
            else {
                if (relatedData == null) {
                    relatedData = new AssociatedRecordsHMT(this, retrieveAssociatedDataList(options));
                    setAssociatedData(relatedData);
                }
                else {
                    relatedData.storeLoadedAssociatedRecords(retrieveAssociatedDataList(options));
                }
            }
        }

        super.setLastUsedOptions(options);

        return relatedData;
    }

    protected int countRecordsInDB() {
        if (owner.isNewRecord()) return -1;

        HasManyThroughRelation acbRel = (HasManyThroughRelation)getRelation();
        ActiveRecord homeB = ActiveRecordUtil.getHomeInstance(acbRel.getTargetClass());
        ActiveRecord homeC = ActiveRecordUtil.getHomeInstance(acbRel.getMiddleC());
        String tableB = homeB.getTableName().toLowerCase();
        String tableC = homeC.getTableName().toLowerCase();

        Map<String, String> properties = acbRel.getProperties();

        //construct a sql query
        String countSQL = Calculator.getCountSelectPart(homeB, properties);

        StringBuilder sb = new StringBuilder();
        sb.append(countSQL);
        sb.append(" INNER JOIN ").append(tableC).append(" ON ");
        sb.append(getTableLinks(tableC, acbRel.getCBMapping(), tableB));
        sb.append(" WHERE (");

        //set middleC's FK with owner's PK data
        Map<String, Object> fkData = new HashMap<String, Object>();
        sb.append(getFKDataMapForMiddleC(owner, tableC, acbRel.getACMapping(), fkData));

        //add conditions if there is one
        String conditions = getAllConditionsString(tableB, tableC, acbRel);
        if (conditions != null && !"".equals(conditions)) sb.append(conditions);

        sb.append(")");

        Object returnObj = SqlServiceClient.retrieveObjectBySQL(sb.toString(), fkData);

        return (returnObj != null)?Integer.parseInt(returnObj.toString()):-1;
    }

    protected List<ActiveRecord> retrieveAssociatedDataList(String options) {
        HasManyThroughRelation acbRel = (HasManyThroughRelation)getRelation();
        ActiveRecord homeB = ActiveRecordUtil.getHomeInstance(acbRel.getTargetClass());
        ActiveRecord homeC = ActiveRecordUtil.getHomeInstance(acbRel.getMiddleC());
        String tableB = homeB.getTableName().toLowerCase();
        String tableC = homeC.getTableName().toLowerCase();

        Map<String, String> prop = acbRel.getProperties();
        if (options != null && !"".equals(options)) {
            if (prop == null) prop = new HashMap<String, String>();
            Map<String, String> m = Converters.convertSqlOptionStringToMap(options);
            prop.putAll(m);
        }

        StringBuilder innerJoinSB = new StringBuilder();
        innerJoinSB.append(" INNER JOIN ").append(tableC).append(" ON ");
        innerJoinSB.append(getTableLinks(tableC, acbRel.getCBMapping(), tableB));

        //set middleC's FK with owner's PK data
        Map<String, Object> fkData = new HashMap<String, Object>();
        String midCMapping = getFKDataMapForMiddleC(owner, tableC, acbRel.getACMapping(), fkData);

        String conditionsSQL = getAllConditionsString(tableB, tableC, acbRel);

        List<ActiveRecord> list = null;
        if (prop.containsKey(ActiveRecordConstants.key_include) ||
        	prop.containsKey(ActiveRecordConstants.key_strict_include)) {
            list = ActiveRecordUtil.getGateway(homeB).internal_findAll_include_hmt((Map<String, Object>)null, prop, innerJoinSB.toString(), midCMapping, fkData, conditionsSQL);
        }
        else {
	        //construct a sql query
	        StringBuilder sb = new StringBuilder();
	        sb.append(ActiveRecordUtil.getGateway(homeB).getFinderSql(prop));
	        sb.append(innerJoinSB);
	        sb.append(" WHERE (");
	        sb.append(midCMapping);

	        //add conditions if there is one
	        if (conditionsSQL != null && !"".equals(conditionsSQL)) sb.append(conditionsSQL);

	        sb.append(")");

	        sb.append(QueryHelper.getAllSelectQueryClauses(prop));

	        list = ActiveRecordUtil.getGateway(homeB).findAllBySQL(sb.toString(), fkData);
        }

        return list;
    }

    protected String getTableLinks(String table1, String mapping12, String table2) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> mappingMap = Converters.convertStringToMap(mapping12);
        int total = mappingMap.size();
        int count = 0;
        for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
            count++;

            String leftField = entry.getKey();
            String rightField = entry.getValue();

            if (leftField.indexOf('.') == -1) {
                sb.append(table1).append(".").append(leftField).append("=");
            }
            else {
                sb.append(leftField).append("=");
            }

            if (rightField.indexOf('.') == -1) {
                sb.append(table2).append(".").append(rightField);
            }
            else {
                sb.append(rightField);
            }

            if (count < total) sb.append(" AND ");
        }

        return sb.toString();
    }

    protected String getFKDataMapForMiddleC(ActiveRecord recordA, String tableC, String mappingAC, Map<String, Object> fkData) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> mappingMap = Converters.convertStringToMap(mappingAC);
        int total = mappingMap.size();
        int count = 0;
        for (Map.Entry<String, String> entry : mappingMap.entrySet()) {
            count++;

            String leftField = entry.getKey();
            String rightField = entry.getValue();
            String fullRightField = tableC + "." + rightField;
            fkData.put(fullRightField, recordA.getField(leftField));
            sb.append(fullRightField).append("=");
            sb.append("?").append(fullRightField);

            if (count < total) sb.append(" AND ");
        }

        return sb.toString();
    }

    /**
     * Creates a concatenated conditions string.
     *
     * This is a condition string which includes conditions from AB + AC + CB.
     *
     * @param acbRel HasManyThrough relation
     * @return conditions a string of all related conditions
     */
    protected String getAllConditionsString(String tableB, String tableC, HasManyThroughRelation acbRel) {
        StringBuilder sb = new StringBuilder();

        //add abConditions if there is one
        String abConditions = acbRel.getConditionsString();
        if (abConditions != null && !"".equals(abConditions)) sb.append(" AND (").append(abConditions).append(")");

        //add acConditions if there is one
        Relation acRel = acbRel.getACRelation();
        if (acRel == null) throw new UndefinedRelationException(acbRel.getOwnerClass(), acbRel.getMiddleC());
        String acConditions = acRel.getConditionsString();
        if (acConditions != null && !"".equals(acConditions)) sb.append(" AND (").append(acConditions).append(")");

        //add cbConditions if there is one
        Relation cbRel = acbRel.getCBRelation();
        if (cbRel == null) throw new UndefinedRelationException(acbRel.getMiddleC(), acbRel.getTargetClass());
        String cbConditions = cbRel.getConditionsString();
        if (cbConditions != null && !"".equals(cbConditions)) sb.append(" AND (").append(cbConditions).append(")");

        return sb.toString();
    }
}
