/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.exception.ObjectCreationException;
import com.scooterframework.common.util.CurrentThreadCache;

/**
 * ACH (ActionContextHolder) class holds a current action context object.
 * 
 * @author (Fei) John Chen
 */
public class ACH {
    private static final String KEY_AC = "key.ac";

    public static ActionContext getActionContext(String type) {
        if (WAC.equals(type)) {
            return getWAC();
        }
        else if (AAC.equals(type) || OAC.equals(type)) {
            return getAAC();
        }
        else {
            throw new IllegalArgumentException("Unknown ActionContext type: " + type + ".");
        }
    }
    
    public static ActionContext getActionContext() {
        EnvConfig wc = EnvConfig.getInstance();
        if (wc == null) return null;
        
        String type = wc.getServerType();
        return getActionContext(type);
    }
    
    
    /**
     * A short-hand version of getActionContext(String)
     * 
     * @param type
     * @return ActionContext
     */
    public static ActionContext getAC(String type) {
        return getActionContext(type);
    }
    
    /**
     * A short-hand version of getActionContext()
     * 
     * @return ActionContext
     */
    public static ActionContext getAC() {
        return getActionContext();
    }
    
    /**
     * Returns <tt>AppActionContext</tt>.
     * 
     * @return the AppActionContext instance
     */
    public static AppActionContext getAAC() {
        AppActionContext aac = (AppActionContext)CurrentThreadCache.get(KEY_AC);
        if (aac == null) {
            aac = new AppActionContext();
            setActionContext(aac);
        }
        return aac;
    }
    
    /**
     * Returns <tt>WebActionContext</tt>.
     * 
     * @return the WebActionContext instance
     */
    public static WebActionContext getWAC() {
        WebActionContext wac = (WebActionContext)CurrentThreadCache.get(KEY_AC);
        if (wac == null) {
            throw new ObjectCreationException("WebActionContext must be created first, for example, by a filter class.");
        }
        return wac;
    }
    
    /**
     * Stores an action context.
     * 
     * @param ac the action context to store
     */
    public static void setActionContext(ActionContext ac) {
        CurrentThreadCache.set(KEY_AC, ac);
    }
    
    /**
     * AAC is constant for Application Action Context.
     */
    public static final String AAC = Constants.CONFIGURED_MODE_SCOOTER_APP;
    
    /**
     * OAC is constant for ORM Application Context.
     */
    public static final String OAC = Constants.CONFIGURED_MODE_SCOOTER_ORM;
    
    /**
     * WAC is constant for Web Action Context.
     */
    public static final String WAC = Constants.CONFIGURED_MODE_SCOOTER_WEB;
}
