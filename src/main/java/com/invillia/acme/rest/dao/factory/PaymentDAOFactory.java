package com.invillia.acme.rest.dao.factory;

import com.invillia.acme.rest.dao.PaymentDAO;
import com.invillia.acme.rest.dao.impl.PaymentDynamoDBDAOImpl;

public class PaymentDAOFactory {

	enum implementations {DYNAMODB};
	
	private String implementationName;
	
	public PaymentDAOFactory() {
		implementationName = implementations.DYNAMODB.name();
	}
	
	public PaymentDAOFactory(String implementationName) {
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
	
	public PaymentDAO getInstance() {
		if (implementationName.equals(implementations.DYNAMODB.name())){
			return new PaymentDynamoDBDAOImpl();
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
}
