/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;
import java.io.IOException;

import com.scooterframework.common.logging.LogUtil;

/**
 * SourceFile contains information on source file and its corresponding class file.
 * 
 * @author (Fei) John Chen
 */
public class SourceFile {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    private File sourceFile;
    private String sourceDirPath;
    private String sourceFilePath = "";
    private long lastSourceModifiedTime = 0L;
    
    private File classFile = null;
    private String classFilePath = "";
    
    public SourceFile(File file, String sourceDirPath) {
        init(file, sourceDirPath);
    }
    
    private void init(File file, String sourceDirPath) {
        if (file == null) {
            throw new IllegalArgumentException("Input file is null.");
        }
        else if (!file.isFile()) {
            throw new IllegalArgumentException("Input file is not file or does not exist: " + file);
        }
        
        this.sourceFile = file;
        this.sourceDirPath = sourceDirPath;
        
        try {
			sourceFilePath = sourceFile.getCanonicalPath();
		} catch (IOException ex) {
			log.error("Error in init(): " + ex.getMessage());
			sourceFilePath = sourceFile.getAbsolutePath();
		}
        lastSourceModifiedTime = sourceFile.lastModified();
        
        classFilePath = SourceFileHelper.getClassFilePath(sourceFile, sourceDirPath);
        classFile = new File(classFilePath);
    }
    
    public boolean isUpdated(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        
        try {
        	String s = file.getCanonicalPath();
        	if (!sourceFilePath.equals(s)) return false;
        } catch (Exception ex) {}
        
        boolean updated = false;
        
        if (lastSourceModifiedTime < file.lastModified()) {
            updated = true;
            init(file, sourceDirPath);
        }
        
        return updated;
    }
    
    public File getSource() {
        return sourceFile;
    }
    
    public boolean availableForRecompile() {
        return (lastSourceModifiedTime > classFile.lastModified() || classFile.lastModified() == 0L)?true:false;
    }
    
    public String getClassFilePath() {
        return classFilePath;
    }
    
    public long getLastSourceModifiedTime() {
        return lastSourceModifiedTime;
    }
    
    public long getLastClassModifiedTime() {
        return classFile.lastModified();
    }
    
    public File getClassFile() {
    	return classFile;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("sourceFile=" + sourceFile).append(", ");
        sb.append("lastSourceModifiedTime=" + lastSourceModifiedTime).append(", ");
        sb.append("classFile=" + classFile).append(", ");
        sb.append("lastClassModifiedTime=" + classFile.lastModified());
        return sb.toString();
    }
}
