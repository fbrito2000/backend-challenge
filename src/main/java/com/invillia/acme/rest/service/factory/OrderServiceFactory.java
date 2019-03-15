package com.invillia.acme.rest.service.factory;

import com.invillia.acme.rest.enums.OrderServiceFactoryEnum;
import com.invillia.acme.rest.service.OrderService;
import com.invillia.acme.rest.service.impl.OrderServiceImpl;


public class OrderServiceFactory {

	private String implementationName;
	
	public OrderServiceFactory() {
		implementationName = OrderServiceFactoryEnum.LOCAL.name();
	}
	
	public OrderServiceFactory(String implementationName) {
		setImplementationName(OrderServiceFactoryEnum.LOCAL.name());
	}

	public String getImplementationName() {
		return implementationName;
	}

	public void setImplementationName(String implementationName) {
		if (OrderServiceFactoryEnum.valueOf(implementationName) != null){
			this.implementationName = implementationName;
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
	
	public OrderService getImpl() {
		switch(implementationName) {
			default: return new OrderServiceImpl();
		}
	}
}
