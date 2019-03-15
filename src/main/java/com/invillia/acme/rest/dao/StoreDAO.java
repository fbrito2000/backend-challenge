package com.invillia.acme.rest.dao;

import java.util.List;

import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.model.Store;

public interface StoreDAO {
	public Store createOrUpdate (Store store) throws DataAccessException; 
	
	public List<Store> query(Store order) throws DataAccessException;

}
