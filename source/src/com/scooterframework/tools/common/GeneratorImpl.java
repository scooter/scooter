/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.io.File;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;

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
	private Map<String, String> props;
	
	public static final String linebreak = System.getProperty("line.separator", "\r\n");
	
	public GeneratorImpl(Map<String, String> props) {
		this.props = props;
	}
	
	public GeneratorImpl(String templateFilePath, Map<String, String> props) {
		this.templateFilePath = templateFilePath;
		this.props = props;
	}
	
	protected String getProperty(String key) {
		return props.get(key);
	}
	
	protected String toString(Object o) {
		return (o != null)?o.toString():null;
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
	
	protected abstract Map<String, ?> getTemplateProperties();
	
	protected abstract String getRootPath();
	
	protected abstract String getRelativePathToOutputFile();
	
	protected abstract String getOutputFileName();
	
	protected void generate(String templateContent, Map<String, ?> props, String rootPath, 
			String relativePathToFile, String outputFileName, boolean overwrite) {
		String outputFile = (relativePathToFile == null || "".equals(relativePathToFile))?
				outputFileName:(relativePathToFile + File.separatorChar + outputFileName);
		String fullPathToOutputFile = rootPath + File.separator + outputFile;
		generate(templateContent, props, fullPathToOutputFile, overwrite);
	}
	
	protected void generate(String templateContent, Map<String, ?> props, 
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
					log(Util.decode(status, "-1=exists, 0=create, 1=recreate", "      ") + " " + fullPathToOutputFile.replace('\\', '/'));
				}
			}
			catch(Exception ex)	{
				log("ERROR ERROR ERROR in creating file \"" + 
						fullPathToOutputFile + "\": " + ex.getMessage());
			}
		}
	}
	
	protected String[] processTemplateContent(String templateContent, Map<String, ?> props) {
		String[] results = new String[2];
		results[0] = CHANGED_NO;
		results[1] = templateContent;
		if (props == null || props.size() == 0 || templateContent == null || "".equals(templateContent)) {
			return results;
		}
		String content = renderContent(templateContent, props);
		if (!templateContent.equals(content)) {
			results[0] = CHANGED_YES;
		}
		results[1] = content;
		return results;
	}
	
	protected String renderContent(String originalContent, Map<String, ?> props) {
		return ("Q".equals(props.get(Generator.TEMPLATE_PARSER_TYPE)))?
				renderContent_Q(originalContent, props)
				:renderContent_ST(originalContent, props);
	}
	
	private String renderContent_Q(String originalContent, Map<String, ?> props) {
		String newContent = originalContent;
		for (Map.Entry<String, ?> entry : props.entrySet()) {
			String key = entry.getKey();
			String value = escape(entry.getValue());
			newContent = newContent.replaceAll("\\Q{" + key + "}", value);
		}
		return newContent;
	}
	
	private String renderContent_ST(String originalContent, Map<String, ?> props) {
		StringTemplate st = new StringTemplate(originalContent);
		st.setAttributes(props);
		return st.toString();
	}
	
	protected void log(String s) {
		System.out.println(s);
	}
	
	protected boolean isEmpty(String s) {
		return (s == null || "".equals(s.trim()))?true:false;
	}
    
    public static String escape(Object o) {
    	if (o == null) return null;
    	
    	String s = o.toString();
    	if (s.indexOf('\\') == -1) return s;
    	return s.replaceAll("\\\\", "\\\\\\\\");
    }
}