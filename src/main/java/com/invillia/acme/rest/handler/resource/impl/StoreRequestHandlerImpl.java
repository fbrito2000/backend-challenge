package com.invillia.acme.rest.handler.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.invillia.acme.rest.enums.StoreServiceFactoryEnum;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.HTTPMethodNotImplemeted;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.handler.resource.RestResourceHandler;
import com.invillia.acme.rest.model.Store;
import com.invillia.acme.rest.request.HttpRequest;
import com.invillia.acme.rest.response.JSONObjectResponseBuilder;
import com.invillia.acme.rest.service.StoreService;
import com.invillia.acme.rest.service.factory.StoreServiceFactory;
import com.invillia.acme.rest.to.StoreTO;

public class StoreRequestHandlerImpl extends RestRequestHandler implements RestResourceHandler {

	public final static String RESOURCE_NAME = "store";
	StoreService storeService = new StoreServiceFactory(StoreServiceFactoryEnum.LOCAL.name()).getImpl();
	StoreTO transferObject = new StoreTO();

	public StoreRequestHandlerImpl(RestRequestHandler parent) {
		super(parent);
	}

	public JSONObject handlePost(HttpRequest request, Context context) throws DataAccessException, ValidationException {

		JSONObjectResponseBuilder responseBuilder = null;

		JSONObject requestBody = request.getBody();

			if (requestBody == null) {
				ValidationException ve = new ValidationException();
				ve.addMessage("request body is missing. It should contain Json data for store creation.");
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, ve);
			} else {
				Store store = storeService.createOrUpdate(transferObject.toModel(requestBody));

				List<JSONObject> storeJSONObjectList = new ArrayList<JSONObject>();
				storeJSONObjectList.add(transferObject.toJsonObject(store));
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_CREATED, null, storeJSONObjectList, null);

			}

		return responseBuilder.build();
	}

	private JSONObject handleGet(HttpRequest request, Context context) throws DataAccessException {
		JSONObjectResponseBuilder responseBuilder = null;

		StoreFilter filter = transferObject.toFilter(request.getQueryStringParameters(), request.getPathParameters());
			List<Store> stores = this.storeService.query(filter);
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_OK, null, transferObject.toJsonObjectList(stores), null);

		return responseBuilder.build();
	}

	@Override
	public JSONObject executeMethod(HttpRequest request, Context context) throws DataAccessException, ValidationException  {

		switch (request.getHttpMethod()) {
		case (HttpRequest.HTTP_METHOD_POST):
			return handlePost(request, context);
		case (HttpRequest.HTTP_METHOD_GET):
			return handleGet(request, context);
		default:
			return new JSONObjectResponseBuilder(HttpStatus.SC_NOT_IMPLEMENTED, null, null, new HTTPMethodNotImplemeted()).build();
		}
	}
}