package com.invillia.acme.rest.exception;

public class HTTPMethodNotImplemeted extends Exception {
	
	private static final long serialVersionUID = 3662587922272514313L;

	public HTTPMethodNotImplemeted() {
		super("Not known or implemented method.");
	}
	
	public HTTPMethodNotImplemeted(String httpMethodName) {
		super("Not known or implemented method named '" + httpMethodName + "'.");
		
	}

}
