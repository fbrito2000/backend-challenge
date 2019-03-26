package com.invillia.acme.rest.service;

import java.util.List;


import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.PaymentFilter;
import com.invillia.acme.rest.model.Payment;

public interface PaymentService {

	public Payment createOrUpdate (Payment payment) throws DataAccessException; 
	public List<Payment> query(PaymentFilter filter) throws DataAccessException;
	
}
