/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.io.File;
import java.util.Properties;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ControllerScaffoldTestGenerator extends ControllerScaffoldGenerator {

	public ControllerScaffoldTestGenerator(String templateFilePath, Properties props, String model) {
		super(templateFilePath, props, model);
	}

	protected String getRelativePathToOutputFile() {
		return (noPrefix)?(DIRECTORY_NAME_TEST + File.separatorChar + FUNCTIONAL_TEST):
					(DIRECTORY_NAME_TEST + File.separatorChar + 
							FUNCTIONAL_TEST + File.separatorChar +
							packageName.replace('.', File.separatorChar));
	}

	protected String getOutputFileName() {
		return controllerClassName + "Test" + FILE_EXTENSION_JAVA;
	}
}