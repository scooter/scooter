/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;
import java.util.List;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.Event;

/**
 * class CompileEvent represents a compile result event.
 * 
 * @author (Fei) John Chen
 */
public class CompileEvent extends Event {
	private boolean success;
	private List<File> files;
	
	public CompileEvent(boolean success, String eventType, String message, List<File> files) {
		super(eventType, message);
		
		if (!Constants.EVENT_COMPILE.equals(eventType)) {
			throw new IllegalArgumentException("Unsupported event type: " + eventType);
		}
		
		this.success = success;
		this.files = files;
	}
	
	/**
	 * Checks if this is an compile success event.
	 */
	public boolean compileSuccess() {
		return success;
	}
	
	/**
	 * Returns the files that are compiled.
	 */
	public List<File> getCompileFiles() {
		return files;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(", ");
		sb.append("success=").append(success).append(", ");
		sb.append("files=").append(files);
		return sb.toString();
	}
}
