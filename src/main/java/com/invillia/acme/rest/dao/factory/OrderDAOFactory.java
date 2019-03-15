package com.invillia.acme.rest.dao.factory;

import com.invillia.acme.rest.dao.OrderDAO;
import com.invillia.acme.rest.dao.impl.OrderDynamoDBDAOImpl;

public class OrderDAOFactory {

	enum implementations {DYNAMODB};
	
	private String implementationName;
	
	public OrderDAOFactory() {
		implementationName = implementations.DYNAMODB.name();
	}
	
	public OrderDAOFactory(String implementationName) {
		if (implementationName == null) {
			setImplementationName(implementations.DYNAMODB.name());
		} else {
			setImplementationName(implementationName);
		}
	}

	public String getImplementationName() {
		return implementationName;
	}

	public void setImplementationName(String implementationName) {
		if (implementations.valueOf(implementationName) != null){
			this.implementationName = implementationName;
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
	
	public OrderDAO getInstance() {
		if (implementationName.equals(implementations.DYNAMODB.name())){
			return new OrderDynamoDBDAOImpl();
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
}
