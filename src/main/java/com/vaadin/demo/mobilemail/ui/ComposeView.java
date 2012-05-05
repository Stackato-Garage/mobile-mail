package com.vaadin.demo.mobilemail.ui;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.NavigationBar;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.manager.DataUtil;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ComposeView extends Popover implements ClickListener {

	private static final String SEND = "Send";
	private static final String CANCEL = "Cancel";
	private VerticalLayout content = new VerticalLayout();
	private NavigationBar navigationBar = new NavigationBar();
	private EmailField to = new EmailField("To:");
	private EmailField cc = new EmailField("Cc/Bcc:");
	private TextField subject = new TextField("Title:");
	private TextArea body = new TextArea();
	private boolean smartphone;
	private Button cancel;
	private Button send;
	private Select selectMailBox;
	private DataUtil dataUtil = DataUtil.getInstance();

	public ComposeView(boolean smartphone) {
		addStyleName("new-message");

		cancel = new Button(CANCEL, this);
		send = new Button(SEND, this);
		List<MailBox> mailboxes = dataUtil.getMailBoxes();
		MobileMailContainer container = dataUtil.getMailBoxesContainer();
		selectMailBox = new Select("Select an account", container);
		selectMailBox.setNullSelectionAllowed(false);
		selectMailBox.setItemCaptionPropertyId("name");
		selectMailBox.select(container.firstItemId());

		navigationBar.setWidth("100%");
		navigationBar.setCaption("New Message");
		navigationBar.setLeftComponent(cancel);
		navigationBar.setRightComponent(send);

		content.setSizeFull();
		content.addComponent(navigationBar);

		FormLayout fields = new FormLayout();
		fields.setMargin(false, true, false, true);
		fields.setSpacing(false);

		selectMailBox.setWidth("100%");
		fields.addComponent(selectMailBox);

		to.setWidth("100%");
		fields.addComponent(to);

		cc.setWidth("100%");
		fields.addComponent(cc);

		subject.setWidth("100%");
		fields.addComponent(subject);

		content.addComponent(fields);

		body.setSizeFull();
		content.addComponent(body);

		content.setExpandRatio(body, 1);
		setContent(content);

		setModal(true);
		setClosable(false);
		if (smartphone) {
			setSizeFull();
		} else {
			setHeight("100%");
			center();
		}
		this.smartphone = smartphone;
	}

	@Override
	public void attach() {
		super.attach();
		if (!smartphone) {
			if (getParent().getWidth() > 800) {
				setWidth("80%");
			} else {
				setWidth("100%");
			}
		}
	}

	public void buttonClick(ClickEvent event) {
		Button button = event.getButton();
		if (button == cancel) {
			getParent().removeWindow(this);
		} else {
			MailBox mailbox = (MailBox) selectMailBox.getValue();
			String toMessage = to.getValue().toString();
			String subjectMessage = subject.getValue().toString();
			String bodyMessage = body.getValue().toString();
			mailbox.sentMessage(toMessage, subjectMessage, bodyMessage);
			getWindow().showNotification("Email sent!");
			getParent().removeWindow(this);
		}
	}

	public void select(MailBox mailbox) {
		selectMailBox.select(mailbox);
	}

}
