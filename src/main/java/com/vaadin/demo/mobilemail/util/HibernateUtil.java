package com.vaadin.demo.mobilemail.util;

import java.util.Hashtable;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final String HIBERNATE_PASSWORD = "hibernate.connection.password";
	private static final String HIBERNATE_USERNAME = "hibernate.connection.username";
	private static final String HIBERNATE_URL = "hibernate.connection.url";
	private static final String HIBERNATE_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
	private static final SessionFactory sessionFactory;

	static {
		try {
			// Crée la SessionFactory
			sessionFactory = getConfiguration().buildSessionFactory();
			System.out.println("Session creation........OK!");
		} catch (HibernateException ex) {
			throw new RuntimeException("Hibernate configuration error: "
					+ ex.getMessage(), ex);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static final ThreadLocal session = new ThreadLocal();

	public static Session currentSession() throws HibernateException {
		Session s = (Session) session.get();
		// Ouvre une nouvelle Session, si ce Thread n'en a aucune
		if (s == null) {
			s = sessionFactory.openSession();
			session.set(s);
		}
		return s;
	}

	public static void closeSession() throws HibernateException {
		Session s = (Session) session.get();
		session.set(null);
		if (s != null)
			s.close();
	}

	private static Configuration getConfiguration()
			throws Exception {
		
		Configuration cfg = new Configuration();
		
		// establish connection to MySQL Service
		System.out.print("Retrieving MySQL information....");
		ServiceManager services = new ServiceManager();
		Hashtable<String, String> mysqlInformation = services
				.getMySQLInformation();
		System.out.println("....OK!");
		
		System.out.print("Configuration creation....");
		cfg.setProperty(HIBERNATE_DIALECT,
				"org.hibernate.dialect.MySQLDialect");
		cfg.setProperty(HIBERNATE_DRIVER_CLASS,
				"com.mysql.jdbc.Driver");
		cfg.setProperty(HIBERNATE_URL,
				mysqlInformation.get(ServiceManager.DB_URL));
		cfg.setProperty(HIBERNATE_USERNAME,
				mysqlInformation.get(ServiceManager.USER));
		cfg.setProperty(HIBERNATE_PASSWORD,
				mysqlInformation.get(ServiceManager.PASSWORD));
		cfg.setProperty("hibernate.hbm2ddl.auto","update");
		cfg.addResource("MailBox.hbm.xml");
		System.out.println("....OK!");
		System.out.println(toStringConfiguration(cfg));
		
		return cfg;
	}
	
	private static String toStringConfiguration(Configuration configuration){
		String string = "";
		
		string+="\nURL: "+configuration.getProperty(HIBERNATE_URL)+"\n";
		string+="Dialect: "+configuration.getProperty(HIBERNATE_DIALECT)+"\n";
		string+="Driver class: "+configuration.getProperty(HIBERNATE_DRIVER_CLASS)+"\n";
		string+="Username: "+configuration.getProperty(HIBERNATE_USERNAME)+"\n";
		string+="Password: "+configuration.getProperty(HIBERNATE_PASSWORD)+"\n";
		
		return string;
	}

}
