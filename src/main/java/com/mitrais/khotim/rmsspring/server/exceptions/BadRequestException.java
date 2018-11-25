package com.mitrais.khotim.rmsspring.server.exceptions;

import java.util.Map;

public class BadRequestException extends RuntimeException {
	private final static long serialVersionUID = 1L;
	
	public BadRequestException(Map<String, String> messages) {
	}
}
