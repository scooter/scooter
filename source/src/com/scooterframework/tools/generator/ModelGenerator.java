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
 * This class generates model class code.
 *
 * @author (Fei) John Chen
 */
public class ModelGenerator extends AbstractGenerator {
	protected String packageName;
	protected String modelName;
	protected String modelClassName;
	protected boolean noPrefix;
	protected boolean noSuffix;

	public ModelGenerator(String templateFilePath, Properties props, String model) {
		super(templateFilePath, props);
		
		modelName = model.toLowerCase();
		String modelNameCamel = "";
		if (StringUtil.startsWithLowerCaseChar(model)) {
			modelNameCamel = WordUtil.camelize(model);
		}
		else {
			modelNameCamel = model;
		}

		String classPrefix = wc.getModelClassPrefix();
		String classSuffix = wc.getModelClassSuffix();
		noPrefix = isEmpty(classPrefix);
		noSuffix = isEmpty(classSuffix);
		packageName = classPrefix;
		modelClassName = (noSuffix)?modelNameCamel:(modelNameCamel + classSuffix);
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
		templateProps.setProperty("model_name", modelName);
		templateProps.setProperty("model_class_name", modelClassName);

		return templateProps;
	}

	protected String getRelativePathToOutputFile() {
		return (noPrefix)?DIRECTORY_NAME_SRC:
					(DIRECTORY_NAME_SRC + File.separatorChar +
					packageName.replace('.', File.separatorChar));
	}

	protected String getOutputFileName() {
		return modelClassName + FILE_EXTENSION_JAVA;
	}
}