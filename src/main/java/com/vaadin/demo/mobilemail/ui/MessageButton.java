package com.vaadin.demo.mobilemail.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.util.DateUtils;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

/**
 * A message button which can be selected. Contains the sender, subject and a
 * shortened version of the body
 * 
 */
public class MessageButton extends CssLayout {
	private static final long serialVersionUID = 1L;

	private Message message;

	public static final String BUTTON_STYLENAME = "message-button";
	public static final String SELECTED_BUTTON_STYLENAME = "message-button-selected";

	public MessageButton(Message message) {
		this.message = message;

		setWidth("100%");
		setStyleName(BUTTON_STYLENAME);

		// Get information
		String from = message.getFrom();
		String subject = message.getSubject();
		Date date = message.getSentDate();

		// Header Label
		Label subjectLavel = new Label();
		subjectLavel.setStyleName(BUTTON_STYLENAME + "-subject");
		subjectLavel.setWidth("-1px");
		if (subject.length() > 25) {
			subjectLavel.setValue(subject.substring(0, 25) + "...");
		} else {
			subjectLavel.setValue(subject);
		}
		subjectLavel.addStyleName(Reindeer.LABEL_H2);
		addComponent(subjectLavel);

		// Date Label
		Label dateLabel = new Label(from);
		int fromLabelLength = 15;
		SimpleDateFormat dateFormat;
		String dateString = "";
		if (DateUtils.isToday(date)) {
			dateFormat = new SimpleDateFormat("h:mm a");
			dateString += "Today ";
			fromLabelLength = 25;
		} else {
			dateFormat = new SimpleDateFormat("yy-MM-dd  h:mm a");
		}
		dateLabel = new Label(dateString + dateFormat.format(date));
		dateLabel.setWidth("-1px");
		dateLabel.setStyleName(BUTTON_STYLENAME + "-time");
		dateLabel.addStyleName(Reindeer.LABEL_SMALL);
		addComponent(dateLabel);

		// From Label
		Label fromLabel = new Label(from);
		fromLabel.setStyleName(BUTTON_STYLENAME + "-from");
		fromLabel.setHeight("1.5em");
		fromLabel.setWidth("-1px");
		if (from.length() > fromLabelLength) {
			fromLabel.setValue(from.substring(0, fromLabelLength) + "...");
		} else {
			fromLabel.setValue(from);
		}
		addComponent(fromLabel);

		// Display body
		/*
		 * Label content = new Label(); if (body.length() > 80) {
		 * content.setValue(body.replaceAll("\\<.*?\\>", "").substring(0, 80) +
		 * "..."); } else { content.setValue(body.replaceAll("\\<.*?\\>", ""));
		 * }
		 * 
		 * content.setStyleName(Reindeer.LABEL_SMALL); addComponent(content);
		 */
	}

	public Message getMessage() {
		return message;
	}
}