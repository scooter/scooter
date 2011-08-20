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
 * FileFilterSameName filters files which have the same name as the specified.
 * 
 * @author (Fei) John Chen
 */
public class FileFilterSameName implements FileFilter, Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1084583110706270183L;
	
	private String filter;
    
    public FileFilterSameName() {
        this.filter = "";
    }
    
    public FileFilterSameName(String filter) {
        this.filter = filter;
    }
    
    public boolean accept(File file) {
        if ("".equals(filter) || filter == null) {
            return true;
        }
        return (file.getName().equals(filter));
    }
    
    public String toString() {
        return "samename_" + filter;
    }
}
