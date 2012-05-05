package com.vaadin.demo.mobilemail.data.folder;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;


public class SentFolder extends Folder {
	
	private static final String ICON = "../runo/icons/64/email-send.png";
	
	private final static String SENT_MAIL = "Sent mail";

	public SentFolder(){
		super(SENT_MAIL, ICON);
	}
}
