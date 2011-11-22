/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.FilterManager;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.autoloader.JavaCompiler;
import com.scooterframework.common.exception.ExecutionException;
import com.scooterframework.common.exception.MethodCreationException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.web.route.NoRouteFoundException;

/**
 * <p><strong>BaseRequestProcessor</strong> contains the processing logic that
 * the {@link MainActionServlet} performs as it receives each servlet request
 * from the container. You can customize the request processing behavior by
 * subclassing this class and overriding the method(s) whose behavior you are
 * interested in changing.</p>
 *
 * @author (Fei) John Chen
 */
public class BaseRequestProcessor {
    protected LogUtil log = LogUtil.getLogger(getClass().getName());

    private Map<String, ActionProperties> requestPropertiesMap = new HashMap<String, ActionProperties>();

    public static final String DEFAULT_CONTROLLER_CLASS = "com.scooterframework.builtin.CRUDController";

    public static final String EXECUTION_INTERRUPTED = "EXECUTION_INTERRUPTED";

    /**
     * Constructor
     */
    public BaseRequestProcessor() {
    }

    /**
     * <p>Process an <tt>HttpServletRequest</tt> and create the
     * corresponding <tt>HttpServletResponse</tt> or dispatch
     * to another resource.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        if (log.isDebugEnabled()) displayHttpRequest(request);

        try {
            processLocale(request, response);

            String requestPath = CurrentThreadCacheClient.requestPath();

            if (!isAdminRequest(requestPath) && JavaCompiler.hasCompileErrors()) {
            	processCompileError(request, response);
            	return;
            }

            if (isRootAccess(requestPath)) {
            	processRootAccess(request, response);
            }
            else {
            	String result = null;

            	String requstPathKey = CurrentThreadCacheClient.requestPathKey();
            	ActionProperties aps = requestPropertiesMap.get(requstPathKey);

            	if (aps == null || ApplicationConfig.getInstance().isInDevelopmentEnvironment()) {
                    String requestHttpMethod = CurrentThreadCacheClient.httpMethod();
                    aps = prepareActionProperties(requestPath, requestHttpMethod, request);
                    registerActionProperties(request, aps);
                    requestPropertiesMap.put(requstPathKey, aps);
            	}
            	else {
            		registerActionProperties(request, aps);
            	}
                log.debug("aps: " + aps);

                result = executeRequest(aps, request, response);
                log.debug("execution result: " + result);

                if (result != null) {
                    processNotNullResult(request, response, aps, result);
                }
                else {
                    processNullResult(request, response, aps);
                }
            }
        }
        catch(Exception ex) {
            processException(request, response, ex);
        }
    }

    private boolean isAdminRequest(String requestPath) {
    	return (requestPath != null && requestPath.toLowerCase().startsWith("/admin"));
    }

    /**
     * <p>Process an <tt>HttpServletRequest</tt>.</p>
     *
     * @param aps properties of request
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @return execution result
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public String executeRequest(ActionProperties aps,
                HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
    	Object controllerInstance = null;

    	if (aps.controllerCreated) {
    		controllerInstance = aps.controllerInstance;
    	}
    	else {
    		controllerInstance = getControllerInstance(aps.controllerClassName);
    		aps.controllerInstance = controllerInstance;
    		aps.controllerCreated = true;
    	}

        if (controllerInstance == null) {
            if (EnvConfig.getInstance().allowForwardToControllerNameViewWhenControllerNotExist()) {
                log.info("Controller instance for \"" + aps.controller +
                    "\" does not exist, forward to view \"" + aps.controller +
                    File.separator + aps.action + "\".");
                return null;
            }
            else {
                throw new NoControllerFoundException(aps.controllerClassName);
            }
        }

        Method actionInstance = null;
        if (aps.methodCreated) {
        	actionInstance = aps.methodInstance;
        }
        else {
        	actionInstance = getActionMethod(controllerInstance.getClass(), aps.action);
        	aps.methodInstance = actionInstance;
        	aps.methodCreated = true;
        }

        if (actionInstance == null) {
            if (EnvConfig.getInstance().allowForwardToActionNameViewWhenActionNotExist()) {
                log.debug("Action method \"" + aps.action +
                    "\", forward to view \"" + aps.action + "\".");
                return null;
            }
            else {
                throw new MethodCreationException(controllerInstance.getClass().getName(), aps.action);
            }
        }

        return executeControllerAction(controllerInstance, actionInstance);
    }

    /**
     * Sets up action properties for the action execution. The properties are
     * wrapped up in an <tt>ActionProperties</tt> instance.
     *
     * @param request The servlet request we are processing
     * @return an ActionProperties instance
     */
	public ActionProperties prepareActionProperties(String requestPath,
			String requestHttpMethod, HttpServletRequest request) {
        String path = requestPath;
        String controllerPath = null;
        String controller = null;
        String action = null;
        String format = null;

        int lastDot = path.lastIndexOf(".");
        int lastSlash = path.lastIndexOf("/");

        if (lastDot != -1 && lastDot > lastSlash) {
            format = path.substring(lastDot + 1);
            if (EnvConfig.getInstance().hasMimeTypeFor(format)) {
                path = path.substring(0, lastDot);
            }
            else {
            	format = null;
            }
        }

        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        lastSlash = path.lastIndexOf("/");
        if (lastSlash > 0) {
            action = path.substring(lastSlash + 1);
            controllerPath = path.substring(0, lastSlash);
            lastSlash = controllerPath.lastIndexOf("/");
            if (lastSlash != -1) {
                controller = controllerPath.substring(lastSlash + 1);
            }
        }
        else if (lastSlash == 0) {
            controllerPath = path;
            controller = path.substring(1);
        }

        if (action == null || "".equals(action)) {
            if (EnvConfig.getInstance().allowDefaultActionMethod()) {
                action = EnvConfig.getInstance().getDefaultActionMethod();
            }
            else {
                throw new IllegalArgumentException("The value for action " +
                "is not detected from the request path \"" + requestPath +
                "\" and the default action method is not allowed in property file.");
            }
        }

        ActionProperties aps = new ActionProperties();
        aps.controllerPath = controllerPath;
        aps.controller = controller;
        aps.controllerClassName = getControllerClassName(controllerPath);
        aps.action = action;
        aps.model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(controller):controller;
        aps.format = format;

        return aps;
    }

