/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.util.List;
import java.util.Map;

/**
 * OmniDTOBasic interface defines methods for all situations.
 * 
 * @author (Fei) John Chen
 */
public interface OmniDTOBasic extends java.io.Serializable
{
    /**
     * returns processor name
     */
    public String getProcessorName();
    
    /**
     * sets processor name
     */
    public void setProcessorName(String processorName);
    
    /**
     * returns processor type
     */
    public String getProcessorType();
    
    /**
     * sets processor type
     */
    public void setProcessorType(String processorType);
    
    
    /**
     * returns tableData corresponding to the key of the tableDataMap. The 
     * key is by default the name of the cursor. 
     */
    public TableData getTableData(String key);
    
     /**
     * returns tableDataMap. The TableDataMap is a Map that contains 
     * output for every cursor. The key is the name of the cursor. The value 
     * is a TableData. 
     */
    public Map<String, TableData> getTableDataMap();
    
    /**
     * adds a TableData
     */
    public void addTableData(String tableName, TableData tableData);
    
    /**
     * returns childrenOmniDTOList
     */
    public List<OmniDTO> getChildrenOmniDTOList();
    
    /**
     * returns childrenOmniDTO from List
     */
    public OmniDTO getChildrenOmniDTOFromList(int index);
    
    /**
     * adds a childrenOmniDTO to List
     */
    public void addChildrenOmniDTOToList(OmniDTO returnTO);
    
    /**
     * returns childrenOmniDTOMap
     */
    public Map<String, OmniDTO> getChildrenOmniDTOMap();
    
    /**
     * returns childrenOmniDTO from Map
     */
    public OmniDTO getChildrenOmniDTOFromMap(String key);
    
    /**
     * adds a childrenOmniDTO to Map
     */
    public void addChildrenOmniDTOToMap(String key, OmniDTO returnTO);
    
    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString();
    
    /**
     * Returns a XML string representation of the object.
     * @return String
     */
    public String toXML();
}
