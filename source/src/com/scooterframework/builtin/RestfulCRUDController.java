/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.R;

/**
 * RestfulCRUDController class serves as default controller class for restful resources.
 * 
 * @author (Fei) John Chen
 */
public class RestfulCRUDController extends CRUDController {
    
    /**
     * Returns a restful action path. This method overrides the same method in 
     * super class to enforce restful rules.
     * 
     * @return a restful action path
     */
    protected String actionPath(String action) {
        return R.resourcePath(getResource());
    }
    
    /**
     * Returns a restful action path for an individual record. This method 
     * overrides the same method in super class to enforce restful rules.
     * 
     * @return a restful action path for an individual record
     */
    protected String actionPath(String action, ActiveRecord record) {
        return actionPath(action, record.getRestfulId());
    }
    
    /**
     * Returns a restful action path for an individual record. This method 
     * overrides the same method in super class to enforce restful rules.
     * 
     * @return a restful action path for an individual record
     */
    protected String actionPath(String action, String restfulId) {
        return restfulId;
    }
    
    /**
     * Returns default view file directory name. 
     * 
     * @return default view file directory name. 
     */
    protected String getDefaultViewFilesDirectoryName() {
        return EnvConfig.getInstance().getDefaultViewFilesDirectoryForREST();
    }
}
