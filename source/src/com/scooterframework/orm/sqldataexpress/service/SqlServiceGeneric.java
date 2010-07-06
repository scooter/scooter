/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.util.Collection;
import java.util.Map;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;

/**
 * SqlServiceGeneric interface specified services that are generic.
 * 
 * @author (Fei) John Chen
 */
public interface SqlServiceGeneric {
    /**
     * execute
     */
    public OmniDTO execute(Map inputs, String processorType, String processorName) throws BaseSQLException;
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(Collection inputParameters, String processorType, String processorName) throws BaseSQLException;
    
    /**
     * execute
     */
    public OmniDTO execute(Map inputs, String processorType, String processorName, Map outputFilters) throws BaseSQLException;
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(Collection inputParameters, String processorType, String processorName, Map outputFilters) throws BaseSQLException;
    
    /**
     * execute a collection of InputInfo with output filter
     */
    public Collection execute(Collection inputInfoList) throws BaseSQLException;
    
    /**
     * execute an InputInfo object in one transaction
     */
    public OmniDTO retrieveMasterDetails(InputInfo inputInfo) throws BaseSQLException;
}
