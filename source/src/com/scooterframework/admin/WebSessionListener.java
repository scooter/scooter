/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * WebSessionListener class monitors when sessions are created and destroyed on
 * the application server.
 * 
 * @author (Fei) John Chen
 */
public class WebSessionListener implements HttpSessionListener {
	private static final AtomicLong sessionCount = new AtomicLong(0);
	private static long sessionCountMax = 0;
	private static Date sessionCountMaxDate;

	public void sessionCreated(HttpSessionEvent se) {
		sessionCount.incrementAndGet();
		if (sessionCount.get() > sessionCountMax) {
			sessionCountMax = sessionCount.get();
			sessionCountMaxDate = new Date();
		}
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		sessionCount.decrementAndGet();
	}
	
	/**
	 * Returns number of current sessions in the container.
	 */
	public static long getSessionCount() {
		return sessionCount.get();
	}
	
	/**
	 * Returns the maximum number of current sessions.
	 */
	public static long getSessionCountMax() {
		return sessionCountMax;
	}
	
	/**
	 * Returns the date of the maximum number of current sessions.
	 */
	public static Date getSessionCountMaxDate() {
		return (sessionCountMaxDate == null)?sessionCountMaxDate:(new Date(sessionCountMaxDate.getTime()));
	}
}