    /**
     * Puts some action properties in <tt>request</tt> object.
     */
    protected void registerActionProperties(HttpServletRequest request, ActionProperties aps) {
    	CurrentThreadCacheClient.cacheController(aps.controller);
        CurrentThreadCacheClient.cacheControllerClass(aps.controllerClassName);
        CurrentThreadCacheClient.cacheControllerPath(aps.controllerPath);
        CurrentThreadCacheClient.cacheAction(aps.action);
        CurrentThreadCacheClient.cacheModel(aps.model);
        CurrentThreadCacheClient.cacheFormat(aps.format);
        request.setAttribute(Constants.CONTROLLER, aps.controller);
        request.setAttribute(Constants.CONTROLLER_CLASS, aps.controllerClassName);
        request.setAttribute(Constants.CONTROLLER_PATH, aps.controllerPath);
        request.setAttribute(Constants.ACTION, aps.action);
        request.setAttribute(Constants.MODEL, aps.model);
        request.setAttribute(Constants.FORMAT, aps.format);
    }

    /**
     * Checks if a request is local. Subclass can override this method if a
     * different logic is used to determine local request.
     *
     * @param request HttpServletRequest
     * @return true if the request is from a localhost
     */
    protected boolean isLocalRequest(HttpServletRequest request) {
        String s = (String)CurrentThreadCache.get(Constants.LOCAL_REQUEST);
        if ((Constants.VALUE_FOR_LOCAL_REQUEST).equals(s)) {
            return true;
        }
        return false;
    }

    protected void processLocale(HttpServletRequest request,
            HttpServletResponse response) {
    	//use the requested locale
    	Locale locale = request.getLocale();

		if (locale == null) {
			locale = ACH.getAC().getLocale(ActionContext.SCOPE_SESSION);
			if (locale != null) {
				//there is no need to do anything if a locale has been chosen.
				return;
			}
			else {
				//use the configured locale
				locale = ActionContext.getGlobalLocale();
				if (locale == null) {
					locale = Locale.getDefault();
				}
				ACH.getAC().setLocale(locale, ActionContext.SCOPE_SESSION);
			}
		}
		else {
			Locale sessionLocale = ACH.getAC().getLocale(ActionContext.SCOPE_SESSION);
			if (sessionLocale == null || !sessionLocale.equals(locale)) {
				ACH.getAC().setLocale(locale, ActionContext.SCOPE_SESSION);
			}
		}

		log.debug("User locale is '" + locale + "'.");
	}

