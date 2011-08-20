/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * StringUtilTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class StringUtilTest {
	
    @Test public void test_remove() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	
    	word = "{add:make edit:change}"; expect = "add:make edit:change"; result = StringUtil.remove(word, "{}");
    	assertEquals("remove {} from word " + word, expect, result);
    	
    	word = "member : { prepare=[get post] }"; expect = "member :  prepare=[get post] "; result = StringUtil.remove(word, "{}");
    	assertEquals("remove {} from word " + word, expect, result);
    	
    	word = "add:make"; expect = "add:make"; result = StringUtil.remove(word, "{}");//no change
    	assertEquals("remove {} from word " + word, expect, result);
    	
    	word = ""; expect = ""; result = StringUtil.remove(word, "{}");
    	assertEquals("remove {} from word " + word, expect, result);
    	
    	word = null; expect = null; result = StringUtil.remove(word, "{}");
    	assertEquals("remove {} from word " + word, expect, result);
    }
    
    @Test public void test_replace() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	
    	word = "requirements : { id => /\\\\d+/ }; "; 
    	expect = "requirements : { id => /\\d+/ }; "; 
    	result = StringUtil.replace(word, "\\\\", "\\");
    	assertEquals("replace '\\\\' with '\\' in '" + word + "'", expect, result);
    	
    	word = "xxx//yyy"; 
    	expect = "xxx/yyy"; 
    	result = StringUtil.replace(word, "//", "/");
    	assertEquals("replace '//' with '/' in word '" + word + "'", expect, result);
    	
    	word = "execute(int a, int b)"; 
    	expect = "execute ( int a, int b)"; 
    	result = StringUtil.replace(word, "(", " ( ");
    	assertEquals("replace '(' with ' ( ' in word '" + word + "'", expect, result);
    	
    	word = "execute(int a, int b)"; 
    	expect = "execute(int a, int b ) "; 
    	result = StringUtil.replace(word, ")", " ) ");
    	assertEquals("replace ')' with ' ) ' in word '" + word + "'", expect, result);
    }
}
