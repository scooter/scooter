/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.i18n;

import java.util.Locale;

import com.scooterframework.web.controller.ACH;

/**
 * Message class provides convenient methods to access i18n messages.
 * 
 * @author (Fei) John Chen
 */
public class Messages {
    
    /**
     * Returns a message associated with the <tt>messageKey</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @return a message string
     */
    public static String get(String messageKey) {
        Locale locale = ACH.getAC().getLocale();
        return get(messageKey, locale);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @return a message string
     */
    public static String get(String messageKey, String language) {
        Locale locale = new Locale(language);
        return get(messageKey, locale);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt> of the specific <tt>country</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @param country       ISO Country Code
     * @return a message string
     */
    public static String get(String messageKey, String language, String country) {
        Locale locale = new Locale(language, country);
        return get(messageKey, locale);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>language</tt> of the specific <tt>country</tt>'s 
     * particular <tt>variant</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param language      ISO Language Code
     * @param country       ISO Country Code
     * @param variant       Variant string
     * @return a message string
     */
    public static String get(String messageKey, String language, String country, String variant) {
        Locale locale = new Locale(language, country, variant);
        return get(messageKey, locale);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> in the 
     * specific <tt>locale</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param locale        a specific locale object
     * @return a message string
     */
    public static String get(String messageKey, Locale locale) {
        String s = I18nConfig.mrm.getMessage(messageKey, locale);
        return (s != null)?s:messageKey;
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> and the 
     * <tt>values</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is returned.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param values        an array of objects to be included in the message
     * @return a message string
     */
    public static String get(String messageKey, Object[] values) {
    	if (messageKey != null && messageKey.startsWith("friendlytime")) 
    		return processFriendlytime(get(messageKey), values);
        return process(get(messageKey), values);
    }
    
    /**
     * Returns a message associated with the <tt>messageKey</tt> and the 
     * <tt>values</tt> in a specific <tt>locale</tt>.
     * 
     * <p>If there is no message associated with the <tt>messageKey</tt> in 
     * messages property files, the <tt>messageKey</tt> itself is used as the message.</p>
     * 
     * @param messageKey    a message key in messages resource files
     * @param locale        a specific locale object
     * @param values        an array of objects to be included in the message
     * @return a message string
     */
    public static String get(String messageKey, Locale locale, Object[] values) {
    	if (messageKey != null && messageKey.startsWith("friendlytime")) 
    		return processFriendlytime(get(messageKey, locale), values);
        return process(get(messageKey, locale), values);
    }
    
    private static String processFriendlytime(String message, Object[] values) {
        if (values == null || values.length == 0 || message == null || "".equals(message)) 
            return message;
        
        int length = values.length;
        for (int i=0; i < length; i++) {
            Object o = values[i];
            String r = (o==null)?"":o.toString();
            message = message.replaceAll("\\Q{" + i + "}", r);
            message = ("0".equals(r) || "1".equals(r))?
            		message.replaceFirst("\\(s\\)", ""):
            	    message.replaceFirst("\\(s\\)", "s");
        }
        
        return message;
    }
    
    private static String process(String message, Object[] values) {
        if (values == null || values.length == 0 || message == null || "".equals(message)) 
            return message;
        
        int length = values.length;
        for (int i=0; i < length; i++) {
            Object o = values[i];
            String r = (o==null)?"":o.toString();
            message = message.replaceAll("\\Q{" + i + "}", r);
        }
        
        return message;
    }
}
