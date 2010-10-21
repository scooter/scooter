/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Date;

/**
 * Event class represent an event.
 * 
 * @author (Fei) John Chen
 */
public class Event {
	private Date timestamp;
	private String eventType;
	private Object data;
	
	public Event(String eventType, Object data) {
		timestamp = new Date();
		this.eventType = eventType;
		this.data = data;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public String getEventType() {
		return eventType;
	}

	public Object getData() {
		return data;
	}
	
	public String getEventID() {
		return getEventName() + "-" + timestamp.getTime();
	}
	
	public String getEventName() {
		return this.getClass().getSimpleName() + "-" + eventType;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("timestamp=").append(timestamp).append(", ");
		sb.append("eventType=").append(eventType).append(", ");
		sb.append("data=").append(data);
		return sb.toString();
	}
}
