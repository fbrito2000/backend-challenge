package com.invillia.acme.rest.exception;

public class DataAccessException extends Exception {

	private static final long serialVersionUID = -4221295489678555252L;

	public DataAccessException(String tableName) {
		super("Database access error while accessing '" + tableName + "' table.");
	}

	public DataAccessException(String tableName, Exception cause) {
		super("Database access error while accessing '" + tableName + "' table.", cause);
	}

	public DataAccessException() {
		super("Database access error while accessing DynamoDB");
	}

}
