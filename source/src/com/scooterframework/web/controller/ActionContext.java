/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.Message;
import com.scooterframework.common.util.StringUtil;

/**
 * <p>
 * ActionContext class holds context data in different scopes.
 * </p>
 * 
 * <p>
 * There are six supported scopes: thread, parameter, request, session, context
 * and global. The first scope supports data held in current thread, the next 
 * four scopes map to the corresponding scopes in Servlet and JSP for web 
 * environment. The global scope provides a place for the whole application to 
 * access data that can be used for all users in a static way. 
 * </p>
 * 
 * @author (Fei) John Chen
 */
public abstract class ActionContext {
    
    private static final String LOCALE_KEY = Constants.CURRENT_LOCALE;
    
    //key to the flash message
    public static final String KEY_FLASH_MESSAGE = "KEY_FLASH_MESSAGE";
    
    /**
     * String constant for thread scope.
     */
    public static final String SCOPE_THREAD = "thread";
    
    /**
     * String constant for parameter scope.
     */
    public static final String SCOPE_PARAMETER = "parameter";
    
    /**
     * String constant for request scope.
     */
    public static final String SCOPE_REQUEST = "request";
    
    /**
     * String constant for session scope.
     */
    public static final String SCOPE_SESSION = "session";
    
    /**
     * String constant for context scope.
     */
    public static final String SCOPE_CONTEXT = "context";
    
    /**
     * String constant for global scope.
     */
    public static final String SCOPE_GLOBAL = "global";
    
    /**
     * Global data map.
     */
    private static Map<String, Object> globalData = new HashMap<String, Object>();

    public ActionContext() {
    }

    /**
     * <p>Gets data in parameter scope as a map.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public abstract Map<String, Object> getParameterDataAsMap();
    
    /**
     * <p>Gets data in request scope as a map.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public abstract Map<String, Object> getRequestDataAsMap();
    
    /**
     * <p>Gets data in both parameter scope and request scope as a map.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public Map<String, Object> getAllRequestDataAsMap() {
        Map<String, Object> pm = getParameterDataAsMap();
        Map<String, Object> rm = getRequestDataAsMap();
        
        if (pm == null) pm = new HashMap<String, Object>();
        
        if (rm != null) pm.putAll(rm);
        return pm;
    }

    /**
     * <p>Gets data in parameter scope as a map. Only those keys with a specific 
     * keyPrefix will be processed.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public Map<String, Object> getParameterDataAsMap(String keyPrefix) {
        Map<String, Object> m = getParameterDataAsMap();
        if (keyPrefix == null || "".equals(keyPrefix)) return m;
        
        Map<String, Object> m2 = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(keyPrefix)) {
                String key2 = key.substring(keyPrefix.length());
                m2.put(key2, entry.getValue());
            }
        }
        return m2;
    }

    /**
     * <p>Gets data in request scope as a map. Only those keys with a specific 
     * keyPrefix will be processed.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public Map<String, Object> getRequestDataAsMap(String keyPrefix) {
        Map<String, Object> m = getRequestDataAsMap();
        if (keyPrefix == null || "".equals(keyPrefix)) return m;
        
        Map<String, Object> m2 = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(keyPrefix)) {
                String key2 = key.substring(keyPrefix.length());
                m2.put(key2, m.get(key));
            }
        }
        return m2;
    }
    
    /**
     * <p>Gets data in both parameter scope and request scope as a map. Only those 
     * keys with a specific keyPrefix will be processed.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public Map<String, Object> getAllRequestDataAsMap(String keyPrefix) {
        Map<String, Object> pm = getParameterDataAsMap(keyPrefix);
        Map<String, Object> rm = getRequestDataAsMap(keyPrefix);
        
        if (pm == null) pm = new HashMap<String, Object>();
        
        if (rm != null) pm.putAll(rm);
        return pm;
    }
    
    /**
     * <p>Gets data in session scope as a map.</p>
     * 
     * <p>Return guaranteed: An empty map will be returned if there is no data.</p>
     * 
     * @return Map
     */
    public abstract Map<String, Object> getSessionDataAsMap();
    
