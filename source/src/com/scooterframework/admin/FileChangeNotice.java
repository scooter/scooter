/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.io.Serializable;

/**
 * FileChangeNotice contains information on what type of change on a file.
 * 
 * @author (Fei) John Chen
 */
public class FileChangeNotice implements Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6447261722840787823L;
	
	public static final String ADD_FILE = "add";
    public static final String MODIFY_FILE = "modify";
    public static final String DELETE_FILE = "delete";

    private File file;
    private String action;
    
	public FileChangeNotice(File file, String action) {
        this.file = file;
        this.action = action;
    }
    
    public File getFile() {
        return file;
    }
    
    public String getAction() {
        return action;
    }
}
