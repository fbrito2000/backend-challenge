package com.invillia.acme.rest.handler.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.invillia.acme.rest.enums.PaymentServiceFactoryEnum;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.HTTPMethodNotImplemeted;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.PaymentFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.handler.resource.RestResourceHandler;
import com.invillia.acme.rest.model.Payment;
import com.invillia.acme.rest.request.HttpRequest;
import com.invillia.acme.rest.response.JSONObjectResponseBuilder;
import com.invillia.acme.rest.service.PaymentService;
import com.invillia.acme.rest.service.factory.PaymentServiceFactory;
import com.invillia.acme.rest.to.PaymentTO;

public class PaymentRequestHandlerImpl extends RestRequestHandler implements RestResourceHandler {

	public final static String RESOURCE_NAME = "payment";
	PaymentService paymentService = new PaymentServiceFactory(PaymentServiceFactoryEnum.LOCAL.name()).getImpl();
	PaymentTO transferObject = new PaymentTO();

	public PaymentRequestHandlerImpl(RestRequestHandler parent) {
		super(parent);

	}

	private JSONObject handlePost(HttpRequest request, Context context) throws DataAccessException, ValidationException {

		JSONObjectResponseBuilder responseBuilder = null;
		JSONObject requestBody = request.getBody();

		if (requestBody == null) {
			ValidationException ve = new ValidationException();
			ve.addMessage("request body is missing. It should contain Json data for payment creation.");
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, ve);
		} else {
			Payment payment = paymentService.createOrUpdate(transferObject.toModel(requestBody, request.getPathParameters()));
			List<JSONObject> paymentJSONObjectList = new ArrayList<JSONObject>();
			paymentJSONObjectList.add(transferObject.toJsonObject(payment));
			responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_CREATED, null, paymentJSONObjectList, null);

		}
		return responseBuilder.build();
	}

	private JSONObject handleGet(HttpRequest request, Context context) throws ValidationException, DataAccessException {
		JSONObjectResponseBuilder responseBuilder = null;

		List<Payment> payments;
		PaymentFilter filter = transferObject.toFilter(request.getQueryStringParameters(), request.getPathParameters());
		payments = paymentService.query(filter);
		responseBuilder = new JSONObjectResponseBuilder(HttpStatus.SC_OK, null, transferObject.toJsonObjectList(payments), null);

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