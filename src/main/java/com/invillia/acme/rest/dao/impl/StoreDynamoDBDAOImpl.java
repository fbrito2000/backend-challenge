package com.invillia.acme.rest.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.dao.StoreDAO;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.model.Store;

public class StoreDynamoDBDAOImpl implements StoreDAO {

	AmazonDynamoDB client;

	DynamoDBMapper mapper;

	@Override
	public List<Store> query(Store store) throws DataAccessException {

		System.out.println("StoreDynamoDBDAOImpl.query(Store) : store --> " + store);
		
		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);

		// Map<String, Object> expressionAttributeValues = new HashMap<String,
		// Object>();

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		Map<String, String> ean = new HashMap<String, String>();
		String filterExpression = "";
		// store.setName("JOE");
		if (!StringUtils.isNullOrEmpty(store.getName())) {
			filterExpression = "#1 = :name ";
			eav.put(":name", new AttributeValue().withS(store.getName()));
			ean.put("#1", "Name");
		}
		if (!StringUtils.isNullOrEmpty(store.getAddress())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression = "#2 = :address ";
			eav.put(":address", new AttributeValue().withS(store.getAddress()));
			ean.put("#2", "Address");
		}

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		if (!StringUtils.isNullOrEmpty(filterExpression)) {
			scanExpression.setFilterExpression(filterExpression);
			scanExpression.setExpressionAttributeNames(ean);
			scanExpression.setExpressionAttributeValues(eav);
		}
		
		System.out.println("StoreDynamoDBDAOImpl.query(Store) : scanExpression --> " + scanExpression);
		return mapper.scan(Store.class, scanExpression);

	}

	@Override
	public Store createOrUpdate(Store store) throws DataAccessException {
		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);
		Store result = new Store();
		try {
			mapper.save(store);
			result = this.query(store).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(Store.class.getName(), e);
		}
		return result;
	}

}
