/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

/**
 * Listener class defines methods of an event listener.
 * 
 * @author (Fei) John Chen
 */
public interface Listener {
	void handleEvent(Event event);
}
