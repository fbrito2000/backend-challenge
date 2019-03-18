package com.invillia.acme.rest.to;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;
import com.invillia.acme.rest.enums.PaymentSituationEnum;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.PaymentFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.model.Payment;

public class PaymentTO {

	DateFormat dateFormat = RestRequestHandler.DATE_FORMATTER;

	public Payment toModel(JSONObject body, Map<String, String> pathParameters) throws ValidationException {

		System.out.println("Payment.toModel(JSONObject): body --> \n" + body);
		// validate JSON request body Input
		List<String> messages = new ArrayList<String>();
		if (pathParameters.get("id") == null) {
			messages.add("payment: could not decode path parameter '{id}'.");
		}
		if (body.get("storeName") == null) {
			messages.add("payment: Field 'storeName' is mandatory.");
		}
		if (body.get("creditCardNumber") == null) {
			messages.add("payment: Field 'creditCardNumber' is mandatory.");
		}
		if (body.get("date") == null) {
			messages.add("payment: Field 'date' is mandatory.");
		}
		if (!messages.isEmpty()) {
			throw new ValidationException(messages);
		}

		Payment payment = new Payment();
		payment.setOrderId(pathParameters.get("id"));
		payment.setStoreName((String) body.get("storeName"));
		try {
			payment.setDate(dateFormat.parse((String) body.get("date")).getTime());
		} catch (ParseException e) {
			throw new ValidationException(e.getMessage());
		}
		payment.setCreditCardNumber((Long) body.get("creditCardNumber"));
		payment.setSituation(PaymentSituationEnum.PAID.name());

		System.out.println("Payment.toModel(JSONObject): payment --> \n" + payment);
		return payment;
	}

	public PaymentFilter toFilter(Map queryStringParameters, Map pathParameters) throws ValidationException {

		System.out.println("PaymentTO.toFilter(Map) --> queryStringParameters:\n" + queryStringParameters);
		System.out.println("PaymentTO.toFilter(Map) --> pathParameters:\n" + pathParameters);
		// validate JSON request body Input
		// create the filter object
		PaymentFilter filter = new PaymentFilter();
		filter.setOrderId((String) pathParameters.get("id"));
		filter.setStoreName((String) queryStringParameters.get("storeName"));
		if (queryStringParameters.get("date") != null) {
			try {
				filter.setDate(dateFormat.parse((String) queryStringParameters.get("date")).getTime());
			} catch (ParseException e) {
				throw new ValidationException(e.getMessage());
			}
		}
		filter.setCreditCardNumber((Long) queryStringParameters.get("creditCardNumber"));
		filter.setSituation((String) queryStringParameters.get("situation"));

		System.out.println("PaymentTO.toFilter(Map):  --> filter:\n" + filter);
		return filter;

	}

	public JSONObject toJsonObject(Payment dataObject) {
		JSONObject paymentJSONObject = new JSONObject();

		paymentJSONObject.put("orderId", dataObject.getOrderId());
		paymentJSONObject.put("storeName", dataObject.getStoreName());
		paymentJSONObject.put("date", dateFormat.format(dataObject.getDate()));
		paymentJSONObject.put("creditCardNumber", dataObject.getCreditCardNumber());
		paymentJSONObject.put("situation", dataObject.getSituation());

		return paymentJSONObject;
	}

	public List<JSONObject> toJsonObjectList(List<Payment> dataObjectList) {

		List<JSONObject> paymentJSONObjectList = new ArrayList<JSONObject>();
		if (!CollectionUtils.isNullOrEmpty(dataObjectList)) {
			for (Payment payment : dataObjectList) {
				paymentJSONObjectList.add(toJsonObject(payment));

			}
		}

		return paymentJSONObjectList;
	}
}
