/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.validation;

import java.util.Iterator;
import java.util.List;

import com.scooterframework.common.exception.InvalidOperationException;
import com.scooterframework.common.util.Converters;

/**
 * 
 * <p>Validators contains basic validation methods to validate data. </p>
 * 
 * <p>
 * Validation can be done for inputs to a controller ({@link com.scooterframework.web.controller.ActionValidators}) 
 * or data in a model ({@link com.scooterframework.orm.activerecord.ModelValidators}).</p>
 * 
 * <p>When a <tt>validatesXXX</tt> method has no <tt>messageKey</tt> as input, 
 * a default constant will be used as the message key. This default constant 
 * is one of the built-in message keys in a message property file. When input 
 * parameters of a <tt>validatesXXX</tt> method contain <tt>messageKey</tt>, 
 * but no associated value of the <tt>messageKey</tt> is found in any message 
 * property files, the <tt>messageKey</tt> itself is treated as a customized  
 * validation message.</p>
 * 
 * Examples:
 * <pre>
 *     public void validatesRecord() {
 *         //return: message is too short (minimum is 20 characters).
 *         validators().validatesLengthMinimum("message", 20);
 *         
 *         //return: Damn! Your post is too short.
 *         validators().validatesLengthMinimum("body", 300, "Damn! Your post is too short.");
 *     }
 * </pre>
 * 
 * @author (Fei) John Chen
 */
abstract public class Validators {

