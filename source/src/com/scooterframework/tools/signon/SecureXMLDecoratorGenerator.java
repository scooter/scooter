/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.signon;

import java.util.Map;

import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates decorators.xml code.
 *
 * @author (Fei) John Chen
 */
public class SecureXMLDecoratorGenerator extends AbstractGenerator {
	
	public SecureXMLDecoratorGenerator(String templateFilePath, Map<String, String> props) {
		super(templateFilePath, props);
	}

	protected Map<String, String> getTemplateProperties() {
		return null;
	}
	
	@Override
	protected String getRootPath() {
		return getProperty("app.path");
	}

	@Override
	protected String getRelativePathToOutputFile() {
		return "/WEB-INF";
	}

	@Override
	protected String getOutputFileName() {
		return "decorators.xml";
	}
}