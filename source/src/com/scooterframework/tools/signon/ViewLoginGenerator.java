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
 * This class generates login view code.
 *
 * @author (Fei) John Chen
 */
public class ViewLoginGenerator extends AbstractGenerator {
	private String controller;
	private String action;
	private String relativePathToView;
	private String viewFileName;

	public ViewLoginGenerator(String templateFilePath, Map<String, String> props, String controller, String action) {
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

	@Override
	protected Map<String, String> getTemplateProperties() {
		Map<String, String> templateProps = new HashMap<String, String>();
		templateProps.put("controller", controller);
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