package com.invillia.acme.rest.exception;

public class UnhandledContentTypeException extends Exception {
	public UnhandledContentTypeException() {
		super("Unhandled Content-type declared in HTTP Headers.");
	}
	
	public UnhandledContentTypeException(String unhandledContentType) {
		super("Unhandled Content-type declared in HTTP Headers: '" + unhandledContentType + "'.");
	}

}
