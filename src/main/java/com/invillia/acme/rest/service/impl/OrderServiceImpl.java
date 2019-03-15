package com.invillia.acme.rest.service.impl;

import java.util.List;

import com.invillia.acme.rest.dao.factory.OrderDAOFactory;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.model.Order;
import com.invillia.acme.rest.service.OrderService;

public class OrderServiceImpl implements OrderService {

	OrderDAOFactory orderDAOFactory = new OrderDAOFactory();
	
	@Override
	public List<Order> query(Order order) throws DataAccessException {
		return orderDAOFactory.getInstance().query(order);
	}

	@Override
	public Order createOrUpdate(Order order) throws DataAccessException {
		return orderDAOFactory.getInstance().createOrUpdate(order);
	}

	@Override
	public Order getById(String orderId) throws DataAccessException {
		return orderDAOFactory.getInstance().getById(orderId);
	}
}
