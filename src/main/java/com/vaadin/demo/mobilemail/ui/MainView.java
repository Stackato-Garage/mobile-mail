package com.vaadin.demo.mobilemail.ui;

import com.vaadin.demo.mobilemail.data.MailBox;
import com.vaadin.demo.mobilemail.data.Message;

public interface MainView {

    public void setMessage(Message message,
            MessageHierarchyView messageHierarchyView);
    
    public void setMailBox(MailBox mailBox,
            AccountHierarchyView accountHierarchyView);

}
