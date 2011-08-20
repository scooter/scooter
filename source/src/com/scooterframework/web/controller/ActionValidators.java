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
            public void myMethod() {
                //validates that "record_id" is passed in as a http request parameter
                ActionControl.validators().validatesPresenceOf("record_id");
                
                //validates that the length of the "name" value is not longer than 30 chars.
                ActionControl.validators().validatesLengthMaximum("name", 30);
                
                //display all validation results
                System.out.println(ActionControl.currentValidationResults());
            }
        }
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class ActionValidators extends Validators {
    
    /**
     * Returns validation results.
     * 
     * @return validation results
     */
    protected ValidationResults getValidationResults() {
        return ActionControl.currentValidationResults();
    }
    
    /**
     * Returns a value stored in either parameter scope or request scope.
     * 
     * @param key a key string representing either a parameter name or a request 
     * attribute name.
     * @return value 
     */
    protected Object getData(String key) {
        Object value = ActionControl.getFromParameterData(key);
        if (value == null) {
            value = ActionControl.getFromRequestData(key);
        }
        return value;
    }
}
