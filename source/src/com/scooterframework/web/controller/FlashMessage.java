/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.util.Message;

/**
 * FlashMessage contains information for the next request only.
 * 
 * @author (Fei) John Chen
 */
public class FlashMessage implements Serializable {

    /**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 4864574800679802319L;

	public void addMessage(String type, String message) {
        addMessage(type, new Message(message));
    }
    
    public void addMessage(String type, Message message) {
        List<Message> messages = (List<Message>)typedMessages.get(type);
        if (messages == null) {
            messages = new ArrayList<Message>();
            typedMessages.put(type, messages);
        }
        messages.add(message);
    }

    /**
     * Returns all messages for the specific type.
     * 
     * @param type
     * @return list of messages associated with the type
     */
    public List<Message> getAll(String type) {
        if (type == null || "".equals(type)) return null;
        return (List<Message>)typedMessages.get(type);
    }
    
    /**
     * Returns the first message for the specific type
     * 
     * @param type
     * @return message associated with the type
     */
    public String getFirst(String type) {
        List<Message> messages = getAll(type);
        if (messages == null || messages.size() == 0) return null;
        
        Message message = (Message) messages.get(0);
        return (message != null)?message.getContent():null;
    }
    
    /**
     * Returns the last message for the specific type
     * 
     * @param type
     * @return message associated with the type
     */
    public String getLast(String type) {
        List<Message> messages = getAll(type);
        if (messages == null || messages.size() == 0) return null;
        
        Message message = (Message) messages.get(messages.size()-1);
        return (message != null)?message.getContent():null;
    }
    
    /**
     * Counts number of messages.
     * 
     * @return int number of messages.
     */
    public int count() {
        int total = 0;
        for (Map.Entry<String, List<Message>> entry : typedMessages.entrySet()) {
            List<Message> list = entry.getValue();
            if (list == null) continue;
            total += list.size();
        }
        return total;
    }

    private Map<String, List<Message>> typedMessages = new HashMap<String, List<Message>>();
}
