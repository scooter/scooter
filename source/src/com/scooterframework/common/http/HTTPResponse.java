/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 * HTTPClient class represents a http response.
 * 
 * @author (Fei) John Chen
 *
 */
public class HTTPResponse {
	private HttpResponse response;

	public HTTPResponse(HttpResponse response) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP response may not be null");
		}

		this.response = response;
	}

	/**
	 * Returns http response status code.
	 * 
	 * @return status code
	 */
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * Returns http response reason phrase.
	 * 
	 * @return reason phrase
	 */
	public String getReasonPhrase() {
		return response.getStatusLine().getReasonPhrase();
	}

	/**
	 * Returns http response protocol version.
	 * 
	 * @return protocol version
	 */
	public String getProtocolVersion() {
		return response.getProtocolVersion().toString();
	}

	/**
	 * Returns http response status.
	 * 
	 * @return status string
	 */
	public String getStatusLine() {
		return response.getStatusLine().toString();
	}

	/**
	 * Returns the content encoding string, if known.
	 * 
	 * @return the content encoding string, or <code>null</code> if the content
	 *         encoding is unknown
	 */
	public String getContentEncodingLine() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentEncoding();
			if (header != null)
				s = header.toString();
		}
		return s;
	}

	/**
	 * Returns the content encoding name, if known.
	 * 
	 * @return the content encoding name, or <code>null</code> if the content
	 *         encoding is unknown
	 */
	public String getContentEncodingName() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentEncoding();
			if (header != null)
				s = header.getName();
		}
		return s;
	}

	/**
	 * Returns the content encoding value, if known.
	 * 
	 * @return the content encoding value, or <code>null</code> if the content
	 *         encoding is unknown
	 */
	public String getContentEncodingValue() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentEncoding();
			if (header != null)
				s = header.getValue();
		}
		return s;
	}

	/**
	 * Returns the content type string, if known.
	 * 
	 * @return the content type header string, or <code>null</code> if the
	 *         content type is unknown
	 */
	public String getContentTypeLine() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentType();
			if (header != null)
				s = header.toString();
		}
		return s;
	}

	/**
	 * Returns the content type name, if known.
	 * 
	 * @return the content type name, or <code>null</code> if the content type
	 *         is unknown
	 */
	public String getContentTypeName() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentType();
			if (header != null)
				s = header.getName();
		}
		return s;
	}

	/**
	 * Returns the content type value, if known.
	 * 
	 * @return the content type value, or <code>null</code> if the content type
	 *         is unknown
	 */
	public String getContentTypeValue() {
		String s = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header header = entity.getContentType();
			if (header != null)
				s = header.getValue();
		}
		return s;
	}

	/**
	 * Returns the length of the content, if known.
	 * 
	 * @return the number of bytes of the content, or a negative number if
	 *         unknown. If the content length is known but exceeds
	 *         {@link java.lang.Long#MAX_VALUE Long.MAX_VALUE}, a negative
	 *         number is returned.
	 */
	public long getContentLength() {
		long l = -1;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			l = entity.getContentLength();
		}
		
		if (l == -1) {
			Set<String> set = getHeaderValuesForName("Content-Length");
			if (set.size() > 0) {
				Iterator<String> it = set.iterator();
				if (it.hasNext()) {
					String s = it.next();
					try {
						l = Long.parseLong(s);
					} catch (Exception ex) {
						;
					}
				}
			}
		}
		return l;
	}

	/**
	 * Returns all headers.
	 * 
	 * @return headers array
	 */
	public String[] getAllHeaders() {
		Header[] headers = response.getAllHeaders();
		if (headers == null || headers.length == 0)
			return null;

		String[] hs = new String[headers.length];
		for (int i = 0; i < headers.length; i++) {
			hs[i] = headers[i].toString();
		}
		return hs;
	}

	/**
	 * Returns a map of all headers. If there is no headers, an empty map
	 * instance is returned.
	 * 
	 * @return headers map
	 */
	public Map<String, String> getAllHeadersAsMap() {
		Header[] headers = response.getAllHeaders();
		if (headers == null || headers.length == 0)
			return new HashMap<String, String>();

		Map<String, String> hs = new HashMap<String, String>(headers.length);
		for (int i = 0; i < headers.length; i++) {
			hs.put(headers[i].getName(), headers[i].getValue());
		}
		return hs;
	}

	/**
	 * Returns a set of values in the header associated with the <tt>name</tt>.
	 * 
	 * @param name  name of a head property
	 * @return a set of values
	 */
	public Set<String> getHeaderValuesForName(String name) {
		HeaderIterator it = response.headerIterator(name);
		Set<String> values = new HashSet<String>();
		while (it.hasNext()) {
			Header header = it.nextHeader();
			HeaderElement[] elements = header.getElements();
			for (HeaderElement element : elements) {
				values.add(element.getName());
			}
		}
		return values;
	}

	/**
	 * Returns a set of allowed methods.
	 * 
	 * @return a set of allowed methods.
	 */
	public Set<String> getAllowedMethods() {
		return getHeaderValuesForName("Allow");
	}

	/**
	 * Returns content as string by using the platform's default charset. 
	 * 
	 * Please notice that you can only call one of
	 * the <tt>getContentAsXXX()</tt> once.
	 * 
	 * @return a string of content
	 * @throws IOException
	 */
	public String getContentAsString() throws IOException {
		byte[] content = getContentAsBytes();
		return (content != null) ? (new String(content)) : "";
	}

	/**
	 * Returns content as string by using the specified charset.
	 * 
	 * Please notice that you can only call one of
	 * the <tt>getContentAsXXX()</tt> once.
	 * 
	 * @param charsetName  the name of a supported charset
	 * @return a string of content
	 * @throws IOException
	 */
	public String getContentAsString(String charsetName) throws IOException {
		byte[] content = getContentAsBytes();
		return (content != null) ? (new String(content, charsetName)) : "";
	}

	/**
	 * Returns content as bytes. 
	 * 
	 * Please notice that you can only call one of the 
	 * <tt>getContentAsXXX()</tt> once.
	 * 
	 * @return a byte array of content
	 * @throws IOException
	 */
	public byte[] getContentAsBytes() throws IOException {
		byte[] content = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = null;
			ByteArrayOutputStream out = null;
			try {
				is = entity.getContent();
				if (is == null) {
					return new byte[] {};
				}
				out = new ByteArrayOutputStream(2048);
				int n = 0;
				byte[] b = new byte[2048];
				while ((n = is.read(b)) != -1) {
					out.write(b, 0, n);
				}
			} catch (IOException ex) {
				throw ex;
			} finally {
				closeInputStream(is);
				closeOutputStream(out);
			}

			content = out.toByteArray();
		}
		return content;
	}

	/**
	 * Returns content as input stream. 
	 * 
	 * Please notice that you can only call one of the 
	 * <tt>getContentAsXXX()</tt> once.
	 * 
	 * @return a stream of content
	 * @throws IOException
	 */
	public InputStream getContentAsInputStream() throws IOException {
		InputStream is = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			is = entity.getContent();
		}
		return is;
	}

	private void closeInputStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (Exception ex) {
				is = null;
			}
		}
	}

	private void closeOutputStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (Exception ex) {
				out = null;
			}
		}
	}
}
