/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.util.ArrayList;
import java.util.List;

import com.scooterframework.common.logging.LogUtil;

/**
 * class CompileErrors represents compile errors.
 * 
 * @author (Fei) John Chen
 */
public class CompileErrors {
	private LogUtil log = LogUtil.getLogger(this.getClass().getName());
	
	private String rawResult;
	private int errorCount;
	private List<ErrorItem> errorItems = new ArrayList<ErrorItem>();
	
	public CompileErrors(String result) {
		this.rawResult = result;
		init(result);
	}
	
	private void init(String result) {
		List<String> lines = new ArrayList<String>();
		int begin = 0;
		int end = 0;
		int length = result.length();
		for (int i = 0; i < length; i++) {
			char c = result.charAt(i);
			
			if (c == '\n') {
				end = i - 1;
				String line = result.substring(begin, end);
				lines.add(line);
				begin = i + 1;
			}
		}
		
		String errorLine = null;
		ErrorItem errorItem = null;
		String aLine = "";
		List<String> details = null;
		int index = 0;
		for (index = 0; index < lines.size() -1; index++) {
			aLine = lines.get(index);
			if (aLine.indexOf(".java:") != -1) {
				if (errorItem != null) errorItem.setDetails(details);
				
				details = new ArrayList<String>();
				errorLine = aLine;
				errorItem = new ErrorItem(errorLine);
				errorItems.add(errorItem);
			}
			else {
				details.add(aLine);
			}
		}
		
		if (errorItem != null) errorItem.setDetails(details);
		
		aLine = lines.get(index);
		errorCount = Integer.parseInt(aLine.substring(0, aLine.indexOf(' ')));
		
		if (errorCount != errorItems.size()) {
			log.error("There are " + errorCount + " errors, but only parsed " + errorItems.size() + " errors.");
		}
	}
	
	public String getRawError() {
		return rawResult;
	}
	
	public int getErrorCount() {
		return errorCount;
	}
	
	public List<ErrorItem> getAllErrors() {
		return errorItems;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("errorCount=").append(errorCount).append(", ");
		sb.append("errorItems=").append(errorItems);
		return sb.toString();
	}
}
