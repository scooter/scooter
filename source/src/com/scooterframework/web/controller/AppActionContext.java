/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * AppActionContext class holds context data for current non-web request. In 
 * the non-web environment, the context data is the global data. 
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class AppActionContext extends ActionContext {

    public AppActionContext() {
        super();
        resetFlashMessage();
        cycleData.set(new HashMap());
    }
    
    /**
     * Gets data in parameter scope as a map.
     * 
     * Return guarrented: An empty map will be returned if there is no data.
     * 
     * @return Map
     */
    public Map getParameterDataAsMap() {
        Map m = (Map)parameterLocal.get();
        if (m == null) {
            m = new HashMap();
            parameterLocal.set(m);
        }
        return m;
    }
    
    /**
     * Sets data in parameter scope.
     * 
     */
    public void setParameterData(Map data) {
        parameterLocal.set(data);
    }
    
    /**
     * Gets data in request scope as a map.
     * 
     * Return guarrented: An empty map will be returned if there is no data.
     * 
     * @return Map
     */
    public Map getRequestDataAsMap() {
        Map m = (Map)requestLocal.get();
        if (m == null) {
            m = new HashMap();
            requestLocal.set(m);
        }
        return m;
    }
    
    /**
     * Sets data in request scope.
     * 
     */
    public void setRequestData(Map data) {
        requestLocal.set(data);
    }
    
    /**
     * Gets data in session scope as a map.
     * 
     * Return guarrented: An empty map will be returned if there is no data.
     * 
     * @return Map
     */
    public Map getSessionDataAsMap() {
        return sessionData;
    }
    
    /**
     * Sets data in session scope.
     * 
     */
    public void setSessionData(Map data) {
        if (data == null) sessionData.clear();
        else sessionData = data;
    }
    
    /**
     * Gets data in context scope as a map.
     * 
     * Return guarrented: An empty map will be returned if there is no data.
     * 
     * @return Map
     */
    public Map getContextDataAsMap() {
        return getGlobalDataAsMap();
    }
    
    /**
     * Gets data represented by the key from the parameter scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     * @return Object
     */
    public Object getFromParameterData(String key) {
        return getParameterDataAsMap().get(key);
    }
    
    /**
     * Gets data represented by the key from the request scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     * @return Object
     */
    public Object getFromRequestData(String key) {
        return getRequestDataAsMap().get(key);
    }
    
    /**
     * Gets data represented by the key from the session scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     * @return Object
     */
    public Object getFromSessionData(String key) {
        return getSessionDataAsMap().get(key);
    }
    
    /**
     * Gets data represented by the key from the context scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     * @return Object
     */
    public Object getFromContextData(String key) {
        return getContextDataAsMap().get(key);
    }
    
    /**
     * Removes data represented by the key from all scopes.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     */
    public void remove(String key) {
        removeFromParameterData(key);
        super.remove(key);
    }
    
    /**
     * Removes data represented by the key from parameter scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     */
    public void removeFromParameterData(String key) {
        getParameterDataAsMap().remove(key);
    }
    
    /**
     * Removes data represented by the key from request scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     */
    public void removeFromRequestData(String key) {
        getRequestDataAsMap().remove(key);
    }
    
    /**
     * Removes data represented by the key from session scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     */
    public void removeFromSessionData(String key) {
        getSessionDataAsMap().remove(key);
    }
    
    /**
     * Removes all data represented by the key from session scope.
     */
    public void removeAllSessionData() {
        getSessionDataAsMap().clear();
    }
    
    /**
     * Removes data represented by the key from context scope.
     * 
     * Note: The result of this method is sensitive to the case of key string.
     * 
     * @param key
     */
    public void removeFromContextData(String key) {
        getContextDataAsMap().remove(key);
    }
    
    /**
     * Stores the object represented by the key to parameter scope.
     * 
     * @param key String
     * @param object Object
     */
    public void storeToParameter(String key, Object object) {
        getParameterDataAsMap().put(key, object);
    }
    
    /**
     * Stores the object represented by the key to request scope.
     * 
     * @param key String
     * @param object Object
     */
    public void storeToRequest(String key, Object object) {
        getRequestDataAsMap().put(key, object);
    }
    
    /**
     * Stores the object represented by the key to session scope.
     * 
     * @param key String
     * @param object Object
     */
    public void storeToSession(String key, Object object) {
        sessionData.put(key, object);
    }
    
    /**
     * Stores the object represented by the key to context scope.
     * 
     * @param key String
     * @param object Object
     */
    public void storeToContext(String key, Object object) {
        getContextDataAsMap().put(key, object);
    }
    
    /**
     * Starts session.
     */
    public void startSession() {
        if (sessionData == null) sessionData = new HashMap();
        else sessionData.clear();
    }
    
    /**
     * Ends session.
     */
    public void endSession() {
        sessionData.clear();
        errors.clear();
        clearCachedRequestData();
    }
    
    /**
     * Cleans up all local cached data to prepare for the next request.
     */
    protected void clearCachedRequestData() {
        parameterLocal.set(null);
        requestLocal.set(null);
        cycleData.set(null);
    }
    
    /**
     * Returns a named cycle from cycle map.
     * 
     * @param name
     * @return cycle object
     */
    protected Object getCycleFromCycleMap(String name) {
        return getCycleMap().get(name);
    }
    
    /**
     * Sets a named cycle in cycle map.
     * 
     * @param name
     * @param cycle
     */
    protected void setCycleToCycleMap(String name, Object cycle) {
        getCycleMap().put(name, cycle);
    }
    
    private Map getCycleMap() {
        return (Map)cycleData.get();
    }
    
    /**
     * Session data map.
     */
    protected Map sessionData = new HashMap();
    
    private static final ThreadLocal parameterLocal = new ThreadLocal();
    private static final ThreadLocal requestLocal = new ThreadLocal();
    private static final ThreadLocal cycleData = new ThreadLocal();
}
