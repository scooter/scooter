/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Date;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * WebSessionListener class monitors when sessions are created and destroyed on
 * the application server.
 * 
 * @author (Fei) John Chen
 */
public class WebSessionListener implements HttpSessionListener {
	private static int sessionCount = 0;
	private static int sessionCountMax = 0;
	private static Date sessionCountMaxDate = new Date();

	public void sessionCreated(HttpSessionEvent se) {
		synchronized (this) {
			sessionCount++;
			if (sessionCount > sessionCountMax) {
				sessionCountMax = sessionCount;
				sessionCountMaxDate = new Date();
			}
		}
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		synchronized (this) {
			--sessionCount;
		}
	}
	
	/**
	 * Returns number of current sessions in the container.
	 */
	public static int getSessionCount() {
		return sessionCount;
	}
	
	/**
	 * Returns the maximum number of current sessions.
	 */
	public static int getSessionCountMax() {
		return sessionCountMax;
	}
	
	/**
	 * Returns the date of the maximum number of current sessions.
	 */
	public static Date getSessionCountMaxDate() {
		return sessionCountMaxDate;
	}
}
