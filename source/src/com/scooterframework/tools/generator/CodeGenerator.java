/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.tools.common.AbstractGenerator;
import com.scooterframework.tools.common.AmbiguousAppNameException;
import com.scooterframework.tools.common.Generator;
import com.scooterframework.tools.common.ToolsUtil;

/**
 * CodeGenerator generates code for complete CRUD operations. 
 * 
 * <p>
 * Usage examples:
 * <pre>
	Usage:
	    java -jar tools/generate.jar [app_path] scaffold {model_name_in_singular_form}[@connection_name]
	    java -jar tools/generate.jar [app_path] scaffold-ajax {model_name_in_singular_form}[@connection_name]
	    java -jar tools/generate.jar [app_path] controller controller_name_in_plural_form [method] [method] ...
	    java -jar tools/generate.jar [app_path] model {model_name_in_singular_form}[@connection_name]
	    
	Options:
		-Dnoenhance       -- Do not allow byte code enhancement. Default is allowed.
		-Dtable=YourTable -- Explicitly specify a table name for the model.
	
	Examples:
	    This page:
	        java -jar tools/generate.jar -help
	
	    Generate scaffold code for blog app's domain named post:
	        java -jar tools/generate.jar blog scaffold post
	
	    Generate ajax scaffold code for blog app's domain named post:
	        java -jar tools/generate.jar blog scaffold-ajax post
	
	    Generate scaffold code for blog app's domain named post based on blog_oracle database:
	        java -jar tools/generate.jar blog scaffold post@blog_oracle
	
	    Generate scaffold code for blog app's domain named post which maps to table message:
	        java -Dtable=message -jar tools/generate.jar blog scaffold post
	
	    Generate scaffold code for blog app's domain named post without allowing enhancement:
	        java -Dnoenhance -jar tools/generate.jar blog scaffold post
	
	    Generate scaffold code of post model for blog app in user home directory:
	        java -jar tools/generate.jar /home/john/projects/blog scaffold post
	
	    Generate scaffold code for blog app's domain named post when blog is the only app under webapps:
	        java -jar tools/generate.jar scaffold post
	
	    Generate controller code for blog app's domain post with index and show actions:
	        java -jar tools/generate.jar blog controller posts index show
	
	    Generate controller code for domain post with index and show actions when blog is the only app under webapps:
	        java -jar tools/generate.jar controller posts index show
	
	    Generate model code for blog app's domain post:
	        java -jar tools/generate.jar blog model post
	
	    Generate model code for domain post when blog is the only app under webapps:
	        java -jar tools/generate.jar model post
 * </pre>
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class CodeGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) {
    	if (args.length < 1 || args[0].equalsIgnoreCase("-help")) {
			usage();
			return;
		}
		
		try {
			doTheWork(args);
		}
		catch(AmbiguousAppNameException ax) {
			log("ERROR ERROR ERROR: " + ax.getMessage());
		}
		catch(Throwable ex) {
			ex.printStackTrace();
			log("ERROR ERROR ERROR: " + ex.getMessage());
		}

		if (AbstractGenerator.frameworkInitiated) 
			ApplicationConfig.configInstanceForApp().endApplication();
    }

    private static void doTheWork(String[] args) throws Throwable {
        String scooterHome = ToolsUtil.setSystemProperty("scooter.home", ToolsUtil.detectRootPath());
    	String defaultWebappsName = ToolsUtil.setSystemProperty("webapps.name", "webapps");
    	
    	boolean useImplicitAppName = false;
    	String appName = "";
		String webappsPath = "";
		String webappPath = "";
		String firstArg = args[0];
		if (isTypeName(firstArg)) {
	        webappsPath = scooterHome + File.separator + defaultWebappsName;
			appName = ToolsUtil.detectImplicitAppName(webappsPath);
	        webappPath = webappsPath + File.separator + appName;
	        useImplicitAppName = true;
		}
		else {
			if (ToolsUtil.containsPath(firstArg)) {
				String[] ss = ToolsUtil.getPathAndName(firstArg);
				webappsPath = ss[0];
				webappPath  = ss[1];
				appName     = ss[2];
			}
			else {
				appName = firstArg;
		        webappsPath = scooterHome + File.separator + defaultWebappsName;
		        webappPath = webappsPath + File.separator + appName;
			}
		}
		appName = appName.toLowerCase();
        System.setProperty("app.name", appName);
        
        webappsPath = ToolsUtil.setSystemProperty("webapps.path", webappsPath);
        ToolsUtil.validatePathExistence(webappsPath);
        
        webappPath = ToolsUtil.setSystemProperty("app.path", webappPath);
        ToolsUtil.validatePathExistence(webappPath);
        
    	String appLogs = ToolsUtil.setSystemProperty("app.logs", (scooterHome + File.separator + "logs"));
        ToolsUtil.validatePathExistence(appLogs);
        
        String classFiles = webappPath + File.separator + "WEB-INF" + File.separator + "classes";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_CLASSFILE, classFiles);
        ToolsUtil.validatePathExistence(classFiles);
        
        String propertyFiles = webappPath + File.separator + "WEB-INF" + File.separator + "config";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PROPERTYFILE, propertyFiles);
        ToolsUtil.validatePathExistence(propertyFiles);
        
        String sourceFiles = webappPath + File.separator + "WEB-INF" + File.separator + "src";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_SOURCEFILE, sourceFiles);
        ToolsUtil.validatePathExistence(sourceFiles);
        
        String referenceFiles = scooterHome + File.separator + "lib";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_REFERENCEFILE, referenceFiles);
        ToolsUtil.validatePathExistence(referenceFiles);
        
        String pluginFiles = scooterHome + File.separator + "plugins";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PLUGINFILE, pluginFiles);
        ToolsUtil.validatePathExistence(pluginFiles);
        
    	String targetDir = webappPath;

		log("Generating code for app named " + appName + ":");
    	log("scooter.home: " + scooterHome);
    	log("Target dir: " + targetDir);
    	
    	//create all properties
    	Map<String, String> allProps = new HashMap<String, String>();
    	allProps.put("scooter.home", scooterHome);
    	allProps.put("webapps.path", webappsPath);
    	allProps.put("app.path", webappPath);
    	allProps.put("app.name", appName);
    	
    	String templateRoot = scooterHome + File.separator + 
							"source" + File.separator + 
							"templates";

		if ((useImplicitAppName && (args.length < 2))
				|| (!useImplicitAppName && (args.length < 3))) {
			usage();
			return;
		}
		
		String[] s3 = getCCM(useImplicitAppName, args);
		String connName = s3[0];
		String controller = s3[1];
		String model = s3[2];
		
		String noenhanceStr = System.getProperty("noenhance");
		boolean enhance = ("".equals(noenhanceStr))?false:true;
		String table = System.getProperty("table");
		
		String typeName = getTypeName(useImplicitAppName, args);
		if ("scaffold".equals(typeName)) {
			//generate application controller
	    	String cagPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ApplicationController.tmpl";
			Generator cag = new ControllerApplicationGenerator(cagPath, allProps);
			cag.generate(false);

			//generate controller
	    	String csgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffold.tmpl";
			Generator csg = new ControllerScaffoldGenerator(csgPath, allProps, connName, controller, model, table);
			csg.generate();
			
	    	String cstgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffoldTest.tmpl";
			Generator cstg = new ControllerScaffoldTestGenerator(cstgPath, allProps, connName, controller, model, table);
			cstg.generate();

			//generate model
			if (!enhance) {
		    	String mgPath = templateRoot + File.separator + 
									"model" + File.separator + 
									"ModelHelper.tmpl";
				Generator mcg = new ModelHelperGenerator(mgPath, allProps, connName, model, table, enhance);
				mcg.generate();
			}
			
	    	String mgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"Model.tmpl";
			Generator mg = new ModelGenerator(mgPath, allProps, connName, model, table, enhance);
			mg.generate();
			
	    	String mtgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"ModelTest.tmpl";
			Generator mtg = new ModelTestGenerator(mtgPath, allProps, connName, model, table, enhance);
			mtg.generate();

			//generate views

	    	String vigPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold" + File.separator + 
								"index.tmpl";
			Generator vig = new ViewIndexGenerator(vigPath, allProps, connName, controller, model, table);
			vig.generate();

	    	String vsgPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold" + File.separator + 
								"show.tmpl";
			Generator vsg = new ViewShowGenerator(vsgPath, allProps, connName, controller, model, table);
			vsg.generate();

	    	String vagPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold" + File.separator + 
								"add.tmpl";
			Generator vag = new ViewAddGenerator(vagPath, allProps, connName, controller, model, table);
			vag.generate();

	    	String vegPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold" + File.separator + 
								"edit.tmpl";
			Generator veg = new ViewEditGenerator(vegPath, allProps, connName, controller, model, table);
			veg.generate();

	    	String vpgPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold" + File.separator + 
								"paged_list.tmpl";
			Generator vpg = new ViewPagedGenerator(vpgPath, allProps, connName, controller, model, table);
			vpg.generate();

			//update resources
			Generator rg = new ResourceGenerator(allProps, controller);
			rg.generate();
		} else if ("scaffold-ajax".equals(typeName)) {
			//generate application controller
	    	String cagPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ApplicationController.tmpl";
			Generator cag = new ControllerApplicationGenerator(cagPath, allProps);
			cag.generate(false);

			//generate controller
	    	String csgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffoldAjax.tmpl";
			Generator csg = new ControllerScaffoldGenerator(csgPath, allProps, connName, controller, model, table);
			csg.generate();
			
	    	String cstgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffoldTest.tmpl";
			Generator cstg = new ControllerScaffoldTestGenerator(cstgPath, allProps, connName, controller, model, table);
			cstg.generate();

			//generate model
			if (!enhance) {
		    	String mgPath = templateRoot + File.separator + 
									"model" + File.separator + 
									"ModelHelper.tmpl";
				Generator mcg = new ModelHelperGenerator(mgPath, allProps, connName, model, table, enhance);
				mcg.generate();
			}
			
	    	String mgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"Model.tmpl";
			Generator mg = new ModelGenerator(mgPath, allProps, connName, model, table, enhance);
			mg.generate();
			
	    	String mtgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"ModelTest.tmpl";
			Generator mtg = new ModelTestGenerator(mtgPath, allProps, connName, model, table, enhance);
			mtg.generate();

			//generate views

	    	String vigPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold-ajax" + File.separator + 
								"index.tmpl";
			Generator vig = new ViewIndexGenerator(vigPath, allProps, connName, controller, model, table);
			vig.generate();

	    	String vsgPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold-ajax" + File.separator + 
								"show.tmpl";
			Generator vsg = new ViewShowGenerator(vsgPath, allProps, connName, controller, model, table);
			vsg.generate();

	    	String vagPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold-ajax" + File.separator + 
								"add.tmpl";
			Generator vag = new ViewAddGenerator(vagPath, allProps, connName, controller, model, table);
			vag.generate();

	    	String vegPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold-ajax" + File.separator + 
								"edit.tmpl";
			Generator veg = new ViewEditGenerator(vegPath, allProps, connName, controller, model, table);
			veg.generate();

	    	String vpgPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"scaffold-ajax" + File.separator + 
								"paged_list.tmpl";
			Generator vpg = new ViewPagedGenerator(vpgPath, allProps, connName, controller, model, table);
			vpg.generate();

			//update resources
			Generator rg = new ResourceGenerator(allProps, controller);
			rg.generate();
		}
		else if ("controller".equals(typeName)) {
			//generate application controller
	    	String cagPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ApplicationController.tmpl";
			Generator cag = new ControllerApplicationGenerator(cagPath, allProps);
			cag.generate(false);

			controller = getControllerName(useImplicitAppName, args, false);
			String[] actions = null;
			if (useImplicitAppName) {
				if (args.length == 2) {
					actions = new String[1];
					actions[0] = "index";
				}
				else {
					actions = new String[args.length-2];
					System.arraycopy(args, 2, actions, 0, actions.length);
				}
			}
			else {
				if (args.length == 3) {
					actions = new String[1];
					actions[0] = "index";
				}
				else {
					actions = new String[args.length-3];
					System.arraycopy(args, 3, actions, 0, actions.length);
				}
			}

			
	    	String cgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ActionController.tmpl";
			Generator cg = new ControllerGenerator(cgPath, allProps, controller, actions);
			cg.generate();

	    	String ctgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ActionControllerTest.tmpl";
			Generator ctg = new ControllerTestGenerator(ctgPath, allProps, controller, actions);
			ctg.generate();

			//generate view
	    	String vigPath = templateRoot + File.separator + 
								"view" + File.separator + 
								"action.tmpl";
			int views = actions.length;
			Generator vig = null;
			for (int i = 0; i < views; i++) {
				vig = new ViewActionGenerator(vigPath, allProps, controller, actions[i]);
				vig.generate();
			}
		}
		else if ("model".equals(typeName)) {
			if (!enhance) {
		    	String mgPath = templateRoot + File.separator + 
									"model" + File.separator + 
									"ModelHelper.tmpl";
				Generator mcg = new ModelHelperGenerator(mgPath, allProps, connName, model, table, enhance);
				mcg.generate();
			}
			
	    	String mgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"Model.tmpl";
			Generator mg = new ModelGenerator(mgPath, allProps, connName, model, table, enhance);
			mg.generate();
			
	    	String mtgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"ModelTest.tmpl";
			Generator mtg = new ModelTestGenerator(mtgPath, allProps, connName, model, table, enhance);
			mtg.generate();
		}
		else {
			throw new Exception("Type name \"" + typeName + "\" is not supported.");
		}
	}
    
    private static boolean isTypeName(String s) {
    	s = s.toLowerCase();
    	return "scaffold".equals(s) || "scaffold-ajax".equals(s) || "controller".equals(s) || "model".equals(s) || "model2".equals(s);
    }
    
    private static String getTypeName(boolean useImplicitAppName, String[] args) {
    	String typeName = (useImplicitAppName)?args[0]:args[1];
    	if (!isTypeName(typeName)) {
    		throw new IllegalArgumentException("Type name \"" + typeName + "\" is not supported.");
    	}
    	return typeName;
    }
    
    private static String getControllerName(boolean useImplicitAppName, String[] args, boolean plural) {
    	String controllerName = (useImplicitAppName)?args[1]:args[2];
    	int index = controllerName.indexOf('@');
    	if (index != -1) {
    		controllerName = controllerName.substring(0, index);
    	}
    	if (plural) controllerName = WordUtil.pluralize(controllerName);
    	return controllerName;
    }
    
    private static String[] getCCM(boolean useImplicitAppName, String[] args) {
    	String connectionName = null;
    	String controllerName = null;
    	String modelName = null;
    	String s = (useImplicitAppName)?args[1]:args[2];
    	int index = s.indexOf('@');
    	if (index != -1) {
    		String s1 = s.substring(0, index);
    		String s2 = s.substring(index + 1);
    		connectionName = s2;
    		controllerName = s1;
    		modelName = s1;
    	}
    	else {
    		controllerName = s;
    		modelName = s;
    	}
    	
    	if (controllerName.indexOf('.') != -1) {
    		controllerName = controllerName.replace('.', '_');
    		controllerName = WordUtil.camelize(controllerName).toLowerCase();
    	}

		controllerName = WordUtil.pluralize(controllerName);
		modelName = enforceSingle(modelName);
		
		String[] s3 = new String[3];
		s3[0] = connectionName;
		s3[1] = controllerName;
		s3[2] = modelName;
    	return s3;
    }

	private static String enforceSingle(String word) {
		if (word.equals(WordUtil.pluralize(word))) {
			String sword = WordUtil.singularize(word);
			log(word + " is a plural word. Its singularized form \"" + sword +
					"\" is used to generate code. Use \"additional.single.plural\" " +
					"property in environment.properties file if you want to use " +
					"a different singularized word.");
			word = sword;
		}
		return word;
	}

    private static void log(Object o) {
		System.out.println(o);
    }

    private static void usage() {
    	log("Summary:");
    	log("    This utility generates codes.");
    	log("");
    	log("Usage:");
    	log("    java -jar tools/generate.jar [app_path] scaffold {model_name_in_singular_form}[@connection_name]");
    	log("    java -jar tools/generate.jar [app_path] scaffold-ajax {model_name_in_singular_form}[@connection_name]");
    	log("    java -jar tools/generate.jar [app_path] controller controller_name_in_plural_form [method] [method] ...");
    	log("    java -jar tools/generate.jar [app_path] model {model_name_in_singular_form}[@connection_name]");
    	log("");
    	log("Options:");
    	log("    -Dnoenhance       -- Do not allow byte code enhancement. Default is allowed.");
    	log("    -Dtable=YourTable -- Explicitly specify a table name for the model.");
    	log("");
    	log("Examples:");
    	log("    This page:");
    	log("        java -jar tools/generate.jar -help");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post based on default db connection:");
    	log("        java -jar tools/generate.jar blog scaffold post");
    	log("");
    	log("    Generate ajax scaffold code for blog app's domain named post:");
    	log("        java -jar tools/generate.jar blog scaffold-ajax post");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post based on blog_oracle database:");
    	log("        java -jar tools/generate.jar blog scaffold post@blog_oracle");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post which maps to table message:");
    	log("        java -Dtable=message -jar tools/generate.jar blog scaffold post");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post without allowing enhancement:");
    	log("        java -Dnoenhance -jar tools/generate.jar blog scaffold post");
	    log("");
    	log("    Generate scaffold code of post model for blog app in user home directory:");
    	log("        java -jar tools/generate.jar /home/john/projects/blog scaffold post");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post when blog is the only app under webapps:");
    	log("        java -jar tools/generate.jar scaffold post");
    	log("");
    	log("    Generate controller code for blog app's domain post with index and show actions:");
    	log("        java -jar tools/generate.jar blog controller posts index show");
    	log("");
    	log("    Generate controller code for domain post with index and show actions when blog is the only app under webapps:");
    	log("        java -jar tools/generate.jar controller posts index show");
    	log("");
    	log("    Generate model code for blog app's domain post:");
    	log("        java -jar tools/generate.jar blog model post");
    	log("");
    	log("    Generate model code for domain post when blog is the only app under webapps:");
    	log("        java -jar tools/generate.jar model post");
    	log("");
    }
}