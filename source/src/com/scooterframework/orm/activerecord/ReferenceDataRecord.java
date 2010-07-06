/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import com.scooterframework.orm.sqldataexpress.object.RowData;

/**
 * ReferenceDataRecord class
 * 
 * @author (Fei) John Chen
 *
 */
public class ReferenceDataRecord implements ReferenceData {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1538141352929735938L;
	
	public ReferenceDataRecord(String entity, String keyFieldName, String valueFieldName, ActiveRecord record) {
        if (record == null) throw new IllegalArgumentException("record input cannot be null.");
        this.keyFieldName = keyFieldName;
        this.valueFieldName = valueFieldName;
        keyFieldData = record.getField(keyFieldName);
        valueFieldData = record.getField(valueFieldName);
        this.record = record;
    }

    public ReferenceDataRecord(String entity, String keyFieldName, String valueFieldName, RowData row) {
        if (row == null) throw new IllegalArgumentException("row data input cannot be null.");
        this.keyFieldName = keyFieldName;
        this.valueFieldName = valueFieldName;
        keyFieldData = row.getField(keyFieldName);
        valueFieldData = row.getField(valueFieldName);
        this.row = row;
    }
    
    /**
     * Returns data for a field
     * 
     * @param fieldName field name
     * @return Object data for the field
     */
    public Object getFieldData(String fieldName) {
        Object data = null;
        if (record != null) {
            data = record.getField(fieldName);
        }
        else if (row != null) {
            data = row.getField(fieldName);
        }
        return data;
    }
    
    /**
     * Returns key field name
     * 
     * @return String key field name
     */
    public String getKeyName() {
        return keyFieldName;
    }
    
    /**
     * Returns key field data
     * 
     * @return Object key field data
     */
    public Object getKeyData() {
        return keyFieldData;
    }

    /**
     * Returns value field name
     * 
     * @return String value field name
     */
    public String getValueName() {
        return valueFieldName;
    }

    /**
     * Returns value field data
     * 
     * @return Object value field data
     */
    public Object getValueData() {
        return valueFieldData;
    }
    
    public String toString() {
        String separator = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("keyFieldName: " + keyFieldName).append(separator);
        sb.append("keyFieldData: " + keyFieldData).append(separator);
        sb.append("valueFieldName: " + valueFieldName).append(separator);
        sb.append("valueFieldData: " + valueFieldData).append(separator);
        if (record != null) sb.append("record: " + record);
        if (row != null) sb.append("row: " + row);
        return sb.toString();
    }
    
    private String keyFieldName = "";
    private Object keyFieldData = null;
    private String valueFieldName = "";
    private Object valueFieldData = null;
    private ActiveRecord record = null;
    private RowData row = null;
}
