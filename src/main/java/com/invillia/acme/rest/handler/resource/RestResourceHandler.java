package com.invillia.acme.rest.handler.resource;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.request.HttpRequest;

public interface RestResourceHandler {
	public JSONObject executeMethod(HttpRequest request, Context context) throws DataAccessException, ValidationException;
}