    /**
     * Gets data in context scope as a map.
     * 
     * @return Map
     */
    public abstract Map<String, Object> getContextDataAsMap();
    
    /**
     * Gets data in global scope as a map.
     * 
     * @return Map
     */
    public static Map<String, Object> getGlobalDataAsMap() {
        return globalData;
    }
    
    /**
     * <p>Gets data represented by the key from the thread scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public static Object getFromThreadData(String key) {
        return CurrentThreadCache.get(key);
    }
    
    /**
     * <p>Gets data represented by the key from the parameter scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public abstract Object getFromParameterData(String key);
    
    /**
     * <p>Gets data represented by the key from the parameter scope. Ignore the 
     * case of the key string.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public Object getFromParameterDataIgnoreCase(String key) {
    	if (key == null) return null;
    	
        Object tmp = null;
        Map<String, Object> m = getParameterDataAsMap();
        if (m == null) return null;
        
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String name = entry.getKey();
            if (name.equalsIgnoreCase(key)) {
                tmp = entry.getValue();
                break;
            }
        }
        return tmp;
    }
    
    /**
     * <p>Gets data represented by the key from the request scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public abstract Object getFromRequestData(String key);
    
    /**
     * <p>Gets data represented by the key from the session scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public abstract Object getFromSessionData(String key);
    
    /**
     * <p>Gets data represented by the key from the context scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public abstract Object getFromContextData(String key);
    
    /**
     * <p>Gets data represented by the key from the global scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public static Object getFromGlobalData(String key) {
        return getGlobalDataAsMap().get(key);
    }
    
    /**
     * Gets the first message represented by the type from the flash.
     * 
     * @param type
     * @return String
     */
    public String getFirstFlashMessage(String type) {
        FlashMessage fm = (FlashMessage)getFromRequestData(KEY_FLASH_MESSAGE);
        return (fm != null)?fm.getFirst(type):"";
    }
    
    /**
     * Gets the latest message represented by the type from the flash.
     * 
     * @param type
     * @return String
     */
    public String getLatestFlashMessage(String type) {
        FlashMessage fm = (FlashMessage)getFromRequestData(KEY_FLASH_MESSAGE);
        return (fm != null)?fm.getLast(type):"";
    }
    
    /**
     * Gets all messages represented by the type from the flash.
     * 
     * @param type flash message type
     * @return list of messages
     */
    public List<Message> getAllFlashMessages(String type) {
        FlashMessage fm = (FlashMessage)getFromRequestData(KEY_FLASH_MESSAGE);
        return (fm != null)?fm.getAll(type):null;
    }
    
    /**
     *  <p>Gets data represented by the key from the first scope it is found in
     * parameter scope and request scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public Object getFromAllRequestData(String key) {
        Object o = getFromParameterData(key);
        if (o == null) {
            o = getFromRequestData(key);
        }
        return o;
    }
    
    /**
     * <p>Gets data represented by the key from the first scope it is found.</p>
     * 
     * <p>
     * There are six supported scopes: thread, parameter, request, session, context
     * and global. The first scope supports data held in current thread, the next 
     * four scopes map to the corresponding scopes in Servlet and JSP for web 
     * environment. The global scope provides a place for the whole application to 
     * access data that can be used for all users in a static way. 
     * </p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.<p>
     * 
     * @param key
     * @return Object
     */
    public Object get(String key) {
        Object o = getFromThreadData(key);
        if (o == null) {
            o = getFromParameterData(key);
            if (o == null) {
                o = getFromRequestData(key);
                if (o == null) {
                    o = getFromSessionData(key);
                    if (o == null) {
                        o = getFromContextData(key);
                        if (o == null) {
                            o = getFromGlobalData(key);
                        }
                    }
                }
            }
        }
        return o;
    }
    
