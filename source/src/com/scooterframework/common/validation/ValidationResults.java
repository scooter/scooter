/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.scooterframework.common.util.ExpandedMessage;
import com.scooterframework.common.util.Message;

/**
 * ValidationResults class contains results of validations.
 * 
 * @author (Fei) John Chen
 */
public class ValidationResults implements Serializable 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1413365201819867894L;
	
	/**
     * Records a validation error message
     *
     * @param messageKey    error message key or error message
     */
    public void recordValidationException(String messageKey) {
        recordValidationException(new ExpandedMessage(messageKey));
    }
    
    /**
     * Records a validation error message for a key or a field
     *
     * @param key String key or field
     * @param messageKey    error message key or error message
     */
    public void recordValidationException(String key, String messageKey) {
        recordValidationException(new ExpandedMessage(key, messageKey));
    }
    
    /**
     * Records a validation error message for a key or a field
     *
     * @param key String key or field
     * @param messageKey    error message key or error message
     * @param value a value that can be used in the message
     */
    public void recordValidationException(String key, String messageKey, Object value) {
        recordValidationException(new ExpandedMessage(key, messageKey, value));
    }
    
    /**
     * Records a validation error message for a key or a field
     *
     * @param key String key or field
     * @param messageKey    error message key or error message
     * @param value0 a value that can be used in the message
     * @param value1 a value that can be used in the message
     */
    public void recordValidationException(String key, String messageKey, Object value0, Object value1) {
        recordValidationException(new ExpandedMessage(key, messageKey, value0, value1));
    }
    
    /**
     * Records a validation error message for a key or a field
     *
     * @param key String key or field
     * @param messageKey    error message key or error message
     * @param value0 a value that can be used in the message
     * @param value1 a value that can be used in the message
     * @param value2 a value that can be used in the message
     */
    public void recordValidationException(String key, String messageKey, Object value0, Object value1, Object value2) {
        recordValidationException(new ExpandedMessage(key, messageKey, value0, value1, value2));
    }
    
    /**
     * Records a validation error message for a key or a field
     *
     * @param key String key or field
     * @param messageKey    error message key or error message
     * @param values an array of values that can be used in the message
     */
    public void recordValidationException(String key, String messageKey, Object[] values) {
        recordValidationException(new ExpandedMessage(key, messageKey, values));
    }
    
    /**
     * Records a validation error message
     *
     * @param validationMessage a generic validation message
     */
    public void recordValidationException(Message validationMessage) {
        if (!errorMessageList.contains(validationMessage)) {
            errorMessageList.add(validationMessage);
            bFailed = true;
        }
    }

    /**
     * Checks the end result of a validation process
     *
     * @return boolean True if there is no validation failure
     */
    public boolean failed() {
        return bFailed;
    }
    
    /**
     * Clears all errors.
     */
    public void clear() {
        bFailed = false;
        errorMessageList.clear();
    }

    /**
     * Gets error messages of validation
     *
     * @return Vector errorMessageList
     */
    public List getErrorMessages() {
        return errorMessageList;
    }
    
    /**
     * Returns number of errors.
     * 
     * @return int number of errors.
     */
    public int count() {
        return size();
    }
    
    /**
     * Returns number of errors.
     * 
     * @return int number of errors.
     */
    public int size() {
        return errorMessageList.size();
    }
    
    /**
     * Returns a string array of all the contents of error messages.
     * 
     * @return a string array 
     */
    public String[] getFullMessages() {
        if (!failed()) return null;
        int size = size();
        String[] strary = new String[size];
        for (int i=0; i<size; i++) {
            strary[i] = ((Message)errorMessageList.get(i)).getContent();
        }
        return strary;
    }
    
    /**
     * Returns a string array of all the contents of error messages on a 
     * specific key (column).
     * 
     * @return a string array 
     */
    public String[] getFullMessagesOn(String key) {
        List errs = getErrorsOn(key);
        if (errs == null || errs.size() == 0) return null;
        
        int size = errs.size();
        String[] strary = new String[size];
        for (int i=0; i<size; i++) {
            strary[i] = ((Message)errs.get(i)).getContent();
        }
        return strary;
    }
    
    /**
     * Returns a list of errors on a specific key (column). Each item in the 
     * list is an ExpandedMessage instance.
     * 
     * @param key a key to an error, usually is a column name
     * @return list of errors
     */
    public List getErrorsOn(String key) {
        if (!failed() || key == null || "".equals(key)) return null;
        
        List errs = new ArrayList();
        int size = size();
        for (int i=0; i<size; i++) {
            Message msg = (Message)errorMessageList.get(i);
            if (key.equalsIgnoreCase(msg.getId())) errs.add(msg);
        }
        
        return errs;
    }
    
    /**
     * Checks if there are errors associated with a specific key.
     * 
     * @param key a key to an error, usually is a column name
     * @return if there are errors associated with the key.
     */
    public boolean hasErrorOn(String key) {
        if (!failed() || key == null || "".equals(key)) return false;
        
        boolean status = false;
        int size = size();
        for (int i=0; i<size; i++) {
            Message msg = (Message)errorMessageList.get(i);
            if (key.equalsIgnoreCase(msg.getId())) {
                status = true;
                break;
            }
        }
        
        return status;
    }
    
    /**
     * Returns a string representation of this object.
     * 
     * @return String
     */
    public String toString() {
        String newLineMark = "\n\r";
        StringBuffer sb = new StringBuffer();
        sb.append(errorMessageList.size());
        sb.append(" error(s).").append(newLineMark);
        Iterator it = errorMessageList.iterator();
        while(it.hasNext()) {
            sb.append(it.next()).append(newLineMark);
        }
        return sb.toString();
    }
    
    /**
     * Returns a xml representation of this object.
     * 
     * <pre>
     * Example:
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <errors>
     *        <error>Age must be within range of 0 and 5.</error>
     *        <error>Name must be unique.</error>
     *      </errors>
     * </pre>
     * 
     * @return String
     */
    public String toXML() {
        String newLine = "\r\n";
        String indent = "  ";//2 spaces
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(newLine);
        xml.append("<errors>").append(newLine);
        String[] messages = getFullMessages();
        if (messages != null) {
            int size = size();
            for (int i=0; i<size; i++) {
                xml.append(indent).append("<error>").append(messages[i]).append("</error>").append(newLine);
            }
        }
        xml.append("</errors>").append(newLine);
        return xml.toString();
    }

    private boolean bFailed = false;
    private List errorMessageList = new ArrayList();
}
