/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.util.Properties;

import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public abstract class ViewScaffoldGenerator extends AbstractGenerator {
	protected static final String requiredHtmlText = "<span class=\"required\">*</span>";

	protected String resource;
	protected String controller;
	protected String model;
	protected String action;
	protected String relativePathToView;
	protected String viewFileName;

	public ViewScaffoldGenerator(Properties props, String controller, String model) {
		super(props);
		
		this.controller = controller.toLowerCase();
		this.model = model.toLowerCase();
		this.action = getAction();
		this.resource = this.controller;

		String viewExtension = wc.getViewExtension();
		if (viewExtension != null && !viewExtension.startsWith(".")) viewExtension = "." + viewExtension;

		String webpageDirectoryName = wc.getWebPageDirectoryName();
		if (webpageDirectoryName.startsWith("/") || webpageDirectoryName.startsWith("\\"))
			webpageDirectoryName = webpageDirectoryName.substring(1);

		relativePathToView = webpageDirectoryName + "/" + controller.toLowerCase();
		
		viewFileName = isEmpty(viewExtension)?action:(action + viewExtension);
	}

	protected abstract String getAction();

	protected Properties getTemplateProperties() {
		Properties templateProps = new Properties();
		templateProps.setProperty("resource", resource);
		templateProps.setProperty("controller", controller);
		templateProps.setProperty("model", model);
		templateProps.setProperty("action", action);
		return templateProps;
	}
	
	protected String getRootPath() {
		return getProperty("app.path");
	}

	protected String getRelativePathToOutputFile() {
		return relativePathToView;
	}

	protected String getOutputFileName() {
		return viewFileName;
	}
}