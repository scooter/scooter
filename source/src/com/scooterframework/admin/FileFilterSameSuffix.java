/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * FileFilterSameSuffix filters files which have the same name suffix as the specified.
 * 
 * @author (Fei) John Chen
 */
public class FileFilterSameSuffix implements FileFilter, Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5275090534529307258L;
	
	private String filter;
    
    public FileFilterSameSuffix() {
        this.filter = "";
    }
    
    public FileFilterSameSuffix(String filter) {
        this.filter = filter;
    }
    
    public boolean accept(File file) {
        if ("".equals(filter) || filter == null) {
            return true;
        }
        return (file.getName().endsWith(filter));
    }
    
    public String toString() {
        return "samesuffix_" + filter;
    }
}
