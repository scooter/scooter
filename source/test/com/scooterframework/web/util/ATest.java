/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.scooterframework.test.ScooterTestHelper;

/**
 * ATest class
 * 
 * @author (Fei) John Chen
 *
 */
public class ATest extends ScooterTestHelper {
	
	/**
	 * Test to create an ajax link for TEXT processing
	 */
	@Test
	public void test_createAjaxLabelLinkForText() {
		String expectedLInk = "<a href=\"#\" onclick=\"ajax_link4text('output', '/blog/posts/1', undefined, undefined); return false;\" >show post #1</a>";
		String targetElementId = "output";
        String label = "show post #1";
        String actionPath = "/blog/posts/1";
        Map linkProperties = null;
        String method = null;
        String responseHandlers = null;
        String responseType = null;
        
        String labelLink = A.labelLink(targetElementId, label, actionPath,
        		linkProperties, method, responseHandlers, responseType);
        
		assertEquals("ajax label link1", expectedLInk, labelLink);
	}
	
	/**
	 * Test to create an ajax link for XML processing
	 */
	@Test
	public void test_createAjaxLabelLinkForXML() {
		String expectedLInk = "<a href=\"#\" onclick=\"ajax_link4xml('output', '/blog/posts/1', undefined, {parseXML:myParseFunction}); return false;\" >show post #1</a>";
		String targetElementId = "output";
        String label = "show post #1";
        String actionPath = "/blog/posts/1";
        Map linkProperties = null;
        String method = null;
        String responseHandlers = "{parseXML:myParseFunction}";
        String responseType = "XML";
        
        String labelLink = A.labelLink(targetElementId, label, actionPath,
        		linkProperties, method, responseHandlers, responseType);
        
		assertEquals("ajax label link2", expectedLInk, labelLink);
	}
}
