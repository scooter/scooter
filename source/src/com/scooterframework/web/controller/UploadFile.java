/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;

/**
 * UploadFile represents a file uploaded.
 *
 * @author (Fei) John Chen
 */
public class UploadFile {
	private FileItem fi;

	public UploadFile(FileItem fi) {
		this.fi = fi;
	}

	/**
	 * Returns the content type passed by the browser or null if not defined.
	 */
	public String getContentType() {
		return fi.getContentType();
	}

	/**
	 * Returns the name of the field in the multipart form corresponding to this file item.
	 */
	public String getFieldName() {
		return fi.getFieldName();
	}

	/**
	 * Returns the value of the field in the multipart form corresponding to this file item.
	 */
	public String getFieldValue() {
		return fi.getString();
	}

	/**
	 * Returns the original file name.
	 */
	public String getFileName() {
		String fileName = fi.getName();
		if (fileName.indexOf(File.separator) != -1)
		    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		return fileName;
	}

	/**
	 * Returns the contents of the file item as an array of bytes.
	 */
	public byte[] getContent() {
		return fi.get();
	}

	/**
	 * Returns the size of the file item.
	 */
	public long getSize() {
		return fi.getSize();
	}

	/**
	 * Writes an uploaded item to disk.
	 *
	 * @param dirPath The directory into which the uploaded item should be stored.
	 * @throws Exception
	 */
	public void writeTo(String dirPath) throws Exception {
		writeTo(new File(dirPath + File.separator + getFileName()));
	}

	/**
	 * Writes an uploaded item to disk.
	 *
	 * @param file The File into which the uploaded item should be stored.
	 * @throws Exception
	 */
	public void writeTo(File file) throws Exception {
		fi.write(file);
	}

	/**
	 * Returns an InputStream that can be used to retrieve the contents of the file.
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return fi.getInputStream();
	}

	/**
	 * Returns an OutputStream that can be used for storing the contents of the file.
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		return fi.getOutputStream();
	}
}
