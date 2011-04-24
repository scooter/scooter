/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.common.exception.ExecutionException;
import com.scooterframework.common.exception.MethodCreationException;

/**
 * <tt>BeanUtil</tt> class has helper methods on beans.
 * 
 * @author (Fei) John Chen
 */
public class BeanUtil {
	private static Map<String, Method> allMethods = new ConcurrentHashMap<String, Method>();

    /**
     * Returns method of an object.
     * 
     * @param clz the class type
     * @param methodName the method name of the object
     * @return the method object
     * @exception MethodCreationException if <tt>bean</tt> or
     *  <tt>method</tt> is null
     */
    public static Method getMethod(Class<?> clz, String methodName) {
        if (clz == null) {
            throw new IllegalArgumentException("BeanUtil.getMethod(): No bean class specified");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("BeanUtil.getMethod(): No method name specified");
        }
        
        Method method = null;
        String methodKey = clz.getName() + "." + methodName.toLowerCase();
        if (ApplicationConfig.getInstance().isInDevelopmentEnvironment()) {
        	method = (Method)CurrentThreadCache.get(methodKey);
        	if (method != null) {
        		return method;
        	}
        	else {
        		Method[] methods = clz.getMethods();
        		for (Method m : methods) {
        			if (methodName.equalsIgnoreCase(m.getName())) {
        				method = m;
        				CurrentThreadCache.set(methodKey, method);
        				break;
        			}
        		}
        	}
        	
        	if (method == null) {
        		throw new MethodCreationException(clz.getName(), methodName);
        	}
        	else {
        		return method;
        	}
        }
        
        method = (Method)allMethods.get(methodKey);
        if (method != null) {
        	return method;
        }
        else {
    		Method[] methods = clz.getMethods();
    		for (Method m : methods) {
    			if (methodName.equalsIgnoreCase(m.getName())) {
    				method = m;
    				allMethods.put(methodKey, method);
    				break;
    			}
    		}
    		
    		if (method == null) {
        		throw new MethodCreationException(clz.getName(), methodName);
        	}
        }
        
        return method;
    }
    
    /**
     * Executes a method of a bean instance.
     * 
     * @param instance      the bean instance
     * @param methodName    the method to be executed
     * @param args          input parameters for the method
     * @return return value of the method
     */
    public static Object execute(Object instance, String methodName, Object[] args) {
        if (instance == null) {
            throw new IllegalArgumentException("Input bean instance is null.");
        }
        
        Object result = null;
        try {
            Method m = getMethod(instance.getClass(), methodName);
            result = m.invoke(instance, args);
        }
        catch(Exception ex) {
            throw new ExecutionException(instance.getClass().getName(), methodName, args, ex);
        }
        return result;
    }
    
    /**
     * <p>Gets property of a bean for a <tt>property</tt>. </p>
     * 
     * <pre>
     * Examples:
     *     getBeanProperty(pet, "name") -> pet.getName()
     *     getBeanProperty(pet, "owner.first_name") -> pet.getOwner().getFirstName()
     * </pre>
     * 
     * @param bean        the bean instance
     * @param property    the property name
     * @return value of the property
     */
    public static Object getBeanProperty(Object bean, String property) {
        if (property.indexOf('.') == -1) 
            return _getBeanPropertyData(bean, property);
        
        StringTokenizer st = new StringTokenizer(property, " .");
        Object tmp = bean;
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            tmp = _getBeanPropertyData(tmp, token);
        }
        
        return tmp;
    }
    
    private static Object _getBeanPropertyData(Object bean, String property) {
        String methodName = "get" + WordUtil.camelize(property);
        return execute(bean, methodName, null);
    }
}
