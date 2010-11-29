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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * FileUtil class has helper methods for files and directories.
 * 
 * @author (Fei) John Chen
 */
public class FileUtil {
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
    
	public static void copy(File source, File target) throws IOException {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Input cannot be null.");
		}
		
		if (source.isDirectory()) {
			copyDir(source, target);
		}
		else {
			copyFile(source, target);
		}
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
    
    public static boolean rename(File source, File target) {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Input cannot be null.");
		}
		return source.renameTo(target);
    }
    
    public static boolean rename(String parentPathAbsolute, String oldName, String newName) {
    	boolean status = false;
    	
    	File oldFile = new File(parentPathAbsolute + File.separatorChar + oldName);
    	status = oldFile.renameTo(new File(parentPathAbsolute + File.separatorChar + newName));
    	
    	return status;
    }
    
    public static List<String> readContent(File file) throws IOException {
    	List<String> contentLines = new ArrayList<String>();
    	
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "utf-8"));
			while ((line = in.readLine()) != null) {
				contentLines.add(line);
			}
			in.close();
		} finally {
			if (in != null) in.close();
		}
    	return contentLines;
    }
    
    public static void updateFile(File file, String content) throws IOException {
    	List<String> contentLines = new ArrayList<String>();
    	contentLines.add(content);
    	updateFile(file, contentLines);
    }
    
    public static void updateFile(File file, List<String> contentLines) throws IOException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			Iterator<String> it = contentLines.iterator();
			while (it.hasNext()) {
				String l = it.next();
				out.println(l);
			}
			out.flush();
			out.close();
		}
		finally {
			if (out != null) out.close();
		}
    }
}
