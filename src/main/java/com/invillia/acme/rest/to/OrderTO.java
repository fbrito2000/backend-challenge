package com.invillia.acme.rest.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.model.Order;

public class OrderTO {
	
	public Order toPojo(JSONObject jsonObject, boolean isUpdate) throws ValidationException {
		
		System.out.println("JSONOBJECT:\n\n" + jsonObject + "\n\n");
		//validate JSON request body Input
		List<String> messages = new ArrayList<String>();
		if (jsonObject.get("id") == null && isUpdate) {
			messages.add("order: Field 'id' is mandatory.");
		}
		if (jsonObject.get("date") == null) {
			messages.add("order: Field 'date' is mandatory.");
		}
		if (jsonObject.get("situation") == null) {
			messages.add("order: Field 'situation' is mandatory.");
		}
		if (jsonObject.get("address") == null) {
			messages.add("order: Field 'address' is mandatory.");
		}
		if (!messages.isEmpty()) {
			throw new ValidationException(messages);
		}
		
		Order orderPojo = new Order();
		if (isUpdate) {
			orderPojo.setId((String)jsonObject.get("id"));
		} else {
			orderPojo.setId(String.valueOf(Math.random()));
		}
		orderPojo.setAddress((String)jsonObject.get("address"));
		orderPojo.setDate((String)jsonObject.get("date"));
		orderPojo.setSituation((String)jsonObject.get("situation"));
		return orderPojo;		

		//TEST
		/*Order orderPojo = new Order();
		orderPojo.setAddress("RUA TAL 515");
		orderPojo.setId("id-" + String.valueOf(Math.random()));
		orderPojo.setDate(String.valueOf(new Date().getTime()));
		orderPojo.setSituation("situation");
		return orderPojo;*/

	}
	
	public JSONObject toJsonObject(Order dataObject) {
		JSONObject orderJSONObject = new JSONObject();
		orderJSONObject.put("address", dataObject.getAddress());
		orderJSONObject.put("id", dataObject.getId());
		orderJSONObject.put("date", dataObject.getDate());
		orderJSONObject.put("situation", dataObject.getSituation());
		
		return orderJSONObject;
	}
	
	public List<JSONObject> toJsonObjectList(List<Order> dataObjectList) {
		
		List<JSONObject> orderJSONObjectList = new ArrayList<JSONObject>();
		if (!CollectionUtils.isNullOrEmpty(dataObjectList)) {
			for(Order order : dataObjectList) {
				orderJSONObjectList.add(toJsonObject(order));
				
			}
		}
		
		return orderJSONObjectList;
	}
}
