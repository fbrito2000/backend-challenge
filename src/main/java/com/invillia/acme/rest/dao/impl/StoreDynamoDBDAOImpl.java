package com.invillia.acme.rest.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.dao.StoreDAO;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.model.Store;

public class StoreDynamoDBDAOImpl implements StoreDAO {

	private LambdaLogger logger = RestRequestHandler.logger;
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	DynamoDBMapper mapper = new DynamoDBMapper(client);

	@Override
	public List<Store> query(StoreFilter storeFilter) throws DataAccessException {

		logger.log("StoreDynamoDBDAOImpl.query(StoreFilter) : storeFilter --> \n" + storeFilter);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		Map<String, String> ean = new HashMap<String, String>();
		String filterExpression = "";

		if (!StringUtils.isNullOrEmpty(storeFilter.getName())) {
			filterExpression = "#1 = :name ";
			eav.put(":name", new AttributeValue().withS(storeFilter.getName()));
			ean.put("#1", "Name");
		}
		if (!StringUtils.isNullOrEmpty(storeFilter.getAddress())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression += "#2 = :address ";
			eav.put(":address", new AttributeValue().withS(storeFilter.getAddress()));
			ean.put("#2", "Address");
		}

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		if (!StringUtils.isNullOrEmpty(filterExpression)) {
			scanExpression.setFilterExpression(filterExpression);
			scanExpression.setExpressionAttributeNames(ean);
			scanExpression.setExpressionAttributeValues(eav);
		}
		
		return mapper.scan(Store.class, scanExpression);
	}

	@Override
	public Store createOrUpdate(Store store) throws DataAccessException {
		
		logger.log("StoreDynamoDBDAOImpl.create(Store) : store --> \n" + store );
		
		try {
			mapper.save(store);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(Store.class.getName(), e);
		}
		return store;
	}
}
