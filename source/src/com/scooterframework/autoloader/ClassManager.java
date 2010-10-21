/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import com.scooterframework.common.exception.ObjectCreationException;

/**
 * ClassManager class
 * 
 * @author (Fei) John Chen
 *
 */
public class ClassManager {
    
    private static ClassManager cm;
    private MyClassLoader xcl;
    
    static {
        cm = new ClassManager();
    }
    
    private ClassManager() {
        xcl = new MyClassLoader(this);
    }
    

    public static synchronized ClassManager getInstance() {
        return cm;
    }
    
    public Object newInstance(String className) {
        Object o = null;
        try {
            Class c = loadMyClass(className);
            o = c.newInstance();
        } catch (Exception ex) {
            throw new ObjectCreationException(className, ex);
        }
        return o;
    }
    
    public Class loadMyClass(String className) 
    throws ClassNotFoundException {
        return getMyClassLoader().loadMyClass(className);
    }
    
    public MyClassLoader getMyClassLoader() {
        return xcl;
    }
    
    public void createNewClassLoader(String className) {
        xcl = new MyClassLoader(this);
    }
}
