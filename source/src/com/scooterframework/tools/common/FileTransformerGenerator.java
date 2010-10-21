/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.util.Map;

/**
 * This class processes a file.
 * 
 * @author (Fei) John Chen
 */
public class FileTransformerGenerator extends GeneratorImpl {
	private String targetFileFullPath;
	private Map transformProperties;
	
	public FileTransformerGenerator(String sourceFileFullPath, Map transformProperties) {
		this(sourceFileFullPath, sourceFileFullPath, transformProperties);
	}
	
	public FileTransformerGenerator(String sourceFileFullPath,
			String targetFileFullPath, Map transformProperties) {
		super(sourceFileFullPath, transformProperties);
		this.targetFileFullPath = targetFileFullPath;
		this.transformProperties = transformProperties;
	}
	
	public void transform() {
		generate(getTemplateContent(), transformProperties, targetFileFullPath, true);
	}
	
	protected Map getTemplateProperties() {
		return transformProperties;
	}
	
	protected String getRootPath() {
		throw new UnsupportedOperationException("getRootPath() is not supported.");
	}

	protected String getRelativePathToOutputFile() {
		throw new UnsupportedOperationException("getRelativePathToOutputFile() is not supported.");
	}

	protected String getOutputFileName() {
		return null;//not used here
	}
}