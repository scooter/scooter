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
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.exception.ExecutionException;
import com.scooterframework.common.exception.MethodCreationException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.BeanUtil;
import com.scooterframework.common.util.CurrentThreadCache;
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
            
            String requestPath = getRequestPath(request);
            if (isRootAccess(requestPath)) {
            	processRootAccess(request, response);
            }
            else {
            	String result = null;
                ActionProperties aps = prepareActionProperties(requestPath, request);
                registerActionProperties(request, aps);
                log.debug("aps: " + aps);
                
                result = executeRequest(aps, request, response);
                log.debug("execution result: " + result);
                
                if (result != null) {
                    processNotNullResult(request, response, result);
                }
                else {
                    processNullResult(request, response, aps.controller, aps.action);
                }
            }
        }
        catch(Exception ex) {
            processException(request, response, ex);
        }
    }

    /**
     * <p>Process an <tt>HttpServletRequest</tt>.</p>
     * 
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
        Object controllerInstance = getControllerInstance(aps.controllerClassName);
        if (controllerInstance == null) {
            if (EnvConfig.getInstance().allowForwardToControllerNameViewWhenControllerNotExist()) {
                log.warn("Controller instance for \"" + aps.controller + 
                    "\" does not exist, forward to view \"" + aps.controller + 
                    File.separator + aps.action + "\".");
                return null;
            }
            else {
                throw new NoControllerFoundException(aps.controllerClassName);
            }
        }
        
        Method actionInstance = getActionMethod(controllerInstance.getClass(), aps.action);
        if (actionInstance == null) {
            if (EnvConfig.getInstance().allowForwardToActionNameViewWhenActionNotExist()) {
                log.warn("Action method \"" + aps.action + 
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
    												HttpServletRequest request) {
        String path = requestPath;
        String controllerPath = null;
        String controller = null;
        String action = null;
        String format = null;
        
        int lastDot = path.lastIndexOf(".");
        int lastSlash = path.lastIndexOf("/");
        
        if (lastDot != -1 && lastDot > lastSlash) {
            format = path.substring(lastDot + 1);
            path = path.substring(0, lastDot);
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
        aps.controllerClassName = getControllerClassName(controllerPath);;
        aps.action = action;
        aps.model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(controller):controller;
        aps.format = format;
        
        return aps;
    }
    
    /**
     * Puts some action properties in <tt>request</tt> object.
     */
    protected void registerActionProperties(HttpServletRequest request, ActionProperties aps) {
        request.setAttribute(Constants.CONTROLLER, aps.controller);
        request.setAttribute(Constants.CONTROLLER_PATH, aps.controllerPath);
        request.setAttribute(Constants.MODEL, aps.model);
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
    
    /**
     * Returns request path of the HttpServletRequest <tt>request</tt>. A 
     * request path is a combination of the <tt>request</tt>'s servletPath and pathInfo. 
     * 
     * @param request HttpServletRequest
     * @return request path
     */
    protected String getRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        return requestURI.substring(contextPath.length());
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
    protected Method getActionMethod(Class controllerClass, String actionName) {
        if (controllerClass == null || actionName == null) return null;
        
        Method method = null;
        try {
            method = BeanUtil.getMethod(controllerClass, actionName);
        }
        catch(Exception ex) {
            log.warn("Failed to create action method instance: " + ex.getMessage());
        }
        return method;
    }

    /**
     * Invokes an action method of a controller.
     *
     * @param controller The controller instance to be invoked
     * @param method The method
     * @return execution result
     */
    protected String executeControllerAction(Object controller, Method method) {
        if (controller == null || method == null) return null;
        
        String result = null;
        try {
            boolean beforeIsSuccess = true;
            if (ActionControl.class.isInstance(controller)) {
                result = ((ActionControl)controller).executeBeforeFiltersOn(method.getName());
                if (result != null) beforeIsSuccess = false;
            }
            
            if (beforeIsSuccess) {
                result = (String)method.invoke(controller, (Object[])null);
            }
            
            if (beforeIsSuccess && ActionControl.class.isInstance(controller)) {
                String afResult = ((ActionControl)controller).executeAfterFiltersOn(method.getName());
                if (afResult != null) {
                    result = afResult;
                }
            }
        } catch (Exception ex) {
            ExecutionException eex = 
                new ExecutionException(controller.getClass().getName(), 
                                        method.getName(), null, ex);
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
     * @param request http servlet request
     * @param response http servlet response
     * @param result tagged result of an action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processNotNullResult(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String result)
    throws IOException, ServletException 
    {
        if (ActionResult.checkResultTag(result, ActionResult.TAG_HTML)) {
            processResultHtml(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_REDIRECT_TO)) {
            processResultRedirect(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_FORWARD_TO)) {
            processResultForward(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_TEXT)) {
            processResultText(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_XML)) {
            processResultXML(request, response, result);
        }
        else if (ActionResult.checkResultTag(result, ActionResult.TAG_ERROR)) {
            processResultError(request, response, result);
        }
        else {
            throw new IllegalArgumentException("The execution result starts " + 
            "with an unsupported tag: \"" + result + "\".");
        }
    }
    
    protected void println(HttpServletResponse response, String content, String contentType) 
    throws IOException, ServletException {
        String encoding = response.getCharacterEncoding();
    	if (encoding == null) encoding = "utf-8";
        
        response.setContentType(contentType + "; charset="+encoding);
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = response.getWriter();
        out.println(content);
        out.flush();
    }
    
    /**
     * Processes error result.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultError(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String message = ActionResult.getResultByTag(result, ActionResult.TAG_ERROR);
        processError(request, response, message);
    }
    
    /**
     * Processes html result.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultHtml(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String content = ActionResult.getResultByTag(result, ActionResult.TAG_HTML);
        String contentType = "text/html";
        println(response, content, contentType);
    }
    
    /**
     * Processes plain-text result.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultText(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String content = ActionResult.getResultByTag(result, ActionResult.TAG_TEXT);
        String contentType = "text/plain";
        println(response, content, contentType);
    }
    
    /**
     * Processes xml result.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultXML(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String content = ActionResult.getResultByTag(result, ActionResult.TAG_XML);
        if (!content.startsWith("<?xml")) content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + content;
        String contentType = "text/xml";
        println(response, content, contentType);
    }
    
    /**
     * Processes redirect result.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultRedirect(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String target = ActionResult.getResultByTag(result, ActionResult.TAG_REDIRECT_TO);
        
        if (target.startsWith("/")) {
            String contextPath = request.getContextPath();
            if (!target.startsWith(contextPath)) target = contextPath + target;
        }
        response.sendRedirect(response.encodeRedirectURL(target));
    }
    
    /**
     * Processes forward result.
     * 
     * Forwards to a uri denoted by the <tt>result</tt>.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param result result of action
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processResultForward(HttpServletRequest request, HttpServletResponse response, String result) 
    throws IOException, ServletException {
        String target = ActionResult.getResultByTag(result, ActionResult.TAG_FORWARD_TO);
        doForward(target, request, response);
    }
    
    /**
     * Processes null result.
     * 
     * Forwards to a default uri derived based on controller and action names. 
     * See <tt>getDefaultViewUri</tt> method.
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param controller the name of the controller
     * @param action the action method
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void processNullResult(HttpServletRequest request, HttpServletResponse response, String controller, String action) 
    throws IOException, ServletException {
        String target = getViewURI(controller, action);
        doForward(target, request, response);
    }
    
    /**
     * Processes root access.
     * 
     * Forwards to a default uri derived based on controller and action names. 
     * See <tt>getDefaultViewUri</tt> method.
     * 
     * @param request http servlet request
     * @param response http servlet response
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
     * @param request http servlet request
     * @param response http servlet response
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
     * Processes an exception.
     * 
     * @param request http servlet request
     * @param response http servlet response
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
    	return understand;
    }
    
    /**
     * Returns a default view uri. This uri is actually a real view file 
     * associated with the controller and the action method name. 
     * 
     * @param controller the name of the controller
     * @param action the action method
     * @return view uri.
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
    
    /**
     * <p>Do a forward to specified URI using a <tt>RequestDispatcher</tt>.
     * This method is used by all internal method needing to do a forward.</p>
     *
     * @param uri Context-relative URI to forward to
     * @param request http servlet request
     * @param response http servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void doForward(
        String uri,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        log.debug("doForward: " + uri);
        if (uri != null && !uri.startsWith("/")) uri = "/" + uri;
        RequestDispatcher rd = getServletContext().getRequestDispatcher(uri);
        
        if (rd == null) {
            uri = "/WEB-INF/views/404.jsp";
            log.error("Unable to locate \"" + uri + "\", forward to " + uri);
            rd = getServletContext().getRequestDispatcher(uri);
        }
        rd.forward(request, response);
    }
    
    /**
     * Displays all parameter names and theirs values in a http request. 
     * 
     * @param request
     */
    public void displayHttpRequest(HttpServletRequest request) {
        if (request != null) {
            Enumeration en = request.getParameterNames();
            while(en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String[] values = request.getParameterValues(key);
                if (values != null) {
                    String tmp = "";
                    
                    int length = values.length;
                    for (int i = 0; i < length - 1; i++) {
                        tmp += values[i] + ", ";
                    }

                    tmp += values[length-1];
                    
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
        if ("/".equals(requestPath) || requestPath.startsWith("/index")) {
            rootAccess = true;
        }
        return rootAccess;
    }
}
