package com.vaadin.demo.mobilemail.ui;

import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.HorizontalComponentGroup;
import com.vaadin.addon.touchkit.ui.NavigationBar;
import com.vaadin.addon.touchkit.ui.Toolbar;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * A navigation view to display a single mail box.
 * 
 */
public class AccountView extends AbstractNavigationView {

	private static final long serialVersionUID = 1L;

	private Toolbar mailBoxActions = new Toolbar();
	private boolean smartphone;

	// Graphic components
	private CssLayout layout = new CssLayout();
	private HorizontalComponentGroup navigationActions = new HorizontalComponentGroup();
	private VerticalLayout content = new VerticalLayout();
	private HorizontalComponentGroup buttons = new HorizontalComponentGroup();
	private NavigationBar navigationBar = new NavigationBar();
	private Button createAccount;
	private Button deleteAccount = new Button("Delete account");

	// Fields
	private Form form = new Form();
	private TextField accountName = new TextField("Account Name:");
	private TextField userName = new TextField("Your Name:");
	private EmailField email = new EmailField("Email Address:");
	private PasswordField password = new PasswordField("Password:");
	private TextField incoming_address = new TextField("Incoming (IMAP):");
	private TextField outcoming_address = new TextField("Outcoming (SMTP):");

	// Elements
	private MailBox mailBox;
	private AccountHierarchyView currentAccountList;

	public AccountView(boolean smartphone) {
		this.smartphone = smartphone;
		setContent(layout);
		layout.setWidth("100%");

		// Everything is required
		accountName.setRequired(true);
		userName.setRequired(true);
		email.setRequired(true);
		password.setRequired(true);
		incoming_address.setRequired(true);
		outcoming_address.setRequired(true);

		if (smartphone) {
			setToolbar(mailBoxActions);
			setRightComponent(navigationActions);
		} else {
			mailBoxActions.setStyleName(null);
			mailBoxActions.setWidth("200px");
			mailBoxActions.setHeight("32px");
			mailBoxActions.setMargin(false);
			setRightComponent(mailBoxActions);
			setLeftComponent(navigationActions);

			content.setSizeFull();
			content.addComponent(navigationBar);

			form.setDescription("Please, enter your detail account.");
			form.setImmediate(true);

			accountName.setWidth("60%");
			accountName.setNullRepresentation("");
			accountName.setRequired(true);
			form.getLayout().addComponent(accountName);

			userName.setWidth("60%");
			userName.setNullRepresentation("");
			userName.setRequired(true);
			form.getLayout().addComponent(userName);

			email.setWidth("60%");
			email.setNullRepresentation("");
			Validator emailValidator = new EmailValidator("Email Invalid");
			email.addValidator(emailValidator);
			email.setRequired(true);

			form.getLayout().addComponent(email);

			password.setWidth("60%");
			password.setNullRepresentation("");
			password.setRequired(true);
			form.getLayout().addComponent(password);
			
			incoming_address.setWidth("60%");
			incoming_address.setNullRepresentation("");
			incoming_address.setRequired(true);
			form.getLayout().addComponent(incoming_address);

			outcoming_address.setWidth("60%");
			outcoming_address.setNullRepresentation("");
			outcoming_address.setRequired(true);
			form.getLayout().addComponent(outcoming_address);

			content.addComponent(form);

			createAccount = new Button("Save account", form, "commit");
			buttons.addComponent(createAccount);
			createAccount.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					if (isFormValid()) {
						System.out.println("Form valid");
						// New account
						if (mailBox == null) {
							// Creation of the new mail box
							mailBox = new MailBox("");
						}
						// Update the mail box
						mailBox.setName(accountName.getValue().toString());
						mailBox.setEmailAddress(email.getValue().toString());
						mailBox.setIncoming(incoming_address.getValue()
								.toString());
						mailBox.setOutcoming(outcoming_address.getValue()
								.toString());
						mailBox.setPassword(password.getValue().toString());
						mailBox.setUserName(userName.getValue().toString());

						currentAccountList.addOrUpdateAccount(mailBox);
						// Set the current mail box
						currentAccountList.mailBoxClicked(mailBox);
					}

				}
			});

			buttons.addComponent(deleteAccount);
			deleteAccount.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					currentAccountList.deleteAccount(mailBox);
					// Set the current mail box
					currentAccountList.mailBoxClicked(null);
				}
			});

			content.addComponent(buttons);

			content.setHeight(450, Sizeable.UNITS_PIXELS);
			content.setMargin(true);

			setContent(content);

			if (smartphone) {
				setSizeFull();
			} else {
				setHeight("80%");
			}
		}
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

	public MailBox getMailBox() {
		return mailBox;
	}

	public void setMailBox(MailBox mb, AccountHierarchyView accountList) {
		mailBox = mb;
		currentAccountList = accountList;
		if (mailBox != null) {
			accountName.setValue(mailBox.getName());
			userName.setValue(mailBox.getUserName());
			email.setValue(mailBox.getEmailAddress());
			password.setValue(mailBox.getPassword());
			incoming_address.setValue(mailBox.getIncoming());
			outcoming_address.setValue(mailBox.getOutcoming());
		} else {
			accountName.setValue("");
			userName.setValue("");
			email.setValue("");
			password.setValue("");
			incoming_address.setValue("");
			outcoming_address.setValue("");
		}
	}

	private boolean isFormValid() {
		if (!accountName.isValid()) {
			form.setComponentError(new UserError("Account Name can not be null"));
			return false;
		}
		else if (!userName.isValid()){
			form.setComponentError(new UserError("Name can not be null"));
			return false;
		}
		else if (!email.isValid()) {
			form.setComponentError(new UserError("Email Address invalid"));
			return false;
		}
		else if (!password.isValid()){
			form.setComponentError(new UserError("Password can not be null"));
			return false;
		}
		else if (!incoming_address.isValid()){
			form.setComponentError(new UserError("Incoming can not be null"));
			return false;
		}
		else if (!outcoming_address.isValid()){
			form.setComponentError(new UserError("Outcoming can not be null"));
			return false;
		}
		getContent().requestRepaint();
		return true;
	}
}
