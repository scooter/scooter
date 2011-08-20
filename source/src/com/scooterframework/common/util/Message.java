/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Message represents a general purpose message.
 *
 * @author (Fei) John Chen
 */
public class Message implements Serializable {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8833981862431036268L;

	/**
     * <p>Construct a message.</p>
     *
     * @param content Message content
     */
    public Message(String content) {
        this(null, content);
    }

    /**
     * <p>Construct a message.</p>
     *
     * @param id an identifier that the message is tied to
     * @param content Message content
     */
    public Message(String id, String content) {
        this.id = id;
        this.content = content;
        createdAt = new Date();
    }


    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return (createdAt == null)?createdAt:(new Date(createdAt.getTime()));
    }

    /**
     * <p>Returns as a string.</p>
     *
     * @return String representation of this message
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message [").append(content).append("]");
        if (id != null && !"".equals(id)) {
            sb.append(" for id [").append(id).append("]");
        }
        sb.append(" created at ").append(createdAt);
        return sb.toString();
    }

    /**
     * <p>The message id.</p>
     */
    protected String id;

    /**
     * <p>The message content.</p>
     */
    protected String content;

    /**
     * <p>Timestamp for this mesasge.</p>
     */
    protected Date createdAt;
}
