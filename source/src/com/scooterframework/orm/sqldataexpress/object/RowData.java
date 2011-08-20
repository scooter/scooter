/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;

/**
 * RowData class represents a row in TableData object.
 *
 * @author (Fei) John Chen
 */
public class RowData implements RESTified, Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 891263627275680195L;

	public RowData(RowInfo rowInfo, Object[] data) {
        if (rowInfo != null) {
            this.rowInfo = rowInfo;
        }
        else {
            this.rowInfo = new RowInfo();
        }

        if (data == null) {
            if (rowInfo.getDimension() >0)
                this.data = rowInfo.getColumnDefaults();
        }
        else {
            initialized = true;
            this.data = data;
        }

        //set dataMap
        createDataMap();
    }

    /**
     * <p>
     * Returns the restified id of the resource.
     * </p>
     *
     * <p>By default, this method returns a string of the primary key value of
     * the record. If the primary key is a composite key, a separator
     * ({@link com.scooterframework.orm.sqldataexpress.config.DatabaseConfig#PRIMARY_KEY_SEPARATOR}) is used
     * to link values of the key fields. The order of the fields of a composite
     * primary key is defined by the {@link #getRestfulIdNames getRestfulIdNames} method.
     * </p>
     *
     * <p>If the underline data does not have primary key, <tt>null</tt> is
     * returned.</p>
     *
     * <p>Subclass may override this method if a customized string format is
     * required.</p>
     *
     * @return id String
     */
    public String getRestfulId() {
        if (!hasInitialized()) return null;

        String[] idNames = getRestfulIdNames();

        if (idNames == null || idNames.length == 0) return null;

        String result = "";
        int total = idNames.length;
        for (int i = 0; i < total-1; i++) {
            Object o = getField(idNames[i]);
            String pkValue = (o != null)?o.toString():"";
            result += pkValue + DatabaseConfig.PRIMARY_KEY_SEPARATOR;
        }
        result += getField(idNames[total-1]);
        return result;
    }

    /**
     * Returns column names corresponding to the RESTful id. If there is
     * primary key, the column names come from primary key. Otherwise,
     * <tt>null</tt> is returned.
     *
     * @return a string array
     */
    public String[] getRestfulIdNames() {
        return (rowInfo != null)?rowInfo.getPrimaryKeyColumnNames():null;
    }

    /**
     * Returns the data map for the restified id. By default, the keys in the
     * map are primary key column names in lower case. If there is no primary
     * key, an empty map is returned.
     *
     * @return map of restified id data
     */
    public Map<String, Object> getRestfulIdMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!hasInitialized()) return map;

        String[] idNames = getRestfulIdNames();
        if (idNames == null) return map;

        int total = idNames.length;
        for (int i = 0; i < total-1; i++) {
            String key = idNames[i];
            Object value = getField(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * <p>Sets the id value of the resource. The format of the id string must
     * follow the pattern of the corresponding id config. If the id is backed
     * by a composite primary key, a separator
     * ({@link com.scooterframework.orm.sqldataexpress.config.DatabaseConfig#PRIMARY_KEY_SEPARATOR})
     * must be used to link values of each primary key column.</p>
     *
     * <pre>
     * Examples:
     *   id string          id config array         description
     *   ---------          ---------------         -------
     *     0001             [id]                    an order
     *     0001-99          [order_id, id]          an item of an order
     *
     * </pre>
     *
     * @param id
     */
    public void setRestfulId(String id) {
        if (id == null) throw new IllegalArgumentException("Input id is null.");

        String[] ids = Converters.convertStringToStringArray(id, DatabaseConfig.PRIMARY_KEY_SEPARATOR, false);
        String[] fields = getRestfulIdNames();
        if (ids.length != fields.length)
            throw new IllegalArgumentException("Input id does not match id config.");

        int total = ids.length;
        for (int i = 0; i < total; i++) {
            setField(fields[i], ids[i]);
        }
    }

    /**
     * returns plain data
     */
    public Object[] getFields() {
        return data;
    }

    /**
     * <p>Sets data.</p>
     *
     * <p>The order of values in the data array must be the same as the order of
     * column names in the RowInfo object of this RowData instance.</p>
     */
    public void setFields(Object[] data) {
        this.data = data;

        //reset dataMap
        createDataMap();
    }

    /**
     * Returns column data for a column index
     *
     * index: 0, 1, 2, ...
     */
    public Object getField(int index) {
        if (data == null || index >= data.length) return null;
        return data[index];
    }

    /**
     * Sets column data for a column index
     *
     * index: 0, 1, 2, ...
     */
    public void setField(int index, Object columnData) {
        if (data == null || index >= data.length) return;
        data[index] = columnData;
        createDataMap();
    }

    /**
     * Returns column data for a column name
     */
    public Object getField(String columnName) {
        if (data == null || columnName == null) return null;
        return dataMap.get(columnName.toUpperCase());
    }

    /**
     * Sets column data for a column name
     *
     * If there is no such a columnName, an InvalidColumnNameException
     * will be thrown.
     */
    public void setField(String columnName, Object columnData) {
        if (data == null || columnName == null) return;
        if (rowInfo == null || rowInfo.getDimension() == 0) return;
        int columnIndex = rowInfo.getColumnPositionIndex(columnName);
        data[columnIndex] = columnData;
        dataMap.put(columnName.toUpperCase(), columnData);
    }

    /**
     * returns row meta data
     */
    public RowInfo getRowInfo() {
        return rowInfo;
    }

    /**
     * sets row meta data
     */
    void setRowInfo(RowInfo newRowInfo) {
        if (newRowInfo != null) {
            if (newRowInfo.getDimension() != rowInfo.getDimension()) {
                throw new IllegalArgumentException("The input newRowInfo " +
                "must have the same dimension as the original rowInfo.");
            }
            rowInfo = newRowInfo;
        }
        else {
            throw new IllegalArgumentException("\"rowInfo\" input cannot be null in setRowInfo().");
        }

        //reset dataMap
        createDataMap();
    }

    /**
     * Clears all existing data except the primary key data.
     *
     */
    public void clearData() {
        if (data != null && data.length > 0) {
            int columnSize = data.length;
            for (int i=0; i<columnSize; i++) {
                ColumnInfo ci = rowInfo.getColumnInfo(i);
                if (ci == null || ci.isPrimaryKey()) continue;

                data[i] = null;
                dataMap.put(ci.getColumnName().toUpperCase(), null);
            }
        }
    }

    /**
     * Clears all existing data and resets data from a Map.
     *
     * The key of the data entry in the Map is corresponding to a
     * column name in the RowInfo object. If the key is not a column name,
     * its value is ignored. If the column name is not in the key set of
     * the Map, the column data is set to null.
     *
     */
    public void clearAndSetData(Map<String, ?> inputDataMap) {
        clearData();
        setData(inputDataMap);
    }

    /**
     * <p>Sets data from a Map.</p>
     *
     * <p>The key of the data entry in the Map is corresponding to a
     * column name in the RowInfo object. If the key is not a column name, its
     * value is ignored. If the column name is not in the key set of the
     * Map, the column data is not updated. To set those column data
     * to null when the column name is not in the key set, use the
     * {@link #setField(java.lang.String, java.lang.Object) setField} method.</p>
     *
     * <p>This method is restrictive. If a column is readonly, or not writable,
     * or is primary key, then the data for the column in the
     * <tt>inputDataMap</tt> is ignored. In that case, use the
     * {@link #setField(java.lang.String, java.lang.Object) setField} method</p>
     *
     * @return a list of modified column names
     */
    public List<String> setData(Map<String, ?> inputDataMap) {
        if (inputDataMap == null) return null;

        if (rowInfo == null)
            throw new IllegalArgumentException("Failed to setData(Map) for a " +
                "RowData instance because the instance has no RowInfo.");

        //convert all keys to capital font strings
        Map<String, Object> tmp = StringUtil.convertKeyToUpperCase(inputDataMap);

        Set<String> inputKeys = tmp.keySet();
        int recordWidth = rowInfo.getDimension();

        //create a new data array if there isn't.
        if (data == null) data = new Object[recordWidth];

        ColumnInfo ci = null;
        List<String> modifiedColumnList = new ArrayList<String>();
        for (int i=0; i<recordWidth; i++) {
            ci = rowInfo.getColumnInfo(i);
            String columnName = ci.getColumnName();
            if (columnName == null) continue;

            columnName = columnName.toUpperCase();
            if (ci.isReadOnly() || !ci.isWritable() ||
                !inputKeys.contains(columnName)) continue;

            data[i] = tmp.get(columnName);
            modifiedColumnList.add(columnName);
        }

        //reset dataMap
        createDataMap();

        return modifiedColumnList;
    }

    /**
     * returns the data as a readonly Map. The keys in the Map are
     * column names in upper case.
     *
     * Modifications to this map have no impact on the row data.
     */
    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    /**
     * returns the data as a readonly Map. The keys in the Map are
     * requested column names in upper case.
     *
     * Modifications to this map have no impact on the row data.
     */
    public Map<String, Object> getDataMap(List<String> columnNames) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        if (columnNames == null) return newMap;

        Iterator<String> it = columnNames.iterator();
        while(it.hasNext()) {
            String columnName = it.next();
            Object columnData = getField(columnName);
            newMap.put(columnName, columnData);
        }
        return newMap;
    }

    /**
     * returns primary key data as a readonly Map. The keys in the
     * Map are primary key column names in lower case.
     *
     * Modifications to this map have no impact on the row data.
     */
    public Map<String, Object> getPrimaryKeyDataMap() {
        if (rowInfo == null) return new HashMap<String, Object>();

        Map<String, Object> pkDataMap = new HashMap<String, Object>();
        String[] pkNames = rowInfo.getPrimaryKeyColumnNames();
        if (pkNames == null) return pkDataMap;

        int pkColCount = pkNames.length;
		for (int i = 0; i < pkColCount; i++) {
            String pkName = pkNames[i];
            Object colData = null;
            if (data != null && data.length > 0) {
                colData = getField(pkName);
            }
            pkDataMap.put(pkName.toLowerCase(), colData);
        }

        return pkDataMap;
    }

    /**
     * returns primary key data as a string which comes from the primary key
     * data map.
     *
     * @return string
     */
    public String getPrimaryKeyDataString() {
        Map<String, Object> pkMap = getPrimaryKeyDataMap();
        if (pkMap == null) return null;
        return pkMap.toString();
    }

    /**
     * Indicates if there is primary key defined.
     *
     * @return true if there is primary key defined.
     */
    public boolean hasPrimaryKey() {
        return (rowInfo != null)?rowInfo.hasPrimaryKey():false;
    }

    /**
     * Checks if the record is initialized.
     *
     * @return true if the record is initialized
     */
    public boolean hasInitialized() {
        return initialized;
    }

    /**
     * returns columnSize
     */
    public int getSize() {
        return (data != null)?data.length:0;
    }

    /**
     * returns a list of RowData objects for a child from Map
     */
    public List<RowData> getChildRowListFromMap(String key) {
        if (key == null) return null;
        return childRowListMap.get(key.toUpperCase());
    }

    /**
     * adds a child RowData object to Map
     */
    public void addChildRowToMap(String key, RowData childRow) {
        List<RowData> childRowList = getChildRowListFromMap(key);
        if (childRowList != null) childRowList.add(childRow);
    }

    /**
     * adds a list of child RowData objects to Map
     */
    public void addChildRowToMap(String key, List<RowData> childRowList) {
        if (key == null) return;
        childRowListMap.put(key.toUpperCase(), childRowList);
    }

    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String LINE_BREAK = "\r\n";

        returnString.append("columnSize = " + getSize());
        returnString.append(LINE_BREAK);
        returnString.append("data (separated by comma): ");

        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                returnString.append(data[i] + ", ");
            }
        }
        returnString.append(LINE_BREAK);

        returnString.append("childMap size = " + childRowListMap.size());
        returnString.append(LINE_BREAK);

		for (Map.Entry<String, List<RowData>> entry : childRowListMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            returnString.append("child key = " + key + " content = " + value);
            returnString.append(LINE_BREAK);
        }

        return returnString.toString();
    }



    /**
     * set a Map of column name and column data.
     */
    private void createDataMap() {
        if (rowInfo == null)
            throw new IllegalArgumentException("RowInfo must be set first.");

        initialized = true;

        dataMap.clear();

        if (data != null && data.length > 0) {
            int columnSize = data.length;
            for (int i=0; i<columnSize; i++) {
                ColumnInfo ci = rowInfo.getColumnInfo(i);
                if (ci == null)
                    throw new IllegalArgumentException("There is no ColumnInfo for index " + i);
                dataMap.put(ci.getColumnName().toUpperCase(), data[i]);
            }
        }
    }

    private boolean initialized = false;
    private RowInfo rowInfo = new RowInfo();
    private Object[] data = null;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private Map<String, List<RowData>> childRowListMap = new HashMap<String, List<RowData>>();
}
