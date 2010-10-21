/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * FilterManager manages all filters of a specific controller class.
 * 
 * @author (Fei) John Chen
 */
public class FilterManager {
    public static final String FILTER_TYPE_BEFORE = "before";
    public static final String FILTER_TYPE_AFTER = "after";
    public static final String FILTER_TYPE_SKIP_BEFORE = "skip_before";
    public static final String FILTER_TYPE_SKIP_AFTER = "skip_after";

    public static final String FILTER_OPTION_EXCEPT = "except";
    public static final String FILTER_OPTION_ONLY = "only";
    
    private List afterFilterDataList = Collections.synchronizedList(new ArrayList());
    private List beforeFilterDataList = Collections.synchronizedList(new ArrayList());
    private List skipAfterFilterDataList = Collections.synchronizedList(new ArrayList());
    private List skipBeforeFilterDataList = Collections.synchronizedList(new ArrayList());
    
    private static List allConfiguredClasses = Collections.synchronizedList(new ArrayList());
    
    private List actionMethods = null;
    private Map actionBeforeFiltersMap = Collections.synchronizedMap(new HashMap());
    private Map actionAfterFiltersMap = Collections.synchronizedMap(new HashMap());
    
    private static Map allFiltersMap = Collections.synchronizedMap(new HashMap());
    private static final String FILTER_KEY_SEPARATOR = "-";
    
	private Class ownerClass;
    private boolean filtersRegistrationCompleted;
    private boolean noFilterDeclared;
	
	/**
	 * Constructs a new instance of the <tt>FilterManager</tt>.
	 */
	FilterManager(Class ownerClass) {
		if (ownerClass == null) {
			throw new IllegalArgumentException("ownerClass cannot be null for FilterManager().");
		}
		this.ownerClass = ownerClass;
		filtersRegistrationCompleted = false;
		noFilterDeclared = true;
	}
	
	/**
	 * Returns the owner class.
	 */
	public Class getOwnerClass() {
		return ownerClass;
	}
	
	/**
	 * Returns the name of the owner class.
	 */
	public String getOwnerClassName() {
		return ownerClass.getName();
	}
	
	/**
	 * True if no filter declared.
	 */
	public boolean noFilterDeclared() {
		return noFilterDeclared;
	}
    
    /**
     * Returns a list of before ActionFilter objects for an action. 
     * 
     * @param action action name
     * @return list of ActionFilter objects
     */
	public List getBeforeFiltersForAction(String action) {
        return getFiltersForAction(action, FILTER_TYPE_BEFORE);
    }
    
    /**
     * Returns a list of after ActionFilter objects for an action. 
     * 
     * @param action action name
     * @return list of ActionFilter objects
     */
	public List getAfterFiltersForAction(String action) {
        return getFiltersForAction(action, FILTER_TYPE_AFTER);
    }
    
    /**
     * Returns a map of an action and its related before filters.
     * 
     * @return map of an action and its related before filters.
     */
	public Map getActionBeforeFiltersMap() {
    	if (!filtersRegistrationCompleted) configFilters();
        return actionBeforeFiltersMap;
    }
    
    /**
     * Returns a map of an action and its related after filters.
     * 
     * @return map of an action and its related after filters.
     */
	public Map getActionAfterFiltersMap() {
    	if (!filtersRegistrationCompleted) configFilters();
        return actionAfterFiltersMap;
    }
    
    /**
     * Executes filters before an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @return result of filtering
     */
    public String executeBeforeFiltersOn(String action) {
        return executeFiltersOn(action, FILTER_TYPE_BEFORE);
    }
    
    /**
     * Executes filters after an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @return result of filtering
     */
    public String executeAfterFiltersOn(String action) {
        return executeFiltersOn(action, FILTER_TYPE_AFTER);
    }

    /**
     * Executes filters before an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @param type   filter type
     * @return result of filtering
     */
    private String executeFiltersOn(String action, String type) {
    	if (!filtersRegistrationCompleted) configFilters();
    	
        String ret = null;
        List filters = getFiltersForAction(action, type);
        if (filters != null) {
        	synchronized(filters) {
	            Iterator it = filters.iterator();
	            while(it.hasNext() && (ret == null)) {
	                ActionControlFilter af = (ActionControlFilter)it.next();
	                ret = af.execute();
	            }
        	}
        }
        return ret;
    }
    
    /**
     * Returns a list of ActionFilter objects for an action. 
     * 
     * @param action action name
     * @param type filter type
     * @return list of ActionFilter objects
     */
    private List getFiltersForAction(String action, String type) {
        List l = null;
        if (FILTER_TYPE_BEFORE.equalsIgnoreCase(type)) {
            l = (List)actionBeforeFiltersMap.get(action);
        }
        else 
        if (FILTER_TYPE_AFTER.equalsIgnoreCase(type)) {
            l = (List)actionAfterFiltersMap.get(action);
        }
        return l;
    }
    
