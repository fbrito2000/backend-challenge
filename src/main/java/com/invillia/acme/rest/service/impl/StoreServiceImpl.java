package com.invillia.acme.rest.service.impl;

import java.util.List;

import com.invillia.acme.rest.dao.factory.StoreDAOFactory;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.model.Store;
import com.invillia.acme.rest.service.StoreService;

public class StoreServiceImpl implements StoreService {

	StoreDAOFactory storeDAOFactory = new StoreDAOFactory();
	
	@Override
	public Store createOrUpdate(Store store) throws DataAccessException {
		return storeDAOFactory.getInstance().createOrUpdate(store);
	}

	@Override
	public List<Store> query(StoreFilter filter) throws DataAccessException {
		return storeDAOFactory.getInstance().query(filter);
	}
}
