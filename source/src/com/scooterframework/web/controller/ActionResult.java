/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.util.Map;

import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.W;

/**
 * ActionResult class contains keys, tags and convenient methods for decorating 
 * a result. 
 * 
 * @author (Fei) John Chen
 */
public class ActionResult {
    
    /**
     * forward error key
     */
    public static final String FORWARD_ERROR   = "error";
    
    /**
     * forward home key
     */
    public static final String FORWARD_HOME    = "home";
    
    /**
     * forward login key
     */
    public static final String FORWARD_LOGIN   = "login";
    
    /**
     * forward logout key
     */
    public static final String FORWARD_LOGOUT  = "logout";
    
    /**
     * forward success key
     */
    public static final String FORWARD_SUCCESS = "success";
    
    /**
     * forward failure key
     */
    public static final String FORWARD_FAILURE = "failure";
    
    
    /**
     * content type tag symbol
     */
    public static final String TAG_SYMBOL      = "=>";
    
    
    /**
     * error tag
     */
    public static final String TAG_ERROR       = "error=>";
    
    /**
     * html tag
     */
    public static final String TAG_HTML        = "html=>";
    
    /**
     * text tag
     */
    public static final String TAG_TEXT        = "text=>";
    
    /**
     * xml tag
     */
    public static final String TAG_XML         = "xml=>";
    
    /**
     * render tag
     */
    public static final String TAG_RENDER      = "render=>";
    
    /**
     * forward tag
     */
    public static final String TAG_FORWARD_TO  = "forwardTo=>";
    
    /**
     * redirect tag
     */
    public static final String TAG_REDIRECT_TO = "redirectTo=>";
    
    
    /**
     * Returns an error-tagged content string for error output.
     * 
     * @param content the error content
     * @return a error-tagged content string
     */
    public static String error(String content) {
        return TAG_ERROR + content;
    }
    
    /**
     * Returns a html-tagged content string for html output.
     * 
     * @param content the html content
     * @return a html-tagged content string
     */
    public static String html(String content) {
        return TAG_HTML + content;
    }
    
    /**
     * Returns a text-tagged content string for plain-text output.
     * 
     * @param content the text content
     * @return a text-tagged content string
     */
    public static String text(String content) {
        return TAG_TEXT + content;
    }
    
    /**
     * Returns a xml-tagged content string for xml output.
     * 
     * @param content the xml content
     * @return a xml-tagged content string
     */
    public static String xml(String content) {
        return TAG_XML + content;
    }
    
    /**
     * Returns a forward-tagged URI string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an URI string
     * @param record an ActiveRecord instance
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, ActiveRecord record) {
        return forwardTo(uri, record.getPrimaryKeyDataMap());
    }
    
    /**
     * Returns a forward-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a map of name and value pairs as HTTP query string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, Map<String, Object> nameValuePairs) {
        return forwardTo(uri, Converters.convertMapToUrlString(nameValuePairs));
    }
    
    /**
     * Returns a forward-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a string of name and value pairs as HTTP query string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, String nameValuePairs) {
        if (nameValuePairs == null || "".equals(nameValuePairs)) {
            return TAG_FORWARD_TO + uri;
        }
        return TAG_FORWARD_TO + uri + "?" + nameValuePairs;
    }
    
    /**
     * Returns a forward-tagged URI string.
     * 
     * @param uri an URI string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri) {
        return forwardTo(uri, "");
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an URI string
     * @param record an ActiveRecord instance
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, ActiveRecord record) {
        return redirectTo(uri, record.getPrimaryKeyDataMap());
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a map of name and value pairs as HTTP query string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, Map<String, Object> nameValuePairs) {
        return redirectTo(uri, Converters.convertMapToUrlString(nameValuePairs));
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a string of name and value pairs as HTTP query string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, String nameValuePairs) {
    	if (uri == null) 
    		throw new IllegalArgumentException("uri cannot be null.");
    	
    	String newURI = uri;
        if (nameValuePairs != null && !"".equals(nameValuePairs)) {
        	newURI = uri + "?" + nameValuePairs;
        }
        
        if (ActionControl.isAjaxRequest()) {
        	if (!newURI.toLowerCase().startsWith("http")) {
        		newURI = W.getURL(newURI);
        	}
        	newURI = "<script type=\"text/javascript\">window.location=\"" + newURI + "\"</script>";
        	return TAG_HTML + newURI;
        }
        
        return TAG_REDIRECT_TO + newURI;
    }
    
    /**
     * Returns a redirect-tagged URI string.
     * 
     * @param uri an URI string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri) {
        return redirectTo(uri, "");
    }
    
    /**
     * Checks if a result has a certain tag.
     * 
     * @param result the result
     * @param tag the tag to check
     * @return true if the result contains the tag
     */
    public static boolean checkResultTag(Object result, String tag) {
        if (result == null) return false;
        return (result.toString().startsWith(tag))?true:false;
    }
    
    /**
     * Returns content as denoted by the specified tag. 
     * 
     * @param result a string with a tag
     * @param tag the string denoting the result
     * @return a string of result without the tag
     */
    public static String getResultContentByTag(Object result, String tag) {
        if (result == null || tag == null) return null;
        
        if (!tag.endsWith(TAG_SYMBOL)) {
        	tag += TAG_SYMBOL;
        }
        
        String r = result.toString();
        if (checkResultTag(result, tag)) {
            r = r.substring(tag.length()).trim();
        }
        
        return r;
    }
    
    /**
     * Checks if the <tt>content</tt> starts with a content type tag.
     * 
     * @param content the content to check
     * @return true if the content starts with a content type tag
     */
    public static boolean startsWithContentTypeTag(String content) {
    	if (content == null || "".equals(content)) return false;
    	int tagIndex = content.indexOf(ActionResult.TAG_SYMBOL);
    	if (tagIndex == -1) return false;
    	return (tagIndex <= 10)?true:false;
    }
    
    /**
     * Gets content type tag that is at the beginning of the <tt>content</tt>.
     * 
     * @param content the content string
     * @return the content type tag
     */
    public static String getContentTypeTag(String content) {
    	int tagIndex = content.indexOf(ActionResult.TAG_SYMBOL);
    	return (tagIndex == -1)?null:content.substring(0, tagIndex);
    	
    }
}
