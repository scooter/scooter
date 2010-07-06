/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * FileUtil class has helper methods for files and directories.
 * 
 * @author (Fei) John Chen
 */
public class FileUtil {
	public static final Set asciiFileExtensions = new HashSet();
	
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
		asciiFileExtensions.add("tpl");
		asciiFileExtensions.add("template");
		asciiFileExtensions.add("txt");
		asciiFileExtensions.add("xml");
		asciiFileExtensions.add("xsd");
		asciiFileExtensions.add("xslt");
	}
	
	public static boolean isAsciiFile(File file) {
		boolean check = false;
		String fileName = file.getName();
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot != -1) {
			String extension = fileName.substring(lastDot + 1);
			check = asciiFileExtensions.contains(extension);
		}
		return check;
	}
	
	public static boolean pathExist(String path) {
        boolean exist = false;
        try {
            File f = new File(path);
            if (f.isDirectory() && f.exists()) {
                exist = true;
            }
        }
        catch(Exception ex) {
            ;
        }
        return exist;
    }
    
    public static boolean pathExistAndHasFiles(String path) {
        boolean exist = false;
        try {
            File f = new File(path);
            if (f.isDirectory() && f.exists()) {
                String[] fileNames = f.list();
                if (fileNames != null && fileNames.length > 0) {
                    exist = true;
                }
            }
        }
        catch(Exception ex) {
            ;
        }
        return exist;
    }
    
	public static void copyFile(File oldFile, File newFile) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(oldFile));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
		byte[] bufSpace = new byte[8096];
		int k;
		while ((k=in.read(bufSpace)) != -1 ) out.write(bufSpace, 0, k);
		in.close();
		out.close();
	}
	
	public static void copyDir(File sourceDir, File targetDir) throws IOException {
		if (sourceDir == null || targetDir == null) {
			throw new IllegalArgumentException("Input cannot be null.");
		}
		
		if (targetDir.exists() && !targetDir.isDirectory()) {
			throw new IllegalArgumentException("Target directory is not a directory.");
		}
		
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		
		File[] files = sourceDir.listFiles();
		if (files == null) return;
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			String fileName = f.getName();
			String newFileName = targetDir.getCanonicalPath() + File.separator + fileName;
			File newFile = new File(newFileName);
			if (f.isFile()) {
				copyFile(f, newFile);
			}
			else if (f.isDirectory()) {
				copyDir(f, newFile);
			}
		}
	}
    
    public static boolean renameFile(String parentPathAbsolute, String oldName, String newName) {
    	boolean status = false;
    	
    	File oldFile = new File(parentPathAbsolute + File.separatorChar + oldName);
    	status = oldFile.renameTo(new File(parentPathAbsolute + File.separatorChar + newName));
    	
    	return status;
    }
}
