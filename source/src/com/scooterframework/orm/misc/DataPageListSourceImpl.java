/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DataPageListSourceImpl class retrieves paged record list from an existing 
 * data list. 
 * 
 * @author (Fei) John Chen
 */
public class DataPageListSourceImpl extends PageListSource {
    /**
     * Constructs a PageListSource object.
     * 
     * @param dataList a list of data. 
     */
    public DataPageListSourceImpl(List dataList) {
         this(dataList, null);
    }
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param dataList a list of data to be paginated.
     * @param inputOptions Map of control information.
     */
    public DataPageListSourceImpl(List dataList, Map inputOptions) {
         this(dataList, inputOptions, true);
    }
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param dataList a list of data to be paginated.
     * @param inputOptions Map of control information.
     * @param recount <tt>true</tt> if recount of total records is allowed;
     *		    <tt>false</tt> otherwise.
     */
    public DataPageListSourceImpl(List dataList, Map inputOptions, boolean recount) {
        super(inputOptions, recount);
        this.dataList = dataList;
    }
    
    protected int countTotalRecords() {
    	return (dataList != null)?dataList.size():0;
    }
    
    protected List retrieveList() {
        if (dataList == null) return null;
        
        List pagedList = new ArrayList(limit);
        int startIndex = offset + 1;
        int endIndex   = offset + limit;
        if (endIndex > totalCount) endIndex = totalCount;
        
        for (int i=startIndex; i<=endIndex; i++) {
            pagedList.add(dataList.get(i-1));
        }
        
        return pagedList;
    }
    
    /**
     * data list
     */
    private List dataList;
}