    private void prepareFilter(List filterDataList, String filterType, Class filterClz, String filters) {
    	String key = fileterKey(filterType, filterClz, filters);
        Object filter = null;
        if (!allFiltersMap.containsKey(key)) {
            filter = new ActionFilterData(filterClz, filterType, filters);
            allFiltersMap.put(key, filter);
        }
        else {
            filter = allFiltersMap.get(key);
        }
        filterDataList.add(filter);
        
        filtersRegistrationCompleted = false;
        noFilterDeclared = false;
    }
    
    private void prepareFilter(List filterDataList, String filterType, Class filterClz, String filters, String option, String actions) {
        String key = fileterKey(filterType, filterClz, filters, option, actions);
        Object filter = null;
        if (!allFiltersMap.containsKey(key)) {
            filter = new ActionFilterData(filterClz, filterType, option, filters, actions);
            allFiltersMap.put(key, filter);
        }
        else {
            filter = allFiltersMap.get(key);
        }
        filterDataList.add(filter);
        
        filtersRegistrationCompleted = false;
        noFilterDeclared = false;
    }
    
    /**
     * Specifies 'before' filters that apply to all actions of this class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma.
     */
    public void declareBeforeFilter(String filters) {
        declareBeforeFilter(getOwnerClass(), filters);
    }
    
    /**
     * Specifies 'before' filters that apply to all actions of the controller class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    public void declareBeforeFilter(Class filterClz, String filters) {
        prepareFilter(beforeFilterDataList, FILTER_TYPE_BEFORE, filterClz, filters);
    }
    
    /**
     * Specifies 'before' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareBeforeFilter(String filters, String option, String actions) {
        declareBeforeFilter(getOwnerClass(), filters, option, actions);
    }
    
    /**
     * Specifies 'before' filters that apply to specific actions of the 
     * controller class under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareBeforeFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(beforeFilterDataList, FILTER_TYPE_BEFORE, filterClz, filters, option, actions);
    }
    
    /**
     * Specifies 'after' filters that apply to all actions of this class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    public void declareAfterFilter(String filters) {
        declareAfterFilter(getOwnerClass(), filters);
    }
    
    /**
     * Specifies 'after' filters that apply to all actions of the controller class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    public void declareAfterFilter(Class filterClz, String filters) {
        prepareFilter(afterFilterDataList, FILTER_TYPE_AFTER, filterClz, filters);
    }
    
    /**
     * Specifies 'after' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareAfterFilter(String filters, String option, String actions) {
        declareAfterFilter(getOwnerClass(), filters, option, actions);
    }
    
    /**
     * Specifies 'after' filters that apply to specific actions of the 
     * controller class under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareAfterFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(afterFilterDataList, FILTER_TYPE_AFTER, filterClz, filters, option, actions);
    }
    
    /**
     * Skips 'before' filters that apply to all actions of this class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    public void declareSkipBeforeFilter(String filters) {
        declareSkipBeforeFilter(getOwnerClass(), filters);
    }
    
    /**
     * Skips 'before' filters that apply to all actions of the controller class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    public void declareSkipBeforeFilter(Class filterClz, String filters) {
        prepareFilter(skipBeforeFilterDataList, FILTER_TYPE_SKIP_BEFORE, filterClz, filters);
    }
    
    /**
     * Skips 'before' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareSkipBeforeFilter(String filters, String option, String actions) {
        declareSkipBeforeFilter(getOwnerClass(), filters, option, actions);
    }
    
    /**
     * Skips 'before' filters that apply to specific actions of the controller 
     * class under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareSkipBeforeFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(skipBeforeFilterDataList, FILTER_TYPE_SKIP_BEFORE, filterClz, filters, option, actions);
    }
    
    /**
     * Skips 'after' filters that apply to all actions of this class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    public void declareSkipAfterFilter(String filters) {
        declareSkipAfterFilter(getOwnerClass(), filters);
    }
    
    /**
     * Skips 'after' filters that apply to all actions of the controller class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    public void declareSkipAfterFilter(Class filterClz, String filters) {
        prepareFilter(skipAfterFilterDataList, FILTER_TYPE_SKIP_AFTER, filterClz, filters);
    }
    
    /**
     * Skips 'after' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareSkipAfterFilter(String filters, String option, String actions) {
        declareSkipAfterFilter(getOwnerClass(), filters, option, actions);
    }
    
    /**
     * Skips 'after' filters that apply to specific actions of the controller 
     * class under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    public void declareSkipAfterFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(skipAfterFilterDataList, FILTER_TYPE_SKIP_AFTER, filterClz, filters, option, actions);
    }
    
    

    //create action filters map
    private void configFilters() {
        //get action list
        List actions = getAllActionMethods();
        
        actionBeforeFiltersMap = constructActionFiltersMap(actions, beforeFilterDataList, skipBeforeFilterDataList);
        actionAfterFiltersMap  = constructActionFiltersMap(actions, afterFilterDataList, skipAfterFilterDataList);
        
        filtersRegistrationCompleted = true;
    }
    
    /**
     * Action method must be public accessible, returns a string and takes no
     * arguments. 
     * 
     * @return list of action method names
     */
    private List getAllActionMethods() {
        if (actionMethods != null) return actionMethods;
        
        actionMethods = new ArrayList();
        Method[] ms = this.getOwnerClass().getMethods();//returns all public methods
        for (int i=0; i<ms.length; i++) {
            Method m = ms[i];
            if ((m.getReturnType().isInstance("")) &&
                (!m.getDeclaringClass().getName().equals(Object.class.getName())) &&
                (m.getParameterTypes().length == 0)) 
            {
                actionMethods.add(m.getName());
            }
        }
        return actionMethods;
    }
    
