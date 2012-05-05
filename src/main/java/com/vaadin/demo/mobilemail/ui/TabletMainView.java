package com.vaadin.demo.mobilemail.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class TabletMainView extends HorizontalLayout implements MainView,
		ResizeListener, ClickListener {

	private static final long serialVersionUID = 1L;

	MailboxHierarchyManager mailboxHierarchyView = new MailboxHierarchyManager();

	AbstractNavigationView currentView;

	MessageView messageView = new MessageView(false);

	AccountView accountView = new AccountView(false);

	Button showMailboxHierarchyButton;

	private boolean lastOrientationHorizontal;

	public TabletMainView() {
		setSizeFull();
		addStyleName("tablet");
		// MessageView is displayed by default
		currentView = messageView;
	}

	@Override
	public void attach() {
		super.attach();
		setOrientation();
		getWindow().addListener(this);
	}

	private void setOrientation() {
		removeAllComponents();

		if (isHorizontal()) {
			/*
			 * Removed possible window currently showing the hierarchy view
			 */
			if (mailboxHierarchyView.getParent() != null) {
				Component parent2 = mailboxHierarchyView.getParent();
				if (parent2 instanceof Window) {
					Window window = (Window) parent2;
					window.setContent(null);
					if (window.getParent() != null) {
						window.getParent().removeWindow(window);
					}
				}
			}

			addComponent(mailboxHierarchyView);
			addComponent(currentView);
			setExpandRatio(currentView, 1);
			currentView.setLeftComponent(currentView.getNavigationLayout());
		} else {
			showMailboxHierarchyButton = new Button();
			showMailboxHierarchyButton.addListener(this);

			addComponent(currentView);
			showMailboxHierarchyButton.setCaption(mailboxHierarchyView
					.getCurrentComponent().getCaption());

			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.setSpacing(true);
			hLayout.setHeight("30px");

			hLayout.addComponent(showMailboxHierarchyButton);
			hLayout.addComponent(currentView.getNavigationLayout());

			currentView.setLeftComponent(hLayout);
		}

		lastOrientationHorizontal = isHorizontal();
	}

	private boolean isHorizontal() {
		return getApplication().getMainWindow().getWidth() > getApplication()
				.getMainWindow().getHeight();
	}

	public void windowResized(ResizeEvent e) {
		if (getApplication() != null) {
			if (isHorizontal() != lastOrientationHorizontal) {
				setOrientation();
			}
		}
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() == showMailboxHierarchyButton) {
			Popover popover = new Popover();
			Component parent2 = mailboxHierarchyView.getParent();
			if (parent2 != null && parent2 instanceof Popover) {
				((Popover) parent2).setContent(null);
			}
			popover.setContent(mailboxHierarchyView);
			popover.setClosable(true);
			popover.showRelativeTo(showMailboxHierarchyButton);
			popover.setHeight(getParent().getHeight() - 100, UNITS_PIXELS);
			popover.addListener(new CloseListener() {
				public void windowClose(CloseEvent e) {
					setEnabled(true);
				}
			});
		}

	}

	@Override
	public void setEnabled(boolean enabled) {
		currentView.setEnabled(enabled);
	}

	public void setMailBox(MailBox mailBox,
			AccountHierarchyView accountHierarchyView) {
		accountView.setMailBox(mailBox, accountHierarchyView);
		currentView = accountView;
		setOrientation();
	}

	public void setMessage(Message message,
			MessageHierarchyView messageHierarchyView) {
		messageView.setMessage(message, messageHierarchyView);
		currentView = messageView;
		setOrientation();
	}
	
	public void setDefault(){
		currentView = messageView;
		setOrientation();
	}
}
