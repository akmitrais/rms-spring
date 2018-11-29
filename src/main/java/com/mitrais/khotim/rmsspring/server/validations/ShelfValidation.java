package com.mitrais.khotim.rmsspring.server.validations;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShelfValidation {
	private boolean isValid = false;
	private Map<String, String> messages = new HashMap<>();
	
	public void addMessage(final String key, final String value) {
		messages.put(key, value);
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}