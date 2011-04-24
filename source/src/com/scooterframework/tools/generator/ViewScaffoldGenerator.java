/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public abstract class ViewScaffoldGenerator extends AbstractGenerator {
	protected static final String requiredHtmlText = "<span class=\"required\">*</span>";

	protected String connectionName;
	protected String resource;
	protected String controller;
	protected String modelName;
	protected String action;
	protected String relativePathToView;
	protected String viewFileName;
	protected ActiveRecord recordHome;

	public ViewScaffoldGenerator(String templateFilePath,
			Map<String, String> props, String connName,
			String controller, String model) {
		super(templateFilePath, props);
		
		this.connectionName = connName;
		this.controller = controller.toLowerCase();
		this.modelName = model.toLowerCase();
		this.action = getAction();
		this.resource = controller.toLowerCase();
		
		if (model.indexOf('.') != -1) {
			modelName = model.replace('.', '_');
			modelName = WordUtil.camelize(modelName);
			modelName = modelName.toLowerCase();
		}

		String viewExtension = wc.getViewExtension();
		if (viewExtension != null && !viewExtension.startsWith(".")) viewExtension = "." + viewExtension;

		String webpageDirectoryName = wc.getWebPageDirectoryName();
		if (webpageDirectoryName.startsWith("/") || webpageDirectoryName.startsWith("\\"))
			webpageDirectoryName = webpageDirectoryName.substring(1);

		relativePathToView = webpageDirectoryName + "/" + controller.toLowerCase();
		
		viewFileName = isEmpty(viewExtension)?action:(action + viewExtension);
		
		recordHome = generateActiveRecordHomeInstance(connectionName, model);
	}

	protected abstract String getAction();

	@Override
	protected Map<String, Object> getTemplateProperties() {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("resource", resource);
		templateProps.put("controller", controller);
		templateProps.put("model", modelName);
		templateProps.put("action", action);
		return templateProps;
	}
	
	@Override
	protected String getRootPath() {
		return getProperty("app.path");
	}

	@Override
	protected String getRelativePathToOutputFile() {
		return relativePathToView;
	}

	@Override
	protected String getOutputFileName() {
		return viewFileName;
	}
}