/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.PropertyFileUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.PrimaryKey;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * Resource class
 * 
 * @author (Fei) John Chen
 */
public class Resource {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    /**
     * Constant which indicates that this is a plural resource.
     */
    public static final int PLURAL = 0;
    
    /**
     * Constant which indicates that this is a single resource.
     */
    public static final int SINGLE = 1;

    private static String[] standardRestfulActionNames = 
                                {RouteConstants.ROUTE_ACTION_LIST_RESOURCES, 
                                 RouteConstants.ROUTE_ACTION_ADD_RESOURCE, 
                                 RouteConstants.ROUTE_ACTION_CREATE_RESOURCE, 
                                 RouteConstants.ROUTE_ACTION_READ_RESOURCE, 
                                 RouteConstants.ROUTE_ACTION_EDIT_RESOURCE, 
                                 RouteConstants.ROUTE_ACTION_UPDATE_RESOURCE, 
                                 RouteConstants.ROUTE_ACTION_DELETE_RESOURCE};
	
	protected String name;
    protected int type;
	protected String controller;
	protected String controllerClass;
	protected String singular;
	protected String namespace;
	protected String pathPrefix;
	protected String pathAlias;
	protected String actionAlias;
	protected String only;
	protected String except;
	protected String member;
	protected String collection;
	protected String add;
    protected String requirements;
    protected String parents;
	
    private String model;
    private String implicitPathPrefix;
    private boolean strictParent = false;
    private boolean autoRoute;
    
	private Properties actionAliasProperties;
    private List routes = new ArrayList();
	
    /**
     * Creates a resource instance of either single or plural type. 
     * 
	 * @param name Name of the resource 
	 * @param type Either SINGLE or PLURAL
     */
	public Resource(String name, int type) {
		this(name, type, new Properties());
	}
	
	/**
	 * Creates a resource instance, but uses <tt>noRoute</tt> parameter to 
	 * indicate if routes of the resource are allowed to be created. 
	 * 
	 * @param name     Name of the resource 
	 * @param noRoutes True if no routes are allowed to create
	 */
	public Resource(String name, boolean noRoutes) {
		this(name, PLURAL, new Properties(), noRoutes, false);
	}
	
	/**
	 * Creates a resource instance of either single or plural type with a 
	 * specific property <tt>p</tt>.
	 * 
	 * @param name Name of the resource 
	 * @param type Either SINGLE or PLURAL
	 * @param p    properties of the resource
	 */
	public Resource(String name, int type, Properties p) {
        this(name, type, p, false, false);
	}
	
	Resource(String name, int type, Properties p, boolean noRoutes, boolean autoRoute) {
		this.name = name;
		this.type = type;
        this.autoRoute = autoRoute;
        
        if (name == null || "".equals(name)) 
            throw new IllegalArgumentException("Name cannot be null or empty " + 
            "when instantiating a Resource.");
        
        if (type != SINGLE && type != PLURAL) 
            throw new IllegalArgumentException("Invalid resource type \"" + type + 
                    "\". Only \"" + PLURAL + "\" (plural) and \"" + SINGLE + 
                    "\" (single) are allowed.");
        
		populateProperties(p);
        
        if (type == SINGLE) {
            if (except == null || 
                except.indexOf(RouteConstants.ROUTE_ACTION_LIST_RESOURCES) == -1) {
                except += " " + RouteConstants.ROUTE_ACTION_LIST_RESOURCES;
            }
        }
        
        if (!noRoutes) createRoutes();
	}
	
	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
    
    public boolean isSingle() {
        return (SINGLE == type);
    }
	
	public String getController() {
		return (controller != null)?controller:name;
	}
	
	public String getControllerClass() {
		return controllerClass;
	}
	
