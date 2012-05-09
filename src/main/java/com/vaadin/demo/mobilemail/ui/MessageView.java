package com.vaadin.demo.mobilemail.ui;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.Header;

import com.vaadin.addon.touchkit.ui.HorizontalComponentGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.Toolbar;
import com.vaadin.demo.mobilemail.data.AbstractPojo;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MessageField;
import com.vaadin.demo.mobilemail.data.MessageStatus;
import com.vaadin.demo.mobilemail.data.folder.Folder;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

/**
 * A navigation view to display a single message.
 * 
 */
public class MessageView extends AbstractNavigationView implements
		ClickListener {

	private static final String NOTHING_SELECTED = "";
	private static final long serialVersionUID = 1L;
	private CssLayout layout = new CssLayout();
	private CssLayout detailsLayout = new CssLayout();

	private HorizontalComponentGroup navigationActions = new HorizontalComponentGroup();
	private Button nextButton;
	private Button prevButton;
	private Button markAsUnreadButton;

	private Toolbar messageActions = new Toolbar();
	private Button moveButton;
	private Button composeButton;
	private Button deleteButton;
	private Button replyButton;
	private Popover replyOptions;
	private Button replyOptionsReply;
	private Button replyOptionsReplyAll;
	private Button replyOptionsForward;
	private Button replyOptionsPrint;
	private VerticalLayout replyOptionsLayout = new VerticalLayout();
	private boolean smartphone;

	public MessageView(boolean smartphone) {
		this.smartphone = smartphone;
		setContent(layout);
		layout.setWidth("100%");
		layout.setStyleName("message-layout");
		addStyleName("message-view");

		buildToolbar();

		if (smartphone) {
			setToolbar(messageActions);
			setRightComponent(navigationActions);
		} else {
			messageActions.setStyleName(null);
			messageActions.setWidth("200px");
			messageActions.setHeight("32px");
			messageActions.setMargin(false);
			setRightComponent(messageActions);
			setLeftComponent(navigationActions);
		}

		setMessage(null, null);
	}

	private void buildToolbar() {
		moveButton = new Button(null, this);
		composeButton = new Button(null, this);
		deleteButton = new Button(null, this);
		replyButton = new Button(null, new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				Popover pop = new Popover();
				pop.setWidth("300px");
				Button reply = new Button("Reply", MessageView.this);
				reply.addStyleName("white");
				reply.setWidth("100%");
				Button replyAll = new Button("Reply All", MessageView.this);
				replyAll.addStyleName("white");
				replyAll.setWidth("100%");
				Button forward = new Button("Forward", MessageView.this);
				forward.addStyleName("white");
				forward.setWidth("100%");
				Button print = new Button("Print", MessageView.this);
				print.addStyleName("white");
				print.setWidth("100%");
				pop.addComponent(reply);
				pop.addComponent(replyAll);
				pop.addComponent(forward);
				pop.addComponent(print);
				pop.showRelativeTo(event.getButton());
			}
		});

		moveButton.setStyleName("no-decoration");
		moveButton.setIcon(new ThemeResource("graphics/move-icon.png"));
		composeButton.setStyleName("no-decoration");
		composeButton.setIcon(new ThemeResource("graphics/compose-icon.png"));
		deleteButton.setStyleName("no-decoration");
		deleteButton.setIcon(new ThemeResource("graphics/trash-icon.png"));
		replyButton.setStyleName("no-decoration");
		replyButton.setIcon(new ThemeResource("graphics/reply-icon.png"));

		messageActions.addComponent(moveButton);
		messageActions.addComponent(deleteButton);
		messageActions.addComponent(replyButton);
		messageActions.addComponent(composeButton);

		nextButton = new Button("Down", this);
		nextButton.addStyleName("icon-arrow-down");
		nextButton.setEnabled(false);
		// nextButton.setVisible(smartphone);

		prevButton = new Button("Up", this);
		prevButton.addStyleName("icon-arrow-up");
		prevButton.setEnabled(false);
		// prevButton.setVisible(smartphone);

		navigationActions.addComponent(prevButton);
		navigationActions.addComponent(nextButton);
	}

	/**
	 * @return the layout that contains e.g prev and next buttons. Note that
	 *         this component is not attached by defaults. Users of this class
	 *         can assign it where appropriate (tablet and smartphone views want
	 *         to locate this differently).
	 */
	public CssLayout getNavigationLayout() {
		return navigationActions;
	}

	private Message message;
	private MessageHierarchyView currentMessageList;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message msg, MessageHierarchyView messageList) {
		message = msg;
		currentMessageList = messageList;

		if (msg != null) {

			Folder folder = (Folder) msg.getParent();
			List<AbstractPojo> siblings = folder.getChildren();
			int index = siblings.indexOf(msg);

			nextButton.setEnabled(true);
			prevButton.setEnabled(true);
			if (index == 0) {
				prevButton.setEnabled(false);
			}
			if (index == siblings.size() - 1) {
				nextButton.setEnabled(false);
			}

			setCaption((index + 1) + " of " + siblings.size());

			layout.removeAllComponents();
			detailsLayout.removeAllComponents();
			detailsLayout.setStyleName("message-details");

			Label lbl = new Label("From: ");
			lbl.setStyleName("light-text");
			lbl.setSizeUndefined();
			layout.addComponent(lbl);

			NativeButton fromField = new NativeButton(message.getFrom(), this);
			fromField.addStyleName("from-button");
			layout.addComponent(fromField);

			Button button = new Button("Details");
			button.setStyleName(BaseTheme.BUTTON_LINK);
			button.addStyleName("details-link");
			button.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					detailsLayout.setVisible(!detailsLayout.isVisible());

					if (detailsLayout.isVisible()) {
						event.getButton().setCaption("Hide");
					} else {
						event.getButton().setCaption("Details");
					}

					if (markAsUnreadButton != null) {
						markAsUnreadButton.setVisible(detailsLayout.isVisible());
					}
				}
			});
			layout.addComponent(button);

			lbl = new Label("<hr/>", Label.CONTENT_XHTML);
			layout.addComponent(lbl);

			detailsLayout.setVisible(false);
			layout.addComponent(detailsLayout);

			for (String to : message.getTos()) {
				lbl = new Label("To : ");
				lbl.setStyleName("light-text");
				lbl.setSizeUndefined();
				detailsLayout.addComponent(lbl);

				Button btn = new NativeButton(to, this);
				btn.addStyleName("from-button");
				detailsLayout.addComponent(btn);

				lbl = new Label("<hr/>", Label.CONTENT_XHTML);
				detailsLayout.addComponent(lbl);
			}

			CssLayout subjectField = new CssLayout();
			subjectField.setWidth("100%");

			lbl = new Label(message.getSubject());
			lbl.setStyleName(Reindeer.LABEL_H2);
			subjectField.addComponent(lbl);

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yy-mm-dd  h:mm a");
			lbl = new Label(dateFormat.format(message.getSentDate()),
					Label.CONTENT_XHTML);
			lbl.setStyleName(Reindeer.LABEL_SMALL);
			lbl.setSizeUndefined();
			subjectField.addComponent(lbl);

			if (message.isUnread()) {
				markAsUnreadButton = new Button("Mark as Unread");
				markAsUnreadButton.setVisible(false);
				markAsUnreadButton.setStyleName("mark-as-unread-button");
				markAsUnreadButton.addStyleName(BaseTheme.BUTTON_LINK);
				markAsUnreadButton.setIcon(new ThemeResource(
						"graphics/blue-ball.png"));
				subjectField.addComponent(markAsUnreadButton);
			}

			layout.addComponent(subjectField);

			lbl = new Label("<hr/>", Label.CONTENT_XHTML);
			layout.addComponent(lbl);

			String contentType = message.getContentType();
			int ct = Label.CONTENT_PREFORMATTED;
			if (message.isHtml())
				ct = Label.CONTENT_XHTML;
			
			Label label = new Label(message.getBody(),ct);
			
			layout.addComponent(label);

			removeStyleName("no-message");

		} else {
			layout.removeAllComponents();
			Label noMessageLbl = new Label(NOTHING_SELECTED);
			noMessageLbl.setStyleName(Reindeer.LABEL_SMALL);
			noMessageLbl.addStyleName(Reindeer.LABEL_H1);
			layout.addComponent(noMessageLbl);
			addStyleName("no-message");
			nextButton.setEnabled(false);
			prevButton.setEnabled(false);
		}

	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() == replyButton) {
			showReplyButtonOptions();
			return;
		}
		if (event.getButton() == composeButton) {
			ComposeView composeView = new ComposeView(smartphone);
			getWindow().addWindow(composeView);
			return;
		}
		if (event.getButton() == nextButton) {
			Folder folder = (Folder) message.getParent();
			List<AbstractPojo> messagesAndFolders = folder.getChildren();
			int index = messagesAndFolders.indexOf(message);
			if (index < messagesAndFolders.size() - 1) {
				Message msg = (Message) messagesAndFolders.get(index + 1);
				currentMessageList.selectMessage(msg);
				setMessage(msg, currentMessageList);
			}
			return;

		}
		if (event.getButton() == prevButton) {
			Folder folder = (Folder) message.getParent();
			List<AbstractPojo> messagesAndFolders = folder.getChildren();
			int index = messagesAndFolders.indexOf(message);
			if (index > 0) {
				Message msg = (Message) messagesAndFolders.get(index - 1);
				currentMessageList.selectMessage(msg);
				setMessage(msg, currentMessageList);
			}
			return;
		}

		if (event.getButton().getParent() == replyOptionsLayout) {
			replyOptions.getParent().removeWindow(replyOptions);
		}

		getWindow().showNotification("Not implemented");

	}

	private void showReplyButtonOptions() {
		if (replyOptions == null) {
			replyOptions = new Popover();
			replyOptions.setWidth("300px");
			replyOptions.setClosable(true);
			replyOptions.setContent(replyOptionsLayout);
			replyOptionsLayout.setSpacing(true);

			replyOptionsReply = new Button("Reply", this);
			replyOptionsReply.setWidth("100%");
			replyOptionsReplyAll = new Button("Reply all", this);
			replyOptionsReplyAll.setWidth("100%");
			replyOptionsForward = new Button("Forward", this);
			replyOptionsForward.setWidth("100%");
			replyOptionsPrint = new Button("Print", this);
			replyOptionsPrint.setWidth("100%");

			replyOptions.addComponent(replyOptionsReply);
			replyOptions.addComponent(replyOptionsReplyAll);
			replyOptions.addComponent(replyOptionsForward);
			replyOptions.addComponent(replyOptionsPrint);

		}

		replyOptions.showRelativeTo(replyButton);
	}
}
