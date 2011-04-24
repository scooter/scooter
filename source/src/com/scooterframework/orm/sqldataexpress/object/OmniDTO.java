/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OmniDTO class.
 *
 * @author (Fei) John Chen
 */
public class OmniDTO implements OmniDTOJdbcStatement, OmniDTOStoredProcedure, OmniDTOStoredFunction
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -382384159510043212L;

	//spoc specific keys
    public static final String RETURN_CODE_KEY    = "PN_RESULTOUT";
    public static final String MESSAGE_KEY        = "PC_MESSAGEOUT";
    public static final String NEWUPDATEDATE_KEY  = "PD_NEWUPDATEDATEOUT";

    //jdbc specific keys
    public static final String UPDATED_ROW_COUNT  = "UPDATED_ROW_COUNT";
    public static final String GENERATED_KEY      = "GENERATED_KEY";

    public OmniDTO() {}


    /**
     * returns returnCode
     */
    public String getReturnCode() {
        Object rc = resultMap.get(RETURN_CODE_KEY);
        return (rc==null)?null:rc.toString();
    }

    /**
     * returns returnMessage
     */
    public String getReturnMessage() {
        return (String)resultMap.get(MESSAGE_KEY);
    }

    /**
     * returns newUpdateDate
     */
    public Date getNewUpdateDate() {
        return (Date)resultMap.get(NEWUPDATEDATE_KEY);
    }

    /**
     * returns updatedRowCount
     */
    public int getUpdatedRowCount() {
        Integer Count = (Integer)resultMap.get(UPDATED_ROW_COUNT);
        return (Count != null)?Count.intValue():-1;
    }

    /**
     * sets updatedRowCount
     */
    public void setUpdatedRowCount(int updatedRowCount) {
        resultMap.put(UPDATED_ROW_COUNT, Integer.valueOf(updatedRowCount));
    }

    /**
     * returns generated key value as long.
     *
     * Note: Only one primary key column is allowed to be auto generated.
     *
     * return -1 if the underline database does not support generatedKeys
     * feature or if the sql statement is not a ddl statement.
     */
    public long getGeneratedKey() {
        Long Key = (Long)resultMap.get(GENERATED_KEY);
        return (Key != null)?Key.longValue():-1;
    }

    /**
     * sets getGeneratedKey
     */
    public void setGeneratedKey(long generatedKey) {
        resultMap.put(GENERATED_KEY, Long.valueOf(generatedKey));
    }

    /**
     * returns processor name
     */
    public String getProcessorName() {
        return processorName;
    }

    /**
     * sets processor name
     */
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    /**
     * returns processor type
     */
    public String getProcessorType() {
        return processorType;
    }

    /**
     * sets processor type
     */
    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    /**
     * The resultObjectMap is a Map which contains non-cursor result.
     *
     * returns resultObjectMap
     */
    public Map<String, Object> getResultObjectMap() {
        return resultMap;
    }

    /**
     * adds a (name,value) pair to resultMap
     */
    public void addNamedObject(String name, Object value) {
        resultMap.put(name, value);
    }

    /**
     * returns result from database function call.
     *
     * returns Object result from the database function call
     */
    public Object getFunctionCallResult() {
        return resultMap.get(Parameter.FUNCTION_RETURN);
    }

    /**
     * returns tableData corresponding to the key of the tableDataMap. The
     * key is by default the name of the cursor.
     */
    public TableData getTableData(String key) {
        if (key == null) return null;

        return tableDataMap.get(key.toUpperCase());
    }

    /**
     * returns tableDataMap. The TableDataMap is a Map that contains
     * output for every cursor. The key is the name of the cursor. The value
     * is a TableData.
     */
    public Map<String, TableData> getTableDataMap() {
        return tableDataMap;
    }

    /**
     * adds a TableData
     */
    public void addTableData(String tableName, TableData tableData) {
        if (tableName == null) return;

        tableDataMap.put(tableName.toUpperCase(), tableData);
    }

    /**
     * returns childrenOmniDTO from List
     */
    public OmniDTO getChildrenOmniDTOFromList(int index) {
        return childrenOmniDTOList.get(index);
    }

    /**
     * returns childrenOmniDTOList
     */
    public List<OmniDTO> getChildrenOmniDTOList() {
        return childrenOmniDTOList;
    }

    /**
     * adds a childrenOmniDTO to List
     */
    public void addChildrenOmniDTOToList(OmniDTO returnTO) {
        childrenOmniDTOList.add(returnTO);
    }

    /**
     * returns childrenOmniDTO from Map
     */
    public OmniDTO getChildrenOmniDTOFromMap(String key) {
        if (key == null) return null;

        return (OmniDTO) childrenOmniDTOMap.get(key.toUpperCase());
    }

    /**
     * returns childrenOmniDTOMap
     */
    public Map<String, OmniDTO> getChildrenOmniDTOMap() {
        return childrenOmniDTOMap;
    }

    /**
     * adds a childrenOmniDTO to Map
     */
    public void addChildrenOmniDTOToMap(String key, OmniDTO returnTO) {
        if (key == null) return;

        childrenOmniDTOMap.put(key.toUpperCase(), returnTO);
    }

    /**
     * Returns a XML string representation of the object.
     * @return String
     */
    public String toXML() {
        throw new IllegalArgumentException("To be implemented.");
    }

    /**
     * Returns method names that are only useful when the underline processor
     * is a StoredProcedureProcessor.
     */
    public String getMethodNamesSupportingStoredProcedureProcessorOnly() {
        return "getReturnCode, getReturnMessage, getNewUpdateDate";
    }

    /**
     * Returns method names that are only useful when the underline processor
     * is a FunctionProcessor.
     */
    public String getMethodNamesSupportingFunctionProcessorOnly() {
        return "getFunctionCallResult";
    }

    /**
     * Returns method names that are only useful when the underline processor
     * is a JdbcStatementProcessor.
     */
    public String getMethodNamesSupportingJdbcStatementProcessorOnly() {
        return "getUpdatedRowCount, setUpdatedRowCount, getGeneratedKey, setGeneratedKey";
    }

    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String LINE_BREAK = "\r\n";

        returnString.append(LINE_BREAK);
        returnString.append("returnCode = " + getReturnCode());
        returnString.append(LINE_BREAK);
        returnString.append("returnMessage = " + getReturnMessage());
        returnString.append(LINE_BREAK);
        returnString.append("newUpdateDate = " + getNewUpdateDate());
        returnString.append(LINE_BREAK);
        returnString.append("rowCount = " + getUpdatedRowCount());
        returnString.append(LINE_BREAK);
        returnString.append("processorName = " + processorName);
        returnString.append(LINE_BREAK);
        returnString.append("resultMap = " + resultMap);
        returnString.append(LINE_BREAK);

        returnString.append("tableDataMap: ");
        returnString.append(LINE_BREAK);

        for (Map.Entry<String, TableData> entry : tableDataMap.entrySet()) {
            returnString.append("tableDataMap key = " + entry.getKey() + " Content:");
            returnString.append(LINE_BREAK);
            returnString.append(entry.getValue());
        }
        returnString.append(LINE_BREAK);

        returnString.append("childrenOmniDTOList size = " + childrenOmniDTOList.size());
        returnString.append(LINE_BREAK);

        Iterator<OmniDTO> cit = childrenOmniDTOList.iterator();
        int cindex = 0;
        while(cit.hasNext()) {
            cindex = cindex + 1;
            OmniDTO rto = cit.next();
            returnString.append("childrenOmniDTOList index = " + cindex + " Content:");
            returnString.append(LINE_BREAK);
            returnString.append(rto);
            returnString.append(LINE_BREAK);
        }
        returnString.append(LINE_BREAK);

        returnString.append("childrenOmniDTOMap size = " + childrenOmniDTOMap.size());
        returnString.append(LINE_BREAK);

        for (Map.Entry<String, OmniDTO> entry : childrenOmniDTOMap.entrySet()) {
            returnString.append("childrenOmniDTOMap key = " + entry.getKey() + " Content:");
            returnString.append(LINE_BREAK);
            returnString.append(entry.getValue());
            returnString.append(LINE_BREAK);
        }
        return returnString.toString();
    }


    private String processorName = "";
    private String processorType = "";
    private Map<String, Object> resultMap = new HashMap<String, Object>();
    private Map<String, TableData> tableDataMap = new HashMap<String, TableData>();
    private List<OmniDTO> childrenOmniDTOList = new ArrayList<OmniDTO>();
    private Map<String, OmniDTO> childrenOmniDTOMap = new HashMap<String, OmniDTO>();
}
