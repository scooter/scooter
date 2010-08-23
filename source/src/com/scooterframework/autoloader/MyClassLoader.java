/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    private ClassWorkHelper cwh;
    
    private Map loadedClasses = Collections.synchronizedMap(new HashMap());
    
    public MyClassLoader(ClassManager caller) {
        this(MyClassLoader.class.getClassLoader(), caller);
    }
    
    public MyClassLoader(ClassLoader parent, ClassManager caller) {
        super(parent);
        this.caller = caller;
        key = key + 1;
        cwh = new ClassWorkHelper(this);
    }
    
    public long getKey() {
        return key;
    }
    
    public synchronized Class loadMyClass(String className) 
    throws ClassNotFoundException {
        initiatingClassName = className;
        return loadClass(className, true);
    }
    
    protected Class loadClass(String className, boolean resolve) 
    throws ClassNotFoundException {
        Class c = null;
        
        // See if type has already been loaded 
        LoadedClass loadedClass = (LoadedClass)loadedClasses.get(className);
        if (loadedClass == null) {
        	//record the newly loaded class
            if (!isAllowedToChange(className)) {
                c = super.loadClass(className, resolve);
                return c;
            }
            else {
                //c = findClass(className);
                c = cwh.changeClass(className);
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
    
    protected Class findClass(String className)
        throws ClassNotFoundException {
    	
        byte[] classData = getClassBytes2(className);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        
        // Parse it
        return defineClass(className, classData, 0, classData.length);
    }
    
    protected byte[] getClassBytes2(String className) 
    throws ClassNotFoundException {
        byte[] result = null;
    	try {
            SourceFile sourceFile = FileMonitor.getSourceFile(className);
            //check if the class file exists
            if (sourceFile.getLastClassModifiedTime() == 0) {
                String error = sourceFile.recompile();
                if (error != null) {
                    log.error("Compile error: " + error);
                    throw new CompileException(error);
                }
            }
            FileInputStream fi = new FileInputStream(sourceFile.getClassFilePath());
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n = 0;
            while((n = fi.read(b)) != -1) {
                out.write(b, 0, n);
            }
            fi.close();
            out.close();
            result = out.toByteArray();
    	} catch (Exception ex) {
    	    throw new ClassNotFoundException("Failed to find class " + className);
    	}
        return result;
    }
    
    protected boolean sourceChanged() {
        boolean changed = false;
        if (loadedClasses == null || loadedClasses.size() == 0) return changed;
        
        Iterator it = loadedClasses.keySet().iterator();
        while(it.hasNext()) {
            String className = (String)it.next();
            LoadedClass loadedClass = (LoadedClass)loadedClasses.get(className);
            
            SourceFile sourceFile = FileMonitor.getSourceFile(className);
            if (sourceFile != null && 
                loadedClass.loadedTime < sourceFile.getLastSourceModifiedTime()) {
                changed = true;
                break;
            }
        }
        
        return changed;
    }
    
    private boolean isAllowedToChange(String className) {
    	boolean check = true;
		if ((!FileMonitor.isClassMonitored(className) || 
				AutoLoaderConfig.getInstance().notAllowedToChange(className))
				&& !className.startsWith("com.scooterframework.test") || 
				className.endsWith("FrameworkSupport")) {
			check = false;
		}
    	return check;
    }
}
