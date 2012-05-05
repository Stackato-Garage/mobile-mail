package com.vaadin.demo.mobilemail.ui;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.manager.DataUtil;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class AccountHierarchyView extends NavigationView {

	private static final String ADD_ACCOUNT = "New Account";

	private static final long serialVersionUID = 1L;

	private DataUtil dataUtil = DataUtil.getInstance();

	// Graphic component
	private VerticalComponentGroup accounts;
	private Button selectedButton;
	private MailBox selectedMailBox;
	private CssLayout root;

	public AccountHierarchyView(final NavigationManager nav) {

		setCaption("Settings");
		setWidth("100%");
		setHeight("100%");

		root = new CssLayout();
		root.setWidth("100%");
		root.setMargin(true);

		// Account settings
		accounts = new VerticalComponentGroup();
		// Header
		Label header = new Label("Account Settings");
		header.setSizeUndefined();
		header.addStyleName("grey-title");
		accounts.addComponent(header);

		// Get container
		MobileMailContainer container = dataUtil.getMailBoxesContainer();

		// List every accounts
		for (Object itemId : container.rootItemIds()) {

			final MailBox mb = (MailBox) itemId;
			Button btn = new Button(mb.getName());
			if (mb.getName().length() > 20) {
				btn.setCaption(mb.getName().substring(0, 20) + "…");
			}
			btn.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					// Updates buttons
					selectButton(event.getButton());
					// Set current mail boxes
					mailBoxClicked(mb);
				}
			});

			btn.addStyleName("pill");
			accounts.addComponent(btn);
		}

		// Account actions
		VerticalComponentGroup actions = new VerticalComponentGroup();
		Label header_aa = new Label("Account Actions");
		header_aa.setSizeUndefined();
		header_aa.addStyleName("grey-title");
		actions.addComponent(header_aa);

		// Add an account
		Button add_account_btn = new Button(ADD_ACCOUNT);
		add_account_btn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				// Updates buttons
				selectButton(event.getButton());
				// Mew mail box
				mailBoxClicked(null);
			}
		});
		add_account_btn.addStyleName("pill");
		actions.addComponent(add_account_btn);
		selectButton(add_account_btn);

		// Add components
		root.addComponent(accounts);
		root.addComponent(actions);
		setContent(root);
		setToolbar(MailboxHierarchyView.createToolbar());
	}

	private boolean isSmartphone() {
		return (getParent() instanceof SmartphoneMainView);
	}

	public void mailBoxClicked(MailBox mb) {
		if (!isSmartphone()) {
			setMailBox(mb);
		}
	}

	private void setMailBox(final MailBox mailBox) {
		setSelectedMailBox(mailBox);
		ComponentContainer cc = getApplication().getMainWindow().getContent();
		if (cc instanceof MainView) {
			MainView mainView = (MainView) cc;
			mainView.setMailBox(mailBox, this);
		}
	}

	/**
	 * Add or update the given mail box and update buttons view
	 */
	public void addOrUpdateAccount(final MailBox mailBox) {
		
		// Control mailbox configuration
		if(!mailBox.tryConnection()){
			getWindow().showNotification("Connection failed. Connection details invalid.");
			return ;
		}

		// Updates views
		/* ACCOUNT ADDED */
		if (getSelectedButton().getCaption().equals(ADD_ACCOUNT)) {

			// Account added, add button
			Button btn = new Button(mailBox.getName());
			if (mailBox.getName().length() > 20) {
				btn.setCaption(mailBox.getName().substring(0, 20) + "…");
			}
			btn.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					// Updates buttons
					selectButton(event.getButton());
					// Set current mail boxes
					mailBoxClicked(mailBox);
				}
			});
			btn.addStyleName("pill");
			accounts.addComponent(btn);

			// Updates buttons selection
			selectButton(btn);
			// Save in database
			DataUtil.addMailBox(mailBox);

		}
		/* ACCOUNT UPDATED */
		else {
			// Account updated, update the button name
			getSelectedButton().setCaption(mailBox.getName());
			// Update database
			DataUtil.updateMailBox(mailBox);
		}
		
		getWindow().showNotification("Account saved!");
	}

	/**
	 * Delete mail box from database and delete mail box button
	 */
	public void deleteAccount(final MailBox mailBox) {
		// Save in database
		DataUtil.deleteMailBox(mailBox);
		// Delete the button
		accounts.removeComponent(getSelectedButton());
		// Set the message view
		ComponentContainer cc = getApplication().getMainWindow().getContent();
		if (cc instanceof TabletMainView) {
			TabletMainView mainView = (TabletMainView) cc;
			((TabletMainView) cc).setDefault();
		}
	}
	
	/**
	 * This method selects the given button and deselect the previous selected button.
	 */
	public void selectButton(Button btn) {
		// Updates buttons
		if (getSelectedButton() != null)
			getSelectedButton().setEnabled(true);
		btn.setEnabled(false);
		setSelectedButton(btn);
	}

	public MailBox getSelectedMailBox() {
		return selectedMailBox;
	}

	public void setSelectedMailBox(MailBox selectedMailBox) {
		this.selectedMailBox = selectedMailBox;
	}

	public Button getSelectedButton() {
		return selectedButton;
	}

	public void setSelectedButton(Button selectedButton) {
		this.selectedButton = selectedButton;
	}

}
