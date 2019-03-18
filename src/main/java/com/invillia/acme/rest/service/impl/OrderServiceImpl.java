package com.invillia.acme.rest.service.impl;

import java.util.Date;
import java.util.List;

import com.invillia.acme.rest.dao.factory.OrderDAOFactory;
import com.invillia.acme.rest.dao.factory.StoreDAOFactory;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.model.Order;
import com.invillia.acme.rest.service.OrderService;

public class OrderServiceImpl implements OrderService {

	OrderDAOFactory orderDAOFactory = new OrderDAOFactory();
	StoreDAOFactory storeDAOFactory = new StoreDAOFactory();
	
	@Override
	public List<Order> query(OrderFilter orderFilter) throws DataAccessException {
		return orderDAOFactory.getInstance().query(orderFilter);
	}

	@Override
	public Order createOrUpdate(Order order) throws DataAccessException {
		
		StoreFilter filter = new StoreFilter();
		filter.setName(order.getStoreName());
		if (storeDAOFactory.getInstance().query(filter).size() == 0) {
			throw new DataAccessException("create order: store name '" + order.getStoreName() + "' does not exist in store database.");
		}
		return orderDAOFactory.getInstance().createOrUpdate(order);
	}

	@Override
	public Order getById(String orderId) throws DataAccessException {
		return orderDAOFactory.getInstance().getById(orderId);
	}

	//@Override
	//public List<Order> query(String storeName, String id, Date initialDate, Date finalDate, String situation) throws DataAccessException {
	//	return orderDAOFactory.getInstance().query(storeName, id, initialDate, finalDate, situation);
	//}

	
}
