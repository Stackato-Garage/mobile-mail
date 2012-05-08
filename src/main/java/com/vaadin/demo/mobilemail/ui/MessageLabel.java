package com.vaadin.demo.mobilemail.ui;

import com.vaadin.ui.Label;

public class MessageLabel extends Label {
	
	private static final String WIDTH = "-1px";
	private static final String NEW_MARKER = "new-marker";
	private static final String SPACE = "&nbsp;";

	public MessageLabel(){
		super(SPACE, Label.CONTENT_XHTML);
		setStyleName(NEW_MARKER);
		setWidth(WIDTH);
	}

}
