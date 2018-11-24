package com.mitrais.khotim.rmsspring.server.exceptions;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
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
     * Gets message.
     *
     * @return String The value of message.
     */
    public String getMessage() {
        return message;
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
     * Gets validation error messages.
     *
     * @param errors Validation error.
     * @return Error messages with field as key and error message as value.
     */
    public static Map<String, String> getMessages(BindingResult errors) {
        Map<String, String> messages = new HashMap<>();

        for (ObjectError error : errors.getAllErrors()) {
            messages.put(((FieldError)error).getField(), error.getDefaultMessage());
        }

        return messages;
    }
}
