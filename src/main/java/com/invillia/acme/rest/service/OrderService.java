package com.invillia.acme.rest.service;

import java.util.Date;
import java.util.List;

import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.model.Order;

public interface OrderService {
	
	public List<Order> query(OrderFilter filter) throws DataAccessException;
	
	//public List<Order> query(String storeName, String orderId, Date initialDate, Date finalDate, String situation) throws DataAccessException;
	
	public Order getById(String orderId) throws DataAccessException;
	
	public Order createOrUpdate(Order order) throws DataAccessException;
	
}