	public String getSingular() {
		return singular;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getPathPrefix() {
		return pathPrefix;
	}
	
	public String getPathAlias() {
		return pathAlias;
	}
    
    //Note: It is important that the returned url must be prefixed with a 
    //slash, because other code depend on it.
    public String getUnprifixedURL() {
        return "/" + ((pathAlias != null)?pathAlias:name);
    }
    
    public String getScreenURLPattern() {
        String url = getUnprifixedURL();
        if (pathPrefix != null) {
            url = pathPrefix + url;
        }
        else {
            if (implicitPathPrefix != null) {
                url = implicitPathPrefix + url;
            }
        }
        return url;
    }
    
    /**
     * Returns url string.
     * 
     * @param fieldValues   name/value pairs to be used to resolve dynamic url.
     * @return a url
     */
    public String getScreenURL(Map fieldValues) {
        return Route.resolveURL(getScreenURLPattern(), fieldValues);
    }
	
	public String getActionAlias() {
		return actionAlias;
	}
	
	public String getOnly() {
		return only;
	}
	
	public String getExcept() {
		return except;
	}
	
	public String getMember() {
		return member;
	}
	
	public String getCollection() {
		return collection;
	}
	
	public String getAdd() {
		return add;
	}
	
	public String getRequirements() {
		return requirements;
	}
    
    public String getParents() {
        return parents;
    }
    
    public boolean isStrictParent() {
        return strictParent;
    }
    
    public String getModel() {
        return model;
    }
    
    public List getRoutes() {
        return routes;
    }
    
    /**
     * Returns a rest route of the underline resource. The <tt>target</tt> 
     * must a model name if a member route is desired, or a resource name if a 
     * collection route is wanted.
     * 
     * @param action     action name
     * @param target     target name
     * @return resource
     */
    public RestRoute getRestRoute(String action, String target) {
        RestRoute rr = null;
        String routeName = createRestRouteName(action, target);
        Iterator it = routes.iterator();
        while(it.hasNext()) {
            RestRoute tmp = (RestRoute)it.next();
            if (tmp.getName().equals(routeName)) {
                rr = tmp;
                break;
            }
        }
        return rr;
    }
	
	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String SEPARATOR = ", ";
        
        returnString.append("name = " + name).append(SEPARATOR);
        returnString.append("type = " + type).append(SEPARATOR);
        returnString.append("controller = " + controller).append(SEPARATOR);
        returnString.append("controllerClass = " + controllerClass).append(SEPARATOR);
        returnString.append("singular = " + singular).append(SEPARATOR);
        returnString.append("namespace = " + namespace).append(SEPARATOR);
        returnString.append("pathPrefix = " + pathPrefix).append(SEPARATOR);
        returnString.append("pathAlias = " + pathAlias).append(SEPARATOR);
        returnString.append("actionAlias = " + actionAlias).append(SEPARATOR);
        returnString.append("only = " + only).append(SEPARATOR);
        returnString.append("except = " + except).append(SEPARATOR);
        returnString.append("member = " + member).append(SEPARATOR);
        returnString.append("collection = " + collection).append(SEPARATOR);
        returnString.append("add = " + add).append(SEPARATOR);
        returnString.append("requirements = " + requirements).append(SEPARATOR);
        returnString.append("parents = " + parents).append(SEPARATOR);
        returnString.append("strictParent = " + strictParent).append(SEPARATOR);
        returnString.append("implicitPathPrefix = " + implicitPathPrefix).append(SEPARATOR);
        returnString.append("model = " + model);
        
        return returnString.toString();
    }
    
    /**
     * Returns a format string for resource id. For example, for a resource 
     * named "<tt>users</tt>", the resource id is "<tt>user_id</tt>";
     * 
     * @param resource a resource
     * @return a format string for resource id
     */
    public static String getNesteeIdFormat(Resource resource) {
        return resource.getModel() + "_id";
    }
	
