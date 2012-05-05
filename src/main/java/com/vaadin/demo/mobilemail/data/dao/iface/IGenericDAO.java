package com.vaadin.demo.mobilemail.data.dao.iface;

import java.io.Serializable;
import java.util.List;

/**
 * IGenericDAO interface.
 * 
 * @author AP
 * 
 * @param <T>
 *            : type of db object
 * @param <ID>
 *            : ID type
 */

public interface IGenericDAO<T, ID extends Serializable> {

	/**
	 * Add a new item in db.
	 * 
	 * @param object
	 * @return the index
	 * 
	 * @pre : the item doesn't already exist in db.
	 */
	ID add(T object);

	/**
	 * Delete an item in db.
	 * 
	 * @param object
	 * 
	 * @pre : the item already exists in db.
	 */
	void delete(T object);

	/**
	 * Get all items in db.
	 * 
	 * @return
	 */
	List<T> findAll();

	/**
	 * Get items by their ID.
	 * 
	 * @param id
	 * @return the item or null if it doesn't exist
	 */
	T findById(ID id);

}