/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.common.validation.Validators;

/**
 * ActionValidators contains validation methods to validate inputs to a controller.
 * 
 * <pre>
 * Examples:
 *      public class PostsController extends ApplicationController {
            public void validatesRecord() {
                //validates_presence_of :body
                getValidators().validatesPresenceOf("body");
                
                //validates_length_of :body, :maximum => 10000
                getValidators().validatesLengthMaximum("body", 10000);
            }
        }
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class ActionValidators extends Validators {

    /**
     * <p>Construct a validator.</p>
     * 
     * @param ac an ActionControl instance
     */
    public ActionValidators(ActionControl ac) {
        this.ac = ac;
    }
    
    
    /**
     * Returns validation results.
     * 
     * @return validation results
     */
    protected ValidationResults getValidationResults() {
        return ac.currentValidationResults();
    }
    
    /**
     * Returns a value stored in either parameter scope or request scope.
     * 
     * @param key a key string representing either a parameter name or a request 
     * attribute name.
     * @return value 
     */
    protected Object getData(String key) {
        Object value = ac.getFromParameterData(key);
        if (value == null) {
            value = ac.getFromRequestData(key);
        }
        return value;
    }
    
    /**
     * <p>The current ActionControl instance.</p>
     */
    protected ActionControl ac;
}
