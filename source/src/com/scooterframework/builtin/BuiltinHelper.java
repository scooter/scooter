/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.web.controller.ActionControl;
import com.scooterframework.web.controller.ActionResult;
import com.scooterframework.web.util.W;

/**
 * BuiltinHelper class has helper methods for builtins.
 * 
 * @author (Fei) John Chen
 */
public class BuiltinHelper extends ActionControl {
    /**
     * Checks if the request is valid.
     * 
     * Currently only local requests are valid. 
     * 
     * @return true if the request is valid. 
     */
    public String validateRequest() {
        boolean valid = W.isLocalRequest();
        if (!valid) {
            super.flash("error", "You are not allowed to browse data because you are not from localhost.");
            return ActionResult.forwardTo(EnvConfig.getInstance().getErrorPageURI());
        }
        
        return null;
    }
    
    public String displayParams() {
        //System.out.println("################# helper displayParams: " + params());
        return null;
    }
}
