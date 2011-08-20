/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.scooterframework.common.logging.LogUtil;

/**
 * EventsManager class manages events.
 * 
 * @author (Fei) John Chen
 *
 */
public class EventsManager {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
	private static final EventsManager me = new EventsManager();
	private final ConcurrentHashMap<String, List<Listener>> listenersMap = 
		new ConcurrentHashMap<String, List<Listener>>();
	
	private EventsManager() {
	}
	
	/**
	 * Returns the singleton instance of the <tt>EventsManager</tt>.
	 * 
	 * @return the singleton instance of the <tt>EventsManager</tt>.
	 */
	public static EventsManager getInstance() {
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
			listeners = new CopyOnWriteArrayList<Listener>();
			List<Listener> oldListeners = listenersMap.putIfAbsent(eventType, listeners);
			if (oldListeners != null) {
				listeners = oldListeners;
			}
		}
		if (!listeners.contains(listener)) listeners.add(listener);
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
		for (Map.Entry<String, List<Listener>> entry : listenersMap.entrySet()) {
			List<Listener> listeners = entry.getValue();
			listeners.remove(listener);
		}
	}
	
	/**
	 * Publishes an event.
	 * 
	 * @param event The event to be published.
	 */
	public void publishEvent(Event event) {
		if (event == null) return;
		List<Listener> listeners = listenersMap.get(event.getEventType());
		if (listeners != null) {
			for (Iterator<Listener> it = listeners.iterator(); it.hasNext();) {
				Listener listener = it.next();
				try {
					listener.handleEvent(event);
				}
				catch (RuntimeException ex) {
					log.error("Error in calling listener: " + ex.getMessage());
					it.remove();
				}
			}
		}
	}
}
