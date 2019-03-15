package com.invillia.acme.rest.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.model.Store;

public class StoreTO {

	public Store toPojo(JSONObject jsonObject) throws ValidationException {
		
		System.out.println("JSONOBJECT:\n\n" + jsonObject + "\n\n");
		//validate JSON request body Input
		List<String> messages = new ArrayList<String>();
		if (jsonObject.get("name") == null) {
			messages.add("store: Field 'name' is mandatory.");
		}
		if (jsonObject.get("address") == null) {
			messages.add("store: Field 'address' is mandatory.");
		}
		if (!messages.isEmpty()) {
			throw new ValidationException(messages);
		}
		
		Store store = new Store();
		store.setName((String)jsonObject.get("name"));
		store.setAddress((String)jsonObject.get("address"));

		return store;		


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
