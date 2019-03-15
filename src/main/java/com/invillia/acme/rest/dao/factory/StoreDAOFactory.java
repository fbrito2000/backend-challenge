package com.invillia.acme.rest.dao.factory;

import com.invillia.acme.rest.dao.StoreDAO;
import com.invillia.acme.rest.dao.impl.StoreDynamoDBDAOImpl;

public class StoreDAOFactory {

	enum implementations {
		DYNAMODB
	};

	private String implementationName;

	public StoreDAOFactory() {
		implementationName = implementations.DYNAMODB.name();
	}

	public StoreDAOFactory(String implementationName) {
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
		if (implementations.valueOf(implementationName) != null) {
			this.implementationName = implementationName;
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}

	public StoreDAO getInstance() {
		if (implementationName.equals(implementations.DYNAMODB.name())) {
			return new StoreDynamoDBDAOImpl();
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
}
