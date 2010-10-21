/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.activerecord.ActiveRecordConstants;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;

/**
 * <p>Paginator class manages pagination of records of a model object. </p>
 * 
 * <p>Any information on a url link can be passed to this object through 
 * controlOptions parameter. </p>
 * 
 * <p>The following keys have an impact on the result of pagination:</p>
 * 
 * <ul>
 * <li>key_limit "<tt>limit</tt>": specifies limit of a page, default to 15.</li>
 * <li>key_npage "<tt>npage</tt>": specifies the number of the page to be opened by this click.</li>
 * <li>key_order_by "<tt>order_by</tt>": order by clause.</li>
 * <li>key_sort "<tt>sort</tt>": column to be sorted.</li>
 * <li>key_order "<tt>order</tt>": sort direction, either "up" (default) or "down".</li>
 * </ul>
 * <p>Note: Either use key_order_by or use key_sort and key_order together.</p>
 * 
 * <p>The following keys are for information only:
 * key_cpage = "<tt>cpage</tt>": specifies the origin page number of the current click. 
 * key_link  = "<tt>r</tt>": specifies the origin place of the current click.</p>
 * 
 * <p>If a key/value pair is not used by the paginator, it will reappear in 
 * query string outputs. </p>
 * 
 * <p>In addition, all sql related information can be passed to the paginator 
 * through its PageListSource instance.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * Paginator page = new Paginator(new JdbcPageListSource(modelClass), controlOptions);
 * List pagedRecords = page.getRecordList();
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class Paginator
{
    /**
     * <p>Constructs a Paginator object that manages a model entity's 
     * pagination. Always recounts the total records.</p>
     * 
     * <p>String controlOptions is a string of name and value pairs 
     * separated by "=" sign. The default delimiter string to separate 
     * name-value pairs is ",|&". </p>
     * 
     * <p>String controlOptions may have the following format: </p>
     * <pre>
     *          cpage=2, limit=10,...
     *       or cpage=2|limit=10|...
     *       or cpage=2&limit=10&...
     * </pre>
     * 
     * @param pls PageListSource. 
     * @param controlOptions String of control information.
     */
    public Paginator(PageListSource pls, String controlOptions) {
        this(pls, Converters.convertStringToMap(controlOptions));
    }
    
    /**
     * <p>Constructs a Paginator object that manages a model entity's 
     * pagination. Always recounts the total records.</p>
     * 
     * @param pls PageListSource. 
     * @param controlOptions Map of control information.
     */
    public Paginator(PageListSource pls, Map controlOptions) {
        this.controlOptions = controlOptions;
        
        initialize(pls, controlOptions);
    }
    
    /**
     * Return offset
     */
    public int getOffset() {
        int offset = (cpage-1) * limit;
        if (totalCounted && offset >= totalCount) offset = totalCount - limit;
        return (offset > 0)?offset:0;
    }
    
    /**
     * Return maximum number of records per page
     */
    public int getLimit() {
        return limit;
    }
    
    /**
     * Return the number of records on the current page
     */
    public int getCurrentPageSize() {
        return (recordList != null)?recordList.size():0;
    }
    
    /**
     * Return total number of records
     */
    public int getTotalCount() {
        return totalCount;
    }
    
    /**
     * Return total number of pages
     */
    public int getPageCount() {
        return pageCount;
    }
    
    /**
     * Return origin page number.
     * 
     * Page number starts from 1.
     */
    public int getOriginPage() {
        return opage;
    }
    
    /**
     * Return current page number.
     * 
     * Page number starts from 1.
     */
    public int getCurrentPage() {
        return cpage;
    }
    
    /**
     * Return index number of the first record on the current page
     */
    public int getStartIndex() {
        return getOffset() + 1;
    }
    
    /**
     * Return index number of the last record on the current page
     */
    public int getEndIndex() {
        int endIndex = getOffset() + getLimit();
        if (endIndex > getTotalCount()) endIndex = getTotalCount();
        return endIndex;
    }
    
    /**
     * Return query string for link of the origin page
     */
    public String getQueryStringOrigin() {
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(ref);
        qs.append("&npage=").append(opage);
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Return query string for link of a page
     * 
     * pageNumber starts from 1 to total page count. 
     */
    public String getQueryStringPage(int pageNumber) {
        if (pageNumber > pageCount || pageNumber < 1) return "";
        
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(link_value_page);
        qs.append("&npage=").append(pageNumber);
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Return query string for link "first"
     */
    public String getQueryStringFirst() {
        if (cpage == 1) return "";
        
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(link_value_first);
        qs.append("&npage=1");
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Return query string for link "previous"
     */
    public String getQueryStringPrevious() {
        if (!hasPreviousPage()) return "";
        
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(link_value_previous);
        qs.append("&npage=").append(cpage-1);
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Return query string for link "next"
     */
    public String getQueryStringNext() {
        if (!hasLastPage()) return "";
        
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(link_value_next);
        qs.append("&npage=").append(cpage+1);
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Return query string for link "last"
     */
    public String getQueryStringLast() {
        if (!hasLastPage()) return "";
        
        StringBuffer qs = new StringBuffer();
        qs.append("r=").append(link_value_last);
        qs.append("&npage=").append(pageCount);
        qs.append("&limit=").append(limit);
        qs.append("&cpage=").append(cpage);
        appendExclude(qs, "r, npage, cpage, limit");
        return qs.toString();
    }
    
    /**
     * Check if the paginator is on the first page.
     * @return true for first page
     */
    public boolean isFirstPage() {
        return cpage == 1;
    }
    
    /**
     * Check if the paginator is on the last page.
     * @return true for last page
     */
    public boolean isLastPage() {
        return cpage == pageCount;
    }
    
    /**
     * Check if there is link on previous page.
     * @return true for having link
     */
    public boolean hasPreviousPage() {
        return cpage > 1;
    }
    
    /**
     * Check if there is link on last page.
     * @return true for having link
     */
    public boolean hasLastPage() {
        return cpage < pageCount;
    }
    
    public List getRecordList() {
        return recordList;
    }
    
    /**
     * <p>Sets html keys that do not have to appear in url. </p>
     * 
     * <p><tt>excludedKeys</tt> consists of comma separated keys.</p>
     * 
     * @param excludedKeys
     */
    public void setExcludedKeys(String excludedKeys) {
    	this.excludedKeys = excludedKeys;
    }
    
    public String toString() {
        String newLine = "\r\n";
        StringBuffer sb = new StringBuffer();
        sb.append("        limit: ").append(this.getLimit()).append(newLine);
        sb.append("       offset: ").append(this.getOffset()).append(newLine);
        sb.append("  total pages: ").append(this.getPageCount()).append(newLine);
        sb.append(" current page: ").append(this.getCurrentPage()).append(newLine);
        sb.append("total records: ").append(this.getTotalCount()).append(newLine);
        sb.append("  start index: ").append(this.getStartIndex()).append(newLine);
        sb.append("    end index: ").append(this.getEndIndex()).append(newLine);
        sb.append("    uri first: ").append(this.getQueryStringFirst()).append(newLine);
        sb.append(" uri previous: ").append(this.getQueryStringPrevious()).append(newLine);
        sb.append("     uri next: ").append(this.getQueryStringNext()).append(newLine);
        sb.append("     uri last: ").append(this.getQueryStringLast()).append(newLine);
        
        List recordList = getRecordList();
        if (recordList != null) {
            int count = 0;
            Iterator it = recordList.iterator();
            while(it.hasNext()) {
                count++;
                Object data = it.next();
                sb.append("record #").append(count).append(" content: ").append(newLine);
                sb.append(data).append(newLine);
            }
        }
        
        return sb.toString();
    }

    protected void initialize(PageListSource pls, Map options) {
        limit = Util.getIntValue(options, key_limit, DEFAULT_PAGE_LIMIT);
        opage = Util.getIntValue(options, key_cpage, 1);
        cpage = Util.getIntValue(options, key_npage, 1);
        ref   = Util.getStringValue(options, key_link, "");
        
        pls.setLimit(limit);
        pls.setOffset(getOffset());
        pls.setInputs(options);//in case some other conditions are passed into options
        pls.execute();
        
        totalCount = pls.getTotalCount();
        recordList = pls.getRecordList();
        totalCounted = true;
        pageCount = countPages(totalCount);
    }
    
    protected int countPages(int totalRecords) {
        return (int)Math.ceil(totalRecords/(limit*1.0));
    }
    
    /**
     * <p>Append all parameters in the options map except those in the 
     * excludeList and internal parameters.</p>
     * 
     * <p>Internal parameters have key names starting with value of 
     * DataProcessor.framework_input_key_prefix.</p>
     * 
     * @param excludeString
     * @return StringBuffer
     */
    private void appendExclude(StringBuffer qs, String excludeString) {
    	if (excludedKeys != null) {
    		excludeString = (excludeString != null)?
    				(excludeString + ", " + excludedKeys):excludedKeys;
    	}
        List excludeList = Converters.convertStringToList(excludeString);
        Iterator it = controlOptions.keySet().iterator();
        while(it.hasNext()) 
        {
            String key = (String)it.next();
            if (excludeList.contains(key) || 
                key.startsWith(DataProcessor.framework_input_key_prefix) ||
                key.startsWith("scooter.") || 
                key.startsWith("_") ||
                key.startsWith("org.mortbay.jetty")) continue;
            
            qs.append("&").append(key).append("=").append(controlOptions.get(key));
        }
    }

    public static final String key_link = "r";
    public static final String link_value_page = "page";
    public static final String link_value_first = "first";
    public static final String link_value_previous = "previous";
    public static final String link_value_next = "next";
    public static final String link_value_last = "last";
    public static final String key_limit = "limit";
    public static final String key_cpage = "cpage";
    public static final String key_npage = "npage";
    
    /**
     * <p>Key <tt>group_by</tt> represents <tt>GROUP BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>group_by=id, name</tt>" will be translated to sql 
     * query as "GROUP BY id, name".</p>
     */
    public static final String key_group_by = ActiveRecordConstants.key_group_by;
    
    /**
     * <p>Key <tt>having</tt> represents <tt>HAVING</tt> clause in sql. This is 
     * usually used with <tt>group_by</tt> together.</p>
     * 
     * <p>The <tt>HAVING</tt> clause was added to SQL because the 
     * <tt>WHERE</tt> keyword could not be used with aggregate functions.</p>
     * 
     * <p>For example, "<tt>having=sum(price)<100</tt>" will be translated to 
     * sql query as "HAVING sum(price)<100".</p>
     */
    public static final String key_having = ActiveRecordConstants.key_having;
    
    /**
     * <p>Key <tt>order_by</tt> represents <tt>ORDER BY</tt> clause in sql. </p>
     * 
     * <p>For example, "<tt>order_by=age desc</tt>" will be translated to sql 
     * query as "ORDER BY age desc".</p>
     */
    public static final String key_order_by = ActiveRecordConstants.key_order_by;
    
    /**
     * <p>Key <tt>sort</tt> indicates column names to sort. </p>
     * 
     * <p>For example, "<tt>sort=first_name</tt>" will be translated to sql 
     * query as "<tt>order by first_name</tt>".</p>
     */
    public static final String key_sort = ActiveRecordConstants.key_sort;
    
    /**
     * <p>Key <tt>order</tt> represents direction of sort. If the query 
     * result set is in descending order, use value "<tt>Down</tt>". Otherwise 
     * by default the query results are in ascending order.</p>
     */
    public static final String key_order = ActiveRecordConstants.key_order;
    
    protected Map controlOptions = new HashMap();
    
    public static final int DEFAULT_PAGE_LIMIT = DataProcessor.DEFAULT_PAGINATION_LIMIT;
    
    /**
     * Maximum number of records per page
     */
    protected int limit = DEFAULT_PAGE_LIMIT;
    
    /**
     * Link reference
     */
    protected String ref = "";
    
    /**
     * Origin page number
     */
    protected int opage = 1;
    
    /**
     * Current page number
     */
    protected int cpage = 1;
    
    /**
     * New page number
     */
    protected int npage = 1;
    
    /**
     * Sort column name
     */
    protected String sort = "";
    
    /**
     * Sort order direction: up or down
     */
    protected String order = "up";
    
    /**
     * Total number of records
     */
    protected int totalCount;
    
    /**
     * Indicates whether totalCount has been calculated.
     */
    private boolean totalCounted = false;
    
    /**
     * Total number of pages
     */
    protected int pageCount;
    
    /**
     * paged record list
     */
    protected List recordList;
    
    protected String excludedKeys;
}
