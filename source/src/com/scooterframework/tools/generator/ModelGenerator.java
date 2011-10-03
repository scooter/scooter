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

import com.scooterframework.autoloader.AutoLoaderConfig;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates model class code.
 *
 * @author (Fei) John Chen
 */
public class ModelGenerator extends AbstractGenerator {
	protected String packageLine;
	protected String packageName;
	protected String connectionName;
	protected String modelName;
	protected String modelClassName;
	protected String tableName;
	protected String superClassName;
	protected boolean noPrefix;
	protected boolean noSuffix;
	protected boolean enhance;

	public ModelGenerator(String templateFilePath, Map<String, String> props,
			String connName, String model, String table, boolean enhance) {
		super(templateFilePath, props);
		
		this.connectionName = connName;
		this.modelName = model.toLowerCase();
		
		if (model.indexOf('.') != -1) {
			tableName = model.toLowerCase();
			modelName = WordUtil.camelize(model.replace('.', '_')).toLowerCase();
		}
		
		if (table != null && !"".equals(table)) {
			tableName = table;
		}
		
		String modelNameCamel = "";
		if (StringUtil.startsWithLowerCaseChar(modelName)) {
			modelNameCamel = WordUtil.camelize(modelName);
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
		
		if (enhance) {
			superClassName = "ActiveRecord";
		}
		else {
			superClassName = AutoLoaderConfig.GENERATED_MODEL_CLASS_PREFIX
					+ modelClassName
					+ AutoLoaderConfig.GENERATED_MODEL_CLASS_SUFFIX;
		}
		
		this.enhance = enhance;
		
		if (!noPrefix) {
			packageLine = "package " + packageName + ";" + linebreak;
		}
	}

	@Override
	protected Map<String, String> getTemplateProperties() {
		Map<String, String> templateProps = new HashMap<String, String>();
		templateProps.put("package_line", packageLine);
		templateProps.put("package_name", packageName);
		templateProps.put("connection_name", connectionName);
		templateProps.put("model_name", modelName);
		templateProps.put("model_class_name", modelClassName);
		templateProps.put("table_name", tableName);
		templateProps.put("super_class_name", superClassName);
		templateProps.put("enhance", (enhance)?"":null);

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
		return modelClassName + FILE_EXTENSION_JAVA;
	}
}