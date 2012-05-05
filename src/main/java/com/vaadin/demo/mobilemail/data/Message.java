package com.vaadin.demo.mobilemail.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

public class Message extends AbstractPojo {

	private static final long serialVersionUID = 1L;

	private javax.mail.Message message;

	private boolean textIsHtml = false;

	public Message(javax.mail.Message message) {
		this.setMessage(message);
	}

	public javax.mail.Message getMessage() {
		return message;
	}

	public void setMessage(javax.mail.Message message) {
		this.message = message;
	}

	public String getSubject() {
		String header = "";
		try {
			if (message.getSubject() != null) {
				header = message.getSubject().toString();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return header;
	}

	public Date getSentDate() {
		Date date = new Date();
		try {
			date = message.getSentDate();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return date;
	}

	public String getFrom() {
		String from = "";
		try {
			from = InternetAddress.toString(message.getFrom());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return from;
	}

	public List<String> getTo() {
		List<String> toAddresses = new ArrayList<String>();
		Address[] recipients;
		try {
			recipients = message
					.getRecipients(javax.mail.Message.RecipientType.TO);

			for (Address address : recipients) {
				toAddresses.add(address.toString());
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return toAddresses;
	}

	public String getReplyTo() {
		String to = null;
		try {
			to = InternetAddress.toString(message.getReplyTo());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return to;
	}

	public String getBody() {
		String body = "";

		Object content;
		try {
			content = message.getContent();
			if (content instanceof String) {
				body = content.toString();
			} else {
				Multipart multipart = (Multipart) content;
				for (int x = 0; x < multipart.getCount(); x++) {
					BodyPart p = multipart.getBodyPart(x);

					if (p.isMimeType("text/*")) {
						String s = (String) p.getContent();
						textIsHtml = p.isMimeType("text/html");
						return s;
					}

					if (p.isMimeType("multipart/alternative")) {
						// prefer html text over plain text
						Multipart mp = (Multipart) p.getContent();
						String text = null;
						for (int i = 0; i < mp.getCount(); i++) {
							Part bp = mp.getBodyPart(i);
							if (bp.isMimeType("text/plain")) {
								if (text == null)
									text = bp.getContent().toString();
								continue;
							} else if (bp.isMimeType("text/html")) {
								String s = bp.getContent().toString();
								if (s != null)
									return s;
							} else {
								return bp.getContent().toString();
							}
						}
						return text;
					} else if (p.isMimeType("multipart/*")) {
						Multipart mp = (Multipart) p.getContent();
						for (int i = 0; i < mp.getCount(); i++) {
							String s = mp.getBodyPart(i).getContent()
									.toString();
							if (s != null)
								return s;
						}
					}

					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return body;
	}

	public boolean isNew() {
		boolean isNew = false;
		try {
			isNew = getMessage().isSet(Flag.RECENT);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return isNew;
	}

	public boolean isUnread() {
		boolean isUnread = false;
		try {
			isUnread = !getMessage().isSet(Flag.SEEN);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return isUnread;
	}

	public String getContentType() {
		Enumeration headers;
		try {
			headers = message.getAllHeaders();
			while (headers.hasMoreElements()) {
				Header h = (Header) headers.nextElement();
				if (h.getName().equals("Content-Type")) {
					return h.getValue().split(";")[0];
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
