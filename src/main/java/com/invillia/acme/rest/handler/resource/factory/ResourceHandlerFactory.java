package com.invillia.acme.rest.handler.resource.factory;

import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.handler.resource.RestResourceHandler;
import com.invillia.acme.rest.handler.resource.impl.OrderRequestHandlerImpl;
import com.invillia.acme.rest.handler.resource.impl.StoreRequestHandlerImpl;

public class ResourceHandlerFactory {

	private final String ORDER = "/order";
	private final String STORE = "/store";
	private final RestRequestHandler restApiRequestHandler;
	private final String resourcePath;

	public ResourceHandlerFactory(String resourcePath, RestRequestHandler restApiRequestHandler) throws ValidationException {
		if (restApiRequestHandler == null) {
			throw new ValidationException("FATAL: RestRequestHandler restApiRequestHandler is NULL");
		} else {
			this.restApiRequestHandler = restApiRequestHandler;
		}
		if (resourcePath != null) {
			this.resourcePath = resourcePath;
		} else {
			throw new ValidationException("FATAL: resourceName NAME NOT MAPPED: " + resourcePath);
		}
	}

	public RestResourceHandler getImpl() throws ValidationException {
		if (resourcePath.startsWith(STORE))
			return new StoreRequestHandlerImpl(restApiRequestHandler);
		if (resourcePath.startsWith(ORDER))
			return new OrderRequestHandlerImpl(restApiRequestHandler);
		throw new ValidationException("FATAL: RESOURCE NAME NOT IMPLEMENTED: " + resourcePath);
	}
}
