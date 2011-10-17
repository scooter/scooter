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
    
    private MyClassLoader xcl;
    
    private ClassManager() {
        xcl = new MyClassLoader(this);
    }
    
    private static class SingletonHolder { 
        public static final ClassManager instance = new ClassManager();
    }

    public static ClassManager getInstance() {
        return SingletonHolder.instance;
    }
    
    public Object newInstance(String className) {
        Object o = null;
        try {
            Class<?> c = loadMyClass(className);
            o = c.newInstance();
        } catch (Exception ex) {
            throw new ObjectCreationException(className, ex);
        }
        return o;
    }
    
    public Class<?> loadMyClass(String className) 
    throws ClassNotFoundException {
        return getMyClassLoader().loadMyClass(className);
    }
    
    public MyClassLoader getMyClassLoader() {
        return xcl;
    }
    
    void createNewClassLoader(String className) {
        xcl = new MyClassLoader(this);
    }
}
