/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import com.scooterframework.orm.sqldataexpress.object.Function;

/**
 * FunctionProcessor class.
 * 
 * @author (Fei) John Chen
 */
public class FunctionProcessor extends StoredProcedureProcessor {    
    public FunctionProcessor(Function sp) {
        super(sp);
    }
}
