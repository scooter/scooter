/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import com.scooterframework.common.util.Util;

/**
 * The is the super class of all generator classes.
 * 
 * @author (Fei) John Chen
 */
public abstract class GeneratorImpl implements Generator {
	public static final String DIRECTORY_NAME_SRC = "src";
	public static final String DIRECTORY_NAME_TEST = "test";
	public static final String FILE_EXTENSION_JAVA = ".java";
	public static final String FILE_EXTENSION_JSP = ".jsp";
	public static final String FILE_EXTENSION_PROPERTIES = ".properties";
	public static final String FILE_EXTENSION_YAML = ".yaml";
	public static final String UNIT_TEST = "unit";
	public static final String FUNCTIONAL_TEST = "functional";
	
	private static final String CHANGED_NO  = "N";
	private static final String CHANGED_YES = "Y";
	
	protected String templateFilePath;
	private Properties props;
	
	public static final String linebreak = System.getProperty("line.separator", "\r\n");
	
	public GeneratorImpl(Properties props) {
		this.props = props;
	}
	
	public GeneratorImpl(String templateFilePath, Properties props) {
		this.templateFilePath = templateFilePath;
		this.props = props;
	}
	
	protected String getProperty(String key) {
		return props.getProperty(key);
	}

	protected String getTemplateFileContent(String templateFilePath) {
		String templateContent = "";
		try {
			templateContent = GeneratorHelper.loadToStringFromFile(templateFilePath);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to load template file '" 
					+ templateFilePath + "': " + ex.getMessage());
		}
		return templateContent;
	}

	/**
	 * Generates code. If the code is already generated, overwrite it.
	 */
	public void generate() {
		generate(true);
	}

	/**
	 * Generates code with an option if to overwrite the existing code.
	 * 
	 * @param overwrite
	 */
	public void generate(boolean overwrite) {
		generate(getTemplateContent(), getTemplateProperties(), getRootPath(), 
				 getRelativePathToOutputFile(), getOutputFileName(), overwrite);
	}

	protected String getTemplateContent() {
		if (templateFilePath == null)
			throw new IllegalArgumentException(
				"Template file path is null. Please either provide template " + 
				"file path or implement getTemplateContent() method.");
		return getTemplateFileContent(templateFilePath);
	}
	
	protected abstract Properties getTemplateProperties();
	
	protected abstract String getRootPath();
	
	protected abstract String getRelativePathToOutputFile();
	
	protected abstract String getOutputFileName();
	
	protected void generate(String templateContent, Properties props, String rootPath, 
			String relativePathToFile, String outputFileName, boolean overwrite) {
		String outputFile = (relativePathToFile == null || "".equals(relativePathToFile))?
				outputFileName:(relativePathToFile + File.separatorChar + outputFileName);
		String fullPathToOutputFile = rootPath + File.separator + outputFile;
		generate(templateContent, props, fullPathToOutputFile, overwrite);
	}
	
	protected void generate(String templateContent, Properties props, 
			String fullPathToOutputFile, boolean overwrite) {
		int status = -1;
		if (templateContent == null) {
			status = -1;
		}
		else {
			String[] results = processTemplateContent(templateContent, props);
			String processStatus = results[0];
			String processedContent = results[1];
			try {
				status = GeneratorHelper.outputTo(processedContent, 
						fullPathToOutputFile, overwrite);
				if (CHANGED_YES.equals(processStatus)) {
					status = 1;
					log(Util.decode(status, "-1=exists, 0=create, 1=recreate", "      ") + " " + fullPathToOutputFile.replace('\\', '/'));
				}
			}
			catch(Exception ex)	{
				log("ERROR ERROR ERROR in creating file \"" + 
						fullPathToOutputFile + "\": " + ex.getMessage());
			}
		}
	}
	
	protected String[] processTemplateContent(String templateContent, Properties props) {
		String[] results = new String[2];
		results[0] = CHANGED_NO;
		results[1] = templateContent;
		if (props == null || templateContent == null || "".equals(templateContent)) {
			return results;
		}
		String content = templateContent;
		Enumeration en = props.propertyNames();
		while(en.hasMoreElements()) {
			String key = (String)en.nextElement();
			String value = escape(props.getProperty(key));
			content = content.replaceAll("\\Q{" + key + "}", value);
		}
		if (!templateContent.equals(content)) {
			results[0] = CHANGED_YES;
		}
		results[1] = content;
		return results;
	}
	
	protected void log(String s) {
		System.out.println(s);
	}
	
	protected boolean isEmpty(String s) {
		return (s == null || "".equals(s.trim()))?true:false;
	}
    
    public static String escape(String s) {
    	if (s.indexOf('\\') == -1) return s;
    	return s.replaceAll("\\\\", "\\\\\\\\");
    }
}