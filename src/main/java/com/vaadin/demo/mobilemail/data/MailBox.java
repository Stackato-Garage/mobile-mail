package com.vaadin.demo.mobilemail.data;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags.Flag;
import javax.mail.MessagingException;

import com.vaadin.demo.mobilemail.data.folder.BasicFolder;
import com.vaadin.demo.mobilemail.data.folder.DraftFolder;
import com.vaadin.demo.mobilemail.data.folder.Folder;
import com.vaadin.demo.mobilemail.data.folder.InboxFolder;
import com.vaadin.demo.mobilemail.data.folder.SentFolder;
import com.vaadin.demo.mobilemail.data.folder.TrashFolder;
import com.vaadin.demo.mobilemail.util.Mail;

/**
 * A mailbox contains details about the account as well as links to the root
 * folders of the mail box.
 */
public class MailBox extends AbstractPojo {

	private static final long serialVersionUID = 1L;

	private Long mb;

	private String userName;

	private String emailAddress;

	private String password;

	private String incoming;

	private String outcoming;

	private Mail mail;

	private List<Folder> folders;

	private DraftFolder draft;

	private SentFolder sent;

	private TrashFolder trash;

	private InboxFolder inbox;

	public MailBox() {
		draft = new DraftFolder();
		sent = new SentFolder();
		trash = new TrashFolder();
		inbox = new InboxFolder();

		folders = new ArrayList<Folder>();
		folders.add(inbox);
		folders.add(draft);
		folders.add(sent);
		folders.add(trash);

		mail = new Mail();
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the mail box
	 */
	public MailBox(String name) {
		this.name = name;
		draft = new DraftFolder();
		sent = new SentFolder();
		trash = new TrashFolder();
		inbox = new InboxFolder();

		folders = new ArrayList<Folder>();
		folders.add(inbox);
		folders.add(draft);
		folders.add(sent);
		folders.add(trash);

		mail = new Mail();
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the mailServerUrl
	 */
	public String getIncoming() {
		return incoming;
	}

	/**
	 * @param mailServerUrl
	 *            the mailServerUrl to set
	 */
	public void setIncoming(String incoming) {
		this.incoming = incoming;
	}

	/**
	 * @return the mailServerUsername
	 */
	public String getOutcoming() {
		return outcoming;
	}

	/**
	 * @param mailServerUsername
	 *            the mailServerUsername to set
	 */
	public void setOutcoming(String outcoming) {
		this.outcoming = outcoming;
	}

	/**
	 * @return the mailServerPassword
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param mailServerPassword
	 *            the mailServerPassword to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the folders
	 */
	public List<Folder> getFolders() {
		return folders;
	}

	/**
	 * @param folders
	 *            the folders to set
	 */
	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String accountName) {
		this.userName = accountName;
	}

	public Long getMb() {
		return mb;
	}

	public void setMb(Long mb) {
		this.mb = mb;
	}

	Runnable run = new Runnable() {

		public void run() {
			UpdateConfiguration();
			javax.mail.Folder[] MBfolders = mail.readImap();

			for (javax.mail.Folder folder : MBfolders) {
				try {
					folder.open(javax.mail.Folder.READ_ONLY);

					// Create new folder and add the to list folders
					Folder currentFolder;
					// If it is the inbox folder, add first
					if (folder.getName().equals("INBOX")) {
						currentFolder = inbox;
					} else {
						currentFolder = new BasicFolder(folder.getName());
						folders.add(currentFolder);
					}
					// Get messages for the current folder
					javax.mail.Message[] messages = folder.getMessages();
					currentFolder.setMBMessages(messages);
					currentFolder.run();

				} catch (MessagingException e) {
					if (e.getMessage().equals("folder cannot contain messages")) {
						// Gmail exception
					} else {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public void run() {
		Thread runMB = new Thread(run);
		runMB.start();
	}

	public void sentMessage(String to, String subject, String body) {
		UpdateConfiguration();
		try {
			mail.send(to, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void UpdateConfiguration() {
		// Mail configuration
		mail.setImap(incoming);
		String user = emailAddress.split("@")[0];
		mail.setUser(user);
		mail.setPassword(password);
		mail.setSmtp(outcoming);
		mail.setEmail(emailAddress);
	}

	public boolean tryConnection() {
		UpdateConfiguration();
		return mail.tryConnection();
	}
}
