/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Iterator;
import java.util.List;

import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.common.validation.Validators;

/**
 * ModelValidators contains validation methods to validate a model record.
 * 
 * <p>
 * Subclass of ActiveRecord must overrise one or more of the following methods 
 * in order to use validations:
 * <ul>
 *   <li>validatesRecord() (applies to create, update, and save a record)</li>
 *   <li>validatesRecordBeforeCreate() (applies to create a record)</li>
 *   <li>validatesRecordBeforeUpdate() (applies to update a record)</li>
 *   <li>validatesRecordBeforeSave() (applies to save a record)</li>
 *   <li>validatesRecordBeforeDelete() (applies to delete a record)</li>
 * </ul>
 * </p>
 * 
 * <p>
 * To use a different ModelValidators class, subclass of ActiveRecord must 
 * overrise validators() method.
 * </p>
 * 
 * <pre>
 * Examples:
 *      public class Post extends ActiveRecord {
            public void registerRelations() {
                belongsTo(Topic.class, "mapping: topic_id=id; counter_cache: true");
                belongsTo(User.class, "mapping: user_id=id; counter_cache: true");
            }
            
            public void validatesRecord() {
                validators().validatesPresenceOf("body");
                validators().validatesLengthMaximum("body", 10000);
            }
        }
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class ModelValidators extends Validators {

    /**
     * <p>Construct a validator.</p>
     * 
     * @param record an ActiveRecord instance
     */
    public ModelValidators(ActiveRecord record) {
        this.record = record;
    }
    
    /**
     * Validates each field data is unique.
     * 
     * @param columnNames A string of column names separated by comma or blank
     * @param messageKey key to the message in MessagesResources file
     */
    public void validatesUniqenessOf(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                boolean numeric = isNumericColumn(colName);
                String options = "conditions_sql: " + colName + "=";
                
                if (numeric) {
                    options += colData;
                }
                else {
                    options += StringUtil.doubleSingleQuoteInString((String)colData);
                }
                
                long total = record.getCalculator().count(colName, options);
                if (total > 0) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
        }
    }
    
    
    /**
     * Returns validation results.
     * 
     * @return validation results
     */
    protected ValidationResults getValidationResults() {
        return record.getValidationResults();
    }
    
    /**
     * Returns value for the specific <tt>fieldName</tt> of the underlying record.
     * 
     * @param fieldName
     * @return value for the field
     */
    protected Object getData(String fieldName) {
        return record.getField(fieldName);
    }
    
    private boolean isNumericColumn(String colName) {
        return record.getRowInfo().isNumericColumn(colName);
    }
    
    /**
     * <p>The current record instance.</p>
     */
    protected ActiveRecord record;
}
