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

import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates built-in header view code.
 *
 * @author (Fei) John Chen
 */
public class SecureHeaderGenerator extends AbstractGenerator {
	private String relativePathToView;
	private String viewFileName;

	public SecureHeaderGenerator(String templateFilePath, Map props) {
		super(templateFilePath, props);
		
		String headerName = "header";
		
		String viewExtension = wc.getViewExtension();
		if (viewExtension != null && !viewExtension.startsWith(".")) viewExtension = "." + viewExtension;

		String webpageDirectoryName = wc.getWebPageDirectoryName();
		if (webpageDirectoryName.startsWith("/") || webpageDirectoryName.startsWith("\\"))
			webpageDirectoryName = webpageDirectoryName.substring(1);

		relativePathToView = webpageDirectoryName + "/layouts/includes";
		
		viewFileName = isEmpty(viewExtension)?headerName:(headerName + viewExtension);
	}

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();
		templateProps.put("app_name", WordUtil.titleize(getProperty("app.name").toString()));
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