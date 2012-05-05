package com.vaadin.demo.mobilemail.data.dao;


import java.util.List;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.dao.iface.IMailBoxDAO;


public class MailBoxDAO extends GenericDAO<MailBox, Long> implements IMailBoxDAO {

	public MailBoxDAO() {
		super();
	}

}
