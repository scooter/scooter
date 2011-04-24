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
import java.util.ArrayList;
import java.util.List;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.FileUtil;

/**
 * class ErrorItem represents a compile error.
 * 
 * @author (Fei) John Chen
 */

public class ErrorItem {
	private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private static final int WINDOW = 4;
	private String filePath;
	private int errorLineNumber;
	private String errorMessage;
	private String errorIndicator;
	private List<String> errorCodeLines = new ArrayList<String>();

	public ErrorItem(String error) {
		int comma = error.indexOf(".java:");
		if (comma != -1) {
			filePath = error.substring(0, comma + 5);
			String s = error.substring(comma + 6);
			comma = s.indexOf(':');
			if (comma != -1) {
				errorLineNumber = Integer.parseInt(s.substring(0, comma));
				errorMessage = s.substring(comma + 1);
			}
		}
		
		try {
			List<String> content = FileUtil.readContent(new File(filePath));

			int x = 0;
			boolean startRecord = false;
			for (String line : content) {
				x++;

				if (x == getBeginLineNumber()) {
					startRecord = true;
				} else if (x > getEndLineNumber() || x > content.size()) {
					startRecord = false;
				}

				if (startRecord) {
					errorCodeLines.add(line);
				}
			}
		} catch (IOException e) {
			log.error("Error reading file " + filePath);
		}
	}
	
	void setDetails(List<String> details) {
	    if (details.size() > 0) {
	        if (details.get(0).startsWith("symbol")) errorMessage += " => " + details.get(0);
	        errorIndicator = details.get(details.size()-1);
	    }
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public int getErrorLineNumber() {
		return errorLineNumber;
	}
	
	public String getErrorIndicator() {
		return errorIndicator;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public int getBeginLineNumber() {
		return ((errorLineNumber - WINDOW) <= 0) ? 1 : (errorLineNumber - WINDOW);
	}

	public int getEndLineNumber() {
		return errorLineNumber + WINDOW - 1;
	}
	
	public List<String>	getCodeAroundError() {
		return errorCodeLines;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fileName=").append(filePath).append(", ");
		sb.append("lineNumber=").append(errorLineNumber).append(", ");
		sb.append("errorMessage=").append(errorMessage).append(", ");
		sb.append("errorCodeLines=").append(errorCodeLines);
		return sb.toString();
	}
}