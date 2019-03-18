package com.invillia.acme.rest.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.model.Store;

public class StoreTO {

	public Store toModel(JSONObject body) throws ValidationException {
		
		System.out.println("Store.toModel(JSONObject): body --> \n" + body);
		//validate JSON request body Input
		List<String> messages = new ArrayList<String>();
		if (body.get("name") == null) {
			messages.add("store: Field 'name' is mandatory.");
		}
		if (body.get("address") == null) {
			messages.add("store: Field 'address' is mandatory.");
		}
		if (!messages.isEmpty()) {
			throw new ValidationException(messages);
		}
		
		Store store = new Store();
		store.setName((String)body.get("name"));
		store.setAddress((String)body.get("address"));

		System.out.println("Store.toModel(JSONObject): store --> \n" + store);
		return store;		
	}

	public StoreFilter toFilter(Map queryStringParameters, Map pathParameters) {
		
		System.out.println("StoreTO.toFilter(Map) --> queryStringParameters:\n" + queryStringParameters);
		System.out.println("StoreTO.toFilter(Map) --> pathParameters:\n" + pathParameters);

		//create the filter object
		StoreFilter filter = new StoreFilter();
		filter.setName((String)queryStringParameters.get("name"));
		filter.setAddress((String)queryStringParameters.get("address"));

		return filter;		

	}
	
	public JSONObject toJsonObject(Store dataObject) {
		JSONObject storeJSONObject = new JSONObject();

		storeJSONObject.put("name", dataObject.getName());
		storeJSONObject.put("address", dataObject.getAddress());
		
		return storeJSONObject;
	}

	public List<JSONObject> toJsonObjectList(List<Store> dataObjectList) {

		List<JSONObject> storeJSONObjectList = new ArrayList<JSONObject>();
		if (!CollectionUtils.isNullOrEmpty(dataObjectList)) {
			for (Store store : dataObjectList) {
				storeJSONObjectList.add(toJsonObject(store));

			}
		}

		return storeJSONObjectList;
	}
}
