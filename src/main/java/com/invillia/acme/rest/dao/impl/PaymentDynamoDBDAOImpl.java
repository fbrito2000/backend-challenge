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
import com.invillia.acme.rest.dao.PaymentDAO;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.PaymentFilter;
import com.invillia.acme.rest.handler.RestRequestHandler;
import com.invillia.acme.rest.model.Payment;

public class PaymentDynamoDBDAOImpl implements PaymentDAO {
	
	private LambdaLogger logger = RestRequestHandler.logger;
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	DynamoDBMapper mapper = new DynamoDBMapper(client);
	
	@Override
	public List<Payment> query(PaymentFilter paymentFilter) throws DataAccessException {

		logger.log("PaymentDynamoDBDAOImpl.query(Payment) : paymentFilter --> " + paymentFilter);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		Map<String, String> ean = new HashMap<String, String>();
		String filterExpression = "";

		if (!StringUtils.isNullOrEmpty(paymentFilter.getOrderId())) {
			filterExpression = "#1 = :orderId ";
			eav.put(":orderId", new AttributeValue().withS(paymentFilter.getOrderId()));
			ean.put("#1", "OrderId");
		}
	
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		if (filterExpression != null) { 
			scanExpression.setFilterExpression(filterExpression);
			scanExpression.setExpressionAttributeNames(ean);
			scanExpression.setExpressionAttributeValues(eav);
		}
		return mapper.scan(Payment.class, scanExpression);
	}
	
	@Override
	public Payment createOrUpdate(Payment payment) throws DataAccessException {
		logger.log("PaymentDynamoDBDAOImpl.create(Payment) : payment ->> " + payment );

		try {
			mapper.save(payment);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(Payment.class.getName(), e);
		}
		return payment;
	}
}