    /**
     * <p>Gets data represented by the key from the specific scope.</p>
     * 
     * <p>
     * There are six supported scopes: thread, parameter, request, session, context
     * and global. The first scope supports data held in current thread, the next 
     * four scopes map to the corresponding scopes in Servlet and JSP for web 
     * environment. The global scope provides a place for the whole application to 
     * access data that can be used for all users in a static way. 
     * </p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     * @return Object
     */
    public Object get(String key, String scope) {
        Object o = null;
        if (SCOPE_THREAD.equals(scope)) {
            o = getFromThreadData(key);
        }
        else if (SCOPE_PARAMETER.equals(scope)) {
            o = getFromParameterData(key);
        }
        else if (SCOPE_REQUEST.equals(scope)) {
            o = getFromRequestData(key);
        }
        else if (SCOPE_SESSION.equals(scope)) {
            o = getFromSessionData(key);
        }
        else if (SCOPE_CONTEXT.equals(scope)) {
            o = getFromContextData(key);
        }
        else if (SCOPE_GLOBAL.equals(scope)) {
            o = getFromGlobalData(key);
        }
        else {
            throw new IllegalArgumentException("Undefined scope: " + scope + ".");
        }
        return o;
    }
    
    /**
     * <p>Removes data represented by the key from all scopes except the 
     * parameter scope. There is no servlet API that can remove data from 
     * parameter scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public void remove(String key) {
        removeFromThreadData(key);
        removeFromRequestData(key);
        removeFromSessionData(key);
        removeFromContextData(key);
        removeFromGlobalData(key);
    }
    
    /**
     * <p>Removes data represented by the key from thread scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public static void removeFromThreadData(String key) {
        CurrentThreadCache.clear(key);
    }
    
    /**
     * <p>Removes data represented by the key from request scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public abstract void removeFromRequestData(String key);
    
    /**
     * <p>Removes data represented by the key from session scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public abstract void removeFromSessionData(String key);
    
    /**
     * <p>Removes data represented by the key from context scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public abstract void removeFromContextData(String key);
    
    /**
     * <p>Removes data represented by the key from global scope.</p>
     * 
     * <p>Note: The result of this method is sensitive to the case of key string.</p>
     * 
     * @param key
     */
    public static void removeFromGlobalData(String key) {
        getGlobalDataAsMap().remove(key);
    }
    
    /**
     * Removes all data represented by the key from session scope.
     */
    public abstract void removeAllSessionData();
    
    /**
     * Stores the object represented by the key to thread scope.
     * 
     * @param key String
     * @param value Object
     */
    public static void storeToThread(String key, Object value) {
        CurrentThreadCache.set(key, value);
    }
    
    /**
     * Stores the object represented by the key to request scope.
     * 
     * @param key String
     * @param value Object
     */
    public abstract void storeToRequest(String key, Object value);
    
    /**
     * Stores the object represented by the key to session scope.
     * 
     * @param key String
     * @param value Object
     */
    public abstract void storeToSession(String key, Object value);
    
    /**
     * Stores the object represented by the key to context scope.
     * 
     * @param key String
     * @param object Object
     */
    public abstract void storeToContext(String key, Object object);
    
    /**
     * Stores the object represented by the key to global scope.
     * 
     * @param key String
     * @param value Object
     */
    public static void storeToGlobal(String key, Object value) {
        getGlobalDataAsMap().put(key, value);
    }
    
    /**
     * Sets message for a type. 
     */
    public void setFlashMessage(String type, String message) {
        setFlashMessage(type, new Message(message));
    }
    
    /**
     * Sets message for a type. 
     */
    public void setFlashMessage(String type, Message message) {
        FlashMessage fm = (FlashMessage)getFromSessionData(KEY_FLASH_MESSAGE);
        if (fm == null) {
            fm = new FlashMessage();
            storeToSession(KEY_FLASH_MESSAGE, fm);
        }
        fm.addMessage(type, message);
        
        //set to request too
        FlashMessage fm2 = (FlashMessage)getFromRequestData(KEY_FLASH_MESSAGE);
        if (fm2 == null) {
            storeToRequest(KEY_FLASH_MESSAGE, fm);
        }
        else {
            fm2.addMessage(type, message);
        }
    }
    
    /**
     * Removes FlashMessage data from session and stores it in the 
     * incoming request. 
     */
    public void resetFlashMessage() {
        FlashMessage fm = (FlashMessage)getFromSessionData(KEY_FLASH_MESSAGE);
        if (fm != null) {
            storeToRequest(KEY_FLASH_MESSAGE, fm);
            removeFromSessionData(KEY_FLASH_MESSAGE);
        }
    }
    
