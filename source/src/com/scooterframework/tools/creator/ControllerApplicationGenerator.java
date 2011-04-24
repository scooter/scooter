/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.tools.common.GeneratorImpl;

/**
 * This class generates controller class code.
 * 
 * @author (Fei) John Chen
 */
public class ControllerApplicationGenerator extends GeneratorImpl {
	protected String packageLine;
	private String packageName;
	private String controllerClassName;
	private boolean noPrefix;
	
	public ControllerApplicationGenerator(String templateFilePath, Map<String, String> props) {
		super(templateFilePath, props);
		
		String controllerNameCamel = "Application";
		String classPrefix = props.get("package_prefix") + ".controllers";
		String classSuffix = "Controller";
		noPrefix = isEmpty(classPrefix);
		if (!noPrefix) packageName = classPrefix;
		boolean noSuffix = isEmpty(classSuffix);
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
		
		if (!noPrefix) {
			packageLine = "package " + packageName + ";" + linebreak;
		}
	}
	
	@Override
	protected Map<String, String> getTemplateProperties() {
		Map<String, String> templateProps = new HashMap<String, String>();
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("controller_class_name", controllerClassName);
		
		return templateProps;
	}
	
	@Override
	protected String getRootPath() {
		return getProperty("app_path") + File.separator + "WEB-INF";
	}
	
	@Override
	protected String getRelativePathToOutputFile() {
		return (noPrefix)?DIRECTORY_NAME_SRC:
					(DIRECTORY_NAME_SRC + File.separatorChar + 
					packageName.replace('.', File.separatorChar));
	}
	
	@Override
	protected String getOutputFileName() {
		return controllerClassName + FILE_EXTENSION_JAVA;
	}
}