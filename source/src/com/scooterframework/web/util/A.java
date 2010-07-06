/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.Map;

import com.scooterframework.common.util.Converters;

/**
 * <p>A(AJAXHelper) class has helper methods for ajax related requests. </p>
 * 
 * @author (Fei) John Chen
 */
public class A {
    
    /**
     * Returns a url link on a label.
     * 
     * <p>See description of {@link #labelLink(java.lang.String, java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param targetElementId   a view element id
     * @param label             link label 
     * @param actionPath        path to an action
     * @return url link on the label
     */
    public static String labelLink(String targetElementId, String label, String actionPath) {
        return labelLink(targetElementId, label, actionPath, (Map)null);
    }
    
    /**
     * Returns a url link with query strings on a label.
     * 
     * <p>See description of {@link #labelLink(java.lang.String, java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param targetElementId   a view element id
     * @param label             link label 
     * @param actionPath        path to an action
     * @param linkProperties    string of link related properties
     * @return url link on the label
     */
    public static String labelLink(String targetElementId, String label, String actionPath, String linkProperties) {
        return labelLink(targetElementId, label, actionPath, Converters.convertStringToMap(linkProperties, ":", ";"));
    }
    
    /**
     * Returns a url link with query strings on a label.
     * 
     * The <tt>linkProperties</tt> string contains name and value pairs of options. 
     * The name and value are separated by colon, while each pair is separated by semi-colon. 
     * 
     * Supported linkProperties are:
     * <ul>
     * <li>all options specified in <tt>getURL(String actionPath, String options)</tt> method.</li>
     * <li>noLinkOnEmptyQueryString: if true, no link is added to the label if the query string is empty. </li>
     * <li>noLinkOnCurrentUri: if true, no link is added to the label if the current uri is the same as action path. </li>
     * <li>confirm: This is the same as "onclick:return confirm('Do you agree?')". 
     *      A html part like <tt>onclick="return confirm('Do you agree?');"</tt> will be added to the link.</li>
     * <li>popup: adds a pop-up window.</li>
     * <li>many other html and css key attributes--see the <tt>linkKeys</tt> section of the description of this class.</li>
     * </ul>
     * 
     * <p>
     * Examples
     * <pre>
     *      labelLink("Back to Home", "http://www.example.com", "", "confirm:'Do you agree?';id:good")
     *      result link: <a href="http://www.example.com" onclick="return confirm('Do you agree?');" id="good">Back to Home</a>
     *      
     *      You can also use this because <tt>onclick</tt> is a key attribute:
     *      labelLink("Back to Home", "http://www.example.com", "", "onclick:return confirm('Do you agree?');id:good")
     *      result link: <a href="http://www.example.com" onclick="return confirm('Do you agree?');" id="good">Back to Home</a>
     *      
     *      labelLink("Back to Home", "http://www.example.com", "", "popup:true")
     *      result link: <a href="http://www.example.com" onclick="window.open(this.href);return false;">Back to Home</a>
     *      
     *      labelLink("Back to Home", "http://www.example.com", "", "popup:'http://www.google.com','new_window_name','height=440,width=650,resizable,top=200,left=250,scrollbars=yes'")
     *      result link: <a href="http://www.example.com" onclick="window.open('http://www.google.com','new_window_name','height=440,width=650,resizable,top=200,left=250,scrollbars=yes');">Back to Home</a>
     * </pre>
     * </p>
     * 
     * @param targetElementId   a view element id
     * @param label             link label 
     * @param actionPath        path to an action
     * @param linkProperties    map of link related properties
     * @return url link on the label
     */
    public static String labelLink(String targetElementId, String label, String actionPath, Map linkProperties) {
        if (linkProperties != null) {
            if ("true".equals(linkProperties.get(W.noLinkOnEmptyQueryString))) {
                String queryString = W.getQueryString(actionPath);
                if (queryString == null || "".equals(queryString.trim())) return label;
            }
        }
        
        String url = W.getURL(actionPath, linkProperties);
        
        if (linkProperties != null) {
            String uri = W.getHttpRequest().getRequestURI();
            if ("true".equals(linkProperties.get(W.noLinkOnCurrentUri)) && 
                url.startsWith(uri)) {
                return label;
            }
        }
        
        return labelLink(targetElementId, label, url, linkProperties, null, null, null);
    }
     
