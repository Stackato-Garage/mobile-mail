package com.vaadin.demo.mobilemail.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.vaadin.addon.touchkit.ui.NavigationBar;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.TouchKitApplication;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.mobilemail.MobileMailApplication;
import com.vaadin.demo.mobilemail.data.AbstractPojo;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MessageStatus;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.data.ParentFilter;
import com.vaadin.demo.mobilemail.data.folder.Folder;
import com.vaadin.demo.mobilemail.manager.DataUtil;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * Displays accounts, mailboxes, message list hierarchically
 */
public class MailboxHierarchyView extends NavigationView {

	// Util component
	private DataUtil dataUtil = DataUtil.getInstance();
	private MobileMailContainer container;

	// Graphic components
	private VerticalComponentGroup accounts;
	private static ComposeView composeView;
	private MailboxHierarchyManager nav;
	static Resource refreshIcon = new ThemeResource("graphics/reload-icon.png");
	private Resource mailboxIcon = new ThemeResource(
			"../runo/icons/64/globe.png");

	public MailboxHierarchyView(final MailboxHierarchyManager nav) {

		this.nav = nav;
		composeView = new ComposeView(true);

		setCaption("Mailboxes");
		setWidth("100%");
		setHeight("100%");

		// Get container
		container = dataUtil.getMailBoxesContainer();

		CssLayout root = new CssLayout();
		root.setWidth("100%");
		root.setMargin(true);

		accounts = new VerticalComponentGroup();
		displayMailBoxes();

		VerticalComponentGroup settings = new VerticalComponentGroup();
		Label header_settings = new Label("Settings");
		header_settings.setSizeUndefined();
		header_settings.addStyleName("grey-title");
		settings.addComponent(header_settings);

		// Add an account button
		NavigationButton accounts_btn = new NavigationButton("Accounts");
		final AccountHierarchyView v = new AccountHierarchyView(nav);

		accounts_btn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				nav.navigateTo(v);
				composeView.select(v.getSelectedMailBox());
				v.mailBoxClicked(v.getSelectedMailBox());
			}
		});
		accounts_btn.addStyleName("pill");
		settings.addComponent(accounts_btn);

		root.addComponent(accounts);
		root.addComponent(settings);
		setContent(root);
		setToolbar(createToolbar());
	}

	@Override
	protected void onBecomingVisible() {
		super.onBecomingVisible();
		// TODO
		 displayMailBoxes();
	}

	static Component createToolbar() {

		final NavigationBar toolbar = new NavigationBar();

		Button refresh = new Button();
		refresh.setIcon(refreshIcon);
		refresh.addStyleName("no-decoration");

		toolbar.setLeftComponent(refresh);

		final SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
		toolbar.setCaption("Updated "
				+ formatter.format(Calendar.getInstance().getTime()));

		refresh.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				toolbar.setCaption("Updated "
						+ formatter.format(Calendar.getInstance().getTime()));
			}
		});

		TouchKitApplication touchKitApplication = MobileMailApplication.get();
		if (touchKitApplication instanceof MobileMailApplication) {
			MobileMailApplication app = (MobileMailApplication) touchKitApplication;
			if (app.isSmallScreenDevice()) {
				/*
				 * For small screen devices we add shortcut to new message below
				 * hierarcy views
				 */
				ClickListener showComposeview = new ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = event.getButton().getWindow();
						window.addWindow(composeView);
					}
				};
				Button button = new Button(null, showComposeview);
				button.setIcon(new ThemeResource("graphics/compose-icon.png"));
				toolbar.setRightComponent(button);

				button.addStyleName("no-decoration");
			}
		}

		return toolbar;
	}

	public void displayMailBoxes() {
		// Empty vertical components group
		accounts.removeAllComponents();

		// Fill accounts
		Label header = new Label("Accounts");
		header.setSizeUndefined();
		header.addStyleName("grey-title");
		accounts.addComponent(header);

		for (Object itemId : container.rootItemIds()) {
			if (itemId instanceof MailBox) {
				final MailBox mb = (MailBox) itemId;
				NavigationButton btn = new NavigationButton(mb.getName());
				if (mb.getName().length() > 20) {
					btn.setCaption(mb.getName().substring(0, 20) + "…");
				}
				btn.setIcon(mailboxIcon);
				btn.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						// Get messages from account (To get number of inbox
						FolderHierarchyView v = new FolderHierarchyView(nav, mb);
						nav.navigateTo(v);
					}
				});

				// Set new messages
				// int newMessages = 0;
				// for (Folder child : mb.getFolders()) {
				// for (AbstractPojo p : child.getChildren()) {
				// if (p instanceof MessageManager) {
				// MessageManager msg = (MessageManager) p;
				// newMessages += msg.getStatus() == MessageStatus.NEW ? 1
				// : 0;
				// }
				// }
				// }
				// if (newMessages > 0) {
				// btn.setDescription(newMessages + "");
				// }
				btn.addStyleName("pill");
				accounts.addComponent(btn);
			}
		}

	}
}
