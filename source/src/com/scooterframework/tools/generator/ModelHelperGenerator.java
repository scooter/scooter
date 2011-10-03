/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.util.Map;

/**
 * This class generates model class code.
 *
 * @author (Fei) John Chen
 */
public class ModelHelperGenerator extends ModelGenerator {
	public ModelHelperGenerator(String templateFilePath, Map<String, String> props,
			String connName, String model, String table, boolean enhance) {
		super(templateFilePath, props, connName, model, table, enhance);
	}

	@Override
	protected String getOutputFileName() {
		return superClassName + FILE_EXTENSION_JAVA;
	}
}