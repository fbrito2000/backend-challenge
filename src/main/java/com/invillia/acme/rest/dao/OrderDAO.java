package com.invillia.acme.rest.dao;

import java.util.List;

import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.model.Order;

public interface OrderDAO {
	
	public Order getById(String orderId) throws DataAccessException;

	public List<Order> query(OrderFilter order) throws DataAccessException;
	
	public Order createOrUpdate(Order order) throws DataAccessException;
	
	//public List<Order> query(String storeName, String id, Date initialDate, Date finalDate, String situation) throws DataAccessException;
	
	//public boolean exists(String storeName, String id);

}
