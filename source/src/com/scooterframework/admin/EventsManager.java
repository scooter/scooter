/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EventsManager {
	private static EventsManager me;
	private Map<String, List<Listener>> listenersMap = new HashMap<String, List<Listener>>();
	
	private EventsManager() {
	}
	
	/**
	 * Returns the singleton instance of the <tt>EventsManager</tt>.
	 * 
	 * @return the singleton instance of the <tt>EventsManager</tt>.
	 */
	public static EventsManager getInstance() {
		if (me == null) me = new EventsManager();
		return me;
	}
	
	/**
	 * Register a listener for a specific event type.
	 * 
	 * @param eventType The event type
	 * @param listener The event listener to be registered.
	 */
	public void registerListener(String eventType, Listener listener) {
		List<Listener> listeners = listenersMap.get(eventType);
		if (listeners == null) {
			listeners = new ArrayList<Listener>();
			listenersMap.put(eventType, listeners);
		}
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener for a specific event type.
	 * 
	 * @param eventType The event type
	 * @param listener The event listener to be removed.
	 */
	public void removeListener(String eventType, Listener listener) {
		List<Listener> listeners = listenersMap.get(eventType);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Removes a listener for all event types.
	 * 
	 * @param listener The event listener to be removed.
	 */
	public void removeListener(Listener listener) {
		Set<Entry<String, List<Listener>>> set = listenersMap.entrySet();
		for (Entry<String, List<Listener>> entry : set) {
			List<Listener> listeners = entry.getValue();
			if (listeners != null && listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}
	
	/**
	 * Publishes an event.
	 * 
	 * @param event The event to be published.
	 */
	public void publishEvent(Event event) {
		if (event == null) return;
		notifyListeners(event);
	}
	
	private void notifyListeners(Event event) {
		List<Listener> listeners = listenersMap.get(event.getEventType());
		if (listeners != null) {
			for (Listener listener: listeners) {
				listener.handleEvent(event);
			}
		}
	}
}
