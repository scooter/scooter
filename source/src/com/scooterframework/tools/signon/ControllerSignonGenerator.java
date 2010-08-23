/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.signon;

import java.io.File;
import java.util.Properties;

import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates a signon controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerSignonGenerator extends AbstractGenerator {
	private String packageName;
	private String controllerName;
	private String controllerClassName;
	private boolean noPrefix;
	private boolean noSuffix;

	public ControllerSignonGenerator(String templateFilePath, Properties props, String controller) {
		super(templateFilePath, props);
		
		controllerName = controller.toLowerCase();
		String controllerNameCamel = "";
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
		tpl.append(super.getTemplateContent());
		return tpl.toString();
	}

	protected Properties getTemplateProperties() {
		Properties templateProps = new Properties();

		templateProps.setProperty("package_name", packageName);
		templateProps.setProperty("controller_class_name", controllerClassName);
		templateProps.setProperty("controller", controllerName);

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