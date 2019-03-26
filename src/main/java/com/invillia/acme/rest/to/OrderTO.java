package com.invillia.acme.rest.to;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;
import com.invillia.acme.rest.enums.OrderSituationEnum;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.model.Order;

public class OrderTO {
	
	DateFormat dateFormat = RestRequestHandler.DATE_FORMATTER;
	
	public Order toModel(JSONObject body, Map<String,String> pathParameters, boolean isUpdate) throws ValidationException {
		
		//validate JSON request body Input
		List<String> messages = new ArrayList<String>();
		if (pathParameters.get("id") == null && isUpdate) {
			messages.add("order: Field 'id' is mandatory.");
		}
		if (body.get("storeName") == null) {
			messages.add("order: Field 'storeName' is mandatory.");
		}
		if (body.get("date") == null) {
			messages.add("order: Field 'date' is mandatory.");
		}
		
		if (body.get("address") == null) {
			messages.add("order: Field 'address' is mandatory.");
		}
		if (!messages.isEmpty()) {
			throw new ValidationException(messages);
		}
		
		//Once validated, create the model object
		Order order = new Order();
		if (isUpdate) {
			order.setId((String)body.get("id"));
		}
		
		order.setStoreName((String)body.get("storeName"));
		order.setAddress((String)body.get("address"));
		
		try {
			order.setDate(dateFormat.parse((String) body.get("date")).getTime());
		} catch (ParseException e) {
			throw new ValidationException(e.getMessage());
		}
		order.setSituation(OrderSituationEnum.NOT_PAID.name());
		
		return order;		

	}

	public OrderFilter toFilter(Map queryStringParameters, Map pathParameters) throws ValidationException {

		//create the filter object
		OrderFilter filter = new OrderFilter();
		filter.setId((String)pathParameters.get("id"));
		
		filter.setStoreName((String)queryStringParameters.get("storeName"));
		filter.setAddress((String)queryStringParameters.get("address"));
		filter.setSituation((String)queryStringParameters.get("situation"));

		if ((String)queryStringParameters.get("initialDate") != null) {
			try {
				filter.setInitialDate((java.util.Date)dateFormat.parse((String)queryStringParameters.get("initialDate")));
			} catch (ParseException e) {
				throw new ValidationException(e.getMessage());
			}
		}
		if ((String)queryStringParameters.get("finalDate") != null) {
			try {
				filter.setFinalDate((java.util.Date)dateFormat.parse((String)queryStringParameters.get("finalDate")));
			} catch (ParseException e) {
				throw new ValidationException(e.getMessage());
			}
		}
		return filter;		

	}

	public JSONObject toJsonObject(Order order) {
		
		//create a JSON object from Model
		JSONObject orderJSONObject = new JSONObject();

		orderJSONObject.put("address", order.getAddress());
		orderJSONObject.put("id", order.getId());
		orderJSONObject.put("date", dateFormat.format(order.getDate()));
		orderJSONObject.put("situation", order.getSituation());
		orderJSONObject.put("storeName", order.getStoreName());
		
		return orderJSONObject;
	}
	
	public List<JSONObject> toJsonObjectList(List<Order> orderList) {
		
		List<JSONObject> orderJSONObjectList = new ArrayList<JSONObject>();
		if (!CollectionUtils.isNullOrEmpty(orderList)) {
			for(Order order : orderList) {
				orderJSONObjectList.add(toJsonObject(order));
				
			}
		}
		
		return orderJSONObjectList;
	}
}
