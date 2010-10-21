/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.signon;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates logout view code.
 *
 * @author (Fei) John Chen
 */
public class ViewLogoutGenerator extends AbstractGenerator {
	private String controller;
	private String action;
	private String relativePathToView;
	private String viewFileName;

	public ViewLogoutGenerator(String templateFilePath, Map props, String controller, String action) {
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
		templateProps.put("controller", controller);
		templateProps.put("action", action);
		templateProps.put("app_name", getProperty("app.name"));
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