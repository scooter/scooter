/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.util.Date;
import java.util.Map;

/**
 * OmniDTOStoredProcedure interface defines methods for results from 
 * stored procedures.
 * 
 * @author (Fei) John Chen
 */
public interface OmniDTOStoredProcedure extends OmniDTOBasic
{
    /**
     * returns returnCode
     */
    public String getReturnCode();
    
    /**
     * returns returnMessage
     */
    public String getReturnMessage();
    
    /**
     * returns newUpdateDate
     */
    public Date getNewUpdateDate();
    
    /**
     * The resultObjectMap is a Map which contains non-cursor result. 
     * 
     * returns resultObjectMap
     */
    public Map<String, Object> getResultObjectMap();
    
    /**
     * adds a (name,value) pair to resultMap
     */
    public void addNamedObject(String name, Object value);
}
