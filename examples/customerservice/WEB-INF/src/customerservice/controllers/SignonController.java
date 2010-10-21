package customerservice.controllers;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.controller.ActionResult;
import com.scooterframework.security.LoginHelper;


/**
 * SignonController class handles signon related access.
 */
public class SignonController extends ApplicationController {

    public void registerFilters() {
        beforeFilter("validateInput", "only", "authenticate");
    }
    
    public String validateInput() {
        validators().validatesPresenceOf("username");
        validators().validatesPresenceOf("password");
        if (validationFailed()) {
            flash("error", "Please submit both username and password.");
            return ActionResult.redirectTo("/signon/login");
        }
        return null;
    }
    
    /**
     * login method
     */
    public String login() {
        return null;
    }
    
    /**
     * Authenticates login request.
     */
    public String authenticate() {
        String username = super.params("username");
        String password = super.params("password");

        /************* Remove this block ************/
            LoginHelper.cacheLoggedInUserId(username);//Save the login user id to session
            return ActionResult.redirectTo("/signon/main");
        

        /************* Implement authentication logic below ***********
        ActiveRecord user = super.findRecord(Account.class, 
                            "username=" + username + ", password=" + password);
        if (user != null) {
            LoginHelper.cacheLoggedInUser(user);//Save the login user to session
            LoginHelper.cacheLoggedInUserId(username);//Save the login user id to session
            return ActionResult.redirectTo("/signon/main");
        }
        
        flash("error", "Please login by using correct username and password.");
        return ActionResult.forwardTo("/signon/login");
        ************** Implement the above block ************/
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
        if (!LoginHelper.isLoggedIn()) {
            flash("error", "You must be logged in to do that.");
            return ActionResult.redirectTo("/signon/login");
        }
        return null;
    }
}
