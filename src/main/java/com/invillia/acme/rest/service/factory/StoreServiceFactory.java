package com.invillia.acme.rest.service.factory;

import com.invillia.acme.rest.enums.StoreServiceFactoryEnum;
import com.invillia.acme.rest.service.StoreService;
import com.invillia.acme.rest.service.impl.StoreServiceImpl;


public class StoreServiceFactory {

	private String implementationName;
	
	public StoreServiceFactory() {
		implementationName = StoreServiceFactoryEnum.LOCAL.name();
	}
	
	public StoreServiceFactory(String implementationName) {
		setImplementationName(StoreServiceFactoryEnum.LOCAL.name());
	}

	public String getImplementationName() {
		return implementationName;
	}

	public void setImplementationName(String implementationName) {
		if (StoreServiceFactoryEnum.valueOf(implementationName) != null){
			this.implementationName = implementationName;
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
	
	public StoreService getImpl() {
		switch(implementationName) {
			default: return new StoreServiceImpl();
		}
	}
}
