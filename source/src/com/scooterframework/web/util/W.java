/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.petebevin.markdown.MarkdownProcessor;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.common.util.ExpandedMessage;
import com.scooterframework.common.util.Message;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.i18n.Messages;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.controller.ACH;
import com.scooterframework.web.controller.ActionContext;

/**
 * <p>W(WebHelper) class has helper methods for web related requests, including
 * AJAX requests. </p>
 * 
 * <p><tt>buttonKeys</tt> supported:</br>
 * disabled, name, type, value, id, class, title, style, dir, lang, accesskey, 
 * tabindex, onblur, onclick, ondblclick, onfocus, onkeydown, onkeypress, 
 * onkeyup, onmousedown, onmousemove, onmouseout, onmouseover, onmouseup
 * </p>
 * 
 * <p><tt>imageKeys</tt> supported:</br>
 * align (deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD), alt, 
 * border (deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD), class, 
 * dir, height, hspace (deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD), 
 * id, ismap, lang, longdesc, name, onclick, ondblclick, onkeydown, onkeypress, 
 * onkeyup, onmousedown, onmousemove, onmouseout, onmouseover, onmouseup, src, 
 * style, title, usemap, vspace (deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD), 
 * width, 
 * </p>
 * 
 * <p><tt>inputKeys</tt> supported:</br>
 * disabled, name, type, value, id, class, title, style, dir, lang, accesskey, 
 * tabindex, onblur, onclick, ondblclick, onfocus, onkeydown, onkeypress, onkeyup, 
 * onmousedown, onmousemove, onmouseout, onmouseover, onmouseup, onselect, onchange
 * </p>
 * 
 * <p><tt>linkKeys</tt> supported:</br>
 * accesskey, charset, class, coords, dir, hreflang, id, lang, name, onblue, 
 * onclick, ondblclick, onfocus, onkeydown, onkeypress, onkeyup, onmousedown, 
 * onmousemove, onmouseout, onmouseover, onmouseup, rel, rev, shape, tabindex, 
 * target, title, type
 * </p>
 * 
 * <p>AJAX related links are supported through <tt>data-*</tt> keys:</p>
 * <pre>
 *   data-ajax: Its value is always true. This is the required property for AJAX.
 *   data-confirm: A confirmation message to be popped up
 *   data-method: HTTP method (GET, POST, UPDATE, DELETE) of AJAX link, "GET" is the default.
 *   data-target: The target element in jQuery syntax, e.g. #posts_list.
 *   data-handler: indicates how the returned message is handled, e.g. html.
 *   data-type: The type of data expected from the server. See jQuery document.
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class W {

    private static final LogUtil log = LogUtil.getLogger(W.class.getName());
    public static final MarkdownProcessor mp = new MarkdownProcessor();
    public static final List<String> html5Keys = new ArrayList<String>();
    public static final List<String> buttonKeys = new ArrayList<String>();
    public static final List<String> imageKeys = new ArrayList<String>();
    public static final List<String> inputKeys = new ArrayList<String>();
    public static final List<String> linkKeys = new ArrayList<String>();
    public static final List<String> styleKeys = new ArrayList<String>();
    
    private static final String DELETE_ADDON = 
                    "f.style.display = 'none'; " + 
                    "this.parentNode.appendChild(f); " + 
                    "f.method = 'POST'; " + 
                    "f.action = this.href; " + 
                    "var m = document.createElement('input'); " + 
                    "m.setAttribute('type', 'hidden'); " + 
                    "m.setAttribute('name', '_method'); " + 
                    "m.setAttribute('value', 'delete'); " + 
                    "f.appendChild(m); ";
    
    static {
    	html5Keys.add("data-ajax");
    	html5Keys.add("data-confirm");
    	html5Keys.add("data-method");
    	html5Keys.add("data-target");
    	html5Keys.add("data-handler");
    	html5Keys.add("data-type");
    	
    	buttonKeys.addAll(html5Keys);
        buttonKeys.add("disabled");
        buttonKeys.add("name");
        buttonKeys.add("type");
        buttonKeys.add("value");
        buttonKeys.add("id");
        buttonKeys.add("class");
        buttonKeys.add("title");
        buttonKeys.add("style");
        buttonKeys.add("dir");
        buttonKeys.add("lang");
        buttonKeys.add("accesskey");
        buttonKeys.add("tabindex");
        buttonKeys.add("onblur");
        buttonKeys.add("onclick");
        buttonKeys.add("ondblclick");
        buttonKeys.add("onfocus");
        buttonKeys.add("onkeydown");
        buttonKeys.add("onkeypress");
        buttonKeys.add("onkeyup");
        buttonKeys.add("onmousedown");
        buttonKeys.add("onmousemove");
        buttonKeys.add("onmouseout");
        buttonKeys.add("onmouseover");
        buttonKeys.add("onmouseup");

        imageKeys.addAll(html5Keys);
        imageKeys.add("align");//deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD
        imageKeys.add("alt");
        imageKeys.add("border");//deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD
        imageKeys.add("class");
        imageKeys.add("dir");
        imageKeys.add("height");
        imageKeys.add("hspace");//deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD
        imageKeys.add("id");
        imageKeys.add("ismap");
        imageKeys.add("lang");
        imageKeys.add("longdesc");
        imageKeys.add("name");
        imageKeys.add("onclick");
        imageKeys.add("ondblclick");
        imageKeys.add("onkeydown");
        imageKeys.add("onkeypress");
        imageKeys.add("onkeyup");
        imageKeys.add("onmousedown");
        imageKeys.add("onmousemove");
        imageKeys.add("onmouseout");
        imageKeys.add("onmouseover");
        imageKeys.add("onmouseup");
        imageKeys.add("src");
        imageKeys.add("style");
        imageKeys.add("title");
        imageKeys.add("usemap");
        imageKeys.add("vspace");//deprecated in HTML4.01, not supported in XHTML 1.0 Strict DTD
        imageKeys.add("width");

        inputKeys.addAll(html5Keys);
        inputKeys.add("disabled");
        inputKeys.add("name");
        inputKeys.add("type");
        inputKeys.add("value");
        
        //standard attributes:
        inputKeys.addAll(html5Keys);
        inputKeys.add("id");
        inputKeys.add("class");
        inputKeys.add("title");
        inputKeys.add("style");
        inputKeys.add("dir");
        inputKeys.add("lang");
        
        //event attributes:
        inputKeys.add("accesskey");
        inputKeys.add("tabindex");
        inputKeys.add("onblur");
        inputKeys.add("onclick");
        inputKeys.add("ondblclick");
        inputKeys.add("onfocus");
        inputKeys.add("onkeydown");
        inputKeys.add("onkeypress");
        inputKeys.add("onkeyup");
        inputKeys.add("onmousedown");
        inputKeys.add("onmousemove");
        inputKeys.add("onmouseout");
        inputKeys.add("onmouseover");
        inputKeys.add("onmouseup");
        inputKeys.add("onselect");
        inputKeys.add("onchange");

        linkKeys.addAll(html5Keys);
        linkKeys.add("accesskey");
        linkKeys.add("charset");
        linkKeys.add("class");
        linkKeys.add("coords");
        linkKeys.add("dir");
        linkKeys.add("hreflang");
        linkKeys.add("id");
        linkKeys.add("lang");
        linkKeys.add("name");
        linkKeys.add("onblue");
        linkKeys.add("onclick");
        linkKeys.add("ondblclick");
        linkKeys.add("onfocus");
        linkKeys.add("onkeydown");
        linkKeys.add("onkeypress");
        linkKeys.add("onkeyup");
        linkKeys.add("onmousedown");
        linkKeys.add("onmousemove");
        linkKeys.add("onmouseout");
        linkKeys.add("onmouseover");
        linkKeys.add("onmouseup");
        linkKeys.add("rel");
        linkKeys.add("rev");
        linkKeys.add("shape");
        linkKeys.add("tabindex");
        linkKeys.add("target");
        linkKeys.add("title");
        linkKeys.add("type");
    
        styleKeys.add("background-color");
        styleKeys.add("background-image");
        styleKeys.add("background-repeat");
        styleKeys.add("border-color");
        styleKeys.add("border-style");
        styleKeys.add("border-width");
        styleKeys.add("color");
        styleKeys.add("font-family");
        styleKeys.add("font-size");
        styleKeys.add("font-style");
        styleKeys.add("height");
        styleKeys.add("list-style-image");
        styleKeys.add("list-style-type");
        styleKeys.add("margin");
        styleKeys.add("outline-color");
        styleKeys.add("outline-style");
        styleKeys.add("outline-width");
        styleKeys.add("padding");
        styleKeys.add("text-align");
        styleKeys.add("text-decoration");
        styleKeys.add("vertical-align");
    }
    
    /**
     * Returns a url-encoded string.
     * 
     * @param s the input string
     * @return encoded string.
     */
    public static String encode(String s) {
        if (s == null) return s;
        try {
        	s = URLEncoder.encode(s, "UTF-8");
        } catch(UnsupportedEncodingException uee) {
            ;
        }
        return s;
    }
    
    /**
     * Returns a url-decoded string.
     * 
     * @param s the input string
     * @return decoded string.
     */
    public static String decode(String s) {
        if (s == null) return s;
        try {
        	s = URLDecoder.decode(s, "UTF-8");
        } catch(UnsupportedEncodingException uee) {
            ;
        }
        return s;
    }
    
    /**
     * Returns an object associated with the key in thread scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromThreadData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object thread(String key) {
        return ActionContext.getFromThreadData(key);
    }
    
    /**
     * Returns an object associated with the key in parameter scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromParameterData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object param(String key) {
        return ACH.getWAC().getFromParameterData(key);
    }
    
    /**
     * Returns an object associated with the key in request scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromRequestData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object request(String key) {
        return ACH.getWAC().getFromRequestData(key);
    }
    
    /**
     * Returns an object associated with the key in session scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromSessionData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object session(String key) {
        return ACH.getWAC().getFromSessionData(key);
    }
    
    /**
     * Returns an object associated with the key in context scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromContextData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object context(String key) {
        return ACH.getWAC().getFromContextData(key);
    }
    
    /**
     * Returns an object associated with the key in global scope. See method 
     * {@link com.scooterframework.web.controller.ActionContext#getFromGlobalData(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object global(String key) {
        return ActionContext.getFromGlobalData(key);
    }
    
    /**
     * Returns an object associated with the key. See method 
     * {@link com.scooterframework.web.controller.ActionContext#get(String)}.
     * 
     * @param key a string
     * @return object associated with the key
     */
    public static Object get(String key) {
        return ACH.getWAC().get(key);
    }
    
    /**
     * Returns an object associated with the key. See method 
     * {@link com.scooterframework.web.controller.ActionContext#get(String)}. 
     * 
     * If there is no value associated with the key, a default value is returned.
     * 
     * @param key a string
     * @param def default value
     * @return object associated with the key
     */
    public static Object get(String key, Object def) {
        Object ret = get(key);
        return (ret != null)?ret:def;
    }
    
    /**
     * Returns a string value for a key. The returned value is html-escaped.
     * 
     * @param key a string
     * @return string value associated with the key
     */
    public static String value(String key) {
    	Object o = get(key);
        String result = (o != null)?o.toString():"";
        return h(result);
    }
    
    /**
     * Returns a string value for a key. If there is no value found from the 
     * <tt>request</tt>, the key is treated as a field name of the record 
     * and its corresponding value in the record is returned. 
     * 
     * The returned value is html-escaped.
     * 
     * @param key a string
     * @param request a http request
     * @param record an active record instance
     * @return string value associated with the key
     */
    public static String value(String key, HttpServletRequest request, ActiveRecord record) {
        String result = request.getParameter(key);
        if (result == null) {
            result = (String) request.getAttribute(key);
            if (result == null && record != null) {
                Object o = record.getField(key);
                if (o != null) {
                    result = o.toString();
                }
            }
        }
        return h(result);
    }
    
    /**
     * Checks if the request is a local request.
     * 
     * @return true if local request
     */
    public static boolean isLocalRequest() {
        String s = (String) CurrentThreadCache.get(Constants.LOCAL_REQUEST);
        if ((Constants.VALUE_FOR_LOCAL_REQUEST).equals(s)) {
            return true;
        }
        return false;
    }
    
    /**
     * Checks whether the object represented by the key has property that is empty.
     * 
     * @param key
     * @param property
     * @return true if the property value is empty.
     */
    public static boolean isEmpty(String key, String property) {
        return isEmpty(O.getProperty(key, property));
    }
    
    /**
     * Checks whether the object has property that is empty.
     * 
     * @param object
     * @param property
     * @return true if the property value is empty.
     */
    public static boolean isEmpty(Object object, String property) {
        return isEmpty(O.getProperty(object, property));
    }
    
    /**
     * Checks whether the object is empty.
     * 
     * @param object
     * @return true if the object is empty.
     */
    public static boolean isEmpty(Object object) {
        return M.isEmpty(object);
    }
    
    /**
     * Checks whether the Object represented by the key has property value that 
     * is equal to the desired value.
     * 
     * @param key
     * @param property
     * @param desired
     * @return true if the property has the same value as the desired.
     */
    public static boolean isEqual(String key, String property, Object desired) {
        return isEqual(O.getProperty(key, property), desired);
    }
    
    /**
     * Checks whether the Object's property has the same value as the desired value.
     * 
     * @param object
     * @param property
     * @param desired
     * @return true if the property has the same value as the desired.
     */
    public static boolean isEqual(Object object, String property, Object desired) {
        return isEqual(O.getProperty(object, property), desired);
    }
    
    /**
     * Checks whether the object has the same value as the desired value.
     * 
     * @param object
     * @param desired
     * @return true if the object has the same value as the desired.
     */
    public static boolean isEqual(Object object, Object desired) {
        if (object == null) {
            return (desired == null)?true:false;
        }
        else {
            if (desired == null) return false;
        }
        return (compare(object, desired) == 0)?true:false;
    }
    
    /**
     * Checks whether the Object represented by the key has property value that 
     * is greater than the desired value.
     * 
     * @param key
     * @param property
     * @param desired
     * @return true if the property value is greater than the desired.
     */
    public static boolean isGreaterThan(String key, String property, Object desired) {
        return isGreaterThan(O.getProperty(key, property), desired);
    }
    
    /**
     * Checks whether the Object's property value is greater than the desired value.
     * 
     * @param object
     * @param property
     * @param desired
     * @return true if the property value is greater than the desired.
     */
    public static boolean isGreaterThan(Object object, String property, Object desired) {
        return isGreaterThan(O.getProperty(object, property), desired);
    }
    
    /**
     * Checks whether the object value is greater than the desired value.
     * 
     * @param object
     * @param desired
     * @return true if the object value is greater than the desired.
     */
    public static boolean isGreaterThan(Object object, Object desired) {
        return (compare(object, desired) == +1)?true:false;
    }
    
    /**
     * Checks whether the Object represented by the key has property value that 
     * is greater than or equal to the desired value.
     * 
     * @param key
     * @param property
     * @param desired
     * @return true if the property value is greater than or equal to the desired.
     */
    public static boolean isGreaterThanOrEqual(String key, String property, Object desired) {
        return isGreaterThanOrEqual(O.getProperty(key, property), desired);
    }
    
    /**
     * Checks whether the Object's property value is greater than or equal to the desired value.
     * 
     * @param object
     * @param property
     * @param desired
     * @return true if the property value is greater than or equal to the desired.
     */
    public static boolean isGreaterThanOrEqual(Object object, String property, Object desired) {
        return isGreaterThanOrEqual(O.getProperty(object, property), desired);
    }
    
    /**
     * Checks whether the object value is greater than or equal to the desired value.
     * 
     * @param object
     * @param desired
     * @return true if the object value is greater than or equal to the desired.
     */
    public static boolean isGreaterThanOrEqual(Object object, Object desired) {
        int res = compare(object, desired);
        return (res == +1 || res == 0)?true:false;
    }
    
    /**
     * Checks whether the Object represented by the key has property value that 
     * is less than the desired value.
     * 
     * @param key
     * @param property
     * @param desired
     * @return true if the property value is less than the desired.
     */
    public static boolean isLessThan(String key, String property, Object desired) {
        return isLessThan(O.getProperty(key, property), desired);
    }
    
    /**
     * Checks whether the Object's property value is less than the desired value.
     * 
     * @param object
     * @param property
     * @param desired
     * @return true if the property value is less than the desired.
     */
    public static boolean isLessThan(Object object, String property, Object desired) {
        return isLessThan(O.getProperty(object, property), desired);
    }
    
    /**
     * Checks whether the object value is less than the desired value.
     * 
     * @param object
     * @param desired
     * @return true if the object value is less than the desired.
     */
    public static boolean isLessThan(Object object, Object desired) {
        return (compare(object, desired) == -1)?true:false;
    }
    
    /**
     * Checks whether the Object represented by the key has property value that 
     * is less than or equal to the desired value.
     * 
     * @param key
     * @param property
     * @param desired
     * @return true if the property value is less than or equal to the desired.
     */
    public static boolean isLessThanOrEqual(String key, String property, Object desired) {
        return isLessThanOrEqual(O.getProperty(key, property), desired);
    }
    
    /**
     * Checks whether the Object's property value is less than or equal to the desired value.
     * 
     * @param object
     * @param property
     * @param desired
     * @return true if the property value is less than or equal to the desired.
     */
    public static boolean isLessThanOrEqual(Object object, String property, Object desired) {
        return isLessThanOrEqual(O.getProperty(object, property), desired);
    }
    
    /**
     * Checks whether the object value is less than or equal to the desired value.
     * 
     * @param object
     * @param desired
     * @return true if the object value is less than or equal to the desired.
     */
    public static boolean isLessThanOrEqual(Object object, Object desired) {
        int res = compare(object, desired);
        return (res == -1 || res == 0)?true:false;
    }
    
    private static int compare(Object object, Object compared) {
        if (object == null || compared == null) 
            throw new IllegalArgumentException("Either input object or compared object is null.");
        
        int result = 0;
        try {
            double dObject = Double.parseDouble(object.toString());
            double dCompared = Double.parseDouble(compared.toString());
            if (dObject < dCompared) {
                result = -1;
            } else if (dObject > dCompared) {
                result = +1;
            }
        } catch (NumberFormatException ed) {
            try {
                long lObject = Long.parseLong(object.toString());
                long lCompared = Long.parseLong(compared.toString());
                if (lObject < lCompared) {
                    result = -1;
                } else if (lObject > lCompared) {
                    result = +1;
                }
            } catch (NumberFormatException el) {
                result = object.toString().compareTo(compared.toString());
            }
        }
        
        return result;
    }
    
    /**
     * Checks whether there is data represent by the key string in a scope.
     * 
     * @param key
     * @return true if found
     */
    public static boolean isPresent(String key) {
        return (ACH.getAC().get(key) != null)?true:false;
    }
    
    /**
     * <p>Checks whether there is data represent by the key string in a specific 
     * scope.</p>
     * 
     * <p>There are four accepted scope constants: parameter, request, sesison and 
     * context. All are defined in ActionContext class.</p>
     * 
     * @param key
     * @return true if found
     */
    public static boolean isPresent(String key, String scope) {
        return (ACH.getAC().get(key, scope) != null)?true:false;
    }
    
    /**
     *  Removes the following html unfriendly characters from the input 
     *  string:<br/>
     *  double quote(&#34;), single quote(&#39;), lower than(&#60;), 
     *  greater than(&#62;), and &amp;.
     */
    public static String htmlEsacpe(String s) {
        if (s == null || s.length() == 0) return s;
        s = StringUtil.replace(s, "&", "&amp;");
        s = StringUtil.replace(s, "\"", "&#34;");
        s = StringUtil.replace(s, "'", "&#39;");
        s = StringUtil.replace(s, "<", "&#60;");
        s = StringUtil.replace(s, ">", "&#62;");
        return s;
    }
    
    /**
     * A short-hand alias of {@link #htmlEsacpe(java.lang.String) htmlEsacpe} method.
     * 
     * @param s input string
     * @return html escaped string
     */
    public static String h(String s) {
        return htmlEsacpe(s);
    }
    
    /**
     * <p>Returns html select code of a list of data and options.
     * Data items in the dataList can be any object.</p>
     * 
     * <p>The <tt>options</tt> string contains name and value pairs of options. 
     * The name and value are separated by colon, while each pair is separated by 
     * semi-colon. </p>
     * 
     * The follow option keys are supported:
     * <pre>
     * options: id:aaa; class:bbb; javascript:ccc; optionId:ddd; optionValue:eee;
     *          selectedId:fff; useBlank:true; prompt:ggg
     * </pre>
     * 
     * <pre>
     * Description of the option keys:
     * id:          html select id
     * class:       css class of the select
     * javascript:  javascript codes embeded in the select
     * optionId:    the property name of the data item object that will be used in the value portion of the option
     * optionValue: the property name of the data item object that will be used in the display portion of the option
     * selectedId:  selected optionId or optionValue if optionId is not present
     * useBlank:    add a blank option. If the useBlank is not "false", its value is display in the option.
     * prompt:      a prompt option displayed only when useBlank if not present. For example, <option>Select a category</option>
     * </pre>
     * <pre>
     * Examples:
     * 1. nameList: Cindy, Dave, Eddy, Frank
     *    displayHtmlSelect("person", nameList, "id:fname;class:big;useBlank:None")
     *    The generated select html code is:
     *    &#60;select id=fname, class=big, name=person&#62;
     *        &#60;option&#62;None&#60;/option&#62;
     *        &#60;option value="Cindy"&#62;Cindy&#60;/option&#62;
     *        &#60;option value="Dave"&#62;Dave&#60;/option&#62;
     *        &#60;option value="Eddy"&#62;Eddy&#60;/option&#62;
     *        &#60;option value="Frank"&#62;Frank&#60;/option&#62;
     *    &#60;/select&#62;
     * 
     * 2. stateList: a list of states {(id=1, name=CA), (id=2, name=MA), (id=3, name=VA)}
     *    displayHtmlSelect("state", stateList, "optionId:id;optionValue:name;prompt:Select a state;selectedId:3")
     *    The generated select html code is:
     *    &#60;select id=fname, class=big, name=person&#62;
     *        &#60;option&#62;Select a state&#60;/option&#62;
     *        &#60;option value="1"&#62;CA&#60;/option&#62;
     *        &#60;option value="2"&#62;MA&#60;/option&#62;
     *        &#60;option value="3" selected="selected"&#62;VA&#60;/option&#62;
     *    &#60;/select&#62;
     * </pre>
     * @param name the name of the select
     * @param dataList the list of data to be used in the select
     * @param options configuration options of the select
     * @return html select string
     */
    public static String displayHtmlSelect(String name, List<Object> dataList, String options) {
        Map<String, String> optionsMap = Converters.convertStringToMap(options, ":", ";");
        String id = optionsMap.get("id");
        String cssClass = optionsMap.get("class");
        String javascript = optionsMap.get("javascript");
        String optionId = optionsMap.get("optionId");
        String optionValue = optionsMap.get("optionValue");
        String selectedId = optionsMap.get("selectedId");
        String useBlank = optionsMap.get("useBlank");
        if (useBlank == null) useBlank = "false";
        String prompt = optionsMap.get("prompt");
        
        StringBuilder selectSB = new StringBuilder();
        selectSB.append("<select ");
        if (id != null && !"".equals(id)) 
            selectSB.append("id=\"").append(id).append("\" ");
        
        if (cssClass != null && !"".equals(cssClass)) 
            selectSB.append("class=\"").append(cssClass).append("\" ");
        
        if (name != null && !"".equals(name)) 
            selectSB.append("name=\"").append(name).append("\" ");
        
        if (javascript != null && !"".equals(javascript)) 
            selectSB.append(javascript).append(" ");
        
        selectSB.append(">");
        
        if (!"false".equalsIgnoreCase(useBlank)) {
            selectSB.append("<option>").append(useBlank).append("</option>");
        }
        else {
            if (prompt != null) {
                selectSB.append("<option>").append(prompt).append("</option>");
            }
        }
        
        if (dataList != null) {
            Iterator<Object> it = dataList.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                String oid = "";
                if (optionId != null) {
                    oid = O.property(o, optionId);
                }
                else {
                    oid = T.text(o);
                }
                selectSB.append("<option value=\"").append(oid).append("\"");
                if (selectedId != null && selectedId.equals(oid)) selectSB.append(" selected=\"selected\"");
                selectSB.append(">");
                
                String ovalue = "";
                if (optionValue != null) {
                    ovalue = O.property(o, optionValue);
                }
                else {
                    ovalue = T.text(o);
                }
                selectSB.append(ovalue);
                
                selectSB.append("</option>");
            }
        }
        
        selectSB.append("</select>");
        return selectSB.toString();
    }
    
    @SuppressWarnings("unchecked")
	public static String displayHtmlSelect(String name, String dataListKey, String options) {
        return displayHtmlSelect(name, (List<Object>)get(dataListKey), options);
    }
    
    /**
     * Returns a string of Yahoo-style pagination page links. The following 
     * is an example of the Yahoo-style page links for page limit of 10:
     * 
     * <pre>
     * Showing 31 - 40 of 42  First |  Previous |  Next |  Last
     * </pre>
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @return a string of Yahoo-style pagination links
     */
    public static String yahooStylePageLinks(Paginator paginator, String actionPath) {
    	return yahooStylePageLinks(paginator, actionPath, (String)null);
    }
    
    /**
     * Returns a string of Yahoo-style pagination page links. The following 
     * is an example of the Yahoo-style page links for page limit of 10:
     * 
     * <pre>
     * Showing 31 - 40 of 42  First |  Previous |  Next |  Last
     * </pre>
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @param linkProperties  string of link related properties
     * @return a string of Yahoo-style pagination links
     */
    public static String yahooStylePageLinks(Paginator paginator, 
    		String actionPath, String linkProperties) {
        if (paginator == null || paginator.getTotalCount() == 0) return "";
        
        String queryStringFirst = paginator.getQueryStringFirst();
        String queryStringPrevious = paginator.getQueryStringPrevious();
        String queryStringNext = paginator.getQueryStringNext();
        String queryStringLast = paginator.getQueryStringLast();
        
        StringBuilder linkSB = new StringBuilder();
        linkSB.append("Showing ").append(paginator.getStartIndex()).append(" - ");
        linkSB.append(paginator.getEndIndex()).append(" of ").append(paginator.getTotalCount()).append(" ");
        
        if (linkProperties == null || "".equals(linkProperties)) {
        	linkProperties = "";
        }
        else {
        	linkProperties += "; ";
        }
        
        if (queryStringFirst == null || "".equals(queryStringFirst)) {
            linkSB.append("First");
        }
        else {
            linkSB.append(pageLink("First", actionPath, queryStringFirst, linkProperties));
        }
        linkSB.append(" | ");
        
        if (queryStringPrevious == null || "".equals(queryStringPrevious)) {
            linkSB.append("Previous");
        }
        else {
            linkSB.append(pageLink("Previous", actionPath, queryStringPrevious, linkProperties));
        }
        linkSB.append(" | ");
        
        if (queryStringNext == null || "".equals(queryStringNext)) {
            linkSB.append("Next");
        }
        else {
            linkSB.append(pageLink("Next", actionPath, queryStringNext, linkProperties));
        }
        linkSB.append(" | ");
        
        if (queryStringLast == null || "".equals(queryStringLast)) {
            linkSB.append("Last");
        }
        else {
            linkSB.append(pageLink("Last", actionPath, queryStringLast, linkProperties));
        }

        return linkSB.toString();
    }
    
    /**
     * Returns a string of pagination page links. 
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @return a string of pagination links
     */
    public static String paginationLinks(Paginator paginator, String actionPath) {
    	return paginationLinks(paginator, actionPath, (String)null);
    }
    
    /**
     * Returns a string of pagination page links. 
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @param linkProperties  string of link related properties
     * @return a string of pagination links
     */
    public static String paginationLinks(Paginator paginator, 
    		String actionPath, String linkProperties) {
        if (paginator == null || paginator.getTotalCount() == 0) return "";
        
        int total = paginator.getPageCount();
        int current = paginator.getCurrentPage();
        int beginWindowIndex = 1;
        int endWindowIndex = total;
        
        if (linkProperties == null || "".equals(linkProperties)) {
        	linkProperties = "";
        }
        else {
        	linkProperties += "; ";
        }
        
        StringBuilder selectSB = new StringBuilder();
        
        //link for window
        for(int i=beginWindowIndex; i<=endWindowIndex; i++) {
            if (i==current) {
                //example: <span class="current">9</span>
                selectSB.append("<span class=\"current\">" + i + "</span>");
            }
            else {
                //example: <a href="/page10" title="Go to page 10">10</a>
                selectSB.append(pageLink("" + i, actionPath, 
                		paginator.getQueryStringPage(i), 
                		linkProperties + "title:Go to page " + i));
            }
            selectSB.append(" ");
        }
        
        return selectSB.toString();
    }
    
    /**
     * Returns a string of window-style pagination page links. 
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @return a string of window-style pagination links
     */
    public static String windowStylePageLinks(Paginator paginator, 
    		String actionPath) {
        return windowStylePageLinks(paginator, actionPath, (String)null);
    }
    
    /**
     * Returns a string of window-style pagination page links. 
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @param linkProperties  string of link related properties
     * @return a string of window-style pagination links
     */
    public static String windowStylePageLinks(Paginator paginator, 
    		String actionPath, String linkProperties) {
        return windowStylePageLinks(paginator, actionPath, -1, -1, linkProperties);
    }
    
    /**
     * Returns a string of pagination page links. The following is an example 
     * of the window-style page links for size=4 and window=11:
     * 
     * <pre> 
     * « Previous 1 2 3 4 .... 6 7 8 9 10 11 12 13 14 15 16 .... 18 19 20 21 Next »
     * </pre>
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @param side        Size of side window
     * @param window      Width of total page links shown
     * @return a string of window-style pagination links
     */
    public static String windowStylePageLinks(Paginator paginator, 
    		String actionPath, int side, int window) {
    	return windowStylePageLinks(paginator, actionPath, -1, -1, (String)null);
    }
    
    /**
     * Returns a string of pagination page links. The following is an example 
     * of the window-style page links for size=4 and window=11:
     * 
     * <pre> 
     * « Previous 1 2 3 4 .... 6 7 8 9 10 11 12 13 14 15 16 .... 18 19 20 21 Next »
     * </pre>
     * 
     * @param paginator   The Paginator instance
     * @param actionPath  Path to an action
     * @param side        Size of side window
     * @param window      Width of total page links shown
     * @param linkProperties  string of link related properties
     * @return a string of window-style pagination links
     */
    public static String windowStylePageLinks(Paginator paginator, 
    		String actionPath, int side, int window, String linkProperties) {
        if (paginator == null || paginator.getTotalCount() == 0) return "";
        if (side <= 0) side = 0;
        
        int total = paginator.getPageCount();
        if (window <= 0) window = total;//show all pages
        
        int current = paginator.getCurrentPage();
        int beginWindowIndex = (current - (window-1)/2);
        int endWindowIndex = beginWindowIndex + window -1;
        
        boolean showLHS = ((side != 0) && (beginWindowIndex > (side + 1)))?true:false;
        if (!showLHS) beginWindowIndex = 1;
        boolean showRHS = ((side != 0) && (endWindowIndex < (total-side)))?true:false;
        if (!showRHS) endWindowIndex = total;
        
        boolean addLinkOnPrev = (current > 1)?true:false;
        boolean addLinkOnNext = (current < total)?true:false;
        
        if (linkProperties == null || "".equals(linkProperties)) {
        	linkProperties = "";
        }
        else {
        	linkProperties += "; ";
        }
        
        StringBuilder selectSB = new StringBuilder();
        
        //link for prev
        if (addLinkOnPrev) {
            //example: <a href="/page8" class="nextprev" title="Go to Previous Page">&#171; Previous</a>
            selectSB.append(pageLink("&#171; Previous", actionPath, 
            		paginator.getQueryStringPrevious(), linkProperties + 
            		"class:nextprev; title:Go to Previous Page"));
        }
        else {
            //example: <span class="nextprev">&#171; Previous</span>
            selectSB.append("<span class=\"nextprev\">&#171; Previous</span>");
        }
        selectSB.append(" ");
        
        //link for LHS
        if (showLHS) {
            //example: <a href="/page1" title="Go to page 1">1</a>
            //         <a href="/page2" title="Go to page 2">2</a>
            //         <span>&#8230;.</span>
            for (int i=1; i<=side; i++) {
                selectSB.append(pageLink("" + i, actionPath, 
                		paginator.getQueryStringPage(i), linkProperties + 
                		"title:Go to page " + i));
                selectSB.append(" ");
            }
            selectSB.append("<span>&#8230;.</span>").append(" ");
        }
        
        //link for window
        for(int i=beginWindowIndex; i<=endWindowIndex; i++) {
            if (i==current) {
                //example: <span class="current">9</span>
                selectSB.append("<span class=\"current\">" + i + "</span>");
            }
            else {
                //example: <a href="/page10" title="Go to page 10">10</a>
                selectSB.append(pageLink("" + i, actionPath, 
                		paginator.getQueryStringPage(i), linkProperties + 
                		"title:Go to page " + i));
            }
            selectSB.append(" ");
        }
        
        //link for RHS
        if (showRHS) {
            selectSB.append("<span>&#8230;.</span>").append(" ");
            
            //example: <span>&#8230;.</span>
            //         <a href="/page193" title="Go to page 193">193</a>
            //         <a href="/page194" title="Go to page 194">194</a>
            for (int i=total-side+1; i<=total; i++) {
                selectSB.append(pageLink("" + i, actionPath, 
                		paginator.getQueryStringPage(i), linkProperties + 
                		"title:Go to page " + i));
                selectSB.append(" ");
            }
        }
        
        //link for next
        if (addLinkOnNext) {
            //example: <a href="/page10" class="nextprev" title="Go to Next Page">Next &#187;</a>
            selectSB.append(pageLink("Next &#187;", actionPath, 
            		paginator.getQueryStringNext(), linkProperties + 
            		"class:nextprev, title:Go to Next Page"));
        }
        else {
            //example: <span class="nextprev">Next &#187;</span>
            selectSB.append("<span class=\"nextprev\">Next &#187;</span>");
        }
        
        return selectSB.toString();
    }
    
    /**
     * <p>Sets a response header with the given name and value. If the header had
     * already been set, the new value overwrites the previous one. Otherwise, 
     * the value is added to the response. </p>
     * 
     * <p>Example: "Location:", "http://www.google.com/mypage.jsp"</p>
     * 
     * @param name  the name of the header
     * @param value the header value If it contains octet string, it should be 
     * encoded according to RFC 2047.
     */
    public static void setHTTPResponseHeader(String name, String value) {
        HttpServletResponse response = ACH.getWAC().getHttpServletResponse();
        if (response != null) {
            if (response.containsHeader(name)) {
                response.setHeader(name, value);
            }
            else {
                response.addHeader(name, value);
            }
        }
    }
    
    /**
     * <p>Sets a response header with the given name and date-value. The date is 
     * specified in terms of milliseconds since the epoch. If the header had 
     * already been set, the new value overwrites the previous one.</p>
     * 
     * @param name  the name of the date header
     * @param value the header value If it contains octet string, it should be 
     * encoded according to RFC 2047.
     */
    public static void setHTTPResponseDateHeader(String name, long value) {
        HttpServletResponse response = ACH.getWAC().getHttpServletResponse();
        if (response != null) {
            if (response.containsHeader(name)) {
                response.setDateHeader(name, value);
            }
            else {
                response.addDateHeader(name, value);
            }
        }
    }
    
    /**
     * Redirects to a new address. 
     * 
     * Examples of address:
     * <ol>
     *  <li>Absolute url: "http://www.google.com"</li>
     *  <li>Relative uri: "/shop/order.do"</li>
     *  <li>Relative uri: "order.do"</li>
     * </ol>
     * 
     * @param uri a new location to go
     * @return boolean status of the redirect. True if success. 
     */
    public static boolean redirect(String uri) {
        if (uri == null) return false;
        
        boolean success = true;
        try {
            String contextPath = getContextPath();
            if (!uri.startsWith("/")) uri = "/" + uri;
            if (uri.indexOf(contextPath) == -1) {
                uri = contextPath + uri;
            }
            HttpServletResponse response = ACH.getWAC().getHttpServletResponse();
            if (response != null) {
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
        }
        catch(Exception ex) {
            success = false;
        }
        return success;
    }
    
    public static void doForward(
        String uri,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        if (uri != null && !uri.startsWith("/")) uri = "/" + uri;
        RequestDispatcher rd = request.getSession().getServletContext().getRequestDispatcher(uri);
        
        if (rd == null) {
            uri = "/WEB-INF/views/404.jsp";
            log.error("Unable to locate \"" + uri + "\", forward to " + uri);
            rd = request.getSession().getServletContext().getRequestDispatcher(uri);
        }
        rd.forward(request, response);
    }
    
    /**
     * Returns context name of the webapp.
     */
    public static String getContextName() {
        String contextPath = getContextPath();
        return (contextPath.startsWith("/"))?contextPath.substring(1):contextPath;
    }
    
    /**
     * Returns context path of a http request. Context path is the same as 
     * document root.
     */
    public static String getContextPath() {
        return getHttpRequest().getContextPath();
    }
    
    /**
     * Returns document root of a http request. 
     */
    public static String getDocumentRoot() {
        return getContextPath();
    }
    
    /**
     * <p>Returns real path of the http servlet context. </p>
     * 
     * <p>Examples:</p>
     * <p>
     * If the myapp.war is running under E:\tomcat\webapps, the real path of the context 
     * is E:\tomcat\webapps\myapp
     * </p>
     */
    public static String getRealPath() {
        String rp = getHttpRequest().getSession().getServletContext().getRealPath("");
        return rp;
    }
    
    /**
     * <p>Returns controller path associated with the servlet request.</p>
     * <pre>
     * Examples:
     *      servletPath                     controllerPath
     *      ----------------------------------------------
     *      /posts/list.do                   /posts
     *      /report/posts/list.do            /report/posts
     *      /posts                           /posts
     * </pre>
     * 
     * @return string current controller path
     */
    public static String getCurrentControllerPath() {
        return CurrentThreadCacheClient.controllerPath();
    }
    
    /**
     * Returns http servlet request instance.
     * 
     * @return http servlet request instance
     */
    public static HttpServletRequest getHttpRequest() {
        return ACH.getWAC().getHttpServletRequest();
    }
    
    //=========================================================
    // helper methods for rendering a html form.
    //=========================================================
    
    /**
     * <p>Returns a html segment for selected choice. If the <tt>choice</tt> is 
     * the same as the <tt>value</tt>, the returned html string will be 
     * <tt>selected="selected"</tt>.</p>
     * 
     * @return html element 
     * @param value the value on the selection list
     * @param choice the value chosen by the user
     */
    public static String checkSelect(String value, String choice) {
        if (value == null || choice == null) return "";
        return value.equals(choice)?"selected=\"selected\"":"";
    }
    
    /**
     * <p>Returns a html segment for checked choice. If the <tt>choice</tt> is 
     * the same as the <tt>value</tt>, the returned html string will be 
     * <tt>checked="checked"</tt>. This is for html radio button and checkbox.</p>
     * 
     * @return html element 
     * @param value the value on the selection list
     * @param choice the value chosen by the user
     */
    public static String checkCheckbox(String value, String choice) {
        if (value == null || choice == null) return "";
        return value.equals(choice)?"checked=\"checked\"":"";
    }
    
    /**
     * <p>Returns a url. By default, this method returns relative url. </p>
     * 
     * <p>See description of {@link #getURL(java.lang.String, 
     * java.util.Map)} method for more details and examples.</p>
     * 
     * @param actionPath        path to an action
     * @return a url string
     */
    public static String getURL(String actionPath) {
        return getURL(actionPath, (Map<String, String>)null);
    }
    
    /**
     * <p>Returns a url. By default, this method returns relative url. </p>
     * 
     * <p>See description of {@link #getURL(java.lang.String, 
     * java.util.Map)} method for more details and examples.</p>
     * 
     * @param actionPath        path to an action
     * @param options           a string of options
     * @return a url string
     */
    public static String getURL(String actionPath, String options) {
        return getURL(actionPath, Converters.convertStringToMap(options, ":", ";"));
    }
    
    /**
     * <p>Returns a url. By default, this method returns relative url. </p>
     * 
     * <p>
     * The format of the generated url depends on values in options map. </p>
     * 
     * <p>
     * The options string contains name and value pairs of options. The name and 
     * value are separated by colon, while each pair is separated by semi-colon. </p>
     * 
     * The following keys are supported in the options:
     * <pre>
     * <tt>extension</tt>: overrides the default (current) extension if provided
     * <tt>fullurl</tt>: if true, a fully qualified URL like http://example.com/posts/list.do is returned. Default is false.
     * <tt>protocol</tt>: overrides the default (http) protocol if provided
     * <tt>host</tt>: overrides the default (current) host if provided
     * <tt>port</tt>: overrides the default (current) port if provided
     * <tt>encode</tt>: if true, the url's query string is encoded. Default is false.
     * </pre>
     * 
     * The generated url string may have the following format:
     * <pre>
     *      actionPath              options                         returned url
     *      --------------------------------------------------------------------
     *      list                                                    /posts/list.do
     *      /posts/list             extension:dx                    /posts/list.dx
     *      list                    extension:dx;fullurl:true       http://example.com/demo/posts/list.dx
     *      /posts/list             fullurl:true;protocol:https;    https://another.com:8080/demo/posts/list.do
     *                              host:another.com;port:8080
     * </pre>
     * 
     * <p>
     * If <tt>actionPath</tt> is empty, an empty string (not url) is returned.</p>
     * 
     * @param actionPath        path to an action
     * @param options           a map of options
     * @return a url string
     */
    public static String getURL(String actionPath, Map<String, String> options) {
        if (actionPath == null || "".equals(actionPath)) return "";
        
        if (options == null) options = new HashMap<String, String>();
        
        if (!actionPath.startsWith("/")) {
            String controllerPath = getCurrentControllerPath();
            
            if (controllerPath != null) {
                if (!controllerPath.startsWith("/")) controllerPath = "/" + controllerPath;
                actionPath = controllerPath + "/" + actionPath;
            }
            else {
                actionPath = "/" + actionPath;
            }
        }
        
        if (actionPath.indexOf('.') == -1) {
            String extension = options.get("extension");
            if (extension != null) {
                actionPath = actionPath + "." + extension;
            }
            else {
                actionPath += EnvConfig.getInstance().getActionExtension();
            }
        }
        
        String url = actionPath;
        String al = actionPath.toLowerCase();
        if (!al.startsWith("http")) {
            String contextPath = getContextPath();
        	String cp = (contextPath.endsWith("/"))?contextPath:(contextPath + "/");
        	if (!al.startsWith(cp.toLowerCase())) {
        		url = contextPath + url;
        	}
        }
        
        String fullurl = options.get("fullurl");
        if ("true".equals(fullurl)) {
            String protocol = options.get("protocol");
            if (protocol == null) protocol = "http";
            
            HttpServletRequest request = getHttpRequest();
            String host = options.get("host");
            if (host == null) host = request.getServerName();
            
            String port = options.get("port");
            if (port == null) port = "" + request.getServerPort();
            port = ("80".equals(port))?"":(":" + port);
            
            url = protocol + "://" + host + port + url;
        }
        
        String encode = options.get("encode");
        if ("true".equals(encode)) {
            String pureURL = "";
            String queryString = null;
            int qmark = url.indexOf('?');
            if (qmark != -1) {
                pureURL = url.substring(0, qmark);
                queryString = url.substring(qmark + 1);
                try {
                    queryString = URLEncoder.encode(queryString, "UTF-8");
                } catch(UnsupportedEncodingException uee) {
                    throw new IllegalArgumentException("The encoding char set is not valid.");
                }
                
                url = pureURL + "?" + queryString;
            }
        }
        return url;
    }
    
    /**
     * Returns non-query string portion of an url. A pure url string is 
     * anything before the first <tt>?</tt> in an url. 
     */
    public static String getPureURLString(String url) {
        String pureURL = url;
        int qmark = url.indexOf('?');
        if (qmark != -1) {
            pureURL = url.substring(0, qmark);
        }
        return pureURL;
    }
    
    /**
     * Returns query string portion of an url. A query string is anything after
     * the first <tt>?</tt> in an url. A <tt>null</tt> is returned if there is 
     * no <tt>?</tt> character in the url.
     */
    public static String getQueryString(String url) {
        String queryString = null;
        int qmark = url.indexOf('?');
        if (qmark != -1) {
            queryString = url.substring(qmark + 1);
        }
        return queryString;
    }
    
    /**
     * <p>Returns a button tag.</p>
     * 
     * <p>Example: </p>
     *      For face = "<tt>Click Here!</tt>", the result button tag is 
     *      <button>Click Here!</button>
     * 
     * @param face display of the button
     * @return a string of html button code
     */
    public static String buttonTag(String face) {
        return buttonTag(face, (Map<String, String>)null);
    }
    
    /**
     * <p>Returns a button tag.</p>
     * 
     * <p>The <tt>buttonProperties</tt> string contains name and value pairs 
     * of options. The name and value are separated by colon, while each pair 
     * is separated by semi-colon. </p>
     * 
     * <p>Supported keys for <tt>buttonProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For face = "Click Here!", 
     *          buttonProperties = "id:fish; name:fish", the result button tag is 
     *      <button id="fish" name="fish">Click Here!</button>
     * </pre>
     * 
     * @param face display of the button
     * @param buttonProperties string of button related properties
     * @return a string of html button code
     */
    public static String buttonTag(String face, String buttonProperties) {
        return buttonTag(face, Converters.convertStringToMap(buttonProperties, ":", ";"));
    }
    
    /**
     * <p>Returns a button tag.</p>
     * 
     * <p>The <tt>buttonProperties</tt> string contains name and value pairs 
     * of options. The name and value are separated by colon, while each pair 
     * is separated by semi-colon. </p>
     * 
     * <p>Supported keys for <tt>buttonProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For face = "Click Here!", 
     *          buttonProperties = "id:fish; name:fish", the result button tag is 
     *      <button id="fish" name="fish">Click Here!</button>
     * </pre>
     * 
     * @param face display of the button
     * @param buttonProperties map of button related properties
     * @return a string of html button code
     */
    public static String buttonTag(String face, Map<String, String> buttonProperties) {
        StringBuilder isb = new StringBuilder();
        isb.append("<button");
        String s = convertButtonPropertiesToString(buttonProperties);
        if (s != null && !"".equals(s)) {
            isb.append(" ").append(s);
        }
        isb.append(">").append(face).append("</button>");
        return isb.toString();
    }
    
    /**
     * <p>Returns a url link on a button. This does not work well with IE7. Use 
     * a corresponding <tt>submitButtonLink</tt> method instead. </p>
     * 
     * <p>Supported keys for <tt>buttonProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <p>See description of {@link #buttonLink(java.lang.String, java.util.Map, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param face              display of the button
     * @param buttonProperties  button related properties
     * @param actionPath        path to an action
     * @return a url link
     */
    public static String buttonLink(String face, String buttonProperties, String actionPath) {
        return buttonLink(face, buttonProperties, actionPath, null);
    }
    
    /**
     * <p>Returns a url link on a button. This does not work well with IE7. Use 
     * a corresponding <tt>submitButtonLink</tt> method instead. </p>
     * 
     * <p>Supported keys for <tt>buttonProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <p>Supported keys for <tt>linkProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <p>See description of {@link #buttonLink(java.lang.String, java.util.Map, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param face              display of the button
     * @param buttonProperties  button related properties
     * @param actionPath        path to an action
     * @param linkProperties    http link related properties
     * @return a url link
     */
    public static String buttonLink(String face, String buttonProperties, String actionPath, String linkProperties) {
        return buttonLink(face, Converters.convertStringToMap(buttonProperties, ":", ";"), actionPath, Converters.convertStringToMap(linkProperties, ":", ";"));
    }
    
    /**
     * <p>Returns a url link on a button. This does not work well with IE7. Use 
     * a corresponding <tt>submitButtonLink</tt> method instead. </p>
     * 
     * <p>Supported keys for <tt>buttonProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <p>Supported keys for <tt>linkProperties</tt> are listed in the 
     * description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For face = "Click Here!", 
     *          buttonProperties = "id:fish; name:fish", and 
     *          actionPath = "/category/show?catid=FISH", 
     *          linkProperties = "confirm:'Do you agree?';id:good", 
     *          the result button link is 
     *      <a href="/jpetstore/category/show?catid=FISH" onclick="return confirm('Do you agree?');" id="good"><button id="fish" name="fish">Click Here!</button></a>
     * </pre>
     * 
     * @param face              display of the button
     * @param buttonProperties  button related properties
     * @param actionPath        path to an action
     * @param linkProperties    http link related properties
     * @return a url link
     */
    public static String buttonLink(String face, Map<String, String> buttonProperties, String actionPath, Map<String, String> linkProperties) {
        return labelLink(buttonTag(face, buttonProperties), actionPath, linkProperties);
    }
    
    /**
     * Returns a html submit button.
     * 
     * <p>See description of {@link #submitButtonLink(java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param face              display of the button
     * @param actionPath        path to an action
     * @return a html submit button in a form
     */
    public static String submitButtonLink(String face, String actionPath) {
        return submitButtonLink(face, actionPath, (Map<String, String>)null);
    }
    
    /**
     * Returns a html submit button.
     * 
     * <p>See description of {@link #submitButtonLink(java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param face              display of the button
     * @param actionPath        path to an action
     * @param options           http input related properties
     * @return a html submit button in a form
     */
    public static String submitButtonLink(String face, String actionPath, String options) {
        return submitButtonLink(face, actionPath, Converters.convertStringToMap(options, ":", ";"));
    }
    
    /**
     * <p>Returns a html submit button.</p>
     * 
     * The <tt>options</tt> string contains name and value pairs of options. 
     * The name and value are separated by colon <tt>:</tt>, while each pair 
     * <p>is separated by semi-colon <tt>;</tt>. </p>
     * 
     * <p>Supported options are:</p>
     * <ul>
     * <li>all options specified in {@link #getURL(java.lang.String, java.util.Map)} method.</li>
     * <li>confirm: This is the same as "onclick:return confirm('Do you agree?')". 
     *      A html part like <tt>onclick="return confirm('Do you agree?');"</tt> will be added to the link.</li>
     * <li>method: http operation to be used by the form. Default is "<tt>GET</tt>".</li>
     * <li>many other html and css key attributes--see the <tt>inputKeys</tt> section of the description of this class.</li>
     * </ul>
     * 
     * <p>
     * Examples
     * <pre>
     *      submitButtonLink("Back to Home", "/category/show?catid=FISH", "confirm:'Do you agree?';id:fish")
     *      result submit: 
     *      <form method="GET" action="/jpetstore/category/show">
     *          <input type="hidden" name="catid" value="FISH"/>
     *          <input value="Back to Home" type="submit" onclick="return confirm('Do you agree?');" id="fish" />
     *      </form>
     *      
     *      You can also use the following because <tt>onclick</tt> is a key attribute:
     *      submitButtonLink("Back to Home", "/category/show?catid=FISH", "method:post;onclick:return confirm('Do you agree?');id:fish")
     * </pre>
     * </p>
     * 
     * @param face              display of the button
     * @param actionPath        path to an action
     * @param options           http input related properties
     * @return a html submit button in a form
     */
    public static String submitButtonLink(String face, String actionPath, Map<String, String> options) {
        String url = getURL(actionPath, options);
        
        String confirm = getMapProperty(options, "confirm", null);
        if (confirm != null) {
            if (!confirm.startsWith("'") && !confirm.endsWith("'")) confirm = "'" + confirm + "'";
            confirm = "return confirm(" + confirm + ");";
            options.put("onclick", confirm);
        }
        
        String method = getMapProperty(options, "method", "GET");
        
        StringBuilder sb = new StringBuilder();
        
        String queryString = getQueryString(url);
        if (queryString != null && !"".equals(queryString)) {
            String pureURL = getPureURLString(url);
            sb.append("<form method=\"").append(method).append("\" action=\"").append(pureURL).append("\">");
            sb.append(convertQueryStringToHiddenFields(queryString));
        }
        else {
            sb.append("<form method=\"").append(method).append("\" action=\"").append(url).append("\">");
        }
        
        sb.append("<input value=\"").append(face).append("\" type=\"submit\" ");
        sb.append(convertInputPropertiesToString(options)).append(" />");
        sb.append("</form>");
        
        return sb.toString();
    }
    
    //<input type="hidden" name="ownerId" value="1"/>
    private static StringBuilder convertQueryStringToHiddenFields(String queryString) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> m = Converters.convertStringToMap(queryString, "=", "&");
        for (Map.Entry<String, String> entry : m.entrySet()) {
            sb.append("<input type=\"hidden\" name=\"").append(entry.getKey());
            sb.append("\" value=\"").append(entry.getValue()).append("\"/>");
        }
        return sb;
    }
    
    /**
     * Returns a url link on a label related to a record.
     * 
     * @param label             link label
     * @param actionPath        path to an action
     * @param record            ActiveRecord instance
     * @return String label link
     */
    public static String labelLinkForRecord(String label, String actionPath, ActiveRecord record) {
        return labelLinkForRecord(label, actionPath, record, null);
    }
    
    /**
     * Returns a url link on a label related to a record.
     * 
     * @param label             link label
     * @param actionPath        path to an action
     * @param record            ActiveRecord instance
     * @param linkProperties    string of link related properties
     * @return url link on the label
     */
    public static String labelLinkForRecord(String label, String actionPath, 
    		ActiveRecord record, String linkProperties) {
    	if (isEmpty(label)) return "";
    	
        if (record != null) {
            String qStr = Converters.convertMapToUrlString(record.getPrimaryKeyDataMap());
            if (qStr != null && qStr.length() > 0) {
                if (actionPath.indexOf('?') == -1) {
                    actionPath = actionPath + "?" + qStr;
                }
                else {
                    actionPath = actionPath + "&" + qStr;
                }
            }
        }
        return labelLink(label, actionPath, linkProperties);
    }
    
    /**
     * <p>Returns a url link on a label.</p>
     * 
     * <p>See description of {@link #labelLink(java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param label             link label 
     * @param actionPath        path to an action
     * @return url link on the label
     */
    public static String labelLink(String label, String actionPath) {
        return labelLink(label, actionPath, (Map<String, String>)null);
    }
    
    /**
     * <p>Returns a url link on a label.</p>
     * 
     * <p>See description of {@link #labelLink(java.lang.String, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param label             link label 
     * @param actionPath        path to an action
     * @param linkProperties    string of link related properties
     * @return url link on the label
     */
    public static String labelLink(String label, String actionPath, String linkProperties) {
        return labelLink(label, actionPath, Converters.convertStringToMap(linkProperties, ":", ";"));
    }
    
    /**
     * <p>Returns a url link with query strings on a label.</p>
     * 
     * <p>The <tt>linkProperties</tt> string contains name and value pairs of 
     * options. The name and value are separated by colon, while each pair is 
     * separated by semi-colon.</p>
     * 
     * <p>Supported linkProperties are:</p>
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
     * @param label             link label 
     * @param actionPath        path to an action
     * @param linkProperties    map of link related properties
     * @return url link on the label
     */
    public static String labelLink(String label, String actionPath, Map<String, String> linkProperties) {
    	if (isEmpty(label)) return "";
    	
        if (linkProperties != null) {
            if ("true".equals(linkProperties.get(W.noLinkOnEmptyQueryString))) {
                String queryString = getQueryString(actionPath);
                if (queryString == null || "".equals(queryString.trim())) return label;
            }
        }
        
        String url = getURL(actionPath, linkProperties);
        
        if (linkProperties != null) {
            String uri = getHttpRequest().getRequestURI();
            if ("true".equals(linkProperties.get(noLinkOnCurrentUri)) && 
                url.startsWith(uri)) {
                return label;
            }
        }
        
        return createLabelLink(label, url, linkProperties);
    }
    
    private static String createLabelLink(String label, String url, Map<String, String> linkProperties) {
        if (url == null || "".equals(url)) return label;
        
        String confirm = getMapProperty(linkProperties, "confirm", null);
        if (confirm != null) {
            if (!confirm.startsWith("'") && !confirm.endsWith("'")) confirm = "'" + confirm + "'";
            String method = getMapProperty(linkProperties, "method", null);
            if (method != null) {
                if ("delete".equalsIgnoreCase(method)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("if (confirm(" + confirm + ")) { ");
                    sb.append("var f = document.createElement('form'); ");
                    sb.append(DELETE_ADDON);
                    sb.append("f.submit(); ");
                    sb.append("}; return false;");
                    confirm = sb.toString();
                }
                else {
                    confirm = "return confirm(" + confirm + ");";
                }
            }
            else {
                confirm = "return confirm(" + confirm + ");";
            }
            linkProperties.put("onclick", confirm);
        }
        
        String popup = getMapProperty(linkProperties, "popup", null);
        if (popup != null) {
            if ("true".equals(popup)) {
                popup = "window.open(this.href);return false";
            }
            else {
                popup = "window.open(" + popup + ");return false;";
            }
            linkProperties.put("onclick", popup);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"").append(url).append("\" ").append(convertLinkPropertiesToString(linkProperties));
        sb.append(">").append(label).append("</a>");
        return sb.toString();
    }
    
    /**
     * <p>Returns an image tag.</p>
     * 
     * Example: 
     *      <img src="../images/fish_icon.gif" />
     * 
     * @param imageSrc          source path to image file
     * @return a string of html image code
     */
    public static String imageTag(String imageSrc) {
        return imageTag(imageSrc, (Map<String, String>)null);
    }
    
    /**
     * <p>Returns an image tag.</p>
     * 
     * <p>The <tt>imageProperties</tt> string contains name and value pairs 
     * of options. The name and value are separated by colon, while each pair 
     * is separated by semi-colon. </p>
     * 
     * <p>Supported keys for <tt>imageProperties</tt> are listed in the 
     * <tt>imageKeys</tt> section of the description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For imageProperties = "alt:Fish; id:fish; name:fish", the result image tag is 
     *      <img src="../images/fish_icon.gif" alt="Fish" id="fish" name="fish" />
     * </pre>
     * 
     * @param imageSrc          source path to image file
     * @param imageProperties   string of image related properties
     * @return a string of html image code
     */
    public static String imageTag(String imageSrc, String imageProperties) {
        return imageTag(imageSrc, Converters.convertStringToMap(imageProperties, ":", ";"));
    }
    
    /**
     * <p>Returns an image tag.</p>
     * 
     * <p>Supported keys for <tt>imageProperties</tt> are in the 
     * <tt>imageKeys</tt> section of the description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For imageProperties = "alt:Fish; id:fish; name:fish", the result image tag is 
     *      <img src="../images/fish_icon.gif" alt="Fish" id="fish" name="fish" />
     * </pre>
     * 
     * @param imageSrc          source path to image file
     * @param imageProperties   map of image related properties
     * @return a string of html image code
     */
    public static String imageTag(String imageSrc, Map<String, String> imageProperties) {
        StringBuilder isb = new StringBuilder();
        isb.append("<img src=\"").append(imageSrc).append("\" ");
        isb.append(convertImagePropertiesToString(imageProperties));
        isb.append(" />");
        return isb.toString();
    }
    
    /**
     * <p>Returns a url link on an image.</p>
     * 
     * <p>See description of {@link #imageLink(java.lang.String, java.util.Map, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param imageSrc          source path to image file
     * @param imageProperties   image related properties
     * @param actionPath        path to an action
     * @return a url link
     */
    public static String imageLink(String imageSrc, String imageProperties, String actionPath) {
        return imageLink(imageSrc, imageProperties, actionPath, null);
    }
    
    /**
     * <p>Returns a url link on an image.</p>
     * 
     * <p>See description of {@link #imageLink(java.lang.String, java.util.Map, 
     * java.lang.String, java.util.Map)} method for more details and examples.</p>
     * 
     * @param imageSrc          source path to image file
     * @param imageProperties   image related properties
     * @param actionPath        path to an action
     * @param linkProperties    http link related properties
     * @return a url link
     */
    public static String imageLink(String imageSrc, String imageProperties, String actionPath, String linkProperties) {
        return imageLink(imageSrc, Converters.convertStringToMap(imageProperties, ":", ";"), 
               actionPath, Converters.convertStringToMap(linkProperties, ":", ";"));
    }
    
    /**
     * <p>Returns a url link on an image.</p>
     * 
     * <p>Supported keys for <tt>imageProperties</tt> are in the <tt>imageKeys</tt> 
     * section of the description of this class.</p>
     * 
     * <p>Supported keys for <tt>linkProperties</tt> are in the <tt>linkKeys</tt> 
     * section of the description of this class.</p>
     * 
     * <pre>
     * Example: 
     *      For imageSrc = "../images/fish_icon.gif", 
     *          imageProperties = "alt:Fish; id:fish; name:fish", 
     *          actionPath = "/category/show?catid=FISH", 
     *          linkProperties = "confirm:'Do you agree?';id:good", 
     *          the result image link is 
     *      <a href="/jpetstore/category/show?catid=FISH" 
     *          onclick="return confirm('Do you agree?');" id="good">
     *          <img src="../images/fish_icon.gif" alt="Fish" id="fish" name="fish" /></a>
     * </pre>
     * 
     * @param imageSrc          source path to image file
     * @param imageProperties   image related properties
     * @param actionPath        path to an action
     * @param linkProperties    http link related properties
     * @return a url link
     */
    public static String imageLink(String imageSrc, Map<String, String> imageProperties, 
        String actionPath, Map<String, String> linkProperties) {
        return labelLink(imageTag(imageSrc, imageProperties), actionPath, linkProperties);
    }
    
    /**
     * Returns a link to a stylesheet file.
     * 
     * <link href="/$CONTEXTPATH/stylesheets/main.css" media="screen" rel="stylesheet" type="text/css" />
     * @return String link
     */
    public static String stylesheetLink(String stylesheetFileName) {
        String contextPath = getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append("<link href=\"").append(contextPath).append("/stylesheets/").append(stylesheetFileName);
        sb.append("\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" />");
        return sb.toString();
    }
    
    /**
     * Returns a link to a javascript file.
     * 
     * <script src="/javascripts/prototype.js" type="text/javascript"></script>
     * @return String link
     */
    public static String javascriptLink(String javascriptFileName) {
        String contextPath = getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append("<script src=\"").append(contextPath).append("/javascripts/").append(javascriptFileName);
        sb.append("\" type=\"text/javascript\"></script>");
        return sb.toString();
    }
    
    /**
     * Returns a url link for a page in pagination.
     * 
     * @param label             link label 
     * @param actionPath        path to an action
     * @param queryString       query string
     * @return a url link
     */
    public static String pageLink(String label, String actionPath, String queryString) {
        String linkProperties = noLinkOnEmptyQueryString + ":true";
        return pageLink(label, actionPath, queryString, linkProperties);
    }
    
    /**
     * Returns a url link for a page in pagination.
     * 
     * @param pageNumber        page number
     * @param pageTitle         title of a page
     * @param actionPath        path to an action
     * @param queryString       query string
     * @return a url link
     */
    public static String pageLink(int pageNumber, String pageTitle, String actionPath, String queryString) {
        String linkProperties = "title:" + pageTitle + ";" + noLinkOnEmptyQueryString + ":true";
        return pageLink(pageNumber+"", actionPath, queryString, linkProperties);
    }
    
    /**
     * Returns a url link for a page in pagination.
     * 
     * @param label             link label 
     * @param actionPath        path to an action
     * @param queryString       query string
     * @param linkProperties    string of link related properties
     * @return a url link
     */
    public static String pageLink(String label, String actionPath, String queryString, String linkProperties) {
    	if (linkProperties != null && !"".equals(linkProperties)) {
    		linkProperties += "; " + noLinkOnEmptyQueryString + ":true";
    	}
    	else {
    		linkProperties = noLinkOnEmptyQueryString + ":true";
    	}
        if (actionPath.indexOf('?') != -1) {
            actionPath += "&" + queryString;
        }
        else {
            actionPath += "?" + queryString;
        }
        return labelLink(label, actionPath, linkProperties);
    }
    
    /**
     * <p>Key to embed in <tt>linkProperties</tt> map or string in order not to 
     * show a link on a label.</p>
     * 
     * <p>
     * For example, if it is desired that no link is displayed when query 
     * string is empty, then developer can embed "noLinkOnEmptyQueryString:true"
     * in the <tt>linkProperties</tt> map or string.
     * </p>
     */
    public static String noLinkOnEmptyQueryString = "noLinkOnEmptyQueryString";
    
    /**
     * <p>Key to embed in <tt>linkProperties</tt> map or string in order not to
     * show a link on a label when the current uri is the same as the 
     * action path.</p>
     * 
     * <p>
     * For example, if it is desired that no link is displayed when current 
     * uri is the same as actionPath, then developer can embed "noLinkOnCurrentUri:true"
     * in the <tt>linkProperties</tt> map or string.
     * </p>
     */
    public static String noLinkOnCurrentUri = "noLinkOnCurrentUri";
    
    /**
     * Converts a button properties map to string. 
     * 
     * @param properties a map of key value pairs
     * @return string
     */
    public static String convertButtonPropertiesToString(Map<String, String> properties) {
        if (properties == null || properties.size() == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        String propertiesString = Converters.convertMapToString(properties, buttonKeys, "=", " ", true);
        String stylesString     = Converters.convertMapToString(properties, styleKeys, ":", "; ", false);
        if (!"".equals(propertiesString)) sb.append(propertiesString);
        if (!"".equals(stylesString)) sb.append(" style=\"").append(stylesString).append("\"");
        return sb.toString();
    }
    
    /**
     * Converts an image properties map to string. 
     * 
     * @param properties a map of key value pairs
     * @return string
     */
    public static String convertImagePropertiesToString(Map<String, String> properties) {
        if (properties == null || properties.size() == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        String propertiesString = Converters.convertMapToString(properties, imageKeys, "=", " ", true);
        String stylesString     = Converters.convertMapToString(properties, styleKeys, ":", "; ", false);
        if (!"".equals(propertiesString)) sb.append(propertiesString);
        if (!"".equals(stylesString)) sb.append(" style=\"").append(stylesString).append("\"");
        return sb.toString();
    }
    
    /**
     * Converts a input properties map to string. 
     * 
     * @param properties a map of key value pairs
     * @return string
     */
    public static String convertInputPropertiesToString(Map<String, String> properties) {
        if (properties == null || properties.size() == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        String propertiesString = Converters.convertMapToString(properties, inputKeys, "=", " ", true);
        String stylesString     = Converters.convertMapToString(properties, styleKeys, ":", "; ", false);
        if (!"".equals(propertiesString)) sb.append(propertiesString);
        if (!"".equals(stylesString)) sb.append(" style=\"").append(stylesString).append("\"");
        return sb.toString();
    }
    
    /**
     * Converts a link properties map to string. 
     * 
     * @param properties a map of key value pairs
     * @return string
     */
    public static String convertLinkPropertiesToString(Map<String, String> properties) {
        if (properties == null || properties.size() == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        String propertiesString = Converters.convertMapToString(properties, linkKeys, "=", " ", true);
        String stylesString     = Converters.convertMapToString(properties, styleKeys, ":", "; ", false);
        if (!"".equals(propertiesString)) sb.append(propertiesString);
        if (!"".equals(stylesString)) sb.append(" style=\"").append(stylesString).append("\"");
        return sb.toString();
    }
    
    private static String getMapProperty(Map<String, String> options, String key, String def) {
        if (options == null) return def;
        String value = options.get(key);
        return (value == null)?def:value;
    }
    
    /**
     * <p>Returns a div block containing all the error messages for the 
     * object located as an instance variable by the name of 
     * <tt>model</tt>. </p>
     * 
     * @param model   model name
     * @return String formatted error message
     */
    public static String errorMessage(String model) {
        return errorMessage(model, new Properties());
    }
    
    /**
     * <p>Returns a div block containing all the error messages for the 
     * object located as an instance variable by the name of 
     * <tt>model</tt>. </p>
     * 
     * <pre>
     * This div block can be tailored by the following options:
     * 
     * <tt>headerTag</tt> - Used for the header of the error div (default: h2)
     * <tt>header</tt> - Used for the header of the error div (default: "error(s) happened:")
     * <tt>name</tt> - The name of the error div (default: errorDetails)
     * <tt>id</tt> - The id of the error div (default: errorDetails)
     * <tt>class</tt> - The css class of the error div (default: errorDetails)
     * </pre>
     * 
     * @param model   model name
     * @param options A Map of options 
     * @return String formatted error message
     */
    public static String errorMessage(String model, Properties options) {
        String result = "";
        List<Message> errors = O.getErrorMessages(model);
        if (errors != null && errors.size() > 0) {
            String headerTag =  options.getProperty("headerTag", "h2");
            String header =  options.getProperty("header", "error(s) happened:");
            String name = options.getProperty("name", "errorDetails");
            String id = options.getProperty("id", "errorDetails");
            String css = options.getProperty("class", "errorDetails");
            String divProperty = "id=\"" + id + "\" name=\"" + name + "\" class=\"" + css + "\"";
            result = taggedContent("div", 
                        taggedContent(headerTag, errors.size() + " " + header) +
                        taggedContent("ul",
                            parseErrorMessages(errors)), divProperty
                     );
        }
        return result;
    }
    
    private static String parseErrorMessages(List<Message> errors) {
        StringBuilder sb = new StringBuilder();
        if (errors != null && errors.size() > 0) {
            Iterator<Message> it = errors.iterator();
            while(it.hasNext()) {
            	Message vm = (ExpandedMessage)it.next();
                String key = vm.getId();
                String content = vm.getContent();
                String error = "";
                if (key != null && !"".equals(key)) {
                    error = key + ": ";
                }
                error += content;
                sb.append(taggedContent("li", error));
            }
        }
        return sb.toString();
    }
    
    /**
     * <p>Returns a formatted html string.</p>
     * 
     * Example: 
     *  <div>3 Errors</div>
     * 
     * @param tag
     * @param content
     * @return String formatted html string
     */
     public static String taggedContent(String tag, String content) {
        return taggedContent(tag, content, "");
    }
    
    /**
     * <p>Returns a formatted html string.</p>
     * 
     * Example: 
     *  <div id="errorMessage" name="errorMessage">3 Errors</div>
     * 
     * @param tag        tag name
     * @param content    content of the tag
     * @param properties string of properties
     * @return String formatted html string
     */
    public static String taggedContent(String tag, String content, String properties) {
        return "<" + tag + " " + properties + ">" + content + "</" + tag + ">";
    }
    
    /**
     * <p>Returns a formatted html string.</p>
     * 
     * Example: 
     *  <div id="errorMessage" name="errorMessage">3 Errors</div>
     * 
     * @param tag        tag name
     * @param content    content of the tag
     * @param properties map of properties
     * @return String formatted html string
     */
    public static String taggedContent(String tag, String content, Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : properties.entrySet()) {
            sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\" ");
        }
        return taggedContent(tag, content, sb.toString());
    }
    
    /**
     * <p>Returns a formatted html string. The <tt>object</tt> is used to check if 
     * the record has error. </p>
     * 
     * Example: 
     *  <div id="errorMessage" name="errorMessage">3 Errors</div>
     * 
     * @param object     an object
     * @param field      field name of the object
     * @param tag        tag name
     * @param content    content of the tag
     * @param properties map of properties
     * @return String formatted html string
     */
    public static String taggedContent(Object object, String field, String tag, String content, Map<String, String> properties) {
        String result = taggedContent(tag, content, properties);
        
        boolean errorOccurred = false;
        if (object != null && object instanceof ActiveRecord) {
            ActiveRecord ar = (ActiveRecord)object;
            if (!ar.isValid() && ar.getValidationResults().hasErrorOn(field)) {
                errorOccurred = true;
            }
        }
        
        if (errorOccurred) {
            result = W.taggedContent("div", result, "class=\"fieldWithErrors\"");
        }
        
        return result;
    }
    
    /**
     * Returns an item from a list items. The name of the cycle is <tt>items</tt>.
     * 
     * Examples:
     * <pre>
     *      class=<%=WebHelper.cycle("odd, even")%> 
     *      -- use "red" class for odd rows and "blue" class for even rows.
     * </pre>
     * 
     * @param items list of items to be cycled
     * @return an item value
     */
    public static String cycle(String items) {
        return ACH.getWAC().cycle(items);
    }
    
    /**
     * Returns an item from a list items.
     * 
     * @param items list of items to be cycled
     * @param name the cycle name
     * @return an item value
     */
    public static String cycle(String items, String name) {
        return ACH.getWAC().cycle(items, name);
    }
    
    /**
     * Returns current item in the named cycle. 
     * 
     * @param name  name of the cycle
     * @return current item in the named cycle.
     */
    public static String currentCycle(String name) {
        return ACH.getWAC().currentCycle(name);
    }
    
    /**
     * Resets the cycle
     * 
     * @param name cycle's name
     */
    public static void resetCycle(String name) {
        ACH.getWAC().resetCycle(name);
    }
    
    /**
     * Returns label value stored in a property file based on user's current 
     * locale.
     * 
     * @param key label key
     * @return value associated with the key
     */
    public static String label(String key) {
        return message(key);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @return a message string
     */
    public static String message(String messageKey) {
        Locale locale = ACH.getAC().getLocale();
        return message(messageKey, locale);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @return a message string
     */
    public static String message(String messageKey, String language) {
        Locale locale = new Locale(language);
        return message(messageKey, locale);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt> of the specific <tt>country</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @param country       ISO Country Code
     * @return a message string
     */
    public static String message(String messageKey, String language, String country) {
        Locale locale = new Locale(language, country);
        return message(messageKey, locale);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt> of the specific <tt>country</tt>'s 
     * particular <tt>variant</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @param country       ISO Country Code
     * @param variant       Variant string
     * @return a message string
     */
    public static String message(String messageKey, String language, String country, String variant) {
        Locale locale = new Locale(language, country, variant);
        return message(messageKey, locale);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>locale</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param locale        a specific locale object
     * @return a message string
     */
    public static String message(String messageKey, Locale locale) {
        return Messages.get(messageKey, locale);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> and the 
     * <tt>values</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param values        an array of objects to be included in the message
     * @return a message string
     */
    public static String message(String messageKey, Object[] values) {
        return Messages.get(messageKey, values);
    }
    
    /**
     * <p>Returns a message associated with the <tt>messageKey</tt> and the 
     * <tt>values</tt> in a specific <tt>locale</tt>.</p>
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param locale        a specific locale object
     * @param values        an array of objects to be included in the message
     * @return a message string
     */
    public static String message(String messageKey, Locale locale, Object[] values) {
        return Messages.get(messageKey, locale, values);
    }
    
    /**
     * Returns html text converted by Markdown. 
     * 
     * See http://daringfireball.net/projects/markdown for details.
     * 
     * @param text a generic text
     * @return html text converted by Markdown
     */
    public static String markdown(String text) {
        return mp.markdown(text);
    }
    
    /**
     * Returns a string formatted for html display by adding <tt>"<br />"</tt> 
     * after each occurance of newlines <tt>"\n"</tt> in a string. 
     * 
     * <pre>
     * Examples: 
     *   "This is a little world. \r\n A little world." =>
     *   "This is a little world. \r\n<br /> A little world."
     * </pre>
     * 
     * @param text a string with line break
     * @return altered string
     */
    public static String nl2br(String text) {
        return text.replaceAll("\n", "\n<br />");
    }
    
    /**
     * <p>Returns a label link on the <tt>columnName</tt> for <tt>columnValue</tt>
     * for <tt>show</tt> action.</p>
     * 
     * <p>There are several restrictions of the use of this method. </p>
     * <p>First, the column name must be of the <tt>'{referencedModelName}_id'</tt> 
     * format where the <tt>referencedModelName</tt> is the model name of the 
     * referenced entity. Second, the primary key of the referenced model must 
     * be <tt>id</tt>. </p>
     * 
     * <p>If the above conditions are violated, the original 
     * <tt>columnName</tt> is returned.</p>
     * 
     * <pre>
     * Examples:
     *      column    value    link
     *      ------    -----    ----
     *      user_id   10       <a http="/blog/users/show?id=10">10</a>
     * </pre>
     * 
     * <p>See method 
     * {@link com.scooterframework.web.util.R#simpleForeignKeyResourceRecordLink(String, String)} 
     * for resource case.</p>
     * 
     * @param columnName  a column name ended with "_id"
     * @param columnValue the value on the column
     * @return a label link
     */
    public static String simpleForeignKeyRecordShowActionLink(String columnName, String columnValue) {
        if (columnName == null || 
            !columnName.toLowerCase().endsWith("_id")) return columnName;
        
        String modelName = columnName.toLowerCase().substring(0, (columnName.length() - 3));
        String modelClassName = EnvConfig.getInstance().getModelClassName(modelName);
        
        ActiveRecord foreignRecordHome = ActiveRecordUtil.getHomeInstance(modelClassName, modelName, ActiveRecordUtil.DEFAULT_RECORD_CLASS);
        String[] pkNames = foreignRecordHome.getPrimaryKeyNames();
        if (pkNames == null || pkNames.length > 1 || 
            !"id".equalsIgnoreCase(pkNames[0])) return columnValue;
        
        String controllerName = WordUtil.pluralize(modelName);
        
        return W.labelLink(columnValue, "/" + controllerName + "/show" + EnvConfig.getInstance().getActionExtension() + "?id=" + columnValue);
    }
}
