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

import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerGenerator extends AbstractGenerator {
	protected String packageLine;
	private String packageName;
	private String controllerName;
	private String controllerNameCamel;
	private String controllerClassName;
	private boolean noPrefix;
	private boolean noSuffix;
	private String[] actions;

	public ControllerGenerator(String templateFilePath, Map<String, String> props, String controller, String[] actions) {
		super(templateFilePath, props);
		
		this.actions = actions;
		controllerName = controller.toLowerCase();
		if (StringUtil.startsWithLowerCaseChar(controller)) {
			controllerNameCamel = WordUtil.camelize(controller);
		}
		else {
			controllerNameCamel = controller;
		}

		String classPrefix = wc.getControllerClassPrefix();
		String classSuffix = wc.getControllerClassSuffix();
		noPrefix = isEmpty(classPrefix);
		noSuffix = isEmpty(classSuffix);
		packageName = classPrefix;
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
		
		if (!noPrefix) {
			packageLine = "package " + packageName + ";" + linebreak;
		}
	}

	@Override
	protected Map<String, Object> getTemplateProperties() {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("controller_class_name", controllerClassName);
		templateProps.put("controller_name", controllerName);
		templateProps.put("actions", actions);

		return templateProps;
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