    //construct a map of actions and their corresponding filter list
    private Map constructActionFiltersMap(List actions, List filterDataList, List skipFilterDataList) {
        Map m = new HashMap();
        synchronized(actions) {
	        Iterator it = actions.iterator();
	        while(it.hasNext()) {
	            String action = (String)it.next();
	            List filters = constructFiltersListForAction(action, filterDataList, skipFilterDataList);
	            if (filters != null && filters.size() > 0) {
	                m.put(action, filters);
	            }
	        }
        }
        return m;
    }
    
    //construct filter list for a specific action
    private List constructFiltersListForAction(String action, List appendFilterDataList, List skipFilterDataList) {
        List al = _constructFiltersListForAction(action, appendFilterDataList);//append filter list
        if (al == null || al.size() == 0) return null;//nothing to append
        
        List sl = _constructFiltersListForAction(action, skipFilterDataList);//skip filter list
        if (sl == null || sl.size() == 0) return al;//nothing to skip
        
        //Note: At this stage, al or sl may contain duplicated filters.
        
        //declare a list of unique items
        List l = new ArrayList();
        Map m = new HashMap();//contains unique filters
        
        synchronized(al) {
	        Iterator it = al.iterator();
	        while(it.hasNext()) {
	            ActionControlFilter acf = (ActionControlFilter)it.next();
	            
	            if (skipActionControlFilter(sl, acf)) continue;
	            
	            //make sure that the l list contains non-duplicated filters.
	            if (!m.containsKey(acf.getACFKey())) {
	                m.put(acf.getACFKey(), acf);
	                l.add(acf);
	            }
	        }
        }
        return l;
    }
    
    //construct filter list for a specific action
    private List _constructFiltersListForAction(String action, List filterDataList) {
        List l = new ArrayList();
        synchronized(filterDataList) {
	        Iterator it = filterDataList.iterator();
	        while(it.hasNext()) {
	            ActionFilterData afd = (ActionFilterData)it.next();
	            List acfList = afd.getFilters(action);
	            if (acfList != null && acfList.size() > 0) {
	                l.addAll(acfList);
	            }
	        }
        }
        return l;
    }
    
    private boolean skipActionControlFilter(List data, ActionControlFilter acf) {
        boolean check = false;
        synchronized(data) {
	        Iterator it = data.iterator();
	        while(it.hasNext()) {
	            ActionControlFilter tmp = (ActionControlFilter)it.next();
	            if (tmp.getACFKey().equals(acf.getACFKey())) {
	                check = true;
	                break;
	            }
	        }
        }
        return check;
    }
    
    private String fileterKey(String filterType, Class filterClz, String filters) {
        StringBuffer key = new StringBuffer();
        key.append(filterClz.getName()).append(FILTER_KEY_SEPARATOR);
        key.append(filterType).append(FILTER_KEY_SEPARATOR);
        key.append(filters);
        return key.toString();
    }
    
    private String fileterKey(String filterType, Class filterClz, String filters, String option, String actions) {
        StringBuffer key = new StringBuffer();
        key.append(filterClz.getName()).append(FILTER_KEY_SEPARATOR);
        key.append(filterType).append(FILTER_KEY_SEPARATOR);
        key.append(filters).append(FILTER_KEY_SEPARATOR);
        key.append(option).append(FILTER_KEY_SEPARATOR);
        key.append(actions);
        return key.toString();
    }
}
