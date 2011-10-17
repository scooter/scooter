/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.common.logging.LogUtil;

/**
 * MyClassLoader class
 * 
 * @author (Fei) John Chen
 *
 */
public class MyClassLoader extends ClassLoader {
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    static private long key = 0L;
    private ClassManager caller;
    private String initiatingClassName;
    private ClassWork cwh;
    
    private Map<String, LoadedClass> loadedClasses = new HashMap<String, LoadedClass>();
    
    public MyClassLoader(ClassManager caller) {
        this(MyClassLoader.class.getClassLoader(), caller);
    }
    
    public MyClassLoader(ClassLoader parent, ClassManager caller) {
        super(parent);
        this.caller = caller;
        key = key + 1;
        cwh = new ClassWork(this);
    }
    
    public long getKey() {
        return key;
    }
    
    public Class<?> loadMyClass(String className) 
    throws ClassNotFoundException {
        initiatingClassName = className;
        return loadClass(className, true);
    }
    
    protected Class<?> loadClass(String className, boolean resolve) 
    throws ClassNotFoundException {
        Class<?> c = null;
        
        // See if type has already been loaded 
        LoadedClass loadedClass = (LoadedClass)loadedClasses.get(className);
        if (loadedClass == null) {
        	//record the newly loaded class
            if (!isAllowedToChange(className)) {
                c = super.loadClass(className, resolve);
                return c;
            }
            else {
                c = cwh.changeClass(className);
                if (c == null) c = super.loadClass(className, resolve);
                loadedClass = new LoadedClass(c, key);
                loadedClasses.put(className, loadedClass);
            }
        }
        else {
            c = loadedClass.clazz;
            resolve = false;
        }
        
        //check if the source file has been changed
        if (sourceChanged()) {
            loadedClasses.clear();
            caller.createNewClassLoader(className);
            c = caller.loadMyClass(initiatingClassName);
        }
        
        if (resolve) {
            resolveClass(c);
        }
        
        return c;
    }
    
    protected boolean sourceChanged() {
        boolean changed = false;
        if (loadedClasses == null || loadedClasses.size() == 0) return changed;
        
        for (Map.Entry<String, LoadedClass> entry : loadedClasses.entrySet()) {
            String className = entry.getKey();
            LoadedClass loadedClass = (LoadedClass)loadedClasses.get(className);
            
            SourceFile sourceFile = FileMonitor.getSourceFile(className);
            if (sourceFile != null && loadedClass != null && 
                loadedClass.loadedTime < sourceFile.getLastSourceModifiedTime()) {
                changed = true;
                break;
            }
        }
        
        return changed;
    }
    
    private boolean isAllowedToChange(String className) {
    	boolean check = false;
    	check = FileMonitor.isClassMonitored(className);
    	check = ClassWorkHelper.isAllowedClassName(className);
    	check = AutoLoaderConfig.getInstance().notAllowedToChange(className);
    	check = false;
		if (((FileMonitor.isClassMonitored(className) || 
				ClassWorkHelper.isAllowedClassName(className)) && 
				!AutoLoaderConfig.getInstance().notAllowedToChange(className)) ||
			(className.startsWith("com.scooterframework") && className.endsWith("Test")) || 
			(className.startsWith("com.scooterframework.test.model"))
			) {
			check = true;
		}
    	return check;
    }
}
