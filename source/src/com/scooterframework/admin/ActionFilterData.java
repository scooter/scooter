/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.util.Converters;

/**
 * ActionFilterData class contains information about filters.
 * 
 * @author (Fei) John Chen
 */
public class ActionFilterData
{
    /**
     * Constructor
     * 
     * @param filterClz the declaring class of filter methods
     * @param type      a string to indicate filter type
     * @param filters   a list of method names that act as filters separated by comma
     */
    public ActionFilterData(Class filterClz, String type, String filters) {
        this(filterClz, type, null, filters, null);
    }
    
    /**
     * Constructor
     * 
     * @param filterClz the declaring class of filter methods
     * @param type      a string to indicate filter type
     * @param option    a string of filtering option
     * @param filters   a list of method names that act as filters separated by comma
     * @param actions   a list of method names separated by comma
     */
    public ActionFilterData(Class filterClz, String type, String option, String filters, String actions) {
        this.filterClz = filterClz;
        this.type = type;
        this.option = option;
        if (option != null && 
            !FilterManager.FILTER_OPTION_ONLY.equals(option) &&
            !FilterManager.FILTER_OPTION_EXCEPT.equals(option)) { 
            throw new IllegalArgumentException("The value of filter option is illegal: " + option + 
                ". Only except or only or null is supported.");
        }
        this.filterNames = Converters.convertStringToList(filters);
        this.actionNames = Converters.convertStringToList(actions);
    }

    /**
     * Returns a list filters for the specific action.
     * 
     * @param action
     * @return a list of filters for the action
     */
    public List getFilters(String action) {
        List filters = new ArrayList();
        
        //check if the action is eligible
        if ((isForAllActions() && (filterNames != null)) ||
            (isOnly() && (actionNames != null && actionNames.contains(action))) || 
            (isExcept() && (actionNames != null && !actionNames.contains(action)))) {
            filters = _getFilters();
        }
        
        return filters;
    }
    
    /**
     * Returns type of the filter.
     * 
     * @return filter type
     */
    public String getType() {
    	return type;
    }

    private List _getFilters() {
        List filters = new ArrayList();
        
        //apply all filters on this action
        Iterator it = filterNames.iterator();
        while(it.hasNext()) {
            String filter = (String)it.next();
            filters.add(getFilter(filterClz, filter));
        }
        
        return filters;
    }

    private boolean isForAllActions() {
        return (option == null)?true:false;
    }
    
    private boolean isOnly() {
        return FilterManager.FILTER_OPTION_ONLY.equals(option);
    }
    
    private boolean isExcept() {
        return FilterManager.FILTER_OPTION_EXCEPT.equals(option);
    }
    
    private ActionControlFilter getFilter(Class fc, String filter) {
        String key = ActionControlFilter.formatKey(fc, filter);
        ActionControlFilter acf = null;
        if (acfMap.containsKey(key)) {
            acf = (ActionControlFilter)acfMap.get(key);
        }
        else {
            acf = new ActionControlFilter(fc, filter);
            acfMap.put(key, acf);
        }
        return acf;
    }
    
    /**
     * the declaring class of filter methods
     */
    private Class filterClz = null;
    
    /**
     * filter type field
     */
    private String type = null;
    
    /**
     * filter option field
     */
    private String option = null;
    
    private List filterNames;
    
    private List actionNames;
    
    /**
     * Map of ActionControlFilter. Key is a combination of the filter method 
     * name and its class name
     */
    private static Map acfMap = new HashMap();
}