    /**
     * Returns a controller class name.
     *
     * @param controllerPath controller path
     * @return controller class name
     */
    protected String getControllerClassName(String controllerPath) {
        String controllerClassName = EnvConfig.getInstance().getControllerClassName(controllerPath);
        return controllerClassName;
    }

    /**
     * Returns a controller instance.
     *
     * @param controllerClassName controller class name
     * @return controller instance
     */
    protected Object getControllerInstance(String controllerClassName) {
        return ControllerFactory.createController(controllerClassName, getDefaultControllerClassName());
    }

    /**
     * Returns class name of default controller. This default controller is used
     * when application specific controller is not available and the
     * <tt>auto.crud</tt> property is set to true in the environment.properties
     * file. Subclass must override this method if a different default
     * controller class is used.
     */
    protected String getDefaultControllerClassName() {
        return DEFAULT_CONTROLLER_CLASS;
    }

    /**
     * Returns a method instance related to an action of a controller.
     *
     * @param controllerClass a controller class type
     * @param actionName name of the action method
     * @return the method instance
     */
    protected Method getActionMethod(Class<?> controllerClass, String actionName) {
        if (controllerClass == null || actionName == null) return null;

        Method method = null;
        try {
            method = ControllerFactory.getMethod(controllerClass, actionName);
        }
        catch(Exception ex) {
            log.debug("Failed to create action method instance: " + ex.getMessage());
        }
        return method;
    }

    /**
     * Invokes an action method of a controller.
     *
     * @param controller The controller instance to be invoked
     * @param method The action method
     * @return execution result
     */
    protected String executeControllerAction(Object controller, Method method) {
        if (controller == null || method == null) return null;

        String result = null;
        try {
            boolean beforeIsSuccess = true;
            FilterManager filterManager = FilterManagerFactory.getInstance().getFilterManager(controller.getClass());
            if (filterManager != null && !filterManager.noFilterDeclared()) {
            	result = filterManager.executeBeforeFiltersOn(method.getName());
                if (result != null) beforeIsSuccess = false;
            }

            if (beforeIsSuccess) {
                result = (String)method.invoke(controller, (Object[])null);
            }

            if (beforeIsSuccess && filterManager != null && !filterManager.noFilterDeclared()) {
                String afResult = filterManager.executeAfterFiltersOn(method.getName());
                if (afResult != null) {
                    result = afResult;
                }
            }
        } catch (Exception ex) {
			log.error("Error in executeControllerAction controller/action: " + controller + "/" + method, ex);
            ExecutionException eex =
                new ExecutionException(controller.getClass().getName(), method.getName(), null, ex);
            throw eex;
        }

        return result;
    }

