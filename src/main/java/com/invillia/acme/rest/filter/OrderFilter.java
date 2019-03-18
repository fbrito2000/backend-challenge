package com.invillia.acme.rest.filter;

import java.util.Date;

import com.invillia.acme.rest.model.Order;


public class OrderFilter extends Order {

	private Date initialDate;
	private Date finalDate;
	
	public OrderFilter() {
		
	}

	public Date getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}
	public Date getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}
	
	
}
