/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

/**
 * OmniDTOStoredFunction interface defines methods for results from 
 * stored functions.
 * 
 * @author (Fei) John Chen
 */
public interface OmniDTOStoredFunction extends OmniDTOStoredProcedure
{
    /**
     * returns result from database function call. 
     * 
     * returns Object result from the database function call
     */
    public Object getFunctionCallResult();
}
