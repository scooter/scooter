/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import com.scooterframework.common.exception.ObjectCreationException;

/**
 * ObjectFactory class has helper methods for object creation. 
 * 
 * @author (Fei) John Chen
 */
public class ObjectFactory
{
	private static ObjectFactory me;
	
	static {
		me = new ObjectFactory();
    }
    
    protected ObjectFactory() {
    }
	
    public static ObjectFactory getFactory() {
        return me;
    }
    
    public Class loadClass(String className) 
    throws ClassNotFoundException {
        Class c = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        c = classLoader.loadClass(className);
        return c;
    }
    
    public Object newInstance(Class clz) {
        return newInstance(clz.getName());
    }
    
    public Object newInstance(String className) {
        Object o = null;
        try {
            Class c = loadClass(className);
            o = c.newInstance();
        } catch (Exception ex) {
            throw new ObjectCreationException(className, ex);
        }
        return o;
    }
    
    public Object newInstance(Class clz, Class[] parameterTypes, Object[] initargs) {
    	return newInstance(clz.getName(), parameterTypes, initargs);
    }
    
    public Object newInstance(String className, Class[] parameterTypes, Object[] initargs) {
        Object o = null;
        try {
            Class c = loadClass(className);
            o = c.getConstructor(parameterTypes).newInstance(initargs);
        } catch (Exception ex) {
            throw new ObjectCreationException(className, ex);
        }
        return o;
    }
    
    public Object execute(String className, String methodName, Object[] args) {
        return BeanUtil.execute(newInstance(className), methodName, args);
    }
}
