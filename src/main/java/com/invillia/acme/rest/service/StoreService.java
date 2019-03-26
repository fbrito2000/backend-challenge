package com.invillia.acme.rest.service;

import java.util.List;


import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.StoreFilter;
import com.invillia.acme.rest.model.Store;

public interface StoreService {

	public Store createOrUpdate (Store store) throws DataAccessException; 
	public List<Store> query(StoreFilter filter) throws DataAccessException;
	
}
