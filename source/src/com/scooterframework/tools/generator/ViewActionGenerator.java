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

import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public class ViewActionGenerator extends AbstractGenerator {
	private String controller;
	private String action;
	private String relativePathToView;
	private String viewFileName;

	public ViewActionGenerator(String templateFilePath, Map props, String controller, String action) {
		super(templateFilePath, props);
		
		this.controller = controller.toLowerCase();
		this.action = action.toLowerCase();

		String viewExtension = wc.getViewExtension();
		if (viewExtension != null && !viewExtension.startsWith(".")) viewExtension = "." + viewExtension;

		String webpageDirectoryName = wc.getWebPageDirectoryName();
		if (webpageDirectoryName.startsWith("/") || webpageDirectoryName.startsWith("\\"))
			webpageDirectoryName = webpageDirectoryName.substring(1);

		relativePathToView = webpageDirectoryName + "/" + controller.toLowerCase();
		
		viewFileName = isEmpty(viewExtension)?action:(action + viewExtension);
	}

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();
		templateProps.put("app_name", getProperty("app.name"));
		templateProps.put("controller", controller);
		templateProps.put("action", action);
		templateProps.put("view_path", relativePathToView);
		templateProps.put("view_file", viewFileName);
		return templateProps;
	}
	
	protected String getRootPath() {
		return getProperty("app.path").toString();
	}

	protected String getRelativePathToOutputFile() {
		return relativePathToView;
	}

	protected String getOutputFileName() {
		return viewFileName;
	}
}