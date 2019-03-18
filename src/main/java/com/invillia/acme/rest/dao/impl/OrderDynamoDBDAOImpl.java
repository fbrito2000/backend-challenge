package com.invillia.acme.rest.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.dao.OrderDAO;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.model.Order;

public class OrderDynamoDBDAOImpl implements OrderDAO {

	//private static final String SGI_NAME_STORE_NAME_DATE = "StoreName-Date-index";
	//private static final String SGI_NAME_STORE_NAME_ORDER_ID = "StoreName-Id-index";
	//private static final String TABLE_NAME = Order.class.getName();

	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	DynamoDBMapper mapper = new DynamoDBMapper(client);

	public Order getById(String orderId) throws DataAccessException {

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

	public Order createOrUpdate(Order order) throws DataAccessException {

		Order result = new Order();
		try {
			mapper.save(order);
			result = this.getById(order.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(Order.class.getName(), e);
		}
		return result;
	}

	public List<Order> query(OrderFilter filter) throws DataAccessException {

		System.out.println("OrderDynamoDBDAOImpl.query(Order) : order --> " + filter);

		client = AmazonDynamoDBClientBuilder.standard().build();
		mapper = new DynamoDBMapper(client);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		Map<String, String> ean = new HashMap<String, String>();
		String filterExpression = "";

		if (!StringUtils.isNullOrEmpty(filter.getId())) {
			filterExpression = "#1 = :id ";
			eav.put(":id", new AttributeValue().withS(filter.getId()));
			ean.put("#1", "Id");
		}
		if (!StringUtils.isNullOrEmpty(filter.getAddress())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression += "#2 = :address ";
			eav.put(":address", new AttributeValue().withS(filter.getAddress()));
			ean.put("#2", "Address");
		}
		if (!StringUtils.isNullOrEmpty(filter.getSituation())) {
			if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
			}
			filterExpression += "#3 = :situation ";
			eav.put(":situation", new AttributeValue().withS(filter.getSituation()));
			ean.put("#3", "Situation");
		}
		if (filter.getInitialDate() == null) {
			filter.setInitialDate(new Date(0));
		}
		if (filter.getFinalDate() == null) {
			filter.setFinalDate(new Date());
		}

		if (!StringUtils.isNullOrEmpty(filterExpression)) {
				filterExpression += " and ";
		}
		filterExpression += "#4 between :initialDate and :finalDate";
		eav.put(":initialDate", new AttributeValue().withN(String.valueOf(filter.getInitialDate().getTime())));
		eav.put(":finalDate", new AttributeValue().withN(String.valueOf(filter.getFinalDate().getTime())));
		ean.put("#4", "Date");
	
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		if (!StringUtils.isNullOrEmpty(filterExpression)) {
			scanExpression.setFilterExpression(filterExpression);
			scanExpression.setExpressionAttributeNames(ean);
			scanExpression.setExpressionAttributeValues(eav);
		}

		System.out.println("OrderDynamoDBDAOImpl.query(Order) : scanExpression --> " + scanExpression);
		return mapper.scan(Order.class, scanExpression);
	}

	/*public List<Order> query(String storeName, String id, Date initialDate, Date finalDate, String situation) throws DataAccessException {

		System.out.println("OrderDynamoDBDAOImpl.query(String, String, Date, Date, String)\n");
		System.out.println("OrderDynamoDBDAOImpl.query: storeName --> " + storeName);
		System.out.println("OrderDynamoDBDAOImpl.query: id --> " + id);
		System.out.println("OrderDynamoDBDAOImpl.query: initialDate -->" + initialDate);
		System.out.println("OrderDynamoDBDAOImpl.query: finalDate -->" + finalDate);
		System.out.println("OrderDynamoDBDAOImpl.query: situation -->" + situation);
		
		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable(TABLE_NAME);
		Index index = table.getIndex(SGI_NAME_STORE_NAME_DATE);

		if (storeName == null || initialDate == null ||	finalDate == null) {
			throw new DataAccessException("Missing required index query parameters.");
		}

		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("#1 = :storeName and #2 between :initialDate and :finalDate")
				.withNameMap(new NameMap()
						.with("#1", "StoreName").with("#2", "Date"))
				.withValueMap(new ValueMap()
						.withString(":storeName", storeName)
						.withNumber(":initialDate", initialDate.getTime())
						.withNumber(":finalDate", finalDate.getTime()));

		List<Order> result = new ArrayList<Order>();
		ItemCollection<QueryOutcome> items = index.query(spec);
		Iterator<Item> iter = items.iterator();
		while (iter.hasNext()) {
			Order order = new Order();
			Item item = (Item) iter.next();
			order.setAddress(item.getString("Address"));
			order.setDate(item.getLong("Date"));
			order.setId(item.getString("Id"));
			order.setSituation(item.getString("Situation"));
			order.setStoreName(item.getJSON("StoreName"));
			System.out.println("OrderDynamoDBDAOImpl.query: [result from query] order -->" + order);
			if (!StringUtils.isNullOrEmpty(situation)) {
				if (situation.equals(order.getSituation())) {
					result.add(order);		
				}
			}
		}
		return result;
	}
	
	public boolean exists(String storeName, String id) {
		System.out.println("OrderDynamoDBDAOImpl.query(String, String)\n");
		System.out.println("OrderDynamoDBDAOImpl.query: storeName --> " + storeName);
		System.out.println("OrderDynamoDBDAOImpl.query: id --> " + id);
	
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(TABLE_NAME);
		Index index = table.getIndex(SGI_NAME_STORE_NAME_ORDER_ID);
		
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("#1 = :storeName and #2 = :id")
				.withNameMap(new NameMap()
						.with("#1", "StoreName")
						.with("#2", "Id"))
				.withValueMap(new ValueMap()
						.withString(":storeName", storeName)
						.withString(":id", id));
		
		//List<Item> result = new ArrayList<Item>();
		ItemCollection<QueryOutcome> items = index.query(spec);
		System.out.println("OrderDynamoDBDAOImpl.items.: [result from query] size -->" + items.getAccumulatedItemCount());
		if (items.getAccumulatedItemCount() > 0) {
			return true;
		} else return false;
		
	}*/
}
