package twitterdemo.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.security.LoginHelper;

import twitterdemo.models.Account;

/**
 * AccountsController class handles accounts related access.
 */
public class AccountsController {
	static {
		filterManagerFor(AccountsController.class).declareBeforeFilter(
			SignonController.class, "loginRequired");
	}

    /**
     * followers method
     */
    public String followers() {
    	String username = p("username");
    	ActiveRecord user = Account.findFirst("username='" + username + "'");
    	storeToRequest("followers", user.allAssociated("followers").getRecords());
        storeToRequest("username", username);
        storeToRequest("user", user);
        return null;
    }

    /**
     * followings method
     */
    public String followings() {
    	String username = p("username");
    	ActiveRecord user = Account.findFirst("username='" + username + "'");
    	storeToRequest("followings", user.allAssociated("followings").getRecords());
        storeToRequest("username", username);
        storeToRequest("user", user);
        return null;
    }

    /**
     * addFollowing method
     */
    public String addFollowing() {
        String username = p("username");
    	ActiveRecord user = Account.findFirst("username='" + username + "'");

        ActiveRecord loginUser = LoginHelper.loginUser();
        loginUser.reload();
        loginUser.allAssociated("followings").add(user);

        return redirectTo("/" + loginUser.getField("username"));
    }
}