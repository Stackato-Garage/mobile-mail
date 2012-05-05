package com.vaadin.demo.mobilemail.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

public class ServiceManager implements StackatoServices {

	private static final String NULL_STRING = "";
	public static final String DB_URL = "dbUrl";
	public static final String USER = "user";
	public static final String PASSWORD = "password";

	private Hashtable<String, String> mysqlInformation;

	public ServiceManager() {
		mysqlInformation = new Hashtable<String, String>();
	}

	/*
	 * This method is responsible for establishing a valid connection to the
	 * MySQL service, using the credentials available in the environment
	 * variable, namely "VCAP_SERVICES".
	 * 
	 * The content of VCAP_SERVICES environment variable is a JSON string, thus
	 * this method uses standard interfaces from the Argo JSON parsing API to
	 * extract the credentials.
	 */

	public Hashtable<String, String> getMySQLInformation(){

		String vcap_services = System.getenv("VCAP_SERVICES");

		String hostname = NULL_STRING;
		String dbname = NULL_STRING;
		String user = NULL_STRING;
		String password = NULL_STRING;
		String port = NULL_STRING;

		if (vcap_services != null && vcap_services.length() > 0) {
			try {
				JsonRootNode root = new JdomParser().parse(vcap_services);

				JsonNode mysqlNode = root.getNode("mysql-5.1");
				JsonNode credentials = mysqlNode.getNode(0).getNode(
						"credentials");

				dbname = credentials.getStringValue("name");
				hostname = credentials.getStringValue("hostname");
				port = credentials.getNumberValue("port");

				String dbUrl = "jdbc:mysql://" + hostname + ":" + port + "/"
						+ dbname;

				mysqlInformation.put(DB_URL, dbUrl);
				mysqlInformation.put(USER, credentials.getStringValue("user"));
				mysqlInformation.put(PASSWORD,
						credentials.getStringValue("password"));

				return mysqlInformation;

			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}