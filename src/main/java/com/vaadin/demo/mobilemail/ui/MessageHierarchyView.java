package com.vaadin.demo.mobilemail.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.data.folder.Folder;
import com.vaadin.demo.mobilemail.manager.DataUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class MessageHierarchyView extends NavigationView implements
		LayoutClickListener, Button.ClickListener {

	// Util components
	private DataUtil dataUtil = DataUtil.getInstance();
	private MobileMailContainer container;
	private Folder folder;
	private boolean editMode = false;

	// Graphical components
	private Table messagesTable;
	private Button editBtn;
	private Button archiveButton;
	private Button moveButton;

	private Map<Message, CheckBox> messageSelectMap = new HashMap<Message, CheckBox>();

	public MessageHierarchyView(final NavigationManager nav, final Folder folder) {
		addStyleName("message-list");

		this.folder = folder;
		int newMessages = 0;
		for (Message message : folder.getMessages()) {
			if (message.isNew())
				newMessages++;
		}

		if (newMessages > 0) {
			setCaption(folder.getName() + " (" + newMessages + ")");
		} else {
			setCaption(folder.getName());
		}

		editBtn = new Button("Edit");
		editBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {

				if (editMode) {
					editBtn.setCaption("Edit");
					editBtn.removeStyleName("blue");
					editMode = false;
					messagesTable.setSelectable(true);
					messagesTable.setVisibleColumns(new Object[] { "new",
							"name" });
					setToolbar(MailboxHierarchyView.createToolbar());

					messagesTable.setColumnExpandRatio("name", 1);

				} else {
					for (CheckBox cb : messageSelectMap.values()) {
						cb.setValue(false);
					}

					selected.clear();

					editBtn.setCaption("Cancel");
					editBtn.addStyleName("blue");
					editMode = true;
					messagesTable.select(null);
					messagesTable.setSelectable(false);
					messagesTable.setVisibleColumns(new Object[] { "selected",
							"new", "name" });
					setToolbar(createEditToolbar());

					messagesTable.setColumnExpandRatio("name", 1);
				}

				// Enable disable message view
				setMessageViewEnabled(!editMode);

				// Hide the back button while editing
				getNavigationBar().getComponentIterator().next()
						.setVisible(!editMode);
			}
		});

		setRightComponent(editBtn);

		// Get container
		container = dataUtil.getFolderContainer(folder);

		messagesTable = new Table(null, container);
		messagesTable.setImmediate(true);
		messagesTable.setSelectable(true);
		messagesTable.setMultiSelect(false);
		messagesTable.setNullSelectionAllowed(false);
		messagesTable.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		messagesTable.setSizeFull();

		// Add a selected colum
		messagesTable.addGeneratedColumn("selected",
				new Table.ColumnGenerator() {
					private static final long serialVersionUID = 1L;

					public Component generateCell(Table source,
							final Object itemId, Object columnId) {
						if (!messageSelectMap.containsKey(itemId)) {
							final CheckBox cb = new CheckBox();
							cb.setImmediate(true);
							cb.setStyleName("selected-checkbox");
							cb.addListener(new Property.ValueChangeListener() {
								public void valueChange(ValueChangeEvent event) {
									if (cb.booleanValue()) {
										selected.add((Message) itemId);
									} else {
										selected.remove(itemId);
									}

									if (selected.isEmpty()) {
										moveButton.setCaption("Move");
										archiveButton.setCaption("Archive");
									} else {
										moveButton.setCaption("Move ("
												+ selected.size() + ")");
										archiveButton.setCaption("Archive ("
												+ selected.size() + ")");
									}
								}
							});

							messageSelectMap.put((Message) itemId, cb);
						}

						return messageSelectMap.get(itemId);
					}
				});
		messagesTable.setColumnWidth("selected", 30);

		// Add a new item column
		messagesTable.addGeneratedColumn("new", new Table.ColumnGenerator() {
			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				Message msg = (Message) itemId;
				if (msg.isNew()) {
					Label lbl = new Label("&nbsp;", Label.CONTENT_XHTML);
					lbl.setStyleName("new-marker");
					lbl.setWidth("-1px");
					return lbl;
				}
				return null;
			}
		});

		// Replace name column with navigation buttons
		messagesTable.addGeneratedColumn("name", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				final Message m = (Message) itemId;
				MessageButton btn = new MessageButton(m);
				btn.addListener(MessageHierarchyView.this);
				return btn;
			}
		});
		messagesTable.setColumnExpandRatio("name", 1);

		if (editMode) {
			messagesTable.setVisibleColumns(new Object[] { "selected", "new",
					"name" });
		} else {
			messagesTable.setVisibleColumns(new Object[] { "new", "name" });
		}

		messagesTable.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				Message msg = (Message) event.getItemId();
				messageClicked(msg);
			}
		});

		setContent(messagesTable);
		setToolbar(MailboxHierarchyView.createToolbar());
	}

	List<Message> selected = new ArrayList<Message>();

	public void layoutClick(LayoutClickEvent event) {
		MessageButton btn = (MessageButton) event.getSource();

		Message msg = btn.getMessage();

		messageClicked(msg);
	}

	private void messageClicked(Message msg) {
		if (editMode) {
			messagesTable.select(null);
			CheckBox cb = messageSelectMap.get(msg);
			cb.setValue(!cb.booleanValue());
		} else {
			messagesTable.select(msg);
			if (!editMode || !isSmartphone()) {
				setMessage(msg);
			}
		}
	}

	private void setMessage(final Message message) {
		ComponentContainer cc = getApplication().getMainWindow().getContent();
		if (cc instanceof MainView) {
			MainView mainView = (MainView) cc;
			mainView.setMessage(message, this);
		}
	}

	private void setMessageViewEnabled(boolean enabled) {
		if (!isSmartphone()) {
			ComponentContainer cc = getApplication().getMainWindow()
					.getContent();
			TabletMainView tmv = (TabletMainView) cc;
			tmv.setEnabled(enabled);
		}

	}

	@Override
	protected void onBecomingVisible() {
		super.onBecomingVisible();
		if (!isSmartphone()) {
			if (container.size() > 0 && messagesTable.getValue() == null) {
				messagesTable.select(container.getIdByIndex(0));
				setMessage((Message) messagesTable.getValue());
			}
			setMessageViewEnabled(true);

			if (folder.getChildren().isEmpty()) {
				setMessage(null);
				editBtn.setEnabled(false);
			}
		}

	}

	private boolean isSmartphone() {
		return (getParent() instanceof SmartphoneMainView);
	}

	protected Component createEditToolbar() {
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.setMargin(false, true, false, true);
		toolbar.setSpacing(true);
		toolbar.setSizeFull();
		toolbar.setStyleName("v-touchkit-navbar");

		archiveButton = new Button("Archive", this);
		archiveButton.setStyleName("red");
		archiveButton.setWidth("100%");
		toolbar.addComponent(archiveButton);
		toolbar.setComponentAlignment(archiveButton, Alignment.MIDDLE_LEFT);

		moveButton = new Button("Move", this);
		moveButton.setStyleName("blue");
		moveButton.setWidth("100%");
		toolbar.addComponent(moveButton);
		toolbar.setComponentAlignment(moveButton, Alignment.MIDDLE_LEFT);

		return toolbar;
	}

	public void buttonClick(ClickEvent event) {
		getWindow().showNotification("Not implemented");

	}

	public void selectMessage(Message msg) {
		messagesTable.setValue(msg);
	}
}
