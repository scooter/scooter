/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


/**
 * The <tt>OrderedProperties</tt> class extends <tt>Properties</tt> class. 
 * The order of properties are preserved. 
 * 
 * @author (Fei) John Chen
 */
public class OrderedProperties extends Properties {
	
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5913286672979011301L;
	
	private static final String commentChars = "#!";
	private static final String separatorChars = "=:";
	
	/**
	 * Holder of keys.
	 */
	protected List<Object> keys;

    /**
     * Creates an empty property list with no default values.
     */
    public OrderedProperties() {
    	this(null);
    }
    
    public int hashCode() {
    	return super.hashCode();
    }
    
    public boolean equals(Object obj) {
    	return super.equals(obj);
    }

    /**
     * Creates an empty property list with the specified defaults.
     *
     * @param   defaults   the defaults.
     */
    public OrderedProperties(Properties defaults) {
    	super(defaults);
    	keys = new ArrayList<Object>();
    	if (defaults != null) {
    		Iterator<Object> it = defaults.keySet().iterator();
    		while(it.hasNext()) {
    			keys.add(it.next());
    		}
    	}
    }
    
    /**
     * Returns an enumeration of keys.
     * 
     * @return an enumeration of keys.
     */
    public Enumeration<Object> keys() {
    	Vector<Object> v = new Vector<Object>(keys);
    	return v.elements();
    }
    
    /**
     * Returns an iterator of keys.
     * 
     * @return an iterator of keys.
     */
    public Iterator<Object> keyIterator() {
    	return keys.iterator();
    }
    
    /**
     * Returns a list of keys.
     * 
     * @return a list of keys.
     */
    public List<Object> keyList() {
    	return keys;
    }
    
    /**
     * Reads a property list (key and element pairs) from an input stream 
     * with "utf-8" encoding. 
     * 
     * <p>
     * The input stream remains open after this method returns.</p>
     * 
     * @param inStream       an input stream.
     * @throws IOException   if an error occurred when reading from 
     *                       the input stream.
     */
    public void load(InputStream inStream) throws IOException {
    	load(inStream, "utf-8");
    }
    
    /**
     * Reads a property list (key and element pairs) from an input stream. 
     * 
     * <p>
     * The input stream remains open after this method returns.</p>
     * 
     * @param is             an input stream.
     * @param encoding       a character encoding scheme.
     * @throws IOException   if an error occurred when reading from 
     *                       the input stream.
     */
    public void load(InputStream is, String encoding) 
    throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
    	String lastLine = "";
    	boolean lastLineUncomplete = false;
    	while (true) {
    		String line = br.readLine();
    		if (line == null) break;
    		
    		line = line.trim();
    		if (line.length() == 0) {
    			continue;
    		}
    		
    		if (isCommentLine(line)) continue;
    		
    		if (lastLineUncomplete) {
    			line = lastLine + line;
    		}
    		
    		if (line.endsWith("\\")) {
    			lastLineUncomplete = true;
    			lastLine = line.substring(0, line.length() - 1);
    		}
    		else {
    			lastLineUncomplete = false;
    			lastLine = "";
    		}
    		
    		if (!lastLineUncomplete) {
    			int separatorIndex = separatorIndex(line);
    			if (separatorIndex == -1) {
    				put(line, "");
    			}
    			else {
    				String key = line.substring(0, separatorIndex);
    				String value = line.substring(separatorIndex + 1);
    				put(key.trim(), value.trim());
    			}
    		}
    	}
    }
    
    private boolean isCommentLine(String line) {
    	char firstChar = line.charAt(0);
		return (commentChars.indexOf(firstChar) != -1)?true:false;
    }
    
    private int separatorIndex(String line) {
    	int sep = -1;
    	int len = separatorChars.length();
    	for (int i = 0; i < len; i++) {
    		char c = separatorChars.charAt(i);
    		int findIndex = line.indexOf(c);
    		if (findIndex != -1) {
    			sep = findIndex;
    			break;
    		}
    	}
    	return sep;
    }
    
    /**
     * 
     * Writes a property list (key and element pairs) to an output stream 
     * with "utf-8" encoding. 
     * 
     * <p>
     * The output stream remains open after this method returns.</p>
     * 
     * @param os             an output stream.
     * @param header         a description of the property list.
     * @throws IOException   if an error occurred when writing to  
     *                       the output stream.
     */
    public void store(OutputStream os, String header)
    throws IOException {
    	store(os, header, "utf-8");
    }
    
    /**
     * 
     * Writes a property list (key and element pairs) to an output stream. 
     * 
     * <p>
     * The output stream remains open after this method returns.</p>
     * 
     * @param os             an output stream.
     * @param header         a description of the property list.
     * @param encoding       a character encoding scheme.
     * @throws IOException   if an error occurred when writing to  
     *                       the output stream.
     */
    public void store(OutputStream os, String header, String encoding)
    throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, encoding));
        if (header != null) {
        	bw.write("#" + header);
        	bw.newLine();
        }
        for (Enumeration<Object> e = keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object val = get(key);
            bw.write(key + "=" + ((val != null)?storeConvert(val.toString()):null));
        	bw.newLine();
        }
        bw.flush();
    }

    /**
     * Returns an enumeration of all the keys in this property list. The 
     * names are in the same order as they are added to the property. The 
     * value returned is the result of the <tt>OrderedProperties</tt> call 
     * to <tt>keys</tt>.
     *
     * @return  an enumeration of all the keys in this property list.
     */
    public Enumeration<Object> propertyNames() {
    	return keys();
    }

    /**
     * Calls the <tt>OrderedProperties</tt> method <tt>put</tt>. Provided 
     * for parallelism with the <tt>getProperty</tt> method. Enforces use of
     * strings for property keys and values. The value returned is the
     * result of the <tt>OrderedProperties</tt> call to <tt>put</tt>.
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return     the previous value of the specified key in this property
     *             list, or <tt>null</tt> if it did not have one.
     */
    public Object setProperty(String key, String value) {
        return put(key, value);
    }
    
    /**
     * <p>
     * Maps the specified <tt>key</tt> to the specified 
     * <tt>value</tt> in this ordered properties. Neither the key nor 
     * the value can be <tt>null</tt>. </p>
     * 
     * <p>
     * The value can be retrieved by calling the <tt>get</tt> method 
     * with a key that is equal to the original key. </p>
     *
     * @param      key     a key.
     * @param      value   a value.
     * @return     the previous value of the specified key in this properties,
     *             or <tt>null</tt> if it did not have one.
     * @exception  NullPointerException  if the key or value is
     *               <tt>null</tt>.
     * @see     Object#equals(Object)
     * @see     #get(Object)
     */
    public Object put(Object key, Object value) {
    	if (!keys.contains(key)) keys.add(key);
    	if (value != null) {
    		value = loadConvert(value.toString());
    	}
    	return super.put(key, value);
    }
    
    private String loadConvert(String s) {
    	if (s != null) {
    		s = StringUtil.replace(s, "\\\\", "\\");
    	}
    	return s;
    }
    
    private String storeConvert(String s) {
    	if (s != null) {
    		s = StringUtil.replace(s, "\\", "\\\\");
    	}
    	return s;
    }
    
    /**
     * Clears this property so that it contains no keys. 
     */
    public void clear() {
    	keys.clear();
    	super.clear();
    }
}
