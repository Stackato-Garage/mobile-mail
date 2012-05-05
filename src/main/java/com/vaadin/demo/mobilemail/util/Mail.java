package com.vaadin.demo.mobilemail.util;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

	private String smtp_server;
	private String imap_server;
	private String user;
	private String password;
	private String email;

	public String getSmtp() {
		return smtp_server;
	}

	public void setSmtp(String smtp_server) {
		this.smtp_server = smtp_server;
	}

	public String getImap() {
		return imap_server;
	}

	public void setImap(String host) {
		imap_server = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Folder[] readImap() {

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect(imap_server, user, password);

			Folder[] folders = store.getDefaultFolder().list();

			return folders;

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(2);
		}

		return null;

	}

	public Message[] readPop() {

		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		// Create empty properties
		Properties props = new Properties();
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.pop3.socketFactory.fallback", "false");
		props.setProperty("mail.pop3.port", "995");
		props.setProperty("mail.pop3.socketFactory.port", "995");
		props.setProperty("mail.pop3.host", "pop.gmail.com");
		props.setProperty("mail.pop3.ssl", "true");
		try {
			// Get session
			Session session = Session.getDefaultInstance(props, null);

			// Get the store
			Store store = session.getStore("pop3");
			store.connect(imap_server, user, password);

			// Get folder
			Folder folder = store.getFolder("Inbox");
			folder.open(Folder.READ_ONLY);

			// Get directory
			Message messages[] = folder.getMessages();

			// Close connection
			folder.close(false);
			store.close();

			return messages;

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(2);
		}
		return null;
	}

	public void send(String to, String title, String content) throws Exception {

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty(smtp_server, imap_server);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set the RFC 822 "From" header field using the
		// value of the InternetAddress.getLocalAddress method.
		message.setFrom(new InternetAddress(user));

		// Add the given addresses to the specified recipient type.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

		// Set the "Subject" header field.
		message.setSubject(title);

		// Sets the given String as this part's content,
		// with a MIME type of "text/plain".
		message.setText(content);

		// Send message
		Transport.send(message);

		System.out.println("Message Send.....");
	}

	public boolean tryConnection() {
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect(imap_server, user, password);
		} catch (AuthenticationFailedException e) {
			return false;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return true;
	}

}