    /**
     * Returns errors as Map.
     * 
     * @return Map of error
     */
    public Map<String, Object> getErrorAsMap() {
        return errors;
    }
    
    /**
     * Sets a map of error to error map.
     * 
     * @param errors Map
     */
    public void setErrors(Map<String, Object> errors) {
    	if (errors != null) this.errors.putAll(errors);
    }
    
    /**
     * Gets an error object represented by a key to error map.
     * 
     * @param key The key to the error object.
     * @return error Object
     */
    public Object getError(String key) {
        return errors.get(key);
    }
    
    /**
     * Sets an error object represented by a key to error map.
     * 
     * @param error An error object.
     */
    public void setError(String key, Object error) {
        errors.put(key, error);
    }
    
    /**
     * Errors map.
     */
    protected Map<String, Object> errors = new HashMap<String, Object>();
    
    
    /**
     * <p>Retrieves a map of primary key and values.</p>
     * 
     * <p>The result map is restricted to those keys in the input with a specific 
     * prefix.</p>
     * 
     * @param keyPrefix a prefix string.
     * @param pkNames an array of primary key names.
     * @return map Map of primary key data.
     */
    public Map<String, Object> retrievePrimaryKeyDataMapFromRequest(String keyPrefix, String[] pkNames) {
        if (pkNames == null || pkNames.length == 0) return new HashMap<String, Object>();
        if (keyPrefix == null) keyPrefix = "";
        
        Map<String, Object> hm = new HashMap<String, Object>();
        Map<String, Object> dataMap = getAllRequestDataAsMap(keyPrefix);
        if (dataMap == null) return hm;
        
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (key != null && StringUtil.isStringInArray(key, pkNames, true)) hm.put(key.toLowerCase(), entry.getValue());
        }
        
