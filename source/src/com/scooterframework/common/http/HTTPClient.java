/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * HTTPClient class acts as a HTTP client.
 * 
 * @author (Fei) John Chen
 *
 */
public class HTTPClient {
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";
	public static final String OPTIONS = "OPTIONS";

	private HttpClient httpclient;
	
	public HTTPClient() {
		startUp();
	}
	
	/**
	 * Creates a HTTP client. 
	 */
	public void startUp() {
		httpclient = new DefaultHttpClient();
	}
	
	/**
	 * Shuts down the HTTP client.
	 */
	public void shutDown() {
		httpclient.getConnectionManager().shutdown();
	}
	
	/**
	 * Sends a HTTP GET request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpGetRequest(String uri) {
		return fireHttpRequest(HTTPClient.GET, uri);
	}
	
	/**
	 * Sends a HTTP POST request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpPostRequest(String uri) {
		return fireHttpRequest(HTTPClient.POST, uri);
	}
	
	/**
	 * Sends a HTTP POST request
	 * 
	 * @param uri  the request uri
	 * @param params  parameters to send with the request
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpPostRequest(String uri, Map<String, String> params) {
		return fireHttpRequest(HTTPClient.POST, uri, params);
	}
	
	/**
	 * Sends a HTTP PUT request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpPutRequest(String uri) {
		return fireHttpRequest(HTTPClient.PUT, uri);
	}
	
	/**
	 * Sends a HTTP PUT request
	 * 
	 * @param uri  the request uri
	 * @param params  parameters to send with the request
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpPutRequest(String uri, Map<String, String> params) {
		return fireHttpRequest(HTTPClient.PUT, uri, params);
	}
	
	/**
	 * Sends a HTTP DELETE request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpDeleteRequest(String uri) {
		return fireHttpRequest(HTTPClient.DELETE, uri);
	}
	
	/**
	 * Sends a HTTP HEAD request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpHeadRequest(String uri) {
		return fireHttpRequest(HTTPClient.HEAD, uri);
	}
	
	/**
	 * Sends a HTTP OPTIONS request
	 * 
	 * @param uri  the request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpOptionsRequest(String uri) {
		return fireHttpRequest(HTTPClient.OPTIONS, uri);
	}
	
	/**
	 * Sends a HTTP request.
	 * 
	 * @param method  a HTTP method
	 * @param uri     request uri
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpRequest(String method, String uri) {
		return fireHttpRequest(method, uri, null);
	}
	
	/**
	 * Sends a HTTP request with parameters. 
	 * 
	 * Please note that the <tt>params</tt> parameter is only useful for 
	 * requests using <tt>POST</tt> and <tt>PUT</tt> method.
	 * 
	 * @param method  a HTTP method
	 * @param uri     request uri
	 * @param params  request parameters
	 * @return an HTTPResponse instance
	 */
	public HTTPResponse fireHttpRequest(String method, String uri,
			Map<String, String> params) {
		if (method == null || "".equals(method)) 
			throw new IllegalArgumentException("method cannot be null or empty.");
		
		if (uri == null || "".equals(uri)) 
			throw new IllegalArgumentException("uri cannot be null or empty.");
		
		HttpUriRequest request = null;
		HttpResponse response = null;
		try {
			request = createHttpRequest(method, uri, params);
			response = httpclient.execute(request);
		} catch (Throwable ex) {
			String error = (request != null)?request.getRequestLine().toString():uri;
			throw new HTTPRequestError("Failed in request \""
					+ error + "\"; details: "
					+ ex.getMessage());
		}
		return new HTTPResponse(response);
	}

	private HttpUriRequest createHttpRequest(String method, String uri, Map<String, String> params) throws UnsupportedEncodingException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		
		HttpUriRequest request = null;

		if (GET.equalsIgnoreCase(method)) {
			request = new HttpGet(uri);
		} else if (POST.equalsIgnoreCase(method)) {
			request = new HttpPost(uri);
			((HttpPost)request).setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} else if (PUT.equalsIgnoreCase(method)) {
			request = new HttpPut(uri);
			((HttpPut)request).setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} else if (DELETE.equalsIgnoreCase(method)) {
			request = new HttpDelete(uri);
		} else if (HEAD.equalsIgnoreCase(method)) {
			request = new HttpHead(uri);
		} else if (OPTIONS.equalsIgnoreCase(method)) {
			request = new HttpOptions(uri);
		} else {
			throw new IllegalArgumentException("Method \"" + method
					+ "\" is not supported.");
		}

		return request;
	}
}