    /**
     * <p>Processes not-null result of an action method. "not-null" result is a
     * result string tagged by one of the supporting tags. All supported tags
     * are documented in ActionResult.</p>
     *
     * <pre>
     * Examples of not-null results:
     *
     * Forward result to a view:
     *   forwardTo=>/WEB-INF/views/jsp/sayit.jsp
     *
     * Display result in html format:
     *   html=><h1>Good morning</h1>
     *
     * Return xml formatted document:
     *   xml=><?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><book><title>Java Programming</title><price>$50</price></book>
     *
     * Return plain-text document:
     *   text=>This is a small world.
     * </pre>
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param aps properties of request
     * @param result tagged result of an action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processNotNullResult(HttpServletRequest request,
                                        HttpServletResponse response,
                                        ActionProperties aps,
                                        String result)
    throws IOException, ServletException
    {
    	if (hasRendered(request)) {
    		return;
    	}

        if (ActionResult.checkResultTag(result, ActionResult.TAG_REDIRECT_TO)) {
            processResultRedirect(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_FORWARD_TO)) {
            processResultForward(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_ERROR)) {
            processResultError(request, response, result);
        }
        else {
        	if (ActionResult.startsWithContentTypeTag(result)) {
            	String tag = ActionResult.getContentTypeTag(result);
            	String content = ActionResult.getResultContentByTag(result, tag);
            	processResultContentForRequestFormatType(request, response, content, tag);
        	}
        	else {
        		String tag = (aps.format != null)?aps.format:Constants.DEFAULT_RESPONSE_FORMAT;
        		processResultContentForRequestFormatType(request, response, result, tag);
        	}
        }
    }

    protected void processResultContentForRequestFormatType(
    		HttpServletRequest request,
			HttpServletResponse response,
			String content,
			String format)
	throws IOException, ServletException
	{
        ContentHandler handler = ContentHandlerFactory.getContentHandler(format);
        if (handler != null) {
        	handler.handle(request, response, content, format);
        }
        else {
			throw new IllegalArgumentException(
					"There is no handler found for format \""
							+ format
							+ "\". You may create your own as a plugin by "
							+ "extending the Plugin class and "
							+ "implementing the ContentHandler interface.");
        }
	}

    /**
     * Processes error result.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param result
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultError(HttpServletRequest request, HttpServletResponse response, String result)
    throws IOException, ServletException {
        String message = ActionResult.getResultContentByTag(result, ActionResult.TAG_ERROR);
        processError(request, response, message);
    }

    /**
     * Processes redirect result.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultRedirect(HttpServletRequest request, HttpServletResponse response, String result)
    throws IOException, ServletException {
        String target = ActionResult.getResultContentByTag(result, ActionResult.TAG_REDIRECT_TO);

        if (target.startsWith("/")) {
            String contextPath = request.getContextPath();
            if (!target.startsWith(contextPath)) target = contextPath + target;
        }
        response.sendRedirect(response.encodeRedirectURL(target));
    }

    /**
     * Processes forward result.
     *
     * Forwards to a URI denoted by the <tt>result</tt>.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultForward(HttpServletRequest request, HttpServletResponse response, String result)
    throws IOException, ServletException {
        String target = ActionResult.getResultContentByTag(result, ActionResult.TAG_FORWARD_TO);
        doForward(target, request, response);
    }

    /**
     * Processes null result.
     *
     * Forwards to a default URI derived based on controller and action names.
     * See <tt>getDefaultViewUri</tt> method.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param aps properties of request
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processNullResult(HttpServletRequest request,
    								 HttpServletResponse response,
    								 ActionProperties aps)
    throws IOException, ServletException {
    	if (hasRendered(request)) return;

        String target = getViewURI(StringUtil.toLowerCase(aps.controller), StringUtil.toLowerCase(aps.action));
        doForward(target, request, response);
    }

    private boolean hasRendered(HttpServletRequest request) {
    	return (request.getAttribute(Constants.REQUEST_RENDERED) != null)?true:false;
    }

    /**
     * Processes root access.
     *
     * Forwards to a default URI derived based on controller and action names.
     * See <tt>getDefaultViewUri</tt> method.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processRootAccess(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        String target = EnvConfig.getInstance().getRootURL();
        doForward(target, request, response);
    }

    /**
     * Processes an error message.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param error an error message
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processError(HttpServletRequest request, HttpServletResponse response, String error)
    throws IOException, ServletException {
        String message = "Error in \"" + CurrentThreadCache.get(Constants.REQUEST_PATH) + "\": " + error;
        log.error(message);
        CurrentThreadCache.set(Constants.ERROR_MESSAGE, message);
        doForwardToErrorPage(request, response);
    }

    /**
     * Processes a compile error message.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processCompileError(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        doForwardToCompileErrorPage(request, response);
    }

    /**
     * Processes an exception.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param ex
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processException(HttpServletRequest request, HttpServletResponse response, Exception ex)
    throws IOException, ServletException {
        String message = "";
        if (ex instanceof NoRouteFoundException) {
            message = ex.getMessage();
        }
        else {
            message = "Error in \"" + CurrentThreadCache.get(Constants.REQUEST_PATH) + "\": " + ex.toString();
        }

        log.error(message);

        if (!interpretException(ex)) {
        	ex.printStackTrace();
        }

        CurrentThreadCache.set(Constants.ERROR_EXCEPTION, ex);
        CurrentThreadCache.set(Constants.ERROR_MESSAGE, message);

        //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        doForwardToErrorPage(request, response);
    }

    protected boolean interpretException(Exception ex)	{
    	boolean understand = false;
    	String error = ex.getMessage();
    	if (error == null || "".equals(error)) return false;

    	String meaning = "";
    	if (error.indexOf("Connections could not be acquired from the underlying database!") != -1) {
    		meaning = "Please verify database connection setup or existence of jdbc library for the database.";
    		log.error(meaning);
    		ActionControl.flash("error", meaning);
    		understand = true;
    	}
    	else if (ex instanceof NoRouteFoundException) {
    		meaning = "Please verify either the route is missing or a view path is setup correctly.";
    		log.error(meaning);
    		ActionControl.flash("error", meaning);
    		understand = true;
    	}
    	else if (ex instanceof NoTemplateHandlerException) {
    		meaning = "Please add a template handler for template type \"" +
    			((NoTemplateHandlerException)ex).getTemplateType() + "\".";
    		log.error(meaning);
    		ActionControl.flash("error", meaning);
    		understand = true;
    	}
    	else if (ex instanceof NoViewFileException) {
    		meaning = "Please add a view file for view \"" +
    			((NoViewFileException)ex).getTargetView() + "\".";
    		log.error(meaning);
    		ActionControl.flash("error", meaning);
    		understand = true;
    	}
    	return understand;
    }

    /**
     * Returns a default view URI. This URI is actually a real view file
     * associated with the controller and the action method name.
     *
     * @param controller the name of the controller
     * @param action the action method
     * @return view URI.
     */
    protected String getViewURI(String controller, String action) {
        String uri = EnvConfig.getViewURI(controller, action, getDefaultViewFilesDirectoryName());
        return uri;
    }