	protected void populateProperties(Properties p) {
		controller = p.getProperty(RouteConstants.ROUTE_KEY_CONTROLLER);
		controllerClass = p.getProperty(RouteConstants.ROUTE_KEY_CONTROLLER_CLASS);
		singular = p.getProperty(RouteConstants.ROUTE_KEY_SINGULAR);
		namespace = p.getProperty(RouteConstants.ROUTE_KEY_NAMESPACE);
		pathPrefix = p.getProperty(RouteConstants.ROUTE_KEY_PATH_PREFIX);
		pathAlias = p.getProperty(RouteConstants.ROUTE_KEY_PATH_ALIAS);
        
		actionAlias = p.getProperty(RouteConstants.ROUTE_KEY_ACTION_ALIAS);
        actionAlias = StringUtil.remove(actionAlias, RouteConstants.PROPERTY_SYMBOL_GROUP);
		
		if (actionAlias != null) {
			actionAliasProperties = 
                Converters.convertStringToProperties(actionAlias, 
                        RouteConstants.PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN, 
                        RouteConstants.PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER);
		}
        
		only = p.getProperty(RouteConstants.ROUTE_KEY_ONLY);
        only = StringUtil.remove(only, RouteConstants.PROPERTY_SYMBOL_ARRAY);
        
		except = p.getProperty(RouteConstants.ROUTE_KEY_EXCEPT);
        except = StringUtil.remove(except, RouteConstants.PROPERTY_SYMBOL_ARRAY);
        
        member = p.getProperty(RouteConstants.ROUTE_KEY_MEMBER);
        member = StringUtil.remove(member, RouteConstants.PROPERTY_SYMBOL_GROUP);
        
        collection = p.getProperty(RouteConstants.ROUTE_KEY_COLLECTION);
        collection = StringUtil.remove(collection, RouteConstants.PROPERTY_SYMBOL_GROUP);
        
        add = p.getProperty(RouteConstants.ROUTE_KEY_ADD);
        add = StringUtil.remove(add, RouteConstants.PROPERTY_SYMBOL_GROUP);
        
        requirements = p.getProperty(RouteConstants.ROUTE_KEY_REQUIREMENTS);
        
		parents = p.getProperty(RouteConstants.ROUTE_KEY_PARENTS);
        parents = StringUtil.remove(parents, RouteConstants.PROPERTY_SYMBOL_ARRAY);
        if (parents != null && parents.indexOf(RouteConstants.PROPERTY_SYMBOL_STRICT_PARENT) != -1) {
            if (!parents.startsWith(RouteConstants.PROPERTY_SYMBOL_STRICT_PARENT)) {
                throw new IllegalArgumentException("Resource definition error for \"" + 
                        getName() + "\": " + RouteConstants.PROPERTY_SYMBOL_STRICT_PARENT + 
                        " must be the first word for parents key.");
            }
            else {
                parents = parents.substring(RouteConstants.PROPERTY_SYMBOL_STRICT_PARENT.length()).trim();
                strictParent = true;
            }
        }
        
        //populate derived fields
        if (singular != null) {
            model = singular;
        }
        else {
            model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(name):name;
        }
	}
    
