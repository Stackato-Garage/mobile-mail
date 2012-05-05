package com.vaadin.demo.mobilemail.ui;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.demo.mobilemail.data.AncestorFilter;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.demo.mobilemail.data.MobileMailContainer;
import com.vaadin.demo.mobilemail.data.folder.Folder;
import com.vaadin.demo.mobilemail.manager.DataUtil;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;

public class FolderHierarchyView extends NavigationView {

	private static final long serialVersionUID = 1L;

	private DataUtil dataUtil = DataUtil.getInstance();

	private MailBox mb;
	private NavigationManager nav;
	private Table foldersTable;
	private MobileMailContainer container;

	public FolderHierarchyView(final NavigationManager nav, final MailBox mb) {

		this.mb = mb;
		this.nav = nav;
		foldersTable = null;

		if (mb.getName().length() > 10) {
			setCaption(mb.getName().substring(0, 10) + "...");
		} else {
			setCaption(mb.getName());
		}

		setWidth("100%");
		setHeight("100%");

		// Get the data container and filter with current mailBox
		container = dataUtil.getMailBoxContainer(mb);
		container.setFilter(new AncestorFilter(mb));

		foldersTable = new Table(null, container);
		foldersTable.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		foldersTable.setVisibleColumns(new Object[] { "name" });
		foldersTable.setSizeFull();

		foldersTable.addGeneratedColumn("name", new Table.ColumnGenerator() {

			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				if (columnId.equals("name") && itemId instanceof Folder) {
					final Folder f = (Folder) itemId;

					NavigationButton btn = new NavigationButton(f.getName());

					btn.addListener(new Button.ClickListener() {

						public void buttonClick(ClickEvent event) {
							nav.navigateTo(new MessageHierarchyView(nav, f));
						}
					});

					if (f.getParent() instanceof MailBox) {

						btn.setIcon(f.getIcon());
						int trashMessages = f.getMessages().size();
						if (trashMessages > 0) {
							btn.setDescription(trashMessages + "");
						}

						btn.addStyleName("pill");
					}

					return btn;
				}
				return null;
			}
		});
		setContent(foldersTable);
		setToolbar(MailboxHierarchyView.createToolbar());
	}
}