    /**
     * Returns default view file directory name.
     *
     * @return default view file directory name.
     */
    protected String getDefaultViewFilesDirectoryName() {
        return (EnvConfig.getInstance().allowAutoCRUD())?
                EnvConfig.getInstance().getDefaultViewFilesDirectory():null;
    }

    protected void doForwardToErrorPage(
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        doForward(EnvConfig.getInstance().getErrorPageURI(), request, response);
    }

    protected void doForwardToCompileErrorPage(
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        doForward(EnvConfig.getInstance().getCompileErrorPageURI(), request, response);
    }

    /**
     * <p>Do a forward to specified URI using a <tt>RequestDispatcher</tt>.
     * This method is used by all internal method needing to do a forward.</p>
     *
     * @param uri Context-relative URI to forward to
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void doForward(
        String uri,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        ActionControl.doForward(uri, request, response);
    }

    /**
     * Displays all parameter names and theirs values in a HTTP request.
     *
     * @param request
     */
    public void displayHttpRequest(HttpServletRequest request) {
        if (request != null) {
            @SuppressWarnings("unchecked")
			Enumeration<String> en = request.getParameterNames();
            while(en.hasMoreElements()) {
                String key = en.nextElement();
                String[] values = request.getParameterValues(key);
                if (values != null) {
                    String tmp = "";

                    int length = values.length;
                    for (int i = 0; i < length - 1; i++) {
                        tmp += values[i] + ", ";
                    }

                    tmp += values[length-1];

                    if ("password".equalsIgnoreCase(key)) tmp = "********";
                    log.debug("name=["+key+"] values=["+tmp+"]");
                }
            }
        }
    }

    /**
     * Returns ServletContext
     */
    protected ServletContext getServletContext() {
        return ACH.getWAC().getHttpServletRequest().getSession().getServletContext();
    }

    /**
     * Returns RealPath
     */
    protected String getRealPath() {
        return getServletContext().getRealPath("");
    }

    /**
     * Checks if a request path is a root access. A root access means the
     * <tt>requestPath</tt> is either "/" or starts with "<tt>/index</tt>".
     * @param requestPath
     * @return true if the request path is root path.
     */
    protected boolean isRootAccess(String requestPath) {
        boolean rootAccess = false;
        if ("".equals(requestPath) ||
        	"/".equals(requestPath) || requestPath.startsWith("/index")) {
            rootAccess = true;
        }
        return rootAccess;
    }
}
