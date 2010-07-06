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
import com.scooterframework.web.route.MatchMaker;
import com.scooterframework.web.route.RouteConfig;

/**
 * SiteInfoController class has methods for site into.
 * 
 * @author (Fei) John Chen
 */
public class SiteInfoController extends ActionControl {
    
    /**
     * registerFilters
     */
    public void registerFilters() {
        beforeFilter(BuiltinHelper.class, "validateRequest");
        beforeFilter(BuiltinHelper.class, "displayParams");
    }
    
    public String routes() {
        storeToRequest("auto.rest", (RouteConfig.getInstance().allowAutoREST()?"On":"Off"));
        storeToRequest("auto.crud", (EnvConfig.getInstance().allowAutoCRUD()?"On":"Off"));
        storeToRequest("routes", MatchMaker.getInstance().getAllRoutes());
        return null;
    }
}
