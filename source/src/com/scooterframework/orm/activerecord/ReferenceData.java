/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * ReferenceData interface contains common methods to access reference data.
 * 
 * @author (Fei) John Chen
 */
public interface ReferenceData extends java.io.Serializable {
    
    /**
     * Returns data for a field
     * 
     * @param fieldName field name
     * @return Object data for the field
     */
    public Object getFieldData(String fieldName);
    
    /**
     * Returns key field name
     * 
     * @return String key field name
     */
    public String getKeyName();
    
    /**
     * Returns key field data
     * 
     * @return Object key field data
     */
    public Object getKeyData();

    /**
     * Returns value field name
     * 
     * @return String value field name
     */
    public String getValueName();

    /**
     * Returns value field data
     * 
     * @return Object value field data
     */
    public Object getValueData();
}
