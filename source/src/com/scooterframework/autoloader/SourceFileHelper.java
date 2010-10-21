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
import java.util.StringTokenizer;

import com.scooterframework.common.logging.LogUtil;

/**
 * SourceFileHelper contains helper methods on source file and its 
 * corresponding class file.
 * 
 * @author (Fei) John Chen
 */
public class SourceFileHelper {
    private static LogUtil log = LogUtil.getLogger(SourceFileHelper.class.getName());
    
    public static SourceFile getSourceFileFromClassName(String className) {
        if (className == null || className.length() == 0) {
        	throw new IllegalArgumentException("className cannot be null.");
        }
        
        SourceFile sf = null;
        
        String sourceName = className;
        if (className.endsWith(".class")) {
            sourceName = className.substring(0, className.indexOf(".class"));
        }
        sourceName = sourceName.replace('.', File.separatorChar);

        boolean foundSourceFile = false;
        String sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        StringTokenizer st = new StringTokenizer(sourcePath, File.pathSeparator);
        while(st.hasMoreTokens()) {
        	String sourceDirPath = st.nextToken();
            if (sourceDirPath.endsWith(File.separator)) {
                sourceName = sourceDirPath + sourceName;
            }
            else {
                sourceName = sourceDirPath + File.separator + sourceName;
            }
            sourceName = sourceName + ".java";
            File sourceFile = new File(sourceName);
            if (sourceFile.exists()) {
            	foundSourceFile = true;
            	sf = new SourceFile(sourceFile, sourceDirPath);
            	break;
            }
        }
        
        if (!foundSourceFile) {
        	throw new IllegalArgumentException("There is no source file for class " + className);
        }
        return sf;
    }
    
    public static String getClassNameFromSourceFile(File javaSourceFile, String sourceDirPath) {
    	String javaSourceFilePath = "";
    	try {
			javaSourceFilePath = javaSourceFile.getCanonicalPath();
		} catch (IOException ex) {
			log.error("Error in getClassNameFromSourceFile(): " + ex.getMessage());
		}
        String relativeJavaFileName = getRelativeFileName(javaSourceFilePath, sourceDirPath);
        String classFileName = convertExtensionFromJavaToClass(relativeJavaFileName);
        
        if (classFileName.indexOf(".class") != -1) {
            classFileName = classFileName.substring(0, classFileName.indexOf(".class"));
        }
        String fullClassName = classFileName;
        if (classFileName.indexOf(File.separatorChar) != -1) {
            fullClassName = classFileName.replace(File.separatorChar, '.');
        }
        return fullClassName;
    }
    
    public static String getClassNameFromClassFile(File javaClassFile) {
    	String javaClassFilePath = "";
    	try {
			javaClassFilePath = javaClassFile.getCanonicalPath();
		} catch (IOException ex) {
			log.error("Error in getClassNameFromClassFile(): " + ex.getMessage());
		}
		String classDirPath = AutoLoaderConfig.getInstance().getOutputClassLocation();
        String classFileName = getRelativeFileName(javaClassFilePath, classDirPath);
        
        if (classFileName.indexOf(".class") != -1) {
            classFileName = classFileName.substring(0, classFileName.indexOf(".class"));
        }
        String fullClassName = classFileName;
        if (classFileName.indexOf(File.separatorChar) != -1) {
            fullClassName = classFileName.replace(File.separatorChar, '.');
        }
        return fullClassName;
    }
    
    public static String getClassFilePath(File javaSourceFile, String sourceDirPath) {
    	String javaSourceFilePath = "";
    	try {
			javaSourceFilePath = javaSourceFile.getCanonicalPath();
		} catch (IOException ex) {
			log.error("Error in getClassNameFromSourceFile(): " + ex.getMessage());
		}
        String relativeJavaFileName = getRelativeFileName(javaSourceFilePath, sourceDirPath);
        String classFileName = convertExtensionFromJavaToClass(relativeJavaFileName);
        
        String classFilePath = "";
        String outputClassLocation = AutoLoaderConfig.getInstance().getOutputClassLocation();
        if (outputClassLocation.endsWith(File.separator)) {
            classFilePath = outputClassLocation + classFileName;
        }
        else {
            classFilePath = outputClassLocation + File.separator + classFileName;
        }
        return classFilePath;
    }
    
    private static String getRelativeFileName(String filePath, String directoryPath) {
        String relativeFileName = filePath;
        if (directoryPath != null) {
            relativeFileName = filePath.substring(directoryPath.length());
            if (relativeFileName.startsWith(File.separator)) 
                relativeFileName = relativeFileName.substring(1);
        }
        return relativeFileName;
    }
    
    private static String convertExtensionFromJavaToClass(String javaSourceFile) {
        if (javaSourceFile == null || javaSourceFile.length() == 0) 
            return javaSourceFile;
        String className = javaSourceFile.substring(0, javaSourceFile.lastIndexOf('.'));
        return className + ".class";
    }
}
