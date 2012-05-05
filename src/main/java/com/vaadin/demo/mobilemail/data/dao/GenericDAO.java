package com.vaadin.demo.mobilemail.data.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.vaadin.demo.mobilemail.data.dao.iface.IGenericDAO;
import com.vaadin.demo.mobilemail.util.HibernateUtil;

/**
 * GenericDAO implementation class.
 * 
 * @author AP
 */

public class GenericDAO<T, ID extends Serializable> implements
		IGenericDAO<T, ID> {

	/**
	 * Class type
	 */
	private Class<T> persistentClass;

	/**
	 * Constructor
	 * 
	 * @param session
	 *            : the session used by DAO
	 */
	public GenericDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public ID add(T object) {
		final Transaction tx = getCurrentSession().beginTransaction();
		final ID index = (ID) getCurrentSession().save(object);
		tx.commit();

		return index;
	}

	public void saveOrUpdate(T object) {
		final Transaction tx = getCurrentSession().beginTransaction();
		getCurrentSession().saveOrUpdate(object);
		tx.commit();
	}

	public void delete(T object) {
		final Transaction tx = getCurrentSession().beginTransaction();
		getCurrentSession().delete(object);
		tx.commit();
	}

	protected List<T> findByCriteria(Criterion... criterion) {
		Criteria criteria = getCurrentSession().createCriteria(
				getPersistentClass());
		for (Criterion c : criterion) {
			criteria.add(c);
		}

		// TODO useful ?
		// Refresh
		for (Object object : criteria.list()) {
			getCurrentSession().refresh(object);
		}

		return criteria.list();
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public T findById(ID id) {
		List<T> results = findByCriteria(Restrictions.eq("id", id));

		return results.isEmpty() ? null : results.get(0);
	}

	/*
	 * MUTATORS
	 */

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public Session getCurrentSession() {
		return HibernateUtil.currentSession();
	}
}