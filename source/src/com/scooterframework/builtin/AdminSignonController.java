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
import com.scooterframework.common.util.Util;
import com.scooterframework.security.LoginHelper;


/**
 * AdminSignonController class handles admin signon related requests.
 * 
 * @author (Fei) John Chen
 */
public class AdminSignonController {

    static {
		filterManagerFor(AdminSignonController.class).declareBeforeFilter("loginRequired", "only", "main");
        filterManagerFor(AdminSignonController.class).declareBeforeFilter("validateInput", "only", "authenticate");
    }
    
    public String validateInput() {
        validators().validatesPresenceOf("username");
        validators().validatesPresenceOf("password");
        if (validationFailed()) {
            flash("error", "Please submit both username and password.");
            return redirectTo("/admin/signon/login");
        }
        return null;
    }
    
    /**
     * default action
     */
    public String index() {
    	return (LoginHelper.isAdminLoggedIn())?
    			redirectTo("/admin/signon/main")
    			//:redirectTo("/admin/signon/login");
    			:forwardTo(viewPath("/login.jsp"));
    }
    
    /**
     * main method
     */
    public String main() {
        return null;
    }
    
    /**
     * login method
     */
    public String login() {
    	return (LoginHelper.isAdminLoggedIn())?
    			redirectTo("/admin/signon/main")
    			:null;
    }
    
    /**
     * Authenticates login request.
     */
    public String authenticate() {
        String username = p("username");
        String password = p("password");
        
        String sau = EnvConfig.getInstance().getSiteAdminUsername();
        String sap = EnvConfig.getInstance().getSiteAdminPassword();
        if (sau != null && sap != null && 
        		sau.equals(username) && sap.equals(Util.md5(password))) {
            LoginHelper.cacheLoggedInUserId(username);
            LoginHelper.cacheLoggedInPassword(password);
            return redirectTo("/admin/signon/main");
        }
        
        flash("error", "Please login by using correct username and password for site admin.");
        return forwardTo("/admin/signon/login");
    }

    /**
     * logout method
     */
    public String logout() {
        LoginHelper.userLogout();
        return null;
    }

    /**
     * loginRequired method (usually used in beforeFilter)
     */
    public String loginRequired() {
        if (!LoginHelper.isAdminLoggedIn()) {
            flash("error", "You must be logged in to do that.");
            return redirectTo("/admin/signon/login");
        }
        return null;
    }
}
