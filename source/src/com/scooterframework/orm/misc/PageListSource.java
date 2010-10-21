/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;

/**
 * PageListSource class sets up basic framework for retrieving paged data.
 * 
 * @author (Fei) John Chen
 */
public abstract class PageListSource {
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param inputOptions Map of control information.
     */
    public PageListSource(Map inputOptions) {
         this(inputOptions, true);
    }
    
    /**
     * Constructs a PageListSource object.
     * 
     * @param inputOptions Map of control information.
     * @param recount <tt>true</tt> if recount of total records is allowed;
     *		    <tt>false</tt> otherwise.
     */
    public PageListSource(Map inputOptions, boolean recount) {
        this.inputOptions = inputOptions;
        if (this.inputOptions == null) {
        	this.inputOptions = new HashMap();
        }
        this.inputOptions.put(DataProcessor.input_key_use_pagination, "Y");
        
        this.recount = recount;
    }
    
    /**
     * Returns offset
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * Sets offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    /**
     * Returns maximum number of records per page
     */
    public int getLimitX() {
        return limit;
    }
    
    /**
     * Sets maximum number of records per page
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    /**
     * Sets recount. True for recount; false for no recount.
     */
    public void setRecount(boolean recount) {
        this.recount = recount;
    }
    
    /**
     * Returns Map of input parameters.
     */
    public Map getInputsX() {
        return inputOptions;
    }
    
    /**
     * Merges data from an input map with the existing inputOptions map.
     */
    public void setInputs(Map inputs) {
        if (inputs == null || inputs.keySet().size() == 0) return;
        
        if (inputOptions != null) inputOptions.putAll(inputs);
    }
    
    /**
     * Returns count of total records.
     */
    public int getTotalCount() {
        return totalCount;
    }
    
    /**
     * Returns retrieved record list.
     */
    public List getRecordList() {
        return recordList;
    }
    
    /**
     * Checks if offset is still in the reasonable range. 
     * 
     * If offset is larger than total number of records, reset it to the 
     * beginning of the last page. 
     */
    protected int checkOffset() {
        if (offset >= totalCount) offset = totalCount - limit;
        return (offset > 0)?offset:0;
    }

    /**
     * Really does the record count and retrieval. 
     */
    protected void execute() {
        // count records
        if (recount || !totalCounted) {
            totalCount = countTotalRecords();
            totalCounted = true;
        }
        
        // make sure offset is still in the reasonable range
        offset = checkOffset();
        
        // reorg parameters by using internal keys
        inputOptions.put(DataProcessor.input_key_records_limit,  new Integer(limit));
        inputOptions.put(DataProcessor.input_key_records_offset, new Integer(offset));
        
        // retrieve data
        if (totalCount > 0) {
            recordList = retrieveList();
        }
    }
    
    /**
     * Counts total number of records. May not be invoked if <tt>recounted</tt> 
     * is <tt>false</tt>.
     * 
     * @return total number of records
     */
    protected abstract int countTotalRecords();
    
    /**
     * Retrieves list of records. 
     * 
     * @return list of records
     */
    protected abstract List retrieveList();

	protected Map inputOptions;
    
    /**
     * Maximum number of records per page
     */
    protected int limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;
    
    /**
     * Boolean variable to indicate whether to recount total records.
     * 
     * True for recount, false for no recount.
     */
    protected boolean recount = true;
    
    /**
     * Offset
     */
    protected int offset;
    
    /**
     * Total number of records
     */
    protected int totalCount;
    
    private boolean totalCounted = false;
    
    /**
     * paged record list
     */
    protected List recordList;
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
