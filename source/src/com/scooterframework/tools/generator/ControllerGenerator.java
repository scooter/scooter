/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.io.File;
import java.util.Properties;

import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerGenerator extends AbstractGenerator {
	private String packageName;
	private String controllerName;
	private String controllerNameCamel;
	private String controllerClassName;
	private boolean noPrefix;
	private boolean noSuffix;
	private String[] actions;

	public ControllerGenerator(Properties props, String controller, String[] actions) {
		super(props);
		
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
	}

	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		if (!noPrefix) {
			tpl.append("package {package_name};").append(linebreak);
			tpl.append("").append(linebreak);
		}
		tpl.append("/**").append(linebreak);
		tpl.append(" * {controller_class_name} class handles {controller_name} related access.").append(linebreak);
		tpl.append(" */").append(linebreak);
		tpl.append("public class {controller_class_name} extends ApplicationController {").append(linebreak);

		int length = actions.length;
		for (int i = 0; i < length; i++) {
			String action = actions[i];

			tpl.append("    /**").append(linebreak);
			tpl.append("     * " + action + " method").append(linebreak);
			tpl.append("     */").append(linebreak);
			tpl.append("    public String " + action + "() {").append(linebreak);
			tpl.append("        return null;").append(linebreak);
			tpl.append("    }").append(linebreak);
			tpl.append("").append(linebreak);
		}

		tpl.append("}");
		return tpl.toString();
	}

	protected Properties getTemplateProperties() {
		Properties templateProps = new Properties();

		templateProps.setProperty("package_name", packageName);
		templateProps.setProperty("controller_class_name", controllerClassName);
		templateProps.setProperty("controller_name", controllerName);

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