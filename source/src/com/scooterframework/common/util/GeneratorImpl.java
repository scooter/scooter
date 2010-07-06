/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The is the super class of all generator classes.
 * 
 * @author (Fei) John Chen
 */
public abstract class GeneratorImpl implements Generator {
	public static final String sourceDirName = "src";
	public static final String testDirName = "test";
	public static final String javaFileExtension = ".java";
	public static final String propertiesFileExtension = ".properties";
	public static final String linebreak = System.getProperty("line.separator", "\r\n");
	private static final String CHANGED_NO  = "N";
	private static final String CHANGED_YES = "Y";

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
	
	protected abstract String getTemplateContent();
	
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