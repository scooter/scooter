/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.scooterframework.common.util.GeneratorHelper;
import com.scooterframework.common.util.GeneratorImpl;

/**
 * This class initiates database.properties file.
 * 
 * @author (Fei) John Chen
 */
public class FileProcessor extends GeneratorImpl {
	private File file;
	private String fileName;
	private String fullFileName;
	private Properties props;
	
	public FileProcessor (File file, Properties props) {
		this.file = file;
		this.fileName = file.getName();
		try {
			fullFileName = file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.props = props;
	}
	
	public void process() {
		generate(getTemplateContent(), props, fullFileName, true);
	}

	protected String getTemplateContent() {
		String content = null;
		try {
			content = GeneratorHelper
					.loadToStringFromFile(file.getCanonicalPath());
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		return content;
	}
	
	protected Properties getTemplateProperties() {
		return props;
	}
	
	protected String getRootPath() {
		return System.getProperty("scooter.home");//not used here
	}

	protected String getRelativePathToOutputFile() {
		return null;//not used here
	}

	protected String getOutputFileName() {
		return fileName;//not used here
	}
}