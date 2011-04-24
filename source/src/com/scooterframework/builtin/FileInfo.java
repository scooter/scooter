/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import java.io.File;
import java.util.Date;

import com.scooterframework.admin.ApplicationConfig;

/**
 * FileInfo class.
 * 
 * @author (Fei) John Chen
 */
public class FileInfo {
	private static String appPath = ApplicationConfig.getInstance().getApplicationPath();
	
	private File file;
	private String name;
	private Date lastModified;
	private String extension;
	private long sizeInBytes;
	private String relativePath;
	
	public FileInfo(File file) {
		this.file = file;
		name = file.getName();
		lastModified = new Date(file.lastModified());
		
		extension = "";
		if (name.indexOf('.') != -1) {
			extension = name.substring(name.lastIndexOf('.') + 1);
		}
		
		sizeInBytes = file.length();
		relativePath = getRelativePath(file);
	}
	
	public static String getRelativePath(File file) {
		String rPath = "";
		String cPath = "";
		try {
			cPath = file.getCanonicalPath();
		} catch (Exception ex) {
			;
		}
		if (cPath.startsWith(appPath)) {
			rPath = cPath.substring(appPath.length());
		}
		rPath = rPath.replace('\\', '/');
		return rPath;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getLastModified() {
		return (lastModified == null)?lastModified:(new Date(lastModified.getTime()));
	}
	
	public String getType() {
		return (file.isDirectory())?"Folder":(extension + " File");
	}
	
	public String getSizeDisplay() {
		long kbs = sizeInBytes/1024;
		if (kbs == 0) kbs = 1L;
		return kbs + " KB";
	}
	
	public boolean isDirectory() {
		return file.isDirectory();
	}
	
	public String getRelativePath() {
		return relativePath;
	}
	
	public String getActionURI(String action) {
		return BuiltinHelper.FILE_BROWSER_LINK_PREFIX + "/" + action + "?f=" + relativePath;
	}
}
