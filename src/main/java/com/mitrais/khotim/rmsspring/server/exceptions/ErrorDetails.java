package com.mitrais.khotim.rmsspring.server.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ErrorDetails {
    private final Date timestamp = new Date();
    private Map<String, String> messages = new HashMap<>();
    private String details;

    public ErrorDetails(String message, String details) {
    	this.messages.put("error", message);
    	this.details = details;
    }
    
    public ErrorDetails(Map<String, String> messages, String details) {
        this.messages = messages;
        this.details = details;
    }
    
    /**
     * Gets timestamp.
     *
     * @return Date The value of timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Gets details.
     *
     * @return String The value of details.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Gets messages.
     * 
	 * @return Map The messages.
	 */
	public Map<String, String> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}
}