    protected void createRoutes() {
        //
        //parse allowed actions
        //
        String[] allowedActionNames = standardRestfulActionNames;
        if (only != null) {
            allowedActionNames = Converters.convertStringToStringArray(only, RouteConstants.PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER);
        }
        else if (except != null) {
            allowedActionNames = updateActionNamesByExcept(except);
        }
        
        if (allowedActionNames == null || allowedActionNames.length ==0) return;
        
        //
        //Start to create all kinds of routes
        //
        
        //
        //create nested routes
        //
        if (parents != null) {
            String[] parentsArray = Converters.convertStringToStringArray(parents, 
                            RouteConstants.PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER);
            if (parentsArray != null && parentsArray.length > 0) {
                //create all 7 restful nested routes in another method.
                int total = parentsArray.length;
                
                if (isStrictParent() && total > 1) {
                    throw new IllegalArgumentException("Resource definition error for \"" + 
                        getName() + "\": There can be only one parent in strict case.");
                }
                
                String nestedPrefix = "";
                
                for (int j = 0; j < total; j++) {
                    String[] parentResourceNames = Converters.convertStringToStringArray(parentsArray[j], RouteConstants.PROPERTY_SYMBOL_PARENTS_CONNECTION);
                    Resource[] parentResource = getParentResource(parentResourceNames);
                    nestedPrefix = getNestedURLPrefixString(parentResource);
                    String indexURLNested = getNestedURLStringForList(nestedPrefix);
                    String  curdURLNested = getNestedURLStringForCRUD(nestedPrefix);
                    String   addURLNested = getNestedURLStringForADD(nestedPrefix);
                    String  editURLNested = getNestedURLStringForEDIT(nestedPrefix);
                    String[] parentModels = getParentModels(parentResource);
                    int countOfActions = allowedActionNames.length;
                    for (int i = 0; i < countOfActions; i++) {
                        String action = allowedActionNames[i];
                        if (RouteConstants.ROUTE_ACTION_LIST_RESOURCES.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, name), indexURLNested, RouteConstants.ROUTE_HTTP_METHOD_GET));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_ADD_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), addURLNested, RouteConstants.ROUTE_HTTP_METHOD_GET));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_CREATE_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), indexURLNested, RouteConstants.ROUTE_HTTP_METHOD_POST));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_READ_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), curdURLNested, RouteConstants.ROUTE_HTTP_METHOD_GET));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_EDIT_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), editURLNested, RouteConstants.ROUTE_HTTP_METHOD_GET));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_UPDATE_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), curdURLNested, RouteConstants.ROUTE_HTTP_METHOD_PUT));
                        }
                        else 
                        if (RouteConstants.ROUTE_ACTION_DELETE_RESOURCE.equals(action)) {
                            routes.add(createRestRoute(action, getTarget(parentModels, model), curdURLNested, RouteConstants.ROUTE_HTTP_METHOD_DELETE));
                        }
                    }
                }
                
                if (isStrictParent()) implicitPathPrefix = nestedPrefix;
            }
        }
        
        if (strictParent) return;
        
        //
        //create standard REST routes
        //
        String indexURL = getURLStringForList();
        String  curdURL = getURLStringForCRUD(indexURL);
        String   addURL = getURLStringForADD(indexURL);
        String  editURL = getURLStringForEDIT(indexURL);
        
        //
        //create member routes
        //
        Properties memberProperties = 
            PropertyFileUtil.parseNestedPropertiesFromLine(member, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER);
        if (memberProperties != null) {
            Iterator it = memberProperties.keySet().iterator();
            while(it.hasNext()) {
                String action = (String)it.next();
                String methods = memberProperties.getProperty(action);
                methods = StringUtil.remove(methods, RouteConstants.PROPERTY_SYMBOL_ARRAY);
                routes.add(createRestRoute(action, model, curdURL + "/" + action, methods));
            }
        }
        
        //
        //create collection routes
        //
        Properties collectionProperties = 
            PropertyFileUtil.parseNestedPropertiesFromLine(collection, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER);
        if (collectionProperties != null) {
            Iterator it = collectionProperties.keySet().iterator();
            while(it.hasNext()) {
                String action = (String)it.next();
                String methods = collectionProperties.getProperty(action);
                methods = StringUtil.remove(methods, RouteConstants.PROPERTY_SYMBOL_ARRAY);
                routes.add(createRestRoute(action, name, indexURL + "/" + action, methods));
            }
        }
        
        //
        //create add routes
        //
        Properties addProperties = 
            PropertyFileUtil.parseNestedPropertiesFromLine(add, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN, 
                    RouteConstants.PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER);
        if (addProperties != null) {
            Iterator it = addProperties.keySet().iterator();
            while(it.hasNext()) {
                String action = (String)it.next();
                String methods = addProperties.getProperty(action);
                methods = StringUtil.remove(methods, RouteConstants.PROPERTY_SYMBOL_ARRAY);
                routes.add(createRestRoute(action, model, indexURL + "/" + action, methods));
            }
        }
        
        //
        //create the seven standard rest routes
        //
        int count = allowedActionNames.length;
        for (int i = 0; i < count; i++) {
            String action = allowedActionNames[i];
            if (RouteConstants.ROUTE_ACTION_LIST_RESOURCES.equals(action)) {
                routes.add(createRestRoute(action, name, indexURL, RouteConstants.ROUTE_HTTP_METHOD_GET));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_ADD_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, addURL, RouteConstants.ROUTE_HTTP_METHOD_GET));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_CREATE_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, indexURL, RouteConstants.ROUTE_HTTP_METHOD_POST));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_READ_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, curdURL, RouteConstants.ROUTE_HTTP_METHOD_GET));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_EDIT_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, editURL, RouteConstants.ROUTE_HTTP_METHOD_GET));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_UPDATE_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, curdURL, RouteConstants.ROUTE_HTTP_METHOD_PUT));
            }
            else 
            if (RouteConstants.ROUTE_ACTION_DELETE_RESOURCE.equals(action)) {
                routes.add(createRestRoute(action, model, curdURL, RouteConstants.ROUTE_HTTP_METHOD_DELETE));
            }
        }
    }
    
    private String[] updateActionNamesByExcept(String except) {
        int length = standardRestfulActionNames.length;
        String allowedActions = "";
        for (int i = 0; i < length; i++) {
            String action = standardRestfulActionNames[i];
            if (except.indexOf(action) == -1) {
                allowedActions += action + " ";
            }
        }
        return Converters.convertStringToStringArray(allowedActions, " ");
    }
    
    private Route createRestRoute(String action, String target, String url, String methodsAllowed) {
        Route.validateMethods(methodsAllowed);
        
        Properties p = new Properties();
        p.put(RouteConstants.ROUTE_KEY_URL, url);
        p.put(RouteConstants.ROUTE_KEY_CONTROLLER, getController());
        p.put(RouteConstants.ROUTE_KEY_ACTION, action);
        p.put(RouteConstants.ROUTE_KEY_ALLOWED_METHODS, methodsAllowed);
        
        if (getSingular() != null) {
            p.put(RouteConstants.ROUTE_KEY_SINGULAR, getSingular());
        }
        
        if (getNamespace() != null) {
            p.put(RouteConstants.ROUTE_KEY_NAMESPACE, getNamespace());
        }
        
        if (getControllerClass() != null) {
            p.put(RouteConstants.ROUTE_KEY_CONTROLLER_CLASS, getControllerClass());
        }
        
        if (getRequirements() != null) {
            p.put(RouteConstants.ROUTE_KEY_REQUIREMENTS, requirements);
        }
        
        String routeName = createRestRouteName(action, target);
        RestRoute rr = new RestRoute(routeName, p);
        rr.setResourceName(this.name);
        rr.setModel(this.model);
        return rr;
    }
    
    
    
    private String getURLStringForList() {
        return getScreenURLPattern();
    }
    
    private String getURLStringForCRUD(String indexURL) {
        return (!isSingle())?indexURL + "/" + 
            ((autoRoute)?getDynamicPrimaryKeyRepresentation():RouteConstants.ROUTE_DEFAULT_ID):
                             indexURL;
    }
    
    private String getURLStringForADD(String indexURL) {
        return indexURL + "/" + getActionString("add");
    }
    
    private String getURLStringForEDIT(String indexURL) {
        return getURLStringForCRUD(indexURL) + "/" + getActionString("edit");
    }
    
    
    private Resource[] getParentResource(String[] parentResourceNames) {
        if (parentResourceNames == null) return null;
        int length = parentResourceNames.length;
        Resource[] resourceArray = new Resource[length];
        for (int i = 0; i < length; i++) {
            resourceArray[i] = getParentResource(parentResourceNames[i]);
        }
        return resourceArray;
    }
    
    private Resource getParentResource(String parentResourceName) {
        Resource resource = MatchMaker.getInstance().getResource(parentResourceName);
        if (resource == null) {
            resource = new Resource(parentResourceName, true);
            //throw new IllegalArgumentException("\"" + parentResourceName + "\"" + 
            //            " is not defined as a resource, but is used as a" + 
            //            " parent resource for \"" + getName() + "\".");
        }
        return resource;
    }
    
    
    
    private String getNestedURLPrefixString(Resource[] parents) {
        if (pathPrefix != null) return getURLStringForList();
        String nestedPath = "";
        int length = parents.length;
        for (int i = length-1; i >= 0; i--) {
            Resource parent = parents[i];
            String parentPath = parent.getUnprifixedURL() + "/" + "$" + getNesteeIdFormat(parent);
            nestedPath += parentPath + "/";
        }
        nestedPath = StringUtil.replace(nestedPath, "//", "/");
        nestedPath = StringUtil.removeLastToken(nestedPath, "/");
        return nestedPath;
    }
    
    private String getNestedURLStringForList(String nestedPrefix) {
        String nestedPath = nestedPrefix;
        nestedPath += getUnprifixedURL();
        return nestedPath;
    }
    
    private String getNestedURLStringForCRUD(String nestedPrefix) {
        return (!isSingle())?getNestedURLStringForList(nestedPrefix) + "/" + 
            ((autoRoute)?getDynamicPrimaryKeyRepresentation():RouteConstants.ROUTE_DEFAULT_ID):
                             getNestedURLStringForList(nestedPrefix);
    }
    
    private String getNestedURLStringForADD(String nestedPrefix) {
        return getNestedURLStringForList(nestedPrefix) + "/" + getActionString("add");
    }
    
    private String getNestedURLStringForEDIT(String nestedPrefix) {
        return getNestedURLStringForCRUD(nestedPrefix) + "/" + getActionString("edit");
    }
    
    private String[] getParentModels(Resource[] parentResource) {
        int length = parentResource.length;
        String[] models = new String[length];
        for (int i = 0; i < length; i++) {
            models[i] = parentResource[i].getModel();
        }
        return models;
    }
    
    
    private String getActionString(String action) {
        return (actionAliasProperties != null)?actionAliasProperties.getProperty(action, action):action;
    }
    
    private String getTarget(String[] parents, String target) {
        if (parents == null) return target;
        int length = parents.length;
        String nestedTarget = "";
        for (int i = length-1; i >= 0; i--) {
            nestedTarget += parents[i] + "-";
        }
        nestedTarget = StringUtil.removeLastToken(nestedTarget, "-");
        nestedTarget += "-" + target;
        return nestedTarget;
    }
    
    private String createRestRouteName(String action, String target) {
        return action + " " + target;
    }
    
    /**
     * <p>Uses the <tt>model</tt> field to retrieve and construct a dynamic 
     * primary key representation for a record in route definition.</p>
     * 
     * <p>The dynamic primary key representation should be like "$itemid", 
     * or "$orderid-$itemid" if the primary key is a composite key consisting 
     * of <tt>orderid</tt> and <tt>itemid</tt> fields. The separator between 
     * key fields are defined by {@link com.scooterframework.web.route.RouteConstants#PRIMARY_KEY_SEPARATOR}.</p>
     * 
     * <p>In the event that the <tt>model</tt> is not a table name, the 
     * regular "$id" is used to represent dynamic primary key string.</p>
     */
    private String getDynamicPrimaryKeyRepresentation() {
        String tableName = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.tableize(model):model;
        String fullTableName = DatabaseConfig.getInstance().getFullTableName(tableName);
        
        List columns = null;
        try {
            PrimaryKey pk = SqlExpressUtil.lookupTablePrimaryKeyForDefaultConnection(null, null, fullTableName);
            columns = pk.getColumns();
        } catch(Exception ex) {
            log.info("There is no primary key detected for table \"" + fullTableName + 
            "\" as it may be just a resource, not a real table. \"" + 
            RouteConstants.ROUTE_DEFAULT_ID + "\" will be used to " +
            "represent dynamic id for " + model + " in the route definition instead.");
        }
        
        String s = "";
        if (columns != null && columns.size() > 0) {
            Iterator it = columns.iterator();
            while(it.hasNext()) {
                s += "$" + it.next().toString().toLowerCase() + RouteConstants.PRIMARY_KEY_SEPARATOR;
            }
            s = StringUtil.removeLastToken(s, RouteConstants.PRIMARY_KEY_SEPARATOR);
        }
        else {
            s = RouteConstants.ROUTE_DEFAULT_ID;
        }
        return s;
    }
}
