/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Util class contains utility methods for tools.
 * 
 * @author (Fei) John Chen
 */
public class ToolsUtil {
	private static final Set<String> asciiFileExtensions = new HashSet<String>();
	
	static {
		asciiFileExtensions.add("bat");
		asciiFileExtensions.add("c");
		asciiFileExtensions.add("cc");
		asciiFileExtensions.add("classpath");
		asciiFileExtensions.add("config");
		asciiFileExtensions.add("css");
		asciiFileExtensions.add("env");
		asciiFileExtensions.add("htm");
		asciiFileExtensions.add("html");
		asciiFileExtensions.add("ini");
		asciiFileExtensions.add("java");
		asciiFileExtensions.add("js");
		asciiFileExtensions.add("jsp");
		asciiFileExtensions.add("project");
		asciiFileExtensions.add("properties");
		asciiFileExtensions.add("sh");
		asciiFileExtensions.add("st");
		asciiFileExtensions.add("tpl");
		asciiFileExtensions.add("template");
		asciiFileExtensions.add("txt");
		asciiFileExtensions.add("xml");
		asciiFileExtensions.add("xsd");
		asciiFileExtensions.add("xslt");
	}
	
	/**
	 * Checks whether the file is a test file. This method should only be used 
	 * by Scooter's code generator.
	 * 
	 * @param file  the file to check
	 * @return true if the file is text file.
	 */
	public static boolean isTextFile(File file) {
		boolean check = false;
		String fileName = file.getName();
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot != -1) {
			String extension = fileName.substring(lastDot + 1);
			check = asciiFileExtensions.contains(extension);
		}
		return check;
	}
    
	public static String detectRootPath() throws Exception {
        String path = "";
        try {
            path = (new File("")).getCanonicalPath();
        }
        catch(Exception ex) {
            String errorMessage = "Failed to detect root path: " + ex.getMessage();
            throw new Exception(errorMessage);
        }
        return path;
    }
	
	public static String detectImplicitAppName(String webappsPath) throws Exception {
		String appName = "";
        try {
            File webappsDir = new File(webappsPath);
            if (!webappsDir.isDirectory()) {
            	throw new Exception("The input webappsPath \"" + webappsPath + "\" is not a directory.");
            }
            
            int count = 0;
            File[] files = webappsDir.listFiles();
            for (int i = 0; i < files.length; i++) {
            	File f = files[i];
            	if (f.isDirectory() && !"ROOT".equals(f.getName()) && isWebapp(f)) {
            		appName = f.getName();
            		count++;
            	}
            }
            
            if (count > 1) {
            	throw new AmbiguousAppNameException("More than one webapp is found under the path \"" + webappsPath + "\".");
            }
        }
        catch(AmbiguousAppNameException ex) {
            String errorMessage = "Failed to detect implicit app name because " + ex.getMessage();
            throw new AmbiguousAppNameException(errorMessage);
        }
        catch(Exception ex) {
            String errorMessage = "Failed to detect implicit app name because " + ex.getMessage();
            throw new Exception(errorMessage);
        }
        return appName;
	}
	
	private static boolean isWebapp(File dir) throws Exception {
		if (!dir.isDirectory()) return false;
		
		boolean check = false;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
        	File f = files[i];
        	if (f.isDirectory() && "WEB-INF".equalsIgnoreCase(f.getName())) {
        		check = true;
        		break;
        	}
        }
		
		return check;
	}
    
	public static boolean containsPath(String s) {
		boolean check = false;
		if (s.indexOf(File.separatorChar) != -1 || s.indexOf('/') != -1) {
			check = true;
		}
		return check;
	}

	public static String[] getPathAndName(String s) {
		String[] ss = new String[3];
		File f = new File(s);
		try {
			String filePath = f.getCanonicalPath();
			String parentPath = (new File(filePath)).getParentFile().getCanonicalPath();
			ss[0] = parentPath;
			ss[1] = filePath;
			ss[2] = (parentPath.endsWith(File.separator))?
					filePath.substring(parentPath.length()):
					filePath.substring(parentPath.length() + 1);
		}
		catch(Exception ex) {
			;
		}
		return ss;
	}

	public static String setSystemProperty(String key, String defaultValue) {
		String p = System.getProperty(key);
		if (p == null) {
			System.setProperty(key, defaultValue);
			p = defaultValue;
		}
		return p;
	}
    
	public static void processAllFiles(File targetDir, Map allProps) throws IOException {
    	File[] files = targetDir.listFiles();
    	if (files == null) return;
    	for (int i = 0; i < files.length; i++) {
    		File file = files[i];
    		if (file.isFile()) {
    			if (isTextFile(file)) {
    				FileTransformerGenerator fp = new FileTransformerGenerator(file.getCanonicalPath(), allProps);
    				fp.transform();
    			}
    		}
    		else if (file.isDirectory()) {
    			processAllFiles(file, allProps);
    		}
    	}
    }

	public static void validatePathExistence(String path) {
		File f = new File(path);
        if (!f.exists()) {
            System.out.println("");
            System.out.println("ERROR ERROR ERROR: Path [" + path + "] does not exist.");
            System.out.println("");
            System.out.println("System exits now.");
            System.exit(-1);
        }
	}
	
}