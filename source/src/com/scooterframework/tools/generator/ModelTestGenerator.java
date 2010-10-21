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
 * This class generates model test class code.
 *
 * @author (Fei) John Chen
 */
public class ModelTestGenerator extends ModelGenerator {

	public ModelTestGenerator(String templateFilePath, Map props, String model) {
		super(templateFilePath, props, model);
	}

	protected String getRelativePathToOutputFile() {
		return (noPrefix)?(DIRECTORY_NAME_TEST + File.separatorChar + UNIT_TEST):
					(DIRECTORY_NAME_TEST + File.separatorChar + 
							UNIT_TEST + File.separatorChar +
							packageName.replace('.', File.separatorChar));
	}

	protected String getOutputFileName() {
		return modelClassName + "Test" + FILE_EXTENSION_JAVA;
	}
}