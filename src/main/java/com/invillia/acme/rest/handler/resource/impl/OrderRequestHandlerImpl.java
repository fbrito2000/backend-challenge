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
		// TODO Auto-generated constructor stub
	}

	private JSONObject handlePost(HttpRequest request, Context context) {

		JSONObjectResponseBuilder responseBuilder = null;

		String requestBody = (String) request.getBody();
		JSONObject orderJSONObject;

		try {
			if (!StringUtils.isNullOrEmpty(requestBody)) {
				orderJSONObject = (JSONObject) PARSER.parse((String) request.getBody());
			} else {
				orderJSONObject = null;
			}

			if (orderJSONObject == null) {
				ValidationException ve = new ValidationException();
				ve.addMessage("body is missing");
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, ve);
			} else {
				Order order = orderService.createOrUpdate(transferObject.toPojo(orderJSONObject, false)); 

				List<JSONObject> orderJSONObjectList = new ArrayList<JSONObject>();
				orderJSONObjectList.add(transferObject.toJsonObject(order));
				responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_CREATED, null, orderJSONObjectList, null);

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

		Order orderQuery = new Order();
		if (!StringUtils.isNullOrEmpty(request.getQueryStringParameters().get("address"))) {
			orderQuery.setAddress((String) request.getQueryStringParameters().get("address"));
		}
		if (!StringUtils.isNullOrEmpty(request.getQueryStringParameters().get("id"))) {
			orderQuery.setId((String) request.getQueryStringParameters().get("id"));
		}
		try {
			List<Order> orders = this.orderService.query(orderQuery);
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_OK, null, transferObject.toJsonObjectList(orders), null);
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