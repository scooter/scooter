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

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
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
	    java -jar tools/generate.jar [app_path] scaffold model_name_in_singular_form
	    java -jar tools/generate.jar [app_path] controller controller_name_in_plural_form [method] [method] ...
	    java -jar tools/generate.jar [app_path] model model_name_in_singular_form
	
	Examples:
	    This page:
	        java -jar tools/generate.jar -help
	
	    Generate scaffold code for blog app's domain named post:
	        java -jar tools/generate.jar blog scaffold post
	
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
        System.setProperty(Constants.SKIP_CLASSWORK, Constants.SKIP_CLASSWORK_TRUE);
        
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
        
        webappPath = ToolsUtil.setSystemProperty("app.path", webappPath);
        
    	String appLogs = ToolsUtil.setSystemProperty("app.logs", (scooterHome + File.separator + "logs"));
        
        String classFiles = webappPath + File.separator + "WEB-INF" + File.separator + "classes";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_CLASSFILE, classFiles);
        
        String propertyFiles = webappPath + File.separator + "WEB-INF" + File.separator + "config";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PROPERTYFILE, propertyFiles);
        
        String sourceFiles = webappPath + File.separator + "WEB-INF" + File.separator + "src";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_SOURCEFILE, sourceFiles);
        
        String referenceFiles = scooterHome + File.separator + "lib";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_REFERENCEFILE, referenceFiles);
        
    	String targetDir = webappPath;

		log("Generating code for app named " + appName + ":");
    	log("scooter.home: " + scooterHome);
    	log("Target dir: " + targetDir);
    	
    	//create all properties
    	Properties allProps = new Properties();
    	allProps.setProperty("scooter.home", scooterHome);
    	allProps.setProperty("webapps.path", webappsPath);
    	allProps.setProperty("app.path", webappPath);
    	allProps.setProperty("app.name", appName);
    	
    	String templateRoot = scooterHome + File.separator + 
							"source" + File.separator + 
							"templates";
		
		String typeName = getTypeName(useImplicitAppName, args);
		if ("scaffold".equals(typeName)) {
			if ((useImplicitAppName && (args.length < 2)) || 
			   (!useImplicitAppName && (args.length < 3))) {
				usage();
				return;
			}

			//generate application controller
			//Generator cag = new ControllerApplicationGenerator();
			//cag.generate(false);

			//generate controller
	    	String csgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffold.tmpl";
			String model = enforceSingle(getName(useImplicitAppName, args));
			Generator csg = new ControllerScaffoldGenerator(csgPath, allProps, model);
			csg.generate();
			
	    	String cstgPath = templateRoot + File.separator + 
								"controller" + File.separator + 
								"ControllerScaffoldTest.tmpl";
			Generator cstg = new ControllerScaffoldTestGenerator(cstgPath, allProps, model);
			cstg.generate();

			//generate model
	    	String mgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"Model.tmpl";
			Generator mg = new ModelGenerator(mgPath, allProps, model);
			mg.generate();
			
	    	String mtgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"ModelTest.tmpl";
			Generator mtg = new ModelTestGenerator(mtgPath, allProps, model);
			mtg.generate();

			//generate views
			String controller = WordUtil.pluralize(model);
			Generator vig = new ViewIndexGenerator(allProps, controller, model);
			vig.generate();
			Generator vsg = new ViewShowGenerator(allProps, controller, model);
			vsg.generate();
			Generator vag = new ViewAddGenerator(allProps, controller, model);
			vag.generate();
			Generator veg = new ViewEditGenerator(allProps, controller, model);
			veg.generate();
			Generator vpg = new ViewPagedGenerator(allProps, controller, model);
			vpg.generate();

			//update resources
			Generator rg = new ResourceGenerator(allProps, controller);
			rg.generate();
		}
		else if ("controller".equals(typeName)) {
			if ((useImplicitAppName && (args.length < 2)) || 
			   (!useImplicitAppName && (args.length < 3))) {
				usage();
				return;
			}

			//generate application controller
			//Generator cag = new ControllerApplicationGenerator();
			//cag.generate(false);

			String controller = getName(useImplicitAppName, args);
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
			
			Generator cg = new ControllerGenerator(allProps, controller, actions);
			cg.generate();
			
			Generator ctg = new ControllerTestGenerator(allProps, controller, actions);
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
			if ((useImplicitAppName && (args.length < 2)) || 
			   (!useImplicitAppName && (args.length < 3))) {
				usage();
				return;
			}

			String model = enforceSingle(getName(useImplicitAppName, args));
			
	    	String mgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"Model.tmpl";
			Generator mg = new ModelGenerator(mgPath, allProps, model);
			mg.generate();
			
	    	String mtgPath = templateRoot + File.separator + 
								"model" + File.separator + 
								"ModelTest.tmpl";
			Generator mtg = new ModelTestGenerator(mtgPath, allProps, model);
			mtg.generate();
		}
		else {
			throw new Exception("Type name \"" + typeName + "\" is not supported.");
		}
	}
    
    private static boolean isTypeName(String s) {
    	s = s.toLowerCase();
    	return "scaffold".equals(s) || "controller".equals(s) || "model".equals(s);
    }
    
    private static String getTypeName(boolean useImplicitAppName, String[] args) {
    	String typeName = (useImplicitAppName)?args[0]:args[1];
    	if (!isTypeName(typeName)) {
    		throw new IllegalArgumentException("Type name \"" + typeName + "\" is not supported.");
    	}
    	return typeName;
    }
    
    private static String getName(boolean useImplicitAppName, String[] args) {
    	return (useImplicitAppName)?args[1]:args[2];
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
    	log("    This is a utility that generates codes.");
    	log("");
    	log("Usage:");
    	log("    java -jar tools/generate.jar [app_path] scaffold model_name_in_singular_form");
    	log("    java -jar tools/generate.jar [app_path] controller controller_name_in_plural_form [method] [method] ...");
    	log("    java -jar tools/generate.jar [app_path] model model_name_in_singular_form");
    	log("");
    	log("Examples:");
    	log("    This page:");
    	log("        java -jar tools/generate.jar -help");
    	log("");
    	log("    Generate scaffold code for blog app's domain named post:");
    	log("        java -jar tools/generate.jar blog scaffold post");
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