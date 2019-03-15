package com.invillia.acme.rest.handler.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.enums.StoreServiceFactoryEnum;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.HTTPMethodNotImplemeted;
import com.invillia.acme.rest.exception.ValidationException;
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

	public JSONObject handlePost(HttpRequest request, Context context) {

		JSONObjectResponseBuilder responseBuilder = null;

		String requestBody = (String) request.getBody();
		JSONObject storeJSONObject;
		try {
			if (!StringUtils.isNullOrEmpty(requestBody)) {
				storeJSONObject = (JSONObject) PARSER.parse((String) request.getBody());
			} else {
				storeJSONObject = null;
			}
			if (storeJSONObject == null) {
				ValidationException ve = new ValidationException();
				ve.addMessage("body is missing");
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, ve);
			} else {
				Store store = storeService.createOrUpdate(transferObject.toPojo(storeJSONObject));

				List<JSONObject> storeJSONObjectList = new ArrayList<JSONObject>();
				storeJSONObjectList.add(transferObject.toJsonObject(store));
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_CREATED, null, storeJSONObjectList, null);

			}
		} catch (DataAccessException e) {
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, null, e);
		} catch (ValidationException e) {
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, e);
		} catch (ParseException e) {
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, e);
		}
		return responseBuilder.build();
	}

	private JSONObject handleGet(HttpRequest request, Context context) {
		JSONObjectResponseBuilder responseBuilder = null;

		Store storeQuery = new Store();
		if (!StringUtils.isNullOrEmpty(request.getQueryStringParameters().get("address"))) {
			storeQuery.setAddress((String) request.getQueryStringParameters().get("address"));
		}
		if (!StringUtils.isNullOrEmpty(request.getQueryStringParameters().get("name"))) {
			storeQuery.setName((String) request.getQueryStringParameters().get("name"));
		}
		try {
			List<Store> stores = this.storeService.query(storeQuery);
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_OK, null, transferObject.toJsonObjectList(stores), null);
		} catch (DataAccessException e) {
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, null, e);
		}
		return responseBuilder.build();
	}

	@Override
	public JSONObject executeMethod(HttpRequest request, Context context) {

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