/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;

/**
 * SourceFile contains information on source file and its corresponding class file.
 * 
 * @author (Fei) John Chen
 */
public class SourceFile {
    private File source = null;
    private String sourceFilePath = "";
    private long lastSourceModifiedTime = 0L;
    
    private File clazz = null;
    private String clazzFilePath = "";
    private long lastClassModifiedTime = 0L;
    
    public SourceFile(File file) {
        init(file);
    }
    
    private void init(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Input file is null.");
        }
        else if (!file.isFile()) {
            throw new IllegalArgumentException("Input file is not file or does not exist: " + file);
        }
        
        source = file;
        sourceFilePath = source.getAbsolutePath();
        lastSourceModifiedTime = source.lastModified();
        
        clazzFilePath = getClassFilePath(source);
        clazz = new File(clazzFilePath);
        lastClassModifiedTime = clazz.lastModified();
    }
    
    public boolean isUpdated(File file) {
        if (file == null || !file.getAbsolutePath().equals(sourceFilePath) ||
            !file.isFile()) {
            return false;
        }
        
        boolean updated = false;
        
        if (lastSourceModifiedTime < file.lastModified()) {
            updated = true;
            init(file);
        }
        
        return updated;
    }
    
    public File getSource() {
        return source;
    }
    
    public boolean availableForRecompile() {
        return (lastSourceModifiedTime > lastClassModifiedTime || lastClassModifiedTime == 0)?true:false;
    }
    
    public String getClassFilePath() {
        return clazzFilePath;
    }
    
    public long getLastSourceModifiedTime() {
        return lastSourceModifiedTime;
    }
    
    public long getLastClassModifiedTime() {
        return lastClassModifiedTime;
    }
    
    public static String getClassName(File javaSourceFile) {
        String sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        String relativeJavaFileName = getRelativeFileName(javaSourceFile, sourcePath);
        String classFileName = getClassName(relativeJavaFileName);
        if (classFileName.indexOf(".class") != -1) {
            classFileName = classFileName.substring(0, classFileName.indexOf(".class"));
        }
        String className = classFileName;
        if (classFileName.indexOf(File.separatorChar) != -1) {
            className = classFileName.replace(File.separatorChar, '.');
        }
        return className;
    }
    
    public static String getSourceNameFromClassName(String className) {
        if (className == null || className.length() == 0) 
            return className;
        String sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        String sourceName = className;
        if (className.endsWith(".class")) {
            sourceName = className.substring(0, className.indexOf(".class"));
        }
        sourceName = sourceName.replace('.', File.separatorChar);
        if (sourcePath.endsWith(File.separator)) {
            sourceName = sourcePath + sourceName;
        }
        else {
            sourceName = sourcePath + File.separator + sourceName;
        }
        return sourceName + ".java";
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("source=" + source).append(", ");
        sb.append("lastSourceModifiedTime=" + lastSourceModifiedTime).append(", ");
        sb.append("clazz=" + clazz).append(", ");
        sb.append("lastClassModifiedTime=" + lastClassModifiedTime);
        return sb.toString();
    }
    
    private String getClassFilePath(File javaSourceFile) {
        String sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        String outputClassLocation = AutoLoaderConfig.getInstance().getOutputClassLocation();
        String relativeJavaFileName = getRelativeFileName(javaSourceFile, sourcePath);
        String classFilePath = "";
        String classFileName = getClassName(relativeJavaFileName);
        if (outputClassLocation.endsWith(File.separator)) {
            classFilePath = outputClassLocation + classFileName;
        }
        else {
            classFilePath = outputClassLocation + File.separator + classFileName;
        }
        return classFilePath;
    }
    
    private static String getRelativeFileName(File file, String directoryPath) {
        String fileName = file.getAbsolutePath();
        String relativeFileName = fileName;
        if (directoryPath != null) {
            relativeFileName = fileName.substring(directoryPath.length());
            if (relativeFileName.startsWith(File.separator)) 
                relativeFileName = relativeFileName.substring(1);
        }
        return relativeFileName;
    }
    
    private static String getClassName(String javaSourceFile) {
        if (javaSourceFile == null || javaSourceFile.length() == 0) 
            return javaSourceFile;
        String className = javaSourceFile.substring(0, javaSourceFile.lastIndexOf('.'));
        return className + ".class";
    }
    
    public String recompile() {
        String error = JavaCompiler.compile(new File[]{source});
        return error;
    }
}
