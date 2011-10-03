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
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerScaffoldGenerator extends AbstractGenerator {
	protected String packageLine;
	protected String packageName;
	protected String connectionName;
	protected String resourceName;
	protected String controllerName;
	protected String controllerClassName;
	protected String modelName;
	protected String modelNameCamel;
	protected String modelClassName;
	protected String fullModelClassName;
	protected boolean hasPrimaryKey;
	protected boolean noPrefix;
	protected boolean noSuffix;

	public ControllerScaffoldGenerator(String templateFilePath,
			Map<String, String> props, String connName,
			String controller, String model, String table) {
		super(templateFilePath, props);
		
		this.connectionName = connName;
		this.modelName = model.toLowerCase();
		
		if (model.indexOf('.') != -1) {
			modelName = WordUtil.camelize(model.replace('.', '_')).toLowerCase();
		}
		
		if (StringUtil.startsWithLowerCaseChar(modelName)) {
			modelNameCamel = WordUtil.camelize(modelName);
		}
		else {
			modelNameCamel = model;
		}

		String modelClassPrefix = wc.getModelClassPrefix();
		String modelClassSuffix = wc.getModelClassSuffix();
		modelClassName = (isEmpty(modelClassSuffix))?modelNameCamel:(modelNameCamel + modelClassSuffix);
		fullModelClassName = (isEmpty(modelClassPrefix))?modelClassName:(modelClassPrefix + '.' + modelClassName);
	
		controllerName = controller.toLowerCase();
		String controllerNameCamel = "";
		if (StringUtil.startsWithLowerCaseChar(controllerName)) {
			controllerNameCamel = WordUtil.camelize(controllerName);
		}
		else {
			controllerNameCamel = controller;
		}
		resourceName = controllerName;

		String classPrefix = wc.getControllerClassPrefix();
		String classSuffix = wc.getControllerClassSuffix();
		noPrefix = isEmpty(classPrefix);
		noSuffix = isEmpty(classSuffix);
		packageName = classPrefix;
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
		
		ActiveRecord recordHome = generateActiveRecordHomeInstance(connectionName, model, table);
		if (recordHome.hasPrimaryKey()) {
			hasPrimaryKey = true;
		}
		
		if (!noPrefix) {
			packageLine = "package " + packageName + ";" + linebreak;
		}
	}

	@Override
	protected Map<String, ?> getTemplateProperties() {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("resource_name", resourceName);
		templateProps.put("controller_class_name", controllerClassName);
		templateProps.put("controller_name", controllerName);
		templateProps.put("model_name", modelName);
		templateProps.put("model_name_camel", modelNameCamel);
		templateProps.put("model_class_name", modelClassName);
		templateProps.put("full_model_class_name", fullModelClassName);
		templateProps.put("list_key", resourceName);
		templateProps.put("hasPrimaryKey", hasPrimaryKey);

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