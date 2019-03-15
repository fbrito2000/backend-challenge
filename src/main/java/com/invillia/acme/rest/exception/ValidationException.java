package com.invillia.acme.rest.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {

	private static final long serialVersionUID = -2522852977145105199L;
	List<String> messages;

	public ValidationException() {
		this.messages = new ArrayList<String>();
	}

	public ValidationException(String message) {
		if (message != null) {
			this.messages = new ArrayList<String>();
			messages.add(message);
		}
	}

	public ValidationException(List<String> messages) {
		this.messages = messages;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

	public void addMessages(List<String> messages) {
		this.messages.addAll(messages);
	}

	public String getMessagesAsString() {
		if (messages.isEmpty()) {
			messages.add("Validation error.");
		}
		return this.messages.toString();
	}

	@Override
	public String getMessage() {
		return this.getMessagesAsString();
	}

}
