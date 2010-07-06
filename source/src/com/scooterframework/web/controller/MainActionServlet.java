/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scooterframework.admin.AutoLoadedObjectFactory;
import com.scooterframework.admin.Constants;
import com.scooterframework.common.logging.LogUtil;

/**
 * MainActionServlet class
 * 
 * @author (Fei) John Chen
 *
 */
public class MainActionServlet extends HttpServlet {
    
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2227149534966991040L;

	private LogUtil log = LogUtil.getLogger(this.getClass().getName());

    /**
     * The class name of the <tt>RequestProcessor</tt> instance we will use 
     * to process all incoming requests.</p>
     */
    protected static String processorClassName = null;
    
    /**
     * The key for this servlet in servlet context.
     */
    public static final String KEY_ACTION_SERVLET = "scooter.key.actionservlet";
    
    /**
     * The key for this request processor in servlet context.
     */
    public static final String KEY_REQUEST_PROCESSOR = "scooter.key.requestprocessor";
    
    /**
     * Checks if <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
     * is used.
     * 
     * @return trur if used.
     */
    public static boolean isUsingRestfulProcessor() {
        return "com.scooterframework.web.controller.RestfulRequestProcessor".equals(processorClassName);
    }
    
    /**
     * Shuts down this servlet
     */
    public void destroy() {
        getServletContext().removeAttribute(KEY_ACTION_SERVLET);
        getServletContext().removeAttribute(KEY_REQUEST_PROCESSOR);
    }

    /**
     * Initializes this servlet
     *
     * @exception ServletException
     */
    public void init() throws ServletException {
        try {
            initOtherParameters();
            getServletContext().setAttribute(KEY_ACTION_SERVLET, this);
        } catch (UnavailableException ex) {
            throw ex;
        } catch (Throwable t) {
            log.error("Unable to initialize servlet \"" + getServletName() + "\": " + t.getMessage());
            throw new UnavailableException(t.getMessage());
        }
    }

    /**
     * Processes an http request with "HEAD" method.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doHead(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, "HEAD");
    }

    /**
     * Processes an http request with "GET" method.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, "GET");
    }

    /**
     * Processes an http request with "POST" method.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, "POST");
    }

    /**
     * Processes an http request with "PUT" method.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, "PUT");
    }

    /**
     * Processes an http request with "DELETE" method.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, "DELETE");
    }

    /**
     * Processes an http request.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response, request.getMethod().toUpperCase());
    }

    /**
     * Initializes other global characteristics of the controller servlet.
     *
     * @exception ServletException if we cannot initialize these resources
     */
    protected void initOtherParameters() throws ServletException {
        processorClassName = getServletConfig().getInitParameter("processor");
    }

    /**
     * Processes request.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param httpMethod standard http method
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception is thrown
     */
    protected void process(HttpServletRequest request, HttpServletResponse response, String httpMethod)
    throws IOException, ServletException {
        String hm = request.getParameter(Constants.HTTP_METHOD);
        if (hm == null) {
            hm = httpMethod;
        }
        else {
            hm = hm.toUpperCase();
        }
        request.setAttribute(Constants.HTTP_METHOD, hm);
        
        BaseRequestProcessor processor = (BaseRequestProcessor)getServletContext().getAttribute(KEY_REQUEST_PROCESSOR);
        if (processor == null) {
            processor = (BaseRequestProcessor)AutoLoadedObjectFactory.getInstance().newInstance(processorClassName);
            getServletContext().setAttribute(KEY_REQUEST_PROCESSOR, processor);
        }
        processor.process(request, response);
    }
}
