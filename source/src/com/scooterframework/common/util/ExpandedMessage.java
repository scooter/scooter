/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import com.scooterframework.i18n.Messages;

/**
 * ExpandedMessage represents a parsed message the content of which is a 
 * combination of the message associated with the <tt>messageKey</tt> stored 
 * in a resource file and the values related to the message. 
 * 
 * <p>When there is no message associated with the <tt>messageKey</tt> in 
 * resource files, the <tt>messageKey</tt> itself is used as the message for 
 * parsing.</p>
 * 
 * @author (Fei) John Chen
 */
public class ExpandedMessage extends Message {
    
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -3173168788617257705L;

	/**
     * <p>Construct a message.</p>
     * 
     * @param messageKey a string representing a key in a resource file
     */
    public ExpandedMessage(String messageKey) {
        this(null, messageKey, (Object[])null);
    }
    
    /**
     * <p>Construct a message.</p>
     * 
     * @param id Message id
     * @param messageKey a string representing a key in a resource file
     */
    public ExpandedMessage(String id, String messageKey) {
        this(id, messageKey, (Object[])null);
    }
    
    /**
     * <p>Construct a message with a specific replacement value.</p>
     * 
     * @param id Message id
     * @param messageKey a string representing a key in a resource file
     * @param value a value that can be used in the message
     */
    public ExpandedMessage(String id, String messageKey, Object value) {
        this(id, messageKey, new Object[]{value});
    }
    
    /**
     * <p>Construct a message with a specific replacement value.</p>
     * 
     * @param id Message id
     * @param messageKey a string representing a key in a resource file
     * @param value0 a value that can be used in the message
     * @param value1 a value that can be used in the message
     */
    public ExpandedMessage(String id, String messageKey, Object value0, Object value1) {
        this(id, messageKey, new Object[]{value0, value1});
    }
    
    /**
     * <p>Construct a message with a specific replacement value.</p>
     * 
     * @param id Message id
     * @param messageKey a string representing a key in a resource file
     * @param value0 a value that can be used in the message
     * @param value1 a value that can be used in the message
     * @param value2 a value that can be used in the message
     */
    public ExpandedMessage(String id, String messageKey, Object value0, Object value1, Object value2) {
        this(id, messageKey, new Object[]{value0, value1, value2});
    }
    
    /**
     * <p>Construct a message with specific replacement values.</p>
     * 
     * @param id Message id
     * @param messageKey a string representing a key in a resource file
     * @param values an array of values that can be used in the message
     */
    public ExpandedMessage(String id, String messageKey, Object[] values) {
        super(id, messageKey);
        this.messageKey = messageKey;
        this.values = values;
        processContent(messageKey, values);
    }

    /**
     * <p>Gets the content for this message.</p>
     *
     * @return The content for this message
     */
    public String getContent() {
        return content;
    }
    
    /**
     * <p>Gets the messageKey for this message.</p>
     *
     * @return The messageKey for this message
     */
    public String getMessageKey() {
        return messageKey;
    }
    
    /**
     * <p>Returns a String in the format: key[value1, value2, etc].</p>
     *
     * @return String representation of this message
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Message [").append(content).append("]");
        if (id != null && !"".equals(id)) {
            sb.append(" for id [").append(id).append("]");
        }
        if (messageKey != null && !"".equals(messageKey)) {
            sb.append(" with messageKey [").append(messageKey).append("]");
        }
        
        sb.append(" values [");
        if (values != null) {
            for (int i = 0; i < this.values.length; i++) {
                sb.append(this.values[i]);

                // don't append comma to last entry
                if (i < (this.values.length - 1)) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
        
        sb.append(" created at ").append(createdAt);
        return sb.toString();
    }


    /**
     * Combines content with values.
     * 
     * @param messageKey
     * @param values
     */
    private void processContent(String messageKey, Object[] values) {
        content = Messages.get(messageKey, values);
    }
    
    /**
     * <p>The key to content in resource file.</p>
     */
    protected String messageKey;
    
    /**
     * <p>The replacement values.</p>
     */
    private Object[] values;
}
