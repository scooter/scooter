/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;


/**
 * OmniDTOJdbcStatement interface defines methods for results from 
 * jdbc statement processing.
 * 
 * @author (Fei) John Chen
 */
public interface OmniDTOJdbcStatement extends OmniDTOBasic
{
    /**
     * returns updatedRowCount
     */
    public int getUpdatedRowCount();
    
    /**
     * sets updatedRowCount
     */
    public void setUpdatedRowCount(int updatedRowCount);
    
    /**
     * returns generated key value as long.
     * 
     * Note: Only one primary key column is allowed to be auto generated.
     * 
     * return -1 if the underlying database does not support generatedKeys 
     * feature or if the sql statement is not a ddl statement.
     */
    public long getGeneratedKey();
    
    /**
     * sets getGeneratedKey
     */
    public void setGeneratedKey(long generatedKey);
}
