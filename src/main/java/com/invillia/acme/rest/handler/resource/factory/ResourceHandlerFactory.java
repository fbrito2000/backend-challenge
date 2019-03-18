package com.invillia.acme.rest.handler.resource.factory;

import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.handler.resource.RestResourceHandler;
import com.invillia.acme.rest.handler.resource.impl.OrderRequestHandlerImpl;
import com.invillia.acme.rest.handler.resource.impl.PaymentRequestHandlerImpl;
import com.invillia.acme.rest.handler.resource.impl.StoreRequestHandlerImpl;

public class ResourceHandlerFactory {

	private final String ORDER = "/order";
	private final String ORDER_ID = "/order/{id}";
	private final String STORE = "/store";
	private final String STORE_ID = "/store/{id}";
	private final String PAYMENT = "/order/{id}/payment";
	private final RestRequestHandler restApiRequestHandler;
	private final String resource;

	public ResourceHandlerFactory(String resource, RestRequestHandler restApiRequestHandler) throws ValidationException {
		
		if (restApiRequestHandler == null) {
			throw new ValidationException("FATAL: RestRequestHandler restApiRequestHandler is NULL");
		} else {
			this.restApiRequestHandler = restApiRequestHandler;
		}
		if (resource != null) {
			this.resource = resource;
		} else {
			throw new ValidationException("FATAL: resourceName NAME NOT MAPPED: " + resource);
		}
	}

	public RestResourceHandler getImpl() throws ValidationException {
		if (resource.equals(STORE) || resource.equals(STORE_ID))
			return new StoreRequestHandlerImpl(restApiRequestHandler);
		if (resource.equals(ORDER) || resource.equals(ORDER_ID))
			return new OrderRequestHandlerImpl(restApiRequestHandler);
		if (resource.equals(PAYMENT))
			return new PaymentRequestHandlerImpl(restApiRequestHandler);

		throw new ValidationException("FATAL: RESOURCE NAME NOT IMPLEMENTED: " + resource);
	}
}
