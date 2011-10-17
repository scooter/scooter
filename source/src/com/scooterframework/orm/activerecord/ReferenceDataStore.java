/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ReferenceDataStore class holds static reference data for all users.
 * 
 * @author (Fei) John Chen
 * 
 */
public class ReferenceDataStore implements java.io.Serializable {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5382025995336758728L;
    
    private static Map<String, List<ReferenceData>> refData = new HashMap<String, List<ReferenceData>>();
    private static Date refDataLoadedTime = null;
	
	/**
     * Gets ReferenceData by type and key
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndKey(String type, String keyData) {
        if (keyData == null) return null;
        
        ReferenceData rd = null;

        List<ReferenceData> list = refData.get(type);
        if ( list != null ) {
            Iterator<ReferenceData> it = list.iterator();
            while (it.hasNext()) {
                ReferenceData tmp = it.next();
                if (tmp != null && tmp.getKeyData() != null && 
                		keyData.equalsIgnoreCase(tmp.getKeyData().toString())) {
                    rd = tmp;
                    break;
                }
            }
        }

        return rd;
    }
    
    /**
     * Gets ReferenceData by type and value
     * 
     * @return  ReferenceData
     */
    public static ReferenceData getReferenceDataByTypeAndValue(String type, Object valueData) {
        if (valueData == null) return null;
        
        ReferenceData rd = null;

        List<ReferenceData> list = refData.get(type);
        if ( list != null ) {
            Iterator<ReferenceData> it = list.iterator();
            while (it.hasNext()) {
                ReferenceData tmp = it.next();
                if (tmp != null && tmp.getValueData() != null && 
                		valueData.toString().equalsIgnoreCase(tmp.getValueData().toString())) {
                    rd = tmp;
                    break;
                }
            }
        }

        return rd;
    }

    /**
     * Gets a list of ReferenceData instances for a certain type
     * 
     * @return List
     */
    public static List<ReferenceData> getReferenceDataList(String type) {
        return refData.get(type);
    }

    /**
     * Sets ReferenceData map
     *
     * @param dataMap a map of reference data
     */
    public static void setReferenceData(Map<String, List<ReferenceData>> dataMap) {
        if (dataMap == null || dataMap.size() == 0) return;
        refData = dataMap;
        refDataLoadedTime = new Date();
    }

    /**
     * Sets ReferenceData for a certain type
     *
     * @param type type of the data
     * @param data a list of reference data
     */
    public static void setReferenceData(String type, List<ReferenceData> data) {
        if (data == null) return;
        
        refData.put(type, data);
        refDataLoadedTime = new Date();
    }

    /**
     * Returns the time when reference data is loaded
     *
     * @return a date instance
     */
    public static Date getLastReferenceDataLoadedTime() {
        return (refDataLoadedTime == null)?refDataLoadedTime:(new Date(refDataLoadedTime.getTime()));
    }
}
