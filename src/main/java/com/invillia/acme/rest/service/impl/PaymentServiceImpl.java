package com.invillia.acme.rest.service.impl;

import java.util.List;

import com.invillia.acme.rest.dao.OrderDAO;
import com.invillia.acme.rest.dao.PaymentDAO;
import com.invillia.acme.rest.dao.factory.OrderDAOFactory;
import com.invillia.acme.rest.dao.factory.PaymentDAOFactory;
import com.invillia.acme.rest.enums.OrderSituationEnum;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.filter.PaymentFilter;
import com.invillia.acme.rest.model.Order;
import com.invillia.acme.rest.model.Payment;
import com.invillia.acme.rest.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {

	PaymentDAOFactory paymentDAOFactory = new PaymentDAOFactory();
	OrderDAOFactory orderDAOFactory = new OrderDAOFactory();

	@Override
	public Payment createOrUpdate(Payment payment) throws DataAccessException {

		OrderDAO orderDAO = orderDAOFactory.getInstance();
		PaymentDAO paymentDAO = paymentDAOFactory.getInstance();
		OrderFilter orderFilter = new OrderFilter();
		orderFilter.setStoreName(payment.getStoreName());
		orderFilter.setId(payment.getOrderId());
		if (!orderDAO.query(orderFilter).isEmpty()) {
			Payment result = paymentDAO.createOrUpdate(payment);
			Order order = orderDAO.getById(result.getOrderId());
			order.setSituation(OrderSituationEnum.PAID.name());
			orderDAO.createOrUpdate(order);
			return result;
				
		} else {
			throw new DataAccessException("create payment: store name '" + payment.getStoreName() + "' with order id '" + payment.getOrderId() + "' do not exist in store database.");
		}
	}

	@Override
	public List<Payment> query(PaymentFilter filter) throws DataAccessException {
		return paymentDAOFactory.getInstance().query(filter);
	}
}
