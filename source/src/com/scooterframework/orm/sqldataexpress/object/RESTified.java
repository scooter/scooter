/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.util.Map;

/**
 * <p>RESTfied defines methods for operating as a resource. </p>
 * 
 * <p>The most important feature of a RESTfied resource is the presence of an ID. 
 * That is, the resource can be identified by the id. </p>
 * 
 * <p>The ID is not necessarily a primary key field of a record. However, it is
 * always constructed from a primary key or a composite primary key. </p>
 * 
 * @author (Fei) John Chen
 */
public interface RESTified {
    /**
     * Returns the restified id of the resource. 
     * 
     * @return id String
     */
    public String getRestfulId();
    
    /**
     * Returns column names corresponding to the id. 
     * 
     * @return a string array
     */
    public String[] getRestfulIdConfig();
    
    /**
     * Returns the data map for the restified id. 
     * 
     * @return map of restified id data
     */
    public Map getRestfulIdMap();
    
    /**
     * <p>Sets the id value of the resource. The format of the id string must 
     * follow the pattern of the corresponding id config. If the id is backed 
     * by a composite primary key, dash must be used to link the value of each 
     * primary key field together. </p>
     * 
     * <pre>
     * Examples:
     *   id string          id config array         description
     *   ---------          ---------------         -------
     *     0001             [id]                    an order
     *     0001-99          [order_id, id]          an item of an order
     * 
     * </pre>
     * 
     * @param id
     */
    public void setRestfulId(String id);
}
