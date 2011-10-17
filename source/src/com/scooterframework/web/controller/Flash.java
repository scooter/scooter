/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.admin.Constants;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.ExpandedMessage;
import com.scooterframework.common.util.Message;

/**
 * <p>
 * Flash class contains all flash related methods.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class Flash {
	static LogUtil log = LogUtil.getLogger(Flash.class.getName());
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     */
    public static void flash(String type, String message) {
        ACH.getAC().setFlashMessage(type, message);
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void flash(String type, String message, Object value) {
        flash(type, new ExpandedMessage(null, message, value));
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void flash(String type, String message, Object value0, Object value1) {
        flash(type, new ExpandedMessage(null, message, value0, value1));
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void flash(String type, String message, Object value0, Object value1, Object value2) {
        flash(type, new ExpandedMessage(null, message, value0, value1, value2));
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param type      flash message type
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void flash(String type, Message message) {
        ACH.getAC().setFlashMessage(type, message);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     */
    public static void error(String message) {
        flash(Constants.FLASH_TYPE_ERROR, message);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void error(String message, Object value) {
        flash(Constants.FLASH_TYPE_ERROR, message, value);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void error(String message, Object value0, Object value1) {
        flash(Constants.FLASH_TYPE_ERROR, message, value0, value1);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void error(String message, Object value0, Object value1, Object value2) {
        flash(Constants.FLASH_TYPE_ERROR, message, value0, value1, value2);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void error(Message message) {
        flash(Constants.FLASH_TYPE_ERROR, message);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     */
    public static void notice(String message) {
        flash(Constants.FLASH_TYPE_NOTICE, message);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void notice(String message, Object value) {
        flash(Constants.FLASH_TYPE_NOTICE, message, value);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void notice(String message, Object value0, Object value1) {
        flash(Constants.FLASH_TYPE_NOTICE, message, value0, value1);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void notice(String message, Object value0, Object value1, Object value2) {
        flash(Constants.FLASH_TYPE_NOTICE, message, value0, value1, value2);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void notice(Message message) {
        flash(Constants.FLASH_TYPE_NOTICE, message);
    }
}
