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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.scooterframework.admin.AutoLoadedObjectFactory;
import com.scooterframework.common.logging.LogUtil;

/**
 * ClassWorkHelper class has help methods related to class transformation work.
 * 
 * @author (Fei) John Chen
 */
public class ClassWorkHelper {
    private static LogUtil log = LogUtil.getLogger(ClassWorkHelper.class.getName());
    
    private static Collection allowedClassNames = new ArrayList();
    
    public static void preloadClasses(Collection classNames) throws Exception {
    	if (classNames == null) return;
    	allowedClassNames = classNames;
    	Iterator it = classNames.iterator();
    	while(it.hasNext()) {
    		String className = (String)it.next();
    		AutoLoadedObjectFactory.getInstance().loadClass(className);
    	}
    	allowedClassNames.clear();
    }
    
    public static boolean isAllowedClassName(String className) {
    	return allowedClassNames.contains(className);
    }
    
    public static void preloadClasses(String codeDirSet) throws Exception {
    	preloadClasses(codeDirSet, "");
    }
    
    public static void preloadClasses(String codeDirSet, String pkgPrefix) throws Exception {
    	StringTokenizer st = new StringTokenizer(codeDirSet, File.pathSeparator);
    	while(st.hasMoreTokens()) {
    		String codeDir = st.nextToken();
    		_preloadClasses(codeDir, pkgPrefix);
    	}
    }
    
    private static void _preloadClasses(String codeDir, String pkgPrefix) throws Exception {
    	if (codeDir == null) {
    		log.error("Error in precompileClasses(): codeDir is null.");
    	}
    	
		if (pkgPrefix == null) pkgPrefix = "";
    	
    	File classDir = new File(codeDir);
    	Set classNames = new HashSet();
    	getClassNamesSet(codeDir, classNames);
    	Iterator it = classNames.iterator();
    	while(it.hasNext()) {
    		String classFileName = (String)it.next();
    		String className = classFileName.substring(classDir.getCanonicalPath().length());
    		if (className.startsWith(File.separator)) {
    			className = className.substring(1);
    		}
    		if (className.endsWith(".class")) {
    			className = className.substring(0, className.length() - 6);
    		}
    		else if (className.endsWith(".java")) {
    			className = className.substring(0, className.length() - 5);
    		}
    		else {
    			continue;
    		}
    		className = className.replace(File.separatorChar, '.');
    		if (className.startsWith(pkgPrefix) || "".equals(pkgPrefix)) {
    			allowedClassNames.add(className);
    		}
    	}
    	preloadClasses(allowedClassNames);
    }
    
    private static void getClassNamesSet(String dirPath, Set items) throws IOException {
    	File dir = new File(dirPath);
    	File[] files = dir.listFiles();
    	if (files == null) return;
    	for (int i = 0; i < files.length; i++) {
    		File f = files[i];
    		String fullName = f.getCanonicalPath();
    		if (f.isDirectory()) {
    			getClassNamesSet(fullName, items);
    		}
    		else {
    			if (fullName.endsWith(".java") || fullName.endsWith(".class")) {
    				items.add(fullName);
    			}
    		}
    	}
    }
}
