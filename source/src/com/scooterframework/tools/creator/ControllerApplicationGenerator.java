/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.util.Properties;

import com.scooterframework.tools.common.GeneratorImpl;

/**
 * This class generates controller class code.
 * 
 * @author (Fei) John Chen
 */
public class ControllerApplicationGenerator extends GeneratorImpl {
	private String packageName;
	private String controllerClassName;
	private boolean noPrefix;
	
	public ControllerApplicationGenerator(String templateFilePath, Properties props) {
		super(templateFilePath, props);
		
		String controllerNameCamel = "Application";
		String classPrefix = props.getProperty("package_prefix") + ".controllers";
		String classSuffix = "Controller";
		noPrefix = isEmpty(classPrefix);
		if (!noPrefix) packageName = classPrefix;
		boolean noSuffix = isEmpty(classSuffix);
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
	}
	
	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		if (!noPrefix) {
			tpl.append("package {package_name};").append(linebreak);
			tpl.append("").append(linebreak);
		}
		tpl.append(super.getTemplateContent());
		return tpl.toString();
	}
	
	protected Properties getTemplateProperties() {
		Properties templateProps = new Properties();
		
		templateProps.setProperty("package_name", packageName);
		templateProps.setProperty("controller_class_name", controllerClassName);
		
		return templateProps;
	}
	
	protected String getRootPath() {
		return getProperty("app_path") + File.separator + "WEB-INF";
	}
	
	protected String getRelativePathToOutputFile() {
		return (noPrefix)?DIRECTORY_NAME_SRC:
					(DIRECTORY_NAME_SRC + File.separatorChar + 
					packageName.replace('.', File.separatorChar));
	}
	
	protected String getOutputFileName() {
		return controllerClassName + FILE_EXTENSION_JAVA;
	}
}