        return hm;
    }
    
    /**
     * Retrieves a map of primary key and values.
     * 
     * @param pkNames an array of primary key names.
     * @return map Map of primary key data.
     */
    public Map<String, Object> retrievePrimaryKeyDataMapFromRequest(String[] pkNames) {
        return retrievePrimaryKeyDataMapFromRequest("", pkNames);
    }
    
    /**
     * Checks if the value of a field is required.
     * 
     * @param name name of a request parameter.
     * @return true if required
     */
    public boolean isRequiredField(String name) {
        boolean require = false;
        String value = (String)get(name);
        if ("on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) require = true;
        return require;
    }
    
    /**
     * <p>Returns Locale object from the first scope it is found. If no locale 
     * object is found, <tt>Locale.getDefault()</tt> is returned. </p>
     * 
     * <p>
     * There are six supported scopes: thread, parameter, request, session, context
     * and global. The first scope supports data held in current thread, the next 
     * four scopes map to the corresponding scopes in Servlet and JSP for web 
     * environment. The global scope provides a place for the whole application to 
     * access data that can be used for all users in a static way. 
     * </p>
     * 
     * @return Locale for the scope
     */
    public Locale getLocale() {
        Locale l = (Locale)get(LOCALE_KEY);
        if (l == null) {
            l = Locale.getDefault();
        }
        return l;
    }
    
    /**
     * Returns Locale of a scope. If no locale object is found, <tt>Locale.getDefault()</tt> 
     * is returned.
     * 
     * @param scope the specific scope
     * @return Locale for the scope
     */
    public Locale getLocale(String scope) {
        return (Locale)get(LOCALE_KEY, scope);
    }
    
    /**
     * Stores locale object in a scope. The scope is either <tt>request</tt> or 
     * <tt>session</tt> or <tt>context</tt> or <tt>global</tt>.
     * 
     * @param locale
     * @param scope
     */
    public void setLocale(Locale locale, String scope) {
        if (SCOPE_REQUEST.equals(scope)) {
            storeToRequest(LOCALE_KEY, locale);
        }
        else if (SCOPE_SESSION.equals(scope)) {
            storeToSession(LOCALE_KEY, locale);
        }
        else if (SCOPE_CONTEXT.equals(scope)) {
            storeToContext(LOCALE_KEY, locale);
        }
        else if (SCOPE_GLOBAL.equals(scope)) {
            storeToGlobal(LOCALE_KEY, locale);
        }
        else {
            throw new IllegalArgumentException("Unsupported scope value for storing locale object: " + scope + ".");
        }
    }
    
    /**
     * Sets locale object to be used by a single user request.
     * @param locale
     */
    public void setRequestLocale(Locale locale) {
        setLocale(locale, SCOPE_REQUEST);
    }
    
    /**
     * Sets locale object to be used by a single user session.
     * 
     * @param locale
     */
    public void setSessionLocale(Locale locale) {
        setLocale(locale, SCOPE_SESSION);
    }
    
    /**
     * Sets locale object to be used by all users.
     * 
     * @param locale
     */
    public static void setGlobalLocale(Locale locale) {
        storeToGlobal(LOCALE_KEY, locale);
    }
    
    /**
     * Gets a locale object that can be used by all users. 
     */
    public static Locale getGlobalLocale() {
        return (Locale)getFromGlobalData(LOCALE_KEY);
    }
    
    /**
     * Returns an item from a list of items.
     * 
     * Examples:
     * <pre>
     *      class=<%=W.cycle("odd, even")%> 
     *      -- use "red" class for odd rows and "blue" class for even rows.
     * </pre>
     * 
     * @param items list of items to be cycled
     * @return an item value
     */
    public String cycle(String items) {
        return cycle(items, items);
    }
    
    /**
     * Returns an item from a list of items. The <tt>items</tt> string may 
     * contain several items separated by comma.
     * 
     * @param items list of items to be cycled
     * @param name the cycle name
     * @return an item value
     */
    public String cycle(String items, String name) {
        if (items == null || "".equals(items)) return "";
        
        Cycle c = (Cycle)getCycleFromCycleMap(name);
        if (c == null) {
            c = new Cycle(items);
            setCycleToCycleMap(name, c);
        }
        
        //in case the same cycle name is used somewhere else but with a 
        //different list of items to cycle through which may cause something 
        //like IndexOutOfBound exception
        if (!items.equals(c.cycleStrings)) {
            c.setItems(items);
        }
        
        return c.getValue();
    }
    
    /**
     * Returns current item in the named cycle. 
     * 
     * @param name  name of the cycle
     * @return current item in the named cycle.
     */
    public String currentCycle(String name) {
        Cycle c = (Cycle)getCycleFromCycleMap(name);
        return (c != null)?c.getCurrentValue():"";
    }
    
    /**
     * Resets the cycle
     * 
     * @param name cycle's name
     */
    public void resetCycle(String name) {
        Cycle c = (Cycle)getCycleFromCycleMap(name);
        if (c != null) c.resetPointer();
    }
    
    /**
     * Returns a named cycle from cycle map.
     * 
     * @param name
     * @return cycle object
     */
    abstract protected Object getCycleFromCycleMap(String name);
    
    /**
     * Sets a named cycle in cycle map.
     * 
     * @param name
     * @param cycle
     */
    abstract protected void setCycleToCycleMap(String name, Object cycle);
    
    private static class Cycle {
        public Cycle(String items) {
            cycleStrings = items;
            setItems(items);
            currentIndex = 0;
        }
        
        void setItems(String items) {
            itemsList = Converters.convertStringToList(items);
            totalSize = itemsList.size();
        }
        
        String getValue() {
            int pointer = getPointer();
            if (pointer == -1) return "";
            latestItem = itemsList.get(pointer);
            
            return latestItem;
        }
        
        String getCurrentValue() {
            return latestItem;
        }
        
        private int getPointer() {
            if (totalSize == 0) return -1; //nothing to cycle
            
            int pointer = currentIndex;
            
            //move to next item
            incrementPointer(currentIndex);
            
            return pointer;
        }
        
        void resetPointer() {
            currentIndex = 0;
        }
        
        private void incrementPointer(int index) {
            currentIndex = index + 1;
            if (currentIndex >= totalSize) {
                resetPointer();
            }
        }
        
        private String cycleStrings = "";
        private List<String> itemsList = null;
        private int currentIndex = 0;
        private String latestItem = "";
        private int totalSize = 0;
    }
}
