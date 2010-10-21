/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

/**
 * InputParameter class.
 * 
 * @author (Fei) John Chen
 */
public class InputParameter {
    public InputParameter() {}
    
    public InputParameter(String name, Object value) {        
        if ( name == null ) throw new IllegalArgumentException("Name can not be null.");
        
        this.name = name.toUpperCase();
        this.value = value;
    }

    /**
     * returns name
     */
    public String getName() {
        return name;
    }
    
    /**
     * sets name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns value
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * sets value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    private String name = null;
    private Object value = null;
}
