package com.vaadin.demo.mobilemail.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.itextpdf.text.html.HtmlEncoder;

public class Message extends AbstractPojo implements Runnable {

	private static final long serialVersionUID = 1L;

	private javax.mail.Message message;

	private boolean isHtml = false;

	public boolean isHtml() {
		return isHtml;
	}

	private String from = new String();
	private StringBuffer body = new StringBuffer();
	private String subject = new String();
	private Date sentDate;
	private List<String> tos = new ArrayList<String>();
	private String replyTo = new String();

	public Message(javax.mail.Message message) {
		this.setMessage(message);
		Thread run = new Thread(this);
		run.start();
	}

	public String getFrom() {
		return from;
	}

	public String getBody() {
		return body.toString();
	}

	public String getSubject() {
		return subject;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public List<String> getTos() {
		return tos;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public javax.mail.Message getMessage() {
		return message;
	}

	public void setMessage(javax.mail.Message message) {
		this.message = message;
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

	public void run() {
		
		try {
			/** SUBJECT **/
			if (message.getSubject() != null) {
				subject = message.getSubject().toString();
			} else {
				subject = "<<No title>>";
			}

			/** DATE **/
			sentDate = message.getSentDate();

			/** FROM **/
			from = InternetAddress.toString(message.getFrom());

			/** TOS **/
			Address[] recipients;

			recipients = message
					.getRecipients(javax.mail.Message.RecipientType.TO);

			for (Address address : recipients) {
				tos.add(address.toString());
			}

			/** REPLY TO **/
			replyTo = InternetAddress.toString(message.getReplyTo());

			/** BODY **/
			Object content;
			content = message.getContent();
			
			if (content instanceof String) {
				body.append(content);
			} else {
				Multipart multipart = (Multipart) content;
				for (int x = 0; x < multipart.getCount(); x++) {
					BodyPart p = multipart.getBodyPart(x);
					if (p.isMimeType("text/*")) {
						String s = p.getContent().toString();
						if (isHtml)
							body.append(HtmlEncoder.encode(s));
					}

					if (p.isMimeType("multipart/alternative")) {
						
						// prefer html text over plain text
						Multipart mp = (Multipart) p.getContent();
						String text = null;
						for (int i = 0; i < mp.getCount(); i++) {
							Part bp = mp.getBodyPart(i);

							if (bp.isMimeType("text/plain")) {
								// if (text == null)
								text = bp.getContent().toString();
								// continue;
							} else if (bp.isMimeType("text/html")) {
								isHtml = true;
								text = bp.getContent().toString();
								text = Jsoup.clean(text, Whitelist.basic());
							} else {
								// Files
								// MimeMultipart mm = (MimeMultipart)
								// bp.getContent();
							}

						}
						body.append(text);

					} else if (p.isMimeType("multipart/*")) {
						Multipart mp = (Multipart) p.getContent();
						for (int i = 0; i < mp.getCount(); i++) {
							MimeMultipart mm = (MimeMultipart) mp.getBodyPart(i).getContent();
							for (int j = 0; j < mm.getCount(); j++) {
								String s = mm.getBodyPart(j).getContent().toString();
								body.append(s);
							}
//							String s = mp.getBodyPart(i).getContent()
//									.toString();
//							if (s != null)
//								body.append(s);
						}
					} else {
						/*
						 * If we actually want to see the data, and it's not a
						 * MIME type we know, fetch it and check its Java type.
						 */
						Object o = p.getContent();
						if (o instanceof String) {
							body.append((String) o);
						} else if (o instanceof InputStream) {
							InputStream is = (InputStream) o;
							StringWriter writer = new StringWriter();
							int c;
							while ((c = is.read()) != -1)
								writer.write(c);
							body.append(writer.toString());
						} else {
							body.append(o.toString());
						}
					}
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
