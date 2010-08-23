/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.Calendar;
import java.util.Date;

import com.scooterframework.test.ScooterTest;

/**
 * DTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class DTest extends ScooterTest {
	
	/**
	 * Test to display friendly time message
	 */
	public void test_message() {
		int delay = 10;
		long now = Calendar.getInstance().getTimeInMillis();
		Date d = null;
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now);
		assertEquals("moments from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_SECOND * 1);
		assertEquals("1 second from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_SECOND * 5);
		assertEquals("5 seconds from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_MINUTE * 1);
		assertEquals("1 minute from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_MINUTE * 5);
		assertEquals("5 minutes from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_HOUR * 1);
		assertEquals("1 hour from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_HOUR * 5);
		assertEquals("5 hours from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_DAY * 1);
		assertEquals("1 day from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_DAY * 5);
		assertEquals("5 days from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_WEEK * 1);
		assertEquals("1 week from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_WEEK * 2);
		assertEquals("2 weeks from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_MONTH * 1);
		assertEquals("1 month from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_MONTH * 5);
		assertEquals("5 months from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_YEAR * 1);
		assertEquals("1 year from now", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(delay + now + D.ONE_YEAR * 5);
		assertEquals("5 years from now", D.message(d));
		
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - 10);
		assertEquals("moments ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_SECOND * 1);
		assertEquals("1 second ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_SECOND * 5);
		assertEquals("5 seconds ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_MINUTE * 1);
		assertEquals("1 minute ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_MINUTE * 5);
		assertEquals("5 minutes ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_HOUR * 1);
		assertEquals("1 hour ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_HOUR * 5);
		assertEquals("5 hours ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_DAY * 1);
		assertEquals("1 day ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_DAY * 5);
		assertEquals("5 days ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_WEEK * 1);
		assertEquals("1 week ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_WEEK * 2);
		assertEquals("2 weeks ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_MONTH * 1);
		assertEquals("1 month ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_MONTH * 5);
		assertEquals("5 months ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_YEAR * 1);
		assertEquals("1 year ago", D.message(d));
		
		now = Calendar.getInstance().getTimeInMillis();
		d = new Date(now - D.ONE_YEAR * 5);
		assertEquals("5 years ago", D.message(d));
	}
}
