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
 * FileFilterSamePrefix filters files which have the same name prefix as the specified.
 * 
 * @author (Fei) John Chen
 */
public class FileFilterSamePrefix implements FileFilter, Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6245719143887044259L;
	
	private String filter;
    
    public FileFilterSamePrefix() {
        this.filter = "";
    }
    
    public FileFilterSamePrefix(String filter) {
        this.filter = filter;
    }
    
    public boolean accept(File file) {
        if ("".equals(filter) || filter == null) {
            return true;
        }
        return (file.getName().startsWith(filter));
    }
    
    public String toString() {
        return "sameprefix-" + filter;
    }
}
