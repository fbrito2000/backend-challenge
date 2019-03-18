package com.invillia.acme.rest.service.factory;

import com.invillia.acme.rest.enums.PaymentServiceFactoryEnum;
import com.invillia.acme.rest.service.PaymentService;
import com.invillia.acme.rest.service.impl.PaymentServiceImpl;


public class PaymentServiceFactory {

	private String implementationName;
	
	public PaymentServiceFactory() {
		implementationName = PaymentServiceFactoryEnum.LOCAL.name();
	}
	
	public PaymentServiceFactory(String implementationName) {
		setImplementationName(PaymentServiceFactoryEnum.LOCAL.name());
	}

	public String getImplementationName() {
		return implementationName;
	}

	public void setImplementationName(String implementationName) {
		if (PaymentServiceFactoryEnum.valueOf(implementationName) != null){
			this.implementationName = implementationName;
		} else {
			throw new RuntimeException("FATAL: IMPLEMENTATION NAME NOT MAPPED: " + implementationName);
		}
	}
	
	public PaymentService getImpl() {
		switch(implementationName) {
			default: return new PaymentServiceImpl();
		}
	}
}
