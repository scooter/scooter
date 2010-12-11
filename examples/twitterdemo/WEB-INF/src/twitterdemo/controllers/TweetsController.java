package twitterdemo.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import java.util.List;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.security.LoginHelper;

import twitterdemo.models.Account;
import twitterdemo.models.Tweet;

/**
 * TweetsController class handles tweets related access.
 */
public class TweetsController {
    protected LogUtil log = LogUtil.getLogger(getClass().getName());

	static {
		filterManagerFor(TweetsController.class).declareBeforeFilter(
			SignonController.class, "loginRequired", "only", "create, followings_tweets");
	}

    /**
     * followings_tweets method
     */
    public String followings_tweets() {
        ActiveRecord loginUser = LoginHelper.loginUser();
        if (loginUser != null) {
            List tweets = loginUser.allAssociated("followings_tweets", "include:account").getRecords();
            setViewData("followings_tweets", tweets);
            setViewData("username", loginUser.getField("username"));
            setViewData("user", loginUser);
        }
    	return null;
    }

    /**
     * user_tweets method
     */
    public String user_tweets() {
    	String username = p("username");
    	ActiveRecord user = Account.findFirst("username='" + username + "'");
        if (user != null) {
            setViewData("user_tweets", Tweet.findAll("account_id=" + user.getField("id")));
            setViewData("username", username);
            setViewData("user", user);
        }
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>account</tt> record.
     */
    public String create() {
        ActiveRecord newTweet = null;
        try {
            newTweet = Tweet.newRecord();
            newTweet.setData(params());

            //add login user'd account_id
            ActiveRecord loginUser = LoginHelper.loginUser();
        	newTweet.setData("account_id", loginUser.getField("id"));

            newTweet.save();
            flash("notice", "Tweet was successfully created.");

            return redirectTo("/" + loginUser.getField("username"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the tweet record.");
        }

        setViewData("tweet", newTweet);
        return renderView("followings_tweets");
    }
}