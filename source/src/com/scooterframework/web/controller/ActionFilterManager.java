/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;


/**
 * ActionFilterManager class manages filters for controller classes.
 * 
 * @author (Fei) John Chen
 */
public class ActionFilterManager {
    private static ActionFilterManager me;
    
    static {
        me = new ActionFilterManager();
    }

    private ActionFilterManager() {
    }
    
    public static synchronized ActionFilterManager getInstance() {
        return me;
    }
    
    /**
     * Check if a class has been set up relations.
     */
    public boolean hasCompletedRelationSetup(String className) {
        return false;
    }
    
    /**
     * Specifies filters that apply to all actions of the controller class.
     * 
     * Before filters are executed before real action is executed.
     * 
     * @param filters method names that act as filters.
     */
    protected void beforeFilter(String filters) {
        ;
    }
    
    /**
     * Specifies filters that apply to specific actions of the controller class
     * under special conditions.
     * 
     * Before filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters method names that act as filters.
     * @param option  condition for the filter
     * @param actions method names that act as actions.
     */
    protected void beforeFilter(String filters, String option, String actions) {
        ;
    }
    
    /**
     * Specifies filters that apply to all actions of the controller class.
     * 
     * After filters are executed after real action is executed.
     * 
     * @param filters method names that act as filters.
     */
    protected void afterFilter(String filters) {
        ;
    }
    
    /**
     * Specifies filters that apply to specific actions of the controller class
     * under special conditions.
     * 
     * After filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters method names that act as filters.
     * @param option  condition for the filter
     * @param actions method names that act as actions.
     */
    protected void afterFilter(String filters, String option, String actions) {
        ;
    }
}
