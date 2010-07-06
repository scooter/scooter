/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.util.Properties;

import com.scooterframework.common.util.GeneratorImpl;

/**
 * This class generates controller class code.
 * 
 * @author (Fei) John Chen
 */
public class ControllerApplicationGenerator extends GeneratorImpl {
	private String packageName;
	private String controllerClassName;
	private boolean noPrefix;
	private Properties props;
	
	public ControllerApplicationGenerator(Properties props) {
		this.props = props;
		String controllerNameCamel = "Application";
		String classPrefix = props.getProperty("package_prefix") + ".controllers";
		String classSuffix = "Controller";
		noPrefix = isEmpty(classPrefix);
		if (!noPrefix) packageName = classPrefix;
		boolean noSuffix = isEmpty(classSuffix);
		controllerClassName = (noSuffix)?controllerNameCamel:(controllerNameCamel + classSuffix);
	}
	
	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		if (!noPrefix) {
			tpl.append("package {package_name};").append(linebreak);
			tpl.append("").append(linebreak);
		}
		tpl.append("import com.scooterframework.common.logging.LogUtil;").append(linebreak);
		tpl.append("import com.scooterframework.web.controller.ActionControl;").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("/**").append(linebreak);
		tpl.append(" * {controller_class_name} class has methods that are available to all subclass").append(linebreak);
		tpl.append(" * controllers. This is a place to add application-wide action methods and filters.").append(linebreak);
		tpl.append(" */").append(linebreak);
		tpl.append("public class {controller_class_name} extends ActionControl {").append(linebreak);
		tpl.append("    //").append(linebreak);
		tpl.append("    // Add more application-wide methods/filters here.").append(linebreak);
		tpl.append("    //").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("    /**").append(linebreak);
		tpl.append("     * Declares a <tt>log</tt> instance that are available to all subclasses.").append(linebreak);
		tpl.append("     */").append(linebreak);
		tpl.append("    protected LogUtil log = LogUtil.getLogger(getClass().getName());").append(linebreak);
		tpl.append("}").append(linebreak);
		return tpl.toString();
	}
	
	protected Properties getTemplateProperties() {
		Properties templateProps = new Properties();
		
		templateProps.setProperty("package_name", packageName);
		templateProps.setProperty("controller_class_name", controllerClassName);
		
		return templateProps;
	}
	
	protected String getRootPath() {
		return props.getProperty("scooter.home") + File.separator + "webapps" +
						File.separator + props.getProperty("app_name") + 
						File.separator + "WEB-INF";
	}
	
	protected String getRelativePathToOutputFile() {
		return (noPrefix)?sourceDirName:
					(sourceDirName + File.separatorChar + 
					packageName.replace('.', File.separatorChar));
	}
	
	protected String getOutputFileName() {
		return controllerClassName + javaFileExtension;
	}
}