/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerApplicationGenerator extends AbstractGenerator {
	protected String packageLine;
	private String packageName;
	private String controllerClassName;
	private boolean noPrefix;

	public ControllerApplicationGenerator(String templateFilePath, Map props) {
		super(templateFilePath, props);
		
		String controllerNameCamel = "Application";
		wc.update(null, null);
		String classPrefix = wc.getControllerClassPrefix();
		String classSuffix = wc.getControllerClassSuffix();
		noPrefix = isEmpty(classPrefix);
		if (!noPrefix) packageName = classPrefix;
		boolean noSuffix = isEmpty(classSuffix);
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
		
		if (!noPrefix) {
			packageLine = "package " + packageName + ";" + linebreak;
		}
	}

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("controller_class_name", controllerClassName);

		return templateProps;
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