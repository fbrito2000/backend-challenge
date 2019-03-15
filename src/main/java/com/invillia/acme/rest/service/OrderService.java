package com.invillia.acme.rest.service;

import java.util.List;

import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.model.Order;

public interface OrderService {
	
	public List<Order> query(Order order) throws DataAccessException;
	
	public Order getById(String orderId) throws DataAccessException;
	
	public Order createOrUpdate(Order order) throws DataAccessException;
	
}
