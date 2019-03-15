package com.invillia.acme.rest.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.dao.OrderDAO;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.model.Order;

public class OrderDynamoDBDAOImpl implements OrderDAO {

	AmazonDynamoDB client;

	DynamoDBMapper mapper;

	public Order getById(String orderId)throws DataAccessException {

		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);

		Order query = new Order();
		query.setId(orderId);
		Order result = null;
		try {
			result = mapper.load(query);
			if (result != null) {
				System.out.println("[ORDER - getById] Order Information " + result);
			} else {
				System.out.println("No order found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(Order.class.getName(), e);
		}
		return result;
	}

	public Order createOrUpdate(Order order) throws DataAccessException{
		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);
		Order result = new Order();
		try {
			mapper.save(order);
			result = this.getById(order.getId());
		} catch (Exception e){
			e.printStackTrace();
			throw new DataAccessException(Order.class.getName(), e);
		}
		return result;
	}

	public List<Order> query(Order order) throws DataAccessException {
		
		System.out.println("OrderDynamoDBDAOImpl.query(Order) : order --> " + order);
		
		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		Map<String, String> ean = new HashMap<String, String>();
		String filterExpression = "";

		if (!StringUtils.isNullOrEmpty(order.getId())) {
			filterExpression = "#1 = :id ";
			eav.put(":id", new AttributeValue().withS(order.getId()));
			ean.put("#1", "Id");
		}
		if (!StringUtils.isNullOrEmpty(order.getAddress())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression = "#2 = :address ";
			eav.put(":address", new AttributeValue().withS(order.getAddress()));
			ean.put("#2", "Address");
		}
		if (!StringUtils.isNullOrEmpty(order.getId())) {
			filterExpression = "#1 = :id ";
			eav.put(":id", new AttributeValue().withS(order.getId()));
			ean.put("#1", "Id");
		}
		if (!StringUtils.isNullOrEmpty(order.getAddress())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression = "#3 = :situation ";
			eav.put(":situation", new AttributeValue().withS(order.getAddress()));
			ean.put("#2", "Situation");
		}

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		if (!StringUtils.isNullOrEmpty(filterExpression)) {
			scanExpression.setFilterExpression(filterExpression);
			scanExpression.setExpressionAttributeNames(ean);
			scanExpression.setExpressionAttributeValues(eav);
		}
		
		System.out.println("OrderDynamoDBDAOImpl.query(Order) : scanExpression --> " + scanExpression);
		return mapper.scan(Order.class, scanExpression);
	}
}
