package com.vaadin.demo.mobilemail.data.folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.Flags.Flag;

import com.vaadin.demo.mobilemail.data.AbstractPojo;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

/**
 * A folder can contain other folders or messages. A folder cannot contain both
 * folders and subfolders.
 */
public abstract class Folder extends AbstractPojo {

	private static final long serialVersionUID = 1L;

	private List<AbstractPojo> children = new ArrayList<AbstractPojo>();

	private ArrayList<Message> messages;

	private javax.mail.Message[] MBmessages;

	private Resource icon;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the folder
	 */
	public Folder(String name, String iconUrl) {
		this.name = name;
		icon = new ThemeResource(iconUrl);
		messages = new ArrayList<Message>();
	}

	/**
	 * @return the children
	 */
	public List<AbstractPojo> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<AbstractPojo> children) {
		this.children = children;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public Resource getIcon() {
		return icon;
	}

	public void setMBMessages(javax.mail.Message[] messages) {
		this.MBmessages = messages;
	}

	Runnable run = new Runnable() {
		public void run() {
			for (javax.mail.Message message : MBmessages) {
				Message msg = new Message(message);
				addMessage(msg);
			}
			Collections.reverse(messages);
		}
	};

	public void run() {
		Thread runF = new Thread(run);
		runF.start();
	}

}
