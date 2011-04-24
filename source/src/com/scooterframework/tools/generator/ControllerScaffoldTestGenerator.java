/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.io.File;
import java.util.Map;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerScaffoldTestGenerator extends ControllerScaffoldGenerator {

	public ControllerScaffoldTestGenerator(String templateFilePath,
			Map<String, String> props, String connectionName,
			String controller, String model) {
		super(templateFilePath, props, connectionName, controller, model);
	}

	@Override
	protected String getRelativePathToOutputFile() {
		return (noPrefix)?(DIRECTORY_NAME_TEST + File.separatorChar + FUNCTIONAL_TEST):
					(DIRECTORY_NAME_TEST + File.separatorChar + 
							FUNCTIONAL_TEST + File.separatorChar +
							packageName.replace('.', File.separatorChar));
	}

	@Override
	protected String getOutputFileName() {
		return controllerClassName + "Test" + FILE_EXTENSION_JAVA;
	}
}