/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.scooterframework.common.logging.LogUtil;
import com.sun.tools.javac.Main;

/**
 * JavaCompiler class provides helper methods to compile Java source files. <br/>
 * 
 * Notice that class path, output class directory and source path are defined 
 * by <tt>AutoLoaderConfig</tt> class.
 * 
 * @author (Fei) John Chen
 */
public final class JavaCompiler {
	private static LogUtil log = LogUtil.getLogger(JavaCompiler.class.getName());
	
	private static String[] compilerBaseArgs = {
		"-classpath", AutoLoaderConfig.getInstance().getClassPath(),
		"-d", AutoLoaderConfig.getInstance().getOutputClassLocation(),
		"-sourcepath", AutoLoaderConfig.getInstance().getSourcePath()};
	
	public static String compile(String[] sourceFileNames) {
		if (sourceFileNames == null || sourceFileNames.length == 0) return null;
		
		StringWriter compilerMessage = new StringWriter();
		PrintWriter compilerMessageWriter = new PrintWriter(compilerMessage);
		String args[] = constructCompilerArgs(sourceFileNames);
		int exitValue = 0;
		try {
			exitValue = Main.compile(args, compilerMessageWriter);
		}
		catch(Exception ex) {
			log.error(ex);
		}
		compilerMessageWriter.close();
		return (exitValue == 0) ? null : compilerMessage.toString();
	}

	public static String compile(File[] sourceFiles) {
		String[] sourcePaths = new String[sourceFiles.length];
		for (int i = 0; i < sourcePaths.length; i++) {
			sourcePaths[i] = sourceFiles[i].getAbsolutePath();
		}
		return compile(sourcePaths);
	}

	public static String compile(List sourceFiles) {
		int size = sourceFiles.size();
		String sourcePaths[] = new String[size];
		for (int i = 0; i < size; i++) {
			sourcePaths[i] = ((File)sourceFiles.get(i)).getAbsolutePath();
		}
		return compile(sourcePaths);
	}

	private static String[] constructCompilerArgs(String[] sourceFileNames) {
		int baseCount = compilerBaseArgs.length;
		int total = baseCount + sourceFileNames.length;
		String[] args = new String[total];
		for (int i = 0; i < baseCount; i++) {
			args[i] = compilerBaseArgs[i];
		}
		for (int i = baseCount; i < total; i++) {
			args[i] = sourceFileNames[i - baseCount];
		}
		return args;
	}
}
