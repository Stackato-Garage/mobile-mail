package com.vaadin.demo.mobilemail.data.folder;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class DraftFolder extends Folder {

	private static final String ICON = "../runo/icons/64/document-edit.png";

	private final static String DRAFT = "Drafts";

	public DraftFolder() {
		super(DRAFT, ICON);
	}

}
