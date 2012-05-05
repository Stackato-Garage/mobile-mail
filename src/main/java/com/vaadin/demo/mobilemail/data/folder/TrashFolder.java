package com.vaadin.demo.mobilemail.data.folder;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class TrashFolder extends Folder {

	private static final String ICON = "../runo/icons/64/trash.png";

	private final static String TRASH = "Trash";

	public TrashFolder() {
		super(TRASH, ICON);
	}

}
