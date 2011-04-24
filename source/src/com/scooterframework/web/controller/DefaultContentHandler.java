/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * DefaultContentHandler is responsible for handling content of a request
 * format when the specific handler for the format is not available.
 *
 * <p>
 * The content for the following request format is treated as text:
 * json, txt, text, and xml.
 *
 * @author (Fei) John Chen
 */
public class DefaultContentHandler implements ContentHandler {
	private static LogUtil log = LogUtil.getLogger(DefaultContentHandler.class.getName());

	/**
     * Handles result content of a HTTP request.
	 *
	 * @param request  The HTTP request object.
	 * @param response  The HTTP response object.
	 * @param content  The content to be sent.
	 * @param format  The request format.
	 * @throws IOException
	 * @throws ServletException
     */
    public void handle(
    		HttpServletRequest request,
			HttpServletResponse response,
			Object content,
			String format) throws IOException, ServletException {
    	String mimeType = EnvConfig.getInstance().getMimeType(format);

    	if (EnvConfig.getInstance().isTextFile(format)) {
    		content = convertObjectToString(content, format);
    	}

    	if ("xml".equalsIgnoreCase(format)) {
    		String s = (String)content;
    		if (!s.startsWith("<?xml")) s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + s;
    		content = s;
    	}

    	ContentHandlerHelper.publish(response, content, mimeType);
    }

    protected String convertObjectToString(Object data, String format) {
    	StringBuilder sb = new StringBuilder();
    	if (data instanceof Object[]) {
    		for (Object c : (Object[])data) {
    			sb.append(convertObjectToString(c, format));
    		}
    	}
    	else if (data instanceof Collection) {
    		for (Object c : (Collection<?>)data) {
    			sb.append(convertObjectToString(c, format));
    		}
    	}
    	else if (data instanceof Iterator) {
    		Iterator<?> it = (Iterator<?>)data;
    		while(it.hasNext()) {
    			sb.append(convertObjectToString(it.next(), format));
    		}
    	}
    	else if (data instanceof Map) {
    		sb.append(convertMapToString((Map<?, ?>)data, format));
    	}
    	else if (data instanceof ActiveRecord) {
    		sb.append(convertActiveRecordToString((ActiveRecord)data, format));
    	}
    	else {
    		sb.append(data);
    	}
    	return sb.toString();
    }

    protected <K, V> String convertMapToString(Map<K, V> map, String format) {
    	StringBuilder sb = new StringBuilder();
		if ("json".equalsIgnoreCase(format)) {
			sb.append((new JSONObject(map)).toString());
		}
		else if ("xml".equalsIgnoreCase(format)) {
			try {
				sb.append(XML.toString((new JSONObject(map))));
			} catch (JSONException ex) {
				log.error("Failed to conver to xml string: " + ex.getMessage());
			}
		}
		else {
			sb.append(map.toString());
		}
    	return sb.toString();
    }

    protected String convertActiveRecordToString(ActiveRecord record, String format) {
    	StringBuilder sb = new StringBuilder();
		if ("json".equalsIgnoreCase(format)) {
			sb.append(record.toJSON());
		}
		else if ("xml".equalsIgnoreCase(format)) {
			sb.append(record.toXML());
		}
		else {
			sb.append(record.toString());
		}
    	return sb.toString();
    }
}
