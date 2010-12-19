/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.exception.FileUploadException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.CurrentThreadCacheClient;

/**
 * ScooterRequestFilter can be attached to either an individual servlet
 * or to a URL pattern.  This filter performs the following functions:
 * <ul>
 * <li>Loads property value under the attribute name
 *     defined by the value of the <tt>attribute</tt> initialization
 *     parameter.</li>
 * <li>Initializes WebActionContext.</li>
 * <li>Records a bench mark in the response header if specified in environment.properties file.</li>
 * <li>Cleans up content cached in the request thread.</li>
 * </ul>
 * 
 * <p>The following parameters are allowed to configure this filter in web.xml:</p>
 * <pre>
 *   Examples:
 *     excluded_paths: /images, /javascripts, /layouts, /stylesheets, /css
 *           encoding: UTF-8
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class ScooterRequestFilter implements Filter {
    /**
     * Directory paths that we want this filter to skip.
     */
    protected String excludedPaths = null;
    
    /**
     * Character encoding to be used.
     */
    protected String encoding = null; 

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig filterConfig = null;


    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.excludedPaths = filterConfig.getInitParameter("excluded_paths");
        this.encoding = filterConfig.getInitParameter("encoding");
        
        otherInit();
    }
    
    protected void otherInit() {
        //Do something more
    }
    
    /**
     * Take this filter out of service.
     */
    public void destroy() {
        this.excludedPaths = null;
        this.filterConfig = null;
    }

    /**
     * Time the processing that is performed by all subsequent filters in the
     * current filter stack, including the ultimately invoked servlet.
     *
     * @param request The servlet request we are processing
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException 
    {
        boolean staticContent = isStaticContentRequest((HttpServletRequest)request);
    	
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
            if (!staticContent) {
                response.setContentType("text/html;charset="+encoding);
            }
        }
        
       // boolean skip = skippedRequestPath((HttpServletRequest)request);
        boolean skip = staticContent;
        
        long before = System.currentTimeMillis();
        
        if (!skip) {
            initializeActionContext((HttpServletRequest)request, (HttpServletResponse)response);
        }
        else {
            request.setAttribute(Constants.SKIP_PATH, "Y");
        }
        
        if (isAjaxRequest((HttpServletRequest)request)) {
        	request.setAttribute("__sitemesh__filterapplied", Boolean.TRUE);
        }

        String requestPathREST = requestInfo(request);
        log.debug("============>>\"" + requestPathREST + "\"");
        
        try {
        	chain.doFilter(request, response);
        } catch (Throwable ex) {
        	ex.printStackTrace();
        	log.error("Error from chain.doFilter: " + ex.getMessage());
        }
        
        long after = System.currentTimeMillis();
        
        if (EnvConfig.getInstance().allowRecordBenchmark()) {
            log.info("\"" + requestPathREST + "\" takes: " + (after - before) + " ms");
            if (EnvConfig.getInstance().allowRecordBenchmarkInHeader()) {
                HttpServletResponseWrapper resw = new HttpServletResponseWrapper((HttpServletResponse)response);
                resw.addHeader("Exec-Time", (after - before) + " ms");
            }
        }
        
        clearCachedRequestData();
    }
    
    protected boolean isStaticContentRequest(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String staticPath = contextPath + "/" + "static";
        return (requestURI.startsWith(staticPath))?true:false;
    }
    
    protected String requestInfo(ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest)req;
        String method = request.getMethod();
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String requestPath = requestURI.substring(contextPath.length());
        if (requestPath.length() > 1 && 
        		(requestURI.endsWith("/") || requestURI.endsWith("\\"))) {
        	requestURI = requestURI.substring(0, requestURI.length()-1);
        }
        
        //request header
        Properties headers = new Properties();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = (String)headerNames.nextElement();
            String value = request.getHeader(name);
            if (value != null) headers.setProperty(name, value);
        }
        
        String requestPathRESTful = method + " " + requestPath;
        String queryString = request.getQueryString();
        if (queryString != null) requestPathRESTful += "?" + queryString;
        
        CurrentThreadCache.set(Constants.REQUEST_HEADER, headers);
        CurrentThreadCache.set(Constants.REQUEST_PATH, requestPath);
        CurrentThreadCache.set(Constants.REQUEST_PATH_REST, requestPathRESTful);
        CurrentThreadCache.set(Constants.REQUEST_URI, requestURI);
        
        if (isLocalRequest(request)) {
            CurrentThreadCache.set(Constants.LOCAL_REQUEST, Constants.VALUE_FOR_LOCAL_REQUEST);
        }
        
        if (isFileUploadRequest(request)) {
            CurrentThreadCache.set(Constants.FILE_UPLOAD_REQUEST, Constants.VALUE_FOR_FILE_UPLOAD_REQUEST);
            
            try {
            	List<FileItem> files = new ArrayList<FileItem>();
                ServletFileUpload fileUpload = EnvConfig.getInstance().getServletFileUpload();
                List<FileItem> items = fileUpload.parseRequest(request);
                for (FileItem item : items) {
                	if (item.isFormField()) {
                		ActionControl.storeToRequest(item.getFieldName(), item.getString());
                	}
                	else if (!item.isFormField() && !"".equals(item.getName())) {
                		files.add(item);
                	}
                }
                CurrentThreadCache.set(Constants.FILE_UPLOAD_REQUEST_FILES, files);
            } catch (Exception ex) {
            	CurrentThreadCacheClient.storeError(new FileUploadException(ex));
            }
        }
        
        return requestPathRESTful;
    }
    
    protected boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getParameter(Constants.AJAX_REQUEST) != null)?true:false;
    }
    
    /**
     * Cleans up all local cached data to prepare for the next request.
     */
    protected void clearCachedRequestData() {
        CurrentThreadCache.clear();
    }
    
    protected void initializeActionContext(HttpServletRequest request, HttpServletResponse response) {
        WebActionContext wac = new WebActionContext(request, response);
        ACH.setActionContext(wac);
    }
    
    /**
     * Checks if a request path must be skipped.
     * 
     * @param request
     * @return true if the request path should be skipped.
     */
    protected boolean skippedRequestPath(HttpServletRequest request) {
        if (excludedPaths == null || "Y".equals(request.getAttribute(Constants.SKIP_PATH))) return true;
        
        String contextPath = request.getContextPath();
        String uriPath = request.getRequestURI();
        String servletPathInfo = "";
        String requestPathDir = uriPath;
        if (uriPath.length() > contextPath.length()) {
            servletPathInfo = uriPath.substring(contextPath.length());
            requestPathDir = servletPathInfo;
            int secondSlashIndex = servletPathInfo.indexOf("/", 1);
            if (secondSlashIndex != -1) requestPathDir = servletPathInfo.substring(0, secondSlashIndex);
        }
        
        boolean status = false;
        if (!"/".equals(requestPathDir) && excludedPaths.indexOf(requestPathDir) != -1) {
            status = true;
        }
        
        log.debug("skip = " + status + " for " + servletPathInfo);
        return status;
    }
    
    protected boolean isLocalRequest(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        if (requestURL.startsWith(Constants.LOCAL_HOST_URL_PREFIX_1) || 
            requestURL.startsWith(Constants.LOCAL_HOST_URL_PREFIX_2) || 
            requestURL.startsWith(Constants.LOCAL_HOST_URL_PREFIX_3) || 
            requestURL.startsWith(Constants.LOCAL_HOST_URL_PREFIX_4) || 
            remoteAddr.equals(Constants.LOCAL_HOST_REMOTE_ADDRESS) || 
            remoteHost.equals(Constants.LOCAL_HOST_REMOTE_HOST_1) || 
            remoteHost.equals(Constants.LOCAL_HOST_REMOTE_HOST_2)) {
            return true;
        }
        return false;
    }
    
    protected boolean isFileUploadRequest(HttpServletRequest request) {
    	return ServletFileUpload.isMultipartContent(request);
    }

    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
