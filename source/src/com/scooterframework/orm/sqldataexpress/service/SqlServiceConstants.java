/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

/**
 * SqlServiceConstants class contains constants for SqlService. 
 * 
 * @author (Fei) John Chen
 */
public class SqlServiceConstants
{
    public static final String OFFSET = "OFFSET";
    public static final String LIMIT = "LIMIT";
    public static final String ORDER = "ORDER";
    public static final String SORT = "SORT";
    
    /**
     * Key to indicate columns to be excluded from output. The corresponding 
     * value in <tt>outputFilter</tt> map is a list of column names separated 
     * by comma.
     */
    public static final String OUTPUT_FILTER_EXCEPT = "SCOOTER.EXCEPT";
    
    /**
     * Key to indicate columns to be included in output. The corresponding 
     * value in <tt>outputFilter</tt> map is a list of column names separated 
     * by comma.
     */
    public static final String OUTPUT_FILTER_ONLY = "SCOOTER.ONLY";
}
