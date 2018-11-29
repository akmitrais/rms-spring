package com.mitrais.khotim.rmsspring.server.validations;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

public class ValidationMessage {
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