    /**
     * <p>
     * Returns an ajax-url link on a label. If the url is empty, simply the 
     * label is returned. The result of ajax request is displayed at a place 
     * denoted by <tt>targetElementId</tt> which is an id field in an html 
     * element.</p>
     * 
     * <p>
     * The <tt>linkProperties</tt> map contains name and value pairs of options. 
     * 
     * Supported linkProperties are html and css key attributes--see 
     * the <tt>linkKeys</tt> section of the description of this class.</p>
     * 
     * <p>
     * <tt>responseHandlers</tt> must be of JSON's object format. According to 
     * <tt>http://json.org</tt>, an object is an unordered set of name/value 
     * pairs. An object begins with { (left brace) and ends with } (right 
     * brace). Each name is followed by : (colon) and the name/value pairs are 
     * separated by , (comma).
     * </p>
     * 
     * <p>
     * <tt>method</tt> must be either <tt>null</tt> or <tt>GET</tt> 
     * or <tt>POST</tt>. No other value is allowed. Default value is 
     * <tt>GET</tt>.</p>
     * 
     * <p>
     * <tt>responseType</tt> must be either <tt>null</tt> or <tt>TEXT</tt> 
     * or <tt>XML</tt>. No other value is allowed. Default value is 
     * <tt>TEXT</tt>.</p>
     * 
     * <pre>
     * Examples:
     *      labelLink("output", "show post #1", "/blog/posts/1")
     *      result link: <a href="#" onclick="ajax_link('output', '/blog/posts/1'); return false;">show post #1</a>
     * </pre>
     * 
     * @param targetElementId   a view element id
     * @param label             link label
     * @param url               url or uri string
     * @param linkProperties    map of link related properties
     * @param method            request http method, either GET(default) or POST
     * @param responseHandlers  a string of json object format
     * @param responseType      type of expected response
     * @return http link string
     */
    public static String labelLink(String targetElementId, 
            String label, String url, Map linkProperties, 
            String method, String responseHandlers, String responseType) 
    {
        if (url == null || "".equals(url)) return label;
        
        if (method != null && !"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method)) {
            throw new IllegalArgumentException("method should be either GET or POST, not '" + method + "'.");
        }
        
        if (responseType != null && !"TEXT".equals(responseType) && !"XML".equals(responseType)) {
            throw new IllegalArgumentException("responseType should be either TEXT or XML, not '" + responseType + "'.");
        }
        
        if (responseType == null) responseType = "TEXT"; 
        
        String ajaxFunctionName = "ajax_link";
        if ("TEXT".equals(responseType)) ajaxFunctionName = "ajax_link4text";
        if ("XML".equals(responseType))  ajaxFunctionName = "ajax_link4xml";
        
        StringBuffer sb = new StringBuffer();
        sb.append("<a href=\"#\" onclick=\"" + ajaxFunctionName + "('");
        
        sb.append(targetElementId).append("', '").append(url).append("'");
        
        if (method != null) {
            sb.append(", '").append(method).append("'");
        } else {
            sb.append(", undefined");
        }
        
        if (responseHandlers != null) {
            if (!responseHandlers.startsWith("{") || !responseHandlers.endsWith("}")) 
                throw new IllegalArgumentException("responseHandlers should be of JSON's object format.");
            sb.append(", ").append(responseHandlers).append("");
        } else {
            sb.append(", undefined");
        }
        sb.append(")");
        sb.append("; return false;\" ").append(W.convertLinkPropertiesToString(linkProperties));
        sb.append(">").append(label).append("</a>");
        return sb.toString();
    }
}
