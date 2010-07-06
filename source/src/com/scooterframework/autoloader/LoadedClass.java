/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.util.Date;

/**
 * Loaded class contains information about the class loaded.
 * 
 * @author (Fei) John Chen
 *
 */
public class LoadedClass {
    Class clazz = null;
    long loaderKey;
    long loadedTime;
    
    public LoadedClass(Class clazz, long loaderKey) {
        this.clazz = clazz;
        this.loaderKey = loaderKey;
        loadedTime = (new Date()).getTime();
    }
    
    public long getLoaderKey() {
        return loaderKey;
    }
    
    public long getLoadedTime() {
        return loadedTime;
    }
    
    public void setLoadedTime(long loadedTime) {
        this.loadedTime = loadedTime;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("loaderKey=" + loaderKey).append(", ");
        sb.append("loadedTime=" + loadedTime).append(", ");
        sb.append("clazz=" + clazz);
        return sb.toString();
    }
}
