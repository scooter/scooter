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
 * This class generates controller test class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerTestGenerator extends AbstractGenerator {
	protected String packageLine;
	private String packageName;
	private String controllerName;
	private String controllerNameCamel;
	private String controllerClassName;
	private boolean noPrefix;
	private boolean noSuffix;
	private String[] actions;

	public ControllerTestGenerator(String templateFilePath, Map props, String controller, String[] actions) {
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

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();
		templateProps.put("app_name", getProperty("app.name"));
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("controller_class_name", controllerClassName);
		templateProps.put("controller_name", controllerName);
		templateProps.put("actions", actions);

		return templateProps;
	}

	protected String getRelativePathToOutputFile() {
		return (noPrefix)?(DIRECTORY_NAME_TEST + File.separatorChar + FUNCTIONAL_TEST):
					(DIRECTORY_NAME_TEST + File.separatorChar + 
							FUNCTIONAL_TEST + File.separatorChar +
							packageName.replace('.', File.separatorChar));
	}

	protected String getOutputFileName() {
		return controllerClassName + "Test" + FILE_EXTENSION_JAVA;
	}
}