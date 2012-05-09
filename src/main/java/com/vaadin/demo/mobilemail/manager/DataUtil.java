package com.vaadin.demo.mobilemail.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.mobilemail.data.AbstractPojo;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.data.dao.MailBoxDAO;
import com.vaadin.demo.mobilemail.data.folder.Folder;

/**
 * A utility class for generating dummy data
 * 
 */
public class DataUtil {

	// Util variables
	private boolean firstTime = false;
	private List<MailBox> mailBoxes;

	// Containers
	private static MobileMailContainer mailBoxesContainer;
	private MobileMailContainer mailBoxContainer;
	private MobileMailContainer folderContainer;

	// Singleton pattern
	private static DataUtil instance = null;

	public DataUtil() {
		// First, we get the db mail boxes and save them
		mailBoxes = new ArrayList<MailBox>();
//		getDBMailBoxesContainer();
		// Test
		 getDummiesMailBoxes();
	}

	public static DataUtil getInstance() {
		if (instance == null) {
			instance = new DataUtil();
		}
		return instance;
	}

	public void getDBMailBoxesContainer() {
			
		// Zeroing counter to get consistant ids for pojos
		int idcounter = 0;

		MailBoxDAO dao = new MailBoxDAO();
		mailBoxes = dao.findAll();

		// Initiate container
		mailBoxesContainer = new MobileMailContainer();
		for (MailBox mailBox : mailBoxes) {
			mailBox.run();
			mailBox.setId(mailBoxesContainer.size());
			mailBoxesContainer.addBean(mailBox);
		}

		System.out.println("Get mails boxes........OK! (" + mailBoxes.size()
				+ " mail boxes found)\n");
	}



	/**
	 * Used for test
	 */
	public void getDummiesMailBoxes() {
		
		MailBox mailbox = new MailBox();
		mailbox.setEmailAddress("jeremya@activestate.com");
		mailbox.setIncoming("imap.activestate.com");
		mailbox.setOutcoming("smtp.activestate.com");
		mailbox.setName("Gmail");
		mailbox.setPassword("xenone13");
		mailbox.setUserName("OSEF");
		
		mailBoxes.add(mailbox);

		// Initiate container
		mailBoxesContainer = new MobileMailContainer();
		for (MailBox mailBox : mailBoxes) {
			mailBox.run();
			mailBox.setId(mailBoxesContainer.size());
			mailBoxesContainer.addBean(mailBox);
		}

	}

	public static void updateMailBox(MailBox mailBox) {
		// Save in database
		MailBoxDAO dao = new MailBoxDAO();
		dao.saveOrUpdate(mailBox);
		// Run the mail box
		mailBox.run();
	}

	public static void addMailBox(MailBox mailBox) {
		// Save in database
		MailBoxDAO dao = new MailBoxDAO();
		dao.saveOrUpdate(mailBox);
		// Save in container
		mailBox.setId(mailBoxesContainer.size());
		mailBoxesContainer.addBean(mailBox);
		// Run the mail box
		mailBox.run();
	}

	public static void deleteMailBox(MailBox mailBox) {
		// Delete in database
		MailBoxDAO dao = new MailBoxDAO();
		dao.delete(mailBox);
		// Delete in container
		mailBoxesContainer.removeItem(mailBox);
	}

	public MobileMailContainer getMailBoxesContainer() {
		return mailBoxesContainer;
	}

	public MobileMailContainer getMailBoxContainer(MailBox mailBox) {
		mailBoxContainer = new MobileMailContainer();
		int idcounter = 0;
		// Create folders
		List<Folder> folders = mailBox.getFolders();
		for (Folder folder : folders) {
			folder.setId(idcounter);
			folder.setParent(mailBox);
			idcounter++;
		}
		mailBoxContainer.addAll(folders);

		return mailBoxContainer;
	}

	public MobileMailContainer getFolderContainer(Folder folder) {
		folderContainer = new MobileMailContainer();
		int idcounter = 0;
		// Fill folder
		List<Message> messages = folder.getMessages();
		// Create pojo messages
		for (Message message : messages) {
			message.setParent(folder);
			folder.getChildren().add(message);
			message.setId(idcounter);
			idcounter++;
		}
		folderContainer.addAll(messages);

		return folderContainer;
	}

	public List<MailBox> getMailBoxes() {
		return mailBoxes;
	}
}
