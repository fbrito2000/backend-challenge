package com.invillia.acme.rest.handler.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.enums.OrderServiceFactoryEnum;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.HTTPMethodNotImplemeted;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.handler.resource.RestResourceHandler;
import com.invillia.acme.rest.model.Order;
import com.invillia.acme.rest.model.Order;
import com.invillia.acme.rest.request.HttpRequest;
import com.invillia.acme.rest.response.JSONObjectResponseBuilder;
import com.invillia.acme.rest.service.OrderService;
import com.invillia.acme.rest.service.factory.OrderServiceFactory;
import com.invillia.acme.rest.to.OrderTO;

public class OrderRequestHandlerImpl extends RestRequestHandler implements RestResourceHandler {

	public final static String RESOURCE_NAME = "order";
	OrderService orderService = new OrderServiceFactory(OrderServiceFactoryEnum.LOCAL.name()).getImpl();
	OrderTO transferObject = new OrderTO();

	public OrderRequestHandlerImpl(RestRequestHandler parent) {
		super(parent);
	}

	private JSONObject handlePost(HttpRequest request, Context context) throws DataAccessException, ValidationException {

		JSONObjectResponseBuilder responseBuilder = null;

		if (request.getBody() == null) {
			ValidationException ve = new ValidationException();
			ve.addMessage("request body is missing. It should contain Json data for order creation.");
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, ve);
		} else {
			Order order = orderService.createOrUpdate(transferObject.toModel(request.getBody(), request.getPathParameters(), false));
			List<JSONObject> orderJSONObjectList = new ArrayList<JSONObject>();
			orderJSONObjectList.add(transferObject.toJsonObject(order));
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_CREATED, null, orderJSONObjectList, null);
		}
		return responseBuilder.build();
	}

	private JSONObject handleGet(HttpRequest request, Context context) throws ValidationException, DataAccessException {
		JSONObjectResponseBuilder responseBuilder = null;

		List<Order> orders;
		OrderFilter filter = transferObject.toFilter(request.getQueryStringParameters(), request.getPathParameters());

		// If storeName and date interval were provided, will call query by index
		// (faster)
		//if (!StringUtils.isNullOrEmpty(filter.getStoreName()) && filter.getInitialDate() != null && filter.getFinalDate() != null) {
		//	orders = orderService.query(filter.getStoreName(), filter.getId(), filter.getInitialDate(), filter.getFinalDate(), filter.getSituation());
		//} else {
			orders = orderService.query(filter);
		//}
		responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_OK, null, transferObject.toJsonObjectList(orders), null);

		return responseBuilder.build();
	}

	@Override
	public JSONObject executeMethod(HttpRequest request, Context context) throws DataAccessException, ValidationException {
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