/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

/**
 * ParameterFactory class creates Parameter instances of different vendor types. 
 * 
 * @author (Fei) John Chen
 */
public class ParameterFactory {
    private static final ParameterFactory me = new ParameterFactory();

    private ParameterFactory() {}
    

    public static ParameterFactory getInstance() {
        return me;
    }
    
    public Parameter createParameter(String vendor, int index, String name, String mode, int sqlDataType, String sqlDataTypeName) {
        Parameter p = null;
        
        if ("ORACLE".equalsIgnoreCase(vendor)) {
            p = new OracleParameter(index, name, mode, sqlDataType, sqlDataTypeName);
        }
        else {
            p = new Parameter(index, name, mode, sqlDataType, sqlDataTypeName);
        }
        
        p.setVendor(vendor);
        
        return p;
    }
}
