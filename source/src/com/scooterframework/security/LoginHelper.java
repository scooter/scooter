/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.security;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.controller.ACH;

/**
 * LoginHelper class has helper methods for login related requests. 
 * 
 * @author (Fei) John Chen
 */
public class LoginHelper {

    /**
     * Returns user id of the current logged-in user record which has been 
     * saved to session.
     * 
     * @return user id of the current logged-in user.
     */
    public static String loginUserId() {
    	if (CurrentThreadCacheClient.userIDRetrieved()) {
    		return CurrentThreadCacheClient.getUserID();
    	}
    	
        Object userId = null;
        try {
            userId = ACH.getAC().getFromSessionData(SESSION_KEY_LOGIN_USER_ID);
            if (userId == null) {
                ActiveRecord user = loginUser();
                if (user != null) {
                    userId = user.getRestfulId();
                    if (userId != null) cacheLoggedInUserId(userId);
                }
            }
        }
        catch(Exception ex) {
        	userId = null;
        }
        
        String id = (userId != null)?userId.toString():null;
        CurrentThreadCacheClient.cacheUserID(id);
        
        return id;
    }

    /**
     * Returns the password of the current logged-in user which has been 
     * saved to session.
     * 
     * @return the password of the current logged-in user.
     */
    public static String loginPassword() {
        Object userPwd = null;
        try {
            userPwd = ACH.getAC().getFromSessionData(SESSION_KEY_LOGIN_PASSWORD);
            if (userPwd == null) {
                ActiveRecord user = loginUser();
                if (user != null) {
                    userPwd = user.getField("password");
                    if (userPwd != null) cacheLoggedInPassword(userPwd);
                }
            }
        }
        catch(Exception ex) {
        	userPwd = null;
        }
        
        return (userPwd != null)?userPwd.toString():null;
    }
    
    /**
     * Returns the current logged-in user record which has been saved to 
     * session.
     * 
     * @return an ActiveRecord instance of the current logged-in user record.
     */
    public static ActiveRecord loginUser() {
        return (ActiveRecord)ACH.getAC().getFromSessionData(SESSION_KEY_LOGIN_USER_OBJECT);
    }
    
    /**
     * Checks if the current user is already logged in.
     * 
     * @return true if the current user is already logged in.
     */
    public static boolean isLoggedIn() {
        return (loginUserId() != null)?true:false;
    }
    
    /**
     * Checks if the admin user is already logged in.
     * 
     * @return true if the current user is already logged in.
     */
    public static boolean isAdminLoggedIn() {
    	String username = loginUserId();
        String sau = EnvConfig.getInstance().getSiteAdminUsername();
    	if (sau != null && sau.equals(username)) return true;
        return false;
    }
    
    /**
     * Stores logged-in <tt>user</tt> record to the user's http session. 
     * 
     * @param user an ActiveRecord instance.
     */
    public static void cacheLoggedInUser(ActiveRecord user) {
        ACH.getAC().storeToSession(SESSION_KEY_LOGIN_USER_OBJECT, user);
    }
    
    /**
     * Stores logged-in user's id to the user's http session. 
     * 
     * @param userId  the login user id
     */
    public static void cacheLoggedInUserId(Object userId) {
        ACH.getAC().storeToSession(SESSION_KEY_LOGIN_USER_ID, userId);
    }
    
    /**
     * Stores logged-in user's password to the user's http session. 
     * 
     * @param password  the login password
     */
    public static void cacheLoggedInPassword(Object password) {
        ACH.getAC().storeToSession(SESSION_KEY_LOGIN_PASSWORD, password);
    }
    
    /**
     * Checks if a user id is the logged-in user id.
     * 
     * Note: This method compares the logged-in user id saved in session with 
     * the test user id, regardless of cases.
     * 
     * @param testUserId user id to be tested.
     * @return true if the user id is the logged-in user id.
     */
    public static boolean isLoggedInUser(Object testUserId) {
        Object userId = loginUserId();
        if (userId == null || testUserId == null) return false;
        return (userId.toString().equalsIgnoreCase(testUserId.toString()))?true:false;
    }
    
    /**
     * Stores <tt>user</tt> instance to session. 
     * @param user an ActiveRecord instance representing a user/account
     */
    public static void userLogin(ActiveRecord user) {
        cacheLoggedInUser(user);
    }
    
    /**
     * Do something when logging out. All session data associated with the 
     * login are removed from session.
     */
    public static void userLogout() {
        ACH.getAC().removeAllSessionData();
    }
    
    public static final String SESSION_KEY_LOGIN_PASSWORD = "login_password";
    public static final String SESSION_KEY_LOGIN_USER_ID = "login_user_id";
    public static final String SESSION_KEY_LOGIN_USER_OBJECT = "login_user_object";
}
