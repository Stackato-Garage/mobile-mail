package com.vaadin.demo.mobilemail.data.folder;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class InboxFolder extends Folder {

	private static final String ICON = "../runo/icons/64/folder.png";
	public final static String INBOX = "Inbox";

	public InboxFolder() {
		super(INBOX, ICON);
	}

}
