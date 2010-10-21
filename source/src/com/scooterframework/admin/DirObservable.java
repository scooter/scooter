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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import com.scooterframework.common.logging.LogUtil;

/**
 * DirObservable class is a subclass of <tt>Observable</tt>.
 * 
 * @author (Fei) John Chen
 */
public class DirObservable extends Observable {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    private long lastScannedTime = 0L;
    private String path;
    private File[] files;
    private HashMap fileTimestampMap = new HashMap();
    private FileFilter filter;
    
    public DirObservable(String path) {
        this(path, null);
    }
    
    public DirObservable(String path, FileFilter filter) {
        super();
        
        if (path == null) {
            throw new IllegalArgumentException("Input path is null.");
        }
        
        File dir = new File(path);
        
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Input path is not directory or does not exist: " + path);
        }
        
        this.path = path;
        this.filter = filter;
        files = dir.listFiles(filter);
        
        for(int i = 0; i < files.length; i++) {
           fileTimestampMap.put(files[i], new Long(files[i].lastModified()));
        }
        
        lastScannedTime = System.currentTimeMillis();
    }
    
    void checkChange() {
        HashSet checkedFiles = new HashSet();
        files = new File(path).listFiles(filter);
        
        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            checkedFiles.add(file);
            
            //skip copied file in windows XP and Vista
            if (file.getName().startsWith("Copy ") || file.getName().indexOf(" Copy") != -1) continue;
            
            Long current = (Long)fileTimestampMap.get(file);
            if (current == null) {
                fileTimestampMap.put(file, new Long(file.lastModified()));
                onChange(file, FileChangeNotice.ADD_FILE);
            }
            else if (current.longValue() != file.lastModified()) {
                fileTimestampMap.put(file, new Long(file.lastModified()));
                onChange(file, FileChangeNotice.MODIFY_FILE);
            }
        }
        
        Set ref = ((HashMap)fileTimestampMap.clone()).keySet();
        ref.removeAll((Set)checkedFiles);
        Iterator it = ref.iterator();
        while (it.hasNext()) {
            File deletedFile = (File)it.next();
            fileTimestampMap.remove(deletedFile);
            onChange(deletedFile, FileChangeNotice.DELETE_FILE);
        }
        
        lastScannedTime = System.currentTimeMillis();
    }
    
    void onChange(File file, String action) {
        super.setChanged();
        log.debug("File: " + file.getName() + ", action: " + action);
        notifyObservers(new FileChangeNotice(file, action));
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("path=" + path).append(", ");
        sb.append("lastScannedTime=" + lastScannedTime);
        return sb.toString();
    }
}