    public static final String CONFIRMATION             = "validation.confirmation";
    public static final String ACCEPTED                 = "validation.accepted";
    public static final String INCLUSION                = "validation.inclusion";
    public static final String EXCLUSION                = "validation.exclusion";
    public static final String CANNOT_BE_BLANK          = "validation.cannot_be_blank";
    public static final String CANNOT_BE_NULL           = "validation.cannot_be_null";
    public static final String NOT_A_NUMBER             = "validation.not_a_number";
    public static final String EXCEEDING_MAXIMUM        = "validation.exceeding_maximum";
    public static final String IS_LESSTHAN_OR_EQUALTO   = "validation.less_than_or_equal_to";
    public static final String IS_LESSTHAN              = "validation.less_than";
    public static final String IS_LARGERTHAN_OR_EQUALTO = "validation.larger_than_or_equal_to";
    public static final String IS_LARGERTHAN            = "validation.larger_than";
    public static final String IS_EQUALTO               = "validation.equal_to";
    public static final String IS_WITHIN                = "validation.is_within";
    public static final String IS_INSIDE                = "validation.is_inside";
    public static final String IS_UNIQUE                = "validation.is_unique";
    public static final String IS_EMAIL                 = "validation.is_email";
    public static final String TOO_LONG                 = "validation.too_long";
    public static final String TOO_SHORT                = "validation.too_short";
    public static final String WRONG_LENGTH             = "validation.wrong_length";
    public static final String OUTOF_RANGE              = "validation.length_outof_range";
    
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9_-]([a-zA-Z0-9._%-])*@[a-zA-Z0-9._-]+\\.[a-zA-Z]{2,4}";
    
    /**
     * Validates the fields have non-blank values.
     * 
     * @param columnNames A string of column names separated by comma or blank
     */
    public void validatesPresenceOf(String columnNames) {
        validatesPresenceOf(columnNames, CANNOT_BE_BLANK);
    }
    
    /**
     * Validates the fields have non-blank values if check is true.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param check         a boolean variable to indicate whether to do the validation
     */
    public void validatesPresenceOf(String columnNames, boolean check) {
        if (check)
            validatesPresenceOf(columnNames, CANNOT_BE_BLANK);
    }
    
    /**
     * Validates the fields have non-blank values.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesPresenceOf(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData == null || "".equals(colData.toString().trim())) {
                getValidationResults().recordValidationException(colName, messageKey, colName);
            }
        }
    }
    
    /**
     * Validates the fields have non-blank values if check is true.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     * @param check         a boolean variable to indicate whether to do the validation
     */
    public void validatesPresenceOf(String columnNames, String messageKey, boolean check) {
        if (check)
            validatesPresenceOf(columnNames, messageKey);
    }
    
    /**
     * Validates the fields match their corresponding confirmation values.
     * 
     * A field's confirmation field name is "${field_name}_confirmation".
     * 
     * @param columnNames A string of column names separated by comma or blank
     */
    public void validatesConfirmationOf(String columnNames) {
        validatesConfirmationOf(columnNames, CONFIRMATION);
    }
    
    /**
     * Validates the fields match their corresponding confirmation values.
     * 
     * A field's confirmation field name is "${field_name}_confirmation".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param check         a boolean variable to indicate whether to do the validation
     */
    public void validatesConfirmationOf(String columnNames, boolean check) {
        if (check)
            validatesConfirmationOf(columnNames, CONFIRMATION);
    }
    
    /**
     * Validates the fields match their corresponding confirmation values.
     * 
     * A field's confirmation field name is "${field_name}_confirmation".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesConfirmationOf(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            Object confirmationdata = getData(colName + "_confirmation");
            if ((colData == null && confirmationdata != null) ||
                (colData != null && confirmationdata == null) ||
                (!colData.toString().equals(confirmationdata.toString()))
            ) {
                getValidationResults().recordValidationException(colName, messageKey, colName);
            }
        }
    }
    
    /**
     * Validates the fields match their corresponding confirmation values.
     * 
     * A field's confirmation field name is "${field_name}_confirmation".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     * @param check         a boolean variable to indicate whether to do the validation
     */
    public void validatesConfirmationOf(String columnNames, String messageKey, boolean check) {
        if (check)
            validatesConfirmationOf(columnNames, messageKey);
    }
    
    /**
     * Validates the fields match the accepted values.
     * 
     * This method is usually used for validating acceptance of service 
     * agreement checkbox. The default accepted value is "1".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     */
    public void validatesAcceptanceOf(String columnNames) {
        validatesAcceptanceOf(columnNames, ACCEPTED);
    }
    
    /**
     * Validates the fields match the accepted values.
     * 
     * This method is usually used for validating acceptance of service 
     * agreement checkbox. The default accepted value is "1".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesAcceptanceOf(String columnNames, String messageKey) {
        validatesAcceptanceOf(columnNames, "1", messageKey);
    }
    
    /**
     * Validates the fields match the accepted values.
     * 
     * This method is usually used for validating acceptance of service 
     * agreement checkbox. The default accepted value is "1".
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param accept        the acceptance value
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesAcceptanceOf(String columnNames, String accept, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                if (!colData.toString().equals(accept)) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
        }
    }
    
    /**
     * Validates each field matches one of the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesInclusionOf(gender, true, "Male, Female", "Please specify your gender.");//gender can be either "Male" or "Female" or null.
     *      validatesInclusionOf(status, "Open, Close", "validation.inclusion");//status must be either "Open" or "Close", not null.
     *      validatesInclusionOf(status, "Open, Close, null", "validation.inclusion");//status must be either "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param inclusionValues   the inclusion values separated by comma
     */
    public void validatesInclusionOf(String columnNames, String inclusionValues) {
        validatesInclusionOf(columnNames, false, inclusionValues);
    }
    
    /**
     * Validates each field matches one of the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesInclusionOf(gender, true, "Male, Female", "Please specify your gender.");//gender can be either "Male" or "Female" or null.
     *      validatesInclusionOf(status, "Open, Close", "validation.inclusion");//status must be either "Open" or "Close", not null.
     *      validatesInclusionOf(status, "Open, Close, null", "validation.inclusion");//status must be either "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param skipNull          if true skip the validation when the column value is null, default is false.
     * @param inclusionValues   the inclusion values separated by comma
     */
    public void validatesInclusionOf(String columnNames, boolean skipNull, String inclusionValues) {
        validatesInclusionOf(columnNames, skipNull, inclusionValues, INCLUSION);
    }
    
    /**
     * Validates each field matches one of the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesInclusionOf(gender, true, "Male, Female", "Please specify your gender.");//gender can be either "Male" or "Female" or null.
     *      validatesInclusionOf(status, "Open, Close", "validation.inclusion");//status must be either "Open" or "Close", not null.
     *      validatesInclusionOf(status, "Open, Close, null", "validation.inclusion");//status must be either "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param skipNull          if true skip the validation when the column value is null, default is false.
     * @param inclusionValues   the inclusion values separated by comma
     * @param messageKey        key to a message in message resource files or a message
     */
    public void validatesInclusionOf(String columnNames, boolean skipNull, String inclusionValues, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        List values = Converters.convertStringToList(inclusionValues);
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                if (!values.contains(colData)) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
            else if (colData == null && !skipNull) {
                if (!values.contains("null")) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
        }
    }
    
    /**
     * Validates each field excludes from the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesExclusionOf(gender, true, "Female", "No female please.");//gender can be either "Male" or null.
     *      validatesExclusionOf(status, "Open, Close", "validation.exclusion");//status cannot be "Open" or "Close".
     *      validatesExclusionOf(status, "Open, Close, null", "validation.exclusion");//status cannot be "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param exclusionValues   the exclusion values separated by comma
     */
    public void validatesExclusionOf(String columnNames, String exclusionValues) {
        validatesExclusionOf(columnNames, false, exclusionValues, EXCLUSION);
    }
    
    /**
     * Validates each field excludes from the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesExclusionOf(gender, true, "Female", "No female please.");//gender can be either "Male" or null.
     *      validatesExclusionOf(status, "Open, Close", "validation.exclusion");//status cannot be "Open" or "Close".
     *      validatesExclusionOf(status, "Open, Close, null", "validation.exclusion");//status cannot be "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param skipNull          if true skip the validation when the column value is null, default is false.
     * @param exclusionValues   the exclusion values separated by comma
     */
    public void validatesExclusionOf(String columnNames, boolean skipNull, String exclusionValues) {
        validatesExclusionOf(columnNames, skipNull, exclusionValues, EXCLUSION);
    }
    
    /**
     * Validates each field excludes from the specified values.
     * 
     * <pre>
     * Examples:
     *      validatesExclusionOf(gender, true, "Female", "No female please.");//gender can be either "Male" or null.
     *      validatesExclusionOf(status, "Open, Close", "validation.exclusion");//status cannot be "Open" or "Close".
     *      validatesExclusionOf(status, "Open, Close, null", "validation.exclusion");//status cannot be "Open" or "Close" or null.
     * </pre>
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param skipNull          if true skip the validation when the column value is null, default is false.
     * @param exclusionValues   the exclusion values separated by comma
     * @param messageKey        key to a message in message resource files or a message
     */
    public void validatesExclusionOf(String columnNames, boolean skipNull, String exclusionValues, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        List values = Converters.convertStringToList(exclusionValues);
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                if (values.contains(colData)) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
            else if (colData == null && !skipNull) {
                if (values.contains("null")) {
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is not null.
     * 
     * @param columnNames A string of column names separated by comma or blank
     */
    public void validatesNotNull(String columnNames) {
        validatesNotNull(columnNames, CANNOT_BE_NULL);
    }
    
    /**
     * Validates the field data is not null.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNotNull(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData == null) {
                getValidationResults().recordValidationException(colName, messageKey, colName);
            }
        }
    }
    
    /**
     * Validates the field data contains email only.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     */
    public void validatesIsEmail(String columnNames) {
        validatesIsEmail(columnNames, IS_EMAIL);
    }
    
    /**
     * Validates the field data contains email only.
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param messageKey        key to a message in message resource files or a message
     */
    public void validatesIsEmail(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                if (!colData.toString().matches(EMAIL_PATTERN)) {
                    getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, colData});
                }
            }
        }
    }
    
    /**
     * Validates the field data contains number only.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     */
    public void validatesIsNumber(String columnNames) {
        validatesIsNumber(columnNames, NOT_A_NUMBER);
    }
    
    /**
     * Validates the field data contains number only.
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param messageKey        key to a message in message resource files or a message
     */
    public void validatesIsNumber(String columnNames, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    new Double(colData.toString());
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, messageKey, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data does not exceed maximum.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param max           a maximum number
     */
    public void validatesNumberMaximum(String columnNames, Number max) {
        validatesNumberMaximum(columnNames, max, EXCEEDING_MAXIMUM);
    }
    
    /**
     * Validates the field data does not exceed maximum.
     * 
     * @param columnNames       a string of column names separated by comma or blank
     * @param max               a maximum number
     * @param messageKey        key to a message in message resource files or a message
     */
    public void validatesNumberMaximum(String columnNames, Number max, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || max == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m = max.doubleValue();
                    if (d.doubleValue() > m) getValidationResults().recordValidationException(colName, messageKey, colName, max);
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is lower than or equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     */
    public void validatesNumberIsLowerThanOrEqualTo(String columnNames, Number number) {
        validatesNumberIsLowerThanOrEqualTo(columnNames, number, IS_LESSTHAN_OR_EQUALTO);
    }
    
    /**
     * Validates the field data is lower than or equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsLowerThanOrEqualTo(String columnNames, Number number, String messageKey) {
        validatesNumberMaximum(columnNames, number, messageKey);
    }
    
    /**
     * Validates the field data is lower than a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     */
    public void validatesNumberIsLowerThan(String columnNames, Number number) {
        validatesNumberIsLowerThan(columnNames, number, IS_LESSTHAN);
    }
    
    /**
     * Validates the field data is lower than a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsLowerThan(String columnNames, Number number, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m = number.doubleValue();
                    if (d.doubleValue() >= m) getValidationResults().recordValidationException(colName, messageKey, colName, number);
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is larger than or equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     */
    public void validatesNumberIsLargerThanOrEqualTo(String columnNames, Number number) {
        validatesNumberIsLargerThanOrEqualTo(columnNames, number, IS_LARGERTHAN_OR_EQUALTO);
    }
    
    /**
     * Validates the field data is larger than or equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsLargerThanOrEqualTo(String columnNames, Number number, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m = number.doubleValue();
                    if (d.doubleValue() < m) getValidationResults().recordValidationException(colName, messageKey, colName, number);
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is larger than a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     */
    public void validatesNumberIsLargerThan(String columnNames, Number number) {
        validatesNumberIsLargerThan(columnNames, number, IS_LARGERTHAN);
    }
    
    /**
     * Validates the field data is larger than a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsLargerThan(String columnNames, Number number, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m = number.doubleValue();
                    if (d.doubleValue() <= m) getValidationResults().recordValidationException(colName, messageKey, colName, number);
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     */
    public void validatesNumberIsEqualTo(String columnNames, Number number) {
        validatesNumberIsEqualTo(columnNames, number, IS_EQUALTO);
    }
    
    /**
     * Validates the field data is equal to a specific number.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number        a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsEqualTo(String columnNames, Number number, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m = number.doubleValue();
                    if (d.doubleValue() != m) getValidationResults().recordValidationException(colName, messageKey, colName, number);
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is within a specific range of [number1, number2].
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number1       a specific number
     * @param number2       a specific number
     */
    public void validatesNumberIsWithinRangeOf(String columnNames, Number number1, Number number2) {
        validatesNumberIsWithinRangeOf(columnNames, number1, number2, IS_WITHIN);
    }
    
    /**
     * Validates the field data is within a specific range of [number1, number2].
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number1       a specific number
     * @param number2       a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsWithinRangeOf(String columnNames, Number number1, Number number2, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number1 == null || number2 == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m1 = number1.doubleValue();
                    double m2 = number2.doubleValue();
                    if (d.doubleValue() < m1 || d.doubleValue() > m2) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, number1, number2});
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the field data is in a specific range of (number1, number2).
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number1       a specific number
     * @param number2       a specific number
     */
    public void validatesNumberIsInsideRangeOf(String columnNames, Number number1, Number number2) {
        validatesNumberIsInsideRangeOf(columnNames, number1, number2, IS_INSIDE);
    }
    
    /**
     * Validates the field data is in a specific range of (number1, number2).
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param number1       a specific number
     * @param number2       a specific number
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesNumberIsInsideRangeOf(String columnNames, Number number1, Number number2, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null || number1 == null || number2 == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                try{
                    Double d = new Double(colData.toString());
                    double m1 = number1.doubleValue();
                    double m2 = number2.doubleValue();
                    if (d.doubleValue() <= m1 || d.doubleValue() >= m2) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, number1, number2});
                }
                catch(NumberFormatException ex){
                    getValidationResults().recordValidationException(colName, NOT_A_NUMBER, colName);
                }
            }
        }
    }
    
    /**
     * Validates the maximum length of a field.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param maximum       the maximum length
     */
    public void validatesLengthMaximum(String columnNames, int maximum) {
        validatesLengthMaximum(columnNames, maximum, TOO_LONG);
    }
    
    /**
     * Validates the maximum length of a field.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param maximum       the maximum length
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesLengthMaximum(String columnNames, int maximum, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                int len = colData.toString().length();
                if (len > maximum) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, new Integer(maximum)});
            }
        }
    }
    
    /**
     * Validates the minimum length of a field.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param minimum       the minimum length
     */
    public void validatesLengthMinimum(String columnNames, int minimum) {
        validatesLengthMinimum(columnNames, minimum, TOO_SHORT);
    }
    
    /**
     * Validates the minimum length of a field.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param minimum       the minimum length
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesLengthMinimum(String columnNames, int minimum, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                int len = colData.toString().length();
                if (len < minimum) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, new Integer(minimum)});
            }
        }
    }
    
    /**
     * Validates the field length is in a specific range.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param length1       the minimum length
     * @param length2       the maximum length
     */
    public void validatesLengthInRangeOf(String columnNames, int length1, int length2) {
        validatesLengthInRangeOf(columnNames, length1, length2, OUTOF_RANGE);
    }
    
    /**
     * Validates the field length is in a specific range.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param length1       the minimum length
     * @param length2       the maximum length
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesLengthInRangeOf(String columnNames, int length1, int length2, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                int len = colData.toString().length();
                if (len < length1 || len > length2) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, new Integer(length1), new Integer(length2)});
            }
        }
    }
    
    /**
     * Validates the field length is as specfied.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param length        the correct length
     */
    public void validatesLengthOf(String columnNames, int length) {
        validatesLengthOf(columnNames, length, WRONG_LENGTH);
    }
    
    /**
     * Validates the field length is as specfied.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param length        the correct length
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesLengthOf(String columnNames, int length, String messageKey) {
        List colNames = Converters.convertStringToList(columnNames);
        if (colNames == null) return;
        Iterator it = colNames.iterator();
        while(it.hasNext()) {
            String colName = (String)it.next();
            Object colData = getData(colName);
            if (colData != null) {
                int len = colData.toString().length();
                if (len != length) getValidationResults().recordValidationException(colName, messageKey, new Object[]{colName, new Integer(length)});
            }
        }
    }
    
    /**
     * Validates each field data is unique.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     */
    public void validatesUniqenessOf(String columnNames) {
        validatesUniqenessOf(columnNames, IS_UNIQUE);
    }
    
    /**
     * Validates each field data is unique.
     * 
     * @param columnNames   a string of column names separated by comma or blank
     * @param messageKey    key to a message in message resource files or a message
     */
    public void validatesUniqenessOf(String columnNames, String messageKey) {
        throw new InvalidOperationException("This method must be implemented by a subclass of Validators class.");
    }
    
    /**
     * Returns validation result.
     * 
     * @return validation result
     */
    abstract protected ValidationResults getValidationResults();
    
    /**
     * Returns value for the specific <tt>fieldName</tt>. See implementing 
     * classes for details of this method.
     * 
     * @param fieldName
     * @return value for the field
     */
    abstract protected Object getData(String fieldName);
